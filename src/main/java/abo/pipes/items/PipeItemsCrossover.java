package abo.pipes.items;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraftforge.common.util.ForgeDirection;
import abo.PipeIcons;
import abo.pipes.ABOPipe;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TravelingItem;
import buildcraft.transport.pipes.events.PipeEventItem;

public class PipeItemsCrossover extends ABOPipe<PipeTransportItems> {

	public PipeItemsCrossover(Item itemID) {
		super(new PipeTransportItems(), itemID);
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return PipeIcons.PipeItemsCrossover.ordinal();
	}

	public void eventHandler(PipeEventItem.FindDest event) {

		List<ForgeDirection> result = event.destinations;
		TravelingItem item = event.item;
		List<ForgeDirection> list = new LinkedList<ForgeDirection>();

		if (transport.inputOpen(item.input)) {
			list.add(item.input);
			result.clear();
			result.addAll(list);
		}
	}
}
