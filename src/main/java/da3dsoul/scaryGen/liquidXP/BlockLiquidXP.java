package da3dsoul.scaryGen.liquidXP;

import abo.ABO;
import buildcraft.api.core.BlockIndex;
import buildcraft.core.utils.BlockUtils;
import mods.immibis.lxp.LiquidXPMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.*;

public class BlockLiquidXP extends BlockFluidClassic {

    public static BlockLiquidXP init() {
        return new BlockLiquidXP(LiquidXPMod.fluid);
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
        return LiquidXPMod.fluid.getIcon((World)world,x,y,z);
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
        if (world.getBlock(x, y + 1, z).getMaterial() == Material.air && !world.getBlock(x, y + 1, z).isOpaqueCube())
        {
            if (rand.nextInt(30) == 0)
            {
                if(world.getBlockMetadata(x,y,z) == 0) {
                    EntityXPOrb entity = new EntityXPOrb(world, x + 0.5, y + 0.5, z + 0.5, 5);
                    entity.motionY += 0.5;
                    entity.xpOrbAge = 5900;
                    if(!world.isRemote) world.spawnEntityInWorld(entity);
                    if(!world.isRemote) world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.orb", 0.1F, 0.5F * ((rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.8F));
                }
            }
            world.scheduleBlockUpdateWithPriority(x,y,z,this, 200,2);
        }
        super.updateTick(world, x, y, z, rand);
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

    public boolean useXP(World world, int x, int y, int z) {
        TreeMap<Integer, Deque<BlockIndex>> pumpLayerQueues= new TreeMap();
        int numFluidBlocksFound = 0;
        if(!world.isRemote) {
            BlockIndex index = this.getNextIndexToPump(world, pumpLayerQueues, x, y, z, numFluidBlocksFound, false);
            FluidStack fluidToPump = index != null? BlockUtils.drainBlock(world, index.x, index.y, index.z, false):null;
            if(fluidToPump != null) {
                if(fluidToPump.getFluid() == LiquidXPMod.fluid || numFluidBlocksFound < 9) {
                    index = this.getNextIndexToPump(world, pumpLayerQueues, x, y, z, numFluidBlocksFound, true);
                    BlockUtils.drainBlock(world, index.x, index.y, index.z, true);
                    world.notifyBlocksOfNeighborChange(index.x,index.y,index.z, Blocks.air);
                    return true;
                }
            }
        }

        return false;
    }

    private BlockIndex getNextIndexToPump(World world,TreeMap<Integer, Deque<BlockIndex>> pumpLayerQueues, int x, int y, int z, int numFluidBlocksFound, boolean remove) {
        this.rebuildQueue(world,pumpLayerQueues,x, y, z, numFluidBlocksFound);
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
}
