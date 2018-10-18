package da3dsoul.scaryGen.liquidXP;

import abo.ABO;
import buildcraft.api.core.BlockIndex;
import buildcraft.core.lib.utils.BlockUtils;
import cpw.mods.fml.common.registry.GameRegistry;
import mods.immibis.lxp.LiquidXPMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class BlockLiquidXP extends BlockFluidClassic {

    public static void preinit() {
        try {
            LiquidXPMod.fluid.setLuminosity(15);
            ABO.blockLiquidXP = new BlockLiquidXP(LiquidXPMod.fluid);
            LiquidXPMod.mbPerXp = 1;

        }catch(Throwable e){
            e.printStackTrace();
            ABO.blockLiquidXP = null;
        }
    }

    public static void init() {
        try {
            FluidContainerRegistry.registerFluidContainer(new FluidStack(LiquidXPMod.fluid, 1000), new ItemStack(LiquidXPMod.bucket), new ItemStack(Items.bucket));
        }catch(Throwable e){
            e.printStackTrace();
            ABO.blockLiquidXP = null;
        }
    }

    public BlockLiquidXP(Fluid fluid) {
        super(fluid, Material.water);
        this.setLightLevel(1.0f);
        this.setLightOpacity(0);
        this.tickRate = 8;
    }

    /**
     * Gets the block's texture. Args: side, meta
     *
     * @param p_149691_1_
     * @param p_149691_2_
     */
    @Override
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return LiquidXPMod.fluid.getStillIcon();
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        try {
            if (world instanceof World) return LiquidXPMod.fluid.getIcon((World) world, x, y, z);
        } catch (ClassCastException ex) {
        }

        return getIcon(side, world.getBlockMetadata(x,y,z));
    }

    public int getGreatestQuantaValue(Entity entity) {
        World world = entity.worldObj;
        AxisAlignedBB aaBB = entity.boundingBox.expand(-0.1D, -0.4D, -0.1D);
        int i = MathHelper.floor_double(aaBB.minX);
        int i1 = MathHelper.floor_double(aaBB.maxX + 1.0D);
        int j = MathHelper.floor_double(aaBB.minY);
        int j1 = MathHelper.floor_double(aaBB.maxY + 1.0D);
        int k = MathHelper.floor_double(aaBB.minZ);
        int k1 = MathHelper.floor_double(aaBB.maxZ + 1.0D);
        int greatestQuanta = 0;

        for (int i2 = i; i2 < i1; ++i2) {
            for (int j2 = j; j2 < j1; ++j2) {
                for (int k2 = k; k2 < k1; ++k2) {
                    if (world.getBlock(i2, j2, k2) == ABO.blockLiquidXP) {
                        greatestQuanta = Math.max(greatestQuanta, ((BlockLiquidXP) ABO.blockLiquidXP).getQuantaValue(world, i2, j2, k2));
                    }
                }
            }
        }

        return greatestQuanta;
    }

    public boolean isInXP(Entity entity) {
        World world = entity.worldObj;
        AxisAlignedBB aaBB = entity.boundingBox.expand(-0.1D, -0.4D, -0.1D);
        int i = MathHelper.floor_double(aaBB.minX);
        int j = MathHelper.floor_double(aaBB.maxX + 1.0D);
        int k = MathHelper.floor_double(aaBB.minY);
        int l = MathHelper.floor_double(aaBB.maxY + 1.0D);
        int i1 = MathHelper.floor_double(aaBB.minZ);
        int j1 = MathHelper.floor_double(aaBB.maxZ + 1.0D);

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    if (world.getBlock(k1, l1, i2) == ABO.blockLiquidXP) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Ticks the block if it's been scheduled
     *
     * @param world
     * @param x
     * @param y
     * @param z
     * @param rand
     */
    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        super.updateTick(world, x, y, z, rand);
        if(!ABO.spawnOrbs) return;

        if (world.getBlock(x, y + 1, z).getMaterial() == Material.air && !world.getBlock(x, y + 1, z).isOpaqueCube()) {
            if (rand.nextInt(ABO.orbSpawnChance) == 0) {
                if (world.getBlockMetadata(x, y, z) == 0) {
                    EntityXPOrb entity = new EntityXPOrb(world, x + 0.5, y + 0.5, z + 0.5, ABO.orbSize);
                    entity.motionY += 0.5;
                    entity.xpOrbAge = 6000 - ABO.orbLifetime;
                    if (!world.isRemote) world.spawnEntityInWorld(entity);
                    if (!world.isRemote)
                        world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.orb", 0.1F, 0.5F * ((rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.8F));
                }
            }
            world.scheduleBlockUpdateWithPriority(x, y, z, this, 200, 2);
        }
    }

    public int getXPforOneBlock() {
        return 1000;
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     *
     * @param world
     * @param x
     * @param y
     * @param z
     */
    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
    }

    // TODO Drain Pipe

    public boolean useXP(World world, int x, int y, int z) {
        TreeMap<Integer, Deque<BlockIndex>> pumpLayerQueues = new TreeMap();
        int numFluidBlocksFound = 0;
        if (!world.isRemote) {
            BlockIndex index = this.getNextIndexToPump(world, pumpLayerQueues, x, y, z, numFluidBlocksFound, false);
            FluidStack fluidToPump = index != null ? BlockUtils.drainBlock(world, index.x, index.y, index.z, false) : null;
            if (fluidToPump != null) {
                if (fluidToPump.getFluid() == LiquidXPMod.fluid || numFluidBlocksFound < 9) {
                    index = this.getNextIndexToPump(world, pumpLayerQueues, x, y, z, numFluidBlocksFound, true);
                    BlockUtils.drainBlock(world, index.x, index.y, index.z, true);
                    world.notifyBlocksOfNeighborChange(index.x, index.y, index.z, Blocks.air);
                    return true;
                }
            }
        }

        return false;
    }

    public int getLevelTarget(World world, int x, int y, int z, int quanta) {
        int L = LiquidXPMod.xpToLevel((int) LiquidXPMod.convertMBToXP(1000));
        return L * (quanta / quantaPerBlock);
    }

    private BlockIndex getNextIndexToPump(World world,TreeMap<Integer, Deque<BlockIndex>> pumpLayerQueues, int x, int y, int z, int numFluidBlocksFound, boolean remove) {
        this.rebuildQueue(world, pumpLayerQueues, x, y, z, numFluidBlocksFound);
        try {
            Deque topLayer = (Deque) pumpLayerQueues.lastEntry().getValue();
            if (topLayer != null) {
                if (topLayer.isEmpty()) {
                    pumpLayerQueues.pollLastEntry();
                }

                if (remove) {
                    BlockIndex index = (BlockIndex) topLayer.pollLast();
                    return index;
                } else {
                    return (BlockIndex) topLayer.peekLast();
                }
            } else {
                return null;
            }
        }catch(NullPointerException e) { return null;}
    }

    private Deque<BlockIndex> getLayerQueue(TreeMap<Integer, Deque<BlockIndex>> pumpLayerQueues, int layer) {
        Deque pumpQueue = (Deque)pumpLayerQueues.get(Integer.valueOf(layer));
        if(pumpQueue == null) {
            pumpQueue = new LinkedList();
            pumpLayerQueues.put(Integer.valueOf(layer), pumpQueue);
        }

        return (Deque)pumpQueue;
    }

    public void rebuildQueue(World world, TreeMap<Integer, Deque<BlockIndex>> pumpLayerQueues, int xCoord, int yCoord, int zCoord, int numFluidBlocksFound) {
        numFluidBlocksFound = 0;
        pumpLayerQueues.clear();
        int x = xCoord;
        int y = yCoord;
        int z = zCoord;
        Fluid pumpingFluid = BlockUtils.getFluid(world.getBlock(x, y, z));
        if(pumpingFluid != null) {
            if(pumpingFluid == LiquidXPMod.fluid) {
                HashSet visitedBlocks = new HashSet();
                LinkedList fluidsFound = new LinkedList();
                this.queueForPumping(world, pumpLayerQueues, xCoord, yCoord, zCoord, x, y, z, visitedBlocks, fluidsFound, pumpingFluid, numFluidBlocksFound);

                while(!fluidsFound.isEmpty()) {
                    LinkedList fluidsToExpand = fluidsFound;
                    fluidsFound = new LinkedList();
                    Iterator var8 = fluidsToExpand.iterator();

                    while(var8.hasNext()) {
                        BlockIndex index = (BlockIndex)var8.next();
                        this.queueForPumping(world, pumpLayerQueues, xCoord, yCoord, zCoord, index.x, index.y + 1, index.z, visitedBlocks, fluidsFound, pumpingFluid, numFluidBlocksFound);
                        this.queueForPumping(world, pumpLayerQueues, xCoord, yCoord, zCoord, index.x + 1, index.y, index.z, visitedBlocks, fluidsFound, pumpingFluid, numFluidBlocksFound);
                        this.queueForPumping(world, pumpLayerQueues, xCoord, yCoord, zCoord, index.x - 1, index.y, index.z, visitedBlocks, fluidsFound, pumpingFluid, numFluidBlocksFound);
                        this.queueForPumping(world, pumpLayerQueues, xCoord, yCoord, zCoord, index.x, index.y, index.z + 1, visitedBlocks, fluidsFound, pumpingFluid, numFluidBlocksFound);
                        this.queueForPumping(world, pumpLayerQueues, xCoord, yCoord, zCoord, index.x, index.y, index.z - 1, visitedBlocks, fluidsFound, pumpingFluid, numFluidBlocksFound);
                    }
                }

            }
        }
    }

    public void queueForPumping(World world, TreeMap<Integer, Deque<BlockIndex>> pumpLayerQueues, int xCoord, int yCoord, int zCoord, int x, int y, int z, Set<BlockIndex> visitedBlocks, Deque<BlockIndex> fluidsFound, Fluid pumpingFluid, int numFluidBlocksFound) {
        BlockIndex index = new BlockIndex(x, y, z);
        if(visitedBlocks.add(index)) {
            if((x - xCoord) * (x - xCoord) + (z - zCoord) * (z - zCoord) > 4096) {
                return;
            }

            Block block = world.getBlock(x, y, z);
            if(BlockUtils.getFluid(block) == pumpingFluid) {
                fluidsFound.add(index);
            }

            if(this.canDrainBlock(world, block, x, y, z, pumpingFluid)) {
                this.getLayerQueue(pumpLayerQueues, y).add(index);
                ++numFluidBlocksFound;
            }
        }

    }

    private boolean canDrainBlock(World world, Block block, int x, int y, int z, Fluid fluid) {
        FluidStack fluidStack = BlockUtils.drainBlock(block, world, x, y, z, false);
        return fluidStack != null && fluidStack.amount > 0?fluidStack.getFluid() == fluid:false;
    }

    public static boolean tryToPlaceFromBucket(EntityPlayer player, int x, int y, int z, int face) {
        ItemStack itemStack = player.getCurrentEquippedItem();
        World world = player.worldObj;
        if(itemStack != null && itemStack.getItem() == LiquidXPMod.bucket) {
            if(world.getBlock(x,y,z).getMaterial().isReplaceable()){
                if(world.setBlock(x,y,z, ABO.blockLiquidXP, 0, 3)) {
                    if(!player.capabilities.isCreativeMode) player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.bucket));
                    if(!world.isRemote) player.swingItem();
                    return true;
                }
            } else {
                if(face < 0 || face > 5) return false;
                ForgeDirection dir = ForgeDirection.values()[face];
                if(world.setBlock(x+dir.offsetX,y+dir.offsetY,z+dir.offsetZ, ABO.blockLiquidXP, 0, 3)) {
                    if(!player.capabilities.isCreativeMode) player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.bucket));
                    if(!world.isRemote) player.swingItem();
                    return true;
                }
            }
        }
        return false;
    }

    protected static MovingObjectPosition getMovingObjectPositionFromPlayer(World p_77621_1_, EntityPlayer p_77621_2_, boolean p_77621_3_)
    {
        float f = 1.0F;
        float f1 = p_77621_2_.prevRotationPitch + (p_77621_2_.rotationPitch - p_77621_2_.prevRotationPitch) * f;
        float f2 = p_77621_2_.prevRotationYaw + (p_77621_2_.rotationYaw - p_77621_2_.prevRotationYaw) * f;
        double d0 = p_77621_2_.prevPosX + (p_77621_2_.posX - p_77621_2_.prevPosX) * (double)f;
        double d1 = p_77621_2_.prevPosY + (p_77621_2_.posY - p_77621_2_.prevPosY) * (double)f + (double)(p_77621_1_.isRemote ? p_77621_2_.getEyeHeight() - p_77621_2_.getDefaultEyeHeight() : p_77621_2_.getEyeHeight()); // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
        double d2 = p_77621_2_.prevPosZ + (p_77621_2_.posZ - p_77621_2_.prevPosZ) * (double)f;
        Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 5.0D;
        if (p_77621_2_ instanceof EntityPlayerMP)
        {
            d3 = ((EntityPlayerMP)p_77621_2_).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3 vec31 = vec3.addVector((double)f7 * d3, (double)f6 * d3, (double)f8 * d3);
        return p_77621_1_.func_147447_a(vec3, vec31, p_77621_3_, !p_77621_3_, false);
    }

    public static boolean onTryToUseBottle(EntityPlayer player, int x, int y, int z, int face) {
        ItemStack itemStack = player.getCurrentEquippedItem();
        World world = player.worldObj;
        if(itemStack != null && itemStack.getItem() == Items.glass_bottle) {
            MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, player, true);

            if (movingobjectposition == null)
            {
                return false;
            }
            else {
                if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    int i = movingobjectposition.blockX;
                    int j = movingobjectposition.blockY;
                    int k = movingobjectposition.blockZ;

                    if (!world.canMineBlock(player, i, j, k)) {
                        return false;
                    }

                    if (!player.canPlayerEdit(i, j, k, movingobjectposition.sideHit, itemStack)) {
                        return false;
                    }

                    if (world.getBlock(i, j, k) == ABO.blockLiquidXP) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void initAprilFools(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 0) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            //get current date time with Date()
            Date date = new Date();
            String dateString = dateFormat.format(date).substring(5, 10);
            //ABO.aboLog.info("The date is " + dateString);
            if (dateString.equals("04/01")) {
                LiquidXPMod.fluid.setIcons(event.map.registerIcon("Additional-Buildcraft-Objects:liquid"));
            }
        }
    }
}
