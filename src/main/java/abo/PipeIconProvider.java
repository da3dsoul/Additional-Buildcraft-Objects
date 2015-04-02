package abo;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import buildcraft.api.core.IIconProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PipeIconProvider implements IIconProvider {

	@SideOnly(Side.CLIENT)
	private IIcon[]	_icons;

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int iconIndex) {
		return _icons[iconIndex];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		_icons = new IIcon[PipeIcons.MAX.ordinal()];

		_icons[PipeIcons.PipeItemsCrossover.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeItemsCrossover");

		_icons[PipeIcons.PipeItemsExtraction.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeItemsExtract");
		_icons[PipeIcons.PipeItemsExtractionSide.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeItemsExtractSide");

		_icons[PipeIcons.PipeItemsInsertion.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeItemsInsert");
		_icons[PipeIcons.PipeItemsRoundRobin.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeItemsRoundRobin");

		_icons[PipeIcons.PipeLiquidsBalance.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeLiquidsBalance");

		_icons[PipeIcons.PipeLiquidsGoldenIron.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeLiquidsGoldenIron");
		_icons[PipeIcons.PipeLiquidsGoldenIronSide.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeLiquidsGoldenIronSide");
		
		_icons[PipeIcons.PipeLiquidsReinforcedGoldenIron.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeLiquidsReinforcedGoldenIron");
		_icons[PipeIcons.PipeLiquidsReinforcedGoldenIronSide.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeLiquidsReinforcedGoldenIronSide");
		
		_icons[PipeIcons.PipeLiquidsReinforcedGolden.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeLiquidsReinforcedGolden");
		

		_icons[PipeIcons.PipeLiquidsValveClosed.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeLiquidsValveClosed");
		_icons[PipeIcons.PipeLiquidsValveClosedSide.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeLiquidsValveClosedSide");
		_icons[PipeIcons.PipeLiquidsValveOpen.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeLiquidsValveOpen");
		_icons[PipeIcons.PipeLiquidsValveOpenSide.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeLiquidsValveOpenSide");

        _icons[PipeIcons.PipeItemsBounceOpen.ordinal()] = iconRegister
                .registerIcon("additional-buildcraft-objects:PipeItemsBounce");
        _icons[PipeIcons.PipeItemsBounceClosed.ordinal()] = iconRegister
                .registerIcon("additional-buildcraft-objects:PipeItemsBounceClosed");

		_icons[PipeIcons.PipePowerSwitchPowered.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipePowerSwitchPowered");
		_icons[PipeIcons.PipePowerSwitchUnpowered.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipePowerSwitchUnpowered");

		_icons[PipeIcons.PipePowerIron.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipePowerIron");
		_icons[PipeIcons.PipePowerIronSide.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipePowerIronSide");

		_icons[PipeIcons.PipeItemsCompactorOn.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeItemsCompactorOn");
		_icons[PipeIcons.PipeItemsCompactorOff.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeItemsCompactorOff");

		_icons[PipeIcons.PipeItemsDivide.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeItemsDivide");

		_icons[PipeIcons.PipeLiquidsInsertion.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeLiquidsInsertion");

		_icons[PipeIcons.PipeItemsEnderExtraction.ordinal()] = iconRegister
				.registerIcon("additional-buildcraft-objects:PipeItemsEnderExtract");
		
	}
}
