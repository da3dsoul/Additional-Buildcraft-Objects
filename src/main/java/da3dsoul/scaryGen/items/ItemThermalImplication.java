package da3dsoul.scaryGen.items;

import abo.ABO;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalfoundation.ThermalFoundation;
import cofh.thermalfoundation.item.TFItems;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.ArrayList;

@Optional.Interface(iface = "IBauble", modid = "Baubles")
public class ItemThermalImplication extends Item implements IBauble {
    public String modName = "thermalfoundation";
    public final String itemName;

    public ItemThermalImplication() {
        this.itemName = "thermalimplication";
        this.setMaxStackSize(1);
        this.setCreativeTab(ThermalFoundation.tabCommon);
        setUnlocalizedName("thermalImplication");
        init();
    }

    public ItemThermalImplication init()
    {
        GameRegistry.registerItem(this, "ThermalImplication");
        char[] abde = { 'A', 'B', 'D', 'E' };
        ArrayList<char[]> list = new ArrayList<char[]>(16);
        permute(abde, list);
        for (char[] arr : list) {
            String top = " " + arr[0] + " ";
            String middle = arr[1] + "C" + arr[2];
            String bottom = " " + arr[3] + "F";
            ABO.instance.addFullRecipe(new ItemStack(this),
                    new Object[]{top, middle, bottom, 'A', TFItems.gearTin, 'B', TFItems.gearCopper, 'C', TFItems.gearIron, 'D', TFItems.gearLead, 'E', TFItems.gearSilver, 'F', Items.iron_ingot});
        }
        return this;
    }

    private static void permute(char[] arr, ArrayList<char[]> list){
        permuteHelper(arr, 0, list);
    }

    private static void permuteHelper(char[] arr, int index, ArrayList<char[]> list){
        if(index >= arr.length - 1){
            list.add(arr.clone());
            return;
        }

        for(int i = index; i < arr.length; i++){
            char t = arr[index];
            arr[index] = arr[i];
            arr[i] = t;
            permuteHelper(arr, index+1, list);
            t = arr[index];
            arr[index] = arr[i];
            arr[i] = t;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        super.itemIcon = iconRegister.registerIcon("additional-buildcraft-objects:" + "thermalImplication");
    }

    public boolean isFull3D() {
        return true;
    }

    public void onUpdate(ItemStack var1, World var2, Entity var3, int var4, boolean var5) {
            NBTTagCompound var6 = var3.getEntityData();
            var6.setLong("da3dsoul.implicationUpdate", var3.worldObj.getTotalWorldTime());
    }

    public BaubleType getBaubleType(ItemStack var1) {
        return BaubleType.BELT;
    }

    public void onWornTick(ItemStack var1, EntityLivingBase var2) {
        NBTTagCompound var3 = var2.getEntityData();
        var3.setLong("da3dsoul.implicationUpdate", var2.worldObj.getTotalWorldTime());
    }

    public void onEquipped(ItemStack var1, EntityLivingBase var2) {
    }

    public void onUnequipped(ItemStack var1, EntityLivingBase var2) {
    }

    public boolean canEquip(ItemStack var1, EntityLivingBase var2) {
        return true;
    }

    public boolean canUnequip(ItemStack var1, EntityLivingBase var2) {
        return true;
    }
}
