//SJF , ROUND ROBIN

import java.util.*;

public class SJFAndRR {

    static class Process {
        int pid, at, bt, rt, ct, wt, tat;
        Process(int id, int a, int b){ pid=id; at=a; bt=b; rt=b; }
        Process copy(){ return new Process(pid, at, bt); }
    }

    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n=sc.nextInt();

        Process[] base=new Process[n];
        for(int i=0;i<n;i++){
            System.out.println("\nEnter details for Process "+(i+1));
            System.out.print("Arrival Time: "); int at=sc.nextInt();
            System.out.print("Burst Time: ");   int bt=sc.nextInt();
            base[i]=new Process(i+1,at,bt);
        }

        System.out.println("\n1. SJF (Preemptive)");
        System.out.println("2. Round Robin");
        System.out.print("Enter choice: ");
        int ch=sc.nextInt();

        Process[] p=new Process[n];
        for(int i=0;i<n;i++) p[i]=base[i].copy();

        if(ch==1) runSRTF(p);
        else if(ch==2){
            System.out.print("Enter Time Quantum: ");
            runRR(p, sc.nextInt());
        } else System.out.println("Invalid choice");
    }

    // ---------------- SRTF ----------------
    private static void runSRTF(Process[] p){
        Arrays.sort(p, Comparator.comparingInt(a->a.at));

        int t=p[0].at, done=0, n=p.length, last=-1;
        List<String> blk=new ArrayList<>();
        List<Integer> tm=new ArrayList<>();
        tm.add(t);

        while(done<n){
            Process run=null;
            for(Process x:p)
                if(x.at<=t && x.rt>0)
                    if(run==null || x.rt<run.rt || (x.rt==run.rt && x.pid<run.pid))
                        run=x;

            if(run==null){
                int next=Integer.MAX_VALUE;
                for(Process x:p) if(x.rt>0) next=Math.min(next,x.at);
                if(last!=-1){ blk.add(" P"+last+"  "); tm.add(t); }
                blk.add(" IDLE "); tm.add(t);
                last=-1; t=next; continue;
            }

            if(last!=run.pid){
                if(last!=-1){ blk.add(" P"+last+"  "); tm.add(t); }
                last=run.pid;
            }

            run.rt--; t++;

            if(run.rt==0){
                run.ct=t;
                done++;
            }
        }
        if(last!=-1){ blk.add(" P"+last+"  "); tm.add(t); }

        for(Process x:p){
            x.tat=x.ct-x.at;
            x.wt=x.tat-x.bt;
        }

        printTable("=== SJF (Preemptive) / SRTF ===", p);
        printGantt(blk, tm);
    }

    // ---------------- ROUND ROBIN ----------------
    private static void runRR(Process[] p, int q){
        Arrays.sort(p, Comparator.comparingInt(a->a.at));

        int t=p[0].at, done=0, n=p.length, last=-1;
        Queue<Integer> rq=new LinkedList<>();
        int next=0;

        while(next<n && p[next].at<=t) rq.add(next++);

        List<String> blk=new ArrayList<>();
        List<Integer> tm=new ArrayList<>();
        tm.add(t);

        while(done<n){
            if(rq.isEmpty()){
                if(next<n){
                    if(last!=-1){ blk.add(" P"+last+"  "); tm.add(t); }
                    blk.add(" IDLE "); tm.add(t);
                    last=-1;
                    t=p[next].at;
                    while(next<n && p[next].at<=t) rq.add(next++);
                }
                continue;
            }

            int i=rq.poll();
            Process run=p[i];

            if(last!=run.pid){
                if(last!=-1){ blk.add(" P"+last+"  "); tm.add(t); }
                last=run.pid;
            }

            int exe=Math.min(q, run.rt);
            int end=t+exe;

            while(next<n && p[next].at<=end) rq.add(next++);

            run.rt-=exe; t=end;

            if(run.rt==0){
                run.ct=t; done++;
            } else rq.add(i);
        }
        if(last!=-1){ blk.add(" P"+last+"  "); tm.add(t); }

        for(Process x:p){
            x.tat=x.ct-x.at;
            x.wt=x.tat-x.bt;
        }

        printTable("=== Round Robin ===", p);
        printGantt(blk, tm);
    }

    // ---------------- Output ----------------
    private static void printTable(String title, Process[] p){
        System.out.println("\n"+title);
        System.out.printf("%-4s %-4s %-4s %-4s %-5s %-4s\n","PID","AT","BT","CT","TAT","WT");
        System.out.println("-------------------------------------");
        for(Process x:p)
            System.out.printf("P%-3d %-4d %-4d %-4d %-5d %-4d\n",
                    x.pid,x.at,x.bt,x.ct,x.tat,x.wt);
    }

    private static void printGantt(List<String> blk,List<Integer> tm){
        System.out.println("\nGantt Chart:");
        System.out.print("|");
        for(String b:blk) System.out.print(b+"|");
        System.out.println();
        final int W=6;
        System.out.print(tm.get(0));
        for(int i=1;i<tm.size();i++)
            System.out.printf("%"+(W+1)+"d",tm.get(i));
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


// //Enter Time Quantum: 3

// === Round Robin ===
// PID  AT   BT   CT   TAT   WT  
// -------------------------------------
// P1   0    5    11   11    6   
// P2   1    3    6    5     2   
// P3   2    4    13   11    7   
// P4   4    1    12   8     7   

// Gantt Chart:
// | P1  | P2  | P3  | P1  | P4  | P3  |
// 0      3      6      9     11     12     13

