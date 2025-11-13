import java.io.*;
import java.util.*;

public class MacroPass2 {
    public static void main(String[] args) throws Exception {
        BufferedReader ir = new BufferedReader(new FileReader("minput.txt"));
        BufferedReader mntReader = new BufferedReader(new FileReader("mnt.txt"));
        BufferedReader mdtReader = new BufferedReader(new FileReader("mdt.txt"));
        BufferedReader kpReader = new BufferedReader(new FileReader("kptab.txt"));

        BufferedWriter output = new BufferedWriter(new FileWriter("output.txt"));

        Map<String, Integer> nameToMdtp = new HashMap<>();
        Map<String, Integer> nameToKpdtp = new HashMap<>();
        Map<String, Integer> nameToPp = new HashMap<>();
        Map<String, Integer> nameToKp = new HashMap<>();

        String line;
        while ((line = mntReader.readLine()) != null) {
            String[] parts = line.trim().split("\\t");
            nameToPp.put(parts[0], Integer.parseInt(parts[1]));
            nameToKp.put(parts[0], Integer.parseInt(parts[2]));
            nameToMdtp.put(parts[0], Integer.parseInt(parts[3]) - 1);
            nameToKpdtp.put(parts[0], Integer.parseInt(parts[4]) - 1);
        }

        List<String> MDT = new ArrayList<>();
        while ((line = mdtReader.readLine()) != null)
            MDT.add(line);

        List<String[]> KPTAB = new ArrayList<>();
        while ((line = kpReader.readLine()) != null)
            KPTAB.add(line.trim().split(" "));

        while ((line = ir.readLine()) != null) {
            line = line.trim();
            if (line.equals("MACRO") || line.startsWith("&") || line.startsWith("MEND")) continue;

            String[] tokens = line.split("[ ,]+");
            String macroName = tokens[0];

            if (nameToMdtp.containsKey(macroName)) {
                int pp = nameToPp.get(macroName);
                int kp = nameToKp.get(macroName);
                int mdtp = nameToMdtp.get(macroName);
                int kpdtp = nameToKpdtp.get(macroName);

                String[] APTAB = new String[pp + kp + 1];
                int i = 1;
                for (int j = 1; j <= pp && i < tokens.length; j++, i++) {
                    APTAB[j] = tokens[i];
                }

                while (i < tokens.length) {
                    String[] kv = tokens[i].split("=");
                    String key = kv[0].replace("&", "");
                    String val = kv[1];
                    for (int j = 0; j < kp; j++) {
                        if (KPTAB.get(kpdtp + j)[0].equals(key)) {
                            APTAB[pp + j + 1] = val;
                            break;
                        }
                    }
                    i++;
                }

                for (int j = 0; j < kp; j++) {
                    if (APTAB[pp + j + 1] == null) {
                        APTAB[pp + j + 1] = KPTAB.get(kpdtp + j)[1];
                    }
                }

                int curr = mdtp;
                while (!MDT.get(curr).equals("MEND")) {
                    String[] parts = MDT.get(curr).split("\\t");
                    output.write("+ " + parts[0]);
                    for (int j = 1; j < parts.length; j++) {
                        if (parts[j].startsWith("(P,")) {
                            int idx = Integer.parseInt(parts[j].substring(3, parts[j].length() - 1));
                            output.write(" " + APTAB[idx]);
                        } else {
                            output.write(" " + parts[j]);
                        }
                    }
                    output.write("\n");
                    curr++;
                }
            } else {
                output.write(line + "\n");
            }
        }

        ir.close();
        mntReader.close();
        mdtReader.close();
        kpReader.close();
        output.close();
    }
}
