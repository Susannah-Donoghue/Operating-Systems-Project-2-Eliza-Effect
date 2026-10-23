
import java.util.Random;
import java.util.concurrent.locks.Lock;

public class ACLUser extends Thread {
    private int threadNumber;
    private int M;
    private int N;
    private int domain;
    private ObjectACL[] accessLists;
    private FileResource[] files;
    private Random random = new Random();

    private String[] colors = {
            "Red", "Orange", "Green", "Blue", "Pink"
    };

    public ACLUser(int threadNumber, int M, int N, int domain,
                   ObjectACL[] accessLists, FileResource[] files) {
        this.threadNumber = threadNumber;
        this.M = M;
        this.N = N;
        this.domain = domain;
        this.accessLists = accessLists;
        this.files = files;
    }

    private String threadLabel() {
        return "[Thread: " + threadNumber + "(D" + (domain + 1) + ")] ";
    }

    private void yieldRandomly() {
        int count = random.nextInt(5) + 3;

        System.out.println(threadLabel()
                + "Yielding " + count + " times");

        for (int i = 0; i < count; i++) {
            Thread.yield();
        }
    }


    private boolean arbitrator(int objectIndex, String operation) {
        String objectName = accessLists[objectIndex].getObjectName();

        System.out.println(threadLabel()
                + "Arbitrator checking " + operation
                + " permission for " + objectName + ".");

        boolean allowed = accessLists[objectIndex].isAllowed(
                "D" + (domain + 1), operation);

        if (allowed) {
            System.out.println(threadLabel()
                    + "Arbitrator: permission granted.");
        } else {
            System.out.println(threadLabel()
                    + "Arbitrator: permission denied.");
        }

        return allowed;
    }


    private void accessFile(int fileIndex, boolean isWrite) {
        FileResource file = files[fileIndex];
        String operation = isWrite ? "WRITE" : "READ";
        String fileName = file.getName();

        System.out.println(threadLabel()
                + "Attempting to " + operation.toLowerCase()
                + " resource: " + fileName);

        if (!arbitrator(fileIndex, operation)) {
            System.out.println(threadLabel()
                    + "Operation failed, permission denied");
            yieldRandomly();
            return;
        }

        Lock lock;

        if (isWrite) {
            lock = file.getLock().writeLock();
        } else {
            lock = file.getLock().readLock();
        }

        lock.lock();

        try {
            if (isWrite) {
                String content = colors[random.nextInt(colors.length)];
                file.write(content);

                System.out.println(threadLabel()
                        + "Writing '" + content
                        + "' to resource " + fileName);
            } else {
                System.out.println(threadLabel()
                        + "Resource " + fileName
                        + " contains " + file.read());
            }

            yieldRandomly();
            System.out.println(threadLabel() + "Operation Complete.");
        } finally {
            lock.unlock();
        }
    }

    private void switchDomain(int target) {
        System.out.println(threadLabel()
                + "Attempting to switch from D" + (domain + 1)
                + " to D" + (target + 1) + ".");

        int objectIndex = M + target;

        if (!arbitrator(objectIndex, "SWITCH")) {
            System.out.println(threadLabel()
                    + "Operation failed, permission denied");
            yieldRandomly();
            return;
        }

        domain = target;

        System.out.println(threadLabel()
                + "Switched to D" + (domain + 1));

        yieldRandomly();
        System.out.println(threadLabel() + "Operation Complete.");
    }

    @Override
    public void run() {
        int requests = random.nextInt(6) + 5;

        for (int r = 0; r < requests; r++) {
            int X;

            do {
                X = random.nextInt(M + N);
            } while (X >= M && X - M == domain);

            if (X < M) {
                boolean isWrite = random.nextBoolean();
                accessFile(X, isWrite);
            } else {
                int target = X - M;
                switchDomain(target);
            }
        }
    }
}
