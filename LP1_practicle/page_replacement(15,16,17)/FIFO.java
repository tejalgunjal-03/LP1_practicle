import java.util.Scanner;

public class FIFO {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n, f, pageFaults = 0, count = 0;
        int[] pages = new int[30];
        int[] frames;

        System.out.print("Enter number of pages: ");
        n = sc.nextInt();

        System.out.println("Enter page reference string: ");
        for (int i = 0; i < n; i++) {
            pages[i] = sc.nextInt();
        }

        System.out.print("Enter number of frames: ");
        f = sc.nextInt();
        frames = new int[f];

        // Initialize frames to -1 (empty)
        for (int i = 0; i < f; i++) {
            frames[i] = -1;
        }

        System.out.println("\nPage\tFrames\t\tFault/Hit");

        for (int i = 0; i < n; i++) {
            int flag = 0;

            // Check if page already exists in frame
            for (int j = 0; j < f; j++) {
                if (frames[j] == pages[i]) {
                    flag = 1;
                    break;
                }
            }

            // If page not found, replace using FIFO
            if (flag == 0) {
                frames[count] = pages[i];
                count = (count + 1) % f;
                pageFaults++;
            }

            // Print current frame status
            System.out.print(pages[i] + "\t");
            for (int j = 0; j < f; j++) {
                if (frames[j] != -1)
                    System.out.print(frames[j] + " ");
                else
                    System.out.print("- ");
            }

            if (flag == 0)
                System.out.print("\t\tFault");
            else
                System.out.print("\t\tHit");

            System.out.println();
        }

        System.out.println("\nTotal Page Faults = " + pageFaults);
        sc.close();
    }
}
