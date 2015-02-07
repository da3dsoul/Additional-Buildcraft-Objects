package da3dsoul.scaryGen.generate;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldTypeScary extends WorldType {

	private byte index;
	
	public WorldTypeScary(byte j) {
		super("scaryGen"+j);
		index = j;
	}

	@Override
	public IChunkProvider getChunkGenerator(World world, String generatorOptions) {
		return new ChunkProviderScary(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), index);
	}
	
	

}
