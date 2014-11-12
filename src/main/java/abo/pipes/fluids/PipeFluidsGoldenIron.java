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

package abo.pipes.fluids;

import java.util.Collection;
import java.util.LinkedList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidHandler;
import abo.PipeIcons;
import abo.pipes.ABOPipe;
import buildcraft.BuildCraftTransport;
import buildcraft.api.statements.IActionInternal;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportFluids;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.gates.StatementSlot;
import buildcraft.transport.pipes.PipeFluidsWood;
import buildcraft.transport.pipes.PipeLogicIron;
import buildcraft.transport.pipes.PipeStructureCobblestone;
import buildcraft.transport.statements.ActionPipeDirection;

/**
 * @author Flow86
 * 
 */
public class PipeFluidsGoldenIron extends ABOPipe<PipeTransportFluids> {

	private final int			standardIconIndex	= PipeIcons.PipeLiquidsGoldenIron.ordinal();
	private final int			solidIconIndex		= PipeIcons.PipeLiquidsGoldenIronSide.ordinal();

	private final PipeLogicIron	logic				= new PipeLogicIron(this) {
														@Override
														protected boolean isValidConnectingTile(TileEntity tile) {
															if (tile instanceof TileGenericPipe) {
																Pipe otherPipe = ((TileGenericPipe) tile).pipe;
																if (otherPipe instanceof PipeFluidsWood
																		|| otherPipe instanceof PipeStructureCobblestone)
																	return false;
																if (otherPipe.transport instanceof PipeTransportFluids)
																	return true;
																return false;
															}
															if (tile instanceof IFluidHandler) return true;
															return false;
														}
													};

	public PipeFluidsGoldenIron(Item itemID) {
		super(new PipeTransportFluids(), itemID);

		transport.flowRate = 4 * BuildCraftTransport.pipeFluidsBaseFlowRate;
		transport.travelDelay = 2;
	}

	@Override
	public boolean blockActivated(EntityPlayer entityplayer) {
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

	@Override
	public boolean outputOpen(ForgeDirection to) {
		return super.outputOpen(to) && logic.outputOpen(to);
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		if (direction == ForgeDirection.UNKNOWN)
			return standardIconIndex;
		else {
			int metadata = container.getBlockMetadata();

			if (metadata != direction.ordinal())
				return solidIconIndex;
			else
				return standardIconIndex;
		}
	}

	@Override
	protected void actionsActivated(Collection<StatementSlot> actions) {
		super.actionsActivated(actions);

		for (StatementSlot action : actions) {
			if (action.statement instanceof ActionPipeDirection) {
				logic.setFacing(((ActionPipeDirection) action.statement).direction);
				break;
			}
		}
	}

	@Override
	public LinkedList<IActionInternal> getActions() {
		LinkedList<IActionInternal> action = super.getActions();
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			if (container.isPipeConnected(direction))
				action.add(BuildCraftTransport.actionPipeDirection[direction.ordinal()]);
		}
		return action;
	}

	@Override
	public boolean canConnectRedstone() {
		return true;
	}
}
