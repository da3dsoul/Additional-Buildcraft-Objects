package abo.machines;

import buildcraft.api.items.IMapLocation;
import buildcraft.core.BCCreativeTab;
import buildcraft.core.ItemWrench;
import buildcraft.core.lib.block.BlockBuildCraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockAccelerator extends BlockBuildCraft {

    @SideOnly(Side.CLIENT)
    private IIcon icon;

    public BlockAccelerator() {
        super(Material.rock);

        this.setHardness(1.0F);
        this.setCreativeTab(BCCreativeTab.get("main"));
    }

    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileAccelerator();
    }

    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9) {
        if(super.onBlockActivated(world, i, j, k, entityplayer, par6, par7, par8, par9)) {
            return true;
        } else if(entityplayer.inventory.getCurrentItem() != null && entityplayer.inventory.getCurrentItem().getItem() instanceof IMapLocation) {
            return false;
        } else if(entityplayer.inventory.getCurrentItem() != null && entityplayer.inventory.getCurrentItem().getItem() instanceof ItemWrench) {
            TileEntity tile = world.getTileEntity(i, j, k);
            if(tile instanceof TileAccelerator) {
                entityplayer.addChatComponentMessage(new ChatComponentText("Energy: " + ((TileAccelerator) tile).getBattery().getEnergyStored()));
                entityplayer.addChatComponentMessage(new ChatComponentText("X: " + ((TileAccelerator) tile).xCoord + " Y: " + tile.yCoord + " Z: " + tile.zCoord));
                entityplayer.addChatComponentMessage(new ChatComponentText("Min: " + ((TileAccelerator) tile).xMin() + ", " + ((TileAccelerator) tile).yMin() + ", " + ((TileAccelerator) tile).zMin()));
                entityplayer.addChatComponentMessage(new ChatComponentText("Max: " + ((TileAccelerator) tile).xMax() + ", " + ((TileAccelerator) tile).yMax() + ", " + ((TileAccelerator) tile).zMax()));
                entityplayer.addChatComponentMessage(new ChatComponentText("Origin: " + (((TileAccelerator) tile).origin != null ? ((TileAccelerator) tile).origin.originWrapper.x + " Y: " + ((TileAccelerator) tile).origin.originWrapper.y + " Z: " + ((TileAccelerator) tile).origin.originWrapper.z : "NULL")));
                entityplayer.addChatComponentMessage(new ChatComponentText("Origin: " + (((TileAccelerator) tile).origin != null ? "Number of other Vertices: " + ((TileAccelerator) tile).origin.otherVerts.size() : "NULL")));
                return true;
            }
            return false;
        } else if(entityplayer.isSneaking()) {
            return false;
        } else {
            TileEntity tile = world.getTileEntity(i, j, k);
            if(tile instanceof TileAccelerator) {
                ((TileAccelerator)tile).tryConnection();
                return true;
            } else {
                return false;
            }
        }
    }



    @Override
    public void onBlockAdded(World world, int i, int j, int k) {
        TileEntity tile = world.getTileEntity(i, j, k);
        if(tile instanceof TileAccelerator) {
            ((TileAccelerator)tile).tryConnection();
        }
    }
    
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        String abo = "additional-buildcraft-objects:";
        icon = iconRegister.registerIcon(abo + "blockAccelerator");
    }

    public IIcon getIcon(int side, int metadata)
    {
        return icon;
    }

    @Override
    public IIcon getIconAbsolute(int side, int metadata) {
        return icon;
    }
}
