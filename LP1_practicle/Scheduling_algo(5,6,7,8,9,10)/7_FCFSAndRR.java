// FCFS,ROUNDROBIN


import java.util.*;

public class 7_FCFSAndRR {

    static class Process {
        int pid, arrivalTime, burstTime;
        int remainingTime, completionTime, waitingTime, turnaroundTime;

        Process(int pid, int at, int bt) {
            this.pid = pid;
            this.arrivalTime = at;
            this.burstTime = bt;
            this.remainingTime = bt;
        }

        Process copy() { return new Process(pid, arrivalTime, burstTime); }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        Process[] base = new Process[n];
        for (int i = 0; i < n; i++) {
            System.out.println("\nEnter details for Process " + (i+1));
            System.out.print("Arrival Time: "); int at = sc.nextInt();
            System.out.print("Burst Time: ");   int bt = sc.nextInt();
            base[i] = new Process(i+1, at, bt);
        }

        System.out.println("\nChoose Algorithm:");
        System.out.println("1. FCFS");
        System.out.println("2. Round Robin");
        System.out.print("Enter choice: ");
        int choice = sc.nextInt();

        Process[] p = new Process[n];
        for (int i = 0; i < n; i++) p[i] = base[i].copy();

        if (choice == 1)
            runFCFS(p);
        else if (choice == 2) {
            System.out.print("Enter Time Quantum: ");
            int q = sc.nextInt();
            runRR(p, q);
        }
        else System.out.println("Invalid choice.");
    }

    // ---------------- FCFS ----------------
    private static void runFCFS(Process[] p) {

        Arrays.sort(p, Comparator.comparingInt(a -> a.arrivalTime));

        int t = 0;
        for (Process x : p) {
            if (t < x.arrivalTime) t = x.arrivalTime;
            x.completionTime = t + x.burstTime;
            x.turnaroundTime = x.completionTime - x.arrivalTime;
            x.waitingTime    = x.turnaroundTime - x.burstTime;
            t = x.completionTime;
        }

        // Build Gantt Chart
        List<String> blocks = new ArrayList<>();
        List<Integer> times = new ArrayList<>();
        int start = Arrays.stream(p).mapToInt(a -> a.arrivalTime).min().orElse(0);
        times.add(start);

        int cursor = start;
        for (Process x : p) {
            if (cursor < x.arrivalTime) {
                blocks.add(" IDLE ");
                cursor = x.arrivalTime;
                times.add(cursor);
            }
            blocks.add(" P" + x.pid + "  ");
            cursor = x.completionTime;
            times.add(cursor);
        }

        printTable("=== FCFS Scheduling ===", p);
        printGantt(blocks, times);
    }

    // ---------------- Round Robin ----------------
    private static void runRR(Process[] p, int q) {

        Arrays.sort(p, Comparator.comparingInt(a -> a.arrivalTime));
        int n = p.length;

        int currentTime = p[0].arrivalTime;
        int completed = 0;
        Queue<Integer> rq = new LinkedList<>();

        int next = 0;
        while (next < n && p[next].arrivalTime <= currentTime)
            rq.offer(next++);

        List<String> blocks = new ArrayList<>();
        List<Integer> times = new ArrayList<>();
        times.add(currentTime);

        int currentLabel = -999;  // tracks current Gantt block
        final int IDLE = -1;

        while (completed < n) {

            if (rq.isEmpty()) { // CPU idle
                if (next < n) {
                    int nextArrival = p[next].arrivalTime;
                    if (currentLabel != IDLE) {
                        if (currentLabel != -999) {
                            blocks.add(" P" + currentLabel + "  ");
                            times.add(currentTime);
                        }
                        currentLabel = IDLE;
                    }
                    currentTime = nextArrival;
                    while (next < n && p[next].arrivalTime <= currentTime)
                        rq.offer(next++);
                    continue;
                }
                break;
            }

            int idx = rq.poll();
            Process run = p[idx];

            if (currentLabel != run.pid) {
                if (currentLabel == IDLE)
                    blocks.add(" IDLE ");
                else if (currentLabel != -999)
                    blocks.add(" P" + currentLabel + "  ");
                times.add(currentTime);
                currentLabel = run.pid;
            }

            int exec = Math.min(q, run.remainingTime);
            int end = currentTime + exec;

            while (next < n && p[next].arrivalTime <= end)
                rq.offer(next++);

            run.remainingTime -= exec;
            currentTime = end;

            if (run.remainingTime == 0) {
                run.completionTime = currentTime;
                completed++;
            } else rq.offer(idx);
        }

        if (currentLabel != -999) {
            if (currentLabel == IDLE) blocks.add(" IDLE ");
            else blocks.add(" P" + currentLabel + "  ");
            times.add(currentTime);
        }

        for (Process x : p) {
            x.turnaroundTime = x.completionTime - x.arrivalTime;
            x.waitingTime    = x.turnaroundTime - x.burstTime;
        }

        printTable("=== Round Robin Scheduling ===", p);
        printGantt(blocks, times);
    }

    // ---------------- Output Helpers ----------------
    private static void printTable(String title, Process[] p) {
        System.out.println("\n" + title);
        System.out.printf("%-4s %-4s %-4s %-4s %-5s %-4s\n",
                "PID", "AT", "BT", "CT", "TAT", "WT");
        System.out.println("-----------------------------------");

        for (Process x : p) {
            System.out.printf("P%-3d %-4d %-4d %-4d %-5d %-4d\n",
                    x.pid, x.arrivalTime, x.burstTime,
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
        for (int i = 1; i < times.size(); i++)
            System.out.printf("%" + (W + 1) + "d", times.get(i));
        System.out.println();
    }
}


// //=== FCFS Scheduling ===
// PID  AT   BT   CT   TAT   WT  
// -----------------------------------
// P1   0    2    2    2     0   
// P2   1    2    4    3     1   
// P3   5    3    8    3     0   
// P4   6    4    12   6     2   


// //Enter Time Quantum: 2

// === Round Robin Scheduling ===
// PID  AT   BT   CT   TAT   WT  
// -----------------------------------
// P1   0    2    2    2     0   
// P2   1    2    4    3     1   
// P3   5    3    10   5     2   
// P4   6    4    12   6     2   

// Gantt Chart:
// | P1  | P2  | IDLE | P3  | P4  | P3  | P4  |
// 0      0      2      4      5      7      9     10     12

