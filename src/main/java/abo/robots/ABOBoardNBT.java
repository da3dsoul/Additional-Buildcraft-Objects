package abo.robots;

import buildcraft.api.boards.RedstoneBoardRobot;
import buildcraft.core.DefaultProps;
import buildcraft.core.lib.utils.StringUtils;
import buildcraft.robotics.boards.BCBoardNBT;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public class ABOBoardNBT  extends BCBoardNBT {
    private final ResourceLocation texture;
    private final String name;

    public ABOBoardNBT(String id, String name, Class<? extends RedstoneBoardRobot> board, String boardType) {
        super(id, name, board, boardType);
        this.name = name;
        this.texture = new ResourceLocation("additional-buildcraft-objects:textures/robots/" + "robot_" + name + ".png");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        list.add(EnumChatFormatting.BOLD + StringUtils.localize("additional-buildcraft-objects.boardRobot." + name));
        list.add(StringUtils.localize("additional-buildcraft-objects.boardRobot." + name + ".desc"));
    }

    public ResourceLocation getRobotTexture() {
        return this.texture;
    }
}
