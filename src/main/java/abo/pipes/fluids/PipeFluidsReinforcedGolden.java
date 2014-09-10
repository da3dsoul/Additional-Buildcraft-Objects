package abo.pipes.fluids;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.ForgeDirection;
import abo.ABO;
import abo.PipeIcons;
import buildcraft.api.core.IIconProvider;
import buildcraft.transport.pipes.PipeFluidsGold;

public class PipeFluidsReinforcedGolden extends PipeFluidsGold {
	
	public PipeFluidsReinforcedGolden(Item itemID) {
		super(itemID);

		transport.flowRate = 250;
		transport.travelDelay = 2;
	}
	
	@Override
	public int getIconIndex(ForgeDirection direction) {
			return PipeIcons.PipeLiquidsReinforcedGolden.ordinal();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIconProvider getIconProvider() {
		return ABO.instance.pipeIconProvider;
	}

}
