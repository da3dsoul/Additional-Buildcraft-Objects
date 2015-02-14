package abo.energy;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import abo.ABO;
import abo.items.ABOItem;
import buildcraft.core.CreativeTabBuildCraft;

public class ItemWaterwheel extends ABOItem
{
	
    public ItemWaterwheel()
    {
        this.setCreativeTab(CreativeTabBuildCraft.BLOCKS.get());
        setUnlocalizedName("waterwheelItem");
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int x, int y, int z, int side, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
    	ForgeDirection dir = ForgeDirection.getOrientation(side);
            if (entityplayer.canPlayerEdit(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, side, itemstack))
            {
                if (!ABO.waterwheelBlock.canPlaceBlockOnSide(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, (dir.offsetX != 0 ? 1 : 0)))
                {
                    return false;
                }
                else
                {
                    world.setBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, ABO.waterwheelBlock, (dir.offsetX != 0 ? 1 : 0), 3);
                    if(!entityplayer.capabilities.isCreativeMode)--itemstack.stackSize;
                    return true;
                }
            }
            else
            {
                return false;
            }
    }

	@Override
	public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon("additional-buildcraft-objects:waterwheelIcon");
	}
    
}