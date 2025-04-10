package net.cheat.cat.process.utils.mem;

import java.io.*;
import java.util.*;

public class MemoryUtils {

    public static Object getValueFromAddress(int pid, String hexAddress, String valueType) {
        try {
            long address = Long.parseLong(hexAddress, 16);
            try (RandomAccessFile memReader = new RandomAccessFile("/proc/" + pid + "/mem", "r")) {
                memReader.seek(address);

                byte[] buffer;
                switch (valueType) {
                    case "4 Bytes":
                        buffer = new byte[4];
                        memReader.readFully(buffer);
                        return byteArrayToInt(buffer);
                    case "2 Bytes":
                        buffer = new byte[2];
                        memReader.readFully(buffer);
                        return byteArrayToShort(buffer);
                    case "8 Bytes":
                        buffer = new byte[8];
                        memReader.readFully(buffer);
                        return byteArrayToLong(buffer);
                    case "String":
                        buffer = new byte[256];
                        memReader.readFully(buffer);
                        return new String(buffer).trim();
                    case "Float":
                        buffer = new byte[4];
                        memReader.readFully(buffer);
                        return byteArrayToFloat(buffer);
                    case "Double":
                        buffer = new byte[8];
                        memReader.readFully(buffer);
                        return byteArrayToDouble(buffer);
                    default:
                        throw new IllegalArgumentException("Bilinmeyen değer türü: " + valueType);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int byteArrayToInt(byte[] bytes) {
        return (bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8) |
                ((bytes[2] & 0xFF) << 16) | ((bytes[3] & 0xFF) << 24);
    }

    private static short byteArrayToShort(byte[] bytes) {
        return (short) ((bytes[0] & 0xFF) | ((bytes[1] & 0xFF) << 8));
    }

    private static long byteArrayToLong(byte[] bytes) {
        return (long) (bytes[0] & 0xFF) | ((long) (bytes[1] & 0xFF) << 8) |
                ((long) (bytes[2] & 0xFF) << 16) | ((long) (bytes[3] & 0xFF) << 24) |
                ((long) (bytes[4] & 0xFF) << 32) | ((long) (bytes[5] & 0xFF) << 40) |
                ((long) (bytes[6] & 0xFF) << 48) | ((long) (bytes[7] & 0xFF) << 56);
    }

    private static float byteArrayToFloat(byte[] bytes) {
        int intBits = byteArrayToInt(bytes);
        return Float.intBitsToFloat(intBits);
    }

    private static double byteArrayToDouble(byte[] bytes) {
        long longBits = byteArrayToLong(bytes);
        return Double.longBitsToDouble(longBits);
    }


    public static List<MemoryEntry> scan(int pid, String searchType, Object searchValue) {
        List<MemoryEntry> results = new ArrayList<>();

        try (BufferedReader mapsReader = new BufferedReader(new FileReader("/proc/" + pid + "/maps"));
             RandomAccessFile memReader = new RandomAccessFile("/proc/" + pid + "/mem", "r")) {

            String line;
            while ((line = mapsReader.readLine()) != null) {
                if (!line.contains("r") || !line.contains("w")) continue;
                if (!line.contains("[heap]") && !line.contains("[stack]") &&
                        !line.contains("[anon]") && !line.contains("[vdso]") &&
                        !line.contains("[vvar]") && !line.contains("[vsyscall]") &&
                        line.split("\\s+").length < 6) continue;

                String[] parts = line.split("\\s+");
                String[] addr = parts[0].split("-");
                long start = Long.parseLong(addr[0], 16);
                long end = Long.parseLong(addr[1], 16);

                long regionSize = end - start;

                final int MAX_BUFFER = 1024 * 1024;
                long offset = 0;

                while (offset < regionSize) {
                    int bytesToRead = (int) Math.min(MAX_BUFFER, regionSize - offset);
                    byte[] buffer = new byte[bytesToRead];

                    try {
                        memReader.seek(start + offset);
                        memReader.readFully(buffer);

                        switch (searchType) {
                            case "4 Bytes":
                                results.addAll(scanFor4Bytes(buffer, start + offset, Integer.parseInt((String) searchValue), 4));
                                break;
                            case "2 Bytes":
                                results.addAll(scanFor2Bytes(buffer, start + offset, searchValue, 2));
                                break;
                            case "8 Bytes":
                                results.addAll(scanFor8Bytes(buffer, start + offset, searchValue, 8));
                                break;
                            case "Float":
                                //results.addAll(scanForFloat(buffer, start + offset, searchValue, 4));
                                break;
                            case "Double":
                                //results.addAll(scanForDouble(buffer, start + offset, searchValue, 8));
                                break;
                            case "String":
                                results.addAll(scanForString(buffer, start + offset, searchValue));
                                break;
                            case "All":
                               // results.addAll(scanForAll(buffer, start + offset, searchValue));
                                break;
                            default:
                                throw new IllegalArgumentException("Unknown search type: " + searchType);
                        }

                    } catch (IOException ignored) {
                    }

                    offset += bytesToRead;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }



    private static List<MemoryEntry> scanFor4Bytes(byte[] buffer, long startAddr, Object searchValue, int step) {
        List<MemoryEntry> results = new ArrayList<>();
        int searchInt = (Integer) searchValue;

        for (int i = 0; i <= buffer.length - 4; i += step) {
            int value = (buffer[i] & 0xFF) | ((buffer[i + 1] & 0xFF) << 8)
                    | ((buffer[i + 2] & 0xFF) << 16) | ((buffer[i + 3] & 0xFF) << 24);
            if (value == searchInt) {
                results.add(new MemoryEntry(startAddr + i, value));
            }
        }

        return results;
    }



    private static List<MemoryEntry> scanFor2Bytes(byte[] buffer, long startAddr, Object searchValue, int step) {
        List<MemoryEntry> results = new ArrayList<>();
        int searchShort = (Integer) searchValue;

        for (int i = 0; i <= buffer.length - 2; i += step) {
            int value = (buffer[i] & 0xFF) | ((buffer[i + 1] & 0xFF) << 8);
            if (value == searchShort) {
                results.add(new MemoryEntry(startAddr + i, value));
            }
        }

        return results;
    }



    private static List<MemoryEntry> scanFor8Bytes(byte[] buffer, long startAddr, Object searchValue, int step) {
        List<MemoryEntry> results = new ArrayList<>();
        long searchLong = (Long) searchValue;

        for (int i = 0; i <= buffer.length - 8; i += step) {
            long value = 0;
            for (int j = 0; j < 8; j++) {
                value |= ((long) buffer[i + j] & 0xFFL) << (8 * j);
            }

            if (value == searchLong) {
                results.add(new MemoryEntry(startAddr + i, value));
            }
        }

        return results;
    }



    private static List<MemoryEntry> scanForString(byte[] buffer, long startAddr, Object searchValue) {
        List<MemoryEntry> results = new ArrayList<>();
        String target = (String) searchValue;
        byte[] targetBytes = target.getBytes();

        outer:
        for (int i = 0; i <= buffer.length - targetBytes.length; i++) {
            for (int j = 0; j < targetBytes.length; j++) {
                if (buffer[i + j] != targetBytes[j]) {
                    continue outer;
                }
            }
            results.add(new MemoryEntry(startAddr + i, target));
        }

        return results;
    }


    private static List<MemoryEntry> scanForFloat(byte[] buffer, long startAddr, Object searchValue) {
        List<MemoryEntry> results = new ArrayList<>();
        float searchFloatValue = (Float) searchValue;
        for (int i = 0; i < buffer.length - 3; i++) {
            int intBits = (buffer[i] & 0xFF) | ((buffer[i + 1] & 0xFF) << 8) |
                    ((buffer[i + 2] & 0xFF) << 16) | ((buffer[i + 3] & 0xFF) << 24);
            float value = Float.intBitsToFloat(intBits);

            if (value == searchFloatValue) {
                results.add(new MemoryEntry(startAddr + i, value));
            }
        }
        return results;
    }


    private static List<MemoryEntry> scanForDouble(byte[] buffer, long startAddr, Object searchValue) {
        List<MemoryEntry> results = new ArrayList<>();
        double searchDoubleValue = (Double) searchValue;
        for (int i = 0; i < buffer.length - 7; i++) {
            long longBits = (buffer[i] & 0xFFL) | ((buffer[i + 1] & 0xFFL) << 8) |
                    ((buffer[i + 2] & 0xFFL) << 16) | ((buffer[i + 3] & 0xFFL) << 24) |
                    ((buffer[i + 4] & 0xFFL) << 32) | ((buffer[i + 5] & 0xFFL) << 40) |
                    ((buffer[i + 6] & 0xFFL) << 48) | ((buffer[i + 7] & 0xFFL) << 56);
            double value = Double.longBitsToDouble(longBits);

            if (value == searchDoubleValue) {
                results.add(new MemoryEntry(startAddr + i, value));
            }
        }
        return results;
    }
}
