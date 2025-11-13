import java.io.*;
import java.util.*;

public class pass2 {
    public static void main(String[] args) throws IOException {
        Map<Integer, String[]> icLines = new LinkedHashMap<>();
        Map<Integer, String> machineCode = new LinkedHashMap<>();
        Map<Integer, Integer> symbolAddr = loadTable("SYMTAB.txt");
        Map<Integer, Integer> literalAddr = loadTable("LITTAB.txt");

        BufferedReader br = new BufferedReader(new FileReader("IC.txt"));
        int lc = 0;
        String line;

        while ((line = br.readLine()) != null) {
            String[] parts = line.trim().split("\\s+");
            if (parts[0].equals("AD")) {
                if (parts.length >= 4 && parts[1].equals("01")) {
                    lc = Integer.parseInt(parts[3]);
                }
                continue;
            } else if (parts[0].equals("DL") && parts[1].equals("02")) {
                machineCode.put(lc, String.format("%02d %d %03d", 0, 0, 0));
                lc++;
                continue;
            }

            String mc = "";
            String op = parts[1];
            String reg = "0", mem = "000";

            if (parts.length >= 3 && parts[2].matches("\\d")) {
                reg = parts[2];
            }

            if (parts.length >= 5) {
                if (parts[3].equals("S")) {
                    mem = String.format("%03d", symbolAddr.getOrDefault(Integer.parseInt(parts[4]), 0));
                } else if (parts[3].equals("L")) {
                    mem = String.format("%03d", literalAddr.getOrDefault(Integer.parseInt(parts[4]), 0));
                }
            }

            mc = String.format("%s %s %s", op, reg, mem);
            machineCode.put(lc++, mc);
        }
        br.close();

        BufferedWriter bw = new BufferedWriter(new FileWriter("MachineCode.txt"));
        for (Map.Entry<Integer, String> e : machineCode.entrySet()) {
            bw.write(e.getKey() + ": " + e.getValue() + "\n");
        }
        bw.close();
    }

    static Map<Integer, Integer> loadTable(String filename) throws IOException {
        Map<Integer, Integer> table = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        int index = 1;

        while ((line = br.readLine()) != null) {
            String[] parts = line.trim().split("\\s+");
            if (parts.length >= 2) {
                table.put(index++, Integer.parseInt(parts[1]));
            }
        }
        br.close();
        return table;
    }
}
