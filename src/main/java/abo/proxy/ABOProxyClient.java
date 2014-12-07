package abo.proxy;

import abo.ABO;
import abo.energy.TileWindmill;
import abo.network.ABOPacket;
import abo.render.RenderWindmill;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import buildcraft.core.render.RenderingEntityBlocks;
import buildcraft.core.render.RenderingEntityBlocks.EntityRenderIndex;
import buildcraft.transport.TransportProxyClient;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import da3dsoul.scaryGen.entity.EntityItemBat;
import da3dsoul.scaryGen.render.RenderBat;

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

	}

	public void registerBlockRenderers() {
		RenderingEntityBlocks.blockByEntityRenders.put(new EntityRenderIndex(ABO.windmillBlock, 0),
				new RenderWindmill());
	}
	
	public void registerEntities()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityItemBat.class, new RenderBat());
	}
}
