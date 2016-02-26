package da3dsoul.asm;

import da3dsoul.asm.obfuscation.ASMString;
import net.minecraft.launchwrapper.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.util.*;

public class iChunDoorsTransformer implements IClassTransformer, Opcodes {
    private static ASMString Frustrum;
    private static Transformer.MethodTransformer isBoxInFrustrumFullyTransformer;
    private static Transformer.MethodTransformer isBoundingBoxInFrustrumFullyTransformer;
    private static Map<String, Transformer.ClassTransformer> classMap;

    public byte[] transform(final String className, final String className2, byte[] bytes) {
        final Transformer.ClassTransformer clazz = da3dsoul.asm.iChunDoorsTransformer.classMap.get(className);
        if (clazz != null) {
            bytes = clazz.transform(bytes);
            da3dsoul.asm.iChunDoorsTransformer.classMap.remove(className);
        }
        return bytes;
    }

    static {
        iChunDoorsTransformer.Frustrum = new ASMString("us.ichun.mods.doors.client.render.culling.Frustrum");


        iChunDoorsTransformer.isBoxInFrustrumFullyTransformer = new Transformer.MethodTransformer(Transformer.NODE_ADD, new ASMString("<cinit>"), "()V") {
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

        iChunDoorsTransformer.isBoundingBoxInFrustrumFullyTransformer = new Transformer.MethodTransformer(Transformer.NODE_ADD, new ASMString("<cinit>"), "()V") {
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

        iChunDoorsTransformer.classMap = new HashMap<String, Transformer.ClassTransformer>();
        for (final ClassTransformers classTransformer : ClassTransformers.values()) {
            iChunDoorsTransformer.classMap.put(classTransformer.getClassName(), classTransformer.getTransformer());
        }
        Transformer.log.info((ASMString.OBFUSCATED ? "O" : "Deo") + "bfuscated environment detected");
    }

    private enum ClassTransformers {
        INIT(new Transformer.ClassTransformer(da3dsoul.asm.iChunDoorsTransformer.Frustrum, new Transformer[] { iChunDoorsTransformer.isBoxInFrustrumFullyTransformer, iChunDoorsTransformer.isBoundingBoxInFrustrumFullyTransformer }));

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
