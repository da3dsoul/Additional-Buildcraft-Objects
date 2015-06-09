package abo.items;

import buildcraft.core.BCCreativeTab;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import buildcraft.transport.ItemPipe;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ABOItemPipe extends ItemPipe {

	private String	iconName;

	public ABOItemPipe() {
		super(BCCreativeTab.get("pipes"));
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
