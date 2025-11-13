//PRIORITY AND ROUND ROBIN 


import java.util.*;

public class PriorityAndRR {

    static class Process {
        int pid, at, bt, pr;      // pr used only for Priority
        int rt, ct, tat, wt;      // rt used for RR
        boolean done;             // for Priority
        Process(int id, int a, int b){ pid=id; at=a; bt=b; rt=b; }
        Process copy(){ Process p=new Process(pid, at, bt); p.pr=pr; return p; }
    }

    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n=sc.nextInt(); if(n<=0){ System.out.println("Number of processes must be > 0."); return; }

        Process[] base=new Process[n];
        for(int i=0;i<n;i++){
            System.out.println("\nEnter details for Process "+(i+1));
            System.out.print("Arrival Time: "); int at=sc.nextInt();
            System.out.print("Burst  Time: ");  int bt=sc.nextInt();
            base[i]=new Process(i+1,at,bt);
        }

        System.out.println("\nChoose Scheduling Algorithm:");
        System.out.println("1. Priority Scheduling (Non-Preemptive)");
        System.out.println("2. Round Robin Scheduling (Preemptive)");
        System.out.print("Enter choice (1/2): ");
        int ch=sc.nextInt();

        Process[] p=new Process[n];
        for(int i=0;i<n;i++) p[i]=base[i].copy();

        if(ch==1){
            System.out.println("\n(Note: lower number = higher priority; 1 is highest)");
            for(Process x:p){ System.out.print("Priority for P"+x.pid+": "); x.pr=sc.nextInt(); }
            runPriority(p);
        }else if(ch==2){
            System.out.print("Enter Time Quantum: "); int q=sc.nextInt();
            if(q<=0){ System.out.println("Time quantum must be > 0."); return; }
            runRR(p,q);
        }else System.out.println("Invalid choice.");
    }

    // ---------- Priority (Non-Preemptive) ----------
    private static void runPriority(Process[] p){
        Arrays.sort(p, Comparator.comparingInt((Process a)->a.at).thenComparingInt((Process a)->a.pid));
        int n=p.length, done=0, t=Arrays.stream(p).mapToInt(x->x.at).min().orElse(0);

        List<String> blk=new ArrayList<>(); List<Integer> tm=new ArrayList<>(); tm.add(t);

        while(done<n){
            Process best=null;
            for(Process x:p) if(!x.done && x.at<=t)
                if(best==null || x.pr<best.pr || (x.pr==best.pr && (x.at<best.at || (x.at==best.at && x.pid<best.pid))))
                    best=x;

            if(best==null){
                int next=Integer.MAX_VALUE; for(Process x:p) if(!x.done) next=Math.min(next,x.at);
                if(next==Integer.MAX_VALUE) break;
                blk.add(" IDLE "); t=next; tm.add(t); continue;
            }

            blk.add(" P"+best.pid+"  "); t+=best.bt; best.ct=t; best.done=true; done++; tm.add(t);
        }

        for(Process x:p){ x.tat=x.ct-x.at; x.wt=x.tat-x.bt; }
        printTablePriority(p); printGantt(blk,tm);
    }

    // ---------- Round Robin (Preemptive) ----------
    private static void runRR(Process[] p,int q){
        Arrays.sort(p, Comparator.comparingInt((Process a)->a.at).thenComparingInt((Process a)->a.pid));
        int n=p.length, done=0, t=Arrays.stream(p).mapToInt(x->x.at).min().orElse(0);

        Queue<Integer> rq=new LinkedList<>(); int next=0;
        while(next<n && p[next].at<=t) rq.offer(next++);

        List<String> blk=new ArrayList<>(); List<Integer> tm=new ArrayList<>(); tm.add(t);
        String cur=null;

        while(done<n){
            if(rq.isEmpty()){
                if(next<n){
                    if(cur!=null){ blk.add(cur); tm.add(t); cur=null; }
                    blk.add(" IDLE "); t=p[next].at; tm.add(t);
                    while(next<n && p[next].at<=t) rq.offer(next++);
                } else break;
                continue;
            }

            int i=rq.poll(); Process run=p[i]; String lab=" P"+run.pid+"  ";
            if(!lab.equals(cur)){ if(cur!=null){ blk.add(cur); tm.add(t); } cur=lab; }

            int exec=Math.min(q, run.rt), end=t+exec;
            while(next<n && p[next].at<=end) rq.offer(next++);
            run.rt-=exec; t=end;

            if(run.rt==0){ run.ct=t; done++; blk.add(cur); tm.add(t); cur=null; }
            else rq.offer(i);
        }
        if(cur!=null){ blk.add(cur); tm.add(t); }

        for(Process x:p){ x.tat=x.ct-x.at; x.wt=x.tat-x.bt; }
        printTableRR(p); printGantt(blk,tm);
    }

    // ---------- Output ----------
    private static void printTablePriority(Process[] p){
        System.out.println("\n=== Priority Scheduling (Non-Preemptive) ===");
        System.out.printf("%-4s %-4s %-4s %-4s %-4s %-5s %-4s%n","PID","AT","BT","PR","CT","TAT","WT");
        System.out.println("------------------------------------------------");
        for(Process x:p)
            System.out.printf("P%-3d %-4d %-4d %-4d %-4d %-5d %-4d%n", x.pid,x.at,x.bt,x.pr,x.ct,x.tat,x.wt);
    }

    private static void printTableRR(Process[] p){
        System.out.println("\n=== Round Robin Scheduling ===");
        System.out.printf("%-4s %-4s %-4s %-4s %-5s %-4s%n","PID","AT","BT","CT","TAT","WT");
        System.out.println("-------------------------------------");
        for(Process x:p)
            System.out.printf("P%-3d %-4d %-4d %-4d %-5d %-4d%n", x.pid,x.at,x.bt,x.ct,x.tat,x.wt);
    }

    private static void printGantt(List<String> blk,List<Integer> tm){
        System.out.println("\nGantt Chart:"); System.out.print("|");
        for(String b:blk) System.out.print(b+"|"); System.out.println();
        final int W=6; System.out.print(tm.get(0));
        for(int i=1;i<tm.size();i++) System.out.printf("%"+(W+1)+"d",tm.get(i));
        System.out.println();
    }
}


// //=== Round Robin Scheduling ===
// PID  AT   BT   CT   TAT   WT  
// -------------------------------------
// P1   0    5    12   12    7   
// P2   1    4    11   10    6   
// P3   2    2    6    4     2   
// P4   4    1    9    5     4   


// //=== Priority Scheduling (Non-Preemptive) ===
// PID  AT   BT   PR   CT   TAT   WT  
// ------------------------------------------------
// P1   0    5    2    5    5     0   
// P2   1    4    1    9    8     4   
// P3   2    2    4    12   10    8   
// P4   4    1    3    10   6     5   

// Gantt Chart:
// | P1  | P2  | P4  | P3  |
// 0      5      9     10     12

