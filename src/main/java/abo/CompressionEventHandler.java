package abo;

import cofh.core.util.oredict.OreDictionaryArbiter;
import com.rwtema.extrautils.ExtraUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.LogManager;

import static abo.InventoryListeningHelper.HandleAddItemToInventory;

public class CompressionEventHandler {
    public static CompressionEventHandler instance = new CompressionEventHandler();

    private final boolean DEBUG = false;


    public CompressionEventHandler() {
    }

    public static void initialize() {
        MinecraftForge.EVENT_BUS.register(instance);
    }
    private enum SearchType{
        COBBLESTONE,
        COMP_COBBLE,
        DOUBLE_COBBLE,
        TRIPLE_COBBLE,
        DIRT,
        COMP_DIRT,
        SAND,
        COMP_SAND,
        GRAVEL,
        COMP_GRAVEL,
        REDSTONE,
        LAPIS
    }

    private static HashMap<SearchType, ItemStack> typeMap = new HashMap<SearchType, ItemStack>();
    private static HashMap<SearchType, SearchType> nextTypeMap = new HashMap<SearchType, SearchType>();
    private static ItemStack CompressedCobble = new ItemStack(ExtraUtils.cobblestoneCompr, 1, 0);
    private static ItemStack DoubleCompressedCobble = new ItemStack(ExtraUtils.cobblestoneCompr, 1, 1);
    private static ItemStack TripleCompressedCobble = new ItemStack(ExtraUtils.cobblestoneCompr, 1, 2);
    private static ItemStack QuadrupleCompressedCobble = new ItemStack(ExtraUtils.cobblestoneCompr, 1, 3);

    private static ItemStack CompressedDirt = new ItemStack(ExtraUtils.cobblestoneCompr, 1, 8);
    private static ItemStack DoubleCompressedDirt = new ItemStack(ExtraUtils.cobblestoneCompr, 1, 9);

    private static ItemStack CompressedSand = new ItemStack(ExtraUtils.cobblestoneCompr, 1, 14);
    private static ItemStack DoubleCompressedSand = new ItemStack(ExtraUtils.cobblestoneCompr, 1, 15);

    private static ItemStack CompressedGravel = new ItemStack(ExtraUtils.cobblestoneCompr, 1, 12);
    private static ItemStack DoubleCompressedGravel = new ItemStack(ExtraUtils.cobblestoneCompr, 1, 13);

    private static ItemStack RedstoneDust = new ItemStack(Items.redstone, 1, 0);
    private static ItemStack RedstoneBlock = new ItemStack(Blocks.redstone_block, 1, 0);
    private static ItemStack LapisLazuli = new ItemStack(Items.dye, 1, 4);
    private static ItemStack LapisBlock = new ItemStack(Blocks.lapis_block, 1, 0);

    static {
        // 0 cobble, 8 dirt, 12 gravel, 14 sand
        typeMap.put(SearchType.COBBLESTONE, CompressedCobble);
        typeMap.put(SearchType.COMP_COBBLE, DoubleCompressedCobble);
        typeMap.put(SearchType.DOUBLE_COBBLE, TripleCompressedCobble);
        typeMap.put(SearchType.TRIPLE_COBBLE, QuadrupleCompressedCobble);
        typeMap.put(SearchType.DIRT, CompressedDirt);
        typeMap.put(SearchType.COMP_DIRT, DoubleCompressedDirt);
        typeMap.put(SearchType.SAND, CompressedSand);
        typeMap.put(SearchType.COMP_SAND, DoubleCompressedSand);
        typeMap.put(SearchType.GRAVEL, CompressedGravel);
        typeMap.put(SearchType.COMP_GRAVEL, DoubleCompressedGravel);
        typeMap.put(SearchType.REDSTONE, RedstoneBlock);
        typeMap.put(SearchType.LAPIS, LapisBlock);

        addNextType(SearchType.COBBLESTONE, SearchType.COMP_COBBLE);
        addNextType(SearchType.COMP_COBBLE, SearchType.DOUBLE_COBBLE);
        addNextType(SearchType.DOUBLE_COBBLE, SearchType.TRIPLE_COBBLE);
        addNextType(SearchType.DIRT, SearchType.COMP_DIRT);
        addNextType(SearchType.SAND, SearchType.COMP_SAND);
        addNextType(SearchType.GRAVEL, SearchType.COMP_GRAVEL);
    }

    private static void addNextType(SearchType type, SearchType nextType)
    {
        nextTypeMap.put(type, nextType);
    }

    @SubscribeEvent(
            priority = EventPriority.HIGHEST
    )
    public void handleEntityItemPickupEvent(EntityItemPickupEvent var1) {
        ItemStack var2 = var1.item.getEntityItem();

        if (var2 != null) {
            NBTTagCompound var3 = var1.entityPlayer.getEntityData();
            if (var1.entityPlayer.worldObj.getTotalWorldTime() - var3.getLong("da3dsoul.compressionUpdate") <= 20L) {
                boolean changed = false;
                ItemStack[] inventory = var1.entityPlayer.inventory.mainInventory;
                for (SearchType type : SearchType.values()) {
                    ItemStack[] temp = processAddItem(type, var2, inventory);
                    if (temp != null) {
                        log("type: '" + type.toString() + "' stack: '" + var2.getDisplayName() + "' - Using new inventory");
                        log("type: '" + type.toString() + "' stack: '" + var2.getDisplayName() + "' - incoming stack has a size of " + var2.stackSize);
                        changed = true;
                        inventory = temp;
                    }
                }
                if (changed) {
                    updateInventory(inventory, var1, var2);
                }
            }
        }
    }

    private ItemStack[] processAddItem(SearchType type, ItemStack stack, ItemStack[] previousInventory)
    {
        ItemStack[] inventory;
        if (previousInventory != null)
        {
            inventory = cloneInventory(previousInventory);
        } else {
            return null;
        }

        if (isItemOfType(stack, type))
        {
            log("type: '" + type.toString() + "' stack: '" + stack.getDisplayName() + "' - Type matched");
            if (hasRoomForItemStack(inventory, stack)) {
                log("type: '" + type.toString() + "' stack: '" + stack.getDisplayName() + "' - There is room. Adding");
                // it has room for the whole stack, so add it and crush
                addItemToInventory(inventory, stack);

                // backup the inventory just in case there's no room
                ItemStack[] backupInventory = cloneInventory(inventory);

                if (!recursivelyCompact(type, inventory, stack)) {
                    log("type: '" + type.toString() + "' stack: '" + stack.getDisplayName() + "' - No room to compress. Reverting");
                    return backupInventory;
                }
                return inventory;
            } else {
                log("type: '" + type.toString() + "' stack: '" + stack.getDisplayName() + "' - There is not enough space for whole stack");
                // there's not room for the whole stack, so add what we can, crush it, and try again
                int left = getSpaceForItemInInventory(type, inventory);
                log("type: '" + type.toString() + "' stack: '" + stack.getDisplayName() + "' - There are " + left + " spaces available");
                log("type: '" + type.toString() + "' stack: '" + stack.getDisplayName() + "' - There are " + stack.stackSize + " items to fill it");
                left = Math.min(left, stack.stackSize);
                if (left <= 0) return null;

                ItemStack supplement = stack.copy();
                supplement.stackSize = left;
                addItemToInventory(inventory, supplement);
                stack.stackSize -= left;

                // backup the inventory just in case there's no room
                ItemStack[] backupInventory = cloneInventory(inventory);

                log("type: '" + type.toString() + "' stack: '" + stack.getDisplayName() + "' - Compact Recursion 1 - Starting");
                if (!recursivelyCompact(type, inventory, stack)) {
                    log("type: '" + type.toString() + "' stack: '" + stack.getDisplayName() + "' - Compact Recursion 1 - There is no space to compact. Reverting");
                    log("type: '" + type.toString() + "' stack: '" + stack.getDisplayName() + "' - Compact Recursion 1 - Finished");
                    return backupInventory;
                } else {
                    log("type: '" + type.toString() + "' stack: '" + stack.getDisplayName() + "' - Compact Recursion 1 - Finished");
                    if (stack.stackSize > 0 && hasRoomForItemStack(inventory, stack)) {
                        log("type: '" + type.toString() + "' stack: '" + stack.getDisplayName() + "' - Compact Recursion 1 - Compacted. There is now room for more");
                        log("type: '" + type.toString() + "' stack: '" + stack.getDisplayName() + "' - Compact Recursion 1 - Adding " + stack.stackSize);
                        addItemToInventory(inventory, stack);

                        backupInventory = cloneInventory(inventory);

                        if (!recursivelyCompact(type, inventory, stack)) {
                            log("type: '" + type.toString() + "' stack: '" + stack.getDisplayName() + "' - Compact Recursion 2 - There is no space to compact. Reverting");
                            return backupInventory;
                        }
                        log("type: '" + type.toString() + "' stack: '" + stack.getDisplayName() + "' - Compact Recursion 2 - Finished Compacting. Continuing");
                    } else {
                        if (stack.stackSize > 0)
                            log("type: '" + type.toString() + "' stack: '" + stack.getDisplayName() + "' - Compact Recursion 2 - Finished Compacting. There are " + stack.stackSize + " blocks/items that couldn't fit");
                    }
                    return inventory;
                }
            }
        }
        return null;
    }

    private boolean recurse(SearchType thisType, ItemStack[] inventory, ItemStack stack)
    {
        for (SearchType type : SearchType.values()) {
            if (type.ordinal() <= thisType.ordinal()) continue;
            if(!recursivelyCompact(type, inventory, stack)) return false;
        }
        return true;
    }

    private boolean recursivelyCompact(SearchType type, ItemStack[] inventory, ItemStack stack)
    {
        int total = getTotalNumberOfItemInInventory(type, inventory);
        // only compress it if it'll save space
        if (total >= stack.getMaxStackSize()) {
            log("type: '" + type.toString() + "' stack: '" + stack.getDisplayName() + "' - There is " + total + " blocks/items");
            int extraPasses = (int) Math.floor(total / 9D);
            log("type: '" + type.toString() + "' stack: '" + stack.getDisplayName() + "' - Removing " + extraPasses * 9 + " blocks/items");
            if (extraPasses > 0)
                removeItem(inventory, type, extraPasses * 9);

            int left = total % 9;
            // remove what's left, we'll add it back later, this helps to keep it organized
            removeItem(inventory, type, left);

            // there will always be room for this, so no need to check
            ItemStack oldStack = stack.copy();
            oldStack.stackSize = left;
            addItemToInventory(inventory, oldStack);

            ItemStack newStack = typeMap.get(type).copy();
            newStack.stackSize = extraPasses;
            int count = 0;
            while (newStack.stackSize >= newStack.getMaxStackSize()) {
                ItemStack otherStack = newStack.copy();
                otherStack.stackSize = newStack.getMaxStackSize();
                newStack.stackSize -= newStack.getMaxStackSize();
                if (!hasRoomForItemStack(inventory, otherStack)) {
                    return false;
                }
                addItemToInventory(inventory, otherStack);
                count++;
            }

            if (newStack.stackSize > 0) {
                if (!hasRoomForItemStack(inventory, newStack)) return false;
                addItemToInventory(inventory, newStack);
            }
            log("type: '" + type.toString() + "' stack: '" + stack.getDisplayName() + "' - There is " + left + " blocks/items left");
            log("type: '" + type.toString() + "' stack: '" + stack.getDisplayName() + "' - Adding " + count + " stacks of '" + newStack.getDisplayName() + "' and a " + newStack.stackSize + " stack");
            if (!recurse(type, inventory, newStack)) return false;
        }
        return true;
    }

    private void updateInventory(ItemStack[] inventory, EntityItemPickupEvent var1, ItemStack stack)
    {
        for (int i = 0; i < inventory.length; i++) {
            ItemStack temp = var1.entityPlayer.inventory.getStackInSlot(i);
            if (!ItemStack.areItemStacksEqual(temp, inventory[i]))
                var1.entityPlayer.inventory.setInventorySlotContents(i, inventory[i]);
        }

        var1.setResult(Result.DENY);
        var1.item.setEntityItemStack(stack);

        FMLCommonHandler.instance().firePlayerItemPickupEvent(var1.entityPlayer, var1.item);

        if (stack.stackSize <= 0)
        {
            var1.item.setDead();
        }

    }

    private int getTotalNumberOfItemInInventory(SearchType type, ItemStack[] inventory)
    {
        int count = 0;

        for (ItemStack itemStack : inventory) {
            if (itemStack == null) continue;
            if (!isItemOfType(itemStack, type)) continue;

            count += itemStack.stackSize;
        }
        return count;
    }

    private int getSpaceForItemInInventory(SearchType type, ItemStack[] inventory)
    {
        int count = 0;

        for (ItemStack itemStack : inventory) {
            if (itemStack == null) {
                count += itemStack.getMaxStackSize();
                continue;
            }
            if (!isItemOfType(itemStack, type)) continue;

            count += itemStack.getMaxStackSize() - itemStack.stackSize;
        }
        return count;
    }

    private int getSpaceForItemInInventory(ItemStack stack, ItemStack[] inventory)
    {
        int count = 0;

        for (ItemStack itemStack : inventory) {
            if (itemStack == null) {
                count += stack.getMaxStackSize();
                continue;
            }
            if (!itemStack.isItemEqual(stack)) continue;

            count += stack.getMaxStackSize() - itemStack.stackSize;
        }
        return count;
    }

    private void removeItem(ItemStack[] inventory, SearchType type, int number)
    {
        int count = number;
        for (int i = 0; i < inventory.length; i++) {
            ItemStack itemStack = inventory[i];
            if (itemStack == null) continue;
            if (!isItemOfType(itemStack, type)) continue;
            if (itemStack.stackSize > count) {
                itemStack.stackSize -= count;
                return;
            }

            count -= itemStack.stackSize;
            inventory[i] = null;

            if (count == 0) return;
        }
    }

    private boolean hasRoomForItemStack(ItemStack[] inventory, ItemStack stack)
    {
        int space = getSpaceForItemInInventory(stack, inventory);

        return space >= stack.stackSize;
    }
    
    // this is only called when there is space, so we don't need to check it
    private void addItemToInventory(ItemStack[] inventory, ItemStack stack)
    {
        int i;
        do
        {
            i = stack.stackSize;
            stack.stackSize = storePartialItemStack(inventory, stack);
        }
        while (stack.stackSize > 0 && stack.stackSize < i);
    }
    
    private int storePartialItemStack(ItemStack[] inventory, ItemStack stack)
    {
        Item item = stack.getItem();
        int i = stack.stackSize;
        int j = storeItemStack(inventory, stack);

        if (j < 0) j = getFirstEmptyStack(inventory);

        if (j < 0) return i;

        if (inventory[j] == null) inventory[j] = new ItemStack(item, 0, stack.getItemDamage());

        int k = i;

        if (i > inventory[j].getMaxStackSize() - inventory[j].stackSize)
            k = inventory[j].getMaxStackSize() - inventory[j].stackSize;

        if (k > 64 - inventory[j].stackSize) k = 64 - inventory[j].stackSize;

        if (k == 0) return i;

        i -= k;
        inventory[j].stackSize += k;
        return i;
    }

    private int storeItemStack(ItemStack[] inventory, ItemStack stack)
    {
        for (int i = 0; i < inventory.length; ++i)
            if (inventory[i] != null && inventory[i].isItemEqual(stack) && inventory[i].stackSize < inventory[i].getMaxStackSize())
                return i;

        return -1;
    }

    private int getFirstEmptyStack(ItemStack[] inventory)
    {
        for (int i = 0; i < inventory.length; ++i)
            if (inventory[i] == null || inventory[i].stackSize == 0) return i;

        return -1;
    }

    private boolean isItemOfType(ItemStack stack, SearchType type)
    {
        switch (type)
        {
            case COBBLESTONE: return isCobble(stack);
            case COMP_COBBLE: return stack.isItemEqual(CompressedCobble);
            case DOUBLE_COBBLE: return stack.isItemEqual(DoubleCompressedCobble);
            case TRIPLE_COBBLE: return stack.isItemEqual(TripleCompressedCobble);
            case DIRT: return isBlock(stack, Blocks.dirt);
            case COMP_DIRT: return stack.isItemEqual(CompressedDirt);
            case SAND: return isBlock(stack, Blocks.sand);
            case COMP_SAND: return stack.isItemEqual(CompressedSand);
            case GRAVEL: return isBlock(stack, Blocks.gravel);
            case COMP_GRAVEL: return stack.isItemEqual(CompressedGravel);
            case REDSTONE: return stack.isItemEqual(RedstoneDust);
            case LAPIS: return stack.isItemEqual(LapisLazuli);
        }
        return false;
    }

    private boolean isCobble(ItemStack itemStack)
    {
        ArrayList<String> ores = OreDictionaryArbiter.getAllOreNames(itemStack);
        return ores != null && ores.contains("cobblestone");
    }

    private boolean isBlock(ItemStack itemStack, Block block)
    {
        Item item = itemStack.getItem();
        if (!(item instanceof ItemBlock)) return false;
        return ((ItemBlock)item).field_150939_a == block;
    }

    private ItemStack[] cloneInventory(ItemStack[] inventory)
    {
        if (inventory == null) return null;
        ItemStack[] newInventory = new ItemStack[inventory.length];
        for (int i = 0; i < inventory.length; i++)
        {
            if (inventory[i] == null) {
                newInventory[i] = null;
                continue;
            }
            newInventory[i] = inventory[i].copy();
        }
        return newInventory;
    }

    private void log(String text)
    {
        if (!DEBUG) return;
        ABO.aboLog.error(text);
    }
}