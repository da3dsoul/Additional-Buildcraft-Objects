package abo.pipes.items;

import buildcraft.core.lib.RFBattery;
import buildcraft.core.lib.inventory.SimpleInventory;
import buildcraft.core.lib.inventory.StackHelper;
import buildcraft.core.lib.network.IGuiReturnHandler;
import buildcraft.core.lib.utils.NetworkUtils;
import io.netty.buffer.ByteBuf;

import java.util.LinkedList;

import cofh.api.energy.IEnergyHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraftforge.common.util.ForgeDirection;
import abo.ABO;
import abo.PipeIcons;
import abo.gui.ABOGuiIds;
import buildcraft.api.core.IIconProvider;
import buildcraft.api.core.ISerializable;
import buildcraft.api.core.Position;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeConnectionBans;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.TravelingItem;
import buildcraft.transport.pipes.PipeItemsWood;
import buildcraft.transport.pipes.PipeItemsEmerald.FilterMode;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PipeItemsEnderExtraction extends Pipe<PipeTransportItems> implements IEnergyHandler, ISerializable,
        IGuiReturnHandler {
	private final int		standardIconIndex	= PipeIcons.PipeItemsEnderExtraction.ordinal();

	protected RFBattery battery = new RFBattery(640, 640, 0);

	private boolean			powered;

	public class EmeraldPipeSettings {

		private FilterMode	filterMode;

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

	private EmeraldPipeSettings		settings		= new EmeraldPipeSettings();

	private final SimpleInventory	filters			= new SimpleInventory(9, "Filters", 1);

	private int						currentFilter	= 0;

	@SuppressWarnings("unchecked")
	public PipeItemsEnderExtraction(Item itemID) {
		super(new PipeTransportItems(), itemID);

		PipeConnectionBans.banConnection(PipeItemsEnderExtraction.class, PipeItemsWood.class);

		transport.allowBouncing = true;

	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIconProvider getIconProvider() {
		return ABO.instance.pipeIconProvider;
	}

	@Override
	public void updateEntity() {
		// updateRedstoneCurrent();
		// useRedstoneAsPower();
		super.updateEntity();

		if (!hasEnderChest()) return;

		if (container.getWorldObj().isRemote) { return; }

		
		if (battery.getEnergyStored() > 10) {
			if (transport.getNumberOfStacks() < PipeTransportItems.MAX_PIPE_STACKS) {
				extractItems();
			}
		}
	}

	private boolean hasEnderChest() {
		TileEntity tile = null;
		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS) {
			tile = getWorld().getTileEntity(container.xCoord + side.offsetX, container.yCoord + side.offsetY,
					container.zCoord + side.offsetZ);
			if (tile != null && tile instanceof TileEntityEnderChest) break;
		}
		if (tile == null || !(tile instanceof TileEntityEnderChest)) { return false; }
		return true;
	}

	@Override
	public boolean blockActivated(EntityPlayer entityplayer) {
		if (entityplayer.getCurrentEquippedItem() != null) {
			if (Block.getBlockFromItem(entityplayer.getCurrentEquippedItem().getItem()) instanceof BlockGenericPipe) { return false; }
		}

		if (super.blockActivated(entityplayer)) { return true; }

		if (hasEnderChest()) {
			if (getWorld().isRemote) {
				return true;
			} else {
				entityplayer.openGui(ABO.instance, ABOGuiIds.PIPE_ENDER_EXTRACTION, container.getWorldObj(),
						container.xCoord, container.yCoord, container.zCoord);
				return true;
			}
		} else
			return false;
	}

	public ItemStack[] checkExtract(IInventory inventory, boolean doRemove) {
		if (inventory == null) { return null; }

		if (settings.getFilterMode() == FilterMode.ROUND_ROBIN) { return checkExtractRoundRobin(inventory, doRemove); }

		return checkExtractFiltered(inventory, doRemove);
	}

	private ItemStack[] checkExtractFiltered(IInventory inventory, boolean doRemove) {
		for (int k = 0; k < inventory.getSizeInventory(); k++) {
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
				int stackSize = (int) Math.floor(battery.useEnergy(10, 10 * stack.stackSize, false) / 10);
				
				stack = inventory.decrStackSize(k, stackSize);
			}

			return new ItemStack[] { stack };
		}

		return null;
	}

	private ItemStack[] checkExtractRoundRobin(IInventory inventory, boolean doRemove) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);

			if (stack != null && stack.stackSize > 0) {
				ItemStack filter = getCurrentFilter();

				if (filter == null) { return null; }

				if (!StackHelper.isMatchingItem(filter, stack, true, false)) {
					continue;
				}

				if (doRemove) {
					// In Round Robin mode, extract only 1 item regardless of
					// power level.
					stack = inventory.decrStackSize(i, 1);
                    battery.useEnergy(10,10,false);
					incrementFilter();
				}

				return new ItemStack[] { stack };
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
		// updateRedstoneCurrent();
	}

	@SuppressWarnings("unused")
	private void useRedstoneAsPower() {
		if (powered) battery.addEnergy(0,10,false);
	}

	public boolean isPowered() {
		return powered;
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return standardIconIndex;
	}

	private void extractItems() {

		IInventory inventory = ABO.instance.getInventoryEnderChest();
		ItemStack[] extracted = checkExtract(inventory, true);
		if (extracted == null) { return; }

		for (ItemStack stack : extracted) {
			if (stack == null || stack.stackSize == 0) {
				continue;
			}

			Position entityPos = new Position(container.xCoord + 0.5, container.yCoord + 0.5, container.zCoord + 0.5,
					ForgeDirection.UNKNOWN);

			TravelingItem entity = makeItem(entityPos.x, entityPos.y, entityPos.z, stack);

			transport.injectItem(entity, entityPos.orientation);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean canPipeConnect(TileEntity tile, ForgeDirection side) {
		if (tile instanceof TileEntityEnderChest) return true;
		Pipe<PipeTransportItems> otherPipe;
		if (tile instanceof TileGenericPipe && ((TileGenericPipe) tile).pipe.transport instanceof PipeTransportItems) {
			otherPipe = ((TileGenericPipe) tile).pipe;
			if (!BlockGenericPipe.isFullyDefined(otherPipe)) { return false; }

			if (!PipeConnectionBans.canPipesConnect(getClass(), otherPipe.getClass())) { return false; }
			return true;
		}
		return false;
	}

	protected TravelingItem makeItem(double x, double y, double z, ItemStack stack) {
		return TravelingItem.make(x, y, z, stack);
	}

	/**
	 * Return the itemstack that can be if something can be extracted from this
	 * inventory, null if none. On certain cases, the extractable slot depends
	 * on the position of the pipe.
	 */
	/*
	 * public ItemStack[] checkExtract(IInventory inventory, boolean doRemove) {
	 * ItemStack result = checkExtractGeneric(inventory, doRemove);
	 * 
	 * if (result != null) { return new ItemStack[]{result}; }
	 * 
	 * return null; }
	 */

	public ItemStack checkExtractGeneric(IInventory inventory, boolean doRemove) {
		if (inventory == null) { return null; }

		for (int k = 0; k < inventory.getSizeInventory(); k++) {
			ItemStack slot = inventory.getStackInSlot(k);

			if (slot != null && slot.stackSize > 0) {
				if (doRemove) {
					int stackSize = battery.useEnergy(10, slot.stackSize * 10, false) / 10;

					return inventory.decrStackSize(k, stackSize);
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

			if (filter == null) { return false; }

			if (StackHelper.isMatchingItem(filter, stack, true, false)) { return true; }
		}

		return false;
	}

	private void incrementFilter() {
		currentFilter++;
		int count = 0;
		while (filters.getStackInSlot(currentFilter % filters.getSizeInventory()) == null
				&& count < filters.getSizeInventory()) {
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
		NetworkUtils.writeNBT(data, nbt);
	}

	@Override
	public void readData(ByteBuf data) {
		NBTTagCompound nbt = NetworkUtils.readNBT(data);
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

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive,
			boolean simulate) {
		return battery.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract,
			boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return battery.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return battery.getMaxEnergyStored();
	}
}
