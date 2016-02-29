package da3dsoul.asm;

import da3dsoul.asm.obfuscation.ASMString;
import net.minecraft.launchwrapper.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.util.*;

public class da3dsoulClassTransformer implements IClassTransformer, Opcodes {
    private static ASMString Frustrum;
    private static ASMString PathFinding;
    private static Transformer.MethodTransformer isBoxInFrustrumFullyTransformer;
    private static Transformer.MethodTransformer isBoundingBoxInFrustrumFullyTransformer;
    private static Transformer.MethodTransformer pathfinderTransformerAdd;
    private static Transformer.MethodTransformer pathfinderTransformerDelete;
    private static Map<String, Transformer.ClassTransformer> classMap;

    public byte[] transform(final String className, final String className2, byte[] bytes) {
        final Transformer.ClassTransformer clazz = da3dsoulClassTransformer.classMap.get(className);
        if (clazz != null) {
            bytes = clazz.transform(bytes);
            da3dsoulClassTransformer.classMap.remove(className);
        }
        return bytes;
    }

    static {
        da3dsoulClassTransformer.Frustrum = new ASMString("us.ichun.mods.doors.client.render.culling.Frustrum");
        da3dsoulClassTransformer.PathFinding = new ASMString.ASMObfString("net/minecraft/pathfinding/PathFinder","ayg");


        da3dsoulClassTransformer.isBoxInFrustrumFullyTransformer = new Transformer.MethodTransformer(Transformer.NODE_ADD, new ASMString("<cinit>"), "()V") {
            @Override
            protected MethodNode getNodeToAdd() {
                MethodNode mv;
                mv = new MethodNode(ACC_PUBLIC, "isBoxInFrustumFully", "(DDDDDD)Z", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitLineNumber(39, l0);
                /*mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "us/ichun/mods/doors/client/render/culling/Frustrum", "clippingHelper", "Lnet/minecraft/client/renderer/culling/ClippingHelper;");
                mv.visitVarInsn(DLOAD, 1);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "us/ichun/mods/doors/client/render/culling/Frustrum", "xPosition", "D");
                mv.visitInsn(DSUB);
                mv.visitVarInsn(DLOAD, 3);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "us/ichun/mods/doors/client/render/culling/Frustrum", "yPosition", "D");
                mv.visitInsn(DSUB);
                mv.visitVarInsn(DLOAD, 5);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "us/ichun/mods/doors/client/render/culling/Frustrum", "zPosition", "D");
                mv.visitInsn(DSUB);
                mv.visitVarInsn(DLOAD, 7);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "us/ichun/mods/doors/client/render/culling/Frustrum", "xPosition", "D");
                mv.visitInsn(DSUB);
                mv.visitVarInsn(DLOAD, 9);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "us/ichun/mods/doors/client/render/culling/Frustrum", "yPosition", "D");
                mv.visitInsn(DSUB);
                mv.visitVarInsn(DLOAD, 11);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "us/ichun/mods/doors/client/render/culling/Frustrum", "zPosition", "D");
                mv.visitInsn(DSUB);
                mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/client/renderer/culling/ClippingHelper", "isBoxInFrustum", "(DDDDDD)Z", false);
                mv.visitInsn(IRETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", "Lus/ichun/mods/doors/client/render/culling/Frustrum;", null, l0, l1, 0);
                mv.visitLocalVariable("minX", "D", null, l0, l1, 1);
                mv.visitLocalVariable("minY", "D", null, l0, l1, 3);
                mv.visitLocalVariable("minZ", "D", null, l0, l1, 5);
                mv.visitLocalVariable("maxX", "D", null, l0, l1, 7);
                mv.visitLocalVariable("maxY", "D", null, l0, l1, 9);
                mv.visitLocalVariable("maxZ", "D", null, l0, l1, 11);
                mv.visitMaxs(15, 13);*/
                mv.visitInsn(ICONST_1);
                mv.visitInsn(IRETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", "Lus/ichun/mods/doors/client/render/culling/Frustrum;", null, l0, l1, 0);
                mv.visitLocalVariable("minX", "D", null, l0, l1, 1);
                mv.visitLocalVariable("minY", "D", null, l0, l1, 3);
                mv.visitLocalVariable("minZ", "D", null, l0, l1, 5);
                mv.visitLocalVariable("maxX", "D", null, l0, l1, 7);
                mv.visitLocalVariable("maxY", "D", null, l0, l1, 9);
                mv.visitLocalVariable("maxZ", "D", null, l0, l1, 11);
                mv.visitMaxs(1, 13);
                mv.visitEnd();
                return mv;
            }
        };

        da3dsoulClassTransformer.pathfinderTransformerDelete = new Transformer.MethodTransformer(Transformer.NODE_DELETE, new ASMString.ASMObfString("func_82565_a", "a"), new ASMString.ASMObfString("(Lnet/minecraft/entity/Entity;IIILnet/minecraft/pathfinding/PathPoint;ZZZ)I", "(Lsa;IIILaye;ZZZ)I").getText());

        da3dsoulClassTransformer.pathfinderTransformerAdd = new Transformer.MethodTransformer(Transformer.NODE_ADD, new ASMString.ASMObfString("func_82565_a", "a"), new ASMString.ASMObfString("(Lnet/minecraft/entity/Entity;IIILnet/minecraft/pathfinding/PathPoint;ZZZ)I", "(Lsa;IIILaye;ZZZ)I").getText()) {
            @Override
            protected MethodNode getNodeToAdd() {
                MethodNode node = new MethodNode(ACC_PUBLIC + ACC_STATIC, new ASMString.ASMObfString("func_82565_a", "a").getText(), new ASMString.ASMObfString("(Lnet/minecraft/entity/Entity;IIILnet/minecraft/pathfinding/PathPoint;ZZZ)I", "(Lsa;IIILaye;ZZZ)I").getText(), null, null);
                String pathPoint = new ASMString.ASMObfString("net/minecraft/pathfinding/PathPoint", "aye").getText();
                String block = new ASMString.ASMObfString("net/minecraft/block/Block", "aji").getText();
                String blockType = new ASMString.ASMObfString("Lnet/minecraft/Block/Block;", "Laji;").getText();
                String initBlocks = new ASMString.ASMObfString("net/minecraft/init/Blocks", "ajn").getText();
                ASMString.ASMObfString entity = new ASMString.ASMObfString("net/minecraft/entity/Entity", "sa");
                ASMString.ASMObfString world = new ASMString.ASMObfString("net/minecraft/world/World", "ahb");
                ASMString.ASMObfString material = new ASMString.ASMObfString("net/minecraft/block/material/Material", "awt");
                String worldObj = new ASMString.ASMObfString("worldObj", "o").getText();
                String getBlock = new ASMString.ASMObfString("getBlock", "a").getText();
                String getRenderType = new ASMString.ASMObfString("getRenderType", "b").getText();
                String mathHelper = new ASMString.ASMObfString("net/minecraft/util/MathHelper", "qh").getText();

                node.visitCode();
                Label l0 = new Label();
                node.visitLabel(l0);
                node.visitLineNumber(291, l0);
                node.visitInsn(ICONST_0);
                node.visitVarInsn(ISTORE, 8);
                Label l1 = new Label();
                node.visitLabel(l1);
                node.visitLineNumber(293, l1);
                node.visitVarInsn(ILOAD, 1);
                node.visitVarInsn(ISTORE, 9);
                Label l2 = new Label();
                node.visitLabel(l2);
                node.visitFrame(Opcodes.F_APPEND, 2, new Object[]{Opcodes.INTEGER, Opcodes.INTEGER}, 0, null);
                node.visitVarInsn(ILOAD, 9);
                node.visitVarInsn(ILOAD, 1);
                node.visitVarInsn(ALOAD, 4);
                node.visitFieldInsn(GETFIELD, pathPoint, new ASMString.ASMObfString("xCoord", "a").getText(), "I");
                node.visitInsn(IADD);
                Label l3 = new Label();
                node.visitJumpInsn(IF_ICMPGE, l3);
                Label l4 = new Label();
                node.visitLabel(l4);
                node.visitLineNumber(295, l4);
                node.visitVarInsn(ILOAD, 2);
                node.visitVarInsn(ISTORE, 10);
                Label l5 = new Label();
                node.visitLabel(l5);
                node.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER}, 0, null);
                node.visitVarInsn(ILOAD, 10);
                node.visitVarInsn(ILOAD, 2);
                node.visitVarInsn(ALOAD, 4);
                node.visitFieldInsn(GETFIELD, pathPoint, new ASMString.ASMObfString("yCoord", "b").getText(), "I");
                node.visitInsn(IADD);
                Label l6 = new Label();
                node.visitJumpInsn(IF_ICMPGE, l6);
                Label l7 = new Label();
                node.visitLabel(l7);
                node.visitLineNumber(297, l7);
                node.visitVarInsn(ILOAD, 3);
                node.visitVarInsn(ISTORE, 11);
                Label l8 = new Label();
                node.visitLabel(l8);
                node.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER}, 0, null);
                node.visitVarInsn(ILOAD, 11);
                node.visitVarInsn(ILOAD, 3);
                node.visitVarInsn(ALOAD, 4);
                node.visitFieldInsn(GETFIELD, pathPoint, new ASMString.ASMObfString("zCoord", "c").getText(), "I");
                node.visitInsn(IADD);
                Label l9 = new Label();
                node.visitJumpInsn(IF_ICMPGE, l9);
                Label l10 = new Label();
                node.visitLabel(l10);
                node.visitLineNumber(299, l10);
                node.visitVarInsn(ALOAD, 0);
                node.visitFieldInsn(GETFIELD, entity.getText(), worldObj, world.getObfASMTypeName());
                node.visitVarInsn(ILOAD, 9);
                node.visitVarInsn(ILOAD, 10);
                node.visitVarInsn(ILOAD, 11);
                node.visitMethodInsn(INVOKEVIRTUAL, world.getText(), getBlock, "(III)"+blockType, false);
                node.visitVarInsn(ASTORE, 12);
                Label l11 = new Label();
                node.visitLabel(l11);
                node.visitLineNumber(301, l11);
                node.visitVarInsn(ALOAD, 12);
                node.visitMethodInsn(INVOKEVIRTUAL, block, new ASMString.ASMObfString("getMaterial", "o").getText(), "()" + material.getObfASMTypeName(), false);
                node.visitFieldInsn(GETSTATIC, material.getText(), new ASMString.ASMObfString("air", "a").getText(), material.getObfASMTypeName());
                Label l12 = new Label();
                node.visitJumpInsn(IF_ACMPEQ, l12);
                Label l13 = new Label();
                node.visitLabel(l13);
                node.visitLineNumber(303, l13);
                node.visitVarInsn(ALOAD, 12);
                node.visitFieldInsn(GETSTATIC, initBlocks, new ASMString.ASMObfString("trapdoor", "aT").getText(), blockType);
                Label l14 = new Label();
                node.visitJumpInsn(IF_ACMPNE, l14);
                Label l15 = new Label();
                node.visitLabel(l15);
                node.visitLineNumber(305, l15);
                node.visitInsn(ICONST_1);
                node.visitVarInsn(ISTORE, 8);
                Label l16 = new Label();
                node.visitJumpInsn(GOTO, l16);
                node.visitLabel(l14);
                node.visitLineNumber(307, l14);
                node.visitFrame(Opcodes.F_APPEND, 1, new Object[]{block}, 0, null);
                node.visitVarInsn(ALOAD, 12);
                node.visitFieldInsn(GETSTATIC, initBlocks, new ASMString.ASMObfString("flowing_water", "i").getText(), "L"+new ASMString.ASMObfString("net/minecraft/block/BlockLiquid","alw").getText() + ";");
                Label l17 = new Label();
                node.visitJumpInsn(IF_ACMPEQ, l17);
                node.visitVarInsn(ALOAD, 12);
                node.visitFieldInsn(GETSTATIC, initBlocks, new ASMString.ASMObfString("water", "j").getText(), blockType);
                node.visitJumpInsn(IF_ACMPEQ, l17);
                Label l18 = new Label();
                node.visitLabel(l18);
                node.visitLineNumber(309, l18);
                node.visitVarInsn(ILOAD, 7);
                node.visitJumpInsn(IFNE, l16);
                node.visitVarInsn(ALOAD, 12);
                node.visitFieldInsn(GETSTATIC, initBlocks, new ASMString.ASMObfString("wooden_door", "aq").getText(), blockType);
                node.visitJumpInsn(IF_ACMPNE, l16);
                Label l19 = new Label();
                node.visitLabel(l19);
                node.visitLineNumber(311, l19);
                node.visitInsn(ICONST_0);
                node.visitInsn(IRETURN);
                node.visitLabel(l17);
                node.visitLineNumber(316, l17);
                node.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                node.visitVarInsn(ILOAD, 5);
                Label l20 = new Label();
                node.visitJumpInsn(IFEQ, l20);
                Label l21 = new Label();
                node.visitLabel(l21);
                node.visitLineNumber(318, l21);
                node.visitInsn(ICONST_M1);
                node.visitInsn(IRETURN);
                node.visitLabel(l20);
                node.visitLineNumber(321, l20);
                node.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                node.visitInsn(ICONST_1);
                node.visitVarInsn(ISTORE, 8);
                node.visitLabel(l16);
                node.visitLineNumber(324, l16);
                node.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                node.visitVarInsn(ALOAD, 12);
                node.visitMethodInsn(INVOKEVIRTUAL, block, getRenderType, "()I", false);
                node.visitVarInsn(ISTORE, 13);
                Label l22 = new Label();
                node.visitLabel(l22);
                node.visitLineNumber(326, l22);
                node.visitVarInsn(ALOAD, 0);
                node.visitFieldInsn(GETFIELD, entity.getText(), worldObj, world.getObfASMTypeName());
                node.visitVarInsn(ILOAD, 9);
                node.visitVarInsn(ILOAD, 10);
                node.visitVarInsn(ILOAD, 11);
                node.visitMethodInsn(INVOKEVIRTUAL, world.getText(), getBlock, "(III)"+blockType, false);
                node.visitMethodInsn(INVOKEVIRTUAL, block, getRenderType, "()I", false);
                node.visitIntInsn(BIPUSH, 9);
                Label l23 = new Label();
                node.visitJumpInsn(IF_ICMPNE, l23);
                Label l24 = new Label();
                node.visitLabel(l24);
                node.visitLineNumber(328, l24);
                node.visitVarInsn(ALOAD, 0);
                node.visitFieldInsn(GETFIELD, entity.getText(), new ASMString.ASMObfString("posX", "s").getText(), "D");
                node.visitMethodInsn(INVOKESTATIC, mathHelper, new ASMString.ASMObfString("floor_double", "c").getText(), "(D)I", false);
                node.visitVarInsn(ISTORE, 14);
                Label l25 = new Label();
                node.visitLabel(l25);
                node.visitLineNumber(329, l25);
                node.visitVarInsn(ALOAD, 0);
                node.visitFieldInsn(GETFIELD, entity.getText(), new ASMString.ASMObfString("posY", "t").getText(), "D");
                node.visitMethodInsn(INVOKESTATIC, mathHelper, new ASMString.ASMObfString("floor_double", "c").getText(), "(D)I", false);
                node.visitVarInsn(ISTORE, 15);
                Label l26 = new Label();
                node.visitLabel(l26);
                node.visitLineNumber(330, l26);
                node.visitVarInsn(ALOAD, 0);
                node.visitFieldInsn(GETFIELD, entity.getText(), new ASMString.ASMObfString("posZ", "u").getText(), "D");
                node.visitMethodInsn(INVOKESTATIC, mathHelper, new ASMString.ASMObfString("floor_double", "c").getText(), "(D)I", false);
                node.visitVarInsn(ISTORE, 16);
                Label l27 = new Label();
                node.visitLabel(l27);
                node.visitLineNumber(332, l27);
                node.visitVarInsn(ALOAD, 0);
                node.visitFieldInsn(GETFIELD, entity.getText(), worldObj, world.getObfASMTypeName());
                node.visitVarInsn(ILOAD, 14);
                node.visitVarInsn(ILOAD, 15);
                node.visitVarInsn(ILOAD, 16);
                node.visitMethodInsn(INVOKEVIRTUAL, world.getText(), getBlock, "(III)"+blockType, false);
                node.visitMethodInsn(INVOKEVIRTUAL, block, getRenderType, "()I", false);
                node.visitIntInsn(BIPUSH, 9);
                Label l28 = new Label();
                node.visitJumpInsn(IF_ICMPEQ, l28);
                node.visitVarInsn(ALOAD, 0);
                node.visitFieldInsn(GETFIELD, entity.getText(), worldObj, world.getObfASMTypeName());
                node.visitVarInsn(ILOAD, 14);
                node.visitVarInsn(ILOAD, 15);
                node.visitInsn(ICONST_1);
                node.visitInsn(ISUB);
                node.visitVarInsn(ILOAD, 16);
                node.visitMethodInsn(INVOKEVIRTUAL, world.getText(), getBlock, "(III)"+blockType, false);
                node.visitMethodInsn(INVOKEVIRTUAL, block, getRenderType, "()I", false);
                node.visitIntInsn(BIPUSH, 9);
                node.visitJumpInsn(IF_ICMPEQ, l28);
                Label l29 = new Label();
                node.visitLabel(l29);
                node.visitLineNumber(334, l29);
                node.visitIntInsn(BIPUSH, -3);
                node.visitInsn(IRETURN);
                node.visitLabel(l28);
                node.visitLineNumber(336, l28);
                node.visitFrame(Opcodes.F_APPEND, 1, new Object[]{Opcodes.INTEGER}, 0, null);
                Label l30 = new Label();
                node.visitJumpInsn(GOTO, l30);
                node.visitLabel(l23);
                node.visitLineNumber(337, l23);
                node.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                node.visitVarInsn(ALOAD, 12);
                node.visitVarInsn(ALOAD, 0);
                node.visitFieldInsn(GETFIELD, entity.getText(), worldObj, world.getObfASMTypeName());
                node.visitVarInsn(ILOAD, 9);
                node.visitVarInsn(ILOAD, 10);
                node.visitVarInsn(ILOAD, 11);
                node.visitMethodInsn(INVOKEVIRTUAL, block, new ASMString.ASMObfString("getBlocksMovement", "b").getText(), "(L" + new ASMString.ASMObfString("net/minecraft/world/IBlockAccess", "ahl").getText() + ";III)Z", false);
                node.visitJumpInsn(IFNE, l30);
                node.visitVarInsn(ILOAD, 6);
                Label l31 = new Label();
                node.visitJumpInsn(IFEQ, l31);
                node.visitVarInsn(ALOAD, 12);
                node.visitFieldInsn(GETSTATIC, initBlocks, new ASMString.ASMObfString("wooden_door", "aq").getText(), blockType);
                node.visitJumpInsn(IF_ACMPEQ, l30);
                node.visitLabel(l31);
                node.visitLineNumber(339, l31);
                node.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                node.visitVarInsn(ILOAD, 13);
                node.visitIntInsn(BIPUSH, 11);
                Label l32 = new Label();
                node.visitJumpInsn(IF_ICMPEQ, l32);
                node.visitVarInsn(ALOAD, 12);
                node.visitFieldInsn(GETSTATIC, initBlocks, new ASMString.ASMObfString("fence_gate","be").getText(), blockType);
                node.visitJumpInsn(IF_ACMPEQ, l32);
                node.visitVarInsn(ILOAD, 13);
                node.visitIntInsn(BIPUSH, 32);
                Label l33 = new Label();
                node.visitJumpInsn(IF_ICMPNE, l33);
                node.visitLabel(l32);
                node.visitLineNumber(341, l32);
                node.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                node.visitIntInsn(BIPUSH, -3);
                node.visitInsn(IRETURN);
                node.visitLabel(l33);
                node.visitLineNumber(344, l33);
                node.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                node.visitVarInsn(ALOAD, 12);
                node.visitFieldInsn(GETSTATIC, initBlocks, new ASMString.ASMObfString("trapdoor", "aT").getText(), blockType);
                Label l34 = new Label();
                node.visitJumpInsn(IF_ACMPNE, l34);
                Label l35 = new Label();
                node.visitLabel(l35);
                node.visitLineNumber(346, l35);
                node.visitIntInsn(BIPUSH, -4);
                node.visitInsn(IRETURN);
                node.visitLabel(l34);
                node.visitLineNumber(349, l34);
                node.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                node.visitVarInsn(ALOAD, 12);
                node.visitMethodInsn(INVOKEVIRTUAL, block, new ASMString.ASMObfString("getMaterial", "o").getText(), "()" + material.getObfASMTypeName(), false);
                node.visitVarInsn(ASTORE, 14);
                Label l36 = new Label();
                node.visitLabel(l36);
                node.visitLineNumber(351, l36);
                node.visitVarInsn(ALOAD, 14);
                node.visitFieldInsn(GETSTATIC, material.getText(), new ASMString.ASMObfString("lava","i").getText(), material.getObfASMTypeName());
                Label l37 = new Label();
                node.visitJumpInsn(IF_ACMPEQ, l37);
                Label l38 = new Label();
                node.visitLabel(l38);
                node.visitLineNumber(353, l38);
                node.visitInsn(ICONST_0);
                node.visitInsn(IRETURN);
                node.visitLabel(l37);
                node.visitLineNumber(356, l37);
                node.visitFrame(Opcodes.F_APPEND, 1, new Object[]{material.getText()}, 0, null);
                node.visitVarInsn(ALOAD, 0);
                node.visitMethodInsn(INVOKEVIRTUAL, entity.getText(), new ASMString.ASMObfString("handleLavaMovement","P").getText(), "()Z", false);
                node.visitJumpInsn(IFNE, l30);
                Label l39 = new Label();
                node.visitLabel(l39);
                node.visitLineNumber(358, l39);
                node.visitIntInsn(BIPUSH, -2);
                node.visitInsn(IRETURN);
                node.visitLabel(l30);
                node.visitLineNumber(361, l30);
                node.visitFrame(Opcodes.F_CHOP, 2, null, 0, null);
                Label l40 = new Label();
                node.visitJumpInsn(GOTO, l40);
                node.visitLabel(l12);
                node.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                node.visitVarInsn(ALOAD, 0);
                node.visitFieldInsn(GETFIELD, entity.getText(), worldObj, world.getObfASMTypeName());
                node.visitVarInsn(ILOAD, 9);
                node.visitVarInsn(ILOAD, 10);
                node.visitInsn(ICONST_1);
                node.visitInsn(ISUB);
                node.visitVarInsn(ILOAD, 11);
                node.visitMethodInsn(INVOKEVIRTUAL, world.getText(), getBlock, "(III)"+blockType, false);
                node.visitFieldInsn(GETSTATIC, "abo/ABO", "brickNoCrossing", blockType);
                Label l41 = new Label();
                node.visitJumpInsn(IF_ACMPEQ, l41);
                node.visitVarInsn(ALOAD, 0);
                node.visitFieldInsn(GETFIELD, entity.getText(), worldObj, world.getObfASMTypeName());
                node.visitVarInsn(ILOAD, 9);
                node.visitVarInsn(ILOAD, 10);
                node.visitInsn(ICONST_2);
                node.visitInsn(ISUB);
                node.visitVarInsn(ILOAD, 11);
                node.visitMethodInsn(INVOKEVIRTUAL, world.getText(), getBlock, "(III)"+blockType, false);
                node.visitFieldInsn(GETSTATIC, "abo/ABO", "brickNoCrossing", blockType);
                node.visitJumpInsn(IF_ACMPEQ, l41);
                node.visitVarInsn(ALOAD, 0);
                node.visitFieldInsn(GETFIELD, entity.getText(), worldObj, world.getObfASMTypeName());
                node.visitVarInsn(ILOAD, 9);
                node.visitVarInsn(ILOAD, 10);
                node.visitInsn(ICONST_3);
                node.visitInsn(ISUB);
                node.visitVarInsn(ILOAD, 11);
                node.visitMethodInsn(INVOKEVIRTUAL, world.getText(), getBlock, "(III)"+blockType, false);
                node.visitFieldInsn(GETSTATIC, "abo/ABO", "brickNoCrossing", blockType);
                node.visitJumpInsn(IF_ACMPNE, l40);
                node.visitLabel(l41);
                node.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                node.visitInsn(ICONST_0);
                node.visitInsn(IRETURN);
                node.visitLabel(l40);
                node.visitLineNumber(297, l40);
                node.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
                node.visitIincInsn(11, 1);
                node.visitJumpInsn(GOTO, l8);
                node.visitLabel(l9);
                node.visitLineNumber(295, l9);
                node.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
                node.visitIincInsn(10, 1);
                node.visitJumpInsn(GOTO, l5);
                node.visitLabel(l6);
                node.visitLineNumber(293, l6);
                node.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
                node.visitIincInsn(9, 1);
                node.visitJumpInsn(GOTO, l2);
                node.visitLabel(l3);
                node.visitLineNumber(366, l3);
                node.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
                node.visitVarInsn(ILOAD, 8);
                Label l42 = new Label();
                node.visitJumpInsn(IFEQ, l42);
                node.visitInsn(ICONST_2);
                Label l43 = new Label();
                node.visitJumpInsn(GOTO, l43);
                node.visitLabel(l42);
                node.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                node.visitInsn(ICONST_1);
                node.visitLabel(l43);
                node.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{Opcodes.INTEGER});
                node.visitInsn(IRETURN);
                Label l44 = new Label();
                node.visitLabel(l44);
                node.visitLocalVariable("j2", "I", null, l25, l28, 14);
                node.visitLocalVariable("l1", "I", null, l26, l28, 15);
                node.visitLocalVariable("i2", "I", null, l27, l28, 16);
                node.visitLocalVariable("material", material.getObfASMTypeName(), null, l36, l30, 14);
                node.visitLocalVariable("k1", "I", null, l22, l30, 13);
                node.visitLocalVariable("block", blockType, null, l11, l40, 12);
                node.visitLocalVariable("j1", "I", null, l8, l9, 11);
                node.visitLocalVariable("i1", "I", null, l5, l6, 10);
                node.visitLocalVariable("l", "I", null, l2, l3, 9);
                node.visitLocalVariable("p_82565_0_", entity.getObfASMTypeName(), null, l0, l44, 0);
                node.visitLocalVariable("p_82565_1_", "I", null, l0, l44, 1);
                node.visitLocalVariable("p_82565_2_", "I", null, l0, l44, 2);
                node.visitLocalVariable("p_82565_3_", "I", null, l0, l44, 3);
                node.visitLocalVariable("p_82565_4_", "L"+pathPoint+";", null, l0, l44, 4);
                node.visitLocalVariable("p_82565_5_", "Z", null, l0, l44, 5);
                node.visitLocalVariable("p_82565_6_", "Z", null, l0, l44, 6);
                node.visitLocalVariable("p_82565_7_", "Z", null, l0, l44, 7);
                node.visitLocalVariable("flag3", "Z", null, l1, l44, 8);
                node.visitMaxs(5, 17);
                node.visitEnd();
                return node;
            }
        };

        da3dsoulClassTransformer.isBoundingBoxInFrustrumFullyTransformer = new Transformer.MethodTransformer(Transformer.NODE_ADD, new ASMString("<cinit>"), "()V") {
            @Override
            protected MethodNode getNodeToAdd() {
                MethodNode mv;
                mv = new MethodNode(ACC_PUBLIC, "isBoundingBoxInFrustumFully", "(Lnet/minecraft/util/AxisAlignedBB;)Z", null, null);
                mv.visitCode();
                Label l0 = new Label();
                mv.visitLabel(l0);
                mv.visitLineNumber(45, l0);
                /*mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/util/AxisAlignedBB", "field_72340_a", "D");// a -> field_72340_a -> minX
                mv.visitVarInsn(ALOAD, 1);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/util/AxisAlignedBB", "field_72338_b", "D");// b -> field_72338_b -> minY
                mv.visitVarInsn(ALOAD, 1);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/util/AxisAlignedBB", "field_72339_c", "D");// c -> field_72339_c -> minZ
                mv.visitVarInsn(ALOAD, 1);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/util/AxisAlignedBB", "field_72336_d", "D");// d -> field_72336_d -> maxX
                mv.visitVarInsn(ALOAD, 1);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/util/AxisAlignedBB", "field_72337_e", "D");// e -> field_72337_e -> maxY
                mv.visitVarInsn(ALOAD, 1);
                mv.visitFieldInsn(GETFIELD, "net/minecraft/util/AxisAlignedBB", "field_72334_f", "D");// f -> field_72334_f -> maxZ
                mv.visitMethodInsn(INVOKEVIRTUAL, "us/ichun/mods/doors/client/render/culling/Frustrum", "isBoxInFrustum", "(DDDDDD)Z", false);
                mv.visitInsn(IRETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", "Lus/ichun/mods/doors/client/render/culling/Frustrum;", null, l0, l1, 0);
                mv.visitLocalVariable("aab", "Lnet/minecraft/util/AxisAlignedBB;", null, l0, l1, 1);
                mv.visitMaxs(13, 2);*/
                mv.visitInsn(ICONST_1);
                mv.visitInsn(IRETURN);
                Label l1 = new Label();
                mv.visitLabel(l1);
                mv.visitLocalVariable("this", "Lus/ichun/mods/doors/client/render/culling/Frustrum;", null, l0, l1, 0);
                mv.visitLocalVariable("aab", "Lnet/minecraft/util/AxisAlignedBB;", null, l0, l1, 1);
                mv.visitMaxs(1, 2);
                mv.visitEnd();
                return mv;
            }
        };

        da3dsoulClassTransformer.classMap = new HashMap<String, Transformer.ClassTransformer>();
        for (final ClassTransformers classTransformer : ClassTransformers.values()) {
            da3dsoulClassTransformer.classMap.put(classTransformer.getClassName(), classTransformer.getTransformer());
        }
        Transformer.log.info((ASMString.OBFUSCATED ? "O" : "Deo") + "bfuscated environment detected");
    }

    private enum ClassTransformers {
        FRUSTRUM(new Transformer.ClassTransformer(da3dsoulClassTransformer.Frustrum, new Transformer[] { da3dsoulClassTransformer.isBoxInFrustrumFullyTransformer, da3dsoulClassTransformer.isBoundingBoxInFrustrumFullyTransformer })),
        PATHFINDING(new Transformer.ClassTransformer(da3dsoulClassTransformer.PathFinding, new Transformer[] { da3dsoulClassTransformer.pathfinderTransformerDelete, da3dsoulClassTransformer.pathfinderTransformerAdd }));
        
        
        private Transformer.ClassTransformer transformer;

        private ClassTransformers(final Transformer.ClassTransformer transformer) {
            this.transformer = transformer;
        }

        public Transformer.ClassTransformer getTransformer() {
            return this.transformer;
        }

        public String getClassName() {
            return this.transformer.getClassName();
        }
    }
}
