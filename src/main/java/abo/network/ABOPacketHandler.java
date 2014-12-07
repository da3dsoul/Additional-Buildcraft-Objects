package abo.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import buildcraft.core.proxy.CoreProxy;

import abo.ABO;

import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.NetworkRegistry;

public class ABOPacketHandler extends FMLIndexedMessageToMessageCodec<ABOPacket> {

	public ABOPacketHandler() {
		addDiscriminator(0, PacketFluidSlotChange.class);
		addDiscriminator(1, PacketYesNoChange.class);
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ABOPacket packet, ByteBuf data) throws Exception {
		packet.writeData(data);

	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf data, ABOPacket packet) {
		packet.readData(data);
		try {
			INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
			EntityPlayer player = CoreProxy.proxy.getPlayerFromNetHandler(netHandler);

			int packetID = packet.getID();

			switch (packetID) {
				case ABOPacketIds.YesNoChange: {
					PacketYesNoChange yesNoPacket = (PacketYesNoChange) packet;
					yesNoPacket.update((EntityPlayer) player);
					break;
				}
				case ABOPacketIds.LiquidSlotChange: {
					PacketFluidSlotChange liquidSlotPacket = (PacketFluidSlotChange) packet;
					liquidSlotPacket.update((EntityPlayer) player);
					break;
				}
				default:
					ABO.aboLog.info("Packet: " + packetID);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
