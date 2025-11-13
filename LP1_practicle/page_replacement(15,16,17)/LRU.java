import java.util.*;

public class LRU {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n, f, pageFaults = 0;
        System.out.print("Enter number of pages: ");
        n = sc.nextInt();

        int[] pages = new int[n];
        System.out.println("Enter page reference string: ");
        for (int i = 0; i < n; i++) {
            pages[i] = sc.nextInt();
        }

        System.out.print("Enter number of frames: ");
        f = sc.nextInt();

        int[] frames = new int[f];
        int[] lastUsed = new int[f]; // Tracks when each page was last used
        Arrays.fill(frames, -1);

        System.out.println("\nPage\tFrames\t\tFault/hit");

        for (int i = 0; i < n; i++) {
            int page = pages[i];
            boolean hit = false;

            // Check if page is already in frame
            for (int j = 0; j < f; j++) {
                if (frames[j] == page) {
                    hit = true;
                    lastUsed[j] = i; // Update last used time
                    break;
                }
            }

            // If not found (page fault)
            if (!hit) {
                int emptyIndex = -1;
                for (int j = 0; j < f; j++) {
                    if (frames[j] == -1) {
                        emptyIndex = j;
                        break;
                    }
                }

                if (emptyIndex != -1) {
                    // Empty frame available
                    frames[emptyIndex] = page;
                    lastUsed[emptyIndex] = i;
                } else {
                    // Find least recently used page
                    int lruIndex = 0;
                    for (int j = 1; j < f; j++) {
                        if (lastUsed[j] < lastUsed[lruIndex]) {
                            lruIndex = j;
                        }
                    }
                    frames[lruIndex] = page;
                    lastUsed[lruIndex] = i;
                }
                pageFaults++;
            }

            // Print current frame status
            System.out.print(page + "\t");
            for (int j = 0; j < f; j++) {
                if (frames[j] != -1)
                    System.out.print(frames[j] + " ");
                else
                    System.out.print("- ");
            }

            if (!hit)
                System.out.print("\t\tFault");
            else
                System.out.print("\t\tHit");

            System.out.println();
        }

        System.out.println("\nTotal Page Faults = " + pageFaults);
        sc.close();
    }
}
