package abo.energy;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.IFluidBlock;
import abo.ABO;
import buildcraft.BuildCraftCore;
import buildcraft.core.BlockBuildCraft;
import buildcraft.core.ICustomHighlight;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWaterwheel extends BlockConstantPowerProvider {

	public BlockWaterwheel() {
		super();
		setBlockName("waterwheelBlock");
        setCreativeTab(null);

        boxes	= new AxisAlignedBB[][] {
                { AxisAlignedBB.getBoundingBox(-2, -1, 0, 3, 2, 1), AxisAlignedBB.getBoundingBox(-1, -2, 0, 2, -1, 1),
                        AxisAlignedBB.getBoundingBox(-1, 2, 0, 2, 3, 1) },
                { AxisAlignedBB.getBoundingBox(0, -1, -2, 1, 2, 3), AxisAlignedBB.getBoundingBox(0, -2, -1, 1, -1, 2),
                        AxisAlignedBB.getBoundingBox(0, 2, -1, 1, 3, 2) } };
	}

	public BlockWaterwheel(double s) {
		this();
		scalar = s;
	}

	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return ABO.waterwheelItem;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileWaterwheel(scalar);
	}

	protected boolean checkBBClear(World world, int i, int j, int k, int l) {
		boolean flag = true;
		Block block;
		if (l == 0) {
			for (int x = -3; x <= 3; x++) {
				for (int y = -3; y <= 3; y++) {
					if (x == -3 && y == -3) continue;
					if (x == -3 && y == 3) continue;
					if (x == 3 && y == -3) continue;
					if (x == 3 && y == 3) continue;
					if (x == 0 && y == 0) continue;
					block = world.getBlock(i + x, j + y, k);
					if (block.getMaterial() != Material.air && block != ABO.blockNullCollide && !(block instanceof IFluidBlock)
							&& !(block instanceof BlockFluidBase) && !(block instanceof BlockLiquid)
							&& !block.getMaterial().isLiquid() && !block.getMaterial().isReplaceable()) flag = false;
				}
			}
		} else if (l == 1) {
			for (int x = -3; x <= 3; x++) {
				for (int y = -3; y <= 3; y++) {
					if (x == -3 && y == -3) continue;
					if (x == -3 && y == 3) continue;
					if (x == 3 && y == -3) continue;
					if (x == 3 && y == 3) continue;
					if (x == 0 && y == 0) continue;
					block = world.getBlock(i, j + y, k + x);
					if (block.getMaterial() != Material.air && block != ABO.blockNullCollide && !(block instanceof IFluidBlock)
							&& !(block instanceof BlockFluidBase) && !(block instanceof BlockLiquid)
							&& !block.getMaterial().isLiquid() && !block.getMaterial().isReplaceable()) flag = false;
				}
			}
		}
		return flag;
	}



	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		int l = world.getBlockMetadata(i, j, k);
		if (l == 0) {
			for (int x = -2; x <= 2; x++) {
				for (int y = -2; y <= 2; y++) {
					if (x == -2 && y == -2) continue;
					if (x == -2 && y == 2) continue;
					if (x == 2 && y == -2) continue;
					if (x == 2 && y == 2) continue;
					if (x == 0 && y == 0) continue;
					if (world.getBlock(i + x, j + y, k) != ABO.blockNullCollide)
						world.setBlock(i + x, j + y, k, ABO.blockNullCollide);
				}
			}
		} else if (l == 1) {
			for (int x = -2; x <= 2; x++) {
				for (int y = -2; y <= 2; y++) {
					if (x == -2 && y == -2) continue;
					if (x == -2 && y == 2) continue;
					if (x == 2 && y == -2) continue;
					if (x == 2 && y == 2) continue;
					if (x == 0 && y == 0) continue;
					if (world.getBlock(i, j + y, k + x) != ABO.blockNullCollide)
						world.setBlock(i, j + y, k + x, ABO.blockNullCollide);
				}
			}
		}
	}

    @Override
	public void onBlockPreDestroy(World world, int i, int j, int k, int oldMeta) {
		int l = world.getBlockMetadata(i, j, k);
		if (l == 0) {
			for (int x = -2; x <= 2; x++) {
				for (int y = -2; y <= 2; y++) {
					if (x == -2 && y == -2) continue;
					if (x == -2 && y == 2) continue;
					if (x == 2 && y == -2) continue;
					if (x == 2 && y == 2) continue;
					if (x == 0 && y == 0) continue;
					if (world.getBlock(i + x, j + y, k) == ABO.blockNullCollide)
						world.setBlock(i + x, j + y, k, Blocks.air);
				}
			}
		} else if (l == 1) {
			for (int x = -2; x <= 2; x++) {
				for (int y = -2; y <= 2; y++) {
					if (x == -2 && y == -2) continue;
					if (x == -2 && y == 2) continue;
					if (x == 2 && y == -2) continue;
					if (x == 2 && y == 2) continue;
					if (x == 0 && y == 0) continue;
					if (world.getBlock(i, j + y, k + x) == ABO.blockNullCollide)
						world.setBlock(i, j + y, k + x, Blocks.air);
				}
			}
		}
	}

}
