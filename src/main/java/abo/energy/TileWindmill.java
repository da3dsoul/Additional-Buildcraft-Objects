package abo.energy;

import cofh.api.energy.IEnergyHandler;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import abo.ABO;
import buildcraft.api.transport.IPipeTile;
import buildcraft.core.utils.MathUtils;
import buildcraft.energy.TileEngine;
import buildcraft.transport.TileGenericPipe;

public class TileWindmill extends TileConstantPowerProvider {

	private double							BIOME_OUTPUT			= 0.175f;
	private double							HEIGHT_OUTPUT			= 0f;

	public static final ResourceLocation	TRUNK_BLUE_TEXTURE		= new ResourceLocation(
																			"additional-buildcraft-objects:textures/blocks/trunk_blue.png");
	public static final ResourceLocation	TRUNK_GREEN_TEXTURE		= new ResourceLocation(
																			"additional-buildcraft-objects:textures/blocks/trunk_green.png");
	public static final ResourceLocation	TRUNK_YELLOW_TEXTURE	= new ResourceLocation(
																			"additional-buildcraft-objects:textures/blocks/trunk_yellow.png");
	public static final ResourceLocation	TRUNK_RED_TEXTURE		= new ResourceLocation(
																			"additional-buildcraft-objects:textures/blocks/trunk_red.png");

	public TileWindmill() {
		super();
        radialSymmetryParts = 4;
	}

	@Override
	public ResourceLocation getBaseTexture() {
		return BASE_TEXTURES[0];
	}

	@Override
	public ResourceLocation getChamberTexture() {
		return CHAMBER_TEXTURES[0];
	}

	public ResourceLocation getTrunkTexture(EnergyStage stage) {
		switch (stage) {
			case BLUE:
				return TRUNK_BLUE_TEXTURE;
			case GREEN:
				return TRUNK_GREEN_TEXTURE;
			case YELLOW:
				return TRUNK_YELLOW_TEXTURE;
			case RED:
				return TRUNK_RED_TEXTURE;
			default:
				return TRUNK_RED_TEXTURE;
		}
	}

	protected void updateTargetOutput() {
		if (isRedstonePowered) {
			TARGET_OUTPUT = (float) (0.175f + MathUtils.clamp(BIOME_OUTPUT + HEIGHT_OUTPUT, 0.0f, 1.2f) + (getWorldObj().rainingStrength / 8f))
					* 10000 * ABO.windmillBlock.scalar;
		} else {
			TARGET_OUTPUT = 0;
		}
	}

    @Override
    public boolean isOrientationValid(ForgeDirection o) {
        int l = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        if (o == ForgeDirection.UP || o == ForgeDirection.DOWN) return false;
        if(o == ForgeDirection.EAST) return false;
        TileEntity tile = getTile(o);
        return isPoweredTile(tile, o);
    }

	protected void updateTargetOutputFirst() {
		BiomeGenBase biome = getWorldObj().getBiomeGenForCoords(xCoord, zCoord);
		if (Math.round(biome.heightVariation + 0.2f) == 1.0f) {
			BIOME_OUTPUT = 0.0f;
			HEIGHT_OUTPUT = (float) MathUtils.clamp((yCoord - 58) / 66f, 0f, 1.2f);
		} else {
			BIOME_OUTPUT = (float) MathUtils.clamp(1.2f - biome.heightVariation, 0f, 1.2f);
			float distFrom64Mod = (float) (0.2f * (-7.7160494E-4 * yCoord * yCoord + 0.10185 * yCoord - 2.36111111));
			HEIGHT_OUTPUT = (float) MathUtils.clamp(distFrom64Mod, 0f, 0.2f);
		}
		updateTargetOutput();
        //ABO.aboLog.info("Windmill at " + xCoord + ", " + yCoord + ", " + zCoord);
        //ABO.aboLog.info("Windmill Energy Scalar: " + ABO.windmillBlock.scalar);
        //ABO.aboLog.info("In Biome: " + biome.biomeName);
        //ABO.aboLog.info("Biome Output: " + BIOME_OUTPUT);
        //ABO.aboLog.info("Height Output: " + HEIGHT_OUTPUT);
        //ABO.aboLog.info("Target Output: " + TARGET_OUTPUT);
    }

	@Override
	protected EnergyStage computeEnergyStage() {
		double energyLevel = realCurrentOutput;
		if (energyLevel < 3750f * ABO.windmillBlock.scalar) {
			return EnergyStage.BLUE;
		} else if (energyLevel < 7500f * ABO.windmillBlock.scalar) {
			return EnergyStage.GREEN;
		} else if (energyLevel < 13740f * ABO.windmillBlock.scalar) {
			return EnergyStage.YELLOW;
		} else {
			return EnergyStage.RED;
		}
	}

    public float getPistonSpeed() {
        if (!isRedstonePowered) return 0;
        switch (getEnergyStage()) {
            case BLUE:
                return 0.02F;
            case GREEN:
                return 0.04F;
            case YELLOW:
                return 0.08F;
            case RED:
                return 0.16F;
            default:
                return 0.01F;
        }
    }

	protected boolean canGetWind() {
		for (int i = -1; i < 2; i++) {
			for (int j = -2; j <= 2; j++) {
				for (int k = -2; k <= 2; k++) {
					if (getWorldObj().getBlock(xCoord + j, yCoord + i, zCoord + k).isOpaqueCube()) return false;
				}
			}
		}
		return true;
	}

}
