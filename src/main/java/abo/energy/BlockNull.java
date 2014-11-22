package abo.energy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockNull extends Block {

	public BlockNull() {
		super(Material.rock);
	}
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World p_149668_1_, int p_149668_2_, int p_149668_3_,
			int p_149668_4_) {return null;}
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World p_149633_1_, int p_149633_2_, int p_149633_3_,
			int p_149633_4_) {return null;}
	@Override
	public boolean canCollideCheck(int p_149678_1_, boolean p_149678_2_) {return false;}
	@Override
	public boolean isOpaqueCube() {return false;}
	@Override
	public boolean renderAsNormalBlock() {return false;}
	
	
	
}
