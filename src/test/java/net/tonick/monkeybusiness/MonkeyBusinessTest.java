package net.tonick.monkeybusiness;

import net.tonick.monkeybusiness.opcodes.*;
import net.tonick.monkeybusiness.parser.Script;
import net.tonick.monkeybusiness.parser.ScriptExtractor;
import net.tonick.monkeybusiness.parser.ScriptParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MonkeyBusinessTest {
    @Test
    public void testReadScriptAt() throws IOException {
        File f = new File(getClass().getClassLoader().getResource("script1.bin").getFile());

        byte[] bytes = Files.readAllBytes(f.toPath());
        Script script = ScriptExtractor.readScriptAt(bytes, 0);

        ScriptParser parser = new ScriptParser();
        List<OpCode> opCodes = parser.parse(script).getOpCodes();

        List<Class<? extends OpCode>> test;
        {
            test = List.of(
                    CutScene.class,
                    LoadRoom.class,
                    OverRide.class,
                    JumpRelative.class,
                    Delay.class,
                    LoadRoom.class,
                    BreakHere.class,
                    Print.class,
                    Print.class,
                    CursorCommand.class,
                    Print.class,
                    Print.class,
                    StartSound.class,
                    BreakHere.class,
                    IsSoundRunning.class,
                    EqualZero.class,
                    LoadRoom.class,
                    StartScript.class,
                    CursorCommand.class,
                    CursorCommand.class,
                    CursorCommand.class,
                    Delay.class,
                    Print.class,
                    Wait.class,
                    Print.class,
                    Delay.class,
                    OverRide.class,
                    RoomOps.class,
                    LoadRoom.class,
                    CursorCommand.class,
                    Print.class,
                    EndCutScene.class,
                    SetOwnerOf.class,
                    SetOwnerOf.class,
                    SetOwnerOf.class,
                    SetOwnerOf.class,
                    SetOwnerOf.class,
                    SetOwnerOf.class,
                    SetOwnerOf.class,
                    SetOwnerOf.class,
                    SetOwnerOf.class,
                    SetOwnerOf.class,
                    LoadRoom.class,
                    StopObjectCode.class);
        }

        List<? extends Class<? extends OpCode>> collect = opCodes.stream().map(OpCode::getClass).collect(Collectors.toList());

        assertEquals(test.size(), collect.size());
        assertArrayEquals(test.toArray(), collect.toArray());
    }

    @Test
    public void testParserOnFullGameFile() throws IOException {
        File f = new File("/home/oli/Downloads/mi1/monkey.001");

        ScriptParser parser = new ScriptParser();

        byte[] bytes = ScriptExtractor.readFile(f);
        List<Script> scripts = ScriptExtractor.extractScripts(bytes);

        long failedScripts = scripts.stream()
                .map(parser::parse)
                .filter(script -> script.hasErrors())
                .count();

        assertEquals(0, failedScripts);
    }


//    @Test
    public void createRegressiveTestFiles() throws IOException {
        File f = new File("/home/oli/Downloads/mi1/monkey.001");

        ScriptParser parser = new ScriptParser();

        byte[] bytes = ScriptExtractor.readFile(f);
        List<Script> scripts = ScriptExtractor.extractScripts(bytes);

        List<OpCode> opCodes = scripts.stream()
                .map(parser::parse)
                .filter(script -> !script.hasParseError() && script.isTerminatedCorrectly())
                .flatMap(script -> script.getOpCodes().stream())
                .collect(Collectors.toList());

        Map<String, ByteArrayOutputStream> ocMap = new HashMap<>();

        opCodes.forEach(opCode -> {
            String simpleName = opCode.getClass().getSimpleName();
            if (ocMap.get(simpleName) == null) {
                ocMap.put(simpleName, new ByteArrayOutputStream());
                ocMap.get(simpleName).writeBytes("SCRP    ".getBytes(Charset.forName("IBM850")));
            }
            ByteArrayOutputStream byteArray = ocMap.get(simpleName);
            byteArray.writeBytes(opCode.getOriginalBytes());
        });

        Path path = Paths.get("src", "test", "resources", "opcodes");
        ocMap.forEach((key, value) -> {
            Path newFile = Paths.get(path.toAbsolutePath().toString(), key + ".bin");
            try {
                value.write(new byte[]{(byte) 0xA0});
                byte[] fileBytes = value.toByteArray();
                ByteBuffer buffer = ByteBuffer.wrap(fileBytes);
                buffer.position(4);
                buffer.putInt(fileBytes.length);
                Files.write(newFile, fileBytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        System.out.print("OK");
    }

    @ParameterizedTest
    @MethodSource("regressiveTestFileProvider")
    public void testAllOpCodesRegressive(Path path) throws IOException {
        ScriptParser parser = new ScriptParser();

        byte[] bytes = Files.readAllBytes(path);
        Script script = ScriptExtractor.readScriptAt(bytes, 0);

        parser.parse(script);

        assertNull(script.getParseError(), "Script has parse errors");
        assertTrue(script.isTerminatedCorrectly(), "Script is not terminated correctly");
    }

    public static Stream<Path> regressiveTestFileProvider() throws IOException {
        Path path = Paths.get("src", "test", "resources", "opcodes");
        return Files.list(path);//.filter(x -> x.endsWith("VerbOps.bin"));
    }
}