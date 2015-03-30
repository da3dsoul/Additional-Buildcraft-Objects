package da3dsoul.scaryGen.generate;

import abo.ABO;
import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashMap;

public class BiomeStoneGen {
    public static Block[][] biomeStoneArray;

    public static void init()
    {
        BiomeGenBase[] biomes = BiomeGenBase.getBiomeGenArray();

        ArrayList<ItemStack> blocks = OreDictionary.getOres("stone");

        biomeStoneArray = new Block[biomes.length][];

        double[] blockColorHSL;
        int color;
        double[] biomeColorHSL;
        for(BiomeGenBase biome : biomes) {
            // Calculate by biome color
            // Color is Stored in the first 24 bits of an int in RGB
            // I convert to HSL for ease of determining how similar a color is mathematically

            if(biome == null) continue;
            color = biome.color;
            biomeColorHSL = getRGB(color);
            ArrayList<Block> list = new ArrayList<Block>();
            for(ItemStack stack : blocks) {
                if(stack.getItemDamageForDisplay() > 0) continue;
                if(Block.blockRegistry.getNameForObject(((ItemBlock)stack.getItem()).field_150939_a).equalsIgnoreCase(BlockColorLookup.OPAL.name))
                    continue;
                color = getColorFromItemStack(stack);
                blockColorHSL = getRGB(color);
                double dist = dist(biomeColorHSL,blockColorHSL);
                if(dist <= 50) {
                    if(!list.contains(((ItemBlock)stack.getItem()).field_150939_a)) {
                        list.add(((ItemBlock) stack.getItem()).field_150939_a);
                        ABO.aboLog.info("Added " + Block.blockRegistry.getNameForObject(((ItemBlock) stack.getItem()).field_150939_a) + " to " + biome.biomeName + " on the geostrataGen map");
                    }
                }
            }
            biomeStoneArray[biome.biomeID] = (Block[]) list.toArray(new Block[list.size()]);
            ABO.aboLog.info("Added " + biome.biomeName + " to the geostrataGen map with " + list.size() + " entries");
        }
    }

    public static double dist(double[] d, double[] d1)
    {
        return Math.sqrt(Math.pow(d1[0] - d[0], 2) + Math.pow(d1[1] - d[1], 2) + Math.pow(d1[2] - d[2], 2));
    }

    private static int getColorFromItemStack(ItemStack item){
        for(BlockColorLookup colorLookup : BlockColorLookup.values()) {
            if(Block.blockRegistry.getNameForObject(((ItemBlock) item.getItem()).field_150939_a).equalsIgnoreCase(colorLookup.name)){
                return colorLookup.color;
            }
        }
        return 0;
    }

    private static double[] getRGB(int color){
        byte r = (byte)((color >> 16) & 255);
        byte g = (byte)((color >> 8) & 255);
        byte b = (byte)(color & 255);
        return new double[]{r,g,b};
    }

    private static double[] getHSL(int color) {
        byte r = (byte)((color >> 16) & 255);
        byte g = (byte)((color >> 8) & 255);
        byte b = (byte)(color & 255);
        double r1 = r / 255;
        double g1 = g / 255;
        double b1 = b / 255;
        double min = Math.min(Math.min(r1, b1), g1);
        double max = Math.max(Math.max(r1, b1), g1);
        double l = (min + max) / 2;

        double s = 0;
        if(min != max) {
            if(l <= 0.5){
                s = (max-min)/(max+min);
            } else
            {
                s = ( max-min)/(2.0-max-min);
            }
        }

        double h = 0;
        if(s == 0) {
            h = 0;
        } else if(r1 == max){
            h = (g1-b1)/(max-min);
        } else if(g1 == max) {
            h = 2.0 + (b1-r1)/(max-min);
        } else if(b1 == max) {
            h = 4.0 + (r1-g1)/(max-min);
        }
        h *= 60;
        if(h < 0) h += 360;

        return new double[] { h, s, l};
    }
}
