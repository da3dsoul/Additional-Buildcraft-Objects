package da3dsoul.asm.obfuscation;

import net.minecraftforge.classloading.*;

public class ASMString
{
    public static boolean OBFUSCATED;
    private String text;
    
    public ASMString(final String text) {
        this.text = text;
    }
    
    public ASMString(final Class clazz) {
        this.text = clazz.getCanonicalName();
    }
    
    public String getText() {
        return this.text;
    }
    
    public String getReadableText() {
        return this.text;
    }
    
    public String getASMClassName() {
        return this.text.replaceAll("\\.", "/");
    }
    
    public String getObfASMClassName() {
        return this.getASMClassName();
    }
    
    public String getASMTypeName() {
        return "L" + this.getASMClassName() + ";";
    }
    
    public String getObfASMTypeName() {
        return this.getASMTypeName();
    }
    
    static {
        ASMString.OBFUSCATED = FMLForgePlugin.RUNTIME_DEOBF;
    }
    
    public static class ASMObfString extends ASMString
    {
        private String obfText;
        
        public ASMObfString(final String text, final String obfText) {
            super(text);
            this.obfText = obfText;
        }
        
        @Override
        public String getObfASMTypeName() {
            return ASMString.OBFUSCATED ? ("L" + this.obfText + ";") : this.getASMTypeName();
        }
        
        @Override
        public String getObfASMClassName() {
            return ASMString.OBFUSCATED ? this.obfText : this.getASMClassName();
        }
        
        @Override
        public String getText() {
            return ASMString.OBFUSCATED ? this.obfText : super.getText();
        }
    }
}
