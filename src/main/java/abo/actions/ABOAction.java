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

import buildcraft.api.statements.IActionInternal;
import buildcraft.api.statements.IStatement;
import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import buildcraft.core.statements.BCStatement;
import net.minecraft.util.IIcon;
import abo.ABO;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author Flow86
 * 
 */
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
