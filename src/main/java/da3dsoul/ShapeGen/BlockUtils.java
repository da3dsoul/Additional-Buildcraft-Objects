package da3dsoul.ShapeGen;

import net.minecraft.block.Block;
import net.minecraftforge.fluids.FluidRegistry;

public class BlockUtils
{

    public static String getStringWrapper(int i, int j, int k, Block block, int meta) {
        return "" + i + "," + j + "," + k + "," + Block.blockRegistry.getNameForObject(block) + "," + meta;
    }
    
    private static int round(double d)
    {
    	return (int)Math.round(d);
    }

    public static boolean isFluid(Block block) {
        return FluidRegistry.lookupFluidForBlock(block) != null;
    }


}
