package net.tonick.monkeybusiness;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class App {
    public static void main(String... args) throws IOException {
        File dictionary = new File("D:\\Downloads\\germandict\\german.dic");
        Set<String> dict = new LinkedHashSet<>(Files.readAllLines(dictionary.toPath(), Charset.forName("windows-1252")));

        File f = new File("D:\\Downloads\\scummvm\\The Secret Of Monkey Island (CD DOS, German)\\monkey.001");
        byte[] bytes = readFile(f);
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((short) bytes[i] ^ 0x69);
        }
        String s = new String(bytes, Charset.forName("CP850"));
        String[] split = s.split("[\\x00]");

        List<String> matches = new ArrayList<>(Arrays.asList(split));

        List<String> collect = matches.stream()
                .map(x -> {
                    StringInfo info = new StringInfo();
                    info.text = x;
                    info.tokens = Arrays.asList(x.split(" "));
                    return info;
                })
                .filter(x -> x.tokens.stream().anyMatch(y -> dict.contains(y)))
                .map(x -> {
                    x.clean = x.text
                            .replaceAll("[^\\p{Alpha}\\p{Punct}\\p{Blank}äöüÄÖÜßêéèâáàîíìôóòûúù\\p{Digit}]", "")
                            .replaceAll("( )+", " ");
                    return x;
                })
                .map(x -> x.clean)
                .collect(Collectors.toList());

        for (int i = 0; i < collect.size(); i++) {
            System.out.println(i + "|" + collect.get(i));
        }
    }

    static private byte[] readFile(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }
}
