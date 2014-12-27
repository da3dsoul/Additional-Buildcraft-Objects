package abo.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import abo.pipes.items.PipeItemsEnderExtraction;
import abo.pipes.items.gui.ContainerPipeItemsEnderExtraction;
import abo.pipes.items.gui.GuiPipeItemsEnderExtraction;
import abo.pipes.power.gui.ContainerPipePowerDiamond;
import abo.pipes.power.gui.GuiPipePowerDiamond;

import buildcraft.transport.TileGenericPipe;
import cpw.mods.fml.common.network.IGuiHandler;

public class ABOGuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (!world.blockExists(x, y, z)) return null;

		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TileGenericPipe)) return null;

		TileGenericPipe pipe = (TileGenericPipe) tile;

		if (pipe.pipe == null) return null;

		switch (ID) {
			case ABOGuiIds.PIPE_DIAMOND_POWER:
				return new ContainerPipePowerDiamond(player.inventory, pipe);

			case ABOGuiIds.PIPE_ENDER_EXTRACTION:
				return new ContainerPipeItemsEnderExtraction(player.inventory, (PipeItemsEnderExtraction) pipe.pipe);

			default:
				return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (!world.blockExists(x, y, z)) return null;

		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TileGenericPipe)) return null;

		TileGenericPipe pipe = (TileGenericPipe) tile;

		if (pipe.pipe == null) return null;

		switch (ID) {
			case ABOGuiIds.PIPE_DIAMOND_POWER:
				return new GuiPipePowerDiamond(player.inventory, pipe);

			case ABOGuiIds.PIPE_ENDER_EXTRACTION:
				return new GuiPipeItemsEnderExtraction(player.inventory, (PipeItemsEnderExtraction) pipe.pipe);

			default:
				return null;
		}
	}
}
