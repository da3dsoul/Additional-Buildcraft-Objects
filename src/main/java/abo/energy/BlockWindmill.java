package abo.energy;

import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import abo.ABO;
import buildcraft.BuildCraftCore;
import buildcraft.core.BlockBuildCraft;
import buildcraft.core.CreativeTabBuildCraft;
import buildcraft.core.ICustomHighlight;
import buildcraft.core.IItemPipe;

public class BlockWindmill extends BlockConstantPowerProvider {

	public BlockWindmill() {
		super();
		setBlockName("windmillBlock");
		setCreativeTab(CreativeTabBuildCraft.BLOCKS.get());

        boxes	= new AxisAlignedBB[][] {{
                AxisAlignedBB.getBoundingBox(0.375, 0, 0.375, 0.625, 0.8125, 0.625),
                AxisAlignedBB.getBoundingBox(0, 0.25, 0.25, 0.375, 0.75, 0.75),
                AxisAlignedBB.getBoundingBox(0.625, 0.25, 0.0625, 0.75, 1.125, 0.9375) }};
	}
	
	public BlockWindmill(double s)
	{
		this();
		scalar = s;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileWindmill(scalar);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addCollisionBoxesToList(World wrd, int x, int y, int z, AxisAlignedBB mask, List list, Entity ent) {
		for (AxisAlignedBB aabb : boxes[0]) {
			AxisAlignedBB aabbTmp = aabb.getOffsetBoundingBox(x, y, z);
			if (mask.intersectsWith(aabbTmp)) {
				list.add(aabbTmp);
			}
		}
	}

	@Override
	public AxisAlignedBB[] getBoxes(World wrd, int x, int y, int z, EntityPlayer player) {
		return boxes[0];
	}

	@Override
	public double getExpansion() {
		return 0.0075;
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World wrd, int x, int y, int z, Vec3 origin, Vec3 direction) {
		MovingObjectPosition closest = null;
		for (AxisAlignedBB aabb : boxes[0]) {
			MovingObjectPosition mop = aabb.getOffsetBoundingBox(x, y, z).calculateIntercept(origin, direction);
			if (mop != null) {
				if (closest != null && mop.hitVec.distanceTo(origin) < closest.hitVec.distanceTo(origin)) {
					closest = mop;
				} else {
					closest = mop;
				}
			}
		}

		if (closest != null) {
			closest.blockX = x;
			closest.blockY = y;
			closest.blockZ = z;
		}
		return closest;
	}

    @Override
	protected boolean checkBBClear(World world, int x, int y, int z, int l) {
		if (world.getBlock(x, y - 1, z) != Blocks.fence && world.getBlock(x, y - 1, z) != Blocks.nether_brick_fence)
			return false;
		if (world.getBlock(x, y - 2, z) != Blocks.fence && world.getBlock(x, y - 2, z) != Blocks.nether_brick_fence)
			return false;
		if (world.getBlock(x, y + 1, z).getMaterial() != Material.air && world.getBlock(x, y + 1, z) != ABO.blockNull) return false;
		return true;
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		
		if(world.getBlock(i, j + 1, k).getMaterial() == Material.air)
		{
			world.setBlock(i, j + 1, k, ABO.blockNull);
		}
	}

	@Override
	public void onBlockPreDestroy(World world, int i, int j, int k, int oldMeta) {
		if(world.getBlock(i, j + 1, k) == ABO.blockNull) world.setBlock(i, j + 1, k, Blocks.air);
	}

}
