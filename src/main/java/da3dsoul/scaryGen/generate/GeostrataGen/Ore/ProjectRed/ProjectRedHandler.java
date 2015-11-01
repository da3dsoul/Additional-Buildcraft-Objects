package da3dsoul.scaryGen.generate.GeostrataGen.Ore.ProjectRed;

import abo.ABO;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import mrtjp.core.world.GenLogicUniform;
import mrtjp.core.world.ISimpleStructureGen;
import mrtjp.core.world.SimpleGenHandler$;
import mrtjp.projectred.exploration.OreDefs;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import scala.collection.Iterator;
import scala.collection.Seq;

import java.lang.reflect.Field;

public class ProjectRedHandler {
    public static void override(IWorldGenerator gen1) {
        try {
            SimpleGenHandler$ gen2 = (SimpleGenHandler$) gen1;
            Class genClass = gen2.getClass();
            Field structureField = genClass.getDeclaredField("structures");
            structureField.setAccessible(true);
            Field structHashField = genClass.getDeclaredField("structHash");
            structHashField.setAccessible(true);
            Seq<ISimpleStructureGen> structures = (Seq<ISimpleStructureGen>) structureField.get(gen2);
            Iterator it = structures.toIterator();
            Block blockOres = (Block)Block.blockRegistry.getObject("ProjRed|Exploration:projectred.exploration.ore");
            if(blockOres == null) return;
            do {
                if(!it.hasNext()) break;
                ISimpleStructureGen structureGen = (ISimpleStructureGen) it.next();
                if(structureGen.genID().equals("pr_copper")) {
                    WorldGenClusterizerOverride gen = new WorldGenClusterizerOverride();
                    gen.clusterBlock = blockOres;
                    gen.clusterMeta = OreDefs.ORECOPPER().meta();
                    gen.clusterSize = 8;
                    ((GenLogicUniform) structureGen).gen_$eq(gen);
                    ABO.aboLog.info("Replaced Project:Red Copper with an override");
                } else if(structureGen.genID().equals("pr_tin")) {
                    WorldGenClusterizerOverride gen = new WorldGenClusterizerOverride();
                    gen.clusterBlock = blockOres;
                    gen.clusterMeta = OreDefs.ORETIN().meta();
                    gen.clusterSize = 8;
                    ((GenLogicUniform) structureGen).gen_$eq(gen);
                    ABO.aboLog.info("Replaced Project:Red Tin with an override");
                } else if(structureGen.genID().equals("pr_silver")) {
                    WorldGenClusterizerOverride gen = new WorldGenClusterizerOverride();
                    gen.clusterBlock = blockOres;
                    gen.clusterMeta = OreDefs.ORESILVER().meta();
                    gen.clusterSize = 4;
                    ((GenLogicUniform) structureGen).gen_$eq(gen);
                    ABO.aboLog.info("Replaced Project:Red Silver with an override");
                } else if(structureGen.genID().equals("pr_electrotine")) {
                    WorldGenClusterizerOverride gen = new WorldGenClusterizerOverride();
                    gen.clusterBlock = blockOres;
                    gen.clusterMeta = OreDefs.OREELECTROTINE().meta();
                    gen.clusterSize = 8;
                    ((GenLogicUniform) structureGen).gen_$eq(gen);
                    ABO.aboLog.info("Replaced Project:Red Electrotine with an override");
                }
            } while(true);

        } catch (Throwable t) {t.printStackTrace(); }
    }
}
