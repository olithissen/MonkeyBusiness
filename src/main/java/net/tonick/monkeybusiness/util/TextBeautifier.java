package net.tonick.monkeybusiness.util;

import net.tonick.monkeybusiness.opcodes.ITextContainer;
import net.tonick.monkeybusiness.parser.ScriptParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TextBeautifier {
    private static final Logger logger = LogManager.getLogger(TextBeautifier.class);

    public static String beautify(String input) {
        if(input == null) {
            return null;
        }

        byte[] cp850s = input.getBytes();
        String p0 = HexPrettyPrinter.hexStack(cp850s);
        logger.trace("\n{}", p0);

        String output = input
                .replaceAll("\\x0F", "™")
                .replaceAll("\\u0003", " ")
                .replaceAll("\\x5E", "…")
                .replaceAll("\\s+", " ")
                .replaceAll("\\u00A0", "")
                .replaceAll("daß ", "dass ")
                .replaceAll("Daß ", "Dass ")
                .replaceAll("muß", "muss")
                .replaceAll("Muß", "Muss")
                .replaceAll("müß", "müss")
                .replaceAll("wuß", "wuss")
                .replaceAll("wüß", "wüss")
                .replaceAll("laß", "lass")
                .replaceAll("läßt", "lässt")
                .replaceAll("Paß", "Pass")
                .replaceAll("paß", "pass")
                .replaceAll("verpaßte", "verpasste")
                .replaceAll("ergiß", "ergiss")
                .replaceAll("häßlich", "hässlich")
                .replaceAll("Abschluß", "Abschluss")
                .replaceAll("Streß", "Stress")
                .replaceAll("vergeßt", "vergesst")
                .replaceAll("bißchen", "bisschen")
                .replaceAll("frißt", "frisst")
                .replaceAll("Kuß", "Kuss")
                .replaceAll("Küß", "Küss")
                .replaceAll("häßlich", "hässlich")
                .replaceAll("…[^\\p{Graph}]+…", " … ");

        return output;
    }

    public static String beautify(ITextContainer container) {
        return beautify(container.getText());
    }
}
