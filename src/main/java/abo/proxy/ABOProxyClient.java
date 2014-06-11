/** 
 * Copyright (C) 2011-2013 Flow86
 * 
 * AdditionalBuildcraftObjects is open-source.
 *
 * It is distributed under the terms of my Open Source License. 
 * It grants rights to read, modify, compile or run the code. 
 * It does *NOT* grant the right to redistribute this software or its 
 * modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package abo.proxy;

import abo.ABO;
import abo.energy.TileWindmill;
import abo.network.ABOPacket;
import abo.render.RenderWindmill;

import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraftforge.client.MinecraftForgeClient;
import buildcraft.BuildCraftEnergy;
import buildcraft.BuildCraftSilicon;
import buildcraft.builders.urbanism.RenderBoxProvider;
import buildcraft.builders.urbanism.TileUrbanist;
import buildcraft.core.network.PacketCoordinates;
import buildcraft.core.render.RenderingEntityBlocks;
import buildcraft.core.render.RenderingEntityBlocks.EntityRenderIndex;
import buildcraft.transport.TransportProxyClient;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * @author Flow86
 * 
 */
public class ABOProxyClient extends ABOProxy {
	@Override
	public void registerPipe(Item itemID) {
		super.registerPipe(itemID);

		MinecraftForgeClient.registerItemRenderer(itemID, TransportProxyClient.pipeItemRenderer);
	}

	@Override
	public void sendToServer(ABOPacket packet) {
		ABO.instance.sendToServer(packet);
	}
	
	public void registerTileEntities() {
		super.registerTileEntities();
		ClientRegistry.bindTileEntitySpecialRenderer(TileWindmill.class, new RenderWindmill());
		//ClientRegistry.bindTileEntitySpecialRenderer(TileSolarPanel.class, new RenderSolarPanel());

	}

	public void registerBlockRenderers() {
		RenderingEntityBlocks.blockByEntityRenders.put(new EntityRenderIndex(ABO.windmillBlock, 0), new RenderWindmill());
		//RenderingEntityBlocks.blockByEntityRenders.put(new EntityRenderIndex(ABO.solarpanelBlock), new RenderSolarPanel());
	}
}
