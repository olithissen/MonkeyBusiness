package net.tonick.monkeybusiness.parser;

import net.tonick.monkeybusiness.opcodes.OpCode;
import net.tonick.monkeybusiness.opcodes.StopObjectCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Scumm v5 Script
 */
public class Script {
    private String type;
    private int offset;
    private int length;
    private byte[] originalBytes;
    private Exception parseError;
    private List<OpCode> opCodes = new ArrayList<>();

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

    public List<OpCode> getOpCodes() {
        return opCodes;
    }

    public void setOpCodes(List<OpCode> opCodes) {
        this.opCodes = opCodes;
    }

    public boolean isTerminatedCorrectly() {
        if (opCodes.isEmpty()) {
            return true;
        }

        return (opCodes.get(opCodes.size() - 1) instanceof StopObjectCode);
    }

    @Override
    public String toString() {
        return "Script{" +
                "type='" + type + '\'' +
                ", offset=" + offset + " (" + String.format("%08X", offset) + ")" +
                ", length=" + length + " (" + String.format("%08X", length) + ")" +
                '}';
    }

    public Exception getParseError() {
        return parseError;
    }

    public boolean hasParseError() {
        return parseError != null;
    }

    public void setParseError(Exception parseError) {
        this.parseError = parseError;
    }

    public void add(OpCode opCode) {
        opCodes.add(opCode);
    }

    public boolean hasErrors() {
        return hasParseError() || !isTerminatedCorrectly();
    }
}
