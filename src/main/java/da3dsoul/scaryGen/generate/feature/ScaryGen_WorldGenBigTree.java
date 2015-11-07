// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   WorldGenBigTree.java

package da3dsoul.scaryGen.generate.feature;

import java.awt.*;
import java.util.Random;

import abo.ABO;
import da3dsoul.ShapeGen.ShapeGen;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

// Referenced classes of package net.minecraft.src:
//            WorldGenerator, MathHelper, World, ShapeGen

public class ScaryGen_WorldGenBigTree extends WorldGenerator
{
    public ScaryGen_WorldGenBigTree(boolean par1)
    {
        super(par1);
        basePos = new int[3];
        forced = false;
        useShapeGen = false;
        rand = new Random();
        heightLimit = 0;
        heightAttenuation = 0.75799999999999999D;
        branchDensity = 1.0D;
        branchSlope = 0.38100000000000001D;
        scaleWidth = 1.0D;
        leafDensity = 1.0D;
        trunkSize = 1;
        heightLimitLimit = 12;
        leafDistanceLimit = 4;
        meta = 0;
    }

    void generateLeafNodeList()
    {
        height = (int)((double)heightLimit * heightAttenuation);

        if (height >= heightLimit)
        {
            height = heightLimit - 1;
        }

        int i = (int)(1.3819999999999999D + Math.pow((leafDensity * (double)heightLimit) / 13D, 2D));

        if (i < 1)
        {
            i = 1;
        }

        int ai[][] = new int[i * heightLimit][4];
        int j = (basePos[1] + heightLimit) - leafDistanceLimit;
        int k = 1;
        int l = basePos[1] + height;
        int i1 = j - basePos[1];
        ai[0][0] = basePos[0];
        ai[0][1] = j;
        ai[0][2] = basePos[2];
        ai[0][3] = l;
        j--;

        while (i1 >= 0)
        {
            int j1 = 0;
            float f = layerSize(i1);

            if (f < 0.0F)
            {
                j--;
                i1--;
            }
            else
            {
                double d = 0.5D;

                for (; j1 < i; j1++)
                {
                    double d1 = scaleWidth * ((double)f * ((double)rand.nextFloat() + 0.32800000000000001D));
                    double d2 = (double)rand.nextFloat() * 2D * Math.PI;
                    int k1 = MathHelper.floor_double(d1 * Math.sin(d2) + (double)basePos[0] + d);
                    int l1 = MathHelper.floor_double(d1 * Math.cos(d2) + (double)basePos[2] + d);
                    int ai1[] =
                    {
                        k1, j, l1
                    };
                    int ai2[] =
                    {
                        k1, j + leafDistanceLimit, l1
                    };

                    if (checkBlockLine(ai1, ai2) == -1)
                    {
                        int ai3[] =
                        {
                            basePos[0], basePos[1], basePos[2]
                        };
                        double d3 = Math.sqrt(Math.pow(Math.abs(basePos[0] - ai1[0]), 2D) + Math.pow(Math.abs(basePos[2] - ai1[2]), 2D));
                        double d4 = d3 * branchSlope;

                        if ((double)ai1[1] - d4 > (double)l)
                        {
                            ai3[1] = l;
                        }
                        else
                        {
                            ai3[1] = (int)((double)ai1[1] - d4);
                        }

                        if (checkBlockLine(ai3, ai1) == -1)
                        {
                            ai[k][0] = k1;
                            ai[k][1] = j;
                            ai[k][2] = l1;
                            ai[k][3] = ai3[1];
                            k++;
                        }
                    }
                }

                j--;
                i1--;
            }
        }

        leafNodes = new int[k][4];
        System.arraycopy(ai, 0, leafNodes, 0, k);
    }

    void genTreeLayer(int par1, int par2, int par3, float par4, byte par5, Block par6)
    {
        int i = (int)((double)par4 + 0.61799999999999999D);
        byte byte0 = otherCoordPairs[par5];
        byte byte1 = otherCoordPairs[par5 + 3];
        int ai[] =
        {
            par1, par2, par3
        };
        int ai1[] = new int[3];
        int j = -i;
        int k = -i;
        ai1[par5] = ai[par5];

        for (; j <= i; j++)
        {
            ai1[byte0] = ai[byte0] + j;

            for (int l = -i; l <= i;)
            {
                double d = Math.sqrt(Math.pow((double)Math.abs(j) + 0.5D, 2D) + Math.pow((double)Math.abs(l) + 0.5D, 2D));

                if (d > (double)par4)
                {
                    l++;
                }
                else
                {
                    ai1[byte1] = ai[byte1] + l;
                    Block i1 = worldObj.getBlock(ai1[0], ai1[1], ai1[2]);

                    if (i1 != Blocks.air && i1 != Blocks.leaves)
                    {
                        l++;
                    }
                    else
                    {
                        setBlockAndMetadata(worldObj, ai1[0], ai1[1], ai1[2], par6, meta);
                        l++;
                    }
                }
            }
        }
    }

    float layerSize(int par1)
    {
        if ((double)par1 < (double)(float)heightLimit * 0.39999999999999999D)
        {
            return -1.618F;
        }

        float f = (float)heightLimit / 2.0F;
        float f1 = (float)heightLimit / 2.0F - (float)par1;
        float f2;

        if (f1 == 0.0F)
        {
            f2 = f;
        }
        else if (Math.abs(f1) >= f)
        {
            f2 = 0.0F;
        }
        else
        {
            f2 = (float)Math.sqrt(Math.pow(Math.abs(f), 2D) - Math.pow(Math.abs(f1), 2D));
        }

        f2 *= 0.5F;
        return f2;
    }

    float leafSize(int par1)
    {
        if (par1 < 0 || par1 >= leafDistanceLimit)
        {
            return -1F;
        }
        else
        {
            return par1 == 0 || par1 == leafDistanceLimit - 1 ? 2.0F : 3F;
        }
    }

    void generateLeafNode(int par1, int par2, int par3)
    {
        int i = par2;

        for (int j = par2 + leafDistanceLimit; i < j; i++)
        {
            float f = leafSize(i - par2);
            genTreeLayer(par1, i, par3, f, (byte)1, Blocks.leaves);
        }
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

    void generateLeaves()
    {
        int i = 0;

        for (int j = leafNodes.length; i < j; i++)
        {
            int k = leafNodes[i][0];
            int l = leafNodes[i][1];
            int i1 = leafNodes[i][2];
            generateLeafNode(k, l, i1);
        }
    }

    boolean leafNodeNeedsBase(int par1)
    {
        return (double)par1 >= (double)heightLimit * 0.20000000000000001D;
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
            ai[0]++;
            ai1[0]++;
            placeBlockLine(ai, ai1, Blocks.log, meta);
            ai[2]++;
            ai1[2]++;
            placeBlockLine(ai, ai1, Blocks.log, meta);
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
                                    i + i1, k, l + k1
                                });
                        placeBlockLine(ai, ai1, Blocks.log, meta);
                    }
            }
        }
    }

    void generateLeafNodeBases()
    {
        int i = 0;
        int j = leafNodes.length;
        int ai[] =
        {
            basePos[0], basePos[1], basePos[2]
        };

        for (; i < j; i++)
        {
            int ai1[] = leafNodes[i];
            int ai2[] =
            {
                ai1[0], ai1[1], ai1[2]
            };
            ai[1] = ai1[3];
            int k = ai[1] - basePos[1];

            if (leafNodeNeedsBase(k))
            {
                placeBlockLine(ai, ai2, Blocks.log, meta);
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
        if (par1 > 0.5D)
        {
            leafDistanceLimit = par7;
        }

        scaleWidth = par3;
        leafDensity = par5;
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
            generateLeafNodeList();
            generateLeaves();
            generateTrunk();
            generateLeafNodeBases();
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
    private double branchDensity;
    private double branchSlope;
    private double scaleWidth;
    private double leafDensity;
    public int trunkSize;
    public int heightLimitLimit;
    public int leafDistanceLimit;
    private int leafNodes[][];
    public boolean forced;
    public boolean useShapeGen;
    public int meta;
    public int minHeight = 8;
}
