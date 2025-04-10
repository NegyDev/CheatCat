package net.cheat.cat.process;

import java.awt.*;

public class ProcessEntry {
    public String name;
    public long pid;

    public ProcessEntry(String name, long pid) {
        this.name = name;
        this.pid = pid;
    }
}

