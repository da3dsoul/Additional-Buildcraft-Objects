package abo.actions;

import abo.ItemIconProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ActionSwitchOnPipe extends ABOAction {

	public ActionSwitchOnPipe(int id) {
		super(id, "switchonpipe");
	}

	@Override
	public String getDescription() {
		return "Switch On Pipe";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getIconIndex() {
		return ItemIconProvider.ActionSwitchOnPipe;
	}

}
