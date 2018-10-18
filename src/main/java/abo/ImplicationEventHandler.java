package abo;

import cofh.core.util.oredict.OreDictionaryArbiter;
import cofh.lib.util.helpers.ItemHelper;
import cofh.thermalfoundation.block.BlockOre;
import cofh.thermalfoundation.util.LexiconManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import java.util.HashMap;

import static abo.InventoryListeningHelper.HandleAddItemToInventory;

public class ImplicationEventHandler {
    public static ImplicationEventHandler instance = new ImplicationEventHandler();

    public ImplicationEventHandler() {
    }

    public static void initialize() {
        MinecraftForge.EVENT_BUS.register(instance);
    }

    private static final HashMap<String, ItemStack> preferredStacks = new HashMap<String, ItemStack>();
    static
    {
        preferredStacks.put("oreCopper", BlockOre.oreCopper);
        preferredStacks.put("oreLead", BlockOre.oreLead);
        preferredStacks.put("oreSilver", BlockOre.oreSilver);
        preferredStacks.put("oreTin", BlockOre.oreTin);
        preferredStacks.put("oreNickel", BlockOre.oreNickel);
        //preferredStacks.put("oreMithril", BlockOre.oreMithril);
        //preferredStacks.put("orePlatinum", BlockOre.orePlatinum);
    };

    @SubscribeEvent(
            priority = EventPriority.HIGHEST
    )
    public void handleEntityItemPickupEvent(EntityItemPickupEvent var1) {
        ItemStack var2 = var1.item.getEntityItem();
        if (var2 != null && LexiconManager.validOre(var2)) {
            NBTTagCompound var3 = var1.entityPlayer.getEntityData();
            if (var1.entityPlayer.worldObj.getTotalWorldTime() - var3.getLong("da3dsoul.implicationUpdate") <= 20L) {
                ItemStack var4 = getPreferredStack(var2);
                if (var4 == null) return;
                var1.setResult(Result.DENY);
                HandleAddItemToInventory(var1, var2, var4);
            }
        }
    }

    public static ItemStack getPreferredStack(ItemStack var1) {
        String var4 = OreDictionaryArbiter.getOreName(var1);
        if (preferredStacks.containsKey(var4)) {
            ItemStack var5 = preferredStacks.get(var4);
            return ItemHelper.cloneStack(var5, var1.stackSize);
        }
        return null;
    }
}