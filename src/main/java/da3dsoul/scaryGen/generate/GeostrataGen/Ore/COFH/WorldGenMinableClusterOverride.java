package da3dsoul.scaryGen.generate.GeostrataGen.Ore.COFH;

import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.GeoStrata.Registry.RockTypes;
import Reika.GeoStrata.TileEntityGeoOre;
import abo.ABO;
import cofh.lib.util.WeightedRandomBlock;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGenMinableClusterOverride extends WorldGenerator {

    private static Block oreBlock = null;

    private static boolean logged = false;

    private final List<WeightedRandomBlock> cluster;
    private final int genClusterSize;
    private final WeightedRandomBlock[] genBlock;

    public static final List<WeightedRandomBlock> fabricateList(WeightedRandomBlock var0) {
        ArrayList var1 = new ArrayList();
        var1.add(var0);
        return var1;
    }

    public static final List<WeightedRandomBlock> fabricateList(Block var0) {
        ArrayList var1 = new ArrayList();
        var1.add(new WeightedRandomBlock(new ItemStack(var0, 1, 0)));
        return var1;
    }

    public WorldGenMinableClusterOverride(ItemStack var1, int var2) {
        this(new WeightedRandomBlock(var1), var2);
    }

    public WorldGenMinableClusterOverride(WeightedRandomBlock var1, int var2) {
        this(fabricateList(var1), var2);
    }

    public WorldGenMinableClusterOverride(List<WeightedRandomBlock> var1, int var2) {
        this(var1, var2, Blocks.stone);
    }

    public WorldGenMinableClusterOverride(ItemStack var1, int var2, Block var3) {
        this(new WeightedRandomBlock(var1, 1), var2, var3);
    }

    public WorldGenMinableClusterOverride(WeightedRandomBlock var1, int var2, Block var3) {
        this(fabricateList(var1), var2, var3);
    }

    public WorldGenMinableClusterOverride(List<WeightedRandomBlock> var1, int var2, Block var3) {
        this(var1, var2, fabricateList(var3));
    }

    public WorldGenMinableClusterOverride(List<WeightedRandomBlock> var1, int var2, List<WeightedRandomBlock> var3) {
        this.cluster = var1;
        this.genClusterSize = var2 > 32?32:var2;
        this.genBlock = (WeightedRandomBlock[])var3.toArray(new WeightedRandomBlock[var3.size()]);
    }

    public boolean generate(World var1, Random var2, int var3, int var4, int var5) {
        int var6 = this.genClusterSize;
        if(var6 < 4) {
            return this.generateTiny(var1, var2, var3, var4, var5);
        } else {
            float var7 = var2.nextFloat() * 3.1415927F;
            float var8 = (float)(var3 + 8) + MathHelper.sin(var7) * (float)var6 / 8.0F;
            float var9 = (float)(var3 + 8) - MathHelper.sin(var7) * (float)var6 / 8.0F;
            float var10 = (float)(var5 + 8) + MathHelper.cos(var7) * (float)var6 / 8.0F;
            float var11 = (float)(var5 + 8) - MathHelper.cos(var7) * (float)var6 / 8.0F;
            float var12 = (float)(var4 + var2.nextInt(3) - 2);
            float var13 = (float)(var4 + var2.nextInt(3) - 2);
            var9 -= var8;
            var13 -= var12;
            var11 -= var10;
            boolean var14 = false;

            for(int var15 = 0; var15 <= var6; ++var15) {
                float var16 = var8 + var9 * (float)var15 / (float)var6;
                float var17 = var12 + var13 * (float)var15 / (float)var6;
                float var18 = var10 + var11 * (float)var15 / (float)var6;
                float var19 = (float)var2.nextDouble() * (float)var6 / 16.0F;
                float var20 = ((MathHelper.sin((float)var15 * 3.1415927F / (float)var6) + 1.0F) * var19 + 1.0F) * 0.5F;
                float var21 = ((MathHelper.sin((float)var15 * 3.1415927F / (float)var6) + 1.0F) * var19 + 1.0F) * 0.5F;
                int var22 = MathHelper.floor_float(var16 - var20);
                int var23 = MathHelper.floor_float(var17 - var21);
                int var24 = MathHelper.floor_float(var18 - var20);
                int var25 = MathHelper.floor_float(var16 + var20);
                int var26 = MathHelper.floor_float(var17 + var21);
                int var27 = MathHelper.floor_float(var18 + var20);

                for(int var28 = var22; var28 <= var25; ++var28) {
                    float var29 = ((float)var28 + 0.5F - var16) / var20;
                    var29 *= var29;
                    if(var29 < 1.0F) {
                        for(int var30 = var23; var30 <= var26; ++var30) {
                            float var31 = ((float)var30 + 0.5F - var17) / var21;
                            var31 *= var31;
                            float var32 = var31 + var29;
                            if(var32 < 1.0F) {
                                for(int var33 = var24; var33 <= var27; ++var33) {
                                    float var34 = ((float)var33 + 0.5F - var18) / var20;
                                    var34 *= var34;
                                    if(var34 + var32 < 1.0F) {
                                        var14 |= generateBlock(var1, var28, var30, var33, this.genBlock, this.cluster);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return var14;
        }
    }

    public boolean generateTiny(World var1, Random var2, int var3, int var4, int var5) {
        boolean var6 = false;

        for(int var7 = 0; var7 < this.genClusterSize; ++var7) {
            int var8 = var3 + var2.nextInt(2);
            int var9 = var4 + var2.nextInt(2);
            int var10 = var5 + var2.nextInt(2);
            var6 |= generateBlock(var1, var8, var9, var10, this.genBlock, this.cluster);
        }

        return var6;
    }

    public static boolean canGenerateInBlock(World var0, int var1, int var2, int var3, WeightedRandomBlock[] var4) {
        if(var4 != null && var4.length != 0) {
            Block var5 = var0.getBlock(var1, var2, var3);
            int var6 = 0;

            for(int var7 = var4.length; var6 < var7; ++var6) {
                WeightedRandomBlock var8 = var4[var6];
                if((-1 == var8.metadata || var8.metadata == var0.getBlockMetadata(var1, var2, var3)) && (var5.isReplaceableOreGen(var0, var1, var2, var3, var8.block) || var5.isAssociatedBlock(var8.block))) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }

    public static boolean generateBlock(World var0, int var1, int var2, int var3, WeightedRandomBlock[] var4, List<WeightedRandomBlock> var5) {
        return var4 != null && var4.length != 0?(canGenerateInBlock(var0, var1, var2, var3, var4)?generateBlock(var0, var1, var2, var3, var5):false):generateBlock(var0, var1, var2, var3, var5);
    }

    public static boolean generateBlock(World var0, int var1, int var2, int var3, List<WeightedRandomBlock> var4) {
        if(oreBlock == null) oreBlock = (Block)Block.blockRegistry.getObject("GeoStrata:geostrata_block_oretile");
        WeightedRandomBlock var5 = selectBlock(var0, var4);
        if(var5 == null) return false;
        Block target = var0.getBlock(var1,var2,var3);
        boolean returnBoolean = false;
        if(target == Blocks.stone) {
            returnBoolean = var0.setBlock(var1, var2, var3, var5.block, var5.metadata, 2);
        } else {
            RockTypes rockType = RockTypes.getTypeFromID(target);
            returnBoolean = var0.setBlock(var1, var2, var3, oreBlock, 0, 2);
            ((TileEntityGeoOre) var0.getTileEntity(var1, var2, var3)).initialize(rockType, var5.block, var5.metadata);
        }
        return returnBoolean;
    }

    public static WeightedRandomBlock selectBlock(World var0, List<WeightedRandomBlock> var1) {
        int var2 = var1.size();
        return var2 == 0?null:(var2 > 1?(WeightedRandomBlock) WeightedRandom.getRandomItem(var0.rand, var1):(WeightedRandomBlock)var1.get(0));
    }
}

