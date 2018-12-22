package da3dsoul.scaryGen.ModInteraction.ExtraUtilities.AutoCompressor;

import abo.ABO;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

import java.util.ArrayList;

public class CompressionEventHandler {
    public static CompressionEventHandler instance = new CompressionEventHandler();

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
                        log("type: '" + type.getClass().getSimpleName() + "' stack: '" + var2.getDisplayName() + "' - Using new inventory");
                        log("type: '" + type.getClass().getSimpleName() + "' stack: '" + var2.getDisplayName() + "' - incoming stack has a size of " + var2.stackSize);
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

        if (type.isItemStackOfTypeSet(stack))
        {
            log("type: '" + type.getClass().getSimpleName() + "' stack: '" + stack.getDisplayName() + "' - Type matched");
            if (hasRoomForItemStack(inventory, stack)) {
                log("type: '" + type.getClass().getSimpleName() + "' stack: '" + stack.getDisplayName() + "' - There is room. Adding");
                // it has room for the whole stack, so add it and crush
                // always make backups
                ItemStack[] backupInventory = cloneInventory(inventory);

                if (compact(type, inventory, stack)) {
                    stack.stackSize = 0;
                    return inventory;
                }

                log("type: '" + type.getClass().getSimpleName() + "' stack: '" + stack.getDisplayName() + "' - No room to compress. Reverting");
                addItemToInventory(backupInventory, stack);
                return backupInventory;
            } else {
                log("type: '" + type.getClass().getSimpleName() + "' stack: '" + stack.getDisplayName() + "' - There is not enough space for whole stack");
                // there's not room for the whole stack, so add what we can, crush it, and try again
                int left = getSpaceForItemInInventory(type, inventory);
                log("type: '" + type.getClass().getSimpleName() + "' stack: '" + stack.getDisplayName() + "' - There are " + left + " spaces available");
                log("type: '" + type.getClass().getSimpleName() + "' stack: '" + stack.getDisplayName() + "' - There are " + stack.stackSize + " items to fill it");
                left = Math.min(left, stack.stackSize);
                if (left <= 0) return null;

                ItemStack supplement = stack.copy();
                supplement.stackSize = left;
                stack.stackSize -= left;

                // backup the inventory just in case there's no room
                ItemStack[] backupInventory = cloneInventory(inventory);

                log("type: '" + type.getClass().getSimpleName() + "' stack: '" + stack.getDisplayName() + "' - Compact Recursion 1 - Starting");
                if (compact(type, inventory, stack)) {
                    if (!hasRoomForItemStack(inventory, stack) && stack.stackSize > 0)
                        log("type: '" + type.getClass().getSimpleName() + "' stack: '" + stack.getDisplayName() + "' - Compact Recursion 2 - Finished Compacting. There are " + stack.stackSize + " blocks/items that couldn't fit");
                    return inventory;
                } else {
                    log("type: '" + type.getClass().getSimpleName() + "' stack: '" + stack.getDisplayName() + "' - Compact Recursion 1 - There is no space to compact. Reverting");
                    ItemStack tempStack = supplement.copy();
                    tempStack.stackSize = 0;
                    inventory = cloneInventory(backupInventory);
                    if (compact(type, inventory, supplement)) return inventory;
                    addItemToInventory(backupInventory, supplement);
                    return backupInventory;
                }
            }
        }
        return null;
    }

    /*
    - Calculate how much we have total of the base item
    - Calculate how many we can fit of the highest tiers
    - Determine if we can fit the higher stacks into the lower tiers
    - Calculate how many free slots we have after removing them
    - Make sure they fit
    - Actually add them
     */

    private boolean compact(ICompressionStack type, ItemStack[] inventory, ItemStack stack)
    {
        if (type == null || type.getNextTier() == null) return true;
        int total = getTotalNumberOfTypeInInventory(type, inventory);

        int newTotal = total + type.getTotalStackSizeOfType(stack);
        // only compress it if it'll save space
        if (newTotal >= stack.getMaxStackSize()) {
            log("type: '" + type.getClass().getSimpleName() + "' stack: '" + stack.getDisplayName() + "' - There is " + total + " blocks/items");
            log("type: '" + type.getClass().getSimpleName() + "' stack: '" + stack.getDisplayName() + "' - There will be " + newTotal + " blocks/items");

            ArrayList<ItemStack> distribution = new ArrayList<ItemStack>();

            // Build the distribution tree

            // Determine how many of the highest tier we can make
            ICompressionStack currentTier = type.getHighestTier();
            log("The highest tier of " + type.getClass().getSimpleName() + " is " + currentTier.getClass().getSimpleName());
            newTotal = addFullStacksToDistribution(currentTier, distribution, newTotal);
            log("newTotal is " + newTotal + " after processing the highest tier");
            // This should run at least once, since there are at least two tiers
            while (currentTier != null) {
                int numberToFormTier = currentTier.stackSizeOfBaseToForm();
                if (newTotal < numberToFormTier) {
                    log("Tier: " + currentTier.getClass().getSimpleName() + " can't be made, skipping");
                    currentTier = currentTier.getPreviousTier();
                    continue;
                }
                log("Can form " + (int)Math.floor((double)newTotal / numberToFormTier) + " " + currentTier.getClass().getSimpleName());
                if (canTierMergeDown(currentTier, newTotal)) {
                    log("Tier: " + currentTier.getClass().getSimpleName() + " can merge with a smaller tier");
                    // We will use this to add back the leftovers from the full stacks
                    // since they can't all fit into a smaller tier
                    ICompressionStack previousTier = currentTier.getPreviousTier();
                    newTotal = addPartialStacksToDistribution(previousTier, distribution, newTotal);
                    log("newTotal is " + newTotal + " after adding the partial stacks of " + previousTier.getClass().getSimpleName());
                    currentTier = currentTier.getPreviousTier();
                    continue;
                }
                // We skip the full stack stuff here, since if we have enough to take multiple stacks
                // of smaller tiers, we'll want to compress it

                // we might have a new newTotal from the above, we'll try to add some of this tier
                // but there's a chance we only have the smaller tiers left
                newTotal = addPartialStacksToDistribution(currentTier, distribution, newTotal);
                log("newTotal is " + newTotal + " after adding the partial stacks of " + currentTier.getClass().getSimpleName());

                currentTier = currentTier.getPreviousTier();
            }

            // We should have a full optimized distribution tree now
            for (ItemStack logStack : distribution) {
                if (logStack.stackSize == logStack.getMaxStackSize())
                    log("type: '" + type.getClass().getSimpleName() + "' stack: '" + stack.getDisplayName() + "' - Adding full stack");
                else
                    log("type: '" + type.getClass().getSimpleName() + "' stack: '" + stack.getDisplayName() + "' - Adding " + logStack.stackSize + " blocks/items");
            }

            // remove all items of the same type
            removeAllItemsOfTypeSet(inventory, type);

            // make sure it fits
            int emptySlots = getNumberOfEmptySlotsInInventory(inventory);
            if (emptySlots < distribution.size()) return false;

            // actually add the stuff
            for (ItemStack stackToAdd : distribution) {
                addItemToInventory(inventory, stackToAdd);
            }

            // Done!
        } else {
            addItemToInventory(inventory, stack);
        }
        return true;
    }

    private int addFullStacksToDistribution(ICompressionStack type, ArrayList<ItemStack> distribution, int newTotal) {
        int numberToFormTier = type.stackSizeOfBaseToForm();
        if (newTotal >= numberToFormTier) {
            // The total number of highest tier items we can make
            int numberOfItems = (int) Math.floor((double) newTotal / numberToFormTier);
            // how many we will have left after adding these
            newTotal -= numberOfItems * numberToFormTier;

            // determine if we have more than one stack of it
            int maxStack = type.getIdentityItemStack().getMaxStackSize();
            if (numberOfItems >= maxStack) {
                while (numberOfItems >= maxStack) {
                    ItemStack stack1 = type.getIdentityItemStack();
                    stack1.stackSize = maxStack;
                    // we can only merge this into the smaller tiers if it's not a full stack, so add it now
                    distribution.add(stack1);
                    numberOfItems -= maxStack;
                }
            }
            // add back the rest to see if they can fit into a smaller tier
            newTotal += numberOfItems * numberToFormTier;
        }
        log("Can't form highest tier. Skipping.");
        return newTotal;
    }

    private boolean canTierMergeDown(ICompressionStack type, int newTotal) {
        ICompressionStack prev = type.getPreviousTier();
        if (prev == null) return false;
        int numberToFormTier = prev.stackSizeOfBaseToForm();
        // It will need to be greater to take multiple stacks
        if (newTotal > numberToFormTier) {
            // The total number of highest tier items we can make
            int numberOfItems = (int) Math.floor((double) newTotal / numberToFormTier);

            // determine if we can use only one stack
            int maxStack = prev.getIdentityItemStack().getMaxStackSize();
            return numberOfItems <= maxStack;
        }
        return false;
    }

    private int addPartialStacksToDistribution(ICompressionStack type, ArrayList<ItemStack> distribution, int newTotal) {
        int numberToFormTier = type.stackSizeOfBaseToForm();

        // The total number of highest tier items we can make
        int numberOfItems = (int) Math.floor((double) newTotal / numberToFormTier);

        // how many we will have left after adding these
        newTotal -= numberOfItems * numberToFormTier;
        log("Can form " + (int)Math.floor((double)newTotal / numberToFormTier) + " " + type.getClass().getSimpleName());
        // We don't need to check the rest again, since we add the full stacks first
        ItemStack stack1 = type.getIdentityItemStack();
        stack1.stackSize = numberOfItems;
        distribution.add(stack1);
        return newTotal;
    }

    private int getTotalNumberOfTypeInInventory(ICompressionStack type, ItemStack[] inventory)
    {
        int count = 0;

        for (int i = 0; i < inventory.length; i++) {
            ItemStack itemStack = inventory[i];
            int size = type.getTotalStackSizeOfType(itemStack);
            count += size;

            if (type == CompressionStacks.Cobblestone) {
                ICompressionStack stackType = CompressionStacks.getTypeFromSet(CompressionStacks.Cobblestone, itemStack);
                if (stackType == null) continue;
                log("Slot " + i + " has " + size + " " + stackType);
                log("The count is now " + count + " cobblestone");
            }
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

    private int getNumberOfEmptySlotsInInventory(ItemStack[] inventory)
    {
        int count = 0;
        for (ItemStack itemStack : inventory) {
            if (itemStack != null) continue;
            count++;
        }
        return count;
    }

    private void removeAllItemsOfTypeSet(ItemStack[] inventory, ICompressionStack type)
    {
        for (int i = 0; i < inventory.length; i++) {
            ItemStack itemStack = inventory[i];
            if (itemStack == null) continue;
            if (!type.isItemStackOfTypeSet(itemStack)) continue;

            inventory[i] = null;
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
        int stackSize = stack.stackSize;
        int index = storeItemStack(inventory, stack);

        if (index < 0) index = getFirstEmptyStack(inventory);

        if (index < 0) return stackSize;

        if (inventory[index] == null) inventory[index] = new ItemStack(item, 0, stack.getItemDamage());

        int tempStackSize = stackSize;

        if (stackSize > inventory[index].getMaxStackSize() - inventory[index].stackSize)
            tempStackSize = inventory[index].getMaxStackSize() - inventory[index].stackSize;

        if (tempStackSize > 64 - inventory[index].stackSize) tempStackSize = 64 - inventory[index].stackSize;

        if (tempStackSize == 0) return stackSize;

        stackSize -= tempStackSize;
        inventory[index].stackSize += tempStackSize;
        return stackSize;
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
        boolean DEBUG = true;
        if (!DEBUG) return;
        ABO.aboLog.error(text);
    }
}