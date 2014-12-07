package abo.network;

import net.minecraftforge.fluids.Fluid;

public interface IFluidSlotChange {
	public void update(int slot, Fluid stack);
}
