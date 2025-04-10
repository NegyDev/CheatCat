package net.cheat.cat.process.utils.mem;

public class MemoryEntry {
    private final long address;
    private final Object value;

    public MemoryEntry(long address, Object value) {
        this.address = address;
        this.value = value;
    }

    public String getAddress() {
        return Long.toHexString(address).toUpperCase();
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("0x%016X : %s", address, value);
    }
}
