package net.tonick.monkeybusiness.util;

import java.nio.ByteBuffer;

public class HexPrettyPrinter {
    public static String hexView(byte[] bytes, int groupSize) {
        StringBuffer sb = new StringBuffer();
        StringBuffer hex = new StringBuffer();
        StringBuffer ascii = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            if (i % groupSize == 0) {
                sb.append(String.format("%08X | ", i));
            }

            hex.append(String.format("%02X ", bytes[i]));
            ascii.append(new String(new byte[]{bytes[i]}));

            if (i % groupSize == groupSize - 1 || i == bytes.length - 1) {
                sb.append(String.format("%-" + groupSize * 3 + "s", hex));
                sb.append("| ");
                sb.append(ascii.toString().replaceAll("[\\p{Cntrl}\\p{Zl}]", ".").replaceAll("[^\\p{Print}]", "."));
                sb.append(System.getProperty("line.separator"));
                hex.setLength(0);
                ascii.setLength(0);
            }
        }

        return sb.toString();
    }

    public static String hexStack(byte[] bytes) {
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            hex.append(String.format("%02X ", bytes[i]));
        }
        return hex.toString();
    }
}
