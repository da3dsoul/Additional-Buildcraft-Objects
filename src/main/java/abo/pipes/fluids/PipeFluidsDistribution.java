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

package abo.pipes.fluids;

import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidTankInfo;

import abo.ABO;
import abo.PipeIconProvider;
import abo.gui.ABOGuiIds;
import abo.network.IFluidSlotChange;
import abo.pipes.ABOPipe;

import buildcraft.core.IItemPipe;
import buildcraft.core.network.IClientState;
import buildcraft.core.utils.Utils;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.PipeTransportFluids;

/**
 * @author Flow86
 * 
 */
public class PipeFluidsDistribution extends ABOPipe<PipeTransportFluids> implements IFluidSlotChange, IClientState {

	public final Fluid[] fluids = new Fluid[6 * 9];

	public PipeFluidsDistribution(Item itemID) {
		super(new PipeTransportFluids(), itemID);

		transport.flowRate = 160;
		transport.travelDelay = 2;
	}

	@Override
	public boolean blockActivated(EntityPlayer entityplayer) {
		if (entityplayer.getCurrentEquippedItem() != null && entityplayer.getCurrentEquippedItem().getItem() instanceof IItemPipe) {
				return false;
		}

		if (super.blockActivated(entityplayer))
			return true;

		if (!container.getWorldObj().isRemote)
			entityplayer.openGui(ABO.instance, ABOGuiIds.PIPE_DIAMOND_LIQUIDS, container.getWorldObj(), container.xCoord, container.yCoord, container.zCoord);

		return true;
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		switch (direction) {
		case UNKNOWN:
			return PipeIconProvider.PipeLiquidsDiamondCenter;
		case DOWN:
			return PipeIconProvider.PipeLiquidsDiamondDown;
		case UP:
			return PipeIconProvider.PipeLiquidsDiamondUp;
		case NORTH:
			return PipeIconProvider.PipeLiquidsDiamondNorth;
		case SOUTH:
			return PipeIconProvider.PipeLiquidsDiamondSouth;
		case WEST:
			return PipeIconProvider.PipeLiquidsDiamondWest;
		case EAST:
			return PipeIconProvider.PipeLiquidsDiamondEast;
		default:
			throw new IllegalArgumentException("direction out of bounds");
		}
	}

	@Override
	public boolean outputOpen(ForgeDirection to) {
		if (!super.outputOpen(to))
			return false;

		FluidTankInfo[] tanks = transport.getTankInfo(ForgeDirection.UNKNOWN);

		// center tank
		if (tanks == null || tanks[0] == null || tanks[0].fluid == null || tanks[0].fluid.amount == 0)
			return true;

		Fluid fluidInTank = tanks[0].fluid.getFluid();

		boolean[] validDirections = new boolean[ForgeDirection.values().length];
		boolean[] filteredDirections = new boolean[ForgeDirection.values().length];
		boolean filterForLiquid = false;

		// check every direction
		// perhaps we should/can cache this?
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			validDirections[dir.ordinal()] = false;
			filteredDirections[dir.ordinal()] = false;

			if (Utils.checkPipesConnections(container.getTile(dir), container)) {
				for (int slot = 0; slot < 9; ++slot) {
					Fluid fluid = fluids[dir.ordinal() * 9 + slot];

					if (fluid != null) {
						filteredDirections[dir.ordinal()] = true;

						if (fluidInTank.getID() == fluid.getID()) {
							validDirections[dir.ordinal()] = true;
							filterForLiquid = true;
						}
					}
				}
			}
		}

		// the direction is filtered and liquids match
		if (filteredDirections[to.ordinal()] && validDirections[to.ordinal()])
			return true;

		// we havent found a filter for this liquid and the direction is free
		if (!filterForLiquid && !filteredDirections[to.ordinal()])
			return true;

		// we have a filter for the liquid, but not a valid Direction :/
		return false;
	}

	@Override
	public void update(int slot, Fluid fluid) {
		// System.out.println("update: " + worldObj.isRemote + " - " + slot + " to " + stack);

		if (fluid != fluids[slot]) {
			fluids[slot] = fluid;
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if(!nbt.hasKey("fluids")) return;
		NBTTagCompound nbt1 = nbt.getCompoundTag("fluids");
		for(int i = 0; i < fluids.length; i++)
		{
			String name = nbt1.getString("slot:"+i);
			if(name.equals("empty"))
			{
				fluids[i] = null;
			}else
			{
				try
				{
					fluids[i] = FluidRegistry.getFluid(name);
				}catch(Exception e) { fluids[i] = null; ABO.aboLog.error("Liquid does not exist. Did you remove a mod?"); }
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagCompound nbt1 = new NBTTagCompound();
		for(int i = 0; i < fluids.length; i++)
		{
			Fluid fluid = fluids[i];
			String name;
			if(fluid != null)
			{
				name = fluid.getName();
			}else
			{
				name = "empty";
			}
			nbt1.setString("slot:"+i, name);
		}
		nbt.setTag("fluids", nbt1);
	}

	// ICLIENTSTATE
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
	
	
}
