package abo.pipes.fluids;

import buildcraft.transport.PipeTransportFluids;
import net.minecraftforge.fluids.FluidContainerRegistry;

public class PipeTransportFluidsReinforced extends PipeTransportFluids {

    public PipeTransportFluidsReinforced() {
        super();
    }

    @Override
    public int getCapacity() {
        return 2 * FluidContainerRegistry.BUCKET_VOLUME;
    }
}
