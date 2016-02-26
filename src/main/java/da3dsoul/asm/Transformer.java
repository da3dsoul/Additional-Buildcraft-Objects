package da3dsoul.asm;

import da3dsoul.asm.obfuscation.ASMString;
import org.objectweb.asm.*;
import org.apache.logging.log4j.*;
import org.objectweb.asm.tree.*;

public abstract class Transformer<T> implements ITransformer, Opcodes
{
    public static int NODE_MODIFY;
    public static int NODE_ACCESS;
    public static int NODE_DELETE;
    public static int NODE_ADD;
    public static Logger log;
    protected Access access;
    protected int action;
    protected ClassNode classNode;
    protected ASMString name;
    
    protected Transformer() {
        this(Transformer.NODE_MODIFY);
    }
    
    protected Transformer(final int action) {
        this(action, Access.PUBLIC);
    }
    
    protected Transformer(final int action, final Access access) {
        this.action = action;
        this.access = access;
    }
    
    @Override
    public boolean transform(final ClassNode classNode) {
        this.classNode = classNode;
        if ((this.action & Transformer.NODE_ADD) > 0) {
            this.add();
            return true;
        }
        final T node = this.find();
        if (node != null) {
            if ((this.action & Transformer.NODE_MODIFY) > 0) {
                this.modify(node);
            }
            if ((this.action & Transformer.NODE_ACCESS) > 0) {
                this.changeAccess(node);
            }
            if ((this.action & Transformer.NODE_DELETE) > 0) {
                this.delete(node);
            }
            return true;
        }
        return false;
    }
    
    protected abstract T find();
    
    protected abstract void changeAccess(final T p0);
    
    protected abstract void modify(final T p0);
    
    protected abstract void delete(final T p0);
    
    protected abstract void add();
    
    protected abstract T getNodeToAdd();
    
    protected abstract void log();
    
    static {
        Transformer.NODE_MODIFY = 1;
        Transformer.NODE_ACCESS = 2;
        Transformer.NODE_DELETE = 4;
        Transformer.NODE_ADD = 8;
        Transformer.log = LogManager.getLogger("da3dsoulASMFix");
    }
    
    public enum Access
    {
        PUBLIC(1), 
        PRIVATE(2), 
        PROTECTED(4);
        
        public static int clearAccess;
        private int val;
        
        private Access(final int val) {
            this.val = val;
        }
        
        public int modifyAccess(final int access) {
            return (access & Access.clearAccess) ^ this.val;
        }
        
        static {
            Access.clearAccess = -8;
        }
    }
    
    public static class ClassTransformer extends Transformer<ClassNode>
    {
        private Transformer[] transformers;
        
        public ClassTransformer(final String name, final Transformer... transformers) {
            this(new ASMString(name), transformers);
        }
        
        public ClassTransformer(final String name, final String obfName, final Transformer... transformers) {
            this(new ASMString.ASMObfString(name, obfName), transformers);
        }
        
        public ClassTransformer(final ASMString name, final Transformer... transformers) {
            super(Transformer.NODE_MODIFY);
            super.name = name;
            this.transformers = transformers;
        }
        
        public String getClassName() {
            return super.name.getText();
        }
        
        public byte[] transform(final byte[] bytes) {
            final ClassNode node = ASMHelper.readClassFromBytes(bytes);
            if (this.transform(node)) {
                return ASMHelper.writeClassToBytesNoDeobfSkipFrames(node);
            }
            return bytes;
        }
        
        @Override
        protected ClassNode find() {
            return super.classNode;
        }
        
        @Override
        protected void changeAccess(final ClassNode node) {
            super.classNode.access = super.access.modifyAccess(super.classNode.access);
        }
        
        @Override
        protected void modify(final ClassNode node) {
            this.log();
            for (final Transformer transformer : this.transformers) {
                if (transformer.transform(node)) {
                    transformer.log();
                }
            }
        }
        
        @Override
        protected void delete(final ClassNode node) {
        }
        
        @Override
        protected void add() {
        }
        
        @Override
        protected ClassNode getNodeToAdd() {
            return null;
        }
        
        @Override
        protected void log() {
            Transformer.log.info("Transforming Class: " + super.name.getReadableText());
        }
    }
    
    public static class FieldTransformer extends Transformer<FieldNode>
    {
        protected String fieldDesc;
        
        public FieldTransformer(final String name, final String desc) {
            this(new ASMString(name), desc);
        }
        
        public FieldTransformer(final String name, final String obfName, final String desc) {
            this(new ASMString.ASMObfString(name, obfName), desc);
        }
        
        public FieldTransformer(final ASMString name, final String desc) {
            this(Transformer.NODE_ACCESS, Access.PUBLIC, name, desc);
        }
        
        public FieldTransformer(final int action, final Access access, final ASMString name, final String desc) {
            super(action, access);
            super.name = name;
            this.fieldDesc = desc;
        }
        
        @Override
        protected FieldNode find() {
            final String fieldName = super.name.getText();
            for (final FieldNode field : super.classNode.fields) {
                if (field.name.equals(fieldName) && field.desc.equals(this.fieldDesc)) {
                    return field;
                }
            }
            return null;
        }
        
        @Override
        protected void changeAccess(final FieldNode node) {
            node.access = super.access.modifyAccess(node.access);
        }
        
        @Override
        protected void modify(final FieldNode node) {
        }
        
        @Override
        protected void delete(final FieldNode node) {
            super.classNode.fields.remove(node);
        }
        
        @Override
        protected void add() {
            super.classNode.fields.add(this.getNodeToAdd());
        }
        
        @Override
        protected FieldNode getNodeToAdd() {
            return null;
        }
        
        @Override
        protected void log() {
            Transformer.log.info("Transformed Field " + super.name.getReadableText());
        }
    }
    
    public static class MethodTransformer extends Transformer<MethodNode>
    {
        protected String methodDesc;
        
        public MethodTransformer(final String name, final String desc) {
            this(new ASMString(name), desc);
        }
        
        public MethodTransformer(final String name, final String obfName, final String desc) {
            this(new ASMString.ASMObfString(name, obfName), desc);
        }
        
        public MethodTransformer(final ASMString name, final String desc) {
            this(Transformer.NODE_MODIFY, Access.PUBLIC, name, desc);
        }
        
        public MethodTransformer(final int action, final ASMString name, final String desc) {
            this(action, Access.PUBLIC, name, desc);
        }
        
        public MethodTransformer(final int action, final Access access, final ASMString name, final String desc) {
            super(action, access);
            super.name = name;
            this.methodDesc = desc;
        }
        
        @Override
        protected MethodNode find() {
            final String name = super.name.getText();
            for (final MethodNode method : super.classNode.methods) {
                if (method.name.equals(name) && method.desc.equals(this.methodDesc)) {
                    return method;
                }
            }
            return null;
        }
        
        @Override
        protected void changeAccess(final MethodNode node) {
            node.access = super.access.modifyAccess(node.access);
        }
        
        @Override
        protected void modify(final MethodNode node) {
        }
        
        @Override
        protected void delete(final MethodNode node) {
            super.classNode.methods.remove(node);
        }
        
        @Override
        protected void add() {
            super.classNode.methods.add(this.getNodeToAdd());
        }
        
        @Override
        protected MethodNode getNodeToAdd() {
            return null;
        }
        
        @Override
        protected void log() {
            Transformer.log.info("Transformed Method: " + super.name.getReadableText());
        }
    }
    
    public static class InnerClassTransformer extends Transformer<InnerClassNode>
    {
        @Override
        protected InnerClassNode find() {
            final String name = super.classNode.name + "$" + super.name.getText();
            for (final InnerClassNode inner : super.classNode.innerClasses) {
                if (name.equals(inner.name)) {
                    return inner;
                }
            }
            return null;
        }
        
        @Override
        protected void changeAccess(final InnerClassNode node) {
            node.access = super.access.modifyAccess(node.access);
        }
        
        @Override
        protected void modify(final InnerClassNode node) {
        }
        
        @Override
        protected void delete(final InnerClassNode node) {
            super.classNode.innerClasses.remove(node);
        }
        
        @Override
        protected void add() {
            super.classNode.innerClasses.add(this.getNodeToAdd());
        }
        
        @Override
        protected InnerClassNode getNodeToAdd() {
            return null;
        }
        
        @Override
        protected void log() {
            Transformer.log.info("Transformed Inner Class: " + super.name.getReadableText());
        }
    }
}
