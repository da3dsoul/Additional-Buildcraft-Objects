package da3dsoul.scaryGen.generate.GeostrataGen.Ore.COFH;

import abo.ABO;
import cofh.api.world.IFeatureGenerator;
import cofh.api.world.IGeneratorParser;
import cofh.core.world.FeatureParser;
import cofh.core.world.WorldHandler;
import cofh.core.world.decoration.ClusterParser;
import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenMinableCluster;
import cofh.lib.world.WorldGenSparseMinableCluster;
import cofh.lib.world.feature.FeatureGenUniform;
import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Level;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class COFHOverride {

    public static void overrideCOFHWordGen() {
        try {
            Class classFeatureParser = FeatureParser.class;
            Field fieldGeneratorHandlers = classFeatureParser.getDeclaredField("generatorHandlers");
            fieldGeneratorHandlers.setAccessible(true);
            HashMap<String, IGeneratorParser> generatorHandlers = (HashMap<String, IGeneratorParser>) fieldGeneratorHandlers.get(null);
            Iterator<Map.Entry<String,IGeneratorParser>> it = generatorHandlers.entrySet().iterator();
            ArrayList<String> keysClusterFalse = new ArrayList<String>();
            ArrayList<String> keysClusterTrue = new ArrayList<String>();
            do {
                if(!it.hasNext()) break;
                Map.Entry<String, IGeneratorParser> entry = it.next();
                if(entry.getValue() instanceof ClusterParser && entry.getKey() != null && entry.getKey().contains("sparse")) {
                    keysClusterTrue.add(entry.getKey());
                    it.remove();
                } else if(entry.getValue() instanceof ClusterParser) {
                    keysClusterFalse.add(entry.getKey());
                    it.remove();
                }
            }while(true);

            for(String key : keysClusterFalse) {
                FeatureParser.registerGenerator(key, new ClusterParserOverride(false));
            }
            for(String key : keysClusterTrue) {
                FeatureParser.registerGenerator(key, new ClusterParserOverride(true));
            }

            generatorHandlers = (HashMap<String, IGeneratorParser>) fieldGeneratorHandlers.get(null);
            it = generatorHandlers.entrySet().iterator();

            do {
                if(!it.hasNext()) break;
                Map.Entry<String, IGeneratorParser> entry = it.next();
                if(entry.getValue() instanceof ClusterParserOverride) {
                    ABO.aboLog.info("Replaced " + entry.getKey() + " in FeatureParser with an override");
                }
            }while(true);
        } catch(Throwable t) {
            ABO.aboLog.catching(Level.INFO, t);
        }

        /*try {
            Class registry = Class.forName("cpw.mods.fml.common.registry.GameRegistry");
            Field fieldWorldGenerators = registry.getDeclaredField("worldGenerators");
            fieldWorldGenerators.setAccessible(true);
            Set<IWorldGenerator> worldGenerators = (Set<IWorldGenerator>) fieldWorldGenerators.get(null);
            Iterator it = worldGenerators.iterator();
            do{
                if(!it.hasNext()) break;
                IWorldGenerator gen = (IWorldGenerator) it.next();
                if(gen instanceof WorldHandler) {
                    Class worldHandler = gen.getClass();
                    Field fieldFeatures = worldHandler.getDeclaredField("features");
                    fieldFeatures.setAccessible(true);
                    ArrayList<IFeatureGenerator> features = (ArrayList<IFeatureGenerator>) fieldFeatures.get(null);
                    Iterator<IFeatureGenerator> it2 = features.iterator();
                    do {
                        if(!it2.hasNext()) break;
                        IFeatureGenerator feature = it2.next();
                        if(feature instanceof FeatureGenUniform) {
                            Class uniform = feature.getClass();
                            Field fieldWorldGen = uniform.getDeclaredField("worldGen");
                            fieldWorldGen.setAccessible(true);

                            // make it not final
                            Field modifiersField = Field.class.getDeclaredField("modifiers");
                            modifiersField.setAccessible(true);
                            modifiersField.setInt(fieldWorldGen, fieldWorldGen.getModifiers() & ~Modifier.FINAL);

                            WorldGenerator worldGen = (WorldGenerator) fieldWorldGen.get(feature);
                            if(worldGen instanceof WorldGenMinableCluster) {
                                Field fieldCluster = worldGen.getClass().getDeclaredField("cluster");
                                fieldCluster.setAccessible(true);
                                List<WeightedRandomBlock> cluster = (List<WeightedRandomBlock>) fieldCluster.get(worldGen);

                                Field fieldGenClusterSize = worldGen.getClass().getDeclaredField("genClusterSize");
                                fieldGenClusterSize.setAccessible(true);
                                int genClusterSize = (Integer) fieldGenClusterSize.get(worldGen);

                                Field fieldGenBlock = worldGen.getClass().getDeclaredField("genBlock");
                                fieldGenBlock.setAccessible(true);
                                WeightedRandomBlock[] genBlock = (WeightedRandomBlock[]) fieldGenBlock.get(worldGen);

                                fieldWorldGen.set(feature, new WorldGenMinableClusterOverride(cluster, genClusterSize, Arrays.asList(genBlock)));

                            } else if(worldGen instanceof WorldGenSparseMinableCluster) {
                                Field fieldCluster = worldGen.getClass().getDeclaredField("cluster");
                                fieldCluster.setAccessible(true);
                                List<WeightedRandomBlock> cluster = (List<WeightedRandomBlock>) fieldCluster.get(worldGen);

                                Field fieldGenClusterSize = worldGen.getClass().getDeclaredField("genClusterSize");
                                fieldGenClusterSize.setAccessible(true);
                                int genClusterSize = (Integer) fieldGenClusterSize.get(worldGen);

                                Field fieldGenBlock = worldGen.getClass().getDeclaredField("genBlock");
                                fieldGenBlock.setAccessible(true);
                                WeightedRandomBlock[] genBlock = (WeightedRandomBlock[]) fieldGenBlock.get(worldGen);

                                fieldWorldGen.set(feature, new WorldGenSparseMinableClusterOverride(cluster, genClusterSize, Arrays.asList(genBlock)));

                            }
                        }
                    } while(true);
                    features = (ArrayList<IFeatureGenerator>) fieldFeatures.get(null);
                    it2 = features.iterator();
                    do {
                        if (!it2.hasNext()) break;
                        IFeatureGenerator feature = it2.next();
                        if (feature instanceof FeatureGenUniform) {
                            Class uniform = feature.getClass();
                            Field fieldWorldGen = uniform.getDeclaredField("worldGen");
                            fieldWorldGen.setAccessible(true);
                            WorldGenerator worldGen = (WorldGenerator) fieldWorldGen.get(feature);
                            if(worldGen instanceof WorldGenMinableClusterOverride) {
                                Field fieldCluster = worldGen.getClass().getDeclaredField("cluster");
                                fieldCluster.setAccessible(true);
                                List<WeightedRandomBlock> cluster = (List<WeightedRandomBlock>) fieldCluster.get(worldGen);
                                for (WeightedRandomBlock block : cluster) {
                                    ABO.aboLog.info("Successfully replaced Uniform WorldGenMinableCluster in COFH: " + new ItemStack(block.block, 1, block.metadata).getDisplayName());
                                }
                            } else if(worldGen instanceof WorldGenSparseMinableClusterOverride) {
                                Field fieldCluster = worldGen.getClass().getDeclaredField("cluster");
                                fieldCluster.setAccessible(true);
                                List<WeightedRandomBlock> cluster = (List<WeightedRandomBlock>) fieldCluster.get(worldGen);
                                for (WeightedRandomBlock block : cluster) {
                                    ABO.aboLog.info("Successfully replaced Uniform WorldGenSparseMinableCluster in COFH: " + new ItemStack(block.block, 1, block.metadata).getDisplayName());
                                }
                            }
                        }
                    }while(true);
                }
            } while(true);
        }catch (Throwable t) {
            ABO.aboLog.catching(Level.INFO,t);}8*/
    }
}
