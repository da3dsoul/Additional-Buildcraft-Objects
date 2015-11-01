package da3dsoul.scaryGen.generate.GeostrataGen.Ore;

import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;
import Reika.GeoStrata.Blocks.BlockOreTile;
import Reika.GeoStrata.Registry.RockTypes;
import Reika.GeoStrata.TileEntityGeoOre;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;

import java.util.Random;
public class WorldGenMinableOverride extends WorldGenMinable{

    private int mineableBlockMeta;
    private Block oreBlock;
    private OreType oreHelper;
    public int count;
    public int minY;
    public int maxY;
    public int rarity;
    public String name;

    public WorldGenMinableOverride(Block p_i45459_1_, int p_i45459_2_)
    {
        this(p_i45459_1_, p_i45459_2_, Blocks.stone);
    }

    public WorldGenMinableOverride(Block p_i45460_1_, int p_i45460_2_, Block p_i45460_3_)
    {
        this(p_i45460_1_, 0,p_i45460_2_,p_i45460_3_);
    }

    public WorldGenMinableOverride(Block block, int meta, int number, Block target)
    {
        super(block, number, target);
        mineableBlockMeta = meta;
        oreBlock = (Block)Block.blockRegistry.getObject("GeoStrata:geostrata_block_oretile");
        try {
            oreHelper = ReikaOreHelper.getFromVanillaOre(this.field_150519_a);
        } catch (Throwable t) {
            oreHelper = ModOreList.getModOreFromOre(this.field_150519_a,mineableBlockMeta);
        }
    }

    public boolean generate(World p_76484_1_, Random p_76484_2_, int p_76484_3_, int p_76484_4_, int p_76484_5_)
    {
        float f = p_76484_2_.nextFloat() * (float)Math.PI;
        double d0 = (double)((float)(p_76484_3_ + 8) + MathHelper.sin(f) * (float)this.numberOfBlocks / 8.0F);
        double d1 = (double)((float)(p_76484_3_ + 8) - MathHelper.sin(f) * (float)this.numberOfBlocks / 8.0F);
        double d2 = (double)((float)(p_76484_5_ + 8) + MathHelper.cos(f) * (float)this.numberOfBlocks / 8.0F);
        double d3 = (double)((float)(p_76484_5_ + 8) - MathHelper.cos(f) * (float)this.numberOfBlocks / 8.0F);
        double d4 = (double)(p_76484_4_ + p_76484_2_.nextInt(3) - 2);
        double d5 = (double)(p_76484_4_ + p_76484_2_.nextInt(3) - 2);

        for (int l = 0; l <= this.numberOfBlocks; ++l)
        {
            double d6 = d0 + (d1 - d0) * (double)l / (double)this.numberOfBlocks;
            double d7 = d4 + (d5 - d4) * (double)l / (double)this.numberOfBlocks;
            double d8 = d2 + (d3 - d2) * (double)l / (double)this.numberOfBlocks;
            double d9 = p_76484_2_.nextDouble() * (double)this.numberOfBlocks / 16.0D;
            double d10 = (double)(MathHelper.sin((float)l * (float)Math.PI / (float)this.numberOfBlocks) + 1.0F) * d9 + 1.0D;
            double d11 = (double)(MathHelper.sin((float)l * (float)Math.PI / (float)this.numberOfBlocks) + 1.0F) * d9 + 1.0D;
            int i1 = MathHelper.floor_double(d6 - d10 / 2.0D);
            int j1 = MathHelper.floor_double(d7 - d11 / 2.0D);
            int k1 = MathHelper.floor_double(d8 - d10 / 2.0D);
            int l1 = MathHelper.floor_double(d6 + d10 / 2.0D);
            int i2 = MathHelper.floor_double(d7 + d11 / 2.0D);
            int j2 = MathHelper.floor_double(d8 + d10 / 2.0D);

            for (int k2 = i1; k2 <= l1; ++k2)
            {
                double d12 = ((double)k2 + 0.5D - d6) / (d10 / 2.0D);

                if (d12 * d12 < 1.0D)
                {
                    for (int l2 = j1; l2 <= i2; ++l2)
                    {
                        double d13 = ((double)l2 + 0.5D - d7) / (d11 / 2.0D);

                        if (d12 * d12 + d13 * d13 < 1.0D)
                        {
                            for (int i3 = k1; i3 <= j2; ++i3)
                            {
                                double d14 = ((double)i3 + 0.5D - d8) / (d10 / 2.0D);

                                Block target = p_76484_1_.getBlock(k2, l2, i3);
                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D && target.isReplaceableOreGen(p_76484_1_, k2, l2, i3, field_150518_c))
                                {
                                    if(target == Blocks.stone) {
                                        p_76484_1_.setBlock(k2, l2, i3, field_150519_a, mineableBlockMeta, 2);
                                    } else {
                                        RockTypes rockType = RockTypes.getTypeFromID(target);
                                        if(ReikaOreHelper.isVanillaOre(field_150519_a)) {
                                            int oreMeta = BlockOreTile.getMetadataByTypes(rockType, oreHelper);
                                            p_76484_1_.setBlock(k2, l2, i3, oreBlock, oreMeta, 2);
                                        } else {
                                            p_76484_1_.setBlock(k2, l2, i3, oreBlock, 0, 2);
                                        }
                                        ((TileEntityGeoOre) p_76484_1_.getTileEntity(k2, l2, i3)).initialize(rockType, field_150519_a, mineableBlockMeta);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
}
