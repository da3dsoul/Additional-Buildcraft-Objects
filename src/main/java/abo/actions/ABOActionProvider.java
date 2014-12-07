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
