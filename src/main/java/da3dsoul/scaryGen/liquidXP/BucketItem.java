package da3dsoul.scaryGen.liquidXP;

import abo.ABO;
import abo.ItemIconProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class BucketItem extends mods.immibis.lxp.BucketItem {

    public BucketItem() {
        super();
    }

    @Override
    public ItemStack onItemRightClick(ItemStack p_77659_1_, World p_77659_2_, EntityPlayer p_77659_3_)
    {
        MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(p_77659_2_, p_77659_3_, false);

        if (movingobjectposition == null)
        {
            return p_77659_1_;
        }
        else
        {
            if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
            {
                int i = movingobjectposition.blockX;
                int j = movingobjectposition.blockY;
                int k = movingobjectposition.blockZ;

                if (!p_77659_2_.canMineBlock(p_77659_3_, i, j, k))
                {
                    return p_77659_1_;
                }

                if (movingobjectposition.sideHit == 0)
                {
                    --j;
                }

                if (movingobjectposition.sideHit == 1)
                {
                    ++j;
                }

                if (movingobjectposition.sideHit == 2)
                {
                    --k;
                }

                if (movingobjectposition.sideHit == 3)
                {
                    ++k;
                }

                if (movingobjectposition.sideHit == 4)
                {
                    --i;
                }

                if (movingobjectposition.sideHit == 5)
                {
                    ++i;
                }

                if (!p_77659_3_.canPlayerEdit(i, j, k, movingobjectposition.sideHit, p_77659_1_))
                {
                    return p_77659_1_;
                }

                if (this.tryPlaceContainedLiquid(p_77659_2_, i, j, k) && !p_77659_3_.capabilities.isCreativeMode)
                {
                    return new ItemStack(Items.bucket);
                }
            }
        }

        return p_77659_1_;
    }

    public boolean tryPlaceContainedLiquid(World p_77875_1_, int p_77875_2_, int p_77875_3_, int p_77875_4_)
    {
        Material material = p_77875_1_.getBlock(p_77875_2_, p_77875_3_, p_77875_4_).getMaterial();
        boolean flag = !material.isSolid();

        if (!p_77875_1_.isAirBlock(p_77875_2_, p_77875_3_, p_77875_4_) && !flag)
        {
            return false;
        }
        else
        {
            if (!p_77875_1_.isRemote && flag && !material.isLiquid())
            {
                p_77875_1_.func_147480_a(p_77875_2_, p_77875_3_, p_77875_4_, true);
            }

            p_77875_1_.setBlock(p_77875_2_, p_77875_3_, p_77875_4_, ABO.blockLiquidXP, 0, 3);

            return true;
        }

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
