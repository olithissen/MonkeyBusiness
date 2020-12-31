package net.tonick.monkeybusiness;

import net.tonick.monkeybusiness.opcodes.ITextContainer;
import net.tonick.monkeybusiness.opcodes.OpCode;
import net.tonick.monkeybusiness.opcodes.Print;
import net.tonick.monkeybusiness.opcodes.PrintEgo;
import net.tonick.monkeybusiness.parser.Script;
import net.tonick.monkeybusiness.parser.ScriptExtractor;
import net.tonick.monkeybusiness.parser.ScriptParser;
import net.tonick.monkeybusiness.util.HexPrettyPrinter;
import net.tonick.monkeybusiness.util.TextBeautifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A Scumm v5 text extractor
 */
public class App {
    private static final Logger logger = LogManager.getLogger(App.class);

    public static void main(String... args) throws IOException {
        File gameFile = new File(args[0]);
        byte[] bytes = ScriptExtractor.readFile(gameFile);

        // Extract all scripts from file
        List<Script> scripts = ScriptExtractor.extractScripts(bytes);

        ScriptParser parser = new ScriptParser();
        scripts.stream()
                .map(parser::parse)
                .flatMap(s -> s.getOpCodes().stream())
                .filter(oc -> oc instanceof ITextContainer)
                .map(oc -> (ITextContainer)oc)
                .filter(oc -> oc.getText() != null && !oc.getText().isEmpty() && !oc.getText().isBlank())
                .map(TextBeautifier::beautify)
                .forEach(System.out::println);

        System.out.print("");
    }
}
