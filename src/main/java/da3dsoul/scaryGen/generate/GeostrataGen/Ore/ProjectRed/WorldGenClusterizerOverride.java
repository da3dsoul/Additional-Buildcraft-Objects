package da3dsoul.scaryGen.generate.GeostrataGen.Ore.ProjectRed;

import Reika.GeoStrata.Registry.RockTypes;
import Reika.GeoStrata.TileEntityGeoOre;
import mrtjp.core.world.TWorldGenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import scala.Tuple2;
import scala.collection.immutable.Set;

import java.util.Random;

public class WorldGenClusterizerOverride extends WorldGenerator implements TWorldGenerator {

    public Block clusterBlock = null;
    public int clusterMeta = 0;
    public int clusterSize = 0;
    private static Block oreBlock = null;

    public WorldGenClusterizerOverride(){}

    @Override
    public boolean generate(World w, Random rand, int x, int y, int z) {

        if (clusterSize < 4) {
            return generateSmall(w, rand, x, y, z);
        } else {
            return generateNormal(w, rand, x, y, z);
        }
    }

    public boolean setBlock(World w, int x, int y, int z, Tuple2<Block,Object> cluster, Set<Tuple2<Block,Object>> material) {
        return false;
    }

    public boolean setBlock(World w, int x, int y, int z, Set<Tuple2<Tuple2<Block,Object>,Object>> cluster, Set<Tuple2<Block,Object>> material) {
        return false;
    }

    public boolean canSetBlock(World w, int x, int y, int z, Set<Tuple2<Block,Object>> cluster) {
        return false;
    }

    private boolean generateSmall(World w, Random rand, int x, int y, int z)
    {
        boolean generated = false;
        for (int i = 0; i < clusterSize; i++)
        {
            int dx = x+rand.nextInt(2);
            int dy = y+rand.nextInt(2);
            int dz = z+rand.nextInt(2);

            Block target = w.getBlock(dx, dy, dz);
            if (target == Blocks.stone) {
                w.setBlock(dx, dy, dz, clusterBlock, clusterMeta, 2);
            } else {
                RockTypes rockType = RockTypes.getTypeFromID(target);
                if(oreBlock == null) {
                    oreBlock = (Block)Block.blockRegistry.getObject("GeoStrata:geostrata_block_oretile");
                }
                generated |= w.setBlock(dx, dy, dz, oreBlock, 0, 2);
                ((TileEntityGeoOre) w.getTileEntity(dx, dy, dz)).initialize(rockType, clusterBlock, clusterMeta);
            }
        }
        return generated;
    }

    private boolean generateNormal(World w, Random rand, int x, int y, int z) {
        float f = rand.nextFloat() * (float) Math.PI;
        float xNDir = x + 8 + (MathHelper.sin(f) * clusterSize) / 8F;
        float xPDir = x + 8 - (MathHelper.sin(f) * clusterSize) / 8F;
        float zNDir = z + 8 + (MathHelper.cos(f) * clusterSize) / 8F;
        float zPDir = z + 8 - (MathHelper.cos(f) * clusterSize) / 8F;
        float yNDir = (y + rand.nextInt(3)) - 2;
        float yPDir = (y + rand.nextInt(3)) - 2;

        float dx = xPDir - xNDir;
        float dy = yPDir - yNDir;
        float dz = zPDir - zNDir;

        boolean generated = false;
        for (int i = 0; i <= clusterSize; i++) {
            float xCenter = xNDir + (dx * i) / clusterSize;
            float yCenter = yNDir + (dy * i) / clusterSize;
            float zCenter = zNDir + (dz * i) / clusterSize;

            float size = ((float) rand.nextDouble() * clusterSize) / 16f;

            float hMod = ((MathHelper.sin((i * (float) Math.PI) / clusterSize) + 1f) * size + 1f) * 0.5f;
            float vMod = ((MathHelper.sin((i * (float) Math.PI) / clusterSize) + 1f) * size + 1f) * 0.5f;

            int x0 = MathHelper.floor_float(xCenter - hMod);
            int y0 = MathHelper.floor_float(yCenter - vMod);
            int z0 = MathHelper.floor_float(zCenter - hMod);

            int x1 = MathHelper.floor_float(xCenter + hMod);
            int y1 = MathHelper.floor_float(yCenter + vMod);
            int z1 = MathHelper.floor_float(zCenter + hMod);

            for (int blockX = x0; blockX <= x1; blockX++) {
                float xDistSq = ((blockX + 0.5f) - xCenter) / hMod;
                xDistSq *= xDistSq;
                if (xDistSq < 1f) {
                    for (int blockY = y0; blockY <= y1; blockY++) {
                        float yDistSq = ((blockY + 0.5f) - yCenter) / vMod;
                        yDistSq *= yDistSq;
                        float xyDistSq = yDistSq + xDistSq;
                        if (xyDistSq < 1f) {
                            for (int blockZ = z0; blockZ <= z1; blockZ++) {
                                float zDistSq = ((blockZ + 0.5f) - zCenter) / hMod;
                                zDistSq *= zDistSq;
                                if (zDistSq + xyDistSq < 1f ) {
                                    if(blockY <= 1) continue;
                                    Block target = w.getBlock(blockX, blockY, blockZ);
                                    if(target == null) continue;
                                    if(target.isReplaceableOreGen(w,blockX,blockY,blockZ,Blocks.stone) || target.isAssociatedBlock(Blocks.stone)) {
                                        if (target == Blocks.stone) {
                                            w.setBlock(blockX, blockY, blockZ, clusterBlock, clusterMeta, 2);
                                        } else {
                                            RockTypes rockType = RockTypes.getTypeFromID(target);
                                            if(oreBlock == null) {
                                                oreBlock = (Block)Block.blockRegistry.getObject("GeoStrata:geostrata_block_oretile");
                                            }
                                            generated |= w.setBlock(blockX, blockY, blockZ, oreBlock, 0, 2);
                                            TileEntity te = w.getTileEntity(blockX, blockY, blockZ);
                                            if(te == null) {
                                                Block b = w.getBlock(blockX,blockY,blockZ);
                                                if(b != oreBlock) continue;
                                                TileEntityGeoOre te2 = new TileEntityGeoOre();
                                                w.setTileEntity(blockX,blockY,blockZ, te2);
                                            }
                                            ((TileEntityGeoOre) w.getTileEntity(blockX, blockY, blockZ)).initialize(rockType, clusterBlock, clusterMeta);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return generated;
    }

}
