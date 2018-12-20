package da3dsoul.AutoCompressor;

import abo.ABO;
import cofh.core.util.oredict.OreDictionaryArbiter;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import java.util.ArrayList;

public class CompressionEventHandler {
    public static CompressionEventHandler instance = new CompressionEventHandler();

    private final boolean DEBUG = false;


    public CompressionEventHandler() {
    }

    public static void initialize() {
        MinecraftForge.EVENT_BUS.register(instance);
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
                for (ICompressionStack type : CompressionStacks.BaseStacks) {
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

    private ItemStack[] processAddItem(ICompressionStack type, ItemStack stack, ItemStack[] previousInventory)
    {
        ItemStack[] inventory;
        if (previousInventory != null)
        {
            inventory = cloneInventory(previousInventory);
        } else {
            return null;
        }

        if (type.isItemStackOfType(stack))
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

    /*
    - Calculate how much we have total
    - Calculate how many free slots we have after removing them
    - Calculate how many we can fit of the highest tiers
    - Determine if we can fit the higher stacks into the lower tiers

     */

    private boolean recursivelyCompact(ICompressionStack type, ItemStack[] inventory, ItemStack stack)
    {
        if (type == null || type.getNextTier() == null) return true;
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

            ICompressionStack nextTier = type.getNextTier();
            ItemStack newStack = nextTier.getIdentityItemStack();
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
            return recursivelyCompact(type.getNextTier(), inventory, newStack);
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

    private int getTotalNumberOfItemInInventory(ICompressionStack type, ItemStack[] inventory)
    {
        int count = 0;

        for (ItemStack itemStack : inventory) {
            if (itemStack == null) continue;
            if (!type.isItemStackOfType(itemStack)) continue;

            count += itemStack.stackSize;
        }
        return count;
    }

    private int getSpaceForItemInInventory(ICompressionStack type, ItemStack[] inventory)
    {
        int count = 0;

        for (ItemStack itemStack : inventory) {
            if (itemStack == null) {
                count += type.getIdentityItemStack().getMaxStackSize();
                continue;
            }
            if (!type.isItemStackOfType(itemStack)) continue;

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

    private void removeItem(ItemStack[] inventory, ICompressionStack type, int number)
    {
        int count = number;
        for (int i = 0; i < inventory.length; i++) {
            ItemStack itemStack = inventory[i];
            if (itemStack == null) continue;
            if (!type.isItemStackOfType(itemStack)) continue;
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