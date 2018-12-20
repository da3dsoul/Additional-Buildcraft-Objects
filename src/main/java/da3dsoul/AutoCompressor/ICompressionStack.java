package da3dsoul.AutoCompressor;

import net.minecraft.item.ItemStack;

public interface ICompressionStack {
    ItemStack getIdentityItemStack();
    ICompressionStack getPreviousTier();
    int stackSizeToForm();

    ICompressionStack getNextTier();
    int stackSizeToNextTier();

    boolean isItemStackOfType(ItemStack stack);
}
