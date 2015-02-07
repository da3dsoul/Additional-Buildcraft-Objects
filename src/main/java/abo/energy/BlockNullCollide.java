package abo.energy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
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
	
}
