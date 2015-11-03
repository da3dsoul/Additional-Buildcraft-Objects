package da3dsoul.ShapeGen;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fluids.FluidRegistry;

public class BlockUtils
{
    
    public static int[] getIDandMetadata(double d)
    {
    	int a = MathHelper.floor_double(d);
    	d -= a;
    	d *= 100;
    	int b = round(d);
    	//Minecraft.getMinecraft().getLogAgent().logInfo("" + d);
    	return new int[]{ a, b };
    }

    public static String getStringWrapper(int i, int j, int k, Block block, int meta) {
        return "" + i + "|" + j + "|" + k + "|" + Block.blockRegistry.getNameForObject(block) + "|" + meta;
    }
    
    public static double getDoubleWrapper(int id, int metadata)
    {
    	//Minecraft.getMinecraft().getLogAgent().logInfo("" + (id + (metadata / 100)));
    	
    	double d = metadata;
    	d /= 100;
    	d += id;
    	return d;
    }
    
    private static int round(double d)
    {
    	return (int)Math.round(d);
    }
    
    public static boolean isWater(double id)
    {
    	id = Math.floor(id);
    	return id == 8 || id == 9;
    }
    
    public static boolean isLava(double id)
    {
    	id = Math.floor(id);
    	return id == 10 || id == 11;
    }

    public static boolean isFluid(Block block) {
        return FluidRegistry.lookupFluidForBlock(block) != null;
    }


}
