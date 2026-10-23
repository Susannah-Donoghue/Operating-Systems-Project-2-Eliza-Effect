//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Main {
    public Main() {
    }

    public static void main(String[] args) {
        Random seedMake = new Random();
        long seed = seedMake.nextLong();
        Random random = new Random(seed);
        int min = 3;
        int max = 7;
        int N = random.nextInt(max - min + 1) + min;
        System.out.println("Number of Domains: " + N);
        int M = random.nextInt(max - min + 1) + min;
        System.out.println("Number of Files: " + M);
        Semaphore file = new Semaphore(1);
        String[][] accMat = new String[N][N + M];
        new Random();
        System.out.printf("%-6.6s | ", "Object");

        for(int headerCount = 0; headerCount < M; ++headerCount) {
            System.out.printf("%-6.6s | ", "F" + headerCount);
        }

        for(int headerCount0 = 0; headerCount0 < N; ++headerCount0) {
            System.out.printf("%-6.6s | ", "D" + headerCount0);
        }

        System.out.println();
        System.out.println("/File");

        for(int i = 0; i < N; ++i) {
            System.out.printf("%-6.6s | ", "D" + i);

            for(int j = 0; j < N + M; ++j) {
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
                } else if (j == i + M) {
                    accMat[i][j] = "N/A";
                } else {
                    int domainPermInt = random.nextInt(2);
                    if (domainPermInt == 0) {
                        accMat[i][j] = "Switch";
                    } else if (domainPermInt == 1) {
                        accMat[i][j] = " ";
                    }
                }

                System.out.printf("%-6.6s | ", accMat[i][j]);
            }

            System.out.println();
        }

        ArrayList<user> users = new ArrayList();
        int domain = 0;

        for(int u = 0; u < N; ++u) {
            users.add(new user("user" + u, M, N, u, accMat, u, file));
            PrintStream var10000 = System.out;
            String var10001 = ((user)users.get(u)).getNameMe();
            var10000.println("Created " + var10001 + " in domain " + u);
        }

        for(int u = 0; u < N; ++u) {
            ((user)users.get(u)).start();
        }

    }
}
