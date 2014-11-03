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

package abo.actions;

import java.util.Collection;
import java.util.LinkedList;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import abo.pipes.ABOPipe;
import buildcraft.api.statements.IActionExternal;
import buildcraft.api.statements.IActionInternal;
import buildcraft.api.statements.IActionProvider;
import buildcraft.api.statements.IStatementContainer;
import buildcraft.transport.Pipe;
import buildcraft.transport.TileGenericPipe;

public class ABOActionProvider implements IActionProvider {

	@Override
	public Collection<IActionInternal> getInternalActions(IStatementContainer container) {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<IActionExternal> getExternalActions(ForgeDirection side, TileEntity tile) {
		LinkedList<IActionExternal> result = new LinkedList<IActionExternal>();
		if (tile instanceof TileGenericPipe) {
			Pipe pipe = ((TileGenericPipe) tile).pipe;
			
			if(pipe instanceof ABOPipe)
			{
				LinkedList<IActionExternal> list = ((ABOPipe)pipe).getExternalActions();
				if(list == null || list.isEmpty())
					return null;
				result.addAll(list);
			}
		}
		
		return result;
	}
}
