package da3dsoul.AutoCompressor;

import cofh.core.util.oredict.OreDictionaryArbiter;
import com.rwtema.extrautils.ExtraUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class CompressionStacks {
    public static final ArrayList<ICompressionStack> BaseStacks = new ArrayList<ICompressionStack>();
    public static final ICompressionStack Cobblestone = new CobblestoneStack();
    public static final ICompressionStack CompressedCobblestone = new CompressedCobblestoneStack();
    public static final ICompressionStack DoubleCompressedCobblestone = new DoubleCompressedCobblestoneStack();
    public static final ICompressionStack TripleCompressedCobblestone = new TripleCompressedCobblestoneStack();
    public static final ICompressionStack QuadrupleCompressedCobblestone = new QuadrupleCompressedCobblestoneStack();

    public static final ICompressionStack Dirt = new DirtStack();
    public static final ICompressionStack CompressedDirt = new CompressedDirtStack();
    public static final ICompressionStack DoubleCompressedDirt = new DoubleCompressedDirtStack();

    public static final ICompressionStack Gravel = new GravelStack();
    public static final ICompressionStack CompressedGravel = new CompressedGravelStack();
    public static final ICompressionStack DoubleCompressedGravel = new DoubleCompressedGravelStack();

    public static final ICompressionStack Sand = new SandStack();
    public static final ICompressionStack CompressedSand = new CompressedSandStack();
    public static final ICompressionStack DoubleCompressedSand = new DoubleCompressedSandStack();

    public static final ICompressionStack Redstone = new RedstoneStack();
    public static final ICompressionStack RedstoneBlock = new RedstoneBlockStack();

    public static final ICompressionStack Lapis = new LapisStack();
    public static final ICompressionStack LapisBlock = new LapisBlockStack();

    public static abstract class BaseCompressedStack implements ICompressionStack {
        public BaseCompressedStack() {
            BaseStacks.add(this);
        }

        @Override
        public abstract ItemStack getIdentityItemStack();

        @Override
        public abstract ICompressionStack getPreviousTier();

        @Override
        public int stackSizeToForm() {
            return 1;
        }

        @Override
        public abstract ICompressionStack getNextTier();

        @Override
        public int stackSizeToNextTier() {
            return 9;
        }

        @Override
        public boolean isItemStackOfType(ItemStack stack)
        {
            if (stack == null) return false;
            return stack.isItemEqual(getIdentityItemStack());
        }
    }

    public static abstract class Base9CompressedStack implements ICompressionStack {
        @Override
        public abstract ItemStack getIdentityItemStack();

        @Override
        public abstract ICompressionStack getPreviousTier();

        @Override
        public int stackSizeToForm() {
            return 9;
        }

        @Override
        public abstract ICompressionStack getNextTier();

        @Override
        public int stackSizeToNextTier() {
            return 9;
        }

        @Override
        public boolean isItemStackOfType(ItemStack stack)
        {
            if (stack == null) return false;
            return stack.isItemEqual(getIdentityItemStack());
        }
    }

    public static class CobblestoneStack extends BaseCompressedStack {
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(Blocks.cobblestone);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return null;
        }

        @Override
        public ICompressionStack getNextTier() {
            return CompressionStacks.CompressedCobblestone;
        }

        @Override
        public boolean isItemStackOfType(ItemStack stack) {
            if (stack == null) return false;
            return isCobble(stack);
        }

        private boolean isCobble(ItemStack itemStack)
        {
            ArrayList<String> ores = OreDictionaryArbiter.getAllOreNames(itemStack);
            return ores != null && ores.contains("cobblestone");
        }
    }

    public static class CompressedCobblestoneStack extends Base9CompressedStack {
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(ExtraUtils.cobblestoneCompr, 1, 0);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return CompressionStacks.Cobblestone;
        }

        @Override
        public ICompressionStack getNextTier() {
            return CompressionStacks.DoubleCompressedCobblestone;
        }
    }

    public static class DoubleCompressedCobblestoneStack extends Base9CompressedStack {
        @Override
        public ItemStack getIdentityItemStack() { return new ItemStack(ExtraUtils.cobblestoneCompr, 1, 1); }

        @Override
        public ICompressionStack getPreviousTier() { return CompressionStacks.CompressedCobblestone; }

        @Override
        public ICompressionStack getNextTier() { return CompressionStacks.TripleCompressedCobblestone; }
    }

    public static class TripleCompressedCobblestoneStack extends Base9CompressedStack{
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(ExtraUtils.cobblestoneCompr, 1, 2);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return CompressionStacks.DoubleCompressedCobblestone;
        }

        @Override
        public ICompressionStack getNextTier() { return CompressionStacks.QuadrupleCompressedCobblestone; }
    }

    public static class QuadrupleCompressedCobblestoneStack extends Base9CompressedStack{
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(ExtraUtils.cobblestoneCompr, 1, 3);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return CompressionStacks.TripleCompressedCobblestone;
        }

        @Override
        public ICompressionStack getNextTier() { return null; }
    }

    // DIRT
    public static class DirtStack extends BaseCompressedStack {
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(Blocks.dirt);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return null;
        }

        @Override
        public ICompressionStack getNextTier() {
            return CompressionStacks.CompressedDirt;
        }
    }

    public static class CompressedDirtStack extends Base9CompressedStack {
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(ExtraUtils.cobblestoneCompr, 1, 8);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return CompressionStacks.Dirt;
        }

        @Override
        public ICompressionStack getNextTier() {
            return CompressionStacks.DoubleCompressedDirt;
        }
    }

    public static class DoubleCompressedDirtStack extends Base9CompressedStack {
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(ExtraUtils.cobblestoneCompr, 1, 9);
        }

        @Override
        public ICompressionStack getPreviousTier() { return CompressionStacks.CompressedDirt; }

        @Override
        public ICompressionStack getNextTier() { return null; }
    }

    // GRAVEL
    public static class GravelStack extends BaseCompressedStack {
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(Blocks.gravel);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return null;
        }

        @Override
        public ICompressionStack getNextTier() {
            return CompressionStacks.CompressedGravel;
        }
    }

    public static class CompressedGravelStack extends Base9CompressedStack {
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(ExtraUtils.cobblestoneCompr, 1, 12);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return CompressionStacks.Gravel;
        }

        @Override
        public ICompressionStack getNextTier() {
            return CompressionStacks.DoubleCompressedGravel;
        }
    }

    public static class DoubleCompressedGravelStack extends Base9CompressedStack {
        @Override
        public ItemStack getIdentityItemStack() { return new ItemStack(ExtraUtils.cobblestoneCompr, 1, 13); }

        @Override
        public ICompressionStack getPreviousTier() { return CompressionStacks.CompressedGravel; }

        @Override
        public ICompressionStack getNextTier() { return null; }
    }

    // SAND
    public static class SandStack extends BaseCompressedStack {
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(Blocks.sand);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return null;
        }

        @Override
        public ICompressionStack getNextTier() {
            return CompressionStacks.CompressedSand;
        }
    }

    public static class CompressedSandStack extends Base9CompressedStack {
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(ExtraUtils.cobblestoneCompr, 1, 14);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return CompressionStacks.Sand;
        }

        @Override
        public ICompressionStack getNextTier() {
            return CompressionStacks.DoubleCompressedSand;
        }
    }

    public static class DoubleCompressedSandStack extends Base9CompressedStack {
        @Override
        public ItemStack getIdentityItemStack() { return new ItemStack(ExtraUtils.cobblestoneCompr, 1, 15); }

        @Override
        public ICompressionStack getPreviousTier() { return CompressionStacks.CompressedSand; }

        @Override
        public ICompressionStack getNextTier() { return null; }
    }

    // REDSTONE
    public static class RedstoneStack extends BaseCompressedStack {
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(Items.redstone);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return null;
        }

        @Override
        public ICompressionStack getNextTier() {
            return CompressionStacks.RedstoneBlock;
        }

        @Override
        public boolean isItemStackOfType(ItemStack stack) {
            return false;
        }
    }

    public static class RedstoneBlockStack extends Base9CompressedStack {
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(Blocks.redstone_block);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return CompressionStacks.Redstone;
        }

        @Override
        public ICompressionStack getNextTier() {
            return null;
        }
    }

    // LAPIS
    public static class LapisStack extends BaseCompressedStack {
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(Items.dye, 1, 4);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return null;
        }

        @Override
        public ICompressionStack getNextTier() {
            return CompressionStacks.LapisBlock;
        }

        @Override
        public boolean isItemStackOfType(ItemStack stack) {
            return false;
        }
    }

    public static class LapisBlockStack extends Base9CompressedStack {
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(Blocks.lapis_block);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return CompressionStacks.Lapis;
        }

        @Override
        public ICompressionStack getNextTier() {
            return null;
        }
    }
}
