package abo.energy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockNullCollide extends Block {

	public BlockNullCollide() {
		super(Material.rock);
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World p_149731_1_, int p_149731_2_, int p_149731_3_, int p_149731_4_,
			Vec3 p_149731_5_, Vec3 p_149731_6_) {
		return null;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World p_149633_1_, int p_149633_2_, int p_149633_3_,
			int p_149633_4_) {return null;}
	@Override
	public boolean isOpaqueCube() {return false;}
	@Override
	public boolean renderAsNormalBlock() {return false;}

	public int getRenderType() {
		return -1;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return Blocks.planks.getIcon(side,meta);
	}

	@Override
	public boolean canPlaceBlockAt(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_) {
		return false;
	}

	@Override
	public boolean canPlaceBlockOnSide(World p_149707_1_, int p_149707_2_, int p_149707_3_, int p_149707_4_, int p_149707_5_) {
		return false;
	}
}
