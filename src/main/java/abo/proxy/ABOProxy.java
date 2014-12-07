package abo.proxy;

import abo.energy.TileWindmill;
import abo.network.ABOPacket;

import net.minecraft.item.Item;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.registry.GameRegistry;

public class ABOProxy {

	@SidedProxy(clientSide = "abo.proxy.ABOProxyClient", serverSide = "abo.proxy.ABOProxy")
	public static ABOProxy	proxy;

	public void registerPipe(Item itemID) {}

	public void sendToServer(ABOPacket packet) {}

	public void registerBlockRenderers() {}

	public void registerTileEntities() {
		GameRegistry.registerTileEntity(TileWindmill.class, "net.minecraft.src.abo.energy.TileWindmill");
	}
	
	public void registerEntities(){}
}
