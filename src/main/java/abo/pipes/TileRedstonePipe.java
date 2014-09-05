package abo.pipes;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.IPipeConnectionForced;
import buildcraft.transport.Pipe;
import buildcraft.transport.TileGenericPipe;

public class TileRedstonePipe extends TileGenericPipe {

	protected boolean canPipeConnect(TileEntity with, ForgeDirection side) {

		if (worldObj.getBlock(xCoord + side.offsetX, yCoord + side.offsetY, zCoord + side.offsetZ).getMaterial() == Material.circuits)
			return true;

		if (with == null) { return false; }

		if (hasPlug(side) || hasRobotStation(side)) { return false; }

		if (!BlockGenericPipe.isValid(pipe)) { return false; }

		if (!(pipe instanceof IPipeConnectionForced) || !((IPipeConnectionForced) pipe).ignoreConnectionOverrides(side)) {
			if (with instanceof IPipeConnection) {
				IPipeConnection.ConnectOverride override = ((IPipeConnection) with).overridePipeConnection(
						pipe.transport.getPipeType(), side.getOpposite());
				if (override != IPipeConnection.ConnectOverride.DEFAULT) { return override == IPipeConnection.ConnectOverride.CONNECT ? true
						: false; }
			}
		}

		if (with instanceof TileGenericPipe) {
			if (((TileGenericPipe) with).hasPlug(side.getOpposite())) { return false; }
			Pipe otherPipe = ((TileGenericPipe) with).pipe;

			if (!BlockGenericPipe.isValid(otherPipe)) { return false; }

			if (!otherPipe.canPipeConnect(this, side.getOpposite())) { return false; }
		}

		return pipe.canPipeConnect(with, side);
	}

}
