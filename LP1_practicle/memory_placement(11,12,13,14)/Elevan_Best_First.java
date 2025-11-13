// BEST FIT & FIRST FIT Memory Allocation
import java.util.Arrays;
import java.util.Scanner;

public class Elevan_Best_First{

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // ----- INPUT MEMORY BLOCKS -----
        System.out.print("Enter number of memory blocks: ");
        int b = sc.nextInt();

        int[] blockSize = new int[b];
        System.out.println("Enter block sizes:");
        for (int i = 0; i < b; i++) {
            blockSize[i] = sc.nextInt();
        }

        // ----- INPUT PROCESSES -----
        System.out.print("\nEnter number of processes: ");
        int p = sc.nextInt();

        int[] processSize = new int[p];
        System.out.println("Enter process sizes:");
        for (int i = 0; i < p; i++) {
            processSize[i] = sc.nextInt();
        }

        // ----- MENU -----
        System.out.println("\nMemory Placement Strategies");
        System.out.println("1. First Fit");
        System.out.println("2. Best Fit");
        System.out.print("Enter choice (1/2): ");
        int choice = sc.nextInt();

        switch (choice) {
            case 1 : implementFirstFit(blockSize, processSize);
            case 2 : implementBestFit(blockSize, processSize);
            default : {
                System.out.println("Invalid choice. Running First Fit by default.");
                implementFirstFit(blockSize, processSize);
            }
        }

        sc.close();
    }

    // ----- Print Allocation Result -----
    private static void printAllocation(int[] processSize, int[] allocate) {
        System.out.println("\nProcess No.\tProcess Size\tBlock No.");
        for (int i = 0; i < processSize.length; i++) {
            System.out.print((i + 1) + "\t\t" + processSize[i] + "\t\t");
            if (allocate[i] != -1)
                System.out.println(allocate[i] + 1);
            else
                System.out.println("Not Allocated");
        }
    }

    // ================= FIRST FIT =================
    private static void implementFirstFit(int[] blockSize, int[] processSize) {
        int blocks = blockSize.length;
        int processes = processSize.length;

        int[] allocate = new int[processes];
        boolean[] occupied = new boolean[blocks];

        Arrays.fill(allocate, -1);

        for (int i = 0; i < processes; i++) {
            for (int j = 0; j < blocks; j++) {
                if (!occupied[j] && blockSize[j] >= processSize[i]) {
                    allocate[i] = j;
                    occupied[j] = true;
                    break;
                }
            }
        }

        System.out.println("\n--- First Fit Allocation ---");
        printAllocation(processSize, allocate);
    }

    // ================= BEST FIT =================
    private static void implementBestFit(int[] blockSize, int[] processSize) {
        int blocks = blockSize.length;
        int processes = processSize.length;

        int[] allocate = new int[processes];
        boolean[] occupied = new boolean[blocks];

        Arrays.fill(allocate, -1);

        for (int i = 0; i < processes; i++) {
            int best = -1;
            for (int j = 0; j < blocks; j++) {
                if (!occupied[j] && blockSize[j] >= processSize[i]) {
                    if (best == -1 || blockSize[j] < blockSize[best]) {
                        best = j;
                    }
                }
            }
            if (best != -1) {
                allocate[i] = best;
                occupied[best] = true;
            }
        }

        System.out.println("\n--- Best Fit Allocation ---");
        printAllocation(processSize, allocate);
    }
}
