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

package abo.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import buildcraft.core.CreativeTabBuildCraft;
import buildcraft.core.ItemBuildCraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * base class for an abo item.
 * 
 * @author Flow86
 * 
 */
public class ABOItem extends ItemBuildCraft {

	private String iconName;

	public ABOItem() {
		super(CreativeTabBuildCraft.ITEMS);
	}

	@Override
	public Item setUnlocalizedName(String name) {
		iconName = name;
		return super.setUnlocalizedName(name);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon("additional-buildcraft-objects:" + iconName);
	}
}
