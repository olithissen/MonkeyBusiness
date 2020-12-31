package net.tonick.monkeybusiness.opcodes;

public class OpCode {
    private byte opCode;
    private byte[] originalBytes;

    public void setOpCode(byte opCode) {
        this.opCode = opCode;
    }

    public byte getOpCode() {
        return opCode;
    }

    public void setOriginalBytes(byte[] originalBytes) {
        this.originalBytes = originalBytes;
    }

    public byte[] getOriginalBytes() {
        return originalBytes;
    }
}
