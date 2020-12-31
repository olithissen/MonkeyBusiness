package net.tonick.monkeybusiness.opcodes;

public class Print extends OpCode implements ITextContainer {
    private String actor;
    private byte[] bytes;
    private String text;

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Print{" +
                "actor='" + actor + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public String getActor() {
        return actor;
    }

    public Print setActor(String actor) {
        this.actor = actor;
        return this;
    }
}
