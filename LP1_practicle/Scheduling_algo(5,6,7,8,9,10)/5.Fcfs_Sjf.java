//fcfs,sjf(preemptive)

import java.util.*;

public class Fcfs_Sjf {

    static class Process {
        int pid, arrivalTime, burstTime;
        int remainingTime, completionTime, waitingTime, turnaroundTime;

        Process(int pid, int at, int bt) {
            this.pid = pid; this.arrivalTime = at; this.burstTime = bt;
            this.remainingTime = bt;
        }

        Process copy() { return new Process(pid, arrivalTime, burstTime); }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();
        if (n <= 0) { System.out.println("Number of processes must be > 0."); return; }

        Process[] base = new Process[n];
        for (int i = 0; i < n; i++) {
            System.out.println("\nEnter details for Process " + (i + 1));
            System.out.print("Arrival Time: "); int at = sc.nextInt();
            System.out.print("Burst Time: ");   int bt = sc.nextInt();
            base[i] = new Process(i + 1, at, bt);
        }

        System.out.println("\nChoose Scheduling Algorithm:");
        System.out.println("1. FCFS (Non-Preemptive)");
        System.out.println("2. SJF (Preemptive) / SRTF");
        System.out.print("Enter choice (1/2): ");
        int choice = sc.nextInt();

        // fresh copy so original stays intact if you re-run logic later
        Process[] p = new Process[n];
        for (int i = 0; i < n; i++) p[i] = base[i].copy();

        if (choice == 1) runFCFS(p);
        else if (choice == 2) runSRTF(p);
        else System.out.println("Invalid choice. Please run again and choose 1 or 2.");
    }

    // ------------------------- FCFS (Non-Preemptive) -------------------------
    private static void runFCFS(Process[] a) {
        Arrays.sort(a, Comparator.comparingInt((Process x) -> x.arrivalTime).thenComparingInt(x -> x.pid));

        int t = 0;
        for (Process x : a) {
            if (t < x.arrivalTime) t = x.arrivalTime;
            x.completionTime = t + x.burstTime;
            x.turnaroundTime = x.completionTime - x.arrivalTime;
            x.waitingTime    = x.turnaroundTime - x.burstTime;
            t = x.completionTime;
        }

        // Gantt (plain; shows IDLE)
        List<String> blocks = new ArrayList<>();
        List<Integer> times = new ArrayList<>();
        int cursor = Arrays.stream(a).mapToInt(p -> p.arrivalTime).min().orElse(0);
        times.add(cursor);
        for (Process x : a) {
            if (cursor < x.arrivalTime) { blocks.add(" IDLE "); cursor = x.arrivalTime; times.add(cursor); }
            blocks.add(" P" + x.pid + "  ");
            cursor = x.completionTime; times.add(cursor);
        }

        printTable("=== FCFS (Non-Preemptive) ===", a);
        printGantt(blocks, times);
    }

    // -------------------------- SRTF (Preemptive SJF) ------------------------
    private static void runSRTF(Process[] a) {
        Arrays.sort(a, Comparator.comparingInt(p -> p.arrivalTime));
        int n = a.length, done = 0, t = a[0].arrivalTime;

        List<String> blocks = new ArrayList<>();
        List<Integer> times = new ArrayList<>();
        times.add(t);
        String lastLabel = null;

        while (done < n) {
            // ready queue
            Process run = null;
            for (Process x : a) {
                if (x.arrivalTime <= t && x.remainingTime > 0) {
                    if (run == null
                            || x.remainingTime < run.remainingTime
                            || (x.remainingTime == run.remainingTime && (x.arrivalTime < run.arrivalTime
                            || (x.arrivalTime == run.arrivalTime && x.pid < run.pid))))
                        run = x;
                }
            }

            if (run == null) {
                // idle until next arrival
                int next = Integer.MAX_VALUE;
                for (Process x : a) if (x.remainingTime > 0) next = Math.min(next, x.arrivalTime);
                // switch label to IDLE if needed
                t = switchLabel(blocks, times, lastLabel, " IDLE ", t);
                lastLabel = " IDLE ";
                t = next;
                continue;
            }

            // switch label to this process if needed
            String label = " P" + run.pid + "  ";
            if (lastLabel == null || !lastLabel.equals(label)) {
                if (lastLabel != null) { blocks.add(lastLabel); times.add(t); }
                lastLabel = label;
            }

            // run 1 unit
            run.remainingTime--; t++;
            if (run.remainingTime == 0) {
                run.completionTime = t;
                run.turnaroundTime = t - run.arrivalTime;
                run.waitingTime    = run.turnaroundTime - run.burstTime;
                done++;
            }
        }

        // close last block
        if (lastLabel != null) { blocks.add(lastLabel); times.add(t); }

        printTable("=== SJF (Preemptive) / SRTF ===", a);
        printGantt(blocks, times);
    }

    // if current label differs, close previous block at time t and return same t
    private static int switchLabel(List<String> blocks, List<Integer> times, String last, String next, int t) {
        if (last == null || last.equals(next)) return t;
        blocks.add(last); times.add(t);
        return t;
    }

    // ------------------------------ Output -----------------------------------
    private static void printTable(String title, Process[] a) {
        System.out.println("\n" + title);
        System.out.printf("%-4s %-4s %-4s %-4s %-5s %-4s%n", "PID", "AT", "BT", "CT", "TAT", "WT");
        System.out.println("-------------------------------------");
        for (Process x : a) {
            System.out.printf("%-4s %-4d %-4d %-4d %-5d %-4d%n",
                    "P" + x.pid, x.arrivalTime, x.burstTime,
                    x.completionTime, x.turnaroundTime, x.waitingTime);
        }
    }

    private static void printGantt(List<String> blocks, List<Integer> times) {
        System.out.println("\nGantt Chart:");
        System.out.print("|");
        for (String b : blocks) System.out.print(b + "|");
        System.out.println();
        final int W = 6;
        System.out.print(times.get(0));
        for (int i = 1; i < times.size(); i++) System.out.printf("%" + (W + 1) + "d", times.get(i));
        System.out.println();
    }
}




// //=== FCFS (Non-Preemptive) ===
// PID  AT   BT   CT   TAT   WT  
// -------------------------------------
// P1   0    2    2    2     0   
// P2   1    2    4    3     1   
// P3   5    3    8    3     0   
// P4   6    4    12   6     2   

// Gantt Chart:
// | P1  | P2  | IDLE | P3  | P4  |
// 0      2      4      5      8     12




// //=== SJF (Preemptive) / SRTF ===
// PID  AT   BT   CT   TAT   WT  
// -------------------------------------
// P1   0    2    2    2     0   
// P2   1    2    4    3     1   
// P3   5    3    8    3     0   
// P4   6    4    12   6     2   

// Gantt Chart:
// | P1  | P2  | IDLE | P3  | P4  |
// 0      2      4      5      8     12

