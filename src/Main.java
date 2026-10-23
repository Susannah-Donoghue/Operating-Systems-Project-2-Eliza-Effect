import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {

        // DONE
        // Randomly Generate M between 3 and 7, represents files
        // Randomly generate N between 3 and 7, represents domains
        // create matrix sized N x (M + N)
        // create a thread for each domain
        // randomly populate access matrix with read, write, read and write, or none
        // domain needs permission to switch or not switch (random)
        // each thread (user) requests 5 times, can be successful or unsuccessful

        //   TO DO
        //  Generate a random number X in the range [0, M+N) to correspond to a column in the access matrix
        //  If X < M, generate a number between [0,1]
        //      Use semaphores/locks to protect file access, disallow user from reading a file object being written to and vice versa
        //      Thread yields between 3 and 7 times, yield before releasing semaphore/lock
        // if x >= M, switch to domain X - M

        Random seedMake = new Random();
        long seed = seedMake.nextLong();
        Random random = new Random(seed);
        int min = 3;
        int max = 7;
        int N = random.nextInt(max - min + 1) + min; // files
        System.out.println("Number of Domains: " + N);
        int M = random.nextInt(max - min + 1) + min; // domains, users
        System.out.println("Number of Files: " + M);
        Semaphore file = new Semaphore(1);

        String[][] accMat = new String [N][N+M];

        Random accRand = new Random();

        // print header
        System.out.printf("%-6.6s | ", "Object");
        for (int headerCount = 0; headerCount < M; headerCount++) {
            System.out.printf("%-6.6s | ", "F" + (headerCount));
        }
        for (int headerCount0 = 0; headerCount0 < N; headerCount0++) {
            System.out.printf("%-6.6s | ", "D" + (headerCount0));
        }
        System.out.println();
        System.out.println("/File");
        for (int i = 0; i < N; i++) {
            System.out.printf("%-6.6s | ", "D" + (i));
            for (int j = 0; j < N + M; j++) {
                if (j < M) {
                    int permissionInt = random.nextInt(4);
                    if (permissionInt == 0) {
                        accMat[i][j] = "R";
                    } else if (permissionInt == 1) {
                        accMat[i][j] = "W";
                    } else if (permissionInt == 2) {
                        accMat[i][j] = "R/W";
                    } else if (permissionInt == 3) {
                        accMat[i][j] = " ";
                    }
                }
                else {
                    if (j == i + M) {
                        accMat[i][j] = "N/A";
                    }
                    else {
                        int domainPermInt = random.nextInt(2);
                        if (domainPermInt == 0) {
                            accMat[i][j] = "Switch";
                        } else if (domainPermInt == 1) {
                            accMat[i][j] = " ";
                        }
                    }
                }
                    System.out.printf("%-6.6s | ", accMat[i][j]);
            }
            System.out.println();
        }
        // list of users
        ArrayList<user> users= new ArrayList<>();
        int domain = 0;


        for (int u = 0; u < N; u++) {
            domain = u;
            users.add(new user("user" + (u), M, N, domain, accMat, u, file));
            System.out.println("Created " + users.get(u).getNameMe() + " in domain " + domain);
        }

        for (int u = 0; u < N; u++) {
            users.get(u).start();
        }
    }
}