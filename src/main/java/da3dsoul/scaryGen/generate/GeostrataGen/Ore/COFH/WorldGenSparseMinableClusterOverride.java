package da3dsoul.scaryGen.generate.GeostrataGen.Ore.COFH;

import cofh.lib.util.WeightedRandomBlock;
import cofh.lib.world.WorldGenSparseMinableCluster;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class WorldGenSparseMinableClusterOverride extends WorldGenSparseMinableCluster{

    private final List<WeightedRandomBlock> cluster;
    private final int genClusterSize;
    private final WeightedRandomBlock[] genBlock;

    public WorldGenSparseMinableClusterOverride(List<WeightedRandomBlock> list, int i, List<WeightedRandomBlock> list1) {
        super(list, i, list1);
        cluster = list;
        genClusterSize = i;
        genBlock = list1.toArray(new WeightedRandomBlock[list1.size()]);
    }

    public boolean generate(World paramWorld, Random paramRandom, int paramInt1, int paramInt2, int paramInt3)
    {
        int i = this.genClusterSize;
        float f1 = paramRandom.nextFloat() * 3.141593F;

        float f2 = paramInt2 + paramRandom.nextInt(3) - 2;
        float f3 = paramInt2 + paramRandom.nextInt(3) - 2;
        if ((i == 1) && (f2 > f3)) {
            i++;
        }
        if ((i == 2) && (f1 > 1.570796F)) {
            i++;
        }
        float f4 = paramInt1 + 8 + MathHelper.sin(f1) * i / 8.0F;
        float f5 = paramInt1 + 8 - MathHelper.sin(f1) * i / 8.0F;
        float f6 = paramInt3 + 8 + MathHelper.cos(f1) * i / 8.0F;
        float f7 = paramInt3 + 8 - MathHelper.cos(f1) * i / 8.0F;


        f5 -= f4;
        f3 -= f2;
        f7 -= f6;

        boolean bool = false;
        for (int j = 0; j <= i; j++)
        {
            float f8 = f4 + f5 * j / i;
            float f9 = f2 + f3 * j / i;
            float f10 = f6 + f7 * j / i;


            float f11 = (float)paramRandom.nextDouble() * i / 16.0F;

            float f12 = ((MathHelper.sin(j * 3.141593F / i) + 1.0F) * f11 + 1.0F) * 0.5F;
            float f13 = ((MathHelper.sin(j * 3.141593F / i) + 1.0F) * f11 + 1.0F) * 0.5F;

            int k = MathHelper.floor_float(f8 - f12);
            int m = MathHelper.floor_float(f9 - f13);
            int n = MathHelper.floor_float(f10 - f12);

            int i1 = MathHelper.floor_float(f8 + f12);
            int i2 = MathHelper.floor_float(f9 + f13);
            int i3 = MathHelper.floor_float(f10 + f12);
            for (int i4 = k; i4 <= i1; i4++)
            {
                float f14 = (i4 + 0.5F - f8) / f12;
                f14 *= f14;
                if (f14 < 1.0F) {
                    for (int i5 = m; i5 <= i2; i5++)
                    {
                        float f15 = (i5 + 0.5F - f9) / f13;
                        f15 *= f15;
                        float f16 = f15 + f14;
                        if (f16 < 1.0F) {
                            for (int i6 = n; i6 <= i3; i6++)
                            {
                                float f17 = (i6 + 0.5F - f10) / f12;
                                f17 *= f17;
                                if (f17 + f16 < 1.0F) {
                                    bool |= WorldGenMinableClusterOverride.generateBlock(paramWorld, i4, i5, i6, this.genBlock, this.cluster);
                                }
                            }
                        }
                    }
                }
            }
        }
        return bool;
    }
}

