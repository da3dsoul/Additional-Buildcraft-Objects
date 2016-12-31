package da3dsoul.scaryGen.items;

import abo.ABO;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import da3dsoul.scaryGen.pathfinding_astar.FollowableEntity;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.List;
import java.util.ListIterator;

public class ItemSiloStick extends Item {

	public ItemSiloStick() {
		super();
		setCreativeTab(CreativeTabs.tabTools);
		setUnlocalizedName("SiloStick");
	}

    private enum STAGE {
        CENTER,
        RADIUS,
        HEIGHT,
        BLOCK1,
        BLOCK2,
        PLACE
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon("additional-buildcraft-objects:" + "SiloStick");
	}

	@SuppressWarnings("unchecked")
	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
        if(!ABO.useYellowDye) return itemstack;

		STAGE stage = STAGE.values()[getStage(itemstack)];
        switch(stage){
            case CENTER: {
                MovingObjectPosition pos = entityplayer.rayTrace(50, 1.0F);
                if (pos != null && pos.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    setCenter(itemstack, pos.blockX, pos.blockY, pos.blockZ);
                    setStage(itemstack, STAGE.RADIUS.ordinal());
                }
                break;
            }
        }
		return itemstack;
	}

    private void setRadius(ItemStack itemstack, int radius) {
        if(itemstack == null) return;
        NBTTagCompound compound;
        if(!itemstack.hasTagCompound() || itemstack.getTagCompound() == null) {
            compound = new NBTTagCompound();
        } else {
            compound = itemstack.getTagCompound();
        }

        compound.setInteger("radius", radius);
    }

    private int getRadius(ItemStack itemstack) {
        if(itemstack == null) return -1;
        if(!itemstack.hasTagCompound() || itemstack.getTagCompound() == null) return -1;

        NBTTagCompound compound = itemstack.getTagCompound();
        int radius = -1;
        if(compound.hasKey("radius")) {
            radius = compound.getInteger("radius");
        }
        return radius;
    }

    private void setHeight(ItemStack itemstack, int height) {
        if(itemstack == null) return;
        NBTTagCompound compound;
        if(!itemstack.hasTagCompound() || itemstack.getTagCompound() == null) {
            compound = new NBTTagCompound();
        } else {
            compound = itemstack.getTagCompound();
        }

        compound.setInteger("height", height);
    }

    private int getHeight(ItemStack itemstack) {
        if(itemstack == null) return -1;
        if(!itemstack.hasTagCompound() || itemstack.getTagCompound() == null) return -1;

        NBTTagCompound compound = itemstack.getTagCompound();
        int height = -1;
        if(compound.hasKey("height")) {
            height = compound.getInteger("height");
        }
        return height;
    }

    private void setStage(ItemStack itemstack, int stage) {
        if(itemstack == null) return;
        NBTTagCompound compound;
        if(!itemstack.hasTagCompound() || itemstack.getTagCompound() == null) {
            compound = new NBTTagCompound();
        } else {
            compound = itemstack.getTagCompound();
        }

        compound.setInteger("stage", stage);
    }

    private int getStage(ItemStack itemstack) {
        if(itemstack == null) return 0;
        if(!itemstack.hasTagCompound() || itemstack.getTagCompound() == null) return 0;

        NBTTagCompound compound = itemstack.getTagCompound();
        int stage = 0;
        if(compound.hasKey("stage")) {
            stage = compound.getInteger("stage");
        }
        return stage;
    }

    private void setCenter(ItemStack itemstack, int X, int Y, int Z) {
        if(itemstack == null) return;
        NBTTagCompound compound;
        if(!itemstack.hasTagCompound() || itemstack.getTagCompound() == null) {
            compound = new NBTTagCompound();
        } else {
            compound = itemstack.getTagCompound();
        }

        compound.setIntArray("center", new int[] { X, Y, Z });
    }

    private int[] getCenter(ItemStack itemstack) {
        if(itemstack == null) return new int[] { -1, -1, -1 };
        if(!itemstack.hasTagCompound() || itemstack.getTagCompound() == null) return new int[] { -1, -1, -1 };

        NBTTagCompound compound = itemstack.getTagCompound();
        int[] center = new int[] { -1, -1, -1 };
        if(compound.hasKey("center")) {
            center = compound.getIntArray("center");
        }
        return center;
    }

    private void setBlock1(ItemStack itemstack, Block block, int meta) {
        if(itemstack == null) return;
        NBTTagCompound compound;
        if(!itemstack.hasTagCompound() || itemstack.getTagCompound() == null) {
            compound = new NBTTagCompound();
        } else {
            compound = itemstack.getTagCompound();
        }

        compound.setString("block1", "" + GameData.blockRegistry.getNameForObject(block) + "," + meta);
    }

    private Object[] getBlock1(ItemStack itemstack) {
        if(itemstack == null) return null;
        if(!itemstack.hasTagCompound() || itemstack.getTagCompound() == null) return null;

        NBTTagCompound compound = itemstack.getTagCompound();
        Block block = null;
        int meta = 0;
        String string;
        if(compound.hasKey("block1")) {
            string = compound.getString("block1");
            String[] strings = string.split(",");
            block = GameData.blockRegistry.getObject(strings[0]);
            meta = Integer.parseInt(strings[1]);
        }
        if(block == null) return null;
        return new Object[] { block, meta };
    }

    private void setBlock2(ItemStack itemstack, Block block, int meta) {
        if(itemstack == null) return;
        NBTTagCompound compound;
        if(!itemstack.hasTagCompound() || itemstack.getTagCompound() == null) {
            compound = new NBTTagCompound();
        } else {
            compound = itemstack.getTagCompound();
        }

        compound.setString("block2", "" + GameData.blockRegistry.getNameForObject(block) + "," + meta);
    }

    private Object[] getBlock2(ItemStack itemstack) {
        if(itemstack == null) return null;
        if(!itemstack.hasTagCompound() || itemstack.getTagCompound() == null) return null;

        NBTTagCompound compound = itemstack.getTagCompound();
        Block block = null;
        int meta = 0;
        String string;
        if(compound.hasKey("block2")) {
            string = compound.getString("block2");
            String[] strings = string.split(",");
            block = GameData.blockRegistry.getObject(strings[0]);
            meta = Integer.parseInt(strings[1]);
        }
        if(block == null) return null;
        return new Object[] { block, meta };
    }

	private boolean hasRespiration(ItemStack itemstack)
    {
        if(!itemstack.isItemEnchanted()) return false;
        if(EnchantmentHelper.getEnchantmentLevel(Enchantment.respiration.effectId, itemstack) <= 0) return false;
        return true;
    }

    private boolean hasFireProtection(ItemStack itemstack)
    {
        if(!itemstack.isItemEnchanted()) return false;
        if(EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, itemstack) <= 0) return false;
        return true;
    }

}
