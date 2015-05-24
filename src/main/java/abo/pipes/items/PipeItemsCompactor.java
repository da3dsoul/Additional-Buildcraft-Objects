package abo.pipes.items;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

import buildcraft.api.statements.*;
import buildcraft.core.lib.inventory.InvUtils;
import buildcraft.core.lib.utils.Utils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import abo.ABO;
import abo.PipeIcons;
import abo.actions.ActionSwitchOnPipe;
import abo.actions.ActionToggleOffPipe;
import abo.actions.ActionToggleOnPipe;
import abo.pipes.ABOPipe;
import buildcraft.api.core.Position;
import buildcraft.api.core.SafeTimeTracker;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.TransportConstants;
import buildcraft.transport.TravelingItem;
import buildcraft.transport.pipes.events.PipeEventItem;

public class PipeItemsCompactor extends ABOPipe<PipeTransportItems> implements IActionReceptor {
	private final int													onTexture		= PipeIcons.PipeItemsCompactorOn
																								.ordinal();
	private final int													offTexture		= PipeIcons.PipeItemsCompactorOff
																								.ordinal();
	private boolean														powered			= false;
	private boolean														toggled			= false;
	private boolean														switched		= false;
	private final TreeMap<ForgeDirection, PipeItemsCompactorInventory>	receivedStacks	= new TreeMap<ForgeDirection, PipeItemsCompactorInventory>();
	@SuppressWarnings("deprecation")
	private final SafeTimeTracker										timeTracker		= new SafeTimeTracker();

	/**
	 * @param itemID
	 */
	public PipeItemsCompactor(Item itemID) {
		super(new PipeTransportItems(), itemID);
	}

	/**
	 * @param orientation
	 * @param stack
	 */
	public void addItemToItemStack(ForgeDirection orientation, ItemStack stack) {
		// System.out.println("in:  Stack " + stack.toString());

		if (!receivedStacks.containsKey(orientation))
			receivedStacks.put(orientation, new PipeItemsCompactorInventory());

		receivedStacks.get(orientation).addItemStack(container.getWorldObj(), stack);
	}

	@Override
	public void dropContents() {
		powered = false;
		toggled = false;
		switched = false;

		for (Entry<ForgeDirection, PipeItemsCompactorInventory> receivedStack : receivedStacks.entrySet()) {
			receivedStack.getValue().dropContents(container.getWorldObj(), container.xCoord, container.yCoord,
					container.zCoord);
		}
		receivedStacks.clear();

		super.dropContents();
	}

	public void eventHandler(PipeEventItem.Entered event) {
		TravelingItem item = event.item;
		if (isPowered() && item.getItemStack().isStackable()) {
			addItemToItemStack(item.output, item.getItemStack());
			transport.items.scheduleRemoval(item);
		} else
			readjustSpeed(item);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		powered = nbttagcompound.getBoolean("powered");
		switched = nbttagcompound.getBoolean("switched");
		toggled = nbttagcompound.getBoolean("toggled");

		// System.out.println("readFromNBT");

		NBTTagList nbtItems = (NBTTagList) nbttagcompound.getTag("items");

		for (int j = 0; j < nbtItems.tagCount(); ++j) {
			try {
				NBTTagCompound nbtTreeMap = (NBTTagCompound) nbtItems.getCompoundTagAt(j);

				ForgeDirection orientation = ForgeDirection.values()[nbtTreeMap.getInteger("orientation")];

				if (!receivedStacks.containsKey(orientation))
					receivedStacks.put(orientation, new PipeItemsCompactorInventory());

				NBTTagCompound nbtItemStacks = (NBTTagCompound) nbtTreeMap.getTag("itemStacks");

				receivedStacks.get(orientation).readFromNBT(container.getWorldObj(), nbtItemStacks);
			} catch (Throwable t) {
				// It may be the case that entities cannot be reloaded between
				// two versions - ignore these errors.
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setBoolean("powered", powered);
		nbttagcompound.setBoolean("switched", switched);
		nbttagcompound.setBoolean("toggled", toggled);

		// System.out.println("writeToNBT");

		NBTTagList nbtItems = new NBTTagList();

		for (Entry<ForgeDirection, PipeItemsCompactorInventory> receivedStack : receivedStacks.entrySet()) {
			NBTTagCompound nbtTreeMap = new NBTTagCompound();
			NBTTagCompound nbtItemStacks = new NBTTagCompound();

			nbtTreeMap.setInteger("orientation", receivedStack.getKey().ordinal());
			receivedStack.getValue().writeToNBT(nbtItemStacks);

			nbtTreeMap.setTag("itemStacks", nbtItemStacks);

			nbtItems.appendTag(nbtTreeMap);
		}

		nbttagcompound.setTag("items", nbtItems);
	}

	public boolean isPowered() {
		return powered || switched || toggled;
	}

	public void updateRedstoneCurrent() {
		boolean lastPowered = powered;

		LinkedList<TileGenericPipe> neighbours = new LinkedList<TileGenericPipe>();
		neighbours.add(this.container);

		powered = false;
		for (ForgeDirection o : ForgeDirection.VALID_DIRECTIONS) {
			Position pos = new Position(container.xCoord, container.yCoord, container.zCoord, o);
			pos.moveForwards(1.0);

			TileEntity tile = container.getTile(o);

			if (tile instanceof TileGenericPipe) {
				TileGenericPipe pipe = (TileGenericPipe) tile;
				if (BlockGenericPipe.isValid(pipe.pipe)) {
					neighbours.add(pipe);
					if (pipe.pipe.hasGate(o.getOpposite()) && pipe.pipe.gates[o.getOpposite().ordinal()].redstoneOutput > 0) powered = true;
				}
			}
		}

		if (!powered)
			powered = container.getWorldObj().isBlockIndirectlyGettingPowered(container.xCoord, container.yCoord,
					container.zCoord);

		if (lastPowered != powered) {
			for (TileGenericPipe pipe : neighbours) {
				pipe.scheduleNeighborChange();
				pipe.updateEntity();
			}
		}
	}

	@Override
	public void onNeighborBlockChange(int blockId) {
		super.onNeighborBlockChange(blockId);
		updateRedstoneCurrent();
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
	protected void actionsActivated(Collection<StatementSlot> actions) {
		boolean lastSwitched = switched;
		boolean lastToggled = toggled;

		super.actionsActivated(actions);

		switched = false;
		// Activate the actions
		for (StatementSlot actionslot : actions) {
			IStatement i = actionslot.statement;
				if (i instanceof ActionSwitchOnPipe) {
					switched = true;
				} else if (i instanceof ActionToggleOnPipe) {
					toggled = true;
				} else if (i instanceof ActionToggleOffPipe) {
					toggled = false;
				}
			
		}
		if ((lastSwitched != switched) || (lastToggled != toggled)) {
			if (lastSwitched != switched && !switched) toggled = false;

			container.scheduleRenderUpdate();
			updateNeighbors(true);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void updateEntity() {
		super.updateEntity();
		updateRedstoneCurrent();

		if (isPowered() && timeTracker.markTimeIfDelay(container.getWorldObj(), 25)) {
			for (Entry<ForgeDirection, PipeItemsCompactorInventory> receivedStack : receivedStacks.entrySet()) {
				ItemStack stack = receivedStack.getValue().findItemStackToRemove(container.getWorldObj(), 16, 100);
				if (stack != null) {
					// System.out.println("out: Stack " + stack.toString());

					stack.stackSize -= Utils.addToRandomInjectableAround(container.getWorldObj(), container.xCoord,
                            container.yCoord, container.zCoord, receivedStack.getKey(), stack);
					if (stack.stackSize > 0) {
						Position destPos = new Position(container.xCoord, container.yCoord, container.zCoord,
								receivedStack.getKey());

						destPos.moveForwards(0.3);

						InvUtils.dropItems(container.getWorldObj(), stack, (int) destPos.x, (int) destPos.y,
                                (int) destPos.z);
					}

				}
			}
		}
	}

	public void readjustSpeed(TravelingItem item) {
		item.setSpeed(Math.min(Math.max(TransportConstants.PIPE_NORMAL_SPEED, item.getSpeed()) * 2f,
				TransportConstants.PIPE_NORMAL_SPEED * 20F));
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		if (container != null && container.getWorldObj() != null) return (isPowered() ? onTexture : offTexture);
		return offTexture;
	}

	@Override
	public void actionActivated(IStatement action, IStatementParameter[] parameters) {
		boolean lastSwitched = switched;
		boolean lastToggled = toggled;

		switched = false;

		// Activate the actions
		if (action instanceof ActionToggleOnPipe) {
			toggled = true;
		} else if (action instanceof ActionToggleOffPipe) {
			toggled = false;
		}

		if ((lastSwitched != switched) || (lastToggled != toggled)) {
			if (lastSwitched != switched && !switched) toggled = false;
		}
	}
}
