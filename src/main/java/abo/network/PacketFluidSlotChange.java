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
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import buildcraft.transport.TileGenericPipe;

public class PacketFluidSlotChange extends ABOPacket {

	private byte	slot;
	private Fluid	fluid;

	public PacketFluidSlotChange(int xCoord, int yCoord, int zCoord, int slot, Fluid fluid) {
		super(ABOPacketIds.LiquidSlotChange, xCoord, yCoord, zCoord);
		this.slot = (byte) slot;
		this.fluid = fluid;
	}

	public PacketFluidSlotChange() {}

	@Override
	public void writeData(ByteBuf data) {
		super.writeData(data);

		data.writeByte(slot);

		if (fluid == null)
			data.writeShort(0);
		else {
			String nbt = fluid.getName();
			byte[] stringData = nbt.getBytes(Charset.forName("UTF-8"));
			data.writeShort((short)stringData.length);
			data.writeBytes(stringData);
			
		}
	}

	@Override
	public void readData(ByteBuf data) {
		super.readData(data);

		this.slot = data.readByte();

		short length = 0;
		try
		{
			length = data.readShort();
		}catch(IndexOutOfBoundsException e) {}
		if (length == 0)
			fluid = null;
		else {
			byte[] compressed = new byte[length];
			data.readBytes(compressed);
				String nbt = "";
				try {
					nbt = new String(compressed, "UTF-8");
				} catch (UnsupportedEncodingException e) {
				}
				fluid = FluidRegistry.getFluid(nbt);

		}
	}

	public void update(EntityPlayer player) {
		TileGenericPipe pipe = getPipe(player.worldObj, posX, posY, posZ);
		if (pipe == null || pipe.pipe == null) return;

		if (!(pipe.pipe instanceof IFluidSlotChange)) return;

		((IFluidSlotChange) pipe.pipe).update(slot, fluid);
	}
}
