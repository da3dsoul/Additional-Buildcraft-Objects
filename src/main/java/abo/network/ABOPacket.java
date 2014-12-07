package abo.network;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildcraft.core.network.PacketCoordinates;
import buildcraft.transport.TileGenericPipe;

public class ABOPacket extends PacketCoordinates {
	public ABOPacket() {}

	public ABOPacket(int id, int xCoord, int yCoord, int zCoord) {
		super(id, xCoord, yCoord, zCoord);
	}

	protected TileGenericPipe getPipe(World world, int x, int y, int z) {
		if (!world.blockExists(x, y, z)) return null;

		TileEntity tile = world.getTileEntity(x, y, z);
		if (!(tile instanceof TileGenericPipe)) return null;

		return (TileGenericPipe) tile;
	}
}
