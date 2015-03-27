package da3dsoul.scaryGen.liquidXP;

import abo.ABO;
import abo.ItemIconProvider;
import mods.immibis.lxp.LiquidXPMod;
import mods.immibis.lxp.R;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BucketItem extends mods.immibis.lxp.BucketItem {

    public BucketItem() {
        super();
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     *
     * @param itemStack
     * @param player
     * @param world
     * @param x
     * @param y
     * @param z
     * @param face
     * @param hitX
     * @param hitY
     * @param hitZ
     */
    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int face, float hitX, float hitY, float hitZ) {
        if(itemStack != null) {
            if(world.getBlock(x,y,z).getMaterial().isReplaceable()){
                if(world.setBlock(x,y,z, ABO.blockLiquidXP, 0, 3)) {
                    if(!player.capabilities.isCreativeMode) player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.bucket));
                    if(!world.isRemote) player.swingItem();
                    return true;
                }
            } else {
                if(face < 0 || face > 5) return false;
                ForgeDirection dir = ForgeDirection.values()[face];
                if(world.setBlock(x+dir.offsetX,y+dir.offsetY,z+dir.offsetZ, ABO.blockLiquidXP, 0, 3)) {
                    if(!player.capabilities.isCreativeMode) player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.bucket));
                    if(!world.isRemote) player.swingItem();
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets an icon index based on an item's damage value
     *
     * @param p_77617_1_
     */
    @Override
    public IIcon getIconFromDamage(int p_77617_1_) {
        return ABO.instance.itemIconProvider.getIcon(ItemIconProvider.bucket);
    }
}
