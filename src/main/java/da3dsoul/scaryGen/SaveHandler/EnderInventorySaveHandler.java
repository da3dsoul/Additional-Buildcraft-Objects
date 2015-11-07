package da3dsoul.scaryGen.SaveHandler;

import abo.ABO;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class EnderInventorySaveHandler extends WorldSavedData {

    public EnderInventorySaveHandler(String id) {
        super(id);
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     *
     * @param p_76184_1_
     */
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        if (tag != null) {
            NBTTagList list = tag.getTagList("EnderInventory", 10);
            ABO.instance.theInventoryEnderChest.loadInventoryFromNBT(list);
        }
    }

    /**
     * write data to NBTTagCompound from this MapDataBase, similar to Entities and TileEntities
     *
     * @param p_76187_1_
     */
    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        NBTTagList list = ABO.instance.theInventoryEnderChest.saveInventoryToNBT();
        nbt.setTag("EnderInventory", list);
    }

    public static EnderInventorySaveHandler initWorldData(World world) {
        EnderInventorySaveHandler worldData = (EnderInventorySaveHandler)world.loadItemData(EnderInventorySaveHandler.class, "EnderInventory");
        if(worldData == null) {
            worldData = new EnderInventorySaveHandler("EnderInventory");
            world.setItemData("EnderInventory", worldData);
            worldData.markDirty();
        }

        return worldData;
    }

    public boolean isDirty() {
        return true;
    }

}
