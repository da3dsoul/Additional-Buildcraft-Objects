package abo.pipes.power;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.util.ForgeDirection;
import abo.ABO;
import abo.PipeIcons;
import buildcraft.api.core.IIconProvider;
import buildcraft.api.core.SafeTimeTracker;
import buildcraft.api.mj.MjAPILegacy;
import buildcraft.api.mj.MjBattery;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import buildcraft.api.transport.IPipeTile;
import buildcraft.transport.IPipeTransportPowerHook;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeConnectionBans;
import buildcraft.transport.PipeIconProvider;
import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.pipes.PipePowerWood;

public class PipePowerExtraction extends Pipe<PipeTransportPower> implements IPowerReceptor, IPipeTransportPowerHook {

	public final boolean[]			powerSources		= new boolean[6];

	protected int					standardIconIndex	= PipeIconProvider.TYPE.PipePowerWood_Standard.ordinal();
	protected int					solidIconIndex		= PipeIconProvider.TYPE.PipeAllWood_Solid.ordinal();

	@MjBattery(maxCapacity = 1024, maxReceivedPerCycle = 1024, minimumConsumption = 0)
	private double					mjStored			= 0;
	@SuppressWarnings("unused")
	private final SafeTimeTracker	sourcesTracker		= new SafeTimeTracker(1);
	private boolean					full;

	private MjAPILegacy				powerHandler;

	@SuppressWarnings("unchecked")
	public PipePowerExtraction(Item item) {
		super(new PipeTransportPower(), item);
		transport.initFromPipe(getClass());
		PipeConnectionBans.banConnection(PipePowerExtraction.class, PipePowerExtraction.class);
		PipeConnectionBans.banConnection(PipePowerExtraction.class, PipePowerWood.class);
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if (container.getWorldObj().isRemote) { return; }

		if (mjStored > 0) {
			int sources = 0;

			for (ForgeDirection o : ForgeDirection.VALID_DIRECTIONS) {
				if (!container.isPipeConnected(o)) {
					powerSources[o.ordinal()] = false;
					continue;
				}

				if (isPowerSource(o)) {
					powerSources[o.ordinal()] = true;
				}

				if (powerSources[o.ordinal()]) {
					sources++;
				}
			}

			if (sources <= 0) {
				mjStored = mjStored > 5 ? mjStored - 5 : 0;
				return;
			}

			double energyToRemove;

			if (mjStored > 40) {
				energyToRemove = mjStored / 40 + 4;
			} else if (mjStored > 10) {
				energyToRemove = mjStored / 10;
			} else {
				energyToRemove = 1;
			}
			energyToRemove /= sources;

			for (ForgeDirection o : ForgeDirection.VALID_DIRECTIONS) {
				if (!powerSources[o.ordinal()]) {
					continue;
				}

				double energyUsable = mjStored > energyToRemove ? energyToRemove : mjStored;
				double energySent = transport.receiveEnergy(o, energyUsable);

				if (energySent > 0) {
					mjStored -= energySent;
				}
			}
		}
	}

	public boolean requestsPower() {
		if (full) {
			boolean request = mjStored < 1024 / 2;

			if (request) {
				full = false;
			}

			return request;
		}

		full = mjStored >= 1024 - 10;

		return !full;
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setDouble("mj", mjStored);

		for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
			data.setBoolean("powerSources[" + i + "]", powerSources[i]);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		mjStored = data.getDouble("mj");

		for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
			powerSources[i] = data.getBoolean("powerSources[" + i + "]");
		}
	}

	@Override
	public double receiveEnergy(ForgeDirection from, double val) {
		return -1;
	}

	@Override
	public double requestEnergy(ForgeDirection from, double amount) {
		if (container.getTile(from) instanceof IPipeTile) {
			return amount;
		} else {
			return 0;
		}
	}

	public boolean isPowerSource(ForgeDirection from) {
		return container.getTile(from) instanceof IPowerEmitter;
	}

	@Override
	public PowerReceiver getPowerReceiver(ForgeDirection side) {
		if (powerHandler == null) {
			powerHandler = MjAPILegacy.from(container, Type.PIPE);
		}

		return powerHandler.getPowerReceiver(ForgeDirection.UNKNOWN);
	}

	@Override
	public void doWork(PowerHandler workProvider) {

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