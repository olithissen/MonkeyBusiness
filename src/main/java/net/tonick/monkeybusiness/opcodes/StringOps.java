package net.tonick.monkeybusiness.opcodes;

public class StringOps extends OpCode implements ITextContainer {
    private String text;

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "StringOps{" +
                "text='" + text + '\'' +
                '}';
    }
}
