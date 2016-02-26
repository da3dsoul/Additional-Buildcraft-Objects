package da3dsoul.scaryGen.blocks;

import abo.ABO;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMultiTexture;

public class ItemSandStone extends ItemMultiTexture {

    public ItemSandStone(Block p_i45346_1_, Block p_i45346_2_, String[] p_i45346_3_) {
        super(p_i45346_1_, p_i45346_2_, p_i45346_3_);
    }

    public ItemSandStone(Block block) {
        this(ABO.sandStone, ABO.sandStone, BlockSandStone.iconNames);
    }
}
