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

import java.util.LinkedList;
import java.util.List;

import cofh.api.energy.IEnergyHandler;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import abo.ABO;
import abo.PipeIcons;
import buildcraft.api.core.IIconProvider;
import buildcraft.api.core.Position;
import buildcraft.api.transport.IPipeTile;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.PipeConnectionBans;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.PipeItemsWood;
import buildcraft.transport.pipes.events.PipeEventItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * This pipe will always prefer to insert it's objects into another pipe over
 * one that is not a pipe.
 * 
 * @author Scott Chamberlain (Leftler) ported to BC > 2.2 by Flow86
 */
public class PipeItemsExtraction extends PipeItemsWood implements IEnergyHandler {
	private final int		standardIconIndex	= PipeIcons.PipeItemsExtraction.ordinal();
	private final int		solidIconIndex		= PipeIcons.PipeItemsExtractionSide.ordinal();

	private boolean			powered;

	@SuppressWarnings("unchecked")
	public PipeItemsExtraction(Item itemID) {
		super(itemID);

		PipeConnectionBans.banConnection(PipeItemsExtraction.class, PipeItemsWood.class);

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
		if (powered) battery.addEnergy(0, 1, false);
	}

	public boolean isPowered() {
		return powered;
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		if (direction == ForgeDirection.UNKNOWN)
			return standardIconIndex;
		else {
			int metadata = container.getBlockMetadata();

			if (metadata == direction.ordinal())
				return solidIconIndex;
			else
				return standardIconIndex;
		}
	}

	public void eventHandler(PipeEventItem.FindDest event) {
		LinkedList<ForgeDirection> nonPipesList = new LinkedList<ForgeDirection>();
		LinkedList<ForgeDirection> pipesList = new LinkedList<ForgeDirection>();

		List<ForgeDirection> result = event.destinations;

		for (ForgeDirection o : result) {
			TileEntity entity = container.getTile(o);
			if (entity instanceof IPipeTile)
				pipesList.add(o);
			else
				nonPipesList.add(o);
		}

		if (!pipesList.isEmpty()) {
			result.clear();
			result.addAll(pipesList);
			return;
		}
		result.clear();
		result.addAll(nonPipesList);
		return;
	}
	
	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return super.receiveEnergy(from, maxReceive, simulate);
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		return super.getEnergyStored(from);
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return super.getMaxEnergyStored(from);
	}
}
