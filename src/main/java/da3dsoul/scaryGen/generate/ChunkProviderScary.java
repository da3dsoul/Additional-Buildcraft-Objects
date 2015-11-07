package da3dsoul.scaryGen.generate;

import java.lang.reflect.Field;
import java.util.*;

import abo.ABO;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import da3dsoul.scaryGen.generate.GeostrataGen.Ore.Galacticraft.GalactiCraftHandler;
import da3dsoul.scaryGen.generate.GeostrataGen.Ore.ProjectRed.ProjectRedHandler;
import da3dsoul.scaryGen.generate.GeostrataGen.Ore.Reika.ReactorCraft.ReactorOreGeneratorOverride;
import da3dsoul.scaryGen.generate.GeostrataGen.Ore.WorldGenMinableOverride;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.NoiseGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.Event.*;
import net.minecraftforge.event.terraingen.*;

public class ChunkProviderScary implements IChunkProvider {
    /** RNG. */
    private Random					rand;
    protected NoiseGeneratorOctaves	noiseGenOctaves1;
    protected NoiseGeneratorOctaves	noiseGenOctaves2;
    protected NoiseGeneratorOctaves	noiseGenOctaves3;
    protected NoiseGeneratorPerlin	noiseGenPerlin;
    /** A NoiseGeneratorOctaves used in generating terrain */
    public NoiseGeneratorOctaves	noiseGenOctaves5;
    /** A NoiseGeneratorOctaves used in generating terrain */
    public NoiseGeneratorOctaves	noiseGenOctaves6;
    public NoiseGeneratorOctaves	mobSpawnerNoise;
    /** Reference to the World object. */
    protected World					worldObj;
    /** are map structures going to be generated (e.g. strongholds) */
    private final boolean			mapFeaturesEnabled;
    protected WorldType				field_147435_p;
    protected final double[]		field_147434_q;
    protected final float[]			parabolicField;
    protected double[]				stoneNoise					= new double[256];
    private MapGenBase				caveGenerator				= new MapGenCaves();
    /** Holds Stronghold Generator */
    private MapGenStronghold		strongholdGenerator			= new MapGenStronghold();
    /** Holds Village Generator */
    private MapGenVillage			villageGenerator			= new MapGenVillage();
    /** Holds Mineshaft Generator */
    private MapGenMineshaft			mineshaftGenerator			= new MapGenMineshaft();
    private MapGenScatteredFeature	scatteredFeatureGenerator	= new MapGenScatteredFeature();
    /** Holds ravine generator */
    private MapGenBase				ravineGenerator				= new MapGenRavine();
    /** The biomes that are used to generate the chunk */
    protected BiomeGenBase[]		biomesForGeneration;
    double[]						noiseGenDoubleArray2;
    double[]						noiseGenDoubleArray3;
    double[]						noiseGenDoubleArray4;
    double[]						noiseGenDoubleArray1;
    int[][]							field_73219_j				= new int[32][32];
    @SuppressWarnings("unused")
    private static final String		__OBFID						= "CL_00000396";

    public byte	index;
    public int heightLevel;
    public int oceanLevel;
    public Block oceanReplacement;
    public boolean geostrataGen;
    private boolean initBiomeGenDecorators = false;

    {
        caveGenerator = TerrainGen.getModdedMapGen(caveGenerator, InitMapGenEvent.EventType.CAVE);
        strongholdGenerator = (MapGenStronghold) TerrainGen.getModdedMapGen(strongholdGenerator, InitMapGenEvent.EventType.STRONGHOLD);
        villageGenerator = (MapGenVillage) TerrainGen.getModdedMapGen(villageGenerator, InitMapGenEvent.EventType.VILLAGE);
        mineshaftGenerator = (MapGenMineshaft) TerrainGen.getModdedMapGen(mineshaftGenerator, InitMapGenEvent.EventType.MINESHAFT);
        scatteredFeatureGenerator = (MapGenScatteredFeature) TerrainGen.getModdedMapGen(scatteredFeatureGenerator,
                InitMapGenEvent.EventType.SCATTERED_FEATURE);
        ravineGenerator = TerrainGen.getModdedMapGen(ravineGenerator, InitMapGenEvent.EventType.RAVINE);
    }

    public ChunkProviderScary(World world, long seed, boolean mapFeatures, byte i, int hLevel, int oLevel, Block oReplace, boolean geostrataGen) {
        this.worldObj = world;
        this.mapFeaturesEnabled = mapFeatures;
        this.field_147435_p = world.getWorldInfo().getTerrainType();
        this.rand = new Random(seed);
        this.noiseGenOctaves1 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseGenOctaves2 = new NoiseGeneratorOctaves(this.rand, 16);
        this.noiseGenOctaves3 = new NoiseGeneratorOctaves(this.rand, 8);
        this.noiseGenPerlin = new NoiseGeneratorPerlin(this.rand, 4);
        this.noiseGenOctaves5 = new NoiseGeneratorOctaves(this.rand, 10);
        this.noiseGenOctaves6 = new NoiseGeneratorOctaves(this.rand, 16);
        this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
        this.field_147434_q = new double[825];
        this.parabolicField = new float[25];

        for (int j = -2; j <= 2; ++j) {
            for (int k = -2; k <= 2; ++k) {
                float f = 10.0F / MathHelper.sqrt_float((float) (j * j + k * k) + 0.2F);
                this.parabolicField[j + 2 + (k + 2) * 5] = f;
            }
        }

        NoiseGenerator[] noiseGens = { noiseGenOctaves1, noiseGenOctaves2, noiseGenOctaves3, noiseGenPerlin,
                noiseGenOctaves5, noiseGenOctaves6, mobSpawnerNoise };
        noiseGens = TerrainGen.getModdedNoiseGenerators(world, this.rand, noiseGens);
        this.noiseGenOctaves1 = (NoiseGeneratorOctaves) noiseGens[0];
        this.noiseGenOctaves2 = (NoiseGeneratorOctaves) noiseGens[1];
        this.noiseGenOctaves3 = (NoiseGeneratorOctaves) noiseGens[2];
        this.noiseGenPerlin = (NoiseGeneratorPerlin) noiseGens[3];
        this.noiseGenOctaves5 = (NoiseGeneratorOctaves) noiseGens[4];
        this.noiseGenOctaves6 = (NoiseGeneratorOctaves) noiseGens[5];
        this.mobSpawnerNoise = (NoiseGeneratorOctaves) noiseGens[6];

        index = i;
        heightLevel = hLevel;
        oceanLevel = oLevel;
        oceanReplacement = oReplace;
        this.geostrataGen = geostrataGen;
        if(geostrataGen) {
            try {
                Class gameRegistry = GameRegistry.class;
                Field worldGeneratorIndex = gameRegistry.getDeclaredField("worldGeneratorIndex");
                worldGeneratorIndex.setAccessible(true);
                Field worldGenerators = gameRegistry.getDeclaredField("worldGenerators");
                worldGenerators.setAccessible(true);
                Set<IWorldGenerator> iWorldGenerators = (Set<IWorldGenerator>) worldGenerators.get(null);
                Map<IWorldGenerator, Integer> iWorldGeneratorIntegerMap = (Map<IWorldGenerator, Integer>) worldGeneratorIndex.get(null);
                Iterator<IWorldGenerator> it = iWorldGenerators.iterator();
                HashMap<IWorldGenerator, Integer> toAdd = new HashMap<IWorldGenerator, Integer>();
                int j = 0;
                ABO.aboLog.info("GeoStrata is " + (ABO.geostrataInstalled ? "" : "not ") + "Installed");
                do {
                    if (!ABO.geostrataInstalled) break;
                    if (!it.hasNext()) break;
                    IWorldGenerator gen = it.next();
                    if (gen.getClass().getSimpleName().equalsIgnoreCase("RockGenerator")) {
                        j++;
                        iWorldGeneratorIntegerMap.remove(gen);
                        it.remove();
                        continue;
                    }
                    if (gen.getClass().getSimpleName().equalsIgnoreCase("ReactorOreGenerator")) {
                        j++;
                        toAdd.put(ReactorOreGeneratorOverride.instance, iWorldGeneratorIntegerMap.get(gen));
                        iWorldGeneratorIntegerMap.remove(gen);
                        it.remove();
                    }
                    if (gen.getClass().getSimpleName().equalsIgnoreCase("OverworldGenerator")) {
                        j++;
                        iWorldGeneratorIntegerMap.remove(gen);
                        it.remove();
                        GalactiCraftHandler.addGeneratorOverrides(toAdd);
                    }

                    if (gen.getClass().getSimpleName().equalsIgnoreCase("SimpleGenHandler") || gen.getClass().getSimpleName().equalsIgnoreCase("SimpleGenHandler$")) {
                        ProjectRedHandler.override(gen);
                    }
                } while (true);

                ABO.aboLog.info("Unregistered " + j + " ore " + (j > 1 ? "generators" : "generator") + " from World Generation");

                it = toAdd.keySet().iterator();
                j = 0;
                do {
                    if (!it.hasNext()) break;
                    IWorldGenerator gen = it.next();
                    iWorldGenerators.add(gen);
                    iWorldGeneratorIntegerMap.put(gen, toAdd.get(gen));
                    j++;
                } while(true);

                ABO.aboLog.info("Added " + j + " generator " + (j > 1 ? "overrides" : "override") + " to World Generation");
            } catch (Throwable t) {
                ABO.aboLog.warn("Unable to modify ore gen for scaryGen");
                t.printStackTrace();
            }
            if(ABO.geostrataInstalled) {
                try {
                    for(BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
                        if(biome != null && biome.theBiomeDecorator != null) {
                            biome.theBiomeDecorator.coalGen = new WorldGenMinableOverride(Blocks.coal_ore, 16);
                            biome.theBiomeDecorator.ironGen = new WorldGenMinableOverride(Blocks.iron_ore, 8);
                            biome.theBiomeDecorator.goldGen = new WorldGenMinableOverride(Blocks.gold_ore, 8);
                            biome.theBiomeDecorator.redstoneGen = new WorldGenMinableOverride(Blocks.redstone_ore, 7);
                            biome.theBiomeDecorator.diamondGen = new WorldGenMinableOverride(Blocks.diamond_ore, 7);
                            biome.theBiomeDecorator.lapisGen = new WorldGenMinableOverride(Blocks.lapis_ore, 6);
                            ABO.aboLog.info("Replaced ore gen for " + biome.biomeName + " with geoStrata custom ore gen");
                        }
                    }

                }catch (Throwable e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void generateTerrain(int par1, int par2, Block[] blockArray) {
        int b0 = oceanLevel;
        worldObj.theProfiler.startSection("GenerateBiomes");
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(
                this.biomesForGeneration, par1 * 4 - 2, par2 * 4 - 2, 10, 10);
        worldObj.theProfiler.endStartSection("initNoiseFields");
        this.initNoiseFields(par1 * 4, 0, par2 * 4);
        worldObj.theProfiler.endStartSection("ActualStoneGeneration");

        for (int k = 0; k < 4; ++k) {
            int l = k * 5;
            int i1 = (k + 1) * 5;

            for (int j1 = 0; j1 < 4; ++j1) {
                int k1 = (l + j1) * 33;
                int l1 = (l + j1 + 1) * 33;
                int i2 = (i1 + j1) * 33;
                int j2 = (i1 + j1 + 1) * 33;

                for (int k2 = 0; k2 < 32; ++k2) {
                    double d0 = 0.125D;
                    double d1 = this.field_147434_q[k1 + k2];
                    double d2 = this.field_147434_q[l1 + k2];
                    double d3 = this.field_147434_q[i2 + k2];
                    double d4 = this.field_147434_q[j2 + k2];
                    double d5 = (this.field_147434_q[k1 + k2 + 1] - d1) * d0;
                    double d6 = (this.field_147434_q[l1 + k2 + 1] - d2) * d0;
                    double d7 = (this.field_147434_q[i2 + k2 + 1] - d3) * d0;
                    double d8 = (this.field_147434_q[j2 + k2 + 1] - d4) * d0;

                    for (int l2 = 0; l2 < 8; ++l2) {
                        double d9 = 0.35D;
                        double d10 = d1;
                        double d11 = d2;
                        double d12 = (d3 - d1) * d9;
                        double d13 = (d4 - d2) * d9;

                        for (int i3 = 0; i3 < 4; ++i3) {
                            int j3 = ((i3 + k * 4) << 12) | ((0 + j1 * 4) << 8) | (k2 * 8 + l2);
                            short short1 = 256;
                            j3 -= short1;
                            double d14 = 0.25D;
                            double d16 = (d11 - d10) * d14;
                            double d15 = d10 - d16;

                            for (int k3 = 0; k3 < 4; ++k3) {
                                if ((d15 += d16) > 0.0D) {
                                    if(k2 * 8 + l2 < heightLevel)
                                    {
                                        blockArray[j3 += short1] = Blocks.stone;
                                    } else {
                                        blockArray[j3 += short1] = null;
                                    }
                                } else if (k2 * 8 + l2 < b0) {
                                    blockArray[j3 += short1] = oceanReplacement;
                                } else {
                                    blockArray[j3 += short1] = null;
                                }
                            }

                            d10 += d12;
                            d11 += d13;
                        }

                        d1 += d5;
                        d2 += d6;
                        d3 += d7;
                        d4 += d8;
                    }
                }
            }
        }
        worldObj.theProfiler.endSection();
    }

    private double[] applyScaryNoise1(double d, double d1) {
        double[] a = new double[2];
        a[0] = 0.25 * exp(d, 3) * d1 + 0.125 * exp(d1, 3);
        a[1] = 0.25 * exp(d1, 2) + d * d1 + 0.125 * exp(d, 2);
        return a;
    }

    private double[] applyScaryNoise2(double d, double d1) {
        double[] a = new double[2];
        a[0] = d * d1 - exp(d, 2) + exp(d1, 2);
        a[1] = d * d1 + exp(d, 2) - exp(d1, 2);
        return a;
    }

    private double[] applyScaryNoise3(double d, double d1) {
        double[] a = new double[2];
        a[0] = ((d + d1) * (d - d1) * (d)) / (d1 * (d1 - d));
        a[1] = ((d + d1) * (d1 - d) * (d1)) / (d * (d - d1));
        return a;
    }

    private double[] applyScaryNoise4(double d, double d1) {
        double[] a = new double[2];
        a[0] = (exp(0.05 * d - 0.0125 * d1, 5)) * sign(d);
        a[1] = (exp(0.05 * d1 - 0.0125 * d, 5)) * sign(d1);
        return a;
    }

    private double[] applyScaryNoise5(double d, double d1) {
        double[] a = new double[2];
        a[0] = 0.25 * exp(d, 2) + d * d1 + 0.125 * exp(d1, 2);
        a[1] = 0.25 * exp(d1, 2) + d * d1 + 0.125 * exp(d, 2);
        return a;
    }

    private double[] applyScaryNoise6(double d, double d1) {
        double[] a = new double[2];
        a[0] = (exp(0.05 * d - 0.0125 * d1, 5)) * sign(d);
        a[1] = (exp(0.05 * d1 - 0.0125 * d, 5)) * sign(d1);
        return a;
    }

    private double exp(double d, int d1) {
        double a = d;
        do {
            if (d1 <= 0) break;
            a *= d;
            d1--;
        } while (true);
        return a;
    }

    private byte sign(double d) {
        return (byte) (d < 0 ? -1 : 1);
    }

    private void setupNoiseGensDefault(int par1, int par2, int par3) {
        this.noiseGenDoubleArray1 = this.noiseGenOctaves6.generateNoiseOctaves(this.noiseGenDoubleArray1, par1, par3,
                5, 5, 200.0D, 200.0D, 0.5D);
        this.noiseGenDoubleArray2 = this.noiseGenOctaves3.generateNoiseOctaves(this.noiseGenDoubleArray2, par1, par2,
                par3, 5, 33, 5, 8.555150000000001D, 4.277575000000001D, 8.555150000000001D);
        this.noiseGenDoubleArray3 = this.noiseGenOctaves1.generateNoiseOctaves(this.noiseGenDoubleArray3, par1, par2,
                par3, 5, 33, 5, 684.412D, 684.412D, 684.412D);
        this.noiseGenDoubleArray4 = this.noiseGenOctaves2.generateNoiseOctaves(this.noiseGenDoubleArray4, par1, par2,
                par3, 5, 33, 5, 684.412D, 684.412D, 684.412D);
    }

    private void setupNoiseGens1(int par1, int par2, int par3) {
        this.noiseGenDoubleArray1 = this.noiseGenOctaves6.generateNoiseOctaves(this.noiseGenDoubleArray1, par1, par3,
                5, 5, 260.0D, 120.0D, 0.8D);
        this.noiseGenDoubleArray2 = this.noiseGenOctaves3.generateNoiseOctaves(this.noiseGenDoubleArray2, par1, par2,
                par3, 5, 33, 5, 0.106939375D, 0.7310D, 0.0855515D);
        this.noiseGenDoubleArray3 = this.noiseGenOctaves1.generateNoiseOctaves(this.noiseGenDoubleArray3, par1, par2,
                par3, 5, 33, 5, 684.412D, 821.2944D, 701.5223D);
        this.noiseGenDoubleArray4 = this.noiseGenOctaves2.generateNoiseOctaves(this.noiseGenDoubleArray4, par1, par2,
                par3, 5, 33, 5, 684.412D, 804.412D, 764.412D);
    }

    private void setupNoiseGens4(int par1, int par2, int par3) {
        this.noiseGenDoubleArray1 = this.noiseGenOctaves6.generateNoiseOctaves(this.noiseGenDoubleArray1, par1, par3,
                5, 5, 240.0D, 240.0D, 1.5D);
        this.noiseGenDoubleArray2 = this.noiseGenOctaves3.generateNoiseOctaves(this.noiseGenDoubleArray2, par1, par2,
                par3, 5, 33, 5, 0.4277575D, 0.01671D, 0.4277575D);
        this.noiseGenDoubleArray3 = this.noiseGenOctaves1.generateNoiseOctaves(this.noiseGenDoubleArray3, par1, par2,
                par3, 5, 33, 5, 684.412D, 684.412D, 684.412D);
        this.noiseGenDoubleArray4 = this.noiseGenOctaves2.generateNoiseOctaves(this.noiseGenDoubleArray4, par1, par2,
                par3, 5, 33, 5, 684.412D, 684.412D, 684.412D);
    }

    public void replaceBlocksForBiome(int p_147422_1_, int p_147422_2_, Block[] p_147422_3_, byte[] p_147422_4_,
                                      BiomeGenBase[] p_147422_5_) {
        ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(this, p_147422_1_,
                p_147422_2_, p_147422_3_, p_147422_4_, p_147422_5_, this.worldObj);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.getResult() == Result.DENY) return;

        double d0 = 0.03125D;
        this.stoneNoise = this.noiseGenPerlin.func_151599_a(this.stoneNoise, (double) (p_147422_1_ * 16),
                (double) (p_147422_2_ * 16), 16, 16, d0 * 2.0D, d0 * 2.0D, 1.0D);

        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                BiomeGenBase biomegenbase = p_147422_5_[l + k * 16];
                biomegenbase.genTerrainBlocks(this.worldObj, this.rand, p_147422_3_, p_147422_4_, p_147422_1_ * 16 + k,
                        p_147422_2_ * 16 + l, this.stoneNoise[l + k * 16]);
            }
        }
    }

    private void geostrataGen(int x, int z, Block[] blocks, byte[] meta, BiomeGenBase[] biomes){
        int k = blocks.length / 256;
        for (int blockX = 0; blockX < 16; ++blockX)
        {
            for (int blockZ = 0; blockZ < 16; ++blockZ) {
                for (int y = 0; y < k; ++y) {
                    int k1 = blockX * k * 16 | blockZ * k | y;
                    if (blocks[k1] == Blocks.stone) {
                        BiomeGenBase biomegenbase = biomes[blockX + blockZ * 16];

                        Block[] stoneBlocks = BiomeStoneGen.biomeStoneArray[biomegenbase.biomeID];
                        if (stoneBlocks == null || stoneBlocks.length <= 0) continue;

                        blocks[k1] = stoneBlocks[rand.nextInt(stoneBlocks.length)];
                    }
                }
            }
        }
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
    public Chunk loadChunk(int p_73158_1_, int p_73158_2_) {
        return this.provideChunk(p_73158_1_, p_73158_2_);
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it
     * will generates all the blocks for the specified chunk from the map seed
     * and chunk seed
     */
    public Chunk provideChunk(int p_73154_1_, int p_73154_2_) {


        worldObj.theProfiler.startSection("scaryGen");
        this.rand.setSeed((long) p_73154_1_ * 341873128712L + (long) p_73154_2_ * 132897987541L);
        Block[] ablock = new Block[65536];
        byte[] abyte = new byte[65536];
        worldObj.theProfiler.startSection("GenerateStone");
        this.generateTerrain(p_73154_1_, p_73154_2_, ablock);
        worldObj.theProfiler.endStartSection("FindBiomes");
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(
                this.biomesForGeneration, p_73154_1_ * 16, p_73154_2_ * 16, 16, 16);
        BlockFalling.fallInstantly = true;
        worldObj.theProfiler.endStartSection("ReplaceBlocksForBiome");
        this.replaceBlocksForBiome(p_73154_1_, p_73154_2_, ablock, abyte, this.biomesForGeneration);
        worldObj.theProfiler.endStartSection("Caves");
        this.caveGenerator.func_151539_a(this, this.worldObj, p_73154_1_, p_73154_2_, ablock);
        worldObj.theProfiler.endStartSection("Ravines");
        this.ravineGenerator.func_151539_a(this, this.worldObj, p_73154_1_, p_73154_2_, ablock);
        worldObj.theProfiler.endSection();
        if(((ChunkProviderScary)this).geostrataGen) {
            worldObj.theProfiler.startSection("GeostrataGen");
            geostrataGen(p_73154_1_, p_73154_2_, ablock, abyte, this.biomesForGeneration);
            worldObj.theProfiler.endSection();
        }

        if (this.mapFeaturesEnabled) {
            worldObj.theProfiler.startSection("MapFeatures");
            worldObj.theProfiler.startSection("Mineshaft");
            this.mineshaftGenerator.func_151539_a(this, this.worldObj, p_73154_1_, p_73154_2_, ablock);
            worldObj.theProfiler.endStartSection("Village");
            this.villageGenerator.func_151539_a(this, this.worldObj, p_73154_1_, p_73154_2_, ablock);
            worldObj.theProfiler.endStartSection("Stronghold");
            this.strongholdGenerator.func_151539_a(this, this.worldObj, p_73154_1_, p_73154_2_, ablock);
            worldObj.theProfiler.endStartSection("ScatteredFeature");
            this.scatteredFeatureGenerator.func_151539_a(this, this.worldObj, p_73154_1_, p_73154_2_, ablock);
            worldObj.theProfiler.endSection();
            worldObj.theProfiler.endSection();
        }

        BlockFalling.fallInstantly = false;
        Chunk chunk = new Chunk(this.worldObj, ablock, abyte, p_73154_1_, p_73154_2_);
        byte[] abyte1 = chunk.getBiomeArray();

        for (int k = 0; k < abyte1.length; ++k) {
            abyte1[k] = (byte) this.biomesForGeneration[k].biomeID;
        }

        worldObj.theProfiler.startSection("GenerateSkylight");
        chunk.generateSkylightMap();
        worldObj.theProfiler.endSection();
        worldObj.theProfiler.endSection();
        return chunk;
    }

    @SuppressWarnings("unused")
    protected void initNoiseFields(int par1, int par2, int par3) {
        byte sc = index;

        double d0 = 684.412D;
        double d1 = 684.412D;
        double d2 = 512.0D;
        double d3 = 512.0D;

        worldObj.theProfiler.startSection("ScaryGenSetupNoiseGens");
        switch (sc) {
            case 1:
                setupNoiseGens1(par1, par2, par3);
                break;
            case 4:
                setupNoiseGens4(par1, par2, par3);
                break;
            default:
                setupNoiseGensDefault(par1, par2, par3);
                break;
        }
        worldObj.theProfiler.endSection();

        boolean flag1 = false;
        boolean flag = false;
        int l = 0;
        int i1 = 0;
        double d4 = 8.5D;

        for (int j1 = 0; j1 < 5; ++j1) {
            for (int k1 = 0; k1 < 5; ++k1) {
                float f = 0.0F;
                float f1 = 0.0F;
                float f2 = 0.0F;
                byte b0 = 2;
                BiomeGenBase biomegenbase = this.biomesForGeneration[j1 + 2 + (k1 + 2) * 10];

                for (int l1 = -b0; l1 <= b0; ++l1) {
                    for (int i2 = -b0; i2 <= b0; ++i2) {
                        BiomeGenBase biomegenbase1 = this.biomesForGeneration[j1 + l1 + 2 + (k1 + i2 + 2) * 10];
                        float f3 = biomegenbase1.rootHeight;
                        float f4 = biomegenbase1.heightVariation;

                        if (this.field_147435_p == WorldType.AMPLIFIED && f3 > 0.0F) {
                            f3 = 1.0F + f3 * 2.0F;
                            f4 = 1.0F + f4 * 4.0F;
                        }

                        float f5 = this.parabolicField[l1 + 2 + (i2 + 2) * 5] / (f3 + 2.0F);

                        if (biomegenbase1.rootHeight > biomegenbase.rootHeight) {
                            f5 /= 2.0F;
                        }

                        f += f4 * f5;
                        f1 += f3 * f5;
                        f2 += f5;
                    }
                }

                f /= f2;
                f1 /= f2;
                f = f * 0.9F + 0.1F;
                f1 = (f1 * 4.0F - 1.0F) / 8.0F;
                double d12 = this.noiseGenDoubleArray1[i1] / 8000.0D;

                if (d12 < 0.0D) {
                    d12 = -d12 * 0.3D;
                }

                d12 = d12 * 3.0D - 2.0D;

                if (d12 < 0.0D) {
                    d12 /= 2.0D;

                    if (d12 < -1.0D) {
                        d12 = -1.0D;
                    }

                    d12 /= 1.4D;
                    d12 /= 2.0D;
                } else {
                    if (d12 > 1.0D) {
                        d12 = 1.0D;
                    }

                    d12 /= 8.0D;
                }

                ++i1;
                double d13 = (double) f1;
                double d14 = (double) f;
                d13 += d12 * 0.2D;
                d13 = d13 * 8.5D / 8.0D;
                double d5 = 8.5D + d13 * 4.0D;

                for (int j2 = 0; j2 < 33; ++j2) {
                    double d6 = ((double) j2 - d5) * 12.0D * 128.0D / 256.0D / d14;

                    if (d6 < 0.0D) {
                        d6 *= 4.0D;
                    }

                    double d7 = this.noiseGenDoubleArray3[l] / 512.0D;
                    double d8 = this.noiseGenDoubleArray4[l] / 512.0D;
                    double d9 = (this.noiseGenDoubleArray2[l] / 10.0D + 1.0D) / 2.0D;

                    double a[] = null;

                    worldObj.theProfiler.startSection("ScaryGenApplyNoiseGenModifier");
                    switch (sc) {
                        case 0:
                            a = new double[] { d7, d8 };
                            break;
                        case 1:
                            a = applyScaryNoise1(d7, d8);
                            break;
                        case 2:
                            a = applyScaryNoise2(d7, d8);
                            break;
                        case 3:
                            a = applyScaryNoise3(d7, d8);
                            break;
                        case 4:
                            a = applyScaryNoise4(d7, d8);
                            break;
                        case 5:
                            a = applyScaryNoise5(d7, d8);
                            break;
                        case 6:
                            a = applyScaryNoise6(d7, d8);
                            break;
                        default:
                            a = applyScaryNoise1(d7, d8);
                            break;
                    }
                    worldObj.theProfiler.endSection();

                    d7 = a[0];
                    d8 = a[1];

                    double d10 = MathHelper.denormalizeClamp(d7, d8, d9) - d6;

                    if (j2 > 29) {
                        double d11 = (double) ((float) (j2 - 29) / 3.0F);
                        d10 = d10 * (1.0D - d11) + -10.0D * d11;
                    }

                    this.field_147434_q[l] = d10;
                    ++l;
                }
            }
        }
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    public boolean chunkExists(int p_73149_1_, int p_73149_2_) {
        return true;
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_) {
        BlockFalling.fallInstantly = true;
        int k = p_73153_2_ * 16;
        int l = p_73153_3_ * 16;
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(k + 16, l + 16);
        this.rand.setSeed(this.worldObj.getSeed());
        long i1 = this.rand.nextLong() / 2L * 2L + 1L;
        long j1 = this.rand.nextLong() / 2L * 2L + 1L;
        this.rand.setSeed((long) p_73153_2_ * i1 + (long) p_73153_3_ * j1 ^ this.worldObj.getSeed());
        boolean flag = false;

        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(p_73153_1_, worldObj, rand, p_73153_2_, p_73153_3_,
                flag));

        if (this.mapFeaturesEnabled) {
            this.mineshaftGenerator.generateStructuresInChunk(this.worldObj, this.rand, p_73153_2_, p_73153_3_);
            flag = this.villageGenerator.generateStructuresInChunk(this.worldObj, this.rand, p_73153_2_, p_73153_3_);
            this.strongholdGenerator.generateStructuresInChunk(this.worldObj, this.rand, p_73153_2_, p_73153_3_);
            this.scatteredFeatureGenerator.generateStructuresInChunk(this.worldObj, this.rand, p_73153_2_, p_73153_3_);
        }

        int k1;
        int l1;
        int i2;

        if (biomegenbase != BiomeGenBase.desert && biomegenbase != BiomeGenBase.desertHills && !flag
                && this.rand.nextInt(4) == 0
                && TerrainGen.populate(p_73153_1_, worldObj, rand, p_73153_2_, p_73153_3_, flag, PopulateChunkEvent.Populate.EventType.LAKE)) {
            k1 = k + this.rand.nextInt(16) + 8;
            l1 = this.rand.nextInt(256);
            i2 = l + this.rand.nextInt(16) + 8;
            (new WorldGenLakes(Blocks.water)).generate(this.worldObj, this.rand, k1, l1, i2);
        }

        if (TerrainGen.populate(p_73153_1_, worldObj, rand, p_73153_2_, p_73153_3_, flag, PopulateChunkEvent.Populate.EventType.LAVA) && !flag
                && this.rand.nextInt(8) == 0) {
            k1 = k + this.rand.nextInt(16) + 8;
            l1 = this.rand.nextInt(this.rand.nextInt(248) + 8);
            i2 = l + this.rand.nextInt(16) + 8;

            if (l1 < 63 || this.rand.nextInt(10) == 0) {
                (new WorldGenLakes(Blocks.lava)).generate(this.worldObj, this.rand, k1, l1, i2);
            }
        }

        boolean doGen = TerrainGen.populate(p_73153_1_, worldObj, rand, p_73153_2_, p_73153_3_, flag, PopulateChunkEvent.Populate.EventType.DUNGEON);
        for (k1 = 0; doGen && k1 < 8; ++k1) {
            l1 = k + this.rand.nextInt(16) + 8;
            i2 = this.rand.nextInt(256);
            int j2 = l + this.rand.nextInt(16) + 8;
            (new WorldGenDungeons()).generate(this.worldObj, this.rand, l1, i2, j2);
        }

        biomegenbase.decorate(this.worldObj, this.rand, k, l);

        if (TerrainGen.populate(p_73153_1_, worldObj, rand, p_73153_2_, p_73153_3_, flag, PopulateChunkEvent.Populate.EventType.ANIMALS)) {
            SpawnerAnimals.performWorldGenSpawning(this.worldObj, biomegenbase, k + 8, l + 8, 16, 16, this.rand);
        }
        k += 8;
        l += 8;

        doGen = TerrainGen.populate(p_73153_1_, worldObj, rand, p_73153_2_, p_73153_3_, flag, PopulateChunkEvent.Populate.EventType.ICE);
        for (k1 = 0; doGen && k1 < 16; ++k1) {
            for (l1 = 0; l1 < 16; ++l1) {
                i2 = this.worldObj.getPrecipitationHeight(k + k1, l + l1);

                if (this.worldObj.isBlockFreezable(k1 + k, i2 - 1, l1 + l)) {
                    this.worldObj.setBlock(k1 + k, i2 - 1, l1 + l, Blocks.ice, 0, 2);
                }

                if (this.worldObj.func_147478_e(k1 + k, i2, l1 + l, true)) {
                    this.worldObj.setBlock(k1 + k, i2, l1 + l, Blocks.snow_layer, 0, 2);
                }
            }
        }

        MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(p_73153_1_, worldObj, rand, p_73153_2_, p_73153_3_,
                flag));

        BlockFalling.fallInstantly = false;
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go. If
     * passed false, save up to two chunks. Return true if all chunks have been
     * saved.
     */
    public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_) {
        return true;
    }

    /**
     * Save extra data not associated with any Chunk. Not saved during autosave,
     * only during world unload. Currently unimplemented.
     */
    public void saveExtraData() {}

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to
     * unload every such chunk.
     */
    public boolean unloadQueuedChunks() {
        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave() {
        return true;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString() {
        return "RandomLevelSource";
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the
     * given location.
     */
    public List getPossibleCreatures(EnumCreatureType p_73155_1_, int p_73155_2_, int p_73155_3_, int p_73155_4_) {
        BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(p_73155_2_, p_73155_4_);
        return p_73155_1_ == EnumCreatureType.monster
                && this.scatteredFeatureGenerator.func_143030_a(p_73155_2_, p_73155_3_, p_73155_4_) ? this.scatteredFeatureGenerator
                .getScatteredFeatureSpawnList() : biomegenbase.getSpawnableList(p_73155_1_);
    }

    public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_,
                                       int p_147416_5_) {
        return "Stronghold".equals(p_147416_2_) && this.strongholdGenerator != null ? this.strongholdGenerator
                .func_151545_a(p_147416_1_, p_147416_3_, p_147416_4_, p_147416_5_) : null;
    }

    public int getLoadedChunkCount() {
        return 0;
    }

    public void recreateStructures(int p_82695_1_, int p_82695_2_) {
        if (this.mapFeaturesEnabled) {
            this.mineshaftGenerator.func_151539_a(this, this.worldObj, p_82695_1_, p_82695_2_, (Block[]) null);
            this.villageGenerator.func_151539_a(this, this.worldObj, p_82695_1_, p_82695_2_, (Block[]) null);
            this.strongholdGenerator.func_151539_a(this, this.worldObj, p_82695_1_, p_82695_2_, (Block[]) null);
            this.scatteredFeatureGenerator.func_151539_a(this, this.worldObj, p_82695_1_, p_82695_2_, (Block[]) null);
        }
    }
}
