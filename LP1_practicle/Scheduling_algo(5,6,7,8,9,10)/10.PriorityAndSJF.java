//PRIORITY AND SJF



import java.util.*;

public class PriorityAndSJF {

    static class Process {
        int pid, at, bt, pr;        // pr used only for Priority
        int rt, ct, tat, wt;        // rt used for SRTF
        boolean done = false;       // for Priority
        Process(int id, int a, int b){ pid=id; at=a; bt=b; rt=b; }
        Process copy(){ Process p=new Process(pid, at, bt); p.pr=pr; return p; }
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();
        if (n <= 0) { System.out.println("Number of processes must be > 0."); return; }

        // Base input (AT, BT)
        Process[] base = new Process[n];
        for(int i=0;i<n;i++){
            System.out.println("\nEnter details for Process "+(i+1));
            System.out.print("Arrival Time: "); int at = sc.nextInt();
            System.out.print("Burst Time: ");   int bt = sc.nextInt();
            base[i] = new Process(i+1, at, bt);
        }

        System.out.println("\nChoose Scheduling Algorithm:");
        System.out.println("1. Priority (Non-Preemptive)");
        System.out.println("2. SJF (Preemptive) / SRTF");
        System.out.print("Enter choice: ");
        int choice = sc.nextInt();

        // Working copy
        Process[] p = new Process[n];
        for (int i=0;i<n;i++) p[i] = base[i].copy();

        if (choice == 1) {
            System.out.println("\n(Note: lower number = higher priority; 1 is highest)");
            for (int i=0;i<n;i++){
                System.out.print("Priority for P"+p[i].pid+": ");
                p[i].pr = sc.nextInt();
            }
            runPriorityNonPreemptive(p);
        } else if (choice == 2) {
            runSRTF(p);
        } else {
            System.out.println("Invalid choice.");
        }
    }

    // ---------------- Priority (Non-Preemptive) ----------------
    private static void runPriorityNonPreemptive(Process[] p){
        Arrays.sort(p,
                Comparator.comparingInt((Process a) -> a.at)
                        .thenComparingInt((Process a) -> a.pid));

        int n = p.length, done = 0;
        int t = Arrays.stream(p).mapToInt(x->x.at).min().orElse(0);

        List<String> blocks = new ArrayList<>();
        List<Integer> times  = new ArrayList<>();
        times.add(t); // first boundary

        while (done < n) {
            Process best = null;
            for (Process x : p) {
                if (!x.done && x.at <= t) {
                    if (best == null
                            || x.pr < best.pr
                            || (x.pr == best.pr && (x.at < best.at || (x.at == best.at && x.pid < best.pid)))) {
                        best = x;
                    }
                }
            }

            if (best == null) {
                int next = Integer.MAX_VALUE;
                for (Process x : p) if (!x.done) next = Math.min(next, x.at);
                if (next == Integer.MAX_VALUE) break;
                blocks.add(" IDLE ");
                t = next;
                times.add(t);    // close IDLE block
                continue;
            }

            blocks.add(" P"+best.pid+"  ");
            t += best.bt;
            best.ct = t; best.done = true; done++;
            times.add(t);       // close this process block
        }

        // Metrics
        for (Process x : p) { x.tat = x.ct - x.at; x.wt = x.tat - x.bt; }

        printTablePriority(p);
        printGantt(blocks, times);
    }

    // ---------------- SJF (Preemptive) / SRTF ----------------
    private static void runSRTF(Process[] p){
        Arrays.sort(p,
                Comparator.comparingInt((Process a) -> a.at)
                        .thenComparingInt((Process a) -> a.pid));

        int n = p.length, done = 0;
        int t = Arrays.stream(p).mapToInt(x->x.at).min().orElse(0);

        List<String> blocks = new ArrayList<>();
        List<Integer> times  = new ArrayList<>();
        times.add(t); // first boundary

        Integer lastPid = null;

        while (done < n) {
            // pick shortest remaining among arrived
            Process run = null;
            for (Process x : p) {
                if (x.at <= t && x.rt > 0) {
                    if (run == null || x.rt < run.rt || (x.rt == run.rt && x.pid < run.pid))
                        run = x;
                }
            }

            if (run == null) {
                int next = Integer.MAX_VALUE;
                for (Process x : p) if (x.rt > 0) next = Math.min(next, x.at);
                blocks.add(" IDLE ");
                t = next;
                times.add(t);
                lastPid = null;
                continue;
            }

            if (lastPid == null || !lastPid.equals(run.pid)) {
                if (lastPid != null) { blocks.add(" P"+lastPid+"  "); times.add(t); }
                lastPid = run.pid;
            }

            // run 1 time unit (preempt point each tick)
            run.rt--; t++;
            if (run.rt == 0) { run.ct = t; done++; }
        }

        // close trailing block
        if (lastPid != null) { blocks.add(" P"+lastPid+"  "); times.add(t); }

        for (Process x : p) { x.tat = x.ct - x.at; x.wt = x.tat - x.bt; }

        printTableSRTF(p);
        printGantt(blocks, times);
    }

    // ---------------- Output ----------------
    private static void printTablePriority(Process[] p){
        System.out.println("\n=== Priority Scheduling (Non-Preemptive) ===");
        System.out.printf("%-4s %-4s %-4s %-4s %-4s %-5s %-4s%n","PID","AT","BT","PR","CT","TAT","WT");
        System.out.println("------------------------------------------------");
        for (Process x : p)
            System.out.printf("P%-3d %-4d %-4d %-4d %-4d %-5d %-4d%n",
                    x.pid,x.at,x.bt,x.pr,x.ct,x.tat,x.wt);
    }

    private static void printTableSRTF(Process[] p){
        System.out.println("\n=== SJF (Preemptive) / SRTF ===");
        System.out.printf("%-4s %-4s %-4s %-4s %-5s %-4s%n","PID","AT","BT","CT","TAT","WT");
        System.out.println("-------------------------------------");
        for (Process x : p)
            System.out.printf("P%-3d %-4d %-4d %-4d %-5d %-4d%n",
                    x.pid,x.at,x.bt,x.ct,x.tat,x.wt);
    }

    private static void printGantt(List<String> blocks, List<Integer> times){
        // Expect: blocks.size() == times.size() - 1
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



// //=== SJF (Preemptive) / SRTF ===
// PID  AT   BT   CT   TAT   WT  
// -------------------------------------
// P1   0    5    9    9     4   
// P2   1    3    4    3     0   
// P3   2    4    13   11    7   
// P4   4    1    5    1     0   

// Gantt Chart:
// | P1  | P2  | P4  | P1  | P3  |
// 0      1      4      5      9     13



// //=== Priority Scheduling (Non-Preemptive) ===
// PID  AT   BT   PR   CT   TAT   WT  
// ------------------------------------------------
// P1   0    5    2    5    5     0   
// P2   1    3    1    8    7     4   
// P3   2    4    3    12   10    6   
// P4   4    1    4    13   9     8   

// Gantt Chart:
// | P1  | P2  | P3  | P4  |
// 0      5      8     12     13
