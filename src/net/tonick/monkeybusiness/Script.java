package net.tonick.monkeybusiness;

/**
 * Represents a Scumm v5 Script
 */
public class Script {
    private String type;
    private int offset;
    private int length;
    private byte[] originalBytes;

    public Script(String type, int offset, int length, byte[] originalBytes) {
        this.type = type;
        this.offset = offset;
        this.length = length;
        this.originalBytes = originalBytes;
    }

    public Script() {
    }

    public byte[] getOriginalBytes() {
        return originalBytes;
    }

    public void setOriginalBytes(byte[] originalBytes) {
        this.originalBytes = originalBytes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "Script{" +
                "type='" + type + '\'' +
                ", offset=" + offset + " (" + String.format("%08X", offset) + ")" +
                ", length=" + length + " (" + String.format("%08X", length) + ")" +
                '}';
    }
}
