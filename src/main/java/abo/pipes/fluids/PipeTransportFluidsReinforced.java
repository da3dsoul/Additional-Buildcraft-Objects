package abo.pipes.fluids;

        import buildcraft.transport.PipeTransportFluids;
        import net.minecraftforge.fluids.FluidContainerRegistry;

public class PipeTransportFluidsReinforced extends PipeTransportFluids {

    public PipeTransportFluidsReinforced() {
        super();
        INPUT_TTL = 980;
        OUTPUT_TTL = 1000;
    }

    @Override
    public int getCapacity() {
        return 2 * FluidContainerRegistry.BUCKET_VOLUME;
    }
}
