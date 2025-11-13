import java.io.*;
import java.util.*;

public class MacroPass1 {
    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader("minput.txt"));
        BufferedWriter mnt = new BufferedWriter(new FileWriter("mnt.txt"));
        BufferedWriter mdt = new BufferedWriter(new FileWriter("mdt.txt"));
        BufferedWriter kp = new BufferedWriter(new FileWriter("kptab.txt"));
        BufferedWriter pntab = new BufferedWriter(new FileWriter("pntab.txt"));

        String line;
        boolean isMacro = false;
        int mdtp = 1, kpdtp = 1;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.equals("MACRO")) {
                isMacro = true;
                String header = br.readLine().trim();
                String[] parts = header.split("[ ,]+");
                String macroName = parts[0];

                List<String> pntabList = new ArrayList<>();
                int pp = 0, kpCount = 0;
                int kStart = kpdtp;

                for (int i = 1; i < parts.length; i++) {
                    String param = parts[i].replace("&", "");
                    if (param.contains("=")) {
                        String[] kv = param.split("=");
                        pntabList.add(kv[0]);
                        kp.write(kv[0] + " " + (kv.length > 1 ? kv[1] : "-") + "\n");
                        kpCount++;
                        kpdtp++;
                    } else {
                        pntabList.add(param);
                        pp++;
                    }
                }

                mnt.write(macroName + "\t" + pp + "\t" + kpCount + "\t" + mdtp + "\t" + kStart + "\n");

                for (String p : pntabList)
                    pntab.write(macroName + ": " + p + "\n");

                while (!(line = br.readLine().trim()).equals("MEND")) {
                    String[] body = line.split("[ ,]+");
                    StringBuilder sb = new StringBuilder(body[0]);
                    for (int i = 1; i < body.length; i++) {
                        String operand = body[i];
                        if (operand.startsWith("&")) {
                            int idx = pntabList.indexOf(operand.replace("&", "")) + 1;
                            sb.append("\t(P,").append(idx).append(")");
                        } else {
                            sb.append("\t").append(operand);
                        }
                    }
                    mdt.write(sb.toString() + "\n");
                    mdtp++;
                }
                mdt.write("MEND\n");
                mdtp++;
                isMacro = false;
            }
        }

        br.close();
        mnt.close();
        mdt.close();
        kp.close();
        pntab.close();
    }
}
