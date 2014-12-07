package abo.actions;

import buildcraft.api.statements.IActionInternal;
import buildcraft.api.statements.IStatement;
import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import buildcraft.core.statements.BCStatement;
import net.minecraft.util.IIcon;
import abo.ABO;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public abstract class ABOAction extends BCStatement implements IActionInternal {

	public ABOAction(int id, String uniqueTag) {
		super("abo.actions." + uniqueTag);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon() {
		return ABO.instance.itemIconProvider.getIcon(getIconIndex());
	}

	@Override
	public IStatement rotateLeft() {
		return this;
	}
	
	public abstract int getIconIndex();
	
	@Override
	public void actionActivate(IStatementContainer source, IStatementParameter[] parameters) {}
}
