package da3dsoul.scaryGen.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class BlockNoCrossing extends Block {

    public BlockNoCrossing(Material p_i45394_1_) {
        super(p_i45394_1_);
    }

    @Override
    public void addCollisionBoxesToList(World world, int i, int j, int k, AxisAlignedBB alignedBB, List list, Entity entity) {
        if (entity instanceof EntityLivingBase && !(entity instanceof EntityPlayer))
        {
            for(int height = 0; height < 4; height++)
            {
                AxisAlignedBB axisalignedbb1 = AxisAlignedBB.getBoundingBox(i, j + height, k, i + 1, j + height + 1, k + 1);
                if (alignedBB.intersectsWith(axisalignedbb1))
                {
                    list.add(axisalignedbb1);
                }
            }
        } else
        {
            AxisAlignedBB axisalignedbb1 = AxisAlignedBB.getBoundingBox(i, j, k, i + 1, j + 1, k + 1);
            if (alignedBB.intersectsWith(axisalignedbb1))
            {
                list.add(axisalignedbb1);
            }
        }
    }
}
