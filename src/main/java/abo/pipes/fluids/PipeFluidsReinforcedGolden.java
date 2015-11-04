package abo.pipes.fluids;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.ForgeDirection;
import abo.ABO;
import abo.PipeIcons;
import abo.pipes.ABOPipe;
import buildcraft.api.core.IIconProvider;
import buildcraft.transport.PipeTransportFluids;

public class PipeFluidsReinforcedGolden extends ABOPipe<PipeTransportFluidsReinforced> {

	public PipeFluidsReinforcedGolden(Item item) {
	        super(new PipeTransportFluidsReinforced(), item);

        ((PipeTransportFluids)this.transport).initFromPipe(this.getClass());

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
