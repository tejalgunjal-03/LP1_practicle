//FIRST FIT AND WORST FIT
import java.util.Arrays;
import java.util.Scanner;

public class first_worst {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // ----- INPUT BLOCKS -----
        System.out.print("Enter number of memory blocks: ");
        int b = sc.nextInt();
        int[] blockSize = new int[b];
        System.out.println("Enter block sizes:");
        for (int i = 0; i < b; i++) blockSize[i] = sc.nextInt();

        // ----- INPUT PROCESSES -----
        System.out.print("\nEnter number of processes: ");
        int p = sc.nextInt();
        int[] processSize = new int[p];
        System.out.println("Enter process sizes:");
        for (int i = 0; i < p; i++) processSize[i] = sc.nextInt();

        // ----- MENU -----
        System.out.println("\nMemory Placement Strategies");
        System.out.println("1. First Fit");
        System.out.println("2. Worst Fit");
        System.out.print("Enter choice (1/2): ");
        int choice = sc.nextInt();

        switch (choice) {
            case 1 :
                    implimentFirstFit(blockSize, blockSize.length, processSize, processSize.length);
            case 2 :
                    implimentWorstFit(blockSize, blockSize.length, processSize, processSize.length);
            default : {
                System.out.println("Invalid choice. Running First Fit by default.");
                implimentFirstFit(blockSize, blockSize.length, processSize, processSize.length);
            }
        }
        sc.close();
    }

    // ----- same print style as your original code -----
    private static void printAllocation(int[] processSize, int[] allocate) {
        System.out.println("\nProcess No.\tProcess Size\tBlock no.\n");
        for (int i = 0; i < processSize.length; i++) {
            System.out.print((i + 1) + "\t\t\t" + processSize[i] + "\t\t\t");
            if (allocate[i] != -1) System.out.println(allocate[i] + 1);
            else System.out.println("Not Allocated");
        }
    }

    // ================= FIRST FIT (no splitting) =================
    static void implimentFirstFit(int[] blockSize, int blocks, int[] processSize, int processes) {
        int[] allocate = new int[processes];
        int[] occupied = new int[blocks];

        Arrays.fill(allocate, -1);
        Arrays.fill(occupied, 0);

        for (int i = 0; i < processes; i++) {
            for (int j = 0; j < blocks; j++) {
                if (occupied[j] == 0 && blockSize[j] >= processSize[i]) {
                    allocate[i] = j;
                    occupied[j] = 1;
                    break;
                }
            }
        }
        printAllocation(processSize, allocate);
    }

    // ================= WORST FIT (pick largest fitting free block; no splitting) =================
    static void implimentWorstFit(int[] blockSize, int blocks, int[] processSize, int processes) {
        int[] allocate = new int[processes];
        int[] occupied = new int[blocks];

        Arrays.fill(allocate, -1);
        Arrays.fill(occupied, 0);

        for (int i = 0; i < processes; i++) {
            int worst = -1;
            for (int j = 0; j < blocks; j++) {
                if (occupied[j] == 0 && blockSize[j] >= processSize[i]) {
                    if (worst == -1 || blockSize[j] > blockSize[worst]) worst = j;
                }
            }
            if (worst != -1) {
                allocate[i] = worst;
                occupied[worst] = 1;
            }
        }
        printAllocation(processSize, allocate);
    }
}
