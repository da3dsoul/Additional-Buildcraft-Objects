package da3dsoul.asm;

import org.objectweb.asm.tree.*;
import java.util.*;

public class InsnComparator implements Comparator<AbstractInsnNode>
{
    private static final String WILDCARD_STRING = "HilburnIsAwesome";
    private static final int WILDCARD_INT = -42;
    private static final Object[] WILDCARD_ARRAY;
    
    @Override
    public int compare(final AbstractInsnNode a, final AbstractInsnNode b) {
        return areNodesEqual(a, b) ? 0 : 1;
    }
    
    public static boolean areNodesEqual(final AbstractInsnNode a, final AbstractInsnNode b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.equals(b)) {
            return true;
        }
        if (a.getType() == b.getType() && a.getOpcode() == b.getOpcode()) {
            switch (a.getType()) {
                case 4: {
                    return areFieldInsnNodesEqual((FieldInsnNode)a, (FieldInsnNode)b);
                }
                case 10: {
                    return areIincInsnNodesEqual((IincInsnNode)a, (IincInsnNode)b);
                }
                case 1: {
                    return areIntInsnNodesEqual((IntInsnNode)a, (IntInsnNode)b);
                }
                case 6: {
                    return areInvokeDynamicInsnNodesEqual((InvokeDynamicInsnNode)a, (InvokeDynamicInsnNode)b);
                }
                case 9: {
                    return areLdcInsnNodesEqual((LdcInsnNode)a, (LdcInsnNode)b);
                }
                case 5: {
                    return areMethodInsnNodesEqual((MethodInsnNode)a, (MethodInsnNode)b);
                }
                case 13: {
                    return areMultiANewArrayInsnNodesEqual((MultiANewArrayInsnNode)a, (MultiANewArrayInsnNode)b);
                }
                case 3: {
                    return areTypeInsnNodesEqual((TypeInsnNode)a, (TypeInsnNode)b);
                }
                case 2: {
                    return areVarInsnNodesEqual((VarInsnNode)a, (VarInsnNode)b);
                }
            }
        }
        return false;
    }
    
    private static boolean areFieldInsnNodesEqual(final FieldInsnNode a, final FieldInsnNode b) {
        return objectMatch(a.name, b.name) && objectMatch(a.desc, b.desc);
    }
    
    private static boolean areIincInsnNodesEqual(final IincInsnNode a, final IincInsnNode b) {
        return intMatch(a.incr, b.incr) && intMatch(a.var, b.var);
    }
    
    private static boolean areIntInsnNodesEqual(final IntInsnNode a, final IntInsnNode b) {
        return intMatch(a.operand, b.operand);
    }
    
    private static boolean areInvokeDynamicInsnNodesEqual(final InvokeDynamicInsnNode a, final InvokeDynamicInsnNode b) {
        return objectMatch(a.name, b.name) && objectMatch(a.desc, b.desc) && arrayMatch(a.bsmArgs, b.bsmArgs);
    }
    
    private static boolean areLdcInsnNodesEqual(final LdcInsnNode a, final LdcInsnNode b) {
        return objectMatch(a.cst, b.cst);
    }
    
    private static boolean areMethodInsnNodesEqual(final MethodInsnNode a, final MethodInsnNode b) {
        return objectMatch(a.name, b.name) && a.itf == b.itf && objectMatch(a.desc, b.desc);
    }
    
    private static boolean areMultiANewArrayInsnNodesEqual(final MultiANewArrayInsnNode a, final MultiANewArrayInsnNode b) {
        return intMatch(a.dims, b.dims) && objectMatch(a.desc, b.desc);
    }
    
    private static boolean areTypeInsnNodesEqual(final TypeInsnNode a, final TypeInsnNode b) {
        return objectMatch(a.desc, b.desc);
    }
    
    private static boolean areVarInsnNodesEqual(final VarInsnNode a, final VarInsnNode b) {
        return intMatch(a.var, b.var);
    }
    
    private static boolean intMatch(final int a, final int b) {
        return a == b || a == -42 || b == -42;
    }
    
    private static boolean objectMatch(final Object a, final Object b) {
        return a.equals(b) || a.equals("HilburnIsAwesome") || b.equals("HilburnIsAwesome");
    }
    
    private static boolean arrayMatch(final Object[] a, final Object[] b) {
        return Arrays.deepEquals(a, b) || Arrays.deepEquals(a, InsnComparator.WILDCARD_ARRAY) || Arrays.deepEquals(b, InsnComparator.WILDCARD_ARRAY);
    }
    
    static {
        WILDCARD_ARRAY = new Object[] { "HilburnIsAwesome", -42 };
    }
}
