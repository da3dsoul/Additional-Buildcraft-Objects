package abo.pipes;

import java.util.LinkedList;

import net.minecraft.item.Item;
import abo.ABO;
import buildcraft.api.core.IIconProvider;
import buildcraft.api.statements.IActionExternal;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransport;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ABOPipe<T extends PipeTransport> extends Pipe<T> {
	public ABOPipe(T transport, Item itemID) {
		super(transport, itemID);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIconProvider getIconProvider() {
		return ABO.instance.pipeIconProvider;
	}

	public LinkedList<IActionExternal> getExternalActions() {
		return null;
	}
}
