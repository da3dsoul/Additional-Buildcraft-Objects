package abo.pipes.power;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.ForgeDirection;
import abo.ABO;
import abo.PipeIconProvider;
import buildcraft.api.core.IIconProvider;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.transport.IPipeTransportPowerHook;
import buildcraft.transport.pipes.PipePowerWood;

public class PipePowerExtraction extends PipePowerWood implements IPowerReceptor, IPipeTransportPowerHook {


	public PipePowerExtraction(Item item) {
		super(item);
	}
	
	@Override
	public int getIconIndex(ForgeDirection direction) {
		return PipeIconProvider.PipePowerExtraction;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIconProvider getIconProvider() {
		return ABO.instance.pipeIconProvider;
	}
}