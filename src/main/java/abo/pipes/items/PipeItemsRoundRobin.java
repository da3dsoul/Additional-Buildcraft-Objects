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

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import abo.PipeIconProvider;
import abo.pipes.ABOPipe;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.pipes.events.PipeEventItem;

/**
 * @author Flow86
 * 
 */
public class PipeItemsRoundRobin extends ABOPipe<PipeTransportItems> {
	private int lastOrientation = 0;

	public PipeItemsRoundRobin(Item itemID) {
		super(new PipeTransportItems(), itemID);
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return PipeIconProvider.PipeItemsRoundRobin;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);

		lastOrientation = nbttagcompound.getInteger("lastOrientation");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);

		nbttagcompound.setInteger("lastOrientation", lastOrientation);
	}

	public void eventHandler(PipeEventItem.FindDest event) {
		List<ForgeDirection> result = event.destinations;
		
		if (result.size() == 0) {
			return;
		} else {
			lastOrientation = (lastOrientation + 1) % result.size();

			LinkedList<ForgeDirection> newPossibleOrientations = new LinkedList<ForgeDirection>();
			newPossibleOrientations.add(result.get(lastOrientation));
			result.clear();
			result.addAll(newPossibleOrientations);
			return;
		}
	}
}
