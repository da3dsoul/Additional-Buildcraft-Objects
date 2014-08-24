package abo.pipes.power;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.ForgeDirection;
import abo.ABO;
import abo.PipeIcons;
import buildcraft.api.core.IIconProvider;
import buildcraft.transport.pipes.PipePowerWood;

public class PipePowerExtraction extends PipePowerWood {


	public PipePowerExtraction(Item item) {
		super(item);
	}
	
	@Override
	public int getIconIndex(ForgeDirection direction) {
		return PipeIcons.PipePowerExtraction.ordinal();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIconProvider getIconProvider() {
		return ABO.instance.pipeIconProvider;
	}
}