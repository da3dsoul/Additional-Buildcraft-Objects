package abo.pipes.fluids;

        import buildcraft.transport.PipeTransportFluids;
        import net.minecraftforge.fluids.FluidContainerRegistry;

public class PipeTransportFluidsReinforced extends PipeTransportFluids {

    public PipeTransportFluidsReinforced() {
        super();
        super.INPUT_TTL = 2 * FluidContainerRegistry.BUCKET_VOLUME - 250;
        super.OUTPUT_TTL = 2 * FluidContainerRegistry.BUCKET_VOLUME;
    }

    @Override
    public int getCapacity() {
        return 2 * FluidContainerRegistry.BUCKET_VOLUME;
    }
}
