package abo.energy;

import java.text.DecimalFormat;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.util.ForgeDirection;
import abo.ABO;
import buildcraft.BuildCraftCore;
import buildcraft.core.BlockBuildCraft;
import buildcraft.core.CreativeTabBuildCraft;
import buildcraft.core.ICustomHighlight;
import buildcraft.core.IItemPipe;

public class BlockWindmill extends BlockBuildCraft implements ICustomHighlight {

	private static final AxisAlignedBB[]	boxes	= {
			AxisAlignedBB.getBoundingBox(0.375, 0, 0.375, 0.625, 0.8125, 0.625),
			AxisAlignedBB.getBoundingBox(0, 0.25, 0.25, 0.375, 0.75, 0.75),
			AxisAlignedBB.getBoundingBox(0.625, 0.25, 0.0625, 0.75, 1.125, 0.9375) };

	private static IIcon					texture;
	
	private float scalar = 1;

	public BlockWindmill() {
		super(Material.iron);
		setBlockName("windmillBlock");
		setCreativeTab(CreativeTabBuildCraft.BLOCKS.get());
	}
	
	public BlockWindmill(float s)
	{
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

		// REMOVED DUE TO CREATIVE ENGINE REQUIREMENTS - dmillerw
		// Drop through if the player is sneaking
		// if (player.isSneaking()) {
		// return false;
		// }

		// Do not open guis when having a pipe in hand
		if (player.getCurrentEquippedItem() != null) {
			if (player.getCurrentEquippedItem().getItem() instanceof IItemPipe) { return false; }
		}

		if (tile instanceof TileWindmill) {
			if (!world.isRemote) {
				player.addChatComponentMessage(new ChatComponentText("Current Windmill Output is "
						+ new DecimalFormat("##0.0##").format(((TileWindmill) tile).realCurrentOutput/1000) + "RF/t"));
				player.addChatComponentMessage(new ChatComponentText("Target Output is "
						+ new DecimalFormat("##0.0##").format(((TileWindmill) tile).TARGET_OUTPUT/1000) + "RF/t"));
			}
			return ((TileWindmill) tile).onBlockActivated(player, ForgeDirection.getOrientation(side));
		}

		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister) {
		texture = par1IconRegister.registerIcon("additional-buildcraft-objects:windmillIcon");
	}

	@Override
	public int getRenderType() {
		return BuildCraftCore.blockByEntityModel;
	}

	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileWindmill(scalar);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return false;
	}

	@Override
	public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
		TileEntity tile = world.getTileEntity(x, y, z);

		if (tile instanceof TileWindmill) {
			return ((TileWindmill) tile).switchOrientation(false);
		} else {
			return false;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addCollisionBoxesToList(World wrd, int x, int y, int z, AxisAlignedBB mask, List list, Entity ent) {
		for (AxisAlignedBB aabb : boxes) {
			AxisAlignedBB aabbTmp = aabb.getOffsetBoundingBox(x, y, z);
			if (mask.intersectsWith(aabbTmp)) {
				list.add(aabbTmp);
			}
		}
	}

	@Override
	public AxisAlignedBB[] getBoxes(World wrd, int x, int y, int z, EntityPlayer player) {
		return boxes;
	}

	@Override
	public double getExpansion() {
		return 0.0075;
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World wrd, int x, int y, int z, Vec3 origin, Vec3 direction) {
		MovingObjectPosition closest = null;
		for (AxisAlignedBB aabb : boxes) {
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
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (!checkBBClear(world, x, y, z)) { world.func_147480_a(x, y, z, true); return;}
		try {
			TileWindmill tile = (TileWindmill) world.getTileEntity(x, y, z);
			tile.onNeighborBlockChange(world, x, y, z, block);

		} catch (Exception e) {};
	}

	private boolean checkBBClear(World world, int x, int y, int z) {
		if (world.getBlock(x, y - 1, z) != Blocks.fence && world.getBlock(x, y - 1, z) != Blocks.nether_brick_fence)
			return false;
		if (world.getBlock(x, y - 2, z) != Blocks.fence && world.getBlock(x, y - 2, z) != Blocks.nether_brick_fence)
			return false;
		if (world.getBlock(x, y + 1, z) != Blocks.air && world.getBlock(x, y + 1, z) != ABO.blockNull) return false;
		return true;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return super.canPlaceBlockAt(world, x, y, z) && checkBBClear(world, x, y, z);
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
		
		if(world.getBlock(i, j + 1, k) == Blocks.air)
		{
			world.setBlock(i, j + 1, k, ABO.blockNull);
		}
	}

	@Override
	public void onBlockPreDestroy(World world, int i, int j, int k, int oldMeta) {
		if(world.getBlock(i, j + 1, k) == ABO.blockNull) world.setBlock(i, j + 1, k, Blocks.air);
	}

	

}
