package abo.actions;

import abo.ItemIconProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ActionToggleOffPipe extends ABOAction {

	public ActionToggleOffPipe(int id) {
		super(id, "toggleoffpipe");
	}

	@Override
	public String getDescription() {
		return "Toggle Pipe Off";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getIconIndex() {
		return ItemIconProvider.ActionToggleOffPipe;
	}

}
