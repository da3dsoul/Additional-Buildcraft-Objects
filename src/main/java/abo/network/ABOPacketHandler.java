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
