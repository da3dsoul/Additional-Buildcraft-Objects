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

package abo.pipes.power;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import abo.ABO;
import abo.PipeIconProvider;
import abo.gui.ABOGuiIds;
import abo.network.IYesNoChange;
import abo.pipes.ABOPipe;
import buildcraft.core.IItemPipe;
import buildcraft.transport.PipeTransportPower;

public class PipePowerDistribution extends ABOPipe<PipeTransportPower> implements IYesNoChange {
	public final boolean[] connectionMatrix = { true, true, true, true, true, true };

	public boolean isDirty = true;

	public PipePowerDistribution(Item itemID) {
		super(new PipeTransportPower(), itemID);

		transport.powerCapacities.put(PipePowerDistribution.class, 1024);
		transport.initFromPipe(getClass());
	}

	@Override
	public boolean blockActivated(EntityPlayer entityplayer) {
		if (entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem().getItem() instanceof IItemPipe) {
				return false;
		}

		if (super.blockActivated(entityplayer))
			return true;

		if (!container.getWorldObj().isRemote)
			entityplayer.openGui(ABO.instance, ABOGuiIds.PIPE_DIAMOND_POWER, container.getWorldObj(), container.xCoord, container.yCoord, container.zCoord);

		return true;
	}

	@Override
	public void update(int slot, boolean state) {
		if (connectionMatrix[slot] != state) {
			connectionMatrix[slot] = state;
			isDirty = true;
			updateEntity();
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (isDirty) {
			// System.out.println("updateEntity: " + worldObj.isRemote + ": " +
			// isDirty);
			container.scheduleNeighborChange();
			updateNeighbors(true);
			isDirty = false;
		}
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		switch (direction) {
		case UNKNOWN:
			return PipeIconProvider.PipePowerDiamondCenter;
		case DOWN:
			return PipeIconProvider.PipePowerDiamondDown;
		case UP:
			return PipeIconProvider.PipePowerDiamondUp;
		case NORTH:
			return PipeIconProvider.PipePowerDiamondNorth;
		case SOUTH:
			return PipeIconProvider.PipePowerDiamondSouth;
		case WEST:
			return PipeIconProvider.PipePowerDiamondWest;
		case EAST:
			return PipeIconProvider.PipePowerDiamondEast;
		default:
			throw new IllegalArgumentException("direction out of bounds");
		}
	}

	@Override
	public boolean canPipeConnect(TileEntity tile, ForgeDirection side) {
		return connectionMatrix[side.ordinal()] && super.canPipeConnect(tile, side);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		for (int i = 0; i < 6; ++i)
			nbt.setBoolean("connectionMatrix[" + i + "]", connectionMatrix[i]);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		for (int i = 0; i < 6; ++i) {
			if (nbt.hasKey("connectionMatrix[" + i + "]"))
				connectionMatrix[i] = nbt.getBoolean("connectionMatrix[" + i + "]");
		}
		isDirty = true;
	}
}
