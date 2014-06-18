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
import net.minecraftforge.common.util.ForgeDirection;
import abo.PipeIconProvider;
import abo.pipes.ABOPipe;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TravelingItem;
import buildcraft.transport.pipes.events.PipeEventItem;

/**
 * This pipe will always prefer to use the opposite direction, so items will go "straight through"
 * 
 * @author blakmajik ported to BC > 2.2 by Flow86
 */
public class PipeItemsCrossover extends ABOPipe<PipeTransportItems> {

	public PipeItemsCrossover(Item itemID) {
		super(new PipeTransportItems(), itemID);
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return PipeIconProvider.PipeItemsCrossover;
	}
	
	

	public void eventHandler(PipeEventItem.FindDest event) {
		
		List<ForgeDirection> result = event.destinations;
		TravelingItem item = event.item;
		List<ForgeDirection> list = new LinkedList<ForgeDirection>();

		if (transport.canReceivePipeObjects(item.input, item))
		{
			list.add(item.input);
			result.clear();
			result.addAll(list);
		}
	}
}
