package abo.robots;

import buildcraft.api.boards.RedstoneBoardRobotNBT;
import buildcraft.api.core.BuildCraftAPI;
import buildcraft.api.robots.AIRobot;
import buildcraft.api.robots.EntityRobotBase;
import buildcraft.robotics.ai.AIRobotFetchAndEquipItemStack;
import buildcraft.robotics.boards.BCBoardNBT;
import buildcraft.robotics.boards.BoardRobotGenericBreakBlock;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BoardRobotStoner extends BoardRobotGenericBreakBlock {
    private static final int MAX_HARVEST_LEVEL = 3;
    private int harvestLevel = 0;

    public BoardRobotStoner(EntityRobotBase iRobot) {
        super(iRobot);
        this.detectHarvestLevel();
    }

    public void delegateAIEnded(AIRobot ai) {
        super.delegateAIEnded(ai);
        if (ai instanceof AIRobotFetchAndEquipItemStack && ai.success()) {
            this.detectHarvestLevel();
        }

    }

    private void detectHarvestLevel() {
        ItemStack stack = this.robot.getHeldItem();
        if (stack != null && stack.getItem() != null && stack.getItem().getToolClasses(stack).contains("pickaxe")) {
            this.harvestLevel = stack.getItem().getHarvestLevel(stack, "pickaxe");
        }

    }

    public RedstoneBoardRobotNBT getNBTHandler() {
        return (RedstoneBoardRobotNBT) BCBoardNBT.REGISTRY.get("stoner");
    }

    public boolean isExpectedTool(ItemStack stack) {
        return stack != null && stack.getItem().getToolClasses(stack).contains("pickaxe");
    }

    public boolean isExpectedBlock(World world, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        return block != null && !block.isAir(world, x, y , z) && !block.isReplaceable(world, x, y, z) && (block.isReplaceableOreGen(world, x, y, z, Blocks.stone) || block.isReplaceableOreGen(world, x, y, z, Blocks.netherrack));
    }
}
