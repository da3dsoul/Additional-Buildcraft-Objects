package abo.actions;

import abo.ItemIconProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ActionToggleOnPipe extends ABOAction {

	public ActionToggleOnPipe(int id) {
		super(id, "toggleonpipe");
	}

	@Override
	public String getDescription() {
		return "Toggle Pipe On";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getIconIndex() {
		return ItemIconProvider.ActionToggleOnPipe;
	}

}
