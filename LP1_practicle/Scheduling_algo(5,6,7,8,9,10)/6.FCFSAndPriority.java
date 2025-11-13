//fcfs,priority

import java.util.*;

public class FCFSAndPriority {

    // -------- Model --------
    static class Process {
        int pid, arrivalTime, burstTime, priority; // priority used only for Priority scheduling
        int completionTime, waitingTime, turnaroundTime;
        boolean done;

        Process(int pid, int at, int bt) {
            this.pid = pid; this.arrivalTime = at; this.burstTime = bt;
        }
        Process copy() {
            Process p = new Process(pid, arrivalTime, burstTime);
            p.priority = priority;
            return p;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // ----- Input base (AT, BT) -----
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

        // ----- Menu -----
        System.out.println("\nChoose Scheduling Algorithm:");
        System.out.println("1. FCFS (Non-Preemptive)");
        System.out.println("2. Priority (Non-Preemptive)");
        System.out.print("Enter choice (1/2): ");
        int choice = sc.nextInt();

        // fresh working copy
        Process[] p = new Process[n];
        for (int i = 0; i < n; i++) p[i] = base[i].copy();

        switch (choice) {
            case 1:
                runFCFS(p);
                break;
            case 2:
                // collect priorities only when needed
                System.out.println("\n(Note: lower number = higher priority; e.g., 1 is highest)");
                for (int i = 0; i < n; i++) {
                    System.out.print("Priority for P" + p[i].pid + ": ");
                    p[i].priority = sc.nextInt();
                }
                runPriorityNonPreemptive(p);
                break;
            default:
                System.out.println("Invalid choice. Please run again and choose 1 or 2.");
        }

        sc.close();
    }

    // ------------------- FCFS (Non-Preemptive) -------------------
    private static void runFCFS(Process[] a) {
        Arrays.sort(a, Comparator.comparingInt((Process x) -> x.arrivalTime)
                .thenComparingInt(x -> x.pid));

        int t = 0;
        for (Process x : a) {
            if (t < x.arrivalTime) t = x.arrivalTime;       // idle gap
            x.completionTime = t + x.burstTime;             // run to completion
            x.turnaroundTime = x.completionTime - x.arrivalTime;
            x.waitingTime    = x.turnaroundTime - x.burstTime;
            t = x.completionTime;
        }

        // Gantt (plain; includes IDLE)
        List<String> blocks = new ArrayList<>();
        List<Integer> times = new ArrayList<>();
        int cursor = Arrays.stream(a).mapToInt(p -> p.arrivalTime).min().orElse(0);
        times.add(cursor);
        for (Process x : a) {
            if (cursor < x.arrivalTime) { blocks.add(" IDLE "); cursor = x.arrivalTime; times.add(cursor); }
            blocks.add(" P" + x.pid + "  ");
            cursor = x.completionTime; times.add(cursor);
        }

        printTableFCFS(a);
        printGantt(blocks, times);
    }

    // --------------- Priority (Non-Preemptive) -------------------
    private static void runPriorityNonPreemptive(Process[] a) {
        Arrays.sort(a, Comparator.comparingInt((Process x) -> x.arrivalTime)
                .thenComparingInt(x -> x.pid));

        int n = a.length, done = 0;
        int t = Arrays.stream(a).mapToInt(p -> p.arrivalTime).min().orElse(0);

        List<String> blocks = new ArrayList<>();
        List<Integer> times = new ArrayList<>();
        times.add(t);

        while (done < n) {
            Process chosen = null;
            for (Process x : a) {
                if (!x.done && x.arrivalTime <= t) {
                    if (chosen == null
                            || x.priority < chosen.priority
                            || (x.priority == chosen.priority && (x.arrivalTime < chosen.arrivalTime
                            || (x.arrivalTime == chosen.arrivalTime && x.pid < chosen.pid)))) {
                        chosen = x;
                    }
                }
            }

            if (chosen == null) {
                // idle till next arrival
                int next = Integer.MAX_VALUE;
                for (Process x : a) if (!x.done) next = Math.min(next, x.arrivalTime);
                blocks.add(" IDLE ");
                t = next; times.add(t);
                continue;
            }

            // run to completion
            blocks.add(" P" + chosen.pid + "  ");
            t += chosen.burstTime;
            chosen.completionTime = t;
            chosen.turnaroundTime = t - chosen.arrivalTime;
            chosen.waitingTime    = chosen.turnaroundTime - chosen.burstTime;
            chosen.done = true; done++;
            times.add(t);
        }

        printTablePriority(a);
        printGantt(blocks, times);
    }

    // ------------------------ Output Helpers ------------------------
    private static void printTableFCFS(Process[] a) {
        System.out.println("\n=== FCFS (Non-Preemptive) ===");
        System.out.printf("%-4s %-4s %-4s %-4s %-5s %-4s%n", "PID", "AT", "BT", "CT", "TAT", "WT");
        System.out.println("-------------------------------------");
        for (Process x : a) {
            System.out.printf("%-4s %-4d %-4d %-4d %-5d %-4d%n",
                    "P" + x.pid, x.arrivalTime, x.burstTime, x.completionTime, x.turnaroundTime, x.waitingTime);
        }
    }

    private static void printTablePriority(Process[] a) {
        System.out.println("\n=== Priority (Non-Preemptive) ===");
        System.out.printf("%-4s %-4s %-4s %-4s %-4s %-5s %-4s%n", "PID", "AT", "BT", "PR", "CT", "TAT", "WT");
        System.out.println("------------------------------------------------");
        for (Process x : a) {
            System.out.printf("%-4s %-4d %-4d %-4d %-4d %-5d %-4d%n",
                    "P" + x.pid, x.arrivalTime, x.burstTime, x.priority,
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


// //=== Priority (Non-Preemptive) ===
// PID  AT   BT   PR   CT   TAT   WT  
// ------------------------------------------------
// P1   0    2    2    2    2     0   
// P2   1    2    1    4    3     1   
// P3   5    3    3    8    3     0   
// P4   6    4    4    12   6     2   

// Gantt Chart:
// | P1  | P2  | IDLE | P3  | P4  |
// 0      2      4      5      8     12

