import java.util.Random;
import java.util.concurrent.Semaphore;

public class user extends Thread{
    String name;
    int M;
    int N;
    int domain;
    String[][] accMat;
    int u;
    Semaphore file;

    // Constructor
    user(String name, int M, int N, int domain, String[][] accMat, int u, Semaphore file) {
        this.name = name;
        this.M = M;
        this.N = N;
        this.domain = domain;
        this.accMat = accMat;
        this.u = u;
        this.file = file;
    }
    public String getNameMe() {
        return name;
    }

    public int getDomain() {
        return domain;
    }

    public void run() {
        // DONE
        // Generate a random number X in the range [0, M+N) to correspond to a column in the access matrix

        //  TO DO
        //  If X < M, generate a number between [0,1]
        //      Use semaphores/locks to protect file access, disallow user from reading a file object being written to and vice versa
        //      Thread yields between 3 and 7 times, yield before releasing semaphore/lock
        // if x >= M, switch to domain X - M
        int min = 0;
        int max = M + N;
        Random seedMake = new Random();
        long seed = seedMake.nextLong();
        Random random = new Random(seed);
        int requests = random.nextInt(11-5) + 5;

        for (int r = 0; r < requests; r++) {
            int X = random.nextInt(max - min) + min;

            String action = "";

            if (X < M) {
                int binary = random.nextInt(2);
                if (binary == 0) {
                    System.out.println(name + " is attempting to read F" + X);
                    action = "R";
                } else {
                    System.out.println(name + " is attempting to write to F" + X);
                    action = "W";
                }

                if (action.equals(accMat[domain][X]) || action.equals("R") && accMat[domain][X].equals("R/W") || action.equals("W") && accMat[domain][X].equals("R/W")) {
                    try {
                        file.acquire();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        System.out.println("Success!  " + name + " can " + action + " in D" + domain);
                        int yields = random.nextInt(8 - 3) + 3;
                        for (int i = 0; i < yields; i++) {
                            Thread.yield();
                            System.out.println(name + " yield " + i);
                        }
                    } finally {
                        file.release();
                    }
                } else {
                    System.out.println("Permission denied! " + name + " cannot " + action + " in D" + domain);
                }

            } else {
                int target = arbitrator(X, random);
                if (accMat[domain][target + M].equals("Switch")) {
                    System.out.println(name + " in domain D" + domain + " can switch to D" + target);
                    domain = target;
                    System.out.println("Switched " + name + " to domain D" + domain);
                } else {
                    System.out.println("Permission denied.  " + name + "cannot switch to D" + domain);
                }
            }
        }
    }

    public int arbitrator(int X, Random random) {
        int target = X - M;
        System.out.println(name + " is attempting to switch to D" + target);
        while (domain == target) {

            System.out.println("Cannot switch to domain you're already in!");
            target = random.nextInt(N);
            System.out.println(name + " is attempting to switch to D" + target);
        }
        return target;
    }
}
