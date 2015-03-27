package abo;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import buildcraft.api.core.IIconProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemIconProvider implements IIconProvider {

	public static final int	ActionSwitchOnPipe	= 0;
	public static final int	ActionToggleOnPipe	= 1;
	public static final int	ActionToggleOffPipe	= 2;

    public static final int	bucket	= 3;

	public static final int	MAX					= 4;

	@SideOnly(Side.CLIENT)
	private IIcon[]			_icons;

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int iconIndex) {
		return _icons[iconIndex];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
        if(ABO.blockLiquidXP != null) {
            _icons = new IIcon[MAX];
        }else {
            _icons = new IIcon[MAX - 1];
        }

		_icons[ActionSwitchOnPipe] = iconRegister
				.registerIcon("additional-buildcraft-objects:actions/ActionSwitchOnPipe");
		_icons[ActionToggleOnPipe] = iconRegister
				.registerIcon("additional-buildcraft-objects:actions/ActionToggleOnPipe");
		_icons[ActionToggleOffPipe] = iconRegister
				.registerIcon("additional-buildcraft-objects:actions/ActionToggleOffPipe");

        if(ABO.blockLiquidXP != null) {
            _icons[bucket] = iconRegister
                    .registerIcon("liquidxp:bucket");
        }

    }
}
