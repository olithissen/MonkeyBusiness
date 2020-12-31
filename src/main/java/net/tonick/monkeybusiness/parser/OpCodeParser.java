package net.tonick.monkeybusiness.parser;

import net.tonick.monkeybusiness.util.HexPrettyPrinter;
import net.tonick.monkeybusiness.opcodes.OpCode;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class OpCodeParser<C extends OpCode> {
    private static final Logger logger = LogManager.getLogger(OpCodeParser.class);

    private byte mainOpCode;
    protected byte opcode;
    protected ByteBuffer buffer;
    public static final byte PARAM_1 = (byte) 0x80;
    public static final byte PARAM_2 = (byte) 0x40;
    public static final byte PARAM_3 = (byte) 0x20;

    public final C run(byte opcode, ByteBuffer buffer) {
        this.opcode = opcode;
        this.mainOpCode = opcode;
        this.buffer = buffer;

        int start = buffer.position() - 1;
        C parseResult = parse();
        byte[] opCodeBytes = Arrays.copyOfRange(buffer.array(), start, buffer.position());
        logger.printf(Level.TRACE, "%s", HexPrettyPrinter.hexStack(opCodeBytes));
        parseResult.setOpCode(this.mainOpCode);
        parseResult.setOriginalBytes(opCodeBytes);
        return parseResult;
    }

    public C parse() {
        throw new UnsupportedOperationException(getClass().getName() + " is not yet implemented");
    }

    public final byte readValue8() {
        return buffer.get();
    }

    public final int readValue16() {
        return buffer.getShort();
    }

    public final int getVarOrDirectByte(byte mask) {
        if ((opcode & mask) != 0) {
            return getVar();
        }
        return buffer.get();
    }

    public final int getVarOrDirectWord(byte mask) {
        if ((opcode & mask) != 0) {
            return getVar();
        }
        return fetchScriptWord();
    }

    public final int resStrLen() {
        ByteBuffer duplicate = buffer.duplicate();
        int num = 0;

        byte chr;
        while ((chr = duplicate.get()) != (byte) 0x00) {
            num++;
            if (chr == (byte) 0xFF) {
                chr = duplicate.get();
                num++;
                if (chr != 1 && chr != 2 && chr != 3 && chr != 8) {
                    duplicate.position(duplicate.position() + 2);
                    num += 2;
                }
            }
        }

        return num;
        //return duplicate.position() - 1 - buffer.position();
    }

    public final String loadPtrToResource(int textLength) {
        byte[] textBuffer = new byte[textLength];
        buffer.get(textBuffer);
        // Advance pointer over 0x00
        buffer.get();
        return new String(textBuffer, Charset.forName("IBM850"));
    }

    public final String loadPtrToResource2() {
        ByteBuffer duplicate = buffer.duplicate();

        byte chr;
        while ((chr = duplicate.get()) != (byte) 0x00) {
//            if (chr == (byte) 0xFF) {
//                chr = duplicate.get();
//
//                if (chr != 1 && chr != 2 && chr != 3 && chr != 8) {
//                    duplicate.position(duplicate.position() + 2);
//                }
//            }
        }

        int textLength = duplicate.position() - 1 - buffer.position();
        byte[] textBuffer = new byte[textLength];
        buffer.get(textBuffer);
        // Advance pointer over 0x00
        buffer.get();
        return new String(textBuffer, Charset.forName("CP850"));
    }

    public final short fetchScriptWord() {
        ByteOrder byteOrder = buffer.order();
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        short aShort = buffer.getShort();
        buffer.order(byteOrder);
        return aShort;
    }

    public final int getVar() {
        short aShort = fetchScriptWord();
        return readVar(aShort);
    }

    public final int readVar(short var) {
        short a = 0;
        if ((var & 0x2000) != 0) {
            a = fetchScriptWord();
            if ((a & 0x2000) != 0) {
                a += readVar((short) (a & ~0x2000));
            }
        }
        return a;
    }

    public final int readTarget() {
        return fetchScriptWord();
    }

    public final long readValue24() {
        buffer.get(new byte[3]);
        return 0;
    }

    public final int getResultPos() {
        int bufferShort = buffer.getShort();
        if ((bufferShort & 0x0020) != 0) {
            buffer.getShort();
        }
        return bufferShort;
    }

    public final List<Short> getWordVararg() {
        List<Short> values = new ArrayList<>();

        byte aux;

        do {
            aux = buffer.get();
            if (aux != (byte) 0xFF) {
                getVarOrDirectWord(PARAM_1);
            }
        } while (aux != (byte) 0xFF);

        return values;
    }
}
