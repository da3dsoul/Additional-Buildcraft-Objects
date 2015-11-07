// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   WorldGenBigTree.java

package da3dsoul.scaryGen.generate.feature;

import java.util.Random;

import da3dsoul.ShapeGen.ShapeGen;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

// Referenced classes of package net.minecraft.src:
//            WorldGenerator, MathHelper, World, ShapeGen

public class ScaryGen_WorldGenBrokenTrunk extends WorldGenerator
{
    public ScaryGen_WorldGenBrokenTrunk(boolean par1, boolean par2)
    {
        super(par1);
        basePos = new int[3];
        forced = false;
        useShapeGen = false;
        rand = new Random();
        heightLimit = 0;
        heightAttenuation = 0.75799999999999999D;
        trunkSize = 1;
        heightLimitLimit = 12;
        meta = 0;
    }

    void placeBlockLine(int[] par1ArrayOfInteger, int[] par2ArrayOfInteger, Block par3, int meta2)
    {
        int[] var4 = new int[] {0, 0, 0};
        byte var5 = 0;
        byte var6;

        for (var6 = 0; var5 < 3; ++var5)
        {
            var4[var5] = par2ArrayOfInteger[var5] - par1ArrayOfInteger[var5];

            if (Math.abs(var4[var5]) > Math.abs(var4[var6]))
            {
                var6 = var5;
            }
        }
// x -147 z 324 y 77
        if (var4[var6] != 0)
        {
            byte var7 = otherCoordPairs[var6];
            byte var8 = otherCoordPairs[var6 + 3];
            byte var9;

            if (var4[var6] > 0)
            {
                var9 = 1;
            }
            else
            {
                var9 = -1;
            }

            double var10 = (double)var4[var7] / (double)var4[var6];
            double var12 = (double)var4[var8] / (double)var4[var6];
            int[] var14 = new int[] {0, 0, 0};
            int var15 = 0;

            for (int var16 = var4[var6] + var9; var15 != var16; var15 += var9)
            {
                var14[var6] = MathHelper.floor_double((double)(par1ArrayOfInteger[var6] + var15) + 0.5D);
                var14[var7] = MathHelper.floor_double((double)par1ArrayOfInteger[var7] + (double)var15 * var10 + 0.5D);
                var14[var8] = MathHelper.floor_double((double)par1ArrayOfInteger[var8] + (double)var15 * var12 + 0.5D);
                byte var17 = (byte) meta2;
                int var18 = Math.abs(var14[0] - par1ArrayOfInteger[0]);
                int var19 = Math.abs(var14[2] - par1ArrayOfInteger[2]);
                int var20 = Math.max(var18, var19);

                if (var20 > 0)
                {
                    if (var18 == var20)
                    {
                        var17 = (byte)(4 + meta2);
                    }
                    else if (var19 == var20)
                    {
                        var17 = (byte)(8 + meta2);
                    }
                }

                this.setBlockAndMetadata(this.worldObj, var14[0], var14[1], var14[2], par3, var17);
            }
        }
    }

    void generateTrunk()
    {
        int i = basePos[0];
        int j = basePos[1];
        int k = basePos[1] + height;
        int l = basePos[2];
        int ai[] =
        {
            i, j, l
        };
        int ai1[] =
        {
            i, k, l
        };
        placeBlockLine(ai, ai1, Blocks.log, meta);

        if (trunkSize == 2)
        {
        	ai1[1] += rand.nextInt(3);
            ai[0]++;
            ai1[0]++;
            placeBlockLine(ai, ai1, Blocks.log, meta);
            ai1[1] += rand.nextInt(3);
            ai1[1] -= rand.nextInt(3);
            ai[2]++;
            ai1[2]++;
            placeBlockLine(ai, ai1, Blocks.log, meta);
            ai1[1] += rand.nextInt(3);
            ai1[1] -= rand.nextInt(3);
            ai[0]--;
            ai1[0]--;
            placeBlockLine(ai, ai1, Blocks.log, meta);
        }

        if (trunkSize == 3)
        {
            for (int i1 = 0; i1 <= 2; i1++)
            {
                for (int k1 = 0; k1 <= 2; k1++)
                    if (k1 != 0 || i1 != 0)
                    {
                        ai = (new int[]
                                {
                                    i + i1, j, l + k1
                                });
                        ai1 = (new int[]
                                {
                                    i + i1, k + rand.nextInt(3), l + k1
                                });
                        placeBlockLine(ai, ai1, Blocks.log, meta);
                    }
            }
        }
    }

    int checkBlockLine(int par1ArrayOfInteger[], int par2ArrayOfInteger[])
    {
        int ai[] = new int[3];
        byte byte0 = 0;
        int i = 0;

        for (; byte0 < 3; byte0++)
        {
            ai[byte0] = par2ArrayOfInteger[byte0] - par1ArrayOfInteger[byte0];

            if (Math.abs(ai[byte0]) > Math.abs(ai[i]))
            {
                i = byte0;
            }
        }

        if (ai[i] == 0)
        {
            return -1;
        }

        byte byte1 = otherCoordPairs[i];
        byte byte2 = otherCoordPairs[i + 3];
        byte byte3;

        if (ai[i] > 0)
        {
            byte3 = 1;
        }
        else
        {
            byte3 = -1;
        }

        double d = (double)ai[byte1] / (double)ai[i];
        double d1 = (double)ai[byte2] / (double)ai[i];
        int ai1[] = new int[3];
        int j = 0;
        int k = ai[i] + byte3;

        do
        {
            if (j == k)
            {
                break;
            }

            ai1[i] = par1ArrayOfInteger[i] + j;
            ai1[byte1] = MathHelper.floor_double((double)par1ArrayOfInteger[byte1] + (double)j * d);
            ai1[byte2] = MathHelper.floor_double((double)par1ArrayOfInteger[byte2] + (double)j * d1);
            Block l = worldObj.getBlock(ai1[0], ai1[1], ai1[2]);

            if (l != Blocks.air && l != Blocks.leaves)
            {
                break;
            }

            j += byte3;
        }
        while (true);

        if (j == k)
        {
            return -1;
        }
        else
        {
            return Math.abs(j);
        }
    }

    boolean validTreeLocation()
    {
        int ai[] =
        {
            basePos[0], basePos[1], basePos[2]
        };
        int ai1[] =
        {
            basePos[0], (basePos[1] + heightLimit) - 1, basePos[2]
        };
        Block i = worldObj.getBlock(basePos[0], basePos[1] - 1, basePos[2]);

        if (i != Blocks.grass && i != Blocks.dirt)
        {
            return false;
        }

        int j = checkBlockLine(ai, ai1);

        if (j == -1)
        {
            return true;
        }

        if (j < 6)
        {
            return false;
        }
        else
        {
            heightLimit = j;
            return true;
        }
    }

    public void setScale(double par1, double par3, double par5, int par7)
    {
        heightLimitLimit = (int)(par1 * 6D);
        minHeight *= par1;
    }

    public boolean generate(World world, Random par2Random, int par3, int par4, int par5)
    {
        worldObj = world;
        long l = par2Random.nextLong();
        rand.setSeed(l);
        basePos[0] = par3;
        basePos[1] = par4;
        basePos[2] = par5;

        if (heightLimit == 0)
        {
            heightLimit = minHeight + rand.nextInt(heightLimitLimit);
        }

        if (!validTreeLocation() && !forced)
        {
            return false;
        }
        else
        {
            generateTrunk();
            return true;
        }
    }

    protected void setBlockAndMetadata(World world, int par2, int par3, int par4, Block par5, int par6)
    {
        if (useShapeGen)
        {
            ShapeGen.getShapeGen(world).addBlock(par2, par3, par4, par5, par6);
        }
        else
        {
            world.setBlock(par2, par3, par4, par5, par6, 3);
        }
    }

    static final byte otherCoordPairs[] =
    {
        2, 0, 0, 1, 2, 1
    };

    private Random rand;
    private World worldObj;
    private int basePos[];
    public int heightLimit;
    public int height;
    private double heightAttenuation;
    public int trunkSize;
    public int heightLimitLimit;
    public boolean forced;
    public boolean useShapeGen;
    public int meta;
    public int minHeight = 8;
}
