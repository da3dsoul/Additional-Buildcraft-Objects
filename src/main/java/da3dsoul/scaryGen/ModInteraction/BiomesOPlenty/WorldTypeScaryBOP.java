package da3dsoul.scaryGen.ModInteraction.BiomesOPlenty;

import biomesoplenty.common.world.WorldChunkManagerBOP;
import biomesoplenty.common.world.WorldProviderSurfaceBOP;
import biomesoplenty.common.world.layer.GenLayerBiomeBOP;
import da3dsoul.scaryGen.generate.WorldTypeScary;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerBiomeEdge;
import net.minecraft.world.gen.layer.GenLayerZoom;
import net.minecraftforge.common.DimensionManager;

public class WorldTypeScaryBOP extends WorldTypeScary {
    public WorldTypeScaryBOP() {
        super("scaryGenBOP");
        DimensionManager.unregisterProviderType(0);
        DimensionManager.registerProviderType(0, WorldProviderSurfaceBOP.class, true);
    }

    public GenLayer getBiomeLayer(long worldSeed, GenLayer parentLayer) {
        GenLayer ret = new GenLayerBiomeBOP(200L, parentLayer, this);
        ret = GenLayerZoom.magnify(1000L, ret, 2);
        ret = new GenLayerBiomeEdge(1000L, ret);
        return ret;
    }

    public WorldChunkManager getChunkManager(World world) {
        return new WorldChunkManagerBOP(world);
    }
}
