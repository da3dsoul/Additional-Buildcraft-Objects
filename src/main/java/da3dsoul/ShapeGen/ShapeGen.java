// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ShapeGen.java

package da3dsoul.ShapeGen;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.*;

import abo.ABO;
import cpw.mods.fml.common.FMLCommonHandler;
import da3dsoul.scaryGen.generate.feature.ScaryGen_WorldGenBigTree;
import da3dsoul.scaryGen.generate.feature.ScaryGen_WorldGenElevatedMangroveTree;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
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
        if(!ABO.shapeGens.containsKey(i))
        {
            ABO.shapeGens.put(i, new ShapeGen(FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(i)));
        }
        return ABO.shapeGens.get(i);
    }

    public static ShapeGen getShapeGen(World w)
    {
        return getShapeGen(w.provider.dimensionId);
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
        if(msSinceLastTick != 0)
            msSinceLastTick = System.nanoTime() / 1000000 - msSinceLastTick;
        update();
        msSinceLastTick = System.nanoTime() / 1000000;
    }

    public void update()
    {
        if (stopping)
        {
            if(!saved) {
                writeToNBT();
                saved = true;
            }
            return;
        }

        if (server == null || world == null)
        {
            return;
        }

        if(msSinceLastTick >= 100) return;

        int c = 0;

        if (blocks == null || blocks.isEmpty())
        {
            return;
        }

        updatingAnywhere = true;
        for (Iterator<Map.Entry<String,BlockIdentity>> it = blocks.entrySet().iterator(); c <= (512) && it.hasNext(); it.remove())
        {
            java.util.Map.Entry<String, BlockIdentity> block = (java.util.Map.Entry)it.next();
            String a[] = ((String)block.getKey()).split(",");
            int i = Integer.parseInt(a[0]);
            int j = Integer.parseInt(a[1]);
            int k = Integer.parseInt(a[2]);

            Block id = block.getValue().getBlock();
            int meta = block.getValue().getMeta();

            if (world.getBlock(i, j, k) == id && world.getBlockMetadata(i, j, k) == meta)
            {
                continue;
            }

            if(!id.canBlockStay(world, i, j, k)) continue;

            world.setBlock(i, j, k, id, meta, 3);


            c++;
        }
        updatingAnywhere = false;

        blocks.putAll(blocksToAdd);
        blocksToAdd.clear();
    }

    public void addBlock(int i, int j, int k, Block id)
    {
        addBlock(i, j, k, id, 0);
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
    public void addBlockWithRandomMeta(int i, int j, int k, Block id, int startMeta, int endMeta) {
        if(startMeta == 0 && endMeta == 0) {
            addBlock(i,j,k,id,0);
            return;
        }
        int meta = world.rand.nextInt(endMeta - startMeta + 1) + startMeta;
        addBlock(i,j,k,id,meta);
    }

    public void addBlock(int i, int j, int k, Block id, int l)
    {
        if(l == -1) {
            try {
                int size = 0;
                String name = null;
                String name2 = new ItemStack(id, 1, 0).getDisplayName();
                do {
                    name = new ItemStack(id, 1, size).getDisplayName();
                    if((size != 0 && name.equals(name2)) || name.startsWith("tile.") || name.endsWith(".name")) {
                        size--;
                        break;
                    }
                    size++;
                } while(size < 16);
                if(size == 0 || size == -1 || size == 1) {
                    addBlock(i,j,k,id,0);
                    return;
                }
                addBlockWithRandomMeta(i, j, k, id, 0, size);
                return;
            }catch (Throwable t) {}
        }
        if (updatingAnywhere)
        {
            blocksToAdd.put(toString(i, j, k), new BlockIdentity(id,l == -1 ? 0 : l));
            return;
        }

        blocks.put(toString(i, j, k), new BlockIdentity(id, l == -1 ? 0 : l));
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
        if (updatingAnywhere)
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
            blocks.remove("" + i + "," + j + "," + k);
        }
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
            String returnVal = ai1[0]+ "," + ai1[1] + "," + ai1[2] + "," + Block.blockRegistry.getNameForObject(id) + "," + meta;
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
                        addBlockAtStart(X + x, Y + y, Z + z, Blocks.air);
                        addBlockAtStart(X + x, Y + y, Z - z, Blocks.air);
                        addBlockAtStart(X - x, Y + y, Z - z, Blocks.air);
                        addBlockAtStart(X - x, Y + y, Z + z, Blocks.air);
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

                        addBlock(X + x, Y + y, Z + z, id);
                        addBlock(X + x, Y + y, Z - z, id);
                        addBlock(X - x, Y + y, Z - z, id);
                        addBlock(X - x, Y + y, Z + z, id);
                    }
                }
            }
        }
    }

    public void makeLaputa(int i4, int j4, int k4)
    {
        new RunnableGenerator(i4,j4,k4) {
            @Override
            public void generate() {
                placeCylinder(X, Y, Z, 24, 3, Blocks.dirt, 0);
                placeCylinderHollow(X, Y, Z, 25, 9, 1, false, false, Blocks.sandstone, 2, false);
                placeDomeInverted(X, Y - 1, Z, 25F, Blocks.sandstone, false, 0, false, 2);
                ArrayList conepoints1 = getRadialPointsDomeInverted(X, Y, Z, 21, 36);
                for(int i = 0; i < conepoints1.size(); i++)
                {
                    int pos[] = (int[])conepoints1.get(i);
                    placeSpireInverted(pos[0], pos[1], pos[2], 4, 10, 2, Blocks.stonebrick, -1);
                }

                placeCylinderHollow(X, Y - 2, Z, 50, 6, 3, false, false, Blocks.stonebrick, -1, false);
                placeCylinderHollow(X, Y - 2, Z, 80, 6, 10, false, false, Blocks.sandstone, 2, false);
                placeCylinderHollow(X, Y - 2, Z, 80, 12, 1, false, false, Blocks.sandstone, 2, false);
                placeCylinderHollow(X, Y - 2, Z, 71, 12, 1, false, false, Blocks.sandstone, 2, false);
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
                            }, Blocks.sandstone, 2, false, null, null);
                    }

                    placeSpireInverted(pos[0], pos[1] + 1, pos[2], 10, 12, 4, Blocks.sandstone, 2);
                    int l = world.rand.nextInt(5) + 3;
                    for(int m = 0; m <= l; m++) {
                        int x = pos[0] + world.rand.nextInt(6) - world.rand.nextInt(6);
                        int y = pos[1] - 2 - world.rand.nextInt(2);
                        int z = pos[2] + world.rand.nextInt(6) - world.rand.nextInt(6);
                        placeConeInverted(x, y, z, 2, 7, Blocks.stonebrick, -1);
                    }
                    placeSpireHollow(pos[0], Y + 4, pos[2], 10, 19, 6, 1, Blocks.sandstone, 2, false, false);
                    placeCylinder(pos[0], Y + 4, pos[2], 9, 6, Blocks.air, 0);
                    placeCylinder(pos[0], Y + 3, pos[2], 9, 1, Blocks.grass, 0);
                    generateGrass(pos[0], Y + 4, pos[2], Blocks.tallgrass, 1);
                    generateFlowers(pos[0], Y + 4, pos[2], Blocks.red_flower);
                    generateFlowers(pos[0], Y + 4, pos[2], Blocks.yellow_flower);
                }

                conepoints1 = getRegularPolyVertices(X, Y + 3, Z, 50, 5);
                for(int i = 0; i < conepoints1.size(); i++)
                {
                    int pos[] = (int[])conepoints1.get(i);
                    placeSpireInverted(pos[0], pos[1] + 1, pos[2], 15, 15, 5, Blocks.sandstone, 2);
                    placeSpireHollow(pos[0], Y + 4, pos[2], 15, 24, 5, 1, Blocks.sandstone, 2, false, true);
                    placeCylinder(pos[0], Y + 3, pos[2], 14, 1, Blocks.grass, 0);
                    ArrayList points = getRegularPolyVertices(pos[0], Y + 3, pos[2], 7, 3);
                    for(int i1 = 0; i1 < points.size(); i1++)
                    {
                        int pos1[] = (int[])points.get(i1);
                        makeLamp(pos1[0], pos1[1], pos1[2], Blocks.gold_block, 0);
                    }

                    makeLamp(pos[0], Y + 3, pos[2], Blocks.gold_block, 0);
                    generateGrassAndFlowers(pos[0], Y + 4, pos[2], 10);
                }

                placeCylinderHollow(X, Y + 4, Z, 79, 5, 8, false, false, Blocks.air, 0, false);
                conepoints1 = getRegularPolyVertices(X, Y + 3, Z, 25, 5);
                for(int i = 0; i < conepoints1.size(); i++)
                {
                    int pos[] = (int[])conepoints1.get(i);
                    for(int x1 = -1; x1 <= 1; x1++)
                    {
                        for(int z1 = -1; z1 <= 1; z1++)
                        {
                            for(int y1 = 1; y1 <= 3; y1++)
                                addBlock(pos[0] + x1, pos[1] + y1, pos[2] + z1, Blocks.air);
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
                                addBlock(pos[0] + x1, pos[1] + y1, pos[2] + z1, Blocks.air);
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
                                addBlock(pos[0] + x1, pos[1] + y1, pos[2] + z1, Blocks.air);
                        }
                    }
                }

                placeSphere(X, Y - 10, Z, 8F, Blocks.air, false, 0, false, false, 0);
                placeCylinder(X, Y + 3, Z, 24, 1, Blocks.grass, 0);
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
                            }, Blocks.water, 0, false, null, null);
                    }
                }

                generateGrassAndFlowers(X, Y + 4, Z, 20);

                ScaryGen_WorldGenElevatedMangroveTree tree2 = new ScaryGen_WorldGenElevatedMangroveTree(true);
                tree2.useShapeGen = true;
                tree2.setConfigOptions(Blocks.log, Blocks.leaves, 0, 1, Blocks.grass, Blocks.dirt, 69, 75, 3);
                tree2.generateCustom(world, world.rand, X, Y + 4, Z, 7, 60);
                tree2.generateCustom(world, world.rand, X, Y + 4, Z, 7, 40);

                conepoints1 = getRegularPolyVertices(X, Y + 7, Z, 21, 15);
                for(int i = 0; i < conepoints1.size(); i++)
                {
                    int pos[] = (int[])conepoints1.get(i);
                    makeLamp(pos[0], pos[1], pos[2], Blocks.gold_block, 0);
                }

                conepoints1 = getRegularPolyVertices(X, Y + 7, Z, 8, 5);
                for(int i = 0; i < conepoints1.size(); i++)
                {
                    int pos[] = (int[])conepoints1.get(i);
                    makeLamp(pos[0], pos[1], pos[2], Blocks.gold_block, 0);
                }

                conepoints1 = getRegularPolyVertices(X, Y + 3, Z, 75, 15);
                for(int i = 0; i < conepoints1.size(); i++)
                {
                    int pos[] = (int[])conepoints1.get(i);
                    ArrayList points = getRegularPolyVertices(pos[0], Y + 3, pos[2], 5, 3);
                    for(int i1 = 0; i1 < points.size(); i1++)
                    {
                        int pos1[] = (int[])points.get(i1);
                        makeLamp(pos1[0], pos1[1], pos1[2], Blocks.gold_block, 0);
                    }

                    makeLamp(pos[0], pos[1], pos[2], Blocks.gold_block, 0);
                }

                int j = 0;
                for(int i = 0; i < 150 && j < 2500; j++) {
                    int x = X + world.rand.nextInt(100) - world.rand.nextInt(100);
                    int z = Z + world.rand.nextInt(100) - world.rand.nextInt(100);
                    int y = Y + world.rand.nextInt(85) - world.rand.nextInt(40);
                    if((!blocks.containsKey(ShapeGen.this.toString(x, y, z)) || blocks.get(ShapeGen.this.toString(x, y, z)).getBlock().getMaterial() == Material.air) && (!blocksToAdd.containsKey(ShapeGen.this.toString(x,y,z)) || blocksToAdd.get(ShapeGen.this.toString(x,y,z)).getBlock().getMaterial() == Material.air)) {
                        addBlock(x,y,z,Blocks.vine);
                        i++;
                    }
                }
            }
        }.run();
    }

    public void generateGrassAndFlowers(int X, int Y, int Z, int diameter)
    {
        for(int i = 0; i < Math.round(1 / 25 * Math.pow(diameter,2)); i++)
        {
            int x2 = world.rand.nextInt(diameter);
            x2 -= world.rand.nextInt(diameter);
            x2 += X;
            int z2 = world.rand.nextInt(diameter);
            z2 -= world.rand.nextInt(diameter);
            z2 += Z;
            generateGrass(x2, Y, z2, Blocks.tallgrass, 1);
            generateFlowers(x2, Y, z2, Blocks.red_flower);
            generateFlowers(x2, Y, z2, Blocks.yellow_flower);
        }
    }

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
                    addBlock(ai1[0], ai1[1], ai1[2], blockID, meta);
                }
            }
        }
    }

    public void placeCone(int X, int Y, int Z, int radius, int height, Block blockID, int blockMeta)
    {
        Vec3 end = Vec3.createVectorHelper(X, Y + height, Z);
        Vec3 start = Vec3.createVectorHelper(X + radius, Y, Z);

        for(int j = 0; j <= height; j++) {
            Vec3 vec3 = start.getIntermediateWithYValue(end, j + Y);
            if(vec3 == null) {
                addBlock(X, Y + j, Z, blockID, blockMeta);
                continue;
            }
            int newRadius = (int)Math.round(vec3.xCoord - X);
            for (int i = -newRadius; i <= newRadius; i++)
            {
                for (int k = -newRadius; k <= newRadius; k++)
                {
                    double distance = Math.sqrt(i * i + k * k);
                    if(distance <= (double)newRadius) {
                        addBlock(X + i, Y + j, Z + k, blockID, blockMeta);
                    }
                }
            }
        }
    }

    public void placeConeHollow(int X, int Y, int Z, int radius, int height, Block blockID, int blockMeta,
                                int thickness, boolean cap, boolean fillair)
    {
        Vec3 end = Vec3.createVectorHelper(X, Y + height, Z);
        Vec3 start = Vec3.createVectorHelper(X + radius, Y, Z);

        int oldRadius = 0;
        for(int j = 0; j <= height; j++) {
            Vec3 vec3 = start.getIntermediateWithYValue(end, j + Y);
            if(vec3 == null) {
                addBlock(X, Y + j, Z, blockID, blockMeta);
                continue;
            }
            int newRadius = (int)Math.round(vec3.xCoord - X);
            for (int i = -newRadius; i <= newRadius; i++)
            {
                for (int k = -newRadius; k <= newRadius; k++)
                {
                    double distance = Math.sqrt(i * i + k * k);
                    int newThickness = thickness;
                    if(j != 0 && oldRadius - newRadius > 1) {
                        newThickness = thickness + (oldRadius - newRadius);
                    }
                    if(distance <= (double)newRadius && cap && j == 0){
                        addBlock(X + i, Y + j, Z + k, blockID, blockMeta);
                    } else if(distance <= (double)newRadius && distance > (double)(newRadius - newThickness)) {
                        addBlock(X + i, Y + j, Z + k, blockID, blockMeta);
                    } else if(distance <= (double)newRadius && fillair) {
                        addBlock(X + i, Y + j, Z + k, Blocks.air, 0);
                    }
                }
            }
            oldRadius = newRadius;
        }
    }

    public void placeConeHollowInverted(int X, int Y, int Z, int radius, int height, Block blockID, int blockMeta,
                                        int thickness, boolean cap, boolean fillair)
    {
        Vec3 end = Vec3.createVectorHelper(X, Y - height, Z);
        Vec3 start = Vec3.createVectorHelper(X + radius, Y, Z);

        int oldRadius = 0;
        for(int j = 0; j <= height; j++) {
            Vec3 vec3 = start.getIntermediateWithYValue(end, Y - j);
            if(vec3 == null) {
                addBlock(X, Y - j, Z, blockID, blockMeta);
                continue;
            }
            int newRadius = (int)Math.round(vec3.xCoord - X);
            for (int i = -newRadius; i <= newRadius; i++)
            {
                for (int k = -newRadius; k <= newRadius; k++)
                {
                    double distance = Math.sqrt(i * i + k * k);
                    int newThickness = thickness;

                    if(j != 0 && oldRadius - newRadius > 1) {
                        newThickness = thickness + (oldRadius - newRadius);
                    }
                    if(distance <= (double)newRadius && cap && j == 0){
                        addBlock(X + i, Y - j, Z + k, blockID, blockMeta);
                    } else if(distance <= (double)newRadius && distance > (double)(newRadius - newThickness)) {
                        addBlock(X + i, Y - j, Z + k, blockID, blockMeta);
                    } else if(distance <= (double)newRadius && fillair) {
                        addBlock(X + i, Y - j, Z + k, Blocks.air, 0);
                    }
                }
            }
            oldRadius = newRadius;
        }
    }

    public void placeConeInverted(int X, int Y, int Z, int radius, int height, Block blockID, int blockMeta)
    {
        Vec3 end = Vec3.createVectorHelper(X, Y - height, Z);
        Vec3 start = Vec3.createVectorHelper(X + radius, Y, Z);

        for(int j = 0; j <= height; j++) {
            Vec3 vec3 = start.getIntermediateWithYValue(end, Y - j);
            if(vec3 == null) {
                addBlock(X, Y - j, Z, blockID, blockMeta);
                continue;
            }
            int newRadius = (int)Math.round(vec3.xCoord - X);
            for (int i = -newRadius; i <= newRadius; i++)
            {
                for (int k = -newRadius; k <= newRadius; k++)
                {
                    double distance = Math.sqrt(i * i + k * k);
                    if(distance <= (double)newRadius) {
                        addBlock(X + i, Y - j, Z + k, blockID, blockMeta);
                    }
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

        for (int i = -radius; i <= radius; i++)
        {
            for (int k = -radius; k <= radius; k++)
            {
                for (int j = 0; j < height; j++)
                {
                    double distance = Math.sqrt(i * i + k * k);

                    if (distance <= (double)radius)

                        addBlock(X + i, Y + j, Z + k, blockID, blockMeta);
                }
            }
        }
    }

    public void placeCylinderHollow(int X, int Y, int Z, int radius, int height, int thickness, boolean cap,
                                    boolean fillair, Block blockID, int blockMeta, boolean captop)
    {

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

                            addBlock(X + x, Y + y, Z + z, Blocks.air);
                            addBlock(X + x, Y + y, Z - z, Blocks.air);
                            addBlock(X - x, Y + y, Z - z, Blocks.air);
                            addBlock(X - x, Y + y, Z + z, Blocks.air);
                        }
                    }
                    else if (distance <= radius)
                    {
                        if (world.getBlock(X + x, Y + y, Z + z) != BlockID && Y + y != 1)
                            addBlock(X + x, Y + y, Z + z, BlockID, metadata);

                        if (world.getBlock(X + x, Y + y, Z - z) != BlockID && Y + y != 1)
                            addBlock(X + x, Y + y, Z - z, BlockID, metadata);

                        if (world.getBlock(X - x, Y + y, Z - z) != BlockID && Y + y != 1)

                            addBlock(X - x, Y + y, Z - z, BlockID, metadata);

                        if (world.getBlock(X - x, Y + y, Z + z) != BlockID && Y + y != 1)

                            addBlock(X - x, Y + y, Z + z, BlockID, metadata);
                    }
                }
            }
        }
    }

    public void placeDomeInverted(int X, int Y, int Z, float radius, Block BlockID, boolean hollow, int thickness,
                                  boolean fillair, int metadata)
    {

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
                            addBlock(X + x, Y - y, Z + z, Blocks.air);
                            addBlock(X + x, Y - y, Z - z, Blocks.air);
                            addBlock(X - x, Y - y, Z - z, Blocks.air);
                            addBlock(X - x, Y - y, Z + z, Blocks.air);
                        }
                    }
                    else if (distance <= radius)
                    {
                        addBlock(X + x, Y - y, Z + z, BlockID, metadata);
                        addBlock(X + x, Y - y, Z - z, BlockID, metadata);
                        addBlock(X - x, Y - y, Z - z, BlockID, metadata);
                        addBlock(X - x, Y - y, Z + z, BlockID, metadata);
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

                                addBlock((int)((double)X + x), (int)((double)Y + (inverted ? -y : y)), (int)((double)Z + z + 0.5D), Blocks.air);

                        }
                        else
                        {
                            addBlock((int)((double)X + x), (int)((double)Y + (inverted ? -y : y)), (int)((double)Z + z + 0.5D), BlockID, metadata);
                        }
                }
            }
        }
        else if (sizeX % 2D != 0.0D && sizeZ % 2D == 0.0D) {
            for (double x = -(sizeX / 2D); x < sizeX / 2D; x++) {
                for (double y = 0.0D; y < sizeY; y++) {
                    for (double z = -sizeZ / 2D; z < sizeZ / 2D; z++) {
                        if (hollow && x >= -(sizeX / 2D) + (double) thickness && x < sizeX / 2D - (double) thickness && (y >= (double) (0 + thickness) && y < sizeY - (double) thickness || sizeY == 1.0D || sizeY == 2D || sizeY == 3D) && z >= -(sizeZ / 2D) + (double) thickness && z < sizeZ / 2D - (double) thickness) {
                            if (fillair) {

                                addBlock((int) ((double) X + x + 0.5D), (int) ((double) Y + (inverted ? -y : y)), (int) ((double) Z + z), Blocks.air);
                            }

                        } else {
                            addBlock((int) ((double) X + x + 0.5D), (int) ((double) Y + (inverted ? -y : y)), (int) ((double) Z + z), BlockID, metadata);
                        }
                    }
                }
            }
        }
        else if (sizeX % 2D != 0.0D && sizeZ % 2D != 0.0D)
        {
            for (double x = -(sizeX / 2D); x < sizeX / 2D; x++)
            {
                for (double y = 0.0D; y < sizeY; y++) {
                    for (double z = -(sizeZ / 2D); z < sizeZ / 2D; z++) {
                        if (hollow && x >= -(sizeX / 2D) + (double) thickness && x < sizeX / 2D - (double) thickness && (y >= (double) (0 + thickness) && y < sizeY - (double) thickness || sizeY == 1.0D || sizeY == 2D || sizeY == 3D) && z >= -(sizeZ / 2D) + (double) thickness && z < sizeZ / 2D - (double) thickness) {
                            if (fillair)

                                addBlock((int) ((double) X + x + 0.5D), (int) ((double) Y + (inverted ? -y : y)), (int) ((double) Z + z + 0.5D), Blocks.air);
                        } else {
                            addBlock((int) ((double) X + x + 0.5D), (int) ((double) Y + (inverted ? -y : y)), (int) ((double) Z + z + 0.5D), BlockID, metadata);
                        }
                    }
                }
            }
        }
        else
        {
            for (double x = -(sizeX / 2D); x < sizeX / 2D; x++)
            {
                for (double y = 0.0D; y < sizeY; y++) {
                    for (double z = -(sizeZ / 2D); z < sizeZ / 2D; z++) {
                        if (hollow && x >= -(sizeX / 2D) + (double) thickness && x < sizeX / 2D - (double) thickness && (y >= (double) (0 + thickness) && y < sizeY - (double) thickness || sizeY == 1.0D || sizeY == 2D || sizeY == 3D) && z >= -(sizeZ / 2D) + (double) thickness && z < sizeZ / 2D - (double) thickness) {
                            if (fillair) {
                                addBlock((int) ((double) X + x), (int) ((double) Y + (inverted ? -y : y)), (int) ((double) Z + z), Blocks.air);
                            }
                        } else {
                            addBlock((int) ((double) X + x), (int) ((double) Y + (inverted ? -y : y)), (int) ((double) Z + z), BlockID, metadata);
                        }
                    }
                }
            }
        }
    }

    public void placeSphere(int X, int Y, int Z, float radius, Block BlockID, boolean hollow, int thickness,
                            boolean fillair, boolean noisy, int metadata)
    {

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
                            addBlock(X + x, Y + y, Z + z, Blocks.air);
                            addBlock(X + x, Y + y, Z - z, Blocks.air);
                            addBlock(X - x, Y + y, Z - z, Blocks.air);
                            addBlock(X - x, Y + y, Z + z, Blocks.air);
                            addBlock(X + x, Y - y, Z + z, Blocks.air);
                            addBlock(X + x, Y - y, Z - z, Blocks.air);
                            addBlock(X - x, Y - y, Z - z, Blocks.air);
                            addBlock(X - x, Y - y, Z + z, Blocks.air);
                        }
                    }
                    else if (distance <= radius && (!noisy || distance >= radius + 1.0F || distance <= radius - 2.0F || world.rand.nextInt(12) <= 6))
                    {
                        addBlock(X + x, Y + y, Z + z, BlockID, metadata);
                        addBlock(X + x, Y + y, Z - z, BlockID, metadata);
                        addBlock(X - x, Y + y, Z - z, BlockID, metadata);
                        addBlock(X - x, Y + y, Z + z, BlockID, metadata);
                        addBlock(X + x, Y - y, Z + z, BlockID, metadata);
                        addBlock(X + x, Y - y, Z - z, BlockID, metadata);
                        addBlock(X - x, Y - y, Z - z, BlockID, metadata);
                        addBlock(X - x, Y - y, Z + z, BlockID, metadata);
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

        for (int y = 0; y <= (int)Math.ceil(radius); y++)
        {
            for (int z = 0; z <= (int)Math.ceil(radius); z++)
            {
                for (int x = 0; x <= (int)Math.ceil(radius); x++)
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
                    blocks[i] = Block.blockRegistry.getNameForObject(j) + "," + world.getBlockMetadata(x + minX, y + minY, z + minZ);
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
                    String[] id = blocks[index].split(",");
                    Block block2 = (Block) Block.blockRegistry.getObject(id[0]);
                    int meta2 = Integer.parseInt(id[1]);
                    addBlock(x + minX, y + minY, z + minZ, block2, meta2);
                }
            }
        }
    }

    /*
    Ellipsoid Math
    if(isInEllipsoid)
    ((x-i)^2) / (a^2) + ((y-j)^2) / (b^2) + ((z-k)^2) / (c^2) < 1


     */

    public void blend(World world, int i, int j, int k, int radius, int falloff, boolean allowAir, boolean allowFluids, boolean rounded) {
        blend(world, i - radius, j - radius, k - radius, i + radius, j + radius, k + radius, falloff, allowAir, allowFluids, rounded);
    }

    public void blend(World world, int i, int j, int k, int radius, boolean allowAir, boolean allowFluids) {
        blend(world, i - radius, j - radius, k - radius, i + radius, j + radius, k + radius, 1, allowAir, allowFluids, true);
    }

    public void blend(World world, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int falloff, boolean allowAir, boolean allowFluids, boolean rounded) {
        if(minX == maxX || minY == maxY || minZ == maxZ) return;
        int temp1 = 0;
        if(minX > maxX) {
            temp1 = minX;
            minX = maxX;
            maxX = temp1;
        }
        if(minY > maxY) {
            temp1 = minY;
            minY = maxY;
            maxY = temp1;
        }
        if(minZ > maxZ) {
            temp1 = minZ;
            minZ = maxZ;
            maxZ = temp1;
        }
        int lengthX = maxX - minX + 1;
        int lengthY = maxY - minY + 1;
        int lengthZ = maxZ - minZ + 1;
        BlockIdentity oldBlocks[][][] = new BlockIdentity[lengthX + falloff * 2][lengthY + falloff * 2][lengthZ + falloff * 2];
        BlockIdentity newBlocks[][][] = new BlockIdentity[lengthX][lengthY][lengthZ];
        HashMap<BlockIdentity, Float> weightedBlockValue = new HashMap<BlockIdentity, Float>((falloff * 2 + 1) * (falloff * 2 + 1) - 1);

        int xIndex = 0;
        int yIndex = 0;
        int zIndex = 0;
        int x;
        int y;
        int z;

        for(x = minX - falloff; x <= maxX + falloff; x++) {
            for(z = minZ - falloff; z <= maxZ + falloff; z++) {
                for(y = minY - falloff; y <= maxY + falloff; y++) {
                    xIndex = x - minX + falloff;
                    yIndex = y - minY + falloff;
                    zIndex = z - minZ + falloff;
                    oldBlocks[xIndex][yIndex][zIndex] = new BlockIdentity(world.getBlock(x,y,z), world.getBlockMetadata(x,y,z));
                }
            }
        }

        float weight;
        float maxWeight = 0;
        BlockIdentity weightedMaxBlock = null;

        for(x = 0; x < lengthX; x++) {
            for(z = 0; z < lengthZ; z++) {
                for(y = 0; y < lengthY; y++) {
                    newBlocks[x][y][z] = oldBlocks[x + falloff][y + falloff][z + falloff];

                    weightedBlockValue.clear();
                    for(int i = -falloff; i <= falloff; i++) {
                        for(int j = -falloff; j <= falloff; j++) {
                            for(int k = -falloff; k <= falloff; k++) {
                                if(i == 0 && j == 0 && k == 0) continue;
                                weight = 1.0F / (float)Math.sqrt((i * i + j * j + k * k));
                                xIndex = falloff + x + i;
                                yIndex = falloff + y + j;
                                zIndex = falloff + z + k;

                                if(weightedBlockValue.containsKey(oldBlocks[xIndex][yIndex][zIndex])) {
                                    weightedBlockValue.put(oldBlocks[xIndex][yIndex][zIndex], weightedBlockValue.get(oldBlocks[xIndex][yIndex][zIndex]) + weight);
                                } else {
                                    weightedBlockValue.put(oldBlocks[xIndex][yIndex][zIndex], weight);
                                }
                            }
                        }
                    }

                    for(BlockIdentity identity : weightedBlockValue.keySet()) {
                        if(!allowAir && identity.getBlock().getMaterial() == Material.air) continue;
                        if(!allowFluids && BlockUtils.isFluid(identity.getBlock())) continue;

                        if(weightedBlockValue.get(identity) > maxWeight) {
                            maxWeight = weightedBlockValue.get(identity);
                            weightedMaxBlock = identity;
                        }
                    }

                    boolean tiedWeights = false;
                    for(BlockIdentity identity : weightedBlockValue.keySet()) {
                        if(identity != weightedMaxBlock && weightedBlockValue.get(identity) == maxWeight) {
                            tiedWeights = true;
                            break;
                        }
                    }

                    if(!tiedWeights) {
                        newBlocks[x][y][z] = weightedMaxBlock;
                    }
                }
            }
        }

        for(x = minX; x <= maxX; x++) {
            for(z = minZ; z <= maxZ; z++) {
                for(y = minY; y <= maxY; y++) {
                    if(rounded) {
                        double i = ((double)minX + (double)maxX) / 2D;
                        double j = (minY + maxY) / 2D;
                        double k = (minZ + maxZ) / 2D;
                        double a = (maxX - minX) / 2D;
                        double b = (maxY - minY) / 2D;
                        double c = (maxZ - minZ) / 2D;
                        if((Math.pow((x - i),2D) / Math.pow(a,2D) + Math.pow((y - j),2D) / Math.pow(b,2D) + Math.pow((z - k),2D) / Math.pow(c,2D)) > 1D) continue;
                    }
                    xIndex = x - minX;
                    yIndex = y - minY;
                    zIndex = z - minZ;

                    if(newBlocks[xIndex][yIndex][zIndex] == null || newBlocks[xIndex][yIndex][zIndex].getBlock() == null) continue;
                    addBlock(x,y,z,newBlocks[xIndex][yIndex][zIndex].getBlock(), newBlocks[xIndex][yIndex][zIndex].getMeta());
                }
            }
        }

    }

    public void blendOld(World world, int i, int j, int k, int size, boolean excludeAir, boolean excludeFluids)
    {
        int _bSize = size;
        int _twoBrushSize = 2 * _bSize;
        BlockIdentity _oldMaterials[][][] = new BlockIdentity[2 * (_bSize + 1) + 1][2 * (_bSize + 1) + 1][2 * (_bSize + 1) + 1];
        BlockIdentity _newMaterials[][][] = new BlockIdentity[_twoBrushSize + 1][_twoBrushSize + 1][_twoBrushSize + 1];

        for (int _x = 0; _x <= 2 * (_bSize + 1); _x++)
        {
            for (int _y = 0; _y <= 2 * (_bSize + 1); _y++)
            {
                for (int _z = 0; _z <= 2 * (_bSize + 1); _z++)
                {
                    _oldMaterials[_x][_y][_z] = new BlockIdentity(world.getBlock((i - _bSize - 1) + _x, (j - _bSize - 1) + _y, (k - _bSize - 1) + _z), world.getBlockMetadata((i - _bSize - 1) + _x, (j - _bSize - 1) + _y, (k - _bSize - 1) + _z));
                }
            }
        }

        for (int _x = 0; _x <= _twoBrushSize; _x++)
        {
            for (int _y = 0; _y <= _twoBrushSize; _y++)
            {
                for (int _z = 0; _z <= _twoBrushSize; _z++)
                {
                    _newMaterials[_x][_y][_z] = _oldMaterials[_x + 1][_y + 1][_z + 1];
                }
            }
        }

        for (int _x = 0; _x <= _twoBrushSize; _x++)
        {
            for (int _y = 0; _y <= _twoBrushSize; _y++)
            {
                for (int _z = 0; _z <= _twoBrushSize; _z++) {
                    HashMap<BlockIdentity, Integer> _materialFrequency = new HashMap<BlockIdentity, Integer>();
                    int _modeMatCount = 0;
                    BlockIdentity _modeMatId = null;
                    boolean _tiecheck = false;

                    for (int _m = -1; _m <= 1; _m++) {
                        for (int _n = -1; _n <= 1; _n++) {
                            for (int _o = -1; _o <= 1; _o++) {
                                if (_m != 0 || _n != 0 || _o != 0) {

                                    if (_materialFrequency.containsKey(_oldMaterials[_x + 1 + _m][_y + 1 + _n][_z + 1 + _o])) {
                                        _materialFrequency.put(_oldMaterials[_x + 1 + _m][_y + 1 + _n][_z + 1 + _o], _materialFrequency.get(_oldMaterials[_x + 1 + _m][_y + 1 + _n][_z + 1 + _o]) + 1);
                                    } else {
                                        _materialFrequency.put(_oldMaterials[_x + 1 + _m][_y + 1 + _n][_z + 1 + _o], 1);
                                    }
                                }
                            }
                        }
                    }

                    for (BlockIdentity _i : _materialFrequency.keySet()) {
                        if (_materialFrequency.get(_i) > _modeMatCount && (excludeAir && _i.getBlock().getMaterial() != Material.air) && (excludeFluids && !BlockUtils.isFluid(_i.getBlock())) && (_i.getBlock() != Blocks.bedrock)) {
                            _modeMatCount = _materialFrequency.get(_i);
                            _modeMatId = _i;
                        }
                    }

                    for (BlockIdentity _i : _materialFrequency.keySet()) {
                        if (!_i.equals(_modeMatId) && _materialFrequency.get(_i) == _modeMatCount) {
                            _tiecheck = true;
                            break;
                        }
                    }

                    if (!_tiecheck)
                    {
                        _newMaterials[_x][_y][_z] = _modeMatId;
                    }
                }
            }
        }

        double _rPow = Math.pow(_bSize + 1, 2D);

        for (int _x = _twoBrushSize; _x >= 0; _x--)
        {
            double _xPow = Math.pow(_x - _bSize - 1, 2D);

            for (int _y = 0; _y <= _twoBrushSize; _y++)
            {
                double _yPow = Math.pow(_y - _bSize - 1, 2D);

                for (int _z = _twoBrushSize; _z >= 0; _z--)
                {
                    if(_newMaterials[_x][_y][_z] == null || _newMaterials[_x][_y][_z].getBlock() == null) {
                        continue;
                    }
                    if (_xPow + _yPow + Math.pow(_z - _bSize - 1, 2D) > _rPow || (_newMaterials[_x][_y][_z].getBlock() == Blocks.bedrock) || (excludeAir && _newMaterials[_x][_y][_z].getBlock().getMaterial() == Material.air) || (excludeFluids && BlockUtils.isFluid(_newMaterials[_x][_y][_z].getBlock())))
                    {
                        continue;
                    }

                    if (world.getBlock((i - _bSize) + _x, (j - _bSize) + _y, (k - _bSize) + _z) != _newMaterials[_x][_y][_z].getBlock() || world.getBlockMetadata((i - _bSize) + _x, (j - _bSize) + _y, (k - _bSize) + _z) != _newMaterials[_x][_y][_z].getMeta())
                    {
                        addBlock((i - _bSize) + _x, (j - _bSize) + _y, (k - _bSize) + _z, _newMaterials[_x][_y][_z].getBlock(), _newMaterials[_x][_y][_z].getMeta());
                    }
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
        File leveldir = ABO.getWorldDir();
        File shapeGenFile = new File(leveldir, (new StringBuilder("ShapeGen_BlocksList")).append(shapeGenID).append(".dat").toString());

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

                String as1[] = s.split(",");

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

    public void writeToNBT()
    {
        File leveldir = ABO.getWorldDir();
        File shapeGenFile = new File(leveldir, (new StringBuilder("ShapeGen_BlocksList")).append(shapeGenID).append(".dat").toString());

        try
        {
            shapeGenFile.delete();
            FileOutputStream in = new FileOutputStream(shapeGenFile);
            PrintWriter write = new PrintWriter(in);

            synchronized (blocks)
            {
                String s;

                for (Iterator it = blocks.entrySet().iterator(); it.hasNext(); write.print(s))
                {
                    java.util.Map.Entry<String,BlockIdentity> block = (java.util.Map.Entry)it.next();
                    String sep = System.getProperty("line.separator");
                    s = (String)block.getKey() + "," + block.getValue().toString() + sep;
                }
            }

            in.close();
            write.close();
        }
        catch (Exception exception) { ABO.aboLog.warn("Unable to write blocks to file"); ABO.aboLog.catching(exception);}
    }

    private boolean generateFlowers(int par3, int par4, int par5, Block id)
    {
        Random par2Random = world.rand;

        for (int i = 0; i < 6; i++)
        {
            int k = (par3 + par2Random.nextInt(8)) - par2Random.nextInt(8);
            int l = par4;
            int i1 = (par5 + par2Random.nextInt(8)) - par2Random.nextInt(8);

            if (blocks.containsKey(toString(k, l - 1, i1)) && (blocks.get(toString(k, l - 1, i1)).getBlock() == Blocks.dirt || (blocks.get(toString(k, l - 1, i1)).getBlock() == Blocks.grass)) || world.getBlock(k, l - 1, i1) == Blocks.grass || world.getBlock(k, l - 1, i1) == Blocks.dirt)
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

            if (blocks.containsKey(toString(k, l - 1, i1)) && (blocks.get(toString(k, l - 1, i1)).getBlock() == Blocks.dirt || blocks.get(toString(k, l - 1, i1)).getBlock() == Blocks.grass))
            {
                if (!blocks.containsKey(toString(k, l, i1)) || blocks.get(toString(k, l, i1)).getBlock().getMaterial() == Material.air)
                {
                    addBlock(k, l, i1, tallGrassID, tallGrassMetadata);
                }
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
        return "" + i + "," + j + "," + k;
    }

    private static MinecraftServer server;
    private boolean updatingAnywhere = false;
    private Map<String,BlockIdentity> blocks;
    private Map<String, BlockIdentity> blocksToAdd;
    private final byte otherCoordPairs[] =
            {
                    2, 0, 0, 1, 2, 1
            };
    private int shapeGenID;
    private World world;
    public static boolean stopping = false;
    private boolean saved = false;
    private long msSinceLastTick = 0;
}
