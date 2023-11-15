package net.tonick.monkeybusiness;

import net.tonick.monkeybusiness.opcodes.*;
import net.tonick.monkeybusiness.parser.Script;
import net.tonick.monkeybusiness.parser.ScriptExtractor;
import net.tonick.monkeybusiness.parser.ScriptParser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MonkeyBusinessTest {
    public static Stream<Path> regressiveTestFileProvider() throws IOException {
        Path path = Paths.get("src/test/resources/opcodes");
        assumeTrue(Files.exists(path));
        return Files.list(path);
    }

    @Test
    @Order(1)
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

        List<? extends Class<? extends OpCode>> collect = opCodes.stream().map(OpCode::getClass).toList();

        assertEquals(test.size(), collect.size());
        assertArrayEquals(test.toArray(), collect.toArray());
    }

    @Test
    @Order(2)
    public void testParserOnFullGameFile() throws IOException {
        URL resource = getClass().getClassLoader().getResource("monkey.001");
        assumeTrue(resource != null);
        File f = new File(resource.getFile());

        ScriptParser parser = new ScriptParser();

        byte[] bytes = ScriptExtractor.readFile(f);
        List<Script> scripts = ScriptExtractor.extractScripts(bytes);

        long failedScripts = scripts.stream()
                .map(parser::parse)
                .filter(Script::hasErrors)
                .count();

        assertEquals(0, failedScripts);
    }

    /**
     * This is not really a test but rather a test-generator. It creates a One-OPCODE-Script for each OPCODE
     *
     * @throws IOException Error reading file
     */
    @Test
    @Order(3)
    public void createRegressiveTestFiles() throws IOException {
        URL resource = getClass().getClassLoader().getResource("monkey.001");
        assumeTrue(resource != null);
        File f = new File(resource.getFile());

        ScriptParser parser = new ScriptParser();

        byte[] bytes = ScriptExtractor.readFile(f);
        List<Script> scripts = ScriptExtractor.extractScripts(bytes);

        List<OpCode> opCodes = scripts.stream()
                .map(parser::parse)
                .filter(script -> !script.hasParseError() && script.isTerminatedCorrectly())
                .flatMap(script -> script.getOpCodes().stream())
                .toList();

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

        Path path = Paths.get("src/test/resources/opcodes");
        Files.createDirectories(path);
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

        assertTrue(true);
    }

    @ParameterizedTest
    @MethodSource("regressiveTestFileProvider")
    @Order(4)
    public void testAllOpCodesRegressive(Path path) throws IOException {
        ScriptParser parser = new ScriptParser();

        byte[] bytes = Files.readAllBytes(path);
        Script script = ScriptExtractor.readScriptAt(bytes, 0);

        parser.parse(script);

        assertNull(script.getParseError(), "Script has parse errors");
        assertTrue(script.isTerminatedCorrectly(), "Script is not terminated correctly");
    }
}