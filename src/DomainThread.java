import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;

public class DomainThread  extends Thread{
    private final CapabilityLists system;
    private final int threadId;
    private String currentDomain;

    public DomainThread (CapabilityLists system, int threadId, String startingDomain) {
        this.system = system;
        this.threadId = threadId;
        this.currentDomain = startingDomain;
    }

    // Print message w/ the thread ID and current domain
    private void log(String message) {
        System.out.println("[Thread: " + threadId + "(" + currentDomain + ")]" + message);
    }

    // Yield the thread a random num of times
    private void yieldRandomly() {
        int count = ThreadLocalRandom.current().nextInt(3, 8);

        log("Yielding " + count + " times");

        for (int i = 0; i < count; i++) {
            Thread.yield();
        }
    }

    // Read or write a file after checking its permissions
    private void accessFile(int fileIndex, boolean isWrite) {
        FileResource file = system.getFile(fileIndex);
        String fileName = file.getName();

        // Select the permission needed for the operation
        CapabilityLists.Permission operation = isWrite ? CapabilityLists.Permission.WRITE : CapabilityLists.Permission.READ;

        log ("Attempting to " + (isWrite ? "write" : "read") + " resource: " + fileName);

        // Stop if there is no permission for current domain
        if (!system.arbitrator(currentDomain, fileName, operation)) {
            log ("Operation failed, permission denied");
            yieldRandomly();
            return;
        }

        // Use write lock for writing and a read lock for reading
        Lock lock = isWrite ? file.getLock().writeLock() : file.getLock().readLock();

        lock.lock();

        try {
            if (isWrite) {

                // replace file contents w/ a random color
                String content = system.randomColor();
                file.write(content);

                log("Writing '" + content + "' to resource " + fileName);
            }
            else {

                // Display the current contens
                log("Resource " + fileName + " contains " + file.read());
            }

            // Yield whie the file lock is still held
            yieldRandomly();
        }
        finally {
            // release the lock when op ends
            lock.unlock();
        }

        log("Operation Complete.");
    }

    // Switch to another domain if granted
    private void switchDomain(int targetIndex) {
        String targetDomain = "D" + (targetIndex + 1);

        // do not switch to the domain the thread  is already using
        if (targetDomain.equals(currentDomain)) {
            return;
        }

        log("Attempting to switch from " + currentDomain + " to " + targetDomain + ".");

        // check whether current domain allows the switch
        if (!system.arbitrator(currentDomain, targetDomain, CapabilityLists.Permission.SWITCH)) {
            log("Operation failes, permission denied");
            yieldRandomly();
            return;
        }

        // Update threads current domain
        currentDomain = targetDomain;

        log("Switched to " + currentDomain);

        yieldRandomly();

        log("Operation Complete.");
    }

    @Override
    public void run() {

        // Ea domain thread makes 5 req
        for (int request = 0; request < 5; request++) {
            int choice;

            // randomly choose a file or a diff domain
            do {
                choice = ThreadLocalRandom.current().nextInt(system.getFileCount() + system.getDomainCount());
            }
            while (choice >= system.getFileCount() && ("D" + (choice - system.getFileCount() + 1)).equals(currentDomain));

            if (choice < system.getFileCount()) {
                boolean isWrite = ThreadLocalRandom.current().nextBoolean();

                accessFile(choice, isWrite);
            }

            else {
                switchDomain(choice - system.getFileCount());
            }
        }
    }
}