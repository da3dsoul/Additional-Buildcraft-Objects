/**
 * Copyright (c) 2011-2014, SpaceToad and the BuildCraft Team
 * http://www.mod-buildcraft.com
 *
 * BuildCraft is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package abo.pipes.items.gui;

import buildcraft.core.lib.gui.GuiBuildCraft;
import buildcraft.core.lib.gui.buttons.GuiImageButton;
import buildcraft.core.lib.gui.buttons.IButtonClickEventListener;
import buildcraft.core.lib.gui.buttons.IButtonClickEventTrigger;
import buildcraft.core.lib.gui.tooltips.ToolTip;
import buildcraft.core.lib.gui.tooltips.ToolTipLine;
import buildcraft.core.lib.network.PacketGuiReturn;
import buildcraft.core.lib.utils.StringUtils;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import abo.pipes.items.PipeItemsEnderExtraction;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import buildcraft.core.DefaultProps;
import buildcraft.transport.pipes.PipeItemsEmerald.FilterMode;

public class GuiPipeItemsEnderExtraction extends GuiBuildCraft implements IButtonClickEventListener {

    private static final ResourceLocation TEXTURE = new ResourceLocation("buildcrafttransport:textures/gui/pipe_emerald.png");
    private static final ResourceLocation TEXTURE_BUTTON = new ResourceLocation("buildcrafttransport:textures/gui/pipe_emerald_button.png");
    private static final int WHITE_LIST_BUTTON_ID = 1;
    private static final int BLACK_LIST_BUTTON_ID = 2;
    private static final int ROUND_ROBIN_BUTTON_ID = 3;
    private GuiImageButton whiteListButton;
    private GuiImageButton blackListButton;
    private GuiImageButton roundRobinButton;
    private PipeItemsEnderExtraction pipe;

	public GuiPipeItemsEnderExtraction(IInventory playerInventory, PipeItemsEnderExtraction pipe) {
        super(new ContainerPipeItemsEnderExtraction(playerInventory, pipe), pipe.getFilters(), TEXTURE);
        this.pipe = pipe;
        this.xSize = 175;
        this.ySize = 161;
	}

	@SuppressWarnings("unchecked")
	@Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        this.whiteListButton = new GuiImageButton(1, this.guiLeft + 7, this.guiTop + 41, 18, TEXTURE_BUTTON, 19, 19);
        this.whiteListButton.registerListener(this);
        this.whiteListButton.setToolTip(new ToolTip(500, new ToolTipLine[]{new ToolTipLine(StatCollector.translateToLocal("tip.PipeItemsEmerald.whitelist"))}));
        this.buttonList.add(this.whiteListButton);
        this.blackListButton = new GuiImageButton(2, this.guiLeft + 7 + 18, this.guiTop + 41, 18, TEXTURE_BUTTON, 37, 19);
        this.blackListButton.registerListener(this);
        this.blackListButton.setToolTip(new ToolTip(500, new ToolTipLine[]{new ToolTipLine(StatCollector.translateToLocal("tip.PipeItemsEmerald.blacklist"))}));
        this.buttonList.add(this.blackListButton);
        this.roundRobinButton = new GuiImageButton(3, this.guiLeft + 7 + 36, this.guiTop + 41, 18, TEXTURE_BUTTON, 55, 19);
        this.roundRobinButton.registerListener(this);
        this.roundRobinButton.setToolTip(new ToolTip(500, new ToolTipLine[]{new ToolTipLine(StatCollector.translateToLocal("tip.PipeItemsEmerald.roundrobin"))}));
        this.buttonList.add(this.roundRobinButton);
        switch(pipe.getSettings().getFilterMode()) {
            case WHITE_LIST:
                this.whiteListButton.activate();
                break;
            case BLACK_LIST:
                this.blackListButton.activate();
                break;
            case ROUND_ROBIN:
                this.roundRobinButton.activate();
        }

    }

	@Override
    public void onGuiClosed() {
        if(this.pipe.getWorld().isRemote) {
            PacketGuiReturn pkt = new PacketGuiReturn(this.pipe.getContainer());
            pkt.sendPacket();
        }

        super.onGuiClosed();
    }

    @Override
    public void handleButtonClick(IButtonClickEventTrigger sender, int buttonId) {
        switch(buttonId) {
            case 1:
                this.whiteListButton.activate();
                this.blackListButton.deActivate();
                this.roundRobinButton.deActivate();
                this.pipe.getSettings().setFilterMode(FilterMode.WHITE_LIST);
                break;
            case 2:
                this.whiteListButton.deActivate();
                this.blackListButton.activate();
                this.roundRobinButton.deActivate();
                this.pipe.getSettings().setFilterMode(FilterMode.BLACK_LIST);
                break;
            case 3:
                this.whiteListButton.deActivate();
                this.blackListButton.deActivate();
                this.roundRobinButton.activate();
                this.pipe.getSettings().setFilterMode(FilterMode.ROUND_ROBIN);
        }

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        String title = StringUtils.localize("gui.pipes.emerald.title");
        this.fontRendererObj.drawString(title, (this.xSize - this.fontRendererObj.getStringWidth(title)) / 2, 6, 4210752);
        this.fontRendererObj.drawString(StringUtils.localize("gui.inventory"), 8, this.ySize - 93, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.renderEngine.bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
}
