import java.util.Arrays;
import java.util.Scanner;

public class Twel_Best_worst {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // ----- INPUT BLOCKS -----
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
        System.out.println("1. Best Fit");
        System.out.println("2. Worst Fit");
        System.out.print("Enter choice (1/2): ");
        int choice = sc.nextInt();

        switch (choice) {
            case 1:
                implementBestFit(blockSize.clone(), blockSize.length, processSize, processSize.length);
                break;

            case 2:
                implementWorstFit(blockSize.clone(), blockSize.length, processSize, processSize.length);
                break;

            default:
                System.out.println("Invalid choice!");
        }

        sc.close();
    }

    // ----- PRINT ALLOCATION -----
    private static void printAllocation(int[] processSize, int[] allocate) {
        System.out.println("\nProcess No.\tProcess Size\tBlock no.");
        for (int i = 0; i < processSize.length; i++) {
            System.out.print((i + 1) + "\t\t" + processSize[i] + "\t\t");
            if (allocate[i] != -1)
                System.out.println(allocate[i] + 1);
            else
                System.out.println("Not Allocated");
        }
    }

    // ================= BEST FIT =================
    static void implementBestFit(int blockSize[], int blocks, int processSize[], int processes) {
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

    // ================= WORST FIT =================
    static void implementWorstFit(int blockSize[], int blocks, int processSize[], int processes) {
        int[] allocate = new int[processes];
        boolean[] occupied = new boolean[blocks];

        Arrays.fill(allocate, -1);

        for (int i = 0; i < processes; i++) {
            int worst = -1;
            for (int j = 0; j < blocks; j++) {
                if (!occupied[j] && blockSize[j] >= processSize[i]) {
                    if (worst == -1 || blockSize[j] > blockSize[worst]) {
                        worst = j;
                    }
                }
            }

            if (worst != -1) {
                allocate[i] = worst;
                occupied[worst] = true;
            }
        }

        System.out.println("\n--- Worst Fit Allocation ---");
        printAllocation(processSize, allocate);
    }
}
