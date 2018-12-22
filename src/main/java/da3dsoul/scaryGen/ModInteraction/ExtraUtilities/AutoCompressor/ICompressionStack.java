package da3dsoul.scaryGen.ModInteraction.ExtraUtilities.AutoCompressor;

import net.minecraft.item.ItemStack;

public interface ICompressionStack {
    ItemStack getIdentityItemStack();
    ICompressionStack getPreviousTier();
    int stackSizeToForm();
    int stackSizeOfBaseToForm();

    ICompressionStack getNextTier();

    ICompressionStack getHighestTier();

    boolean isItemStackOfType(ItemStack stack);
    boolean isItemStackOfTypeSet(ItemStack stack);
    boolean isItemStackOfTypeSet(ItemStack stack, boolean fromBottom);

    int getTotalStackSizeOfType(ItemStack stack);
    int getTotalStackSizeOfType(ItemStack stack, boolean fromBottom);
}
