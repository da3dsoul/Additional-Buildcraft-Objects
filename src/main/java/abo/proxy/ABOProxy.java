package abo.proxy;

import abo.energy.TileWaterwheel;
import abo.energy.TileWindmill;
import net.minecraft.item.Item;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.registry.GameRegistry;

public class ABOProxy {

	@SidedProxy(clientSide = "abo.proxy.ABOProxyClient", serverSide = "abo.proxy.ABOProxy")
	public static ABOProxy	proxy;

	public void registerPipe(Item itemID) {}

	public void registerBlockRenderers() {}

	public void registerTileEntities() {
		GameRegistry.registerTileEntity(TileWindmill.class, "net.minecraft.src.abo.energy.TileWindmill");
		GameRegistry.registerTileEntity(TileWaterwheel.class, "net.minecraft.src.abo.energy.TileWaterwheel");
	}
	
	public void registerEntities(){}
}
