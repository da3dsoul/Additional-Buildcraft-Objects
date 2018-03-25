package da3dsoul.scaryGen.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockSandStone extends Block
{
    public static final String[] iconNames = new String[] { "bricks", "cracked_bricks", "mossy_bricks", "smooth", "mossy_smooth", "cracked_smooth" };
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
    @SideOnly(Side.CLIENT)
    private IIcon topIcon;
    @SideOnly(Side.CLIENT)
    private IIcon bottomIcon;

    public BlockSandStone()
    {
        super(Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata)
    {
        if(metadata < 3) {
            if (metadata < 0 || metadata >= this.icons.length)
            {
                metadata = 0;
            }

            return this.icons[metadata];
        } else if (side != 1)
        {
            if (side == 0)
            {
                return this.bottomIcon;
            }
            else
            {
                if (metadata < 0 || metadata >= this.icons.length)
                {
                    metadata = 0;
                }

                return this.icons[metadata];
            }
        }
        else
        {
            return this.topIcon;
        }
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(int meta)
    {
        return meta;
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list)
    {
        for(int i = 0; i < iconNames.length; i++) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        String abo = "additional-buildcraft-objects:";
        this.icons = new IIcon[iconNames.length];

        for (int i = 0; i < this.icons.length; ++i)
        {
            this.icons[i] = iconRegister.registerIcon(abo + this.getTextureName() + "_" + iconNames[i]);
        }

        this.topIcon = iconRegister.registerIcon(abo + this.getTextureName() + "_top");
        this.bottomIcon = iconRegister.registerIcon(abo + this.getTextureName() + "_bottom");
    }

}