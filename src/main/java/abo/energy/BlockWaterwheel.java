package abo.energy;

import java.text.DecimalFormat;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
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

public class BlockWaterwheel extends BlockBuildCraft implements ICustomHighlight {

	private static final AxisAlignedBB[][]	boxes	= {
			{ AxisAlignedBB.getBoundingBox(-2, -1, 0, 3, 2, 1), AxisAlignedBB.getBoundingBox(-1, -2, 0, 2, -1, 1),
			AxisAlignedBB.getBoundingBox(-1, 2, 0, 2, 3, 1) },
			{ AxisAlignedBB.getBoundingBox(0, -1, -2, 1, 2, 3), AxisAlignedBB.getBoundingBox(0, -2, -1, 1, -1, 2),
			AxisAlignedBB.getBoundingBox(0, 2, -1, 1, 3, 2) } };

	private static IIcon					texture;

	private float							scalar	= 1;

	public BlockWaterwheel() {
		super(Material.iron);
		setBlockName("waterwheelBlock");
	}

	public BlockWaterwheel(float s) {
		this();
		scalar = s;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int side, float par7,
			float par8, float par9) {

		TileEntity tile = world.getTileEntity(i, j, k);

		if (tile instanceof TileWaterwheel) {
			if (!world.isRemote) {
				player.addChatComponentMessage(new ChatComponentText("Current Waterwheel Output is "
						+ new DecimalFormat("##0.0##").format(((TileWaterwheel) tile).realCurrentOutput / 1000)
						+ "RF/t"));
				player.addChatComponentMessage(new ChatComponentText("Target Output is "
						+ new DecimalFormat("##0.0##").format(((TileWaterwheel) tile).TARGET_OUTPUT / 1000) + "RF/t"));
			}
			return ((TileWaterwheel) tile).onBlockActivated(player, ForgeDirection.getOrientation(side));
		}

		return false;
	}

	/**
	 * Gets an item for the block being called on. Args: world, x, y, z
	 */
	@SideOnly(Side.CLIENT)
	public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
		return ABO.waterwheelItem;
	}

	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return ABO.waterwheelItem;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		texture = par1IconRegister.registerIcon("additional-buildcraft-objects:waterwheelIcon");
	}

	@Override
	public int getRenderType() {
		return BuildCraftCore.blockByEntityModel;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileWaterwheel(scalar);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return false;
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile instanceof TileWaterwheel) {
			return ((TileWaterwheel) tile).switchOrientation(false);
		} else {
			return false;
		}
	}

	@Override
	public AxisAlignedBB[] getBoxes(World world, int i, int j, int k, EntityPlayer player) {
		int l = world.getBlockMetadata(i, j, k);
		AxisAlignedBB[] box = new AxisAlignedBB[boxes[l].length];
		for (int m = 0; m < boxes[l].length; m++) {
			box[m] = boxes[l][m];
		}
		return box;
	}

	@Override
	public double getExpansion() {
		return 0;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (!checkBBClear(world, x, y, z, world.getBlockMetadata(x, y, z))) {
			this.dropBlockAsItemWithChance(world, x, y, z, 0, 1, 0);
			return;
		}
		try {
			TileWaterwheel tile = (TileWaterwheel) world.getTileEntity(x, y, z);
			tile.onNeighborBlockChange(world, x, y, z, block);

		} catch (Exception e) {};
	}

	private boolean checkBBClear(World world, int i, int j, int k, int l) {
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
					if (block != Blocks.air && block != ABO.blockNullCollide && !(block instanceof IFluidBlock)
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
					if (block != Blocks.air && block != ABO.blockNullCollide && !(block instanceof IFluidBlock)
							&& !(block instanceof BlockFluidBase) && !(block instanceof BlockLiquid)
							&& !block.getMaterial().isLiquid() && !block.getMaterial().isReplaceable()) flag = false;
				}
			}
		}
		return flag;
	}
	
	public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int l)
    {
        return this.canPlaceBlockAt(world, x, y, z) && checkBBClear(world, x, y, z, l);
    }

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return texture;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return null;
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
