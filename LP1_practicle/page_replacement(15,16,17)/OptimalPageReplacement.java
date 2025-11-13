import java.util.*;

public class OptimalPageReplacement {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of pages: ");
        int n = sc.nextInt();

        int[] pages = new int[n];
        System.out.println("Enter page reference string:");
        for (int i = 0; i < n; i++) {
            pages[i] = sc.nextInt();
        }

        System.out.print("Enter number of frames: ");
        int f = sc.nextInt();

        int[] frames = new int[f];
        Arrays.fill(frames, -1);

        int pageFaults = 0;

        System.out.println("\nPage\tFrames\t\tFault/Hit");

        for (int i = 0; i < n; i++) {
            int page = pages[i];
            boolean hit = false;

            // Check if page is already in frame
            for (int j = 0; j < f; j++) {
                if (frames[j] == page) {
                    hit = true;
                    break;
                }
            }

            if (!hit) {
                int replaceIndex = -1;

                // Check for empty frame
                for (int j = 0; j < f; j++) {
                    if (frames[j] == -1) {
                        replaceIndex = j;
                        break;
                    }
                }

                // If no empty frame, find the page to replace optimally
                if (replaceIndex == -1) {
                    int farthest = -1; // farthest future use
                    replaceIndex = 0;  // default

                    for (int j = 0; j < f; j++) {
                        int nextUse = -1;
                        for (int k = i + 1; k < n; k++) {
                            if (frames[j] == pages[k]) {
                                nextUse = k;
                                break;
                            }
                        }

                        // If page not used in future, replace it immediately
                        if (nextUse == -1) {
                            replaceIndex = j;
                            break;
                        }

                        // Otherwise, pick the page used farthest in future
                        if (nextUse > farthest) {
                            farthest = nextUse;
                            replaceIndex = j;
                        }
                    }
                }

                frames[replaceIndex] = page;
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

            System.out.println(hit ? "\t\tHit" : "\t\tFault");
        }

        System.out.println("\nTotal Page Faults = " + pageFaults);
        sc.close();
    }
}
