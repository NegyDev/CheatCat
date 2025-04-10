package net.cheat.cat.process.utils;

import net.cheat.cat.process.ProcessEntry;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessUtils {
    public static List<ProcessEntry> getRunningProcesses() {
        List<ProcessEntry> processEntries = new ArrayList<>();
        try {
            Process process = Runtime.getRuntime().exec("ps -eo pid,user,comm");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] tokens = line.trim().split("\\s+", 3);
                if (tokens.length < 3) continue;

                String pidStr = tokens[0];
                String user = tokens[1];
                String command = tokens[2];

                if (user.equals("root")) continue;

                try {
                    long pid = Long.parseLong(pidStr);
                    processEntries.add(new ProcessEntry(command, pid));
                } catch (NumberFormatException ignored) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return processEntries;
    }

}
