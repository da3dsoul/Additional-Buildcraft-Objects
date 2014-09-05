package abo.pipes;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import buildcraft.transport.BlockGenericPipe;

public class BlockRedstonePipe extends BlockGenericPipe {

	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileRedstonePipe();
	}

}
