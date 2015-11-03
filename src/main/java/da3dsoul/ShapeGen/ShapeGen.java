// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ShapeGen.java

package da3dsoul.ShapeGen;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.swing.Timer;

import abo.ABO;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class ShapeGen
{
	
	public static MinecraftServer getServerFromShapeGen()
    {
        return server;
    }

    public static ShapeGen getShapeGen(int i)
    {
        return ABO.shapeGens.get(i);
    }
	
	public static World getWorldFromShapeGen(int i)
    {
        return server.worldServerForDimension(i);
    }
	
	public World getWorldFromShapeGen()
    {
        return world;
    }
	
	public ShapeGen(World world)
    {
        server = FMLCommonHandler.instance().getMinecraftServerInstance();
        blocks = Collections.synchronizedMap(new LinkedHashMap());
        blocksToAdd = new LinkedHashMap();
        this.world = world;
        shapeGenID = world.provider.dimensionId;
        alive = true;
        readFromNBT();
    }
	
	public int getLength()
    {
        if (blocks == null || blocks.isEmpty())
        {
            return 0;
        }
        else
        {
            return blocks.size();
        }
    }    

    public void tick()
    {
        if (server == null)
        {
            return;
        }

        if (world == null)
        {
            return;
        }

        boolean flag = true;

        if (world != null && !world.playerEntities.isEmpty())
        {
            flag = false;
        }

        if (flag)
        {
            if (!Instant)
            {
                Instant = true;
            }
        }
        else if (Instant)
        {
            Instant = false;
        }

        blocks.putAll(blocksToAdd);
        blocksToAdd.clear();

        update();
        cleanUpList();
    }

    public synchronized void addBlock(int i, int j, int k, Block id)
    {
        addBlock(i, j, k, id, 0);
    }

    public synchronized void addBlock(int i, int j, int k, Block id, int l)
    {
        if (!alive) return;

        if (adding)
        {
            blocksToAdd.put(toString(i, j, k),Block.blockRegistry.getNameForObject(id) + "|" + l);
            return;
        }

        synchronized (blocks)
        {
            blocks.put(toString(i, j, k),Block.blockRegistry.getNameForObject(id) + "|" + l);
        }
    }

    public synchronized void addBlockAtStart(int i, int j, int k, Block id)
    {
        addBlockAtStart(i, j, k, id, 0);
    }

    public synchronized void addBlockAtStart(int i, int j, int k, Block id, int l)
    {
        addBlock(i,j,k,id, l);
    }

    public synchronized void addBlocks(Map list)
    {
        if (!alive)
        {
            blocksToAdd.putAll(list);
            return;
        }

        synchronized (blocks)
        {
            blocks.putAll(list);
        }
    }

    public void addBlocksAtStart(Map list)
    {
        addBlocks(list);
    }

    public void cleanUpList()
    {
    }

    public void clearBlocks()
    {
        synchronized (blocks)
        {
            blocks.clear();
        }
    }

    public ArrayList<String> getBlockLine(int arrayStart[], int arrayEnd[])
    {
        int ai[] = new int[3];
        byte byte0 = 0;
        int i = 0;

        for (; byte0 < 3; byte0++)
        {
            ai[byte0] = arrayEnd[byte0] - arrayStart[byte0];

            if (Math.abs(ai[byte0]) > Math.abs(ai[i]))
            {
                i = byte0;
            }
        }

        if (ai[i] == 0)
        {
            return null;
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
        ArrayList returnList = new ArrayList();

        for (int k = ai[i] + byte3; j != k; j += byte3)
        {
            ai1[i] = MathHelper.floor_double((double) (arrayStart[i] + j) + 0.5D);
            ai1[byte1] = MathHelper.floor_double((double)arrayStart[byte1] + (double)j * d + 0.5D);
            ai1[byte2] = MathHelper.floor_double((double) arrayStart[byte2] + (double) j * d1 + 0.5D);
            Block id = world.getBlock(ai1[0], ai1[1], ai1[2]);
            int meta = world.getBlockMetadata(ai1[0], ai1[1], ai1[2]);
            String returnVal = ai1[0]+ "|" + ai1[1] + "|" + ai1[2] + "|" + Block.blockRegistry.getNameForObject(id) + "|" + meta;
            returnList.add(returnVal);
        }

        return returnList;
    }
	
	public static ArrayList<int[]> getCylinder(int X, int Y, int Z, int radius, int height)
    {
        LinkedHashSet list = new LinkedHashSet();

        for (int j = 0; j < height; j++)
        {
            for (int i = -radius; i <= radius; i++)
            {
                for (int k = -radius; k <= radius; k++)
                {
                    double distance = Math.sqrt(i * i + k * k);

                    if (distance <= (double)radius)
                        list.add(new int[]
                                {
                                    X + i, Y + j, Z + k
                                });
                }
            }
        }

        ArrayList list1 = new ArrayList();
        list1.addAll(list);
        return list1;
    }

    public static ArrayList<int[]> getCylinderHollow(int X, int Y, int Z, int radius, int height, int thickness)
    {
        LinkedHashSet list = new LinkedHashSet();

        for (int j = 0; j < height; j++)
        {
            for (int i = -radius; i <= radius; i++)
            {
                for (int k = -radius; k <= radius; k++)
                {
                    double distance = Math.sqrt(i * i + k * k);

                    if (distance <= (double)radius && distance > (double)(radius - thickness))
                        list.add(new int[]
                                {
                                    X + i, Y + j, Z + k
                                });
                }
            }
        }

        ArrayList list1 = new ArrayList();
        list1.addAll(list);
        return list1;
    }

    public ArrayList<int[]> getRadialPointsDome(int X, int Y, int Z, int radius, int steps)
    {
        ArrayList list = new ArrayList();
        double d3 = radius;
        double d = X;
        double d1 = Y;
        double d2 = Z;
        Vec3 vec3d = Vec3.createVectorHelper(d, d1, d2);

        for (float f = -90F; f <= 0.0F; f += 360 / steps)
        {
            for (float f1 = 0.0F; f1 < 360F; f1 += 360 / steps)
            {
                float f2 = MathHelper.cos(-f1 * 0.01745329F - (float)Math.PI);
                float f3 = MathHelper.sin(-f1 * 0.01745329F - (float)Math.PI);
                float f4 = -MathHelper.cos(-f * 0.01745329F);
                float f5 = MathHelper.sin(-f * 0.01745329F);
                float f6 = f3 * f4;
                float f7 = f2 * f4;
                Vec3 vec3d1 = vec3d.addVector((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
                int a[] =
                {
                    (int)vec3d1.xCoord, (int)vec3d1.yCoord, (int)vec3d1.zCoord
                };

                if (!list.contains(a))
                {
                    list.add(a);
                }
            }
        }

        return list;
    }

    public ArrayList<int[]> getRadialPointsDomeInverted(int X, int Y, int Z, int radius, int steps)
    {
        ArrayList list = new ArrayList();
        double d3 = radius;
        double d = X;
        double d1 = Y;
        double d2 = Z;
        Vec3 vec3d = Vec3.createVectorHelper(d, d1, d2);

        for (float f = 90F; f >= 0.0F; f -= 360 / steps)
        {
            for (float f1 = 0.0F; f1 < 360F; f1 += 360 / steps)
            {
                float f2 = MathHelper.cos(-f1 * 0.01745329F - (float)Math.PI);
                float f3 = MathHelper.sin(-f1 * 0.01745329F - (float)Math.PI);
                float f4 = -MathHelper.cos(-f * 0.01745329F);
                float f5 = MathHelper.sin(-f * 0.01745329F);
                float f6 = f3 * f4;
                float f7 = f2 * f4;
                Vec3 vec3d1 = vec3d.addVector((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
                int a[] =
                {
                    (int)vec3d1.xCoord, (int)vec3d1.yCoord, (int)vec3d1.zCoord
                };

                if (!list.contains(a))
                {
                    list.add(a);
                }
            }
        }

        return list;
    }

    public ArrayList getRadialPointsSphere(int X, int Y, int Z, int radius, int steps)
    {
        ArrayList list = new ArrayList();
        double d3 = radius;
        double d = X;
        double d1 = Y;
        double d2 = Z;
        Vec3 vec3d = Vec3.createVectorHelper(d, d1, d2);

        for (float f = -90F; f <= 90F; f += (180 / steps) * 2)
        {
            for (float f1 = 0.0F; f1 < 360F; f1 += 360 / steps)
            {
                float f2 = MathHelper.cos(-f1 * 0.01745329F - (float)Math.PI);
                float f3 = MathHelper.sin(-f1 * 0.01745329F - (float)Math.PI);
                float f4 = -MathHelper.cos(-f * 0.01745329F);
                float f5 = MathHelper.sin(-f * 0.01745329F);
                float f6 = f3 * f4;
                float f7 = f2 * f4;
                Vec3 vec3d1 = vec3d.addVector((double)f6 * d3, (double)f5 * d3, (double)f7 * d3);
                int a[] =
                {
                    (int)vec3d1.xCoord, (int)vec3d1.yCoord, (int)vec3d1.zCoord
                };

                if (!list.contains(a))
                {
                    list.add(a);
                }
            }
        }

        return list;
    }

    public ArrayList<int[]> getRegularPolyVertices(int i, int j, int k, int radius, int n)
    {
        ArrayList list = new ArrayList();

        for (int i2 = 0; i2 < n; i2++)
        {
            double n1 = Math.toRadians(i2 * (360 / n));
            int i1 = (int)((double)i + (double)radius * Math.cos(n1));
            int k1 = (int)((double)k + (double)radius * Math.sin(n1));
            int a[] =
            {
                i1, j, k1
            };
            list.add(a);
        }

        return list;
    }

    public void Level(World w, int X, int Y, int Z, float radius, Block interID, Block topsoilID)
    {
        Level(w, X, Y, Z, radius, interID, topsoilID, null);
    }

    public void Level(World w, int X, int Y, int Z, float radius, Block interID, Block topsoilID,
            EntityPlayer player)
    {
        for (int y = world.getHeight() - Y; y > 0; y--)
        {
            for (int z = 0; (double)z < Math.ceil(radius); z++)
            {
                for (int x = 0; (double)x < Math.ceil(radius); x++)
                {
                    float distance = MathHelper.sqrt_double(x * x + z * z);

                    if (distance <= radius)
                    {
                        if (world.canMineBlock(player, X + x, Y + y, Z + z))
                        {
                            addBlockAtStart(X + x, Y + y, Z + z, Blocks.air);
                        }

                        if (world.canMineBlock(player, X + x, Y + y, Z - z))
                        {
                            addBlockAtStart(X + x, Y + y, Z - z, Blocks.air);
                        }

                        if (world.canMineBlock(player, X - x, Y + y, Z - z))
                        {
                            addBlockAtStart(X - x, Y + y, Z - z, Blocks.air);
                        }

                        if (world.canMineBlock(player, X - x, Y + y, Z + z))
                        {
                            addBlockAtStart(X - x, Y + y, Z + z, Blocks.air);
                        }
                    }
                }
            }
        }

        for (int y = 0; y > -4; y--)
        {
            for (int z = 0; (float)z <= radius; z++)
            {
                for (int x = 0; (float)x <= radius; x++)
                {
                    float distance = MathHelper.sqrt_double(x * x + z * z);

                    if (distance <= radius)
                    {
                        Block id = Blocks.air;

                        if (y == 0)
                        {
                            id = topsoilID;
                        }
                        else
                        {
                            id = interID;
                        }

                        if (world.canMineBlock(player, X + x, Y + y, Z + z))
                        {
                            addBlockAtStart(X + x, Y + y, Z + z, id);
                        }

                        if (world.canMineBlock(player, X + x, Y + y, Z - z))
                        {
                            addBlockAtStart(X + x, Y + y, Z - z, id);
                        }

                        if (world.canMineBlock(player, X - x, Y + y, Z - z))
                        {
                            addBlockAtStart(X - x, Y + y, Z - z, id);
                        }

                        if (world.canMineBlock(player, X - x, Y + y, Z + z))
                        {
                            addBlockAtStart(X - x, Y + y, Z + z, id);
                        }
                    }
                }
            }
        }
    }

    /*    public void makeLaputa(int X, int Y, int Z)
        {
            if(!alive)
                return;
            adding = true;
            placeCylinder(X, Y, Z, 24, 3, 3, 0);
            placeCylinderHollow(X, Y, Z, 25, 9, 1, false, false, 24, 2, false);
            placeDomeInverted(X, Y - 1, Z, 25F, 24, false, 0, false, 2);
            ArrayList conepoints1 = getRadialPointsDomeInverted(X, Y, Z, 21, 36);
            for(int i = 0; i < conepoints1.size(); i++)
            {
                int pos[] = (int[])conepoints1.get(i);
                placeSpireInverted(pos[0], pos[1], pos[2], 4, 10, 2, 98, 0);
            }

            placeCylinderHollow(X, Y - 2, Z, 50, 6, 3, false, false, 98, 0, false);
            placeCylinderHollow(X, Y - 2, Z, 80, 6, 10, false, false, 24, 2, false);
            placeCylinderHollow(X, Y - 2, Z, 80, 12, 1, false, false, 24, 2, false);
            placeCylinderHollow(X, Y - 2, Z, 71, 12, 1, false, false, 24, 2, false);
            conepoints1 = getRegularPolyVertices(X, Y + 3, Z, 75, 15);
            for(int i = 0; i < conepoints1.size(); i++)
            {
                int pos[] = (int[])conepoints1.get(i);
                for(int x1 = -1; x1 <= 1; x1++)
                {
                    for(int z1 = -1; z1 <= 1; z1++)
                        placeBlockLine(new int[] {
                            X + x1, Y + 3, Z + z1
                        }, new int[] {
                            pos[0] + x1, Y + 3, pos[2] + z1
                        }, 24, 2, false, null, null);
                }

                EntityLaputaSpot spot = new EntityLaputaSpot(world, pos[0], Y + 4, pos[2]);
                spot.healthRadius = 9;
                world.spawnEntityInWorld(spot);
                placeSpireInverted(pos[0], pos[1] + 1, pos[2], 10, 14, 4, 24, 2);
                placeSpireHollow(pos[0], Y + 4, pos[2], 10, 19, 6, 1, 24, 2, false, false);
                placeCylinder(pos[0], Y + 4, pos[2], 9, 6, 0, 0);
                placeCylinder(pos[0], Y + 3, pos[2], 9, 1, 2, 0);
                generateGrass(pos[0], Y + 4, pos[2], Block.tallGrass.blockID, 1);
                generateFlowers(pos[0], Y + 4, pos[2], Block.plantRed.blockID);
                generateFlowers(pos[0], Y + 4, pos[2], Block.plantYellow.blockID);
            }

            conepoints1 = getRegularPolyVertices(X, Y + 3, Z, 50, 5);
            for(int i = 0; i < conepoints1.size(); i++)
            {
                int pos[] = (int[])conepoints1.get(i);
                placeSpireInverted(pos[0], pos[1] + 1, pos[2], 15, 15, 5, 24, 2);
                placeSpireHollow(pos[0], Y + 4, pos[2], 15, 24, 5, 1, 24, 2, false, true);
                placeCylinder(pos[0], Y + 3, pos[2], 14, 1, 2, 0);
                ArrayList points = getRegularPolyVertices(pos[0], Y + 3, pos[2], 7, 3);
                for(int i1 = 0; i1 < points.size(); i1++)
                {
                    int pos1[] = (int[])points.get(i1);
                    makeLamp(pos1[0], pos1[1], pos1[2], Block.blockGold.blockID, 0);
                }

                makeLamp(pos[0], Y + 3, pos[2], Block.blockGold.blockID, 0);
                EntityLaputaSpot spot = new EntityLaputaSpot(world, pos[0], Y + 4, pos[2]);
                spot.healthRadius = 14;
                world.spawnEntityInWorld(spot);
                generateGrass(pos[0], Y + 4, pos[2], Block.tallGrass.blockID, 1);
                generateFlowers(pos[0], Y + 4, pos[2], Block.plantRed.blockID);
                generateFlowers(pos[0], Y + 4, pos[2], Block.plantYellow.blockID);
            }

            placeCylinderHollow(X, Y + 4, Z, 79, 5, 8, false, false, 0, 0, false);
            conepoints1 = getRegularPolyVertices(X, Y + 3, Z, 25, 5);
            for(int i = 0; i < conepoints1.size(); i++)
            {
                int pos[] = (int[])conepoints1.get(i);
                for(int x1 = -1; x1 <= 1; x1++)
                {
                    for(int z1 = -1; z1 <= 1; z1++)
                    {
                        for(int y1 = 1; y1 <= 3; y1++)
                            addBlock(pos[0] + x1, pos[1] + y1, pos[2] + z1, 0);
                    }
                }
            }

            conepoints1 = getRegularPolyVertices(X, Y + 3, Z, 35, 5);
            for(int i = 0; i < conepoints1.size(); i++)
            {
                int pos[] = (int[])conepoints1.get(i);
                for(int x1 = -1; x1 <= 1; x1++)
                {
                    for(int z1 = -1; z1 <= 1; z1++)
                    {
                        for(int y1 = 1; y1 <= 3; y1++)
                            addBlock(pos[0] + x1, pos[1] + y1, pos[2] + z1, 0);
                    }
                }
            }

            conepoints1 = getRegularPolyVertices(X, Y + 3, Z, 65, 5);
            for(int i = 0; i < conepoints1.size(); i++)
            {
                int pos[] = (int[])conepoints1.get(i);
                for(int x1 = -1; x1 <= 1; x1++)
                {
                    for(int z1 = -1; z1 <= 1; z1++)
                    {
                        for(int y1 = 1; y1 <= 3; y1++)
                            addBlock(pos[0] + x1, pos[1] + y1, pos[2] + z1, 0);
                    }
                }
            }

            conepoints1 = getRegularPolyVertices(X, Y + 7, Z, 21, 15);
            for(int i = 0; i < conepoints1.size(); i++)
            {
                int pos[] = (int[])conepoints1.get(i);
                makeLamp(pos[0], pos[1], pos[2], Block.blockGold.blockID, 0);
            }

            conepoints1 = getRegularPolyVertices(X, Y + 7, Z, 8, 5);
            for(int i = 0; i < conepoints1.size(); i++)
            {
                int pos[] = (int[])conepoints1.get(i);
                makeLamp(pos[0], pos[1], pos[2], Block.blockGold.blockID, 0);
                EntityLaputaSpot spot = new EntityLaputaSpot(world, pos[0], Y + 4, pos[2]);
                spot.healthRadius = 6;
                world.spawnEntityInWorld(spot);
            }

            placeSphere(X, Y - 10, Z, 8F, 0, false, 0, false, false, 0);
            placeCylinder(X, Y + 3, Z, 24, 1, 2, 0);
            conepoints1 = getRegularPolyVertices(X, Y + 3, Z, 16, 5);
            ArrayList conepoints2 = getRegularPolyVertices(X, Y + 3, Z, 8, 5);
            for(int i = 0; i < conepoints1.size(); i++)
            {
                int pos[] = (int[])conepoints1.get(i);
                int pos2[] = (int[])conepoints2.get(i);
                for(int x1 = -1; x1 <= 1; x1++)
                {
                    for(int z1 = -1; z1 <= 1; z1++)
                        placeBlockLine(new int[] {
                            pos2[0] + x1, Y + 3, pos2[2] + z1
                        }, new int[] {
                            pos[0] + x1, Y + 3, pos[2] + z1
                        }, 9, 0, false, null, null);
                }
            }

            WorldGenBigTree tree1 = new WorldGenBigTree(false);
            tree1.forced = true;
            tree1.useShapeGen = true;
            tree1.heightLimit = 70;
            tree1.heightLimitLimit = 70;
            tree1.trunkSize = 3;
            tree1.leafDistanceLimit = 8;
            tree1.generate(world, world.rand, X, Y + 4, Z);
            for(int i = 0; i < 6; i++)
            {
                int x2 = world.rand.nextInt(20);
                x2 -= world.rand.nextInt(20);
                x2 += X;
                int z2 = world.rand.nextInt(20);
                z2 -= world.rand.nextInt(20);
                z2 += Z;
                generateGrass(x2, Y + 4, z2, Block.tallGrass.blockID, 1);
                generateFlowers(x2, Y + 4, z2, Block.plantRed.blockID);
                generateFlowers(x2, Y + 4, z2, Block.plantYellow.blockID);
            }

            conepoints1 = getRegularPolyVertices(X, Y + 3, Z, 75, 15);
            for(int i = 0; i < conepoints1.size(); i++)
            {
                int pos[] = (int[])conepoints1.get(i);
                ArrayList points = getRegularPolyVertices(pos[0], Y + 3, pos[2], 5, 3);
                for(int i1 = 0; i1 < points.size(); i1++)
                {
                    int pos1[] = (int[])points.get(i1);
                    makeLamp(pos1[0], pos1[1], pos1[2], Block.blockGold.blockID, 0);
                }

                makeLamp(pos[0], pos[1], pos[2], Block.blockGold.blockID, 0);
            }

            adding = false;
        }*/

    public void placeBlockLine(int par1ArrayOfInteger[], int par2ArrayOfInteger[], Block blockID, boolean drop, Block idmask[], Material mask[])
    {
        placeBlockLine(par1ArrayOfInteger, par2ArrayOfInteger, blockID, 0, drop, idmask, mask, null);
    }

    public void placeBlockLine(int par1ArrayOfInteger[], int par2ArrayOfInteger[], Block blockID, int meta, boolean drop, Block idmask[], Material mask[])
    {
        placeBlockLine(par1ArrayOfInteger, par2ArrayOfInteger, blockID, meta, drop, idmask, mask, null);
    }

    public void placeBlockLine(int par1ArrayOfInteger[], int par2ArrayOfInteger[], Block blockID, int meta, boolean drop, Block idmask[], Material mask[],
            EntityPlayer player)
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
            return;
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

        for (int k = ai[i] + byte3; j != k; j += byte3)
        {
            ai1[i] = MathHelper.floor_double((double)(par1ArrayOfInteger[i] + j) + 0.5D);
            ai1[byte1] = MathHelper.floor_double((double)par1ArrayOfInteger[byte1] + (double)j * d + 0.5D);
            ai1[byte2] = MathHelper.floor_double((double) par1ArrayOfInteger[byte2] + (double) j * d1 + 0.5D);
            Block id = world.getBlock(ai1[0], ai1[1], ai1[2]);

            if (id != blockID)
            {
                boolean flag = true;

                if (idmask != null)
                {
                    for (int i1 = 0; i1 < idmask.length; i1++)
                        if (idmask[i1] == id)
                        {
                            flag = false;
                        }
                }

                if (mask != null)
                {
                    for (int i1 = 0; i1 < mask.length; i1++)
                        if (world.getBlock(ai1[0], ai1[1], ai1[2]).getMaterial() == mask[i1])
                        {
                            flag = false;
                        }
                }

                if (flag && world.canMineBlock(player, ai1[0], ai1[1], ai1[2]))
                {
                    if (!id.isAir(world, ai1[0], ai1[1], ai1[2]) && drop)
                    {
                        if(!world.isRemote) {
                            for (ItemStack stack : id.getDrops(world, ai1[0], ai1[1], ai1[2], world.getBlockMetadata(ai1[0], ai1[1], ai1[2]), 0)) {
                                world.spawnEntityInWorld(new EntityItem(world, ai1[0], ai1[1], ai1[2], stack));
                            }
                        }
                    }

                    if (!Instant)
                    {
                        addBlock(ai1[0], ai1[1], ai1[2], blockID, meta);
                    }
                    else
                    {
                        world.setBlock(ai1[0], ai1[1], ai1[2], blockID, meta, 3);
                    }
                }
            }
        }
    }

    public void placeCone(int X, int Y, int Z, int radius, int height, Block blockID, int blockMeta)
    {
        if (!alive)
        {
            return;
        }

        int endPoint[] =
        {
            X, Y + height, Z
        };

        for (int i = -radius; i <= radius; i++)
        {
            for (int k = -radius; k <= radius; k++)
            {
                double distance = Math.sqrt(i * i + k * k);

                if (distance <= (double)radius)
                {
                    int start[] =
                    {
                        X + i, Y, Z + k
                    };
                    placeBlockLine(start, endPoint, blockID, blockMeta, false, null, null);
                }
            }
        }
    }

    public void placeConeHollow(int X, int Y, int Z, int radius, int height, Block blockID, int blockMeta,
            int thickness, boolean cap, boolean fillair)
    {
        if (!alive)
        {
            return;
        }

        int endPoint[] =
        {
            X, Y + height, Z
        };

        for (int i = -radius; i <= radius; i++)
        {
            for (int k = -radius; k <= radius; k++)
            {
                double distance = Math.sqrt(i * i + k * k);

                if (cap && distance <= (double)radius)
                    if (!Instant)
                    {
                        addBlock(X + i, Y, Z + k, blockID, blockMeta);
                    }
                    else
                    {
                        world.setBlock(X + i, Y, Z + k, blockID, blockMeta, 3);
                    }

                if (distance <= (double)radius && distance > (double)(radius - thickness))
                {
                    int start[] =
                    {
                        X + i, Y, Z + k
                    };
                    placeBlockLine(start, endPoint, blockID, blockMeta, false, null, null);
                }
            }
        }
    }

    public void placeConeHollowInverted(int X, int Y, int Z, int radius, int height, Block blockID, int blockMeta,
            int thickness, boolean cap, boolean fillair)
    {
        if (!alive)
        {
            return;
        }

        int endPoint[] =
        {
            X, Y - height, Z
        };

        for (int i = -radius; i <= radius; i++)
        {
            for (int k = -radius; k <= radius; k++)
            {
                double distance = Math.sqrt(i * i + k * k);

                if (cap && distance <= (double)radius)
                    if (!Instant)
                    {
                        addBlock(X + i, Y, Z + k, blockID, blockMeta);
                    }
                    else
                    {
                        world.setBlock(X + i, Y, Z + k, blockID, blockMeta, 3);
                    }

                if (fillair && distance <= (double)(radius - thickness))
                    if (!Instant)
                    {
                        addBlock(X + i, Y - 1, Z + k, Blocks.air);
                    }
                    else
                    {
                        world.setBlock(X + i, Y - 1, Z + k, Blocks.air, 0, 3);
                    }

                if (distance <= (double)radius && distance > (double)(radius - thickness))
                {
                    int start[] =
                    {
                        X + i, Y, Z + k
                    };
                    placeBlockLine(start, endPoint, blockID, blockMeta, false, null, null);
                }
            }
        }
    }

    public void placeConeInverted(int X, int Y, int Z, int radius, int height, Block blockID, int blockMeta)
    {
        if (!alive)
        {
            return;
        }

        int endPoint[] =
        {
            X, Y - height, Z
        };

        for (int i = -radius; i <= radius; i++)
        {
            for (int k = -radius; k <= radius; k++)
            {
                double distance = Math.sqrt(i * i + k * k);

                if (distance <= (double)radius)
                {
                    int start[] =
                    {
                        X + i, Y, Z + k
                    };
                    placeBlockLine(start, endPoint, blockID, blockMeta, false, null, null);
                }
            }
        }
    }

    public void placeCube(int X, int Y, int Z, int size, Block BlockID)
    {
        placeRectangularPrism(X, Y, Z, size, size, size, BlockID, false, false, 0, false, 0);
    }

    public void placeCube(int X, int Y, int Z, int size, Block BlockID, boolean inverted, boolean hollow,
            int thickness, boolean fillair, int metadata)
    {
        placeRectangularPrism(X, Y, Z, size, size, size, BlockID, inverted, hollow, thickness, fillair, metadata);
    }

    public void placeCylinder(int X, int Y, int Z, int radius, int height, Block blockID, int blockMeta)
    {
        if (!alive)
        {
            return;
        }

        for (int i = -radius; i <= radius; i++)
        {
            for (int k = -radius; k <= radius; k++)
            {
                for (int j = 0; j < height; j++)
                {
                    double distance = Math.sqrt(i * i + k * k);

                    if (distance <= (double)radius)
                        if (!Instant)
                        {
                            addBlock(X + i, Y + j, Z + k, blockID, blockMeta);
                        }
                        else
                        {
                            world.setBlock(X + i, Y + j, Z + k, blockID, blockMeta, 3);
                        }
                }
            }
        }
    }

    public void placeCylinderHollow(int X, int Y, int Z, int radius, int height, int thickness, boolean cap,
            boolean fillair, Block blockID, int blockMeta, boolean captop)
    {
        if (!alive)
        {
            return;
        }

        for (int i = -radius; i <= radius; i++)
        {
            for (int k = -radius; k <= radius; k++)
            {
                for (int j = 0; j < height; j++)
                {
                    double distance = Math.sqrt(i * i + k * k);

                    if (distance <= (double)radius)
                    {
                        if (distance > (double)(radius - thickness))
                        {
                            addBlock(X + i, Y + j, Z + k, blockID, blockMeta);
                        }

                        if (cap && j == 0)
                        {
                            addBlock(X + i, Y + j, Z + k, blockID, blockMeta);
                        }

                        if (captop && j == height)
                        {
                            addBlock(X + i, Y + j, Z + k, blockID, blockMeta);
                        }

                        if (fillair && distance <= (double)(radius - thickness) && (!cap || j != 0 && j != height))
                        {
                            addBlock(X + i, Y + j, Z + k, Blocks.air);
                        }
                    }
                }
            }
        }
    }

    public void placeDome(int X, int Y, int Z, float radius, Block BlockID, boolean hollow, int thickness,
            boolean fillair, int metadata)
    {
        if (!alive)
        {
            return;
        }

        for (int y = 0; y <= (int)Math.ceil(radius); y++)
        {
            for (int z = 0; z <= (int)Math.ceil(radius); z++)
            {
                for (int x = 0; x <= (int)Math.ceil(radius); x++)
                {
                    float distance = MathHelper.sqrt_double(x * x + z * z + y * y);

                    if (hollow && distance < radius - (float)thickness)
                    {
                        if (fillair)
                        {
                            if (world.getBlock(X + x, Y + y, Z + z) != Blocks.air)
                                if (!Instant)
                                {
                                    addBlock(X + x, Y + y, Z + z, Blocks.air);
                                }
                                else
                                {
                                    world.setBlock(X + x, Y + y, Z + z, Blocks.air, 0, 3);
                                }

                            if (world.getBlock(X + x, Y + y, Z - z) != Blocks.air)
                                if (!Instant)
                                {
                                    addBlock(X + x, Y + y, Z - z, Blocks.air);
                                }
                                else
                                {
                                    world.setBlock(X + x, Y + y, Z - z, Blocks.air, 0, 3);
                                }

                            if (world.getBlock(X - x, Y + y, Z - z) != Blocks.air)
                                if (!Instant)
                                {
                                    addBlock(X - x, Y + y, Z - z, Blocks.air);
                                }
                                else
                                {
                                    world.setBlock(X - x, Y + y, Z - z, Blocks.air, 0, 3);
                                }

                            if (!Instant)
                            {
                                addBlock(X - x, Y + y, Z + z, Blocks.air);
                            }
                            else
                            {
                                world.setBlock(X - x, Y + y, Z + z, Blocks.air, 0, 3);
                            }
                        }
                    }
                    else if (distance <= radius)
                    {
                        if (world.getBlock(X + x, Y + y, Z + z) != BlockID && Y + y != 1)
                            if (!Instant)
                            {
                                addBlock(X + x, Y + y, Z + z, BlockID, metadata);
                            }
                            else
                            {
                                world.setBlock(X + x, Y + y, Z + z, BlockID, metadata, 3);
                            }

                        if (world.getBlock(X + x, Y + y, Z - z) != BlockID && Y + y != 1)
                            if (!Instant)
                            {
                                addBlock(X + x, Y + y, Z - z, BlockID, metadata);
                            }
                            else
                            {
                                world.setBlock(X + x, Y + y, Z - z, BlockID, metadata, 3);
                            }

                        if (world.getBlock(X - x, Y + y, Z - z) != BlockID && Y + y != 1)
                            if (!Instant)
                            {
                                addBlock(X - x, Y + y, Z - z, BlockID, metadata);
                            }
                            else
                            {
                                world.setBlock(X - x, Y + y, Z - z, BlockID, metadata, 3);
                            }

                        if (world.getBlock(X - x, Y + y, Z + z) != BlockID && Y + y != 1)
                            if (!Instant)
                            {
                                addBlock(X - x, Y + y, Z + z, BlockID, metadata);
                            }
                            else
                            {
                                world.setBlock(X - x, Y + y, Z + z, BlockID, metadata, 3);
                            }
                    }
                }
            }
        }
    }

    public void placeDomeInverted(int X, int Y, int Z, float radius, Block BlockID, boolean hollow, int thickness,
            boolean fillair, int metadata)
    {
        if (!alive)
        {
            return;
        }

        for (int y = 0; y <= (int)Math.ceil(radius); y++)
        {
            for (int z = 0; z <= (int)Math.ceil(radius); z++)
            {
                for (int x = 0; x <= (int)Math.ceil(radius); x++)
                {
                    float distance = MathHelper.sqrt_double(x * x + z * z + y * y);

                    if (hollow && distance < radius - (float)thickness)
                    {
                        if (fillair)
                        {
                            if (!Instant)
                            {
                                addBlock(X + x, Y - y, Z + z, Blocks.air);
                            }
                            else
                            {
                                world.setBlock(X + x, Y - y, Z + z, Blocks.air, 0, 3);
                            }

                            if (!Instant)
                            {
                                addBlock(X + x, Y - y, Z - z, Blocks.air);
                            }
                            else
                            {
                                world.setBlock(X + x, Y - y, Z - z, Blocks.air, 0, 3);
                            }

                            if (!Instant)
                            {
                                addBlock(X - x, Y - y, Z - z, Blocks.air);
                            }
                            else
                            {
                                world.setBlock(X - x, Y - y, Z - z, Blocks.air, 0, 3);
                            }

                            if (!Instant)
                            {
                                addBlock(X - x, Y - y, Z + z, Blocks.air);
                            }
                            else
                            {
                                world.setBlock(X - x, Y - y, Z + z, Blocks.air, 0, 3);
                            }
                        }
                    }
                    else if (distance <= radius)
                    {
                        if (!Instant)
                        {
                            addBlock(X + x, Y - y, Z + z, BlockID, metadata);
                        }
                        else
                        {
                            world.setBlock(X + x, Y - y, Z + z, BlockID, metadata, 3);
                        }

                        if (!Instant)
                        {
                            addBlock(X + x, Y - y, Z - z, BlockID, metadata);
                        }
                        else
                        {
                            world.setBlock(X + x, Y - y, Z - z, BlockID, metadata, 3);
                        }

                        if (!Instant)
                        {
                            addBlock(X - x, Y - y, Z - z, BlockID, metadata);
                        }
                        else
                        {
                            world.setBlock(X - x, Y - y, Z - z, BlockID, metadata, 3);
                        }

                        if (!Instant)
                        {
                            addBlock(X - x, Y - y, Z + z, BlockID, metadata);
                        }
                        else
                        {
                            world.setBlock(X - x, Y - y, Z + z, BlockID, metadata, 3);
                        }
                    }
                }
            }
        }
    }

    public void placeRectangularPrism(int X, int Y, int Z, double sizeX, double sizeY,
            double sizeZ, Block BlockID)
    {
        placeRectangularPrism(X, Y, Z, sizeX, sizeY, sizeZ, BlockID, false, false, 0, false, 0);
    }

    public void placeRectangularPrism(int X, int Y, int Z, double sizeX, double sizeY,
            double sizeZ, Block BlockID, boolean inverted, boolean hollow, int thickness, boolean fillair,
            int metadata)
    {
        if (!alive)
        {
            return;
        }

        if (sizeX % 2D == 0.0D && sizeZ % 2D != 0.0D)
        {
            for (double x = -(sizeX / 2D); x < sizeX / 2D; x++)
            {
                for (double y = 0.0D; y < sizeY; y++)
                {
                    for (double z = -(sizeZ / 2D); z < sizeZ / 2D; z++)
                        if (hollow && x >= -(sizeX / 2D) + (double)thickness && x < sizeX / 2D - (double)thickness && (y >= (double)(0 + thickness) && y < sizeY - (double)thickness || sizeY == 1.0D || sizeY == 2D || sizeY == 3D) && z >= -(sizeZ / 2D) + (double)thickness && z < sizeZ / 2D - (double)thickness)
                        {
                            if (fillair)
                                if (!Instant)
                                {
                                    addBlock((int)((double)X + x), (int)((double)Y + (inverted ? -y : y)), (int)((double)Z + z + 0.5D), Blocks.air);
                                }
                                else
                                {
                                    world.setBlock((int)((double)X + x), (int)((double)Y + (inverted ? -y : y)), (int)((double)Z + z + 0.5D), Blocks.air, 0, 3);
                                }
                        }
                        else
                        {
                            addBlock((int)((double)X + x), (int)((double)Y + (inverted ? -y : y)), (int)((double)Z + z + 0.5D), BlockID, metadata);
                        }
                }
            }
        }
        else if (sizeX % 2D != 0.0D && sizeZ % 2D == 0.0D)
        {
            for (double x = -(sizeX / 2D); x < sizeX / 2D; x++)
            {
                for (double y = 0.0D; y < sizeY; y++)
                {
                    for (double z = -sizeZ / 2D; z < sizeZ / 2D; z++)
                        if (hollow && x >= -(sizeX / 2D) + (double)thickness && x < sizeX / 2D - (double)thickness && (y >= (double)(0 + thickness) && y < sizeY - (double)thickness || sizeY == 1.0D || sizeY == 2D || sizeY == 3D) && z >= -(sizeZ / 2D) + (double)thickness && z < sizeZ / 2D - (double)thickness)
                        {
                            if (fillair)
                                if (!Instant)
                                {
                                    addBlock((int)((double)X + x + 0.5D), (int)((double)Y + (inverted ? -y : y)), (int)((double)Z + z), Blocks.air);
                                }
                                else
                                {
                                    world.setBlock((int)((double)X + x + 0.5D), (int)((double)Y + (inverted ? -y : y)), (int)((double)Z + z), Blocks.air, 0, 3);
                                }
                        }
                        else if (!Instant)
                        {
                            addBlock((int)((double)X + x + 0.5D), (int)((double)Y + (inverted ? -y : y)), (int)((double)Z + z), BlockID, metadata);
                        }
                        else
                        {
                            world.setBlock((int)((double)X + x + 0.5D), (int)((double)Y + (inverted ? -y : y)), (int)((double)Z + z), BlockID, metadata, 3);
                        }
                }
            }
        }
        else if (sizeX % 2D != 0.0D && sizeZ % 2D != 0.0D)
        {
            for (double x = -(sizeX / 2D); x < sizeX / 2D; x++)
            {
                for (double y = 0.0D; y < sizeY; y++)
                {
                    for (double z = -(sizeZ / 2D); z < sizeZ / 2D; z++)
                        if (hollow && x >= -(sizeX / 2D) + (double)thickness && x < sizeX / 2D - (double)thickness && (y >= (double)(0 + thickness) && y < sizeY - (double)thickness || sizeY == 1.0D || sizeY == 2D || sizeY == 3D) && z >= -(sizeZ / 2D) + (double)thickness && z < sizeZ / 2D - (double)thickness)
                        {
                            if (fillair)
                                if (!Instant)
                                {
                                    addBlock((int)((double)X + x + 0.5D), (int)((double)Y + (inverted ? -y : y)), (int)((double)Z + z + 0.5D), Blocks.air);
                                }
                                else
                                {
                                    world.setBlock((int)((double)X + x + 0.5D), (int)((double)Y + (inverted ? -y : y)), (int)((double)Z + z + 0.5D), Blocks.air, 0, 3);
                                }
                        }
                        else if (!Instant)
                        {
                            addBlock((int)((double)X + x + 0.5D), (int)((double)Y + (inverted ? -y : y)), (int)((double)Z + z + 0.5D), BlockID, metadata);
                        }
                        else
                        {
                            world.setBlock((int)((double)X + x + 0.5D), (int)((double)Y + (inverted ? -y : y)), (int)((double)Z + z + 0.5D), BlockID, metadata, 3);
                        }
                }
            }
        }
        else
        {
            for (double x = -(sizeX / 2D); x < sizeX / 2D; x++)
            {
                for (double y = 0.0D; y < sizeY; y++)
                {
                    for (double z = -(sizeZ / 2D); z < sizeZ / 2D; z++)
                        if (hollow && x >= -(sizeX / 2D) + (double)thickness && x < sizeX / 2D - (double)thickness && (y >= (double)(0 + thickness) && y < sizeY - (double)thickness || sizeY == 1.0D || sizeY == 2D || sizeY == 3D) && z >= -(sizeZ / 2D) + (double)thickness && z < sizeZ / 2D - (double)thickness)
                        {
                            if (fillair)
                            {
                                addBlock((int)((double)X + x), (int)((double)Y + (inverted ? -y : y)), (int)((double)Z + z), Blocks.air);
                            }
                        }
                        else if (!Instant)
                        {
                            addBlock((int)((double)X + x), (int)((double)Y + (inverted ? -y : y)), (int)((double)Z + z), BlockID, metadata);
                        }
                        else
                        {
                            world.setBlock((int)((double)X + x), (int)((double)Y + (inverted ? -y : y)), (int)((double)Z + z), BlockID, metadata, 3);
                        }
                }
            }
        }
    }

    public void placeSphere(int X, int Y, int Z, float radius, Block BlockID, boolean hollow, int thickness,
            boolean fillair, boolean noisy, int metadata)
    {
        if (!alive)
        {
            return;
        }

        for (int y = 0; y <= (int)Math.ceil(radius); y++)
        {
            for (int z = 0; z <= (int)Math.ceil(radius); z++)
            {
                for (int x = 0; x <= (int)Math.ceil(radius); x++)
                {
                    float distance = MathHelper.sqrt_double(x * x + z * z + y * y);

                    if (hollow && distance < radius - (float)thickness)
                    {
                        if (fillair)
                        {
                            if (!Instant)
                            {
                                addBlock(X + x, Y + y, Z + z, Blocks.air);
                            }
                            else
                            {
                                world.setBlock(X + x, Y + y, Z + z, Blocks.air, 0, 3);
                            }

                            if (!Instant)
                            {
                                addBlock(X + x, Y + y, Z - z, Blocks.air);
                            }
                            else
                            {
                                world.setBlock(X + x, Y + y, Z - z, Blocks.air, 0, 3);
                            }

                            if (!Instant)
                            {
                                addBlock(X - x, Y + y, Z - z, Blocks.air);
                            }
                            else
                            {
                                world.setBlock(X - x, Y + y, Z - z, Blocks.air, 0, 3);
                            }

                            if (!Instant)
                            {
                                addBlock(X - x, Y + y, Z + z, Blocks.air);
                            }
                            else
                            {
                                world.setBlock(X - x, Y + y, Z + z, Blocks.air, 0, 3);
                            }

                            if (!Instant)
                            {
                                addBlock(X + x, Y - y, Z + z, Blocks.air);
                            }
                            else
                            {
                                world.setBlock(X + x, Y - y, Z + z, Blocks.air, 0, 3);
                            }

                            if (!Instant)
                            {
                                addBlock(X + x, Y - y, Z - z, Blocks.air);
                            }
                            else
                            {
                                world.setBlock(X + x, Y - y, Z - z, Blocks.air, 0, 3);
                            }

                            if (!Instant)
                            {
                                addBlock(X - x, Y - y, Z - z, Blocks.air);
                            }
                            else
                            {
                                world.setBlock(X - x, Y - y, Z - z, Blocks.air, 0, 3);
                            }

                            if (!Instant)
                            {
                                addBlock(X - x, Y - y, Z + z, Blocks.air);
                            }
                            else
                            {
                                world.setBlock(X - x, Y - y, Z + z, Blocks.air, 0, 3);
                            }
                        }
                    }
                    else if (distance <= radius && (!noisy || distance >= radius + 1.0F || distance <= radius - 2.0F || world.rand.nextInt(12) <= 6))
                    {
                        if (!Instant)
                        {
                            addBlock(X + x, Y + y, Z + z, BlockID, metadata);
                        }
                        else
                        {
                            world.setBlock(X + x, Y + y, Z + z, BlockID, metadata, 3);
                        }

                        if (!Instant)
                        {
                            addBlock(X + x, Y + y, Z - z, BlockID, metadata);
                        }
                        else
                        {
                            world.setBlock(X + x, Y + y, Z - z, BlockID, metadata, 3);
                        }

                        if (!Instant)
                        {
                            addBlock(X - x, Y + y, Z - z, BlockID, metadata);
                        }
                        else
                        {
                            world.setBlock(X - x, Y + y, Z - z, BlockID, metadata, 3);
                        }

                        if (!Instant)
                        {
                            addBlock(X - x, Y + y, Z + z, BlockID, metadata);
                        }
                        else
                        {
                            world.setBlock(X - x, Y + y, Z + z, BlockID, metadata, 3);
                        }

                        if (!Instant)
                        {
                            addBlock(X + x, Y - y, Z + z, BlockID, metadata);
                        }
                        else
                        {
                            world.setBlock(X + x, Y - y, Z + z, BlockID, metadata, 3);
                        }

                        if (!Instant)
                        {
                            addBlock(X + x, Y - y, Z - z, BlockID, metadata);
                        }
                        else
                        {
                            world.setBlock(X + x, Y - y, Z - z, BlockID, metadata, 3);
                        }

                        if (!Instant)
                        {
                            addBlock(X - x, Y - y, Z - z, BlockID, metadata);
                        }
                        else
                        {
                            world.setBlock(X - x, Y - y, Z - z, BlockID, metadata, 3);
                        }

                        if (!Instant)
                        {
                            addBlock(X - x, Y - y, Z + z, BlockID, metadata);
                        }
                        else
                        {
                            world.setBlock(X - x, Y - y, Z + z, BlockID, metadata, 3);
                        }
                    }
                }
            }
        }
    }

    public void placeSphere(int X, int Y, int Z, int radius, Block BlockID)
    {
        placeSphere(X, Y, Z, radius, BlockID, false, 0, false, false, 0);
    }

    public void placeSphereTopsoil(int X, int Y, int Z, float radius, Block BlockID, Block interID, Block topsoilID,
            int blockmetadata, int intermetadata, int topmetadata)
    {
        if (!alive)
        {
            return;
        }

        for (int y = 0; y < (int)Math.ceil(radius); y++)
        {
            for (int z = 0; z < (int)Math.ceil(radius); z++)
            {
                for (int x = 0; x < (int)Math.ceil(radius); x++)
                {
                    float distance = MathHelper.sqrt_double(x * x + z * z + y * y);

                    if (distance <= radius)
                    {
                        if (world.getBlock(X + x, Y + y, Z + z) != BlockID && Y + y != 1)
                        {
                            addBlock(X + x, Y + y, Z + z, BlockID, blockmetadata);
                        }

                        if (world.getBlock(X + x, Y + y, Z - z) != BlockID && Y + y != 1)
                        {
                            addBlock(X + x, Y + y, Z - z, BlockID, blockmetadata);
                        }

                        if (world.getBlock(X - x, Y + y, Z - z) != BlockID && Y + y != 1)
                        {
                            addBlock(X - x, Y + y, Z - z, BlockID, blockmetadata);
                        }

                        if (world.getBlock(X - x, Y + y, Z + z) != BlockID && Y + y != 1)
                        {
                            addBlock(X - x, Y + y, Z + z, BlockID, blockmetadata);
                        }

                        if (world.getBlock(X + x, Y - y, Z + z) != BlockID && Y + y != 1)
                        {
                            addBlock(X + x, Y - y, Z + z, BlockID, blockmetadata);
                        }

                        if (world.getBlock(X + x, Y - y, Z - z) != BlockID && Y + y != 1)
                        {
                            addBlock(X + x, Y - y, Z - z, BlockID, blockmetadata);
                        }

                        if (world.getBlock(X - x, Y - y, Z - z) != BlockID && Y + y != 1)
                        {
                            addBlock(X - x, Y - y, Z - z, BlockID, blockmetadata);
                        }

                        if (world.getBlock(X - x, Y - y, Z + z) != BlockID && Y + y != 1)
                        {
                            addBlock(X - x, Y - y, Z + z, BlockID, blockmetadata);
                        }
                    }
                }
            }
        }

        for (int y = 0; y < (int)Math.ceil(radius); y++)
        {
            for (int z = 0; z < (int)Math.ceil(radius); z++)
            {
                for (int x = 0; x < (int)Math.ceil(radius); x++)
                {
                    float distance = MathHelper.sqrt_double(x * x + z * z + y * y);

                    if (distance <= radius)
                    {
                        if (world.getBlock(X + x, Y + y, Z + z) == BlockID && world.getBlock(X + x, Y + y + 1, Z + z) == BlockID && world.getBlock(X + x, Y + y + 2, Z + z).getMaterial() == Material.air)
                        {
                            addBlock(X + x, Y + y, Z + z, interID, intermetadata);
                        }

                        if (world.getBlock(X + x, Y + y, Z - z) == BlockID && world.getBlock(X + x, Y + y + 1, Z - z) == BlockID && world.getBlock(X + x, Y + y + 2, Z - z).getMaterial() == Material.air)
                        {
                            addBlock(X + x, Y + y, Z - z, interID, intermetadata);
                        }

                        if (world.getBlock(X - x, Y + y, Z - z) == BlockID && world.getBlock(X - x, Y + y + 1, Z - z) == BlockID && world.getBlock(X - x, Y + y + 2, Z - z).getMaterial() == Material.air)
                        {
                            addBlock(X - x, Y + y, Z - z, interID, intermetadata);
                        }

                        if (world.getBlock(X - x, Y + y, Z + z) == BlockID && world.getBlock(X - x, Y + y + 1, Z + z) == BlockID && world.getBlock(X - x, Y + y + 2, Z + z).getMaterial() == Material.air)
                        {
                            addBlock(X - x, Y + y, Z + z, interID, intermetadata);
                        }

                        if (world.getBlock(X + x, Y + y, Z + z) == BlockID && world.getBlock(X + x, Y + y + 1, Z + z).getMaterial() == Material.air)
                        {
                            addBlock(X + x, Y + y, Z + z, topsoilID, topmetadata);
                        }

                        if (world.getBlock(X + x, Y + y, Z - z) == BlockID && world.getBlock(X + x, Y + y + 1, Z - z).getMaterial() == Material.air)
                        {
                            addBlock(X + x, Y + y, Z - z, topsoilID, topmetadata);
                        }

                        if (world.getBlock(X - x, Y + y, Z - z) == BlockID && world.getBlock(X - x, Y + y + 1, Z - z).getMaterial() == Material.air)
                        {
                            addBlock(X - x, Y + y, Z - z, topsoilID, topmetadata);
                        }

                        if (world.getBlock(X - x, Y + y, Z + z) == BlockID && world.getBlock(X - x, Y + y + 1, Z + z).getMaterial() == Material.air)
                        {
                            addBlock(X - x, Y + y, Z + z, topsoilID, topmetadata);
                        }
                    }
                }
            }
        }
    }

    public void placeTopsoil(int X, int Y, int Z, int radius, Block interID, Block topsoilID, int intermetadata,
            int topmetadata)
    {
        placeTopsoil(X, Y, Z, radius, interID, topsoilID, intermetadata, topmetadata, null, false);
    }

    public void placeTopsoil(int X, int Y, int Z, int radius, Block interID, Block topsoilID, int intermetadata,
            int topmetadata, EntityPlayer player, boolean fromBiome)
    {
        if (!alive)
        {
            return;
        }
        
        for (int i = X - radius; i <= X + radius; i++)
        {
            for (int k = Z - radius; k <= Z + radius; k++)
            {
            	if(fromBiome)
            	{
            		BiomeGenBase a = world.getBiomeGenForCoords(i, k);
            		topsoilID = a.topBlock;
            		interID = a.fillerBlock;
            	}
                int j = getTopHeight(i, k, Y, radius);

                if (j != 0)
                {
                	if(interID == Blocks.sand && topsoilID == Blocks.sand)
                    {
                    	Block id = world.getBlock(i, j - 3, k);
                    	if(id.getMaterial() == Material.air || BlockUtils.isFluid(id) || id == Blocks.sand || id == Blocks.gravel)
                    	{
                    		addBlock(i, j - 3, k, Blocks.sandstone);
                    	}
                    }
                    addBlock(i, j, k, topsoilID, topmetadata);
                    addBlock(i, j - 1, k, interID, intermetadata);
                    addBlock(i, j - 2, k, interID, intermetadata);
                }
            }
        }
    }

    public int getTopHeight(int i, int k, int startheight, int range)
    {
        int j = startheight + range;
        Block id = world.getBlock(i, j, k);
        Block id1 = world.getBlock(i, j - 1, k);
        Block id2 = world.getBlock(i, j + 1, k);

        do
        {
            if (j < startheight - range)
            {
                return 0;
            }

            if (id.isOpaqueCube() && id1.isOpaqueCube() && (id2.getMaterial() == Material.air || id2.getLightOpacity() <= 2))
            {
                break;
            }

            j--;
            id = world.getBlock(i, j, k);
            id1 = world.getBlock(i, j - 1, k);
            id2 = world.getBlock(i, j + 1, k);
        }
        while (true);

        if (id.isOpaqueCube())
        {
            return j;
        }
        else
        {
            return 0;
        }
    }

    public void placeSpire(int X, int Y, int Z, int radius, int height, int startheight, Block blockID,
            int blockMeta)
    {
        placeCylinder(X, Y, Z, radius, startheight, blockID, blockMeta);
        placeCone(X, Y + startheight, Z, radius, height - startheight, blockID, blockMeta);
    }

    public void placeSpireHollow(int X, int Y, int Z, int radius, int height, int startheight, int thickness,
            Block blockID, int blockMeta, boolean cap, boolean fillair)
    {
        placeCylinderHollow(X, Y, Z, radius, startheight, thickness, cap, fillair, blockID, blockMeta, false);
        placeConeHollow(X, Y + startheight, Z, radius, height - startheight, blockID, blockMeta, thickness, false, fillair);
    }

    public void placeSpireHollowInverted(int X, int Y, int Z, int radius, int height, int startheight, int thickness,
            Block blockID, int blockMeta, boolean cap, boolean fillair)
    {
        placeCylinderHollow(X, Y - startheight, Z, radius, startheight, thickness, cap, fillair, blockID, blockMeta, false);
        placeConeHollowInverted(X, Y - startheight, Z, radius, height - startheight, blockID, blockMeta, thickness, false, fillair);
    }

    public void placeSpireInverted(int X, int Y, int Z, int radius, int height, int startheight, Block blockID,
            int blockMeta)
    {
        placeCylinder(X, Y - startheight, Z, radius, startheight, blockID, blockMeta);
        placeConeInverted(X, Y - startheight, Z, radius, height - startheight, blockID, blockMeta);
    }
    
    
    public void placeDoubleHelix(int x, int y, int z, float size, float startAngle)
    {
    	int i1 = 0;
		int k1 = 0;
		int i2 = 0;
		int k2 = 0;
    	for(int Y = 0; Y < 720; Y++)
    	{
    		
    		i1 = (int)Math.floor(x + Math.cos(Math.toRadians(Y + startAngle)) * size);
    		k1 = (int)Math.floor(z + Math.sin(Math.toRadians(Y + startAngle)) * size);
    		
    		i2 = (int)Math.floor(x - Math.cos(Math.toRadians(Y + startAngle)) * size);
    		k2 = (int)Math.floor(z - Math.sin(Math.toRadians(Y + startAngle)) * size);
    		addBlock(i1, (int)(Y / 30 + y), k1, Blocks.glass);
    		addBlock(i2, (int)(Y / 30 + y), k2, Blocks.glass);
    	}
    }
    
    public void placeLightningSpire(int x, int y, int z, float size)
    {
    	int i1 = 0;
		int k1 = 0;
		int i2 = 0;
		int k2 = 0;
		for(int Y = 0; Y < 17; Y++)
		{
			addBlock(x, (int)(Y + y), z, Blocks.glass);
		}
    	for(int Y = 0; Y < 720; Y++)
    	{
    		if(Y / 45 < 4)
    		{
    			i1 = (int)Math.round(x + Math.cos(Math.toRadians(Y)) * (size * Y / 180));
    			k1 = (int)Math.round(z + Math.sin(Math.toRadians(Y)) * (size * Y / 180));
    		
    			i2 = (int)Math.round(x - Math.cos(Math.toRadians(Y)) * (size * Y / 180));
    			k2 = (int)Math.round(z - Math.sin(Math.toRadians(Y)) * (size * Y / 180));
    		}else if(Y / 45 < 12)
    		{
    			i1 = (int)Math.round(x + Math.cos(Math.toRadians(Y)) * (size));
    			k1 = (int)Math.round(z + Math.sin(Math.toRadians(Y)) * (size));
    		
    			i2 = (int)Math.round(x - Math.cos(Math.toRadians(Y)) * (size));
    			k2 = (int)Math.round(z - Math.sin(Math.toRadians(Y)) * (size));
    		}else
    		{
    			i1 = (int)Math.round(x + Math.cos(Math.toRadians(Y)) * (size * (720 - Y) / 180));
    			k1 = (int)Math.round(z + Math.sin(Math.toRadians(Y)) * (size * (720 - Y) / 180));
    		
    			i2 = (int)Math.round(x - Math.cos(Math.toRadians(Y)) * (size * (720 - Y) / 180));
    			k2 = (int)Math.round(z - Math.sin(Math.toRadians(Y)) * (size * (720 - Y) / 180));
    		}
    		addBlock(i1, (int)(Y / 45 + y), k1, Blocks.glass);
    		addBlock(i2, (int)(Y / 45 + y), k2, Blocks.glass);
    	}
    }
    
    public void shuffle(int x, int y, int z, int range, int randomness, boolean excludeAir, boolean excludeLiquids, boolean excludeBedrock)
    {
    	shuffle(x - range, y - range, z - range, x + range, y + range, z + range, randomness, excludeAir, excludeLiquids, excludeBedrock);
    }
    
    public void shuffle(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int randomness, boolean excludeAir, boolean excludeLiquids, boolean excludeBedrock)
    {
    	int length = (maxX - minX);
    	int width = (maxY - minY);
    	int depth = (maxZ - minZ);
    	String[] blocks = new String[length * width * depth];
    	if(randomness == -1)
    	{
    		randomness = blocks.length - 1;
    	}
    	if(randomness >= blocks.length)
		{
			randomness -= blocks.length;
		}
    	int i = 0;
    	Block j;
    	for(int x = 0; x < length; x++)
    	{
    		for(int y = 0; y < width; y++)
    		{
    			for(int z = 0; z < depth; z++)
    			{
    				j = world.getBlock(x + minX, y + minY, z + minZ);
    				if(excludeAir && j.isAir(world,x + minX,y + minY,z + minZ)) continue;
    				if(excludeLiquids && (BlockUtils.isFluid(j))) continue;
    				if(excludeBedrock && j == Blocks.bedrock) continue;
    				blocks[i] = Block.blockRegistry.getNameForObject(j) + "|" + world.getBlockMetadata(x + minX, y + minY, z + minZ);
    				i++;
    			}
    		}
    	}
    	blocks = cloneTrim(blocks, i);
    	int index = 0;
    	for(int x = 0; x < length; x++)
    	{
    		for(int y = 0; y < width; y++)
    		{
    			for(int z = 0; z < depth; z++)
    			{
    				if(excludeAir)
    				{
    					if(world.isAirBlock(x + minX, y + minY, z + minZ))
    					{
    						continue;
    					}
    				}
    				if(excludeLiquids)
    				{
    					if(BlockUtils.isFluid(world.getBlock(x + minX, y + minY, z + minZ)))
    					{
    						continue;
    					}
    				}
    				if(excludeBedrock)
    				{
    					if(world.getBlock(x + minX, y + minY, z + minZ) == Blocks.bedrock) continue;
    				}
    				
    				index = x + y * width + z * width * depth + world.rand.nextInt(randomness);
    				do
    				{
    					if(index < blocks.length) break;
    					index -= blocks.length;
    				}while(true);
    				String[] id = blocks[index].split("|");
					Block block2 = (Block) Block.blockRegistry.getObject(id[0]);
					int meta2 = Integer.parseInt(id[1]);
    				addBlock(x + minX, y + minY, z + minZ, block2, meta2);
    			}
    		}
    	}
    }
    
    private String[] cloneTrim(String[] array, int newSize)
    {
    	String[] newDouble = new String[newSize];
    	for(int i = 0; i < newSize; i++)
    	{
    		if(i >= array.length)
    		{
    			newDouble[i] = "";
    			continue;
    		}
    		newDouble[i] = array[i];
    	}
    	return newDouble;
    }

    public void readFromNBT()
    {
        File minedir = new File(".");
        File levelsdir = new File(minedir, world.getWorldInfo().getWorldName());
        File shapeGenFile = new File(levelsdir, (new StringBuilder("ShapeGen_BlocksList")).append(shapeGenID).append(".dat").toString());

        if (!shapeGenFile.exists())
        {
            return;
        }

        try
        {
            DataInputStream in = new DataInputStream(new FileInputStream(shapeGenFile));
            BufferedReader read = new BufferedReader(new InputStreamReader(in));

            do
            {
                String s = read.readLine();

                if (MathHelper.stringNullOrLengthZero(s))
                {
                    break;
                }

                String as1[] = s.split("|");

                if (as1.length == 5)
                {
                    int i = Integer.parseInt(as1[0]);
                    int j = Integer.parseInt(as1[1]);
                    int k = Integer.parseInt(as1[2]);
                    int meta = Integer.parseInt(as1[4]);
                    String id = as1[3];
					Block block = (Block)Block.blockRegistry.getObject(id);
                    addBlock(i, j, k, block, meta);
                }
            }
            while (true);

            in.close();
            read.close();
        }
        catch (Exception exception) { }
    }

    public void removeBlocksUpdate(Map list)
    {
        synchronized (blocks)
        {
            blocks.remove(list);
        }
    }

    public void removeBlockUpdate(int i, int j, int k, int id)
    {
        removeBlockUpdate(i, j, k, id, 0);
    }

    public void removeBlockUpdate(int i, int j, int k, int id, int l)
    {
        synchronized (blocks)
        {
            blocks.remove("" + i + "|" + j + "|" + k);
        }
    }

    public void update()
    {
        if (!server.isServerRunning() && !stopping)
        {
            writeToNBT();
            return;
        }

        if (world == null || server == null)
        {
            return;
        }

        if (adding)
        {
            if (!updatingAnywhere)
            {
                updatingAnywhere = true;
            }

            return;
        }

        int c = 0;

        if (blocks == null || blocks.isEmpty())
        {
            if (updatingAnywhere)
            {
                updatingAnywhere = false;
            }

            return;
        }

        if (!updatingAnywhere)
        {
            updatingAnywhere = true;
        }

        if (world.playerEntities.isEmpty() && !Instant)
        {
            Instant = true;
            Notify = true;
        }
        else if (Instant && !world.playerEntities.isEmpty())
        {
            Instant = false;
            Notify = false;
        }

        synchronized (blocks)
        {
            for (Iterator it = blocks.entrySet().iterator(); c <= (Instant ? 16384 : 512) && it.hasNext(); it.remove())
            {
                java.util.Map.Entry block = (java.util.Map.Entry)it.next();
                String a[] = ((String)block.getKey()).split("|");
                int i = Integer.parseInt(a[0]);
                int j = Integer.parseInt(a[1]);
                int k = Integer.parseInt(a[2]);
                String[] b = ((String)block.getValue()).split("|");
                int meta = Integer.parseInt(b[1]);
                Block id = (Block)Block.blockRegistry.getObject(b[0]);

                if (world.getBlock(i, j, k) == id && world.getBlockMetadata(i, j, k) == meta)
                {
                    continue;
                }

                if (Notify)
                {
                    world.setBlock(i, j, k, id, meta, 3);
                }
                else
                {
                    world.setBlock(i, j, k, id, meta, 2);
                }

                c++;
            }
        }
    }

    public void writeToNBT()
    {
        stopping = true;
        File minedir = new File(".");
        File levelsdir = new File(minedir, world.getWorldInfo().getWorldName());
        File shapeGenFile = new File(levelsdir, (new StringBuilder("ShapeGen_BlocksList")).append(shapeGenID).append(".dat").toString());

        try
        {
            FileOutputStream in = new FileOutputStream(shapeGenFile);
            PrintWriter write = new PrintWriter(in);

            synchronized (blocks)
            {
                String s;

                for (Iterator it = blocks.entrySet().iterator(); it.hasNext(); write.print(s))
                {
                    java.util.Map.Entry block = (java.util.Map.Entry)it.next();
                    String sep = System.getProperty("line.separator");
                    s = (String)block.getKey() + "|" + (String)block.getValue() + sep;
                }
            }

            in.close();
            write.close();
        }
        catch (Exception exception) { }
    }

    private boolean generateFlowers(int par3, int par4, int par5, Block id)
    {
        Random par2Random = world.rand;

        for (int i = 0; i < 6; i++)
        {
            int k = (par3 + par2Random.nextInt(8)) - par2Random.nextInt(8);
            int l = par4;
            int i1 = (par5 + par2Random.nextInt(8)) - par2Random.nextInt(8);

            if (blocks.containsKey(toString(k, l - 1, i1)) && (blocks.containsKey(toString(k, l, i1)) && ((Block)Block.blockRegistry.getObject(((String)blocks.get(toString(k, l, i1))).split("|")[0])).getMaterial() == Material.air || !blocks.containsKey(toString(k, l, i1))) && ((Block)Block.blockRegistry.getObject(((String)blocks.get(toString(k, l - 1, i1))).split("|")[0])) == Blocks.dirt || ((Block)Block.blockRegistry.getObject(((String)blocks.get(toString(k, l, i1))).split("|")[0])) == Blocks.grass || world.getBlock(k, l - 1, i1) == Blocks.grass || world.getBlock(k, l - 1, i1) == Blocks.dirt)
            {
                addBlock(k, l, i1, id);
            }
        }

        return true;
    }

    private boolean generateGrass(int par3, int par4, int par5, Block tallGrassID, int tallGrassMetadata)
    {
        Random par2Random = world.rand;

        for (int i = 0; i < 12; i++)
        {
            int k = (par3 + par2Random.nextInt(8)) - par2Random.nextInt(8);
            int l = par4;
            int i1 = (par5 + par2Random.nextInt(8)) - par2Random.nextInt(8);

            if (blocks.containsKey(toString(k, l - 1, i1)) && (blocks.containsKey(toString(k, l, i1)) && ((Block)Block.blockRegistry.getObject(((String)blocks.get(toString(k, l, i1))).split("|")[0])).getMaterial() == Material.air || !blocks.containsKey(toString(k, l, i1))) && ((Block)Block.blockRegistry.getObject(((String)blocks.get(toString(k, l - 1, i1))).split("|")[0])) == Blocks.dirt || ((Block)Block.blockRegistry.getObject(((String)blocks.get(toString(k, l, i1))).split("|")[0])) == Blocks.grass || world.getBlock(k, l - 1, i1) == Blocks.grass || world.getBlock(k, l - 1, i1) == Blocks.dirt)
            {
                addBlock(k, l, i1, tallGrassID, tallGrassMetadata);
            }
        }

        return true;
    }

    private void makeLamp(int i, int j, int k, Block baseID)
    {
        makeLamp(i, j, k, baseID, 0);
    }

    private void makeLamp(int i, int j, int k, Block baseID, int baseMetadata)
    {
        addBlock(i, j, k, baseID, baseMetadata);
        addBlock(i, j + 1, k, Blocks.redstone_torch);
        addBlock(i, j + 2, k, Blocks.lit_redstone_lamp);
    }

    private String toString(int i, int j, int k)
    {
        return (new StringBuilder()).append(i).append("|").append(j).append("|").append(k).toString();
    }

    public synchronized void addBlock(int a[], Block blockID)
    {
        if (a.length < 3)
        {
            return;
        }
        else
        {
            addBlock(a[0], a[1], a[2], blockID);
            return;
        }
    }

    public static boolean Instant = false;
    public static boolean Notify = true;
    public static boolean persistantOrder;
    public static boolean persistantOrder1;
    private static MinecraftServer server;
    public static boolean alive;
    public boolean updatingAnywhere;
    public boolean adding;
    private Map<String,String> blocks;
    private Map<String, String> blocksToAdd;
    private final byte otherCoordPairs[] =
    {
        2, 0, 0, 1, 2, 1
    };
    private int shapeGenID;
    private World world;
    public static boolean stopping = false;
}
