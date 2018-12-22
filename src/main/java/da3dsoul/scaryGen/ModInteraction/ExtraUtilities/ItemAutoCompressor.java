package da3dsoul.scaryGen.ModInteraction.ExtraUtilities;

import abo.ABO;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.rwtema.extrautils.ExtraUtils;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

@Optional.Interface(iface = "baubles.api.IBauble", modid = "Baubles")
public class ItemAutoCompressor extends Item implements IBauble {
    public final String itemName;

    public ItemAutoCompressor() {
        this.itemName = "autocompressor";
        this.setMaxStackSize(1);
        this.setCreativeTab(ExtraUtils.creativeTabExtraUtils);
        setUnlocalizedName("autocompressor");
        init();
    }

    public ItemAutoCompressor init()
    {
        GameRegistry.registerItem(this, "AutoCompressor");
        ABO.instance.addFullRecipe(new ItemStack(this),
                new Object[]{"AAA", "ABA", "AAA", 'A', Blocks.cobblestone, 'B', Blocks.crafting_table});
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        super.itemIcon = iconRegister.registerIcon("additional-buildcraft-objects:" + "autocompressor");
    }

    public boolean isFull3D() {
        return true;
    }

    public void onUpdate(ItemStack var1, World var2, Entity var3, int var4, boolean var5) {
            NBTTagCompound var6 = var3.getEntityData();
            var6.setLong("da3dsoul.compressionUpdate", var3.worldObj.getTotalWorldTime());
    }

    public BaubleType getBaubleType(ItemStack var1) {
        return BaubleType.AMULET;
    }

    public void onWornTick(ItemStack var1, EntityLivingBase var2) {
        NBTTagCompound var3 = var2.getEntityData();
        var3.setLong("da3dsoul.compressionUpdate", var2.worldObj.getTotalWorldTime());
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
