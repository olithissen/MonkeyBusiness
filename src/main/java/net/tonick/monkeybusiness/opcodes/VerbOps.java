package net.tonick.monkeybusiness.opcodes;

public class VerbOps extends OpCode implements ITextContainer {
    private String text;

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "VerbOps{" +
                ", text='" + text + '\'' +
                '}';
    }
}
