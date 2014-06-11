/** 
 * Copyright (C) 2011-2013 Flow86
 * 
 * AdditionalBuildcraftObjects is open-source.
 *
 * It is distributed under the terms of my Open Source License. 
 * It grants rights to read, modify, compile or run the code. 
 * It does *NOT* grant the right to redistribute this software or its 
 * modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package abo.pipes.items;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraftforge.common.util.ForgeDirection;

import abo.ABO;
import abo.PipeIconProvider;
import abo.gui.ABOGuiIds;

import buildcraft.BuildCraftTransport;
import buildcraft.api.core.IIconProvider;
import buildcraft.api.core.Position;
import buildcraft.api.mj.MjBattery;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.PipeManager;
import buildcraft.core.GuiIds;
import buildcraft.core.inventory.InvUtils;
import buildcraft.core.inventory.InventoryWrapper;
import buildcraft.core.inventory.SimpleInventory;
import buildcraft.core.inventory.StackHelper;
import buildcraft.core.network.IClientState;
import buildcraft.core.network.IGuiReturnHandler;
import buildcraft.core.utils.Utils;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.IPipeTransportItemsHook;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeConnectionBans;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.TravelingItem;
import buildcraft.transport.pipes.PipeItemsWood;
import buildcraft.transport.pipes.PipeLogicWood;
import buildcraft.transport.pipes.PipeItemsEmerald.EmeraldPipeSettings;
import buildcraft.transport.pipes.PipeItemsEmerald.FilterMode;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * This pipe will always prefer to insert it's objects into another pipe over one that is not a pipe.
 * 
 * @author Scott Chamberlain (Leftler) ported to BC > 2.2 by Flow86
 */
public class PipeItemsEnderExtraction extends Pipe<PipeTransportItems> implements IPowerReceptor, IPipeTransportItemsHook, IClientState, IGuiReturnHandler {
	private final int standardIconIndex = PipeIconProvider.PipeItemsEnderExtraction;
	
	private PowerHandler powerHandler;
	
	@MjBattery (maxCapacity = 64, maxReceivedPerCycle = 64, minimumConsumption = 1)
	public double mjStored = 0;
	
	private boolean powered;
	
	public class EmeraldPipeSettings {

		private FilterMode filterMode;

		public EmeraldPipeSettings() {
			filterMode = FilterMode.WHITE_LIST;
		}

		public FilterMode getFilterMode() {
			return filterMode;
		}

		public void setFilterMode(FilterMode mode) {
			filterMode = mode;
		}

		public void readFromNBT(NBTTagCompound nbt) {
			filterMode = FilterMode.values()[nbt.getByte("filterMode")];
		}

		public void writeToNBT(NBTTagCompound nbt) {
			nbt.setByte("filterMode", (byte) filterMode.ordinal());
		}
	}

	private EmeraldPipeSettings settings = new EmeraldPipeSettings();

	private final SimpleInventory filters = new SimpleInventory(9, "Filters", 1);

	private int currentFilter = 0;

	public PipeItemsEnderExtraction(Item itemID) {
		super(new PipeTransportItems(), itemID);

		PipeConnectionBans.banConnection(PipeItemsEnderExtraction.class, PipeItemsWood.class);

		transport.allowBouncing = true;
		
		powerHandler = new PowerHandler(this, Type.PIPE);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIconProvider getIconProvider() {
		return ABO.instance.pipeIconProvider;
	}
	
	

	@Override
	public void updateEntity() {
		//updateRedstoneCurrent();
		//useRedstoneAsPower();
		super.updateEntity();

		if (container.getWorldObj().isRemote) {
			return;
		}

		if (mjStored > 0) {
			if (transport.getNumberOfStacks() < PipeTransportItems.MAX_PIPE_STACKS) {
				extractItems();
			}

			mjStored = 0;
		}
	}
	
	@Override
	public boolean blockActivated(EntityPlayer entityplayer) {
		if (entityplayer.getCurrentEquippedItem() != null) {
			if (Block.getBlockFromItem(entityplayer.getCurrentEquippedItem().getItem()) instanceof BlockGenericPipe) {
				return false;
			}
		}

		if (super.blockActivated(entityplayer)) {
			return true;
		}

		if (!container.getWorldObj().isRemote) {
			entityplayer.openGui(ABO.instance, ABOGuiIds.PIPE_ENDER_EXTRACTION, container.getWorldObj(), container.xCoord, container.yCoord, container.zCoord);
		}

		return true;
	}

	public ItemStack[] checkExtract(IInventory inventory, boolean doRemove) {
		if (inventory == null) {
			return null;
		}
		
		if (settings.getFilterMode() == FilterMode.ROUND_ROBIN) {
			return checkExtractRoundRobin(inventory, doRemove);
		}

		return checkExtractFiltered(inventory, doRemove);
	}

	private ItemStack[] checkExtractFiltered(IInventory inventory, boolean doRemove) {
		for (int k  = 0; k < inventory.getSizeInventory(); k++) {
			ItemStack stack = inventory.getStackInSlot(k);

			if (stack == null || stack.stackSize <= 0) {
				continue;
			}

			boolean matches = isFiltered(stack);
			boolean isBlackList = settings.getFilterMode() == FilterMode.BLACK_LIST;

			if ((isBlackList && matches) || (!isBlackList && !matches)) {
				continue;
			}

			if (doRemove) {
				double energyUsed =  mjStored > stack.stackSize ? stack.stackSize : mjStored;
				mjStored -= energyUsed;

				stack = inventory.decrStackSize(k, (int) Math.floor(energyUsed));
			}

			return new ItemStack[] {stack};
		}

		return null;
	}
	
	private ItemStack[] checkExtractRoundRobin(IInventory inventory, boolean doRemove) {
		for (int i  = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null && stack.stackSize > 0) {
				ItemStack filter = getCurrentFilter();

				if (filter == null) {
					return null;
				}

				if (!StackHelper.isMatchingItem(filter, stack, true, false)) {
					continue;
				}

				if (doRemove) {
					// In Round Robin mode, extract only 1 item regardless of power level.
					stack = inventory.decrStackSize(i, 1);
					incrementFilter();
				}

				return new ItemStack[]{ stack };
			}
		}

		return null;
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
					if (pipe.pipe.hasGate() && pipe.pipe.gate.getRedstoneOutput() > 0)
						powered = true;
				}
			}
		}

		if (!powered)
			powered = container.getWorldObj().isBlockIndirectlyGettingPowered(container.xCoord, container.yCoord, container.zCoord);

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
		//updateRedstoneCurrent();
	}
	
	private void useRedstoneAsPower()
	{
		if(powered)	mjStored++;
	}
	
	public boolean isPowered() {
		return powered;
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return standardIconIndex;
	}

	@Override
	public LinkedList<ForgeDirection> filterPossibleMovements(LinkedList<ForgeDirection> possibleOrientations, Position pos, TravelingItem item) {
		LinkedList<ForgeDirection> nonPipesList = new LinkedList<ForgeDirection>();
		LinkedList<ForgeDirection> pipesList = new LinkedList<ForgeDirection>();

		item.blacklist.add(item.input.getOpposite());

		for (ForgeDirection o : possibleOrientations) {
			if (!item.blacklist.contains(o) && container.pipe.outputOpen(o)) {
				if (transport.canReceivePipeObjects(o, item)) {

					TileEntity entity = container.getTile(o);
					if (entity instanceof IPipeTile)
						pipesList.add(o);
					else
						nonPipesList.add(o);
				}
			}
		}

		if (!pipesList.isEmpty())
			return pipesList;
		else
			return nonPipesList;
	}

	@Override
	public void entityEntered(TravelingItem item, ForgeDirection orientation) {
	}

	@Override
	public void readjustSpeed(TravelingItem item) {
		transport.defaultReajustSpeed(item);
	}

	@Override
	public void doWork(PowerHandler arg0) {	}

	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection arg0) {
		return powerHandler.getPowerReceiver();
	}
	
	private void extractItems() {
		
		IInventory inventory = ABO.instance.getInventoryEnderChest();
		ItemStack[] extracted = checkExtract(inventory, true);
		if (extracted == null) {
			return;
		}

		for (ItemStack stack : extracted) {
			if (stack == null || stack.stackSize == 0) {
				mjStored = mjStored > 1 ? mjStored - 1 : 0;

				continue;
			}

			Position entityPos = new Position(container.xCoord + 0.5, container.yCoord + 0.5, container.zCoord + 0.5, ForgeDirection.UNKNOWN);

			TravelingItem entity = makeItem(entityPos.x, entityPos.y, entityPos.z, stack);

			transport.injectItem(entity, entityPos.orientation);
		}
	}
	
	@Override
	public boolean canPipeConnect(TileEntity tile, ForgeDirection side) {
		return tile instanceof TileGenericPipe;
	}
	
	protected TravelingItem makeItem(double x, double y, double z, ItemStack stack) {
		return TravelingItem.make(x, y, z, stack);
	}

	/**
	 * Return the itemstack that can be if something can be extracted from this
	 * inventory, null if none. On certain cases, the extractable slot depends
	 * on the position of the pipe.
	 */
	/*public ItemStack[] checkExtract(IInventory inventory, boolean doRemove) {
		ItemStack result = checkExtractGeneric(inventory, doRemove);

		if (result != null) {
			return new ItemStack[]{result};
		}

		return null;
	}*/

	public ItemStack checkExtractGeneric(IInventory inventory, boolean doRemove) {
		if (inventory == null) {
			return null;
		}

		for (int k  = 0; k < inventory.getSizeInventory(); k++) {
			ItemStack slot = inventory.getStackInSlot(k);

			if (slot != null && slot.stackSize > 0) {
				if (doRemove) {
					double energyUsed =  mjStored > slot.stackSize ? slot.stackSize : mjStored;
					mjStored -= energyUsed;

					return inventory.decrStackSize(k, (int) energyUsed);
				} else {
					return slot;
				}
			}
		}

		return null;
	}
	
	private boolean isFiltered(ItemStack stack) {
		for (int i = 0; i < filters.getSizeInventory(); i++) {
			ItemStack filter = filters.getStackInSlot(i);

			if (filter == null) {
				return false;
			}

			if (StackHelper.isMatchingItem(filter, stack, true, false)) {
				return true;
			}
		}

		return false;
	}

	private void incrementFilter() {
		currentFilter++;
		int count = 0;
		while (filters.getStackInSlot(currentFilter % filters.getSizeInventory()) == null && count < filters.getSizeInventory()) {
			currentFilter++;
			count++;
		}
	}

	private ItemStack getCurrentFilter() {
		ItemStack filter = filters.getStackInSlot(currentFilter % filters.getSizeInventory());
		if (filter == null) {
			incrementFilter();
		}
		return filters.getStackInSlot(currentFilter % filters.getSizeInventory());
	}

	public IInventory getFilters() {
		return filters;
	}

	public EmeraldPipeSettings getSettings() {
		return settings;
	}

	@Override
	public void writeData(ByteBuf data) {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		Utils.writeNBT(data, nbt);
	}

	@Override
	public void readData(ByteBuf data) {
		NBTTagCompound nbt = Utils.readNBT(data);
		readFromNBT(nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		filters.readFromNBT(nbt);
		settings.readFromNBT(nbt);

		currentFilter = nbt.getInteger("currentFilter");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		filters.writeToNBT(nbt);
		settings.writeToNBT(nbt);

		nbt.setInteger("currentFilter", currentFilter);
	}

	@Override
	public void writeGuiData(ByteBuf data) {
		data.writeByte((byte) settings.getFilterMode().ordinal());
	}

	@Override
	public void readGuiData(ByteBuf data, EntityPlayer sender) {
		settings.setFilterMode(FilterMode.values()[data.readByte()]);
	}
}
