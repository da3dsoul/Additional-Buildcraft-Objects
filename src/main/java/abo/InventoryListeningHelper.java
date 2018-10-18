package abo;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

public class InventoryListeningHelper {
    public static void HandleAddItemToInventory(EntityItemPickupEvent var1, ItemStack var2, ItemStack var4) {
        if (!var1.entityPlayer.inventory.addItemStackToInventory(var4)) {
            var2.stackSize = var4.stackSize;
            var1.item.setEntityItemStack(var2);
        } else {
            var2.stackSize = 0;
            if (var2.getItem() == Item.getItemFromBlock(Blocks.log)) {
                var1.entityPlayer.triggerAchievement(AchievementList.mineWood);
            } else if (var2.getItem() == Item.getItemFromBlock(Blocks.log2)) {
                var1.entityPlayer.triggerAchievement(AchievementList.mineWood);
            } else if (var2.getItem() == Items.leather) {
                var1.entityPlayer.triggerAchievement(AchievementList.killCow);
            } else if (var2.getItem() == Items.diamond) {
                var1.entityPlayer.triggerAchievement(AchievementList.diamonds);
            } else if (var2.getItem() == Items.blaze_rod) {
                var1.entityPlayer.triggerAchievement(AchievementList.blazeRod);
            }

            FMLCommonHandler.instance().firePlayerItemPickupEvent(var1.entityPlayer, var1.item);
            if (var2.stackSize <= 0) {
                var1.item.setDead();
            }
        }
    }
}
