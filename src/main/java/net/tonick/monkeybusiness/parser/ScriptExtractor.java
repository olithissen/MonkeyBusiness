package net.tonick.monkeybusiness.parser;

import net.tonick.monkeybusiness.util.KnuthMorrisPratt;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ScriptExtractor {
    // Resource types
    public static final String SCRP = "SCRP"; // script
    public static final String LSCR = "LSCR"; // local script
    public static final String EXCD = "EXCD"; // exit scene script
    public static final String ENCD = "ENCD"; // enter scene script
    public static final String OBCD = "VERB"; // enter scene script
    public static final int SCRIPT_TAG_LENGTH = 4;


    public static List<Script> extractScripts(byte[] bytes) {
        List<Script> scripts = List.of(SCRP, LSCR, EXCD, ENCD, OBCD).stream()
                .map(String::getBytes)
                .map(scriptType -> extractScript(bytes, scriptType))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return scripts;
    }

    private static List<Script> extractScript(byte[] bytes, byte[] scriptTag) {
        List<Script> scripts = new ArrayList<>();
        int scriptStartIndex = 0;

        ByteBuffer buffer = ByteBuffer.wrap(scriptTag);

        for (; ; ) {
            scriptStartIndex = KnuthMorrisPratt.indexOf(bytes, scriptStartIndex, bytes.length, scriptTag);
            if (scriptStartIndex < 0) {
                break;
            }

            Script script = readScriptAt(bytes, scriptStartIndex);
            scripts.add(script);
            scriptStartIndex += script.getLength();
        }

        return scripts;
    }

    public static Script readScriptAt(byte[] bytes, int idx) {
        byte[] scriptTag = Arrays.copyOfRange(bytes, idx, idx + SCRIPT_TAG_LENGTH);
        byte[] lengthBytes = Arrays.copyOfRange(bytes, idx + SCRIPT_TAG_LENGTH, idx + SCRIPT_TAG_LENGTH + Integer.BYTES);

        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.put(lengthBytes);
        buffer.flip();
        int length = buffer.getInt();

        byte[] scriptBytes = Arrays.copyOfRange(bytes, idx, idx + length);

        Script script = new Script(new String(scriptTag), idx, length, scriptBytes);
        return script;
    }

    /**
     * Read and XOR input file
     *
     * @param file input file, currently only Scumm V5 (i.e. "The Secret of Monkey Island CD Version")
     * @return
     * @throws IOException
     */
    public static byte[] readFile(File file) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        IntStream.range(0, bytes.length).forEach(i -> bytes[i] = (byte) ((short) bytes[i] ^ 0x69));
        return bytes;
    }

}
