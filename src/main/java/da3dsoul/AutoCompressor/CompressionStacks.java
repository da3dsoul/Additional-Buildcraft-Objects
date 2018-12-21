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

    public static ICompressionStack getTypeFromSet(ICompressionStack type, ItemStack stack)
    {
        if (!type.isItemStackOfTypeSet(stack)) return null;

        return getItemStackType(type, stack, false);
    }

    private static ICompressionStack getItemStackType(ICompressionStack type, ItemStack stack, boolean fromBottom) {
        if (stack == null) return null;

        if (type.isItemStackOfType(stack)) return type;

        if (!fromBottom && type.getPreviousTier() != null)
            return getItemStackType(type.getPreviousTier(), stack, false);

        if (type.getNextTier() != null)
            return getItemStackType(type.getNextTier(), stack, true);

        return null;
    }

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
        public int stackSizeOfBaseToForm() {
            return 1;
        }

        @Override
        public abstract ICompressionStack getNextTier();

        @Override
        public ICompressionStack getHighestTier() {
            ICompressionStack tier = this;
            while (tier.getNextTier() != null) tier = tier.getNextTier();
            return tier;
        }

        @Override
        public boolean isItemStackOfType(ItemStack stack) {
            if (stack == null) return false;
            return stack.isItemEqual(getIdentityItemStack());
        }

        @Override
        public boolean isItemStackOfTypeSet(ItemStack stack) {
            return isItemStackOfTypeSet(stack, true);
        }

        @Override
        public boolean isItemStackOfTypeSet(ItemStack stack, boolean fromBottom) {
            if (stack == null) return false;

            if (isItemStackOfType(stack)) return true;

            if (getNextTier() != null)
                return getNextTier().isItemStackOfTypeSet(stack, true);

            return false;
        }

        @Override
        public int getTotalStackSizeOfType(ItemStack stack) {
            return getTotalStackSizeOfType(stack, true);
        }

        @Override
        public int getTotalStackSizeOfType(ItemStack stack, boolean fromBottom) {
            if (stack == null) return 0;
            if (isItemStackOfType(stack)) return stackSizeOfBaseToForm() * stack.stackSize;

            if (getNextTier() != null)
                return getNextTier().getTotalStackSizeOfType(stack, true);

            return 0;
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
        public ICompressionStack getHighestTier() {
            ICompressionStack tier = this;
            while (tier.getNextTier() != null) tier = tier.getNextTier();
            return tier;
        }

        @Override
        public boolean isItemStackOfType(ItemStack stack) {
            if (stack == null) return false;
            return stack.isItemEqual(getIdentityItemStack());
        }

        @Override
        public boolean isItemStackOfTypeSet(ItemStack stack) {
            return isItemStackOfTypeSet(stack, false);
        }

        public boolean isItemStackOfTypeSet(ItemStack stack, boolean fromBottom) {
            if (stack == null) return false;

            if (isItemStackOfType(stack)) return true;
            if (!fromBottom && getPreviousTier() != null)
                return getPreviousTier().isItemStackOfTypeSet(stack);

            if (getNextTier() != null)
                return getNextTier().isItemStackOfTypeSet(stack, true);

            return false;
        }

        @Override
        public int getTotalStackSizeOfType(ItemStack stack) {
            return getTotalStackSizeOfType(stack, false);
        }

        public int getTotalStackSizeOfType(ItemStack stack, boolean fromBottom) {
            if (stack == null) return 0;

            if (isItemStackOfType(stack))
                return stack.stackSize * stackSizeOfBaseToForm();

            if (!fromBottom && getPreviousTier() != null)
                return getPreviousTier().getTotalStackSizeOfType(stack);

            if (getNextTier() != null)
                return getNextTier().getTotalStackSizeOfType(stack, true);

            return 0;
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

        private boolean isCobble(ItemStack itemStack) {
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

        @Override
        public int stackSizeOfBaseToForm() {
            return 9;
        }
    }

    public static class DoubleCompressedCobblestoneStack extends Base9CompressedStack {
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(ExtraUtils.cobblestoneCompr, 1, 1);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return CompressionStacks.CompressedCobblestone;
        }

        @Override
        public ICompressionStack getNextTier() {
            return CompressionStacks.TripleCompressedCobblestone;
        }

        @Override
        public int stackSizeOfBaseToForm() {
            return 81;
        }
    }

    public static class TripleCompressedCobblestoneStack extends Base9CompressedStack {
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(ExtraUtils.cobblestoneCompr, 1, 2);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return CompressionStacks.DoubleCompressedCobblestone;
        }

        @Override
        public ICompressionStack getNextTier() {
            return CompressionStacks.QuadrupleCompressedCobblestone;
        }

        @Override
        public int stackSizeOfBaseToForm() {
            return 729;
        }
    }

    public static class QuadrupleCompressedCobblestoneStack extends Base9CompressedStack {
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(ExtraUtils.cobblestoneCompr, 1, 3);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return CompressionStacks.TripleCompressedCobblestone;
        }

        @Override
        public ICompressionStack getNextTier() {
            return null;
        }

        @Override
        public int stackSizeOfBaseToForm() {
            return 6561;
        }
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

        @Override
        public int stackSizeOfBaseToForm() {
            return 9;
        }
    }

    public static class DoubleCompressedDirtStack extends Base9CompressedStack {
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(ExtraUtils.cobblestoneCompr, 1, 9);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return CompressionStacks.CompressedDirt;
        }

        @Override
        public ICompressionStack getNextTier() {
            return null;
        }

        @Override
        public int stackSizeOfBaseToForm() {
            return 81;
        }
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

        @Override
        public int stackSizeOfBaseToForm() {
            return 9;
        }
    }

    public static class DoubleCompressedGravelStack extends Base9CompressedStack {
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(ExtraUtils.cobblestoneCompr, 1, 13);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return CompressionStacks.CompressedGravel;
        }

        @Override
        public ICompressionStack getNextTier() {
            return null;
        }

        @Override
        public int stackSizeOfBaseToForm() {
            return 81;
        }
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

        @Override
        public int stackSizeOfBaseToForm() {
            return 9;
        }
    }

    public static class DoubleCompressedSandStack extends Base9CompressedStack {
        @Override
        public ItemStack getIdentityItemStack() {
            return new ItemStack(ExtraUtils.cobblestoneCompr, 1, 15);
        }

        @Override
        public ICompressionStack getPreviousTier() {
            return CompressionStacks.CompressedSand;
        }

        @Override
        public ICompressionStack getNextTier() {
            return null;
        }

        @Override
        public int stackSizeOfBaseToForm() {
            return 81;
        }
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

        @Override
        public int stackSizeOfBaseToForm() {
            return 9;
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

        @Override
        public int stackSizeOfBaseToForm() {
            return 9;
        }
    }
}
