package da3dsoul.scaryGen.generate.GeostrataGen.Ore.Galacticraft;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.world.gen.OverworldGenerator;

import java.util.HashMap;

public class GalactiCraftHandler {

    public static void addGeneratorOverrides(HashMap<IWorldGenerator, Integer> toAdd) {
        if (ConfigManagerCore.enableCopperOreGen) {
            toAdd.put(new OverworldGeneratorOverride(GCBlocks.basicBlock, 5, 24, 0, 75, 7), 4);
        }

        if (ConfigManagerCore.enableTinOreGen) {
            toAdd.put(new OverworldGeneratorOverride(GCBlocks.basicBlock, 6, 22, 0, 60, 7), 4);
        }

        if (ConfigManagerCore.enableAluminumOreGen) {
            toAdd.put(new OverworldGeneratorOverride(GCBlocks.basicBlock, 7, 18, 0, 45, 7), 4);
        }

        if (ConfigManagerCore.enableSiliconOreGen) {
            toAdd.put(new OverworldGeneratorOverride(GCBlocks.basicBlock, 8, 3, 0, 25, 7), 4);
        }
    }
}
