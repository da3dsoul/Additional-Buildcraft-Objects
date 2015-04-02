package abo.energy;

import buildcraft.BuildCraftCore;
import buildcraft.core.BlockBuildCraft;
import buildcraft.core.ICustomHighlight;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public abstract class BlockConstantPowerProvider extends BlockBuildCraft implements ICustomHighlight {

    protected AxisAlignedBB[][]	boxes = {{AxisAlignedBB.getBoundingBox(0,0,0,1,1,1)}};

    protected float scalar = 1;

    protected static IIcon					texture;

    protected BlockConstantPowerProvider() {
        super(Material.iron);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        texture = par1IconRegister.registerIcon("additional-buildcraft-objects:waterwheelIcon");
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int side, float par7,
                                    float par8, float par9) {

        TileEntity tile = world.getTileEntity(i, j, k);

        if (tile instanceof TileConstantPowerProvider) {
            if (!world.isRemote) {
                player.addChatComponentMessage(new ChatComponentText("Current " + this.getClass().getSimpleName().substring(6) + " Output is "
                        + new DecimalFormat("##0.0##").format(((TileConstantPowerProvider) tile).realCurrentOutput / 1000)
                        + "RF/t"));
                player.addChatComponentMessage(new ChatComponentText("Target Output is "
                        + new DecimalFormat("##0.0##").format(((TileConstantPowerProvider) tile).TARGET_OUTPUT / 1000) + "RF/t"));
            }
            return ((TileConstantPowerProvider) tile).onBlockActivated(player, ForgeDirection.getOrientation(side));
        }

        return false;
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
        return getItemDropped(0, world.rand, 0);
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     *
     * @param p_149692_1_
     */
    @Override
    public int damageDropped(int p_149692_1_) {
        return 0;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return BuildCraftCore.blockByEntityModel;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return false;
    }

    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
        TileEntity tile = world.getTileEntity(x, y, z);

        if (tile instanceof TileConstantPowerProvider) {
            return ((TileConstantPowerProvider) tile).switchOrientation(false);
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

    /**
     * Metadata and fortune sensitive version, this replaces the old (int meta, Random rand)
     * version in 1.1.
     *
     * @param meta    Blocks Metadata
     * @param fortune Current item fortune level
     * @param random  Random number generator
     * @return The number of items to drop
     */
    @Override
    public int quantityDropped(int meta, int fortune, Random random) {
        return 1;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        int l = world.getBlockMetadata(x, y, z);
        if (!checkBBClear(world, x, y, z, l)) {
            world.func_147480_a(x,y,z,false);
            if(!world.isRemote) dropBlockAsItem(world,x,y,z, l, 0);
            return;
        }
        try {
            TileConstantPowerProvider tile = (TileConstantPowerProvider) world.getTileEntity(x, y, z);
            tile.onNeighborBlockChange(world, x, y, z, block);

        } catch (Exception e) {};
    }

    protected boolean checkBBClear(World world, int i, int j, int k, int l) {
        return true;
    }

    public boolean canPlaceBlockOnSide(World world, int x, int y, int z, int l)
    {
        return this.canPlaceBlockAt(world, x, y, z) && checkBBClear(world, x, y, z, l);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return texture;
    }

    /**
     * Return true if a player with Silk Touch can harvest this block directly, and not its normal drops.
     */
    @Override
    protected boolean canSilkHarvest() {
        return false;
    }

}
