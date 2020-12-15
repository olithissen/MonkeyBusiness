package net.tonick.monkeybusiness;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A Scumm v5 text extractor
 */
public class App {
    // Resource types
    public static final String SCRP = "SCRP"; // script
    public static final String LSCR = "LSCR"; // local script
    public static final String EXCD = "EXCD"; // exit scene script
    public static final String ENCD = "ENCD"; // enter scene script
    public static final String VERB = "VERB"; // verb script

    // Script op codes
    public static final byte[] OP_PRINT_EGO = new byte[]{(byte) 0xD8}; // print ego
    public static final byte[] OP_PRINT = new byte[]{(byte) 0x14}; // print
    public static final byte[] OP_PRINT_ALT = new byte[]{(byte) 0x94}; // print with variable?

    public static void main(String... args) throws IOException {
        File gameFile = new File(args[0]);
        byte[] bytes = readFile(gameFile);

        // Extract all scripts from file
        List<Script> scripts = List.of(SCRP, LSCR, EXCD, ENCD, VERB).stream()
                .map(String::getBytes)
                .map(scriptType -> extractScripts(bytes, scriptType))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        scripts.stream().forEach(script -> {
            String s = ByteTools.prettyHexByteArray(script.getOriginalBytes(), 16);
            System.out.printf("%.4s @ %X", script.getType(), script.getOffset());
            System.out.println();
            System.out.println(s);
        });
    }

    private static List<Script> extractScripts(byte[] bytes, byte[] scriptTag) {
        List<Script> scripts = new ArrayList<>();
        int indexOf = 0;

        for (; ; ) {
            indexOf = KnuthMorrisPratt.indexOf(bytes, indexOf, bytes.length - indexOf, scriptTag);
            if (indexOf < 0) {
                break;
            }

            byte[] lengthBytes = Arrays.copyOfRange(bytes, indexOf + scriptTag.length, indexOf + scriptTag.length + Integer.BYTES);
            int length = ByteTools.bytesToInteger(lengthBytes);
            byte[] scriptBytes = Arrays.copyOfRange(bytes, indexOf, indexOf + length);

            Script script = new Script(new String(scriptTag), indexOf, length, scriptBytes);

            scripts.add(script);

            indexOf += length;
        }

        return scripts;
    }

    /**
     * Read and XOR input file
     *
     * @param file input file, currently only Scumm V5 (i.e. "The Secret of Monkey Island CD Version")
     * @return
     * @throws IOException
     */
    static private byte[] readFile(File file) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        IntStream.range(0, bytes.length).forEach(i -> bytes[i] = (byte) ((short) bytes[i] ^ 0x69));
        return bytes;
    }
}
