package abo.actions;

import net.minecraft.nbt.NBTTagCompound;

public class ABOEnergyPulser {

	@SuppressWarnings("unused")
	private double	powerReceptor;

	private boolean	isActive	= false;

	public ABOEnergyPulser(double receptor) {
		powerReceptor = receptor;
	}

	public void update() {
		if (isActive) powerReceptor += 1.0D;
	}

	public void enablePulse() {
		isActive = true;
	}

	public void disablePulse() {
		isActive = false;
	}

	public boolean isActive() {
		return isActive;
	}

	public void writeToNBT(NBTTagCompound nbttagcompound) {
		nbttagcompound.setBoolean("isActive", isActive);
	}

	public void readFromNBT(NBTTagCompound nbttagcompound) {
		isActive = nbttagcompound.getBoolean("isActive");
	}
}
