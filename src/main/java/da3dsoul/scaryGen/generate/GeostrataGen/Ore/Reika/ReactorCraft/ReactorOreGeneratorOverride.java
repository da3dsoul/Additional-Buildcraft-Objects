//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package da3dsoul.scaryGen.generate.GeostrataGen.Ore.Reika.ReactorCraft;

import Reika.DragonAPI.Interfaces.RetroactiveGenerator;
import Reika.ReactorCraft.Registry.FluoriteTypes;
import Reika.ReactorCraft.Registry.ReactorOptions;
import Reika.ReactorCraft.Registry.ReactorOres;
import java.util.Random;

import da3dsoul.scaryGen.generate.GeostrataGen.Ore.WorldGenMinableOverride;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public class ReactorOreGeneratorOverride implements RetroactiveGenerator {
    public static final ReactorOreGeneratorOverride instance = new ReactorOreGeneratorOverride();

    private ReactorOreGeneratorOverride() {
    }

    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkgen, IChunkProvider provider) {
        for(int i = 0; i < ReactorOres.oreList.length; ++i) {
            ReactorOres ore = ReactorOres.oreList[i];
            if(ore.canGenerateInChunk(world, chunkX, chunkZ) && random.nextInt(ReactorOptions.DISCRETE.getValue()) == 0) {
                this.generate(ore, world, random, chunkX * 16, chunkZ * 16);
            }
        }

    }

    public void generate(ReactorOres ore, World world, Random random, int chunkX, int chunkZ) {
        Block id = ore.getBlock();
        int meta = ore.getBlockMetadata();
        int passes = ore.perChunk * ReactorOptions.DISCRETE.getValue();
        if(ore == ReactorOres.FLUORITE) {
            meta = FluoriteTypes.getRandomColor().ordinal();
            if(ReactorOptions.RAINBOW.getState()) {
                passes = (int)((float)passes / 4.0F);
                meta = 0;
            }
        }

        for(int i = 0; i < passes; ++i) {
            int posX = chunkX + random.nextInt(16);
            int posZ = chunkZ + random.nextInt(16);
            int posY = ore.minY + random.nextInt(ore.maxY - ore.minY + 1);
            if(ore.canGenAt(world, posX, posY, posZ) && (new WorldGenMinableOverride(id, meta, ore.veinSize, ore.getReplaceableBlock())).generate(world, random, posX, posY, posZ)) {
                ;
            }

            if(ore == ReactorOres.FLUORITE) {
                byte r = 3;

                for(int k = -r; k <= r; ++k) {
                    for(int l = -r; l <= r; ++l) {
                        for(int m = -r; m <= r; ++m) {
                            world.func_147479_m(posX, posY, posZ);
                        }
                    }
                }
            }
        }

    }

    public String getIDString() {
        return "ReactorCraft Ores";
    }

    public boolean canGenerateAt(Random rand, World world, int chunkX, int chunkZ) {
        return true;
    }
}
