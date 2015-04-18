package abo.pipes.fluids;

import buildcraft.BuildCraftTransport;
import buildcraft.transport.PipeTransportFluids;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.ForgeDirection;
import abo.PipeIcons;
import net.minecraftforge.fluids.FluidContainerRegistry;

public class PipeFluidsReinforcedGoldenIron extends PipeFluidsGoldenIron {
	
	private final int			standardIconIndex	= PipeIcons.PipeLiquidsReinforcedGoldenIron.ordinal();
	private final int			solidIconIndex		= PipeIcons.PipeLiquidsReinforcedGoldenIronSide.ordinal();
	
	public PipeFluidsReinforcedGoldenIron(Item itemID) {
		super(itemID);

		PipeTransportFluids.fluidCapacities.put(PipeFluidsReinforcedGoldenIron.class, Integer.valueOf(2 * FluidContainerRegistry.BUCKET_VOLUME));

		transport.flowRate = 16 * BuildCraftTransport.pipeFluidsBaseFlowRate;
		transport.travelDelay = 2;
	}
	
	@Override
	public int getIconIndex(ForgeDirection direction) {
		if (direction == ForgeDirection.UNKNOWN)
			return standardIconIndex;
		else {
			int metadata = container.getBlockMetadata();

			if (metadata != direction.ordinal())
				return solidIconIndex;
			else
				return standardIconIndex;
		}
	}

}
