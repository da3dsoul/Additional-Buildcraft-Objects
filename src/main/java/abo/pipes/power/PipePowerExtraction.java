package abo.pipes.power;

import net.minecraft.item.Item;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.transport.IPipeTransportPowerHook;
import buildcraft.transport.pipes.PipePowerWood;

public class PipePowerExtraction extends PipePowerWood implements IPowerReceptor, IPipeTransportPowerHook {


	public PipePowerExtraction(Item item) {
		super(item);
		
	}
}