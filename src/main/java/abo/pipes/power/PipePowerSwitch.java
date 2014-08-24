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

import java.util.LinkedList;
import java.util.Map;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import abo.ABO;
import abo.PipeIcons;
import abo.actions.ActionSwitchOnPipe;
import abo.actions.ActionToggleOffPipe;
import abo.actions.ActionToggleOnPipe;
import abo.pipes.ABOPipe;
import buildcraft.api.core.Position;
import buildcraft.api.gates.IAction;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.IPipeTransportPowerHook;
import buildcraft.transport.ISolidSideTile;
import buildcraft.transport.PipeConnectionBans;
import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.TileGenericPipe;

/**
 * @author Flow86
 * 
 */
public class PipePowerSwitch extends ABOPipe<PipeTransportPower> implements IPipeTransportPowerHook, ISolidSideTile {
	private final int unpoweredTexture = PipeIcons.PipePowerSwitchUnpowered.ordinal();
	private final int poweredTexture = PipeIcons.PipePowerSwitchPowered.ordinal();
	private boolean powered;
	private boolean switched;
	private boolean toggled;

	@SuppressWarnings("unchecked")
	public PipePowerSwitch(Item itemID) {
		super(new PipeTransportPower(), itemID);

		PipeConnectionBans.banConnection(PipePowerSwitch.class, PipePowerSwitch.class);

		PipeTransportPower.powerCapacities.put(PipePowerSwitch.class, 1024);
		transport.initFromPipe(getClass());
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return isPowered() ? poweredTexture : unpoweredTexture;
	}

	/**
	 * @return
	 */
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
		updateRedstoneCurrent();
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setBoolean("powered", powered);
		nbttagcompound.setBoolean("switched", switched);
		nbttagcompound.setBoolean("toggled", toggled);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		powered = nbttagcompound.getBoolean("powered");
		switched = nbttagcompound.getBoolean("switched");
		toggled = nbttagcompound.getBoolean("toggled");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.minecraft.src.buildcraft.transport.Pipe#updateEntity()
	 */
	@Override
	public void updateEntity() {
		super.updateEntity();
		updateRedstoneCurrent();
	}

	@Override
	public LinkedList<IAction> getActions() {
		LinkedList<IAction> actions = super.getActions();
		actions.add(ABO.actionSwitchOnPipe);
		actions.add(ABO.actionToggleOnPipe);
		actions.add(ABO.actionToggleOffPipe);
		return actions;
	}

	@Override
	protected void actionsActivated(Map<IAction, Boolean> actions) {
		boolean lastSwitched = switched;
		boolean lastToggled = toggled;

		super.actionsActivated(actions);
		
		switched = false;
		// Activate the actions
		for (IAction i : actions.keySet()) {
			if (actions.get(i)) {
				if (i instanceof ActionSwitchOnPipe) {
					switched = true;
				} else if (i instanceof ActionToggleOnPipe) {
					toggled = true;
				} else if (i instanceof ActionToggleOffPipe) {
					toggled = false;
				}
			}
		}
		if ((lastSwitched != switched) || (lastToggled != toggled)) {
			if (lastSwitched != switched && !switched)
				toggled = false;

			container.scheduleRenderUpdate();
			updateNeighbors(true);
		}
	}

	@Override
	public double receiveEnergy(ForgeDirection from, double val) {
		// no power is received if "disconnected"
		if (!isPowered())
			return val;

		if (val > 0.0) {
			transport.internalNextPower[from.ordinal()] += val;

			if (transport.internalNextPower[from.ordinal()] > transport.maxPower) {
				val = (transport.internalNextPower[from.ordinal()] - transport.maxPower);
				transport.internalNextPower[from.ordinal()] = transport.maxPower;
			}
		}
		return val;
	}

	@Override
	public double requestEnergy(ForgeDirection from, double amount) {
		// no power is requested if "disconnected"
		if (!isPowered())
			return 0;

		return amount;
	}

	@Override
	public boolean canPipeConnect(TileEntity tile, ForgeDirection side) {
		return super.canPipeConnect(tile, side) || getWorld().getBlock(container.xCoord + side.offsetX, container.yCoord + side.offsetY, container.zCoord + side.offsetZ).getMaterial() == Material.circuits;
	}
	
	@Override
	public boolean isSolidOnSide(ForgeDirection side) {
		if(getWorld().getBlock(container.xCoord + side.offsetX, container.yCoord + side.offsetY, container.zCoord + side.offsetZ).getMaterial().isReplaceable() || getWorld().getBlock(container.xCoord + side.offsetX, container.yCoord + side.offsetY, container.zCoord + side.offsetZ).getMaterial() == Material.circuits)
		{
			return true;
		}
		return false;
	}
	
}
