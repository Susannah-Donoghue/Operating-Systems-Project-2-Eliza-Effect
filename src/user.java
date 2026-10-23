//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.Random;
import java.util.concurrent.Semaphore;

public class user extends Thread {
    String name;
    int M;
    int N;
    int domain;
    String[][] accMat;
    int u;
    Semaphore file;

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
        return this.name;
    }

    public int getDomain() {
        return this.domain;
    }

    public void run() {
        int min = 0;
        int max = this.M + this.N;
        Random seedMake = new Random();
        long seed = seedMake.nextLong();
        Random random = new Random(seed);
        int requests = random.nextInt(6) + 5;

        for(int r = 0; r < requests; ++r) {
            int X = random.nextInt(max - min) + min;
            String action = "";
            if (X < this.M) {
                int binary = random.nextInt(2);
                if (binary == 0) {
                    System.out.println(this.name + " is attempting to read F" + X);
                    action = "R";
                } else {
                    System.out.println(this.name + " is attempting to write to F" + X);
                    action = "W";
                }

                if (action.equals(this.accMat[this.domain][X]) || action.equals("R") && this.accMat[this.domain][X].equals("R/W") || action.equals("W") && this.accMat[this.domain][X].equals("R/W")) {
                    try {
                        this.file.acquire();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        System.out.println("Success!  " + this.name + " can " + action + " in D" + this.domain);
                        int yields = random.nextInt(5) + 3;

                        for(int i = 0; i < yields; ++i) {
                            Thread.yield();
                            System.out.println(this.name + " yield " + i);
                        }
                    } finally {
                        this.file.release();
                    }
                } else {
                    System.out.println("Permission denied! " + this.name + " cannot " + action + " in D" + this.domain);
                }
            } else {
                int target = this.arbitrator(X, random);
                if (this.accMat[this.domain][target + this.M].equals("Switch")) {
                    System.out.println(this.name + " in domain D" + this.domain + " can switch to D" + target);
                    this.domain = target;
                    System.out.println("Switched " + this.name + " to domain D" + this.domain);
                } else {
                    System.out.println("Permission denied.  " + this.name + "cannot switch to D" + this.domain);
                }
            }
        }

    }

    public int arbitrator(int X, Random random) {
        int target = X - this.M;
        System.out.println(this.name + " is attempting to switch to D" + target);

        while(this.domain == target) {
            System.out.println("Cannot switch to domain you're already in!");
            target = random.nextInt(this.N);
            System.out.println(this.name + " is attempting to switch to D" + target);
        }

        return target;
    }
}
