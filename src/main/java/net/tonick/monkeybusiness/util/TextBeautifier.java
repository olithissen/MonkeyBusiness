package net.tonick.monkeybusiness.util;

import net.tonick.monkeybusiness.opcodes.ITextContainer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TextBeautifier {
    public static String beautify(String input) {
        if(input == null) {
            return null;
        }

        byte[] cp850s = input.getBytes(Charset.forName("CP850"));
        return input;
    }

    public static String beautify(ITextContainer container) {
        return beautify(container.getText());
    }
}
