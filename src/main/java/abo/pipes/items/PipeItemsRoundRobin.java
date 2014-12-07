package abo.pipes.items;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import abo.PipeIcons;
import abo.pipes.ABOPipe;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.pipes.events.PipeEventItem;

public class PipeItemsRoundRobin extends ABOPipe<PipeTransportItems> {
	private int	lastOrientation	= 0;

	public PipeItemsRoundRobin(Item itemID) {
		super(new PipeTransportItems(), itemID);
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return PipeIcons.PipeItemsRoundRobin.ordinal();
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
