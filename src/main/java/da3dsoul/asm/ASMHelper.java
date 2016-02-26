package da3dsoul.asm;

import org.objectweb.asm.*;
import java.util.*;
import org.objectweb.asm.tree.*;

public class ASMHelper
{
    private static InsnComparator insnComparator;
    
    public static ClassNode readClassFromBytes(final byte[] bytes) {
        final ClassNode classNode = new ClassNode();
        final ClassReader classReader = new ClassReader(bytes);
        classReader.accept((ClassVisitor)classNode, 0);
        return classNode;
    }
    
    public static byte[] writeClassToBytes(final ClassNode classNode) {
        final ClassWriter writer = new ClassWriter(3);
        classNode.accept((ClassVisitor)writer);
        return writer.toByteArray();
    }
    
    public static byte[] writeClassToBytesNoDeobfSkipFrames(final ClassNode classNode) {
        final ClassWriter writer = new ClassWriter(1);
        classNode.accept((ClassVisitor)writer);
        return writer.toByteArray();
    }
    
    public static boolean isLabelOrLineNumber(final AbstractInsnNode insn) {
        return insn.getType() == 8 || insn.getType() == 15;
    }
    
    public static AbstractInsnNode getOrFindInstructionOfType(final AbstractInsnNode firstInsnToCheck, final int type) {
        return getOrFindInstructionWithOpcode(firstInsnToCheck, type, false);
    }
    
    public static AbstractInsnNode getOrFindInstructionOfType(final AbstractInsnNode firstInsnToCheck, final int type, final boolean reverseDirection) {
        for (AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = (reverseDirection ? instruction.getPrevious() : instruction.getNext())) {
            if (instruction.getType() == type) {
                return instruction;
            }
        }
        return null;
    }
    
    public static AbstractInsnNode getOrFindInstructionWithOpcode(final AbstractInsnNode firstInsnToCheck, final int opcode) {
        return getOrFindInstructionWithOpcode(firstInsnToCheck, opcode, false);
    }
    
    public static AbstractInsnNode getOrFindInstructionWithOpcode(final AbstractInsnNode firstInsnToCheck, final int opcode, final boolean reverseDirection) {
        for (AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = (reverseDirection ? instruction.getPrevious() : instruction.getNext())) {
            if (instruction.getOpcode() == opcode) {
                return instruction;
            }
        }
        return null;
    }
    
    public static AbstractInsnNode getOrFindLabelOrLineNumber(final AbstractInsnNode firstInsnToCheck) {
        return getOrFindInstruction(firstInsnToCheck, false);
    }
    
    public static AbstractInsnNode getOrFindLabelOrLineNumber(final AbstractInsnNode firstInsnToCheck, final boolean reverseDirection) {
        for (AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = (reverseDirection ? instruction.getPrevious() : instruction.getNext())) {
            if (isLabelOrLineNumber(instruction)) {
                return instruction;
            }
        }
        return null;
    }
    
    public static AbstractInsnNode getOrFindInstruction(final AbstractInsnNode firstInsnToCheck) {
        return getOrFindInstruction(firstInsnToCheck, false);
    }
    
    public static AbstractInsnNode getOrFindInstruction(final AbstractInsnNode firstInsnToCheck, final boolean reverseDirection) {
        for (AbstractInsnNode instruction = firstInsnToCheck; instruction != null; instruction = (reverseDirection ? instruction.getPrevious() : instruction.getNext())) {
            if (!isLabelOrLineNumber(instruction)) {
                return instruction;
            }
        }
        return null;
    }
    
    public static AbstractInsnNode findFirstInstruction(final MethodNode method) {
        return getOrFindInstruction(method.instructions.getFirst());
    }
    
    public static AbstractInsnNode findFirstInstructionWithOpcode(final MethodNode method, final int opcode) {
        return getOrFindInstructionWithOpcode(method.instructions.getFirst(), opcode);
    }
    
    public static AbstractInsnNode findLastInstructionWithOpcode(final MethodNode method, final int opcode) {
        return getOrFindInstructionWithOpcode(method.instructions.getLast(), opcode, true);
    }
    
    public static AbstractInsnNode findNextInstruction(final AbstractInsnNode instruction) {
        return getOrFindInstruction(instruction.getNext());
    }
    
    public static AbstractInsnNode findNextInstructionWithOpcode(final AbstractInsnNode instruction, final int opcode) {
        return getOrFindInstructionWithOpcode(instruction.getNext(), opcode);
    }
    
    public static AbstractInsnNode findNextLabelOrLineNumber(final AbstractInsnNode instruction) {
        return getOrFindLabelOrLineNumber(instruction.getNext());
    }
    
    public static AbstractInsnNode findPreviousInstruction(final AbstractInsnNode instruction) {
        return getOrFindInstruction(instruction.getPrevious(), true);
    }
    
    public static AbstractInsnNode findPreviousInstructionWithOpcode(final AbstractInsnNode instruction, final int opcode) {
        return getOrFindInstructionWithOpcode(instruction.getPrevious(), opcode, true);
    }
    
    public static AbstractInsnNode findPreviousLabelOrLineNumber(final AbstractInsnNode instruction) {
        return getOrFindLabelOrLineNumber(instruction.getPrevious(), true);
    }
    
    public static MethodNode getMethodByName(final ClassNode classNode, final String name, final String args) {
        for (final MethodNode method : classNode.methods) {
            if (method.name.equals(name) && method.desc.equals(args)) {
                return method;
            }
        }
        return null;
    }
    
    public static LabelNode findEndLabel(final MethodNode method) {
        for (AbstractInsnNode instruction = method.instructions.getLast(); instruction != null; instruction = instruction.getPrevious()) {
            if (instruction instanceof LabelNode) {
                return (LabelNode)instruction;
            }
        }
        return null;
    }
    
    public static int removeFromInsnListUntil(final InsnList insnList, final AbstractInsnNode startInclusive, final AbstractInsnNode endNotInclusive) {
        AbstractInsnNode insnToRemove = startInclusive;
        int numDeleted = 0;
        while (insnToRemove != null && insnToRemove != endNotInclusive) {
            ++numDeleted;
            insnToRemove = insnToRemove.getNext();
            insnList.remove(insnToRemove.getPrevious());
        }
        return numDeleted;
    }
    
    public static AbstractInsnNode move(final AbstractInsnNode start, final int distance) {
        AbstractInsnNode movedTo = start;
        for (int i = 0; i < Math.abs(distance) && movedTo != null; movedTo = ((distance > 0) ? movedTo.getNext() : movedTo.getPrevious()), ++i) {}
        return movedTo;
    }
    
    public static boolean instructionsMatch(final AbstractInsnNode first, final AbstractInsnNode second) {
        final InsnComparator insnComparator = ASMHelper.insnComparator;
        return InsnComparator.areNodesEqual(first, second);
    }
    
    public static boolean patternMatches(final InsnList checkFor, AbstractInsnNode checkAgainst) {
        AbstractInsnNode instruction = checkFor.getFirst();
        while (instruction != null) {
            if (checkAgainst == null) {
                return false;
            }
            if (isLabelOrLineNumber(instruction)) {
                instruction = instruction.getNext();
            }
            else if (isLabelOrLineNumber(checkAgainst)) {
                checkAgainst = checkAgainst.getNext();
            }
            else {
                if (!instructionsMatch(instruction, checkAgainst)) {
                    return false;
                }
                instruction = instruction.getNext();
                checkAgainst = checkAgainst.getNext();
            }
        }
        return true;
    }
    
    public static AbstractInsnNode find(final InsnList haystack, final InsnList needle) {
        return find(haystack.getFirst(), needle);
    }
    
    public static AbstractInsnNode find(final AbstractInsnNode haystackStart, final InsnList needle) {
        if (needle.getFirst() == null) {
            return null;
        }
        final int needleStartOpcode = needle.getFirst().getOpcode();
        for (AbstractInsnNode checkAgainstStart = getOrFindInstructionWithOpcode(haystackStart, needleStartOpcode); checkAgainstStart != null; checkAgainstStart = findNextInstructionWithOpcode(checkAgainstStart, needleStartOpcode)) {
            if (patternMatches(needle, checkAgainstStart)) {
                return checkAgainstStart;
            }
        }
        return null;
    }
    
    public static AbstractInsnNode find(final InsnList haystack, final AbstractInsnNode needle) {
        return find(haystack.getFirst(), needle);
    }
    
    public static AbstractInsnNode find(final AbstractInsnNode haystackStart, final AbstractInsnNode needle) {
        final InsnList insnList = new InsnList();
        insnList.add(needle);
        return find(haystackStart, insnList);
    }
    
    public static AbstractInsnNode findAndReplace(final InsnList haystack, final InsnList needle, final InsnList replacement) {
        return findAndReplace(haystack, needle, replacement, haystack.getFirst());
    }
    
    public static AbstractInsnNode findAndReplace(final InsnList haystack, final InsnList needle, final InsnList replacement, final AbstractInsnNode haystackStart) {
        final AbstractInsnNode foundStart = find(haystackStart, needle);
        if (foundStart != null) {
            haystack.insertBefore(foundStart, cloneInsnList(replacement));
            final AbstractInsnNode afterNeedle = move(foundStart, needle.size());
            removeFromInsnListUntil(haystack, foundStart, afterNeedle);
            return afterNeedle;
        }
        return null;
    }
    
    public static int findAndReplaceAll(final InsnList haystack, final InsnList needle, final InsnList replacement) {
        return findAndReplaceAll(haystack, needle, replacement, haystack.getFirst());
    }
    
    public static int findAndReplaceAll(final InsnList haystack, final InsnList needle, final InsnList replacement, AbstractInsnNode haystackStart) {
        int numReplaced = 0;
        while ((haystackStart = findAndReplace(haystack, needle, replacement, haystackStart)) != null) {
            ++numReplaced;
        }
        return numReplaced;
    }
    
    public static InsnList cloneInsnList(final InsnList source) {
        final InsnList clone = new InsnList();
        final Map<LabelNode, LabelNode> labelMap = new HashMap<LabelNode, LabelNode>();
        for (AbstractInsnNode instruction = source.getFirst(); instruction != null; instruction = instruction.getNext()) {
            if (instruction instanceof LabelNode) {
                labelMap.put((LabelNode)instruction, new LabelNode());
            }
        }
        for (AbstractInsnNode instruction = source.getFirst(); instruction != null; instruction = instruction.getNext()) {
            clone.add(instruction.clone((Map)labelMap));
        }
        return clone;
    }
    
    public static LocalVariableNode findLocalVariableOfMethod(final MethodNode method, final String varName, final String varDesc) {
        for (final LocalVariableNode localVar : method.localVariables) {
            if (localVar.name.equals(varName) && localVar.desc.equals(varDesc)) {
                return localVar;
            }
        }
        return null;
    }
    
    static {
        ASMHelper.insnComparator = new InsnComparator();
    }
}
