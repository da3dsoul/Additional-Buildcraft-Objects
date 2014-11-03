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

import abo.energy.TileWindmill;
import abo.network.ABOPacket;

import net.minecraft.item.Item;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * @author Flow86
 * 
 */
public class ABOProxy {

	@SidedProxy(clientSide = "abo.proxy.ABOProxyClient", serverSide = "abo.proxy.ABOProxy")
	public static ABOProxy	proxy;

	public void registerPipe(Item itemID) {}

	public void sendToServer(ABOPacket packet) {}

	public void registerBlockRenderers() {}

	public void registerTileEntities() {
		GameRegistry.registerTileEntity(TileWindmill.class, "net.minecraft.src.abo.energy.TileWindmill");
		// GameRegistry.registerTileEntity(TileSolarPanel.class,
		// "net.minecraft.src.abo.energy.TileSolarPanel");
	}
	
	public void registerEntities()
	{
		
	}
}
