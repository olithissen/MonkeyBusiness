package net.tonick.monkeybusiness.opcodes;

public class Print extends OpCode {
    private byte[] bytes;
    private String text;

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Print{" +
                "text='" + text + '\'' +
                '}';
    }
}
