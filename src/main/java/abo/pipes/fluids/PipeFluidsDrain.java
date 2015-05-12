package abo.pipes.fluids;

import abo.ABO;
import abo.PipeIcons;
import abo.actions.ActionSwitchOnPipe;
import abo.actions.ActionToggleOffPipe;
import abo.actions.ActionToggleOnPipe;
import abo.pipes.ABOPipe;
import buildcraft.BuildCraftCore;
import buildcraft.BuildCraftFactory;
import buildcraft.BuildCraftTransport;
import buildcraft.api.core.BlockIndex;
import buildcraft.api.core.IIconProvider;
import buildcraft.api.core.Position;
import buildcraft.api.core.SafeTimeTracker;
import buildcraft.api.power.IRedstoneEngineReceiver;
import buildcraft.api.statements.IActionInternal;
import buildcraft.api.statements.IStatement;
import buildcraft.api.tiles.IControllable;
import buildcraft.api.tiles.IHasWork;
import buildcraft.core.EntityBlock;
import buildcraft.core.RFBattery;
import buildcraft.core.TileBuffer;
import buildcraft.core.fluids.SingleUseTank;
import buildcraft.core.fluids.TankUtils;
import buildcraft.core.utils.BlockUtils;
import buildcraft.core.utils.Utils;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.PipeTransportFluids;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.gates.StatementSlot;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.*;

public class PipeFluidsDrain extends ABOPipe<PipeTransportFluidsReinforced> implements IHasWork {

    public static final int REBUID_DELAY = 512;
    public static int MAX_LIQUID = 16000;
    public SingleUseTank tank;
    private TreeMap<Integer, Deque<BlockIndex>> pumpLayerQueues;
    private SafeTimeTracker timer;
    private int tick;
    private int numFluidBlocksFound;

    private boolean			powered;
    private boolean			switched;
    private boolean			toggled;

    public PipeFluidsDrain(Item item) {
        super(new PipeTransportFluidsReinforced(),item);

        PipeTransportFluids.fluidCapacities.put(PipeFluidsDrain.class, Integer.valueOf(2 * FluidContainerRegistry.BUCKET_VOLUME));

        this.tank = new SingleUseTank("tank", MAX_LIQUID, null);
        this.pumpLayerQueues = new TreeMap();
        this.timer = new SafeTimeTracker(512L);
        this.tick = Utils.RANDOM.nextInt();
        this.numFluidBlocksFound = 0;
    }

    @Override
    public boolean blockActivated(EntityPlayer entityplayer) {
        if(tank.getFluid() != null) {
            ABO.aboLog.info("Tank cap " + tank.getFluidAmount());
            ABO.aboLog.info("Tank fluid " + tank.getFluid().getFluid().getName());
        }
        return super.blockActivated(entityplayer);
    }

    public boolean isPowered() {
        return powered || switched || toggled;
    }

    public void updateRedstoneCurrent() {
        boolean lastPowered = powered;


        powered = false;

        if (!powered)
            powered = container.getWorldObj().isBlockIndirectlyGettingPowered(container.xCoord, container.yCoord,
                    container.zCoord);

        if (lastPowered != powered) {
            this.container.scheduleNeighborChange();
            this.container.updateEntity();
        }
    }

    @Override
    protected void actionsActivated(Collection<StatementSlot> actions) {
        boolean lastSwitched = switched;
        boolean lastToggled = toggled;

        super.actionsActivated(actions);

        switched = false;
        // Activate the actions
        for (StatementSlot actionslot : actions) {
            IStatement i = actionslot.statement;
            if (i instanceof ActionSwitchOnPipe) {
                switched = false;
            } else if (i instanceof ActionToggleOnPipe) {
                toggled = false;
            } else if (i instanceof ActionToggleOffPipe) {
                toggled = true;
            }

        }
        if ((lastSwitched != switched) || (lastToggled != toggled)) {
            if (lastSwitched != switched && !switched) toggled = false;

            updateNeighbors(true);
        }
    }

    @Override
    public LinkedList<IActionInternal> getActions() {
        LinkedList<IActionInternal> actions = super.getActions();
        actions.add(ABO.actionSwitchOnPipe);
        actions.add(ABO.actionToggleOnPipe);
        actions.add(ABO.actionToggleOffPipe);
        return actions;
    }

    @Override
    public void onNeighborBlockChange(int blockId) {
        super.onNeighborBlockChange(blockId);
        updateRedstoneCurrent();
    }

    @Override
    public boolean canPipeConnect(TileEntity tile, ForgeDirection side) {
        return side == ForgeDirection.DOWN && super.canPipeConnect(tile, side);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        updateRedstoneCurrent();

        if(!this.container.getWorldObj().isRemote) {
            this.pushToConsumers();
            if(isPowered()) return;
            ++this.tick;
            if(this.tick % 8 == 0) {
                BlockIndex index = this.getNextIndexToPump(false);
                FluidStack fluidToPump = index != null? BlockUtils.drainBlock(this.container.getWorldObj(), index.x, index.y, index.z, false):null;
                if(fluidToPump != null) {
                    if(this.isFluidAllowed(fluidToPump.getFluid()) && this.tank.fill(fluidToPump, false) == fluidToPump.amount) {
                        index = this.getNextIndexToPump(true);
                        BlockUtils.drainBlock(this.container.getWorldObj(), index.x, index.y, index.z, true);

                    }
                } else if(this.tick % 128 == 0) {
                    this.rebuildQueue();
                    if(this.getNextIndexToPump(false) == null) {
                        if(this.isPumpableFluid(this.container.xCoord, this.container.yCoord + 1, this.container.zCoord)) {
                            return;
                        }

                        if(this.isBlocked(this.container.xCoord, this.container.yCoord + 1, this.container.zCoord)) {
                            return;
                        }
                    }
                }
            }
        }

    }

    private boolean isBlocked(int x, int y, int z) {
        Material mat = this.container.getWorldObj().getBlock(x, y, z).getMaterial();
        return mat.blocksMovement();
    }

    private void pushToConsumers() {
        FluidStack maxFromTank = this.tank.drain(MAX_LIQUID, false);
        if(maxFromTank != null && maxFromTank.getFluid() != null && maxFromTank.amount > 0) {
            TileEntity tile = this.container.getTile(ForgeDirection.DOWN);
            if(tile == null) return;
            if(!(tile instanceof IFluidHandler)) return;
            int amt = ((IFluidHandler) tile).fill(ForgeDirection.UP, maxFromTank, true);

            if(amt > 0) {
                maxFromTank = this.tank.drain(amt, true);
            }
        }
    }

    private BlockIndex getNextIndexToPump(boolean remove) {
        if(this.pumpLayerQueues.isEmpty()) {
            if(this.timer.markTimeIfDelay(this.container.getWorldObj())) {
                this.rebuildQueue();
            }

            return null;
        } else {
            Deque topLayer = (Deque)this.pumpLayerQueues.lastEntry().getValue();
            if(topLayer != null) {
                if(topLayer.isEmpty()) {
                    this.pumpLayerQueues.pollLastEntry();
                }

                if(remove) {
                    BlockIndex index = (BlockIndex)topLayer.pollLast();
                    return index;
                } else {
                    return (BlockIndex)topLayer.peekLast();
                }
            } else {
                return null;
            }
        }
    }

    private Deque<BlockIndex> getLayerQueue(int layer) {
        Deque pumpQueue = (Deque)this.pumpLayerQueues.get(Integer.valueOf(layer));
        if(pumpQueue == null) {
            pumpQueue = new LinkedList();
            this.pumpLayerQueues.put(Integer.valueOf(layer), pumpQueue);
        }

        return (Deque)pumpQueue;
    }

    public void rebuildQueue() {
        this.numFluidBlocksFound = 0;
        this.pumpLayerQueues.clear();
        int x = this.container.xCoord;
        int y = this.container.yCoord + 1;
        int z = this.container.zCoord;
        Fluid pumpingFluid = BlockUtils.getFluid(this.container.getWorldObj().getBlock(x, y, z));
        if (pumpingFluid != null) {
            if (pumpingFluid == this.tank.getAcceptedFluid() || this.tank.getAcceptedFluid() == null) {
                HashSet visitedBlocks = new HashSet();
                LinkedList fluidsFound = new LinkedList();
                this.queueForPumping(x, y, z, visitedBlocks, fluidsFound, pumpingFluid);

                while (!fluidsFound.isEmpty()) {
                    LinkedList fluidsToExpand = fluidsFound;
                    fluidsFound = new LinkedList();
                    Iterator var8 = fluidsToExpand.iterator();

                    while (var8.hasNext()) {
                        BlockIndex index = (BlockIndex) var8.next();
                        this.queueForPumping(index.x, index.y + 1, index.z, visitedBlocks, fluidsFound, pumpingFluid);
                        this.queueForPumping(index.x + 1, index.y, index.z, visitedBlocks, fluidsFound, pumpingFluid);
                        this.queueForPumping(index.x - 1, index.y, index.z, visitedBlocks, fluidsFound, pumpingFluid);
                        this.queueForPumping(index.x, index.y, index.z + 1, visitedBlocks, fluidsFound, pumpingFluid);
                        this.queueForPumping(index.x, index.y, index.z - 1, visitedBlocks, fluidsFound, pumpingFluid);
                    }
                }
            }
        }
    }

    public void queueForPumping(int x, int y, int z, Set<BlockIndex> visitedBlocks, Deque<BlockIndex> fluidsFound, Fluid pumpingFluid) {
        BlockIndex index = new BlockIndex(x, y, z);
        if(visitedBlocks.add(index)) {
            if((x - this.container.xCoord) * (x - this.container.xCoord) + (z - this.container.zCoord) * (z - this.container.zCoord) > 4096) {
                return;
            }

            Block block = this.container.getWorldObj().getBlock(x, y, z);
            if(BlockUtils.getFluid(block) == pumpingFluid) {
                fluidsFound.add(index);
            }

            if(this.canDrainBlock(block, x, y, z, pumpingFluid)) {
                this.getLayerQueue(y).add(index);
                ++this.numFluidBlocksFound;
            }
        }

    }

    private boolean isPumpableFluid(int x, int y, int z) {
        Fluid fluid = BlockUtils.getFluid(this.container.getWorldObj().getBlock(x, y, z));
        return fluid == null?false:(!this.isFluidAllowed(fluid)?false:this.tank.getAcceptedFluid() == null || this.tank.getAcceptedFluid() == fluid);
    }

    private boolean canDrainBlock(Block block, int x, int y, int z, Fluid fluid) {
        if(!this.isFluidAllowed(fluid)) {
            return false;
        } else {
            FluidStack fluidStack = BlockUtils.drainBlock(block, this.container.getWorldObj(), x, y, z, false);
            return fluidStack != null && fluidStack.amount > 0?fluidStack.getFluid() == fluid:false;
        }
    }

    private boolean isFluidAllowed(Fluid fluid) {
        return BuildCraftFactory.pumpDimensionList.isFluidAllowed(fluid, this.container.getWorldObj().provider.dimensionId);
    }

    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        this.tank.writeToNBT(nbt);

        nbt.setBoolean("powered", powered);
        nbt.setBoolean("switched", switched);
        nbt.setBoolean("toggled", toggled);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.tank.readFromNBT(nbt);

        powered = nbt.getBoolean("powered");
        switched = nbt.getBoolean("switched");
        toggled = nbt.getBoolean("toggled");
    }

    public void invalidate() {
        super.invalidate();
        this.destroy();
    }

    public void validate() {
        super.validate();
    }

    public void destroy() {
        this.pumpLayerQueues.clear();
    }

    @Override
    public int getIconIndex(ForgeDirection direction) {
        return PipeIcons.PipeLiquidsDrain.ordinal();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIconProvider getIconProvider() {
        return ABO.instance.pipeIconProvider;
    }

    public boolean hasWork() {
        BlockIndex next = this.getNextIndexToPump(false);
        return next != null?this.isPumpableFluid(next.x, next.y, next.z):false;
    }

}
