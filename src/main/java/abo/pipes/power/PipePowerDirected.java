package abo.pipes.power;

import buildcraft.BuildCraftTransport;
import buildcraft.api.statements.IActionInternal;
import buildcraft.api.statements.StatementSlot;
import buildcraft.transport.statements.ActionPipeDirection;
import cofh.api.energy.IEnergyConnection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import abo.PipeIcons;
import abo.pipes.ABOPipe;
import buildcraft.transport.IPipeTransportPowerHook;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.pipes.PipeLogicIron;
import buildcraft.transport.pipes.PipePowerWood;
import buildcraft.transport.pipes.PipeStructureCobblestone;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class PipePowerDirected extends ABOPipe<PipeTransportPower> implements IPipeTransportPowerHook {

	private final int			baseTexture	= PipeIcons.PipePowerIron.ordinal();
	private final int			sideTexture	= PipeIcons.PipePowerIronSide.ordinal();

	private final PipeLogicIron	logic		= new PipeLogicIron(this) {
												@Override
												protected boolean isValidConnectingTile(TileEntity tile) {
													if (tile instanceof TileGenericPipe) {
														Pipe otherPipe = ((TileGenericPipe) tile).pipe;
														if (otherPipe instanceof PipePowerWood
																|| otherPipe instanceof PipeStructureCobblestone)
															return false;
														if (otherPipe.transport instanceof PipeTransportPower)
															return true;
														return false;
													}
													if (tile instanceof IEnergyConnection) return true;
													return false;
												}
											};

	@Override
	public boolean blockActivated(EntityPlayer entityplayer, ForgeDirection side) {
		return logic.blockActivated(entityplayer);
	}

	@Override
	public void onNeighborBlockChange(int blockId) {
		logic.switchOnRedstone();
		super.onNeighborBlockChange(blockId);
	}

	@Override
	public void onBlockPlaced() {
		logic.onBlockPlaced();
		super.onBlockPlaced();
	}

	@Override
	public void initialize() {
		logic.initialize();
		super.initialize();
	}

	public PipePowerDirected(Item itemID) {
		super(new PipeTransportPower(), itemID);

		PipeTransportPower.powerCapacities.put(PipePowerDirected.class, 10240);
		transport.initFromPipe(getClass());
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		if (direction == ForgeDirection.UNKNOWN)
			return baseTexture;
		else {
			int metadata = container.getBlockMetadata();

			if (metadata == direction.ordinal())
				return baseTexture;
			else
				return sideTexture;
		}
	}

    @Override
    public boolean outputOpen(ForgeDirection to) {
        return container.getBlockMetadata() == to.ordinal();
    }

    protected void actionsActivated(Collection<StatementSlot> actions) {
        super.actionsActivated(actions);
        Iterator var2 = actions.iterator();

        while(var2.hasNext()) {
            StatementSlot action = (StatementSlot)var2.next();
            if(action.statement instanceof ActionPipeDirection) {
                this.logic.setFacing(((ActionPipeDirection)action.statement).direction);
                break;
            }
        }

    }

    public LinkedList<IActionInternal> getActions() {
        LinkedList action = super.getActions();
        ForgeDirection[] var2 = ForgeDirection.VALID_DIRECTIONS;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            ForgeDirection direction = var2[var4];
            if(this.container.isPipeConnected(direction)) {
                action.add(BuildCraftTransport.actionPipeDirection[direction.ordinal()]);
            }
        }

        return action;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int val) {
        int metadata = container.getBlockMetadata();

        if (metadata != from.ordinal() && val > 0.0) {
            transport.internalNextPower[from.ordinal()] += val;

            if (transport.internalNextPower[from.ordinal()] > transport.maxPower) {
                val = (int) transport.internalNextPower[from.ordinal()] - transport.maxPower;
                transport.internalNextPower[from.ordinal()] = transport.maxPower;
            }
        }
        return val;
    }

    @Override
    public int requestEnergy(ForgeDirection from, int amount) {
        int metadata = container.getBlockMetadata();

        if (metadata == from.ordinal()) { return amount; }
        return 0;
    }
}
