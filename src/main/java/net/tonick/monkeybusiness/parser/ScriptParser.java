package net.tonick.monkeybusiness.parser;

import net.tonick.monkeybusiness.util.HexPrettyPrinter;
import net.tonick.monkeybusiness.opcodes.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Heavy lifting
 */
public class ScriptParser {
    private static final Logger logger = LogManager.getLogger(ScriptParser.class);

    public Map<Byte, OpCodeParser> opCodeLookup = new HashMap<>();
    private ByteBuffer buffer;
    private Script script;

    public ScriptParser() {
        // Collapsible Region for OpCodes
        {
            opCodeLookup.put((byte) 0x00, new StopObjectCodeParser());
            opCodeLookup.put((byte) 0xA0, new StopObjectCodeParser());

            opCodeLookup.put((byte) 0x01, new PutActorParser());
            opCodeLookup.put((byte) 0x21, new PutActorParser());
            opCodeLookup.put((byte) 0x41, new PutActorParser());
            opCodeLookup.put((byte) 0x61, new PutActorParser());
            opCodeLookup.put((byte) 0x81, new PutActorParser());
            opCodeLookup.put((byte) 0xA1, new PutActorParser());
            opCodeLookup.put((byte) 0xC1, new PutActorParser());
            opCodeLookup.put((byte) 0xE1, new PutActorParser());

            opCodeLookup.put((byte) 0x02, new StartMusicParser());
            opCodeLookup.put((byte) 0x82, new StartMusicParser());

            opCodeLookup.put((byte) 0x03, new GetActorRoomParser());
            opCodeLookup.put((byte) 0x83, new GetActorRoomParser());

            opCodeLookup.put((byte) 0x04, new IsGreaterEqualParser());
            opCodeLookup.put((byte) 0x84, new IsGreaterEqualParser());

            opCodeLookup.put((byte) 0x05, new DrawObjectParser());
            opCodeLookup.put((byte) 0x85, new DrawObjectParser());

            opCodeLookup.put((byte) 0x06, new GetActorElevationParser());
            opCodeLookup.put((byte) 0x86, new GetActorElevationParser());

            opCodeLookup.put((byte) 0x07, new SetStateParser());
            opCodeLookup.put((byte) 0x47, new SetStateParser());
            opCodeLookup.put((byte) 0x87, new SetStateParser());
            opCodeLookup.put((byte) 0xC7, new SetStateParser());

            opCodeLookup.put((byte) 0x08, new IsNotEqualParser());
            opCodeLookup.put((byte) 0x88, new IsNotEqualParser());

            opCodeLookup.put((byte) 0x09, new FaceActorParser());
            opCodeLookup.put((byte) 0x49, new FaceActorParser());
            opCodeLookup.put((byte) 0x89, new FaceActorParser());
            opCodeLookup.put((byte) 0xC9, new FaceActorParser());

            opCodeLookup.put((byte) 0x0A, new StartScriptParser());
            opCodeLookup.put((byte) 0x2A, new StartScriptParser());
            opCodeLookup.put((byte) 0x4A, new StartScriptParser());
            opCodeLookup.put((byte) 0x6A, new StartScriptParser());
            opCodeLookup.put((byte) 0x8A, new StartScriptParser());
            opCodeLookup.put((byte) 0xAA, new StartScriptParser());
            opCodeLookup.put((byte) 0xCA, new StartScriptParser());
            opCodeLookup.put((byte) 0xEA, new StartScriptParser());

            opCodeLookup.put((byte) 0x0B, new GetVerbEntryPointParser());
            opCodeLookup.put((byte) 0x4B, new GetVerbEntryPointParser());
            opCodeLookup.put((byte) 0x8B, new GetVerbEntryPointParser());
            opCodeLookup.put((byte) 0xCB, new GetVerbEntryPointParser());

            opCodeLookup.put((byte) 0x0C, new ResourceRoutinesParser());
            opCodeLookup.put((byte) 0x8C, new ResourceRoutinesParser());

            opCodeLookup.put((byte) 0x0D, new WalkActorToActorParser());
            opCodeLookup.put((byte) 0x4D, new WalkActorToActorParser());
            opCodeLookup.put((byte) 0x8D, new WalkActorToActorParser());
            opCodeLookup.put((byte) 0xCD, new WalkActorToActorParser());

            opCodeLookup.put((byte) 0x0E, new PutActorAtObjectParser());
            opCodeLookup.put((byte) 0x4E, new PutActorAtObjectParser());
            opCodeLookup.put((byte) 0x8E, new PutActorAtObjectParser());
            opCodeLookup.put((byte) 0xCE, new PutActorAtObjectParser());

            opCodeLookup.put((byte) 0x0F, new GetObjectStateParser());
            opCodeLookup.put((byte) 0x8F, new GetObjectStateParser());

            opCodeLookup.put((byte) 0x10, new GetObjectOwnerParser());
            opCodeLookup.put((byte) 0x90, new GetObjectOwnerParser());

            opCodeLookup.put((byte) 0x11, new AnimateActorParser());
            opCodeLookup.put((byte) 0x51, new AnimateActorParser());
            opCodeLookup.put((byte) 0x91, new AnimateActorParser());
            opCodeLookup.put((byte) 0xD1, new AnimateActorParser());

            opCodeLookup.put((byte) 0x12, new PanCameraToParser());
            opCodeLookup.put((byte) 0x92, new PanCameraToParser());

            opCodeLookup.put((byte) 0x13, new ActorOpsParser());
            opCodeLookup.put((byte) 0x53, new ActorOpsParser());
            opCodeLookup.put((byte) 0x93, new ActorOpsParser());
            opCodeLookup.put((byte) 0xD3, new ActorOpsParser());

            opCodeLookup.put((byte) 0x14, new PrintParser());
            opCodeLookup.put((byte) 0x94, new PrintParser());

            opCodeLookup.put((byte) 0x15, new ActorFromPosParser());
            opCodeLookup.put((byte) 0x55, new ActorFromPosParser());
            opCodeLookup.put((byte) 0x95, new ActorFromPosParser());
            opCodeLookup.put((byte) 0xD5, new ActorFromPosParser());

            opCodeLookup.put((byte) 0x16, new GetRandomNumberParser());
            opCodeLookup.put((byte) 0x96, new GetRandomNumberParser());

            opCodeLookup.put((byte) 0x17, new AndParser());
            opCodeLookup.put((byte) 0x97, new AndParser());

            opCodeLookup.put((byte) 0x18, new JumpRelativeParser());

            opCodeLookup.put((byte) 0x19, new DoSentenceParser());
            opCodeLookup.put((byte) 0x39, new DoSentenceParser());
            opCodeLookup.put((byte) 0x59, new DoSentenceParser());
            opCodeLookup.put((byte) 0x79, new DoSentenceParser());
            opCodeLookup.put((byte) 0x99, new DoSentenceParser());
            opCodeLookup.put((byte) 0xB9, new DoSentenceParser());
            opCodeLookup.put((byte) 0xD9, new DoSentenceParser());
            opCodeLookup.put((byte) 0xF9, new DoSentenceParser());

            opCodeLookup.put((byte) 0x1A, new MoveParser());
            opCodeLookup.put((byte) 0x9A, new MoveParser());

            opCodeLookup.put((byte) 0x1B, new MultiplyParser());
            opCodeLookup.put((byte) 0x9B, new MultiplyParser());

            opCodeLookup.put((byte) 0x1C, new StartSoundParser());
            opCodeLookup.put((byte) 0x9C, new StartSoundParser());

            opCodeLookup.put((byte) 0x1D, new IfClassOfIsParser());
            opCodeLookup.put((byte) 0x9D, new IfClassOfIsParser());

            opCodeLookup.put((byte) 0x1E, new WalkActorToParser());
            opCodeLookup.put((byte) 0x3E, new WalkActorToParser());
            opCodeLookup.put((byte) 0x5E, new WalkActorToParser());
            opCodeLookup.put((byte) 0x7E, new WalkActorToParser());
            opCodeLookup.put((byte) 0x9E, new WalkActorToParser());
            opCodeLookup.put((byte) 0xbE, new WalkActorToParser());
            opCodeLookup.put((byte) 0xdE, new WalkActorToParser());
            opCodeLookup.put((byte) 0xfE, new WalkActorToParser());

            opCodeLookup.put((byte) 0x1F, new IsActorInBoxParser());
            opCodeLookup.put((byte) 0x5F, new IsActorInBoxParser());
            opCodeLookup.put((byte) 0x9F, new IsActorInBoxParser());
            opCodeLookup.put((byte) 0xdF, new IsActorInBoxParser());

            opCodeLookup.put((byte) 0x20, new StopMusicParser());

            opCodeLookup.put((byte) 0x22, new GetAnimCounterParser());
            opCodeLookup.put((byte) 0xa2, new GetAnimCounterParser());

            opCodeLookup.put((byte) 0x23, new GetActorYParser());
            opCodeLookup.put((byte) 0xA3, new GetActorYParser());

            opCodeLookup.put((byte) 0x24, new LoadRoomWithEgoParser());
            opCodeLookup.put((byte) 0x64, new LoadRoomWithEgoParser());
            opCodeLookup.put((byte) 0xa4, new LoadRoomWithEgoParser());
            opCodeLookup.put((byte) 0xe4, new LoadRoomWithEgoParser());

            opCodeLookup.put((byte) 0x25, new PickupObjectParser());
            opCodeLookup.put((byte) 0x65, new PickupObjectParser());
            opCodeLookup.put((byte) 0xa5, new PickupObjectParser());
            opCodeLookup.put((byte) 0xe5, new PickupObjectParser());

            opCodeLookup.put((byte) 0x26, new SetVarRangeParser());
            opCodeLookup.put((byte) 0xa6, new SetVarRangeParser());

            opCodeLookup.put((byte) 0x27, new StringOpsParser());

            opCodeLookup.put((byte) 0x28, new EqualZeroParser());

            opCodeLookup.put((byte) 0x29, new SetOwnerOfParser());
            opCodeLookup.put((byte) 0x69, new SetOwnerOfParser());
            opCodeLookup.put((byte) 0xa9, new SetOwnerOfParser());
            opCodeLookup.put((byte) 0xe9, new SetOwnerOfParser());

            opCodeLookup.put((byte) 0x2B, new DelayVariableParser());

            opCodeLookup.put((byte) 0x2C, new CursorCommandParser());

            opCodeLookup.put((byte) 0x2D, new PutActorInRoomParser());
            opCodeLookup.put((byte) 0x6D, new PutActorInRoomParser());
            opCodeLookup.put((byte) 0xAD, new PutActorInRoomParser());
            opCodeLookup.put((byte) 0xED, new PutActorInRoomParser());

            opCodeLookup.put((byte) 0x2E, new DelayParser());

            opCodeLookup.put((byte) 0x30, new MatrixOpParser());
            opCodeLookup.put((byte) 0xb0, new MatrixOpParser());

            opCodeLookup.put((byte) 0x31, new GetInventoryCountParser());
            opCodeLookup.put((byte) 0xb1, new GetInventoryCountParser());

            opCodeLookup.put((byte) 0x32, new SetCameraAtParser());
            opCodeLookup.put((byte) 0xb2, new SetCameraAtParser());

            opCodeLookup.put((byte) 0x33, new RoomOpsParser());
            opCodeLookup.put((byte) 0x73, new RoomOpsParser());
            opCodeLookup.put((byte) 0xb3, new RoomOpsParser());
            opCodeLookup.put((byte) 0xf3, new RoomOpsParser());

            opCodeLookup.put((byte) 0x34, new GetDistParser());
            opCodeLookup.put((byte) 0x74, new GetDistParser());
            opCodeLookup.put((byte) 0xb4, new GetDistParser());
            opCodeLookup.put((byte) 0xf4, new GetDistParser());

            opCodeLookup.put((byte) 0x35, new FindObjectParser());
            opCodeLookup.put((byte) 0x75, new FindObjectParser());
            opCodeLookup.put((byte) 0xb5, new FindObjectParser());
            opCodeLookup.put((byte) 0xf5, new FindObjectParser());

            opCodeLookup.put((byte) 0x36, new WalkActorToObjectParser());
            opCodeLookup.put((byte) 0x76, new WalkActorToObjectParser());
            opCodeLookup.put((byte) 0xb6, new WalkActorToObjectParser());
            opCodeLookup.put((byte) 0xf6, new WalkActorToObjectParser());

            opCodeLookup.put((byte) 0x37, new StartObjectParser());
            opCodeLookup.put((byte) 0x77, new StartObjectParser());
            opCodeLookup.put((byte) 0xb7, new StartObjectParser());
            opCodeLookup.put((byte) 0xf7, new StartObjectParser());

            opCodeLookup.put((byte) 0x38, new LessOrEqualParser());
            opCodeLookup.put((byte) 0xb8, new LessOrEqualParser());

            opCodeLookup.put((byte) 0x3A, new SubtractParser());
            opCodeLookup.put((byte) 0xbA, new SubtractParser());

            opCodeLookup.put((byte) 0x3B, new GetActorScaleParser());
            opCodeLookup.put((byte) 0xbB, new GetActorScaleParser());

            opCodeLookup.put((byte) 0x3C, new StopSoundParser());
            opCodeLookup.put((byte) 0xbC, new StopSoundParser());

            opCodeLookup.put((byte) 0x3D, new FindInventoryParser());
            opCodeLookup.put((byte) 0x7D, new FindInventoryParser());
            opCodeLookup.put((byte) 0xbD, new FindInventoryParser());
            opCodeLookup.put((byte) 0xfD, new FindInventoryParser());

            opCodeLookup.put((byte) 0x3F, new DrawBoxParser());
            opCodeLookup.put((byte) 0x7F, new DrawBoxParser());
            opCodeLookup.put((byte) 0xbF, new DrawBoxParser());
            opCodeLookup.put((byte) 0xfF, new DrawBoxParser());

            opCodeLookup.put((byte) 0x40, new CutSceneParser());

            opCodeLookup.put((byte) 0x42, new ChainScriptParser());
            opCodeLookup.put((byte) 0xc2, new ChainScriptParser());

            opCodeLookup.put((byte) 0x43, new GetActorXParser());
            opCodeLookup.put((byte) 0xC3, new GetActorXParser());

            opCodeLookup.put((byte) 0x44, new IsLessParser());
            opCodeLookup.put((byte) 0xc4, new IsLessParser());

            opCodeLookup.put((byte) 0x46, new IncrementParser());

            opCodeLookup.put((byte) 0x48, new IsEqualParser());
            opCodeLookup.put((byte) 0xc8, new IsEqualParser());

            opCodeLookup.put((byte) 0x4C, new SoundKludgeParser());

            opCodeLookup.put((byte) 0x52, new ActorFollowCameraParser());
            opCodeLookup.put((byte) 0xd2, new ActorFollowCameraParser());

            opCodeLookup.put((byte) 0x54, new SetObjectNameParser());
            opCodeLookup.put((byte) 0xd4, new SetObjectNameParser());

            opCodeLookup.put((byte) 0x56, new GetActorMovingParser());
            opCodeLookup.put((byte) 0xd6, new GetActorMovingParser());

            opCodeLookup.put((byte) 0x57, new OrParser());
            opCodeLookup.put((byte) 0xd7, new OrParser());

            opCodeLookup.put((byte) 0x58, new OverrideParser());

            opCodeLookup.put((byte) 0x5A, new AddParser());
            opCodeLookup.put((byte) 0xDA, new AddParser());

            opCodeLookup.put((byte) 0x5B, new DivideParser());
            opCodeLookup.put((byte) 0xdB, new DivideParser());

            opCodeLookup.put((byte) 0x5D, new ActorSetClassParser());
            opCodeLookup.put((byte) 0xDD, new ActorSetClassParser());

            opCodeLookup.put((byte) 0x60, new FreezeScriptsParser());
            opCodeLookup.put((byte) 0xe0, new FreezeScriptsParser());

            opCodeLookup.put((byte) 0x62, new StopScriptParser());
            opCodeLookup.put((byte) 0xe2, new StopScriptParser());

            opCodeLookup.put((byte) 0x63, new GetActorFacingParser());
            opCodeLookup.put((byte) 0xE3, new GetActorFacingParser());

            opCodeLookup.put((byte) 0x66, new GetClosestObjActorParser());
            opCodeLookup.put((byte) 0xe6, new GetClosestObjActorParser());

            opCodeLookup.put((byte) 0x67, new GetStringWidthParser());
            opCodeLookup.put((byte) 0xe7, new GetStringWidthParser());

            opCodeLookup.put((byte) 0x68, new GetScriptRunningParser());
            opCodeLookup.put((byte) 0xe8, new GetScriptRunningParser());

            opCodeLookup.put((byte) 0x6B, new DebugParser());
            opCodeLookup.put((byte) 0xeB, new DebugParser());

            opCodeLookup.put((byte) 0x6C, new GetActorWidthParser());
            opCodeLookup.put((byte) 0xeC, new GetActorWidthParser());

            opCodeLookup.put((byte) 0x6E, new StopObjectScriptParser());
            opCodeLookup.put((byte) 0xeE, new StopObjectScriptParser());

            opCodeLookup.put((byte) 0x70, new LightsParser());
            opCodeLookup.put((byte) 0xf0, new LightsParser());

            opCodeLookup.put((byte) 0x71, new GetActorCostumeParser());
            opCodeLookup.put((byte) 0xf1, new GetActorCostumeParser());

            opCodeLookup.put((byte) 0x72, new LoadRoomParser());
            opCodeLookup.put((byte) 0xf2, new LoadRoomParser());

            opCodeLookup.put((byte) 0x78, new IsGreaterParser());
            opCodeLookup.put((byte) 0xf8, new IsGreaterParser());

            opCodeLookup.put((byte) 0x7A, new VerbOpsParser());
            opCodeLookup.put((byte) 0xfA, new VerbOpsParser());

            opCodeLookup.put((byte) 0x7B, new GetActorWalkBoxParser());
            opCodeLookup.put((byte) 0xFB, new GetActorWalkBoxParser());

            opCodeLookup.put((byte) 0x7C, new IsSoundRunningParser());
            opCodeLookup.put((byte) 0xfC, new IsSoundRunningParser());

            opCodeLookup.put((byte) 0x80, new BreakHereParser());

            opCodeLookup.put((byte) 0x98, new SystemOpsParser());

            opCodeLookup.put((byte) 0xA7, new DummyParser());

            opCodeLookup.put((byte) 0xA8, new NotEqualZeroParser());

            opCodeLookup.put((byte) 0xAB, new SaveRestoreVerbsParser());

            opCodeLookup.put((byte) 0xAC, new ExpressionParser());

            opCodeLookup.put((byte) 0xAE, new WaitParser());

            opCodeLookup.put((byte) 0xC0, new EndCutSceneParser());

            opCodeLookup.put((byte) 0xC6, new DecrementParser());

            opCodeLookup.put((byte) 0xCC, new PseudoRoomParser());

            opCodeLookup.put((byte) 0xD8, new PrintEgoParser());
        }
    }

    public Script parse(Script script) {
        logger.printf(Level.INFO, "Starting parser for script type \"%s\" @ %08X", script.getType(), script.getOffset());
        if (logger.isTraceEnabled()) {
            logger.printf(Level.TRACE, "Hex: %n%s", HexPrettyPrinter.hexView(script.getOriginalBytes(), 16));
        }

        this.script = script;

        byte[] bytes = script.getOriginalBytes();
        buffer = ByteBuffer.wrap(bytes);
        int offset = 8;
        if (script.getType().equals("LSCR")) {
            offset = 9;
        }
        buffer.position(offset);

        while (buffer.position() < bytes.length) {
            byte opCode = buffer.get();
            OpCodeParser parser = opCodeLookup.get(opCode);
            int relativePosition = buffer.position() - 1 - offset;
            int absolutePosition = script.getOffset() + buffer.position() - 1;

            try {
                logger.printf(Level.DEBUG, "@ %08X (abs: %08X rel: %04X) opcode %02X: %s", script.getOffset(), absolutePosition, relativePosition, opCode, parser.getClass().getSimpleName());
                script.add(parser.run(opCode, buffer));
            } catch (NullPointerException npe) {
                logger.printf(Level.ERROR, "@ %08X (abs: %08X rel: %04X) opcode %02X: %s", script.getOffset(), absolutePosition, relativePosition, opCode, "Unknown OP Code");
                script.setParseError(npe);
            } catch (UnsupportedOperationException uoe) {
                logger.printf(Level.ERROR, "@ %08X (abs: %08X rel: %04X) opcode %02X: Not Implemented: %s", script.getOffset(), absolutePosition, relativePosition, opCode, parser.getClass().getSimpleName());
                script.setParseError(uoe);
            } catch (BufferUnderflowException bue) {
                logger.printf(Level.ERROR, "@ %08X (abs: %08X rel: %04X) ^^^ Something is wrong above. ^^^", script.getOffset(), absolutePosition, relativePosition, opCode, parser.getClass().getSimpleName());
                script.setParseError(bue);
            }

            if (script.hasParseError()) {
                return script;
            }
        }

        return script;
    }

    // 0x44
    class CutSceneParser extends OpCodeParser<OpCode> {
        public OpCode parse() {
            getWordVararg();
            return new CutScene();
        }
    }

    // 0x72
    private class LoadRoomParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            getVarOrDirectByte(PARAM_1);
            return new LoadRoom();
        }
    }

    private class ActorFollowCameraParser extends OpCodeParser<ActorFollowCamera> {
        @Override
        public ActorFollowCamera parse() {
            getVarOrDirectByte(PARAM_1);

            return new ActorFollowCamera();
        }
    }

    // 0x2C
    private class CursorCommandParser extends OpCodeParser<OpCode> {
        // TODO: Haben Sub-Opcodes auch die 0x80, 0x40 und 0x20 Ausprägungen?
        @Override
        public OpCode parse() {
            opcode = readValue8();

            switch (opcode & (byte) 0x1F) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    break;
                case 10:
                    getVarOrDirectByte(PARAM_1);
                    getVarOrDirectByte(PARAM_2);
                    break;
                case 11:
                    getVarOrDirectByte(PARAM_1);
                    getVarOrDirectByte(PARAM_2);
                    getVarOrDirectByte(PARAM_3);
                    break;
                case 12:
                    getVarOrDirectByte(PARAM_1);
                    break;
                case 13:
                    getVarOrDirectByte(PARAM_1);
                    break;
                case 14:
                    getWordVararg();
                    break;
                default:
                    break;
            }

            return new CursorCommand();
        }
    }

    private class StringOpsParser extends OpCodeParser<StringOps> {
        @Override
        public StringOps parse() {
            StringOps stringOps = new StringOps();

            opcode = readValue8();
            switch (opcode & (byte)0x1F) {
                case 1:
                    getVarOrDirectByte(PARAM_1);
                    String s = loadPtrToResource(resStrLen());
                    stringOps.setText(s);
                    break;
                case 2:
                    getVarOrDirectByte(PARAM_1);
                    getVarOrDirectByte(PARAM_2);
                    break;
                case 3:
                    getVarOrDirectByte(PARAM_1);
                    getVarOrDirectByte(PARAM_2);
                    getVarOrDirectByte(PARAM_3);
                    break;
                case 4:
                    int pos = getResultPos();
                    int a = getVarOrDirectByte(PARAM_1);
                    int b = getVarOrDirectByte(PARAM_2);
                    break;
                case 5:
                    getVarOrDirectByte(PARAM_1);
                    getVarOrDirectByte(PARAM_2);
                    break;
                default:
                    break;
            }
            return stringOps;
        }
    }

    private class SubtractParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            getResultPos();
            getVarOrDirectWord(PARAM_1);
            return new Subtract();
        }
    }

    private class SystemOpsParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            opcode = readValue8();
            return new SystemOps();
        }
    }

    private class VerbOpsParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            getVarOrDirectByte(PARAM_1);

            while ((opcode = readValue8()) != (byte) 0xff) {

                switch (opcode & (byte) 0x1F) {
                    case 1:
                        getVarOrDirectWord(PARAM_1);
                        break;
                    case 2:
                        loadPtrToResource(resStrLen());
                        break;
                    case 3:
                        getVarOrDirectByte(PARAM_1);
                        break;

                    case 4:
                        getVarOrDirectByte(PARAM_1);
                        break;

                    case 5:
                        getVarOrDirectWord(PARAM_1);
                        getVarOrDirectWord(PARAM_2);
                        break;

                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        break;
                    case 16:
                        getVarOrDirectByte(PARAM_1);
                        break;

                    case 17:
                        break;
                    case 18:
                        getVarOrDirectByte(PARAM_1);
                        break;

                    case 19:
                        break;
                    case 20:
                        getVarOrDirectWord(PARAM_1);
                        loadPtrToResource(resStrLen() + 1);
                        break;
                    case 22:
                        getVarOrDirectWord(PARAM_1);
                        getVarOrDirectByte(PARAM_2);
                        break;
                    case 23:
                        getVarOrDirectByte(PARAM_1);
                        break;
                    default:
                        break;
                }
            }

            return new VerbOps();
        }
    }

    // 0xAE
    private class WaitParser extends OpCodeParser<OpCode> {
        // TODO: Haben Sub-Opcodes auch die 0x80, 0x40 und 0x20 Ausprägungen?
        @Override
        public OpCode parse() {
            opcode = readValue8();

            switch (opcode & 0x1F) {
                case 1:
                    getVarOrDirectByte(PARAM_1);
                    break;

                case 2:
                case 3:
                case 4:
                    break;
            }

            return new Wait();
        }
    }

    private class WalkActorToParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            getVarOrDirectByte(PARAM_1);
            getVarOrDirectWord(PARAM_2);
            getVarOrDirectWord(PARAM_3);

            return new WalkActorTo();
        }
    }

    private class WalkActorToActorParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            getVarOrDirectByte(PARAM_1);
            getVarOrDirectByte(PARAM_2);
            readValue8();

            return new WalkActorToActor();
        }
    }

    private class WalkActorToObjectParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            getVarOrDirectByte(PARAM_1);
            getVarOrDirectWord(PARAM_2);

            return new WalkActorToObject();
        }
    }

    private class DebugParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            getVarOrDirectWord(PARAM_1);
            return new Debug();
        }
    }

    private class DecrementParser extends OpCodeParser<Decrement> {
        @Override
        public Decrement parse() {
            getResultPos();
            return new Decrement();
        }
    }

    // 0x2E
    private class DelayParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            // This should be 24-bit LE
            readValue24();
            return new Delay();
        }
    }

    private class DelayVariableParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            getVar();
            return new DelayVariable();
        }
    }

    private class DivideParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            getResultPos();
            getVarOrDirectWord(PARAM_1);

            return new Divide();
        }
    }

    private class DoSentenceParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            int verb = getVarOrDirectByte(PARAM_1);
            byte b = Integer.valueOf(verb).byteValue();
            if (b != (byte) 0xFE) {
                getVarOrDirectWord(PARAM_2);
                getVarOrDirectWord(PARAM_3);
            }
            return new DoSentence();
        }
    }

    private class DrawBoxParser extends OpCodeParser<DrawBox> {
        @Override
        public DrawBox parse() {
            getVarOrDirectWord(PARAM_1);
            getVarOrDirectWord(PARAM_2);

            readValue8();
            getVarOrDirectWord(PARAM_1);
            getVarOrDirectWord(PARAM_2);
            getVarOrDirectByte(PARAM_3);

            return new DrawBox();
        }
    }

    private class DrawObjectParser extends OpCodeParser<DrawObject> {
        @Override
        public DrawObject parse() {
            getVarOrDirectWord(PARAM_1);

            opcode = readValue8();

            switch (opcode & (byte) 0x1F) {
                case 1:
                    getVarOrDirectWord(PARAM_1);
                    getVarOrDirectWord(PARAM_2);
                    break;
                case 2:
                    getVarOrDirectWord(PARAM_1);
                    break;
                case 0x1F:
                    break;
                default:
                    break;
            }

            return new DrawObject();
        }
    }

    private class ActorFromPosParser extends OpCodeParser<ActorFromPos> {
        @Override
        public ActorFromPos parse() {
            getResultPos();
            getVarOrDirectWord(PARAM_1);
            getVarOrDirectWord(PARAM_2);

            return new ActorFromPos();
        }
    }

    private class DummyParser extends OpCodeParser<OpCode> {
    }

    // 0xC0
    private class EndCutSceneParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            // NOP
            return new EndCutScene();
        }
    }

    private class EqualZeroParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            getVar();
            readTarget();
            return new EqualZero();
        }
    }

    private class ExpressionParser extends OpCodeParser<Expression> {
        @Override
        public Expression parse() {
            int result = getResultPos();
            while ((opcode = readValue8()) != (byte) 0xff) {
                check:
                switch (opcode & 0x1F) {
                    case 1:
                        getVarOrDirectByte(PARAM_1);
                        break check;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        break;
                    case 6:
                        byte opcode = readValue8();
                        OpCodeParser opCodeParser = opCodeLookup.get(opcode);
                        opCodeParser.run(opcode, buffer);
                        break check;
                }
            }

            return new Expression();
        }
    }

    private class FaceActorParser extends OpCodeParser<FaceActor> {
        @Override
        public FaceActor parse() {
            getVarOrDirectByte(PARAM_1);
            getVarOrDirectWord(PARAM_2);

            return new FaceActor();
        }
    }

    private class FindInventoryParser extends OpCodeParser<FindInventory> {
        @Override
        public FindInventory parse() {
            getResultPos();
            getVarOrDirectByte(PARAM_1);
            getVarOrDirectByte(PARAM_2);
            return new FindInventory();
        }
    }

    private class FindObjectParser extends OpCodeParser<FindObject> {
        @Override
        public FindObject parse() {
            getResultPos();
            getVarOrDirectByte(PARAM_1);
            getVarOrDirectByte(PARAM_2);
            return new FindObject();
        }
    }

    private class FreezeScriptsParser extends OpCodeParser<FreezeScripts> {
        @Override
        public FreezeScripts parse() {
            getVarOrDirectByte(PARAM_1);

            return new FreezeScripts();
        }
    }

    private class GetActorCostumeParser extends OpCodeParser<GetActorCostume> {
        @Override
        public GetActorCostume parse() {
            getResultPos();
            getVarOrDirectByte(PARAM_1);

            return new GetActorCostume();
        }
    }

    private class GetActorElevationParser extends OpCodeParser<GetActorElevation> {
        @Override
        public GetActorElevation parse() {
            getResultPos();
            getVarOrDirectByte(PARAM_1);

            return new GetActorElevation();
        }
    }

    private class ActorOpsParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            int actor = getVarOrDirectByte(PARAM_1);

            while((opcode = buffer.get()) != (byte)0xFF) {
                switch (opcode & (byte)0x1F) {
                    case 0:
                        getVarOrDirectByte(PARAM_1);
                        break;
                    case 1:
                        getVarOrDirectByte(PARAM_1);
                        break;
                    case 2:
                        getVarOrDirectByte(PARAM_1);
                        getVarOrDirectByte(PARAM_2);
                        break;
                    case 3:
                        getVarOrDirectByte(PARAM_1);
                        break;
                    case 4:
                        getVarOrDirectByte(PARAM_1);
                        break;
                    case 5:
                        getVarOrDirectByte(PARAM_1);
                        getVarOrDirectByte(PARAM_2);
                        break;
                    case 6:
                        getVarOrDirectByte(PARAM_1);
                        break;
                    case 7:
                        getVarOrDirectByte(PARAM_1);
                        getVarOrDirectByte(PARAM_2);
                        getVarOrDirectByte(PARAM_3);
                        break;
                    case 8:
                        break;
                    case 9:
                        getVarOrDirectWord(PARAM_1);
                        break;
                    case 10:
                        break;
                    case 11:
                        getVarOrDirectByte(PARAM_1);
                        getVarOrDirectByte(PARAM_2);
                        break;
                    case 12:
                        getVarOrDirectByte(PARAM_1);
                        break;
                    case 13:
                        loadPtrToResource(resStrLen());
                        break;
                    case 14:
                        getVarOrDirectByte(PARAM_1);
                        break;
                    case 16:
                        getVarOrDirectByte(PARAM_1);
                        break;
                    case 17:
                        getVarOrDirectByte(PARAM_1);
                        getVarOrDirectByte(PARAM_2);
                        break;
                    case 18:
                        break;
                    case 19:
                        getVarOrDirectByte(PARAM_1);
                        break;
                    case 20:
                        break;
                    case 21:
                        break;
                    case 22:
                        getVarOrDirectByte(PARAM_1);
                        break;
                    case 23:
                        getVarOrDirectByte(PARAM_1);
                        break;
                }
            }

            return new ActorOps();
        }
    }

    private class GetActorFacingParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            int result = getResultPos();
            int actor = getVarOrDirectByte(PARAM_1);

            return new GetActorFacing();
        }
    }

    private class GetActorMovingParser extends OpCodeParser<GetActorMoving> {
        @Override
        public GetActorMoving parse() {
            getResultPos();
            getVarOrDirectByte(PARAM_1);
            return new GetActorMoving();
        }
    }

    private class GetActorRoomParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            getResultPos();
            getVarOrDirectByte(PARAM_1);
            return new GetActorRoom();
        }
    }

    private class GetActorScaleParser extends OpCodeParser<GetActorScale> {
        @Override
        public GetActorScale parse() {
            getResultPos();
            getVarOrDirectByte(PARAM_1);

            return new GetActorScale();
        }
    }

    private class GetActorWalkBoxParser extends OpCodeParser<GetActorWalkBox> {
        @Override
        public GetActorWalkBox parse() {
            getResultPos();
            getVarOrDirectByte(PARAM_1);

            return new GetActorWalkBox();
        }
    }

    private class GetActorWidthParser extends OpCodeParser<GetActorWidth> {
        @Override
        public GetActorWidth parse() {
            getResultPos();
            getVarOrDirectByte(PARAM_1);

            return new GetActorWidth();
        }
    }

    private class GetActorXParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            int result = buffer.getShort();
            buffer.getShort();
            return new GetActorX();
        }
    }

    private class GetActorYParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            int result = buffer.getShort();
            buffer.getShort();
            return new GetActorY();
        }
    }

    private class GetAnimCounterParser extends OpCodeParser<OpCode> {
    }

    private class GetClosestObjActorParser extends OpCodeParser<OpCode> {
    }

    private class ActorSetClassParser extends OpCodeParser<ActorSetClass> {
        @Override
        public ActorSetClass parse() {
            getVarOrDirectWord(PARAM_1);
            getWordVararg();
            return new ActorSetClass();
        }
    }

    private class GetDistParser extends OpCodeParser<GetDist> {
        @Override
        public GetDist parse() {
            getResultPos();
            getVarOrDirectWord(PARAM_1);
            getVarOrDirectWord(PARAM_2);

            return new GetDist();
        }
    }

    private class GetInventoryCountParser extends OpCodeParser<GetInventoryCount> {
        @Override
        public GetInventoryCount parse() {
            getResultPos();
            getVarOrDirectByte(PARAM_1);
            return new GetInventoryCount();
        }
    }

    private class GetObjectOwnerParser extends OpCodeParser<GetObjectOwner> {
        @Override
        public GetObjectOwner parse() {
            getResultPos();
            getVarOrDirectWord(PARAM_1);

            return new GetObjectOwner();
        }
    }

    private class GetObjectStateParser extends OpCodeParser<GetObjectState> {
        @Override
        public GetObjectState parse() {
            getResultPos();
            getVarOrDirectWord(PARAM_1);

            return new GetObjectState();
        }
    }

    private class GetRandomNumberParser extends OpCodeParser<GetRandomNumber> {
        @Override
        public GetRandomNumber parse() {
            getResultPos();
            getVarOrDirectByte(PARAM_1);
            return new GetRandomNumber();
        }
    }

    private class GetScriptRunningParser extends OpCodeParser<GetScriptRunning> {
        @Override
        public GetScriptRunning parse() {
            getResultPos();
            getVarOrDirectByte(PARAM_1);
            return new GetScriptRunning();
        }
    }

    private class GetStringWidthParser extends OpCodeParser<OpCode> {
    }

    private class GetVerbEntryPointParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            int result = buffer.getShort();
            buffer.getShort();
            buffer.getShort();
            return new GetVerbEntryPoint();
        }
    }

    private class IfClassOfIsParser extends OpCodeParser<IfClassOfIs> {
        @Override
        public IfClassOfIs parse() {
            getVarOrDirectWord(PARAM_1);
            getWordVararg();
            getResultPos();
            return new IfClassOfIs();
        }
    }

    private class IfNotStateParser extends OpCodeParser<OpCode> {
    }

    private class AddParser extends OpCodeParser<Add> {
        @Override
        public Add parse() {
            getResultPos();
            getVarOrDirectWord(PARAM_1);

            return new Add();
        }
    }

    private class IfStateParser extends OpCodeParser<OpCode> {
    }

    private class IncrementParser extends OpCodeParser<Increment> {
        @Override
        public Increment parse() {
            getResultPos();
            return new Increment();
        }
    }

    private class IsActorInBoxParser extends OpCodeParser<IsActorInBox> {
        @Override
        public IsActorInBox parse() {
            getVarOrDirectByte(PARAM_1);
            getVarOrDirectByte(PARAM_2);
            return new IsActorInBox();
        }
    }

    // 0x48 || 0xC8
    private class IsEqualParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            getVar();
            getVarOrDirectWord(PARAM_1);
            readTarget();
            return new IsEqual();
        }
    }

    private class IsGreaterParser extends OpCodeParser<IsGreater> {
        @Override
        public IsGreater parse() {
            getVar();
            getVarOrDirectWord(PARAM_1);
            readTarget();

            return new IsGreater();
        }
    }

    private class IsGreaterEqualParser extends OpCodeParser<IsGreaterEqual> {
        @Override
        public IsGreaterEqual parse() {
            getVar();
            getVarOrDirectWord(PARAM_1);
            readTarget();

            return new IsGreaterEqual();
        }
    }

    private class IsLessParser extends OpCodeParser<IsLess> {
        @Override
        public IsLess parse() {
            getVar();
            getVarOrDirectWord(PARAM_1);
            readTarget();

            return new IsLess();
        }
    }

    private class IsNotEqualParser extends OpCodeParser<IsNotEqual> {
        @Override
        public IsNotEqual parse() {
            getVar();
            getVarOrDirectWord(PARAM_1);
            readTarget();

            return new IsNotEqual();
        }
    }

    // 0x7C
    private class IsSoundRunningParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            getResultPos();
            getVarOrDirectByte(PARAM_1);
            return new IsSoundRunning();
        }
    }

    // 0x18
    private class JumpRelativeParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            readTarget();
            return new JumpRelative();
        }
    }

    private class AndParser extends OpCodeParser<OpCode> {
    }

    private class LessOrEqualParser extends OpCodeParser<LessOrEqual> {
        @Override
        public LessOrEqual parse() {
            getVar();
            getVarOrDirectWord(PARAM_1);
            readTarget();

            return new LessOrEqual();
        }
    }

    private class LightsParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            getVarOrDirectByte(PARAM_1);
            readValue8();
            readValue8();
            return new Lights();
        }
    }

    private class LoadRoomWithEgoParser extends OpCodeParser<LoadRoomWithEgo> {
        @Override
        public LoadRoomWithEgo parse() {
            getVarOrDirectWord(PARAM_1);
            getVarOrDirectByte(PARAM_2);

            int x = readValue16();
            int y = readValue16();

            return new LoadRoomWithEgo();
        }
    }

    private class MatrixOpParser extends OpCodeParser<MatrixOp> {
        @Override
        public MatrixOp parse() {
            opcode = readValue8();

            switch (opcode & (byte)0x1F) {
                case 1:
                case 2:
                case 3:
                    getVarOrDirectByte(PARAM_1);
                    getVarOrDirectByte(PARAM_2);
                    break;
                //case 4:
                default:
                    break;
            }

            return new MatrixOp();
        }
    }

    // 0x1A
    private class MoveParser extends OpCodeParser<Move> {
        @Override
        public Move parse() {
            getResultPos();
            getVarOrDirectWord(PARAM_1);
            return new Move();
        }
    }

    private class MultiplyParser extends OpCodeParser<Multiply> {
        @Override
        public Multiply parse() {
            getResultPos();
            getVarOrDirectWord(PARAM_1);

            return new Multiply();
        }
    }

    private class NotEqualZeroParser extends OpCodeParser<NotEqualZero> {
        @Override
        public NotEqualZero parse() {
            getVar();
            readTarget();
            return new NotEqualZero();
        }
    }

    private class OldRoomEffectParser extends OpCodeParser<OpCode> {
    }

    private class OrParser extends OpCodeParser<OpCode> {
    }

    private class AnimateActorParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            getVarOrDirectByte(PARAM_1);
            getVarOrDirectByte(PARAM_2);
            return new AnimateActor();
        }
    }

    // 0x58
    private class OverrideParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            buffer.get();
            return new OverRide();
        }
    }

    private class PanCameraToParser extends OpCodeParser<PanCameraTo> {
        @Override
        public PanCameraTo parse() {
            getVarOrDirectWord(PARAM_1);

            return new PanCameraTo();
        }
    }

    private class PickupObjectParser extends OpCodeParser<PickupObject> {
        @Override
        public PickupObject parse() {
            getVarOrDirectWord(PARAM_1);
            getVarOrDirectByte(PARAM_2);
            return new PickupObject();
        }
    }

    // 0x14
    private class PrintParser extends OpCodeParser<Print> {
        @Override
        public Print parse() {
            Print p = new Print();
            int actor = getVarOrDirectByte(PARAM_1);
            p.setActor(String.valueOf(actor));

            while ((opcode = readValue8()) != (byte) 0xFF) {
                // Text Pos
                switch (opcode & (byte) 0xF) {
                    case (byte) 0x00:
                        int x = getVarOrDirectWord(PARAM_1);
                        int y = getVarOrDirectWord(PARAM_2);
                        break;
                    case (byte) 0x01:
                        int color = getVarOrDirectByte(PARAM_1);
                        break;
                    case (byte) 0x02:
                        int right = getVarOrDirectWord(PARAM_1);
                        break;
                    case (byte) 0x03:
                        int width = getVarOrDirectWord(PARAM_1);
                        int height = getVarOrDirectWord(PARAM_2);
                        break;
                    case (byte) 0x04:
                        break;
                    case (byte) 0x06:
                        break;
                    case (byte) 0x07:
                        break;
                    case (byte) 0x08:
                        int offset = getVarOrDirectWord(PARAM_1);
                        int delay = getVarOrDirectWord(PARAM_2);
                        break;
                    case (byte) 0x0F:
                        String s = loadPtrToResource(resStrLen());
                        p.setText(s);
                        return p;
                    default:
                        break;
                }
            }

            return p;
        }
    }

    private class PrintEgoParser extends OpCodeParser<Print> {
        @Override
        public Print parse() {
            Print p = new Print();

            p.setActor("Guybrush");

            while ((opcode = readValue8()) != (byte)0xFF) {
                // Text Pos
                if (opcode == 0) {
                    int x = getVarOrDirectWord(PARAM_1);
                    int y = getVarOrDirectWord(PARAM_2);
                } else if (opcode == 1) {
                    int color = getVarOrDirectByte(PARAM_1);
                } else if (opcode == 2) {
                    int right = getVarOrDirectWord(PARAM_1);
                } else if (opcode == 3) {
                    int width = getVarOrDirectWord(PARAM_1);
                    int height = getVarOrDirectWord(PARAM_2);
                } else if (opcode == 4) {
                } else if (opcode == 6) {
                } else if (opcode == 7) {
                } else if (opcode == 8) {
                    int offset = getVarOrDirectWord(PARAM_1);
                    int delay = getVarOrDirectWord(PARAM_2);
                } else if (opcode == 15) {
                    String s = loadPtrToResource(resStrLen());
                    p.setText(s);
                    return p;
                }
            }

            return p;
        }
    }

    private class PseudoRoomParser extends OpCodeParser<PseudoRoom> {
        @Override
        public PseudoRoom parse() {
            while (readValue8() != (byte) 0x00) {
            }

            return new PseudoRoom();
        }
    }

    private class PutActorParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            getVarOrDirectByte(PARAM_1);
            getVarOrDirectWord(PARAM_2);
            getVarOrDirectWord(PARAM_3); // TODO: Check
            return new PutActor();
        }
    }

    private class PutActorAtObjectParser extends OpCodeParser<PutActorAtObject> {
        @Override
        public PutActorAtObject parse() {
            getVarOrDirectByte(PARAM_1);
            getVarOrDirectWord(PARAM_2);

            return new PutActorAtObject();
        }
    }

    private class PutActorInRoomParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            getVarOrDirectByte(PARAM_1);
            getVarOrDirectByte(PARAM_2);
            return new PutActorInRoom();
        }
    }

    // 0x80
    private class BreakHereParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            return new BreakHere();
        }
    }

    private class ResourceRoutinesParser extends OpCodeParser<ResourceRoutines> {
        @Override
        public ResourceRoutines parse() {
            opcode = readValue8();
            if (opcode != 17) {
                getVarOrDirectByte(PARAM_1);
            }

            int op = opcode & 0x3F;

            switch (op) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 15:
                case 16:
                case 18:
                case 19:
                    break;
                case 20:
                    getVarOrDirectByte(PARAM_2);
                    break;
                case 36:
                    getVarOrDirectByte(PARAM_2);
                    readValue8();
                    break;
                case 37:
                    getVarOrDirectByte(PARAM_2);
                    break;
            }

            return new ResourceRoutines();
        }
    }

    // 0x33
    private class RoomOpsParser extends OpCodeParser<OpCode> {
        @Override
        public OpCode parse() {
            opcode = buffer.get();

            switch (opcode & (byte)0x1F) {
                case 1:
                    getVarOrDirectWord(PARAM_1);
                    getVarOrDirectWord(PARAM_2);
                    break;
                case 2:
                    break;
                case 3:
                    getVarOrDirectWord(PARAM_1);
                    getVarOrDirectWord(PARAM_2);
                    break;
                case 4:
                    getVarOrDirectWord(PARAM_1);
                    getVarOrDirectWord(PARAM_2);
                    getVarOrDirectWord(PARAM_3);
                    readValue8();
                    getVarOrDirectByte(PARAM_1);
                    break;
                case 5:
                case 6:
                    // NOP
                    break;
                case 7:
                    getVarOrDirectByte(PARAM_1);
                    getVarOrDirectByte(PARAM_2);
                    readValue8();
                    getVarOrDirectByte(PARAM_1);
                    getVarOrDirectByte(PARAM_2);
                    readValue8();
                    getVarOrDirectByte(PARAM_1);
                    break;
                case 8:
                    getVarOrDirectByte(PARAM_1);
                    getVarOrDirectByte(PARAM_2);
                    getVarOrDirectByte(PARAM_3);
                    break;
                case 9:
                    getVarOrDirectByte(PARAM_1);
                    getVarOrDirectByte(PARAM_2);
                    break;
                case 10:
                    getVarOrDirectWord(PARAM_1);
                    break;
                case 11:
                    getVarOrDirectWord(PARAM_1);
                    getVarOrDirectWord(PARAM_2);
                    getVarOrDirectWord(PARAM_3);
                    readValue8();
                    getVarOrDirectByte(PARAM_1);
                    getVarOrDirectByte(PARAM_2);
                    break;
                case 12:
                    getVarOrDirectWord(PARAM_1);
                    getVarOrDirectWord(PARAM_2);
                    getVarOrDirectWord(PARAM_3);
                    readValue8();
                    getVarOrDirectByte(PARAM_1);
                    getVarOrDirectByte(PARAM_2);
                    break;
                case 13:
                    getVarOrDirectByte(PARAM_1);
                    loadPtrToResource(resStrLen());
                case 14:
                    getVarOrDirectByte(PARAM_1);
                    loadPtrToResource(resStrLen());
                    break;
                case 15:
                    getVarOrDirectByte(PARAM_1);
                    readValue8();
                    getVarOrDirectByte(PARAM_1);
                    getVarOrDirectByte(PARAM_2);
                    readValue8();
                    getVarOrDirectByte(PARAM_1);
                    break;
                case 16:
                    getVarOrDirectByte(PARAM_1);
                    getVarOrDirectByte(PARAM_2);
                    break;
            }

            return new RoomOps();
        }
    }

    private class SaveLoadGameParser extends OpCodeParser<OpCode> {
    }

    private class SaveLoadVarsParser extends OpCodeParser<OpCode> {
    }

    private class SaveRestoreVerbsParser extends OpCodeParser<SaveRestoreVerbs> {
        @Override
        public SaveRestoreVerbs parse() {
            opcode = readValue8();

            getVarOrDirectByte(PARAM_1);
            getVarOrDirectByte(PARAM_2);
            getVarOrDirectByte(PARAM_3);

            switch(opcode) {
                case 1:
                case 2:
                case 3:
                    break;
            }

            return new SaveRestoreVerbs();
        }
    }

    private class SetCameraAtParser extends OpCodeParser<SetCameraAt> {
        @Override
        public SetCameraAt parse() {
            getVarOrDirectWord(PARAM_1);

            return new SetCameraAt();
        }
    }

    private class SetObjectNameParser extends OpCodeParser<SetObjectName> {
        @Override
        public SetObjectName parse() {
            getVarOrDirectWord(PARAM_1);
            loadPtrToResource(resStrLen());
            return new SetObjectName();
        }
    }

    // 0x29
    private class SetOwnerOfParser extends OpCodeParser<SetOwnerOf> {
        @Override
        public SetOwnerOf parse() {
            getVarOrDirectWord(PARAM_1);
            getVarOrDirectByte(PARAM_2);

            return new SetOwnerOf();
        }
    }

    private class SetStateParser extends OpCodeParser<SetState> {
        @Override
        public SetState parse() {
            getVarOrDirectWord(PARAM_1);
            getVarOrDirectByte(PARAM_2);
            return new SetState();
        }
    }

    private class SetVarRangeParser extends OpCodeParser<SetVarRange> {
        @Override
        public SetVarRange parse() {
            getResultPos();
            byte numValues = readValue8();

            for (int i = 0; i < numValues; i++) {
                getVarOrDirectByte(PARAM_1);
            }

            return new SetVarRange();
        }
    }

    private class ChainScriptParser extends OpCodeParser<ChainScript> {
        @Override
        public ChainScript parse() {
            getVarOrDirectByte(PARAM_1);
            getWordVararg();

            return new ChainScript();
        }
    }

    private class SoundKludgeParser extends OpCodeParser<SoundKludge> {
        @Override
        public SoundKludge parse() {
            getWordVararg();
            return new SoundKludge();
        }
    }

    private class StartMusicParser extends OpCodeParser<StartMusic> {
        @Override
        public StartMusic parse() {
            getVarOrDirectByte(PARAM_1);
            return new StartMusic();
        }
    }

    private class StartObjectParser extends OpCodeParser<StartObject> {
        @Override
        public StartObject parse() {
            getVarOrDirectWord(PARAM_1);
            getVarOrDirectByte(PARAM_2);
            getWordVararg();
            return new StartObject();
        }
    }

    // 0x0A, 0x2A, 0x4A, 0x6A
    private class StartScriptParser extends OpCodeParser<StartScript> {
        @Override
        public StartScript parse() {
            getVarOrDirectByte(PARAM_1);
            getWordVararg();
            return new StartScript();
        }
    }

    // 0x1C
    private class StartSoundParser extends OpCodeParser<StartSound> {
        @Override
        public StartSound parse() {
            getVarOrDirectByte(PARAM_1);
            return new StartSound();
        }
    }

    // 0x20
    private class StopMusicParser extends OpCodeParser<StopMusic> {
        @Override
        public StopMusic parse() {
            // NOP
            return new StopMusic();
        }
    }

    // 0xA0
    private class StopObjectCodeParser extends OpCodeParser<StopObjectCode> {
        @Override
        public StopObjectCode parse() {
            // NOP
            return new StopObjectCode();
        }
    }

    private class StopObjectScriptParser extends OpCodeParser<StopObjectScript> {
        @Override
        public StopObjectScript parse() {
            getVarOrDirectByte(PARAM_1);
            return new StopObjectScript();
        }
    }

    private class StopScriptParser extends OpCodeParser<StopScript> {
        @Override
        public StopScript parse() {
            getVarOrDirectByte(PARAM_1);
            return new StopScript();
        }
    }

    private class StopSoundParser extends OpCodeParser<StopSound> {
        @Override
        public StopSound parse() {
            getVarOrDirectByte(PARAM_1);
            return new StopSound();
        }
    }
}
