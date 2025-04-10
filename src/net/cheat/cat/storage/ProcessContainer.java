package net.cheat.cat.storage;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProcessContainer {

    private static int p_pid;
    private static String p_name_cache;

    public static int get_pid(){
        return p_pid;
    }

    public static String p_name() {
        if (p_pid != 0) {
            if (p_name_cache == null || hasPidChanged()) {
                fetchProcessName();
            }
            return p_name_cache;
        } else {
            return "process not selected";
        }
    }

    private static boolean hasPidChanged() {
        try {
            Process process = Runtime.getRuntime().exec("cat /proc/" + p_pid + "/comm");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String currentName = reader.readLine();
            return currentName == null || !currentName.equals(p_name_cache);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void fetchProcessName() {
        try {
            Process process = Runtime.getRuntime().exec("cat /proc/" + p_pid + "/comm");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            p_name_cache = reader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            p_name_cache = "Unknown Process";
        }
    }

    public static void set_pid(int p) {
        if (p != p_pid) {
            p_pid = p;
            p_name_cache = null;
        }
    }
}
