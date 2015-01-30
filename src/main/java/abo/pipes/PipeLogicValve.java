package abo.pipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import buildcraft.api.tools.IToolWrench;
import buildcraft.api.transport.IPipeTile;
import buildcraft.core.TileBuffer;
import buildcraft.transport.Pipe;

public class PipeLogicValve {

	protected final Pipe	pipe;

	public PipeLogicValve(Pipe pipe) {
		this.pipe = pipe;
	}

	public void switchSource() {
		int meta = pipe.container.getBlockMetadata();
		ForgeDirection newFacing = null;

		for (int i = meta + 1; i <= meta + 6; ++i) {
			ForgeDirection facing = ForgeDirection.getOrientation(i % 6);
			if (isValidFacing(facing)) {
				newFacing = facing;
				break;
			}
		}

		if (newFacing != null && newFacing.ordinal() != meta) {
			pipe.container.getWorldObj().setBlockMetadataWithNotify(pipe.container.xCoord, pipe.container.yCoord,
					pipe.container.zCoord, newFacing.ordinal(), 3);
			pipe.container.scheduleRenderUpdate();
		}
	}

	private void switchSourceIfNeeded() {
		int meta = pipe.container.getBlockMetadata();

		if (meta > 5) {
			switchSource();
		} else {
			ForgeDirection facing = ForgeDirection.getOrientation(meta);
			if (!isValidFacing(facing)) {
				switchSource();
			}
		}
	}

	private boolean isValidFacing(ForgeDirection side) {
		TileBuffer[] tileBuffer = pipe.container.getTileCache();
		if (tileBuffer == null) { return true; }

		if (!tileBuffer[side.ordinal()].exists()) { return true; }

		TileEntity tile = tileBuffer[side.ordinal()].getTile();
		return isValidConnectingTile(tile);
	}

	protected boolean isValidConnectingTile(TileEntity tile) {
		if (tile instanceof IPipeTile) { return false; }
		if (!(tile instanceof IFluidHandler)) { return false; }
		// if(container.getBlockMetadata() !=
		// getDirectionToTile(tile).ordinal()) return false;
		return true;
	}

	public void initialize() {
		if (!pipe.container.getWorldObj().isRemote) {
			switchSourceIfNeeded();
		}
	}

	public boolean blockActivated(EntityPlayer entityplayer) {
		Item equipped = entityplayer.getCurrentEquippedItem() != null ? entityplayer.getCurrentEquippedItem().getItem()
				: null;
		if (equipped instanceof IToolWrench
				&& ((IToolWrench) equipped).canWrench(entityplayer, pipe.container.xCoord, pipe.container.yCoord,
						pipe.container.zCoord)) {
			switchSource();
			((IToolWrench) equipped).wrenchUsed(entityplayer, pipe.container.xCoord, pipe.container.yCoord,
					pipe.container.zCoord);
			return true;
		}

		return false;
	}

	public void onNeighborBlockChange(int blockId) {
		if (!pipe.container.getWorldObj().isRemote) {
			switchSourceIfNeeded();
		}
	}
}
