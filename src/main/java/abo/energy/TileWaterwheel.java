package abo.energy;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import abo.ABO;
import buildcraft.api.transport.IPipeTile;
import buildcraft.core.utils.BlockUtils;
import buildcraft.core.utils.MathUtils;
import buildcraft.energy.TileEngine;
import buildcraft.transport.TileGenericPipe;
import cofh.api.energy.IEnergyHandler;

public class TileWaterwheel extends TileConstantPowerProvider {


	private float							BIOME_OUTPUT			= 0.125f;
	private float							DESIGN_OUTPUT			= 0.125f;

	public boolean							renderBackwards			= false;

	public static final ResourceLocation	TRUNK_BLUE_TEXTURE		= new ResourceLocation(
																			"additional-buildcraft-objects:textures/blocks/trunk_blue.png");
	public static final ResourceLocation	TRUNK_GREEN_TEXTURE		= new ResourceLocation(
																			"additional-buildcraft-objects:textures/blocks/trunk_green.png");
	public static final ResourceLocation	TRUNK_YELLOW_TEXTURE	= new ResourceLocation(
																			"additional-buildcraft-objects:textures/blocks/trunk_yellow.png");
	public static final ResourceLocation	TRUNK_RED_TEXTURE		= new ResourceLocation(
																			"additional-buildcraft-objects:textures/blocks/trunk_red.png");

	public TileWaterwheel() {
		super();
        radialSymmetryParts = 6;
	}

	public TileWaterwheel(float scalar) {
		this();
		windmillScalar = scalar;
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

	@Override
	public void readData(ByteBuf stream) {
		super.readData(stream);
		renderBackwards = stream.readBoolean();
	}

	@Override
	public void writeData(ByteBuf stream) {
		super.writeData(stream);
		stream.writeBoolean(renderBackwards);
	}



	protected void updateTargetOutput() {
		if (isRedstonePowered) {
			float x = getLiquidDensity();
			DESIGN_OUTPUT = MathHelper.clamp_float((float) (-2.021 * x * x + 2.021 * x), 0, 0.5f);
			TARGET_OUTPUT = (float) (0.125f + BIOME_OUTPUT + DESIGN_OUTPUT + (getWorldObj().rainingStrength / 8f))
					* 10000 * windmillScalar;
		} else {
			TARGET_OUTPUT = 0;
		}
	}

	protected void updateTargetOutputFirst() {
		BiomeGenBase biome = getWorldObj().getBiomeGenForCoords(xCoord, zCoord);
		BIOME_OUTPUT = (float) MathUtils.clamp(biome.rainfall, 0f, 1f) / 4;

		updateTargetOutput();
	}

	@Override
	protected EnergyStage computeEnergyStage() {
		double energyLevel = currentOutput;
		if (energyLevel < 3500f * windmillScalar) {
			return EnergyStage.BLUE;
		} else if (energyLevel < 5000f * windmillScalar) {
			return EnergyStage.GREEN;
		} else if (energyLevel < 7450f * windmillScalar) {
			return EnergyStage.YELLOW;
		} else {
			return EnergyStage.RED;
		}
	}

	protected boolean canGetWind() {
		calculateBackwardsness();
		float liquidDensity = getLiquidDensity();
		return liquidDensity < 0.55 && liquidDensity > 0.1;
	}

	private void calculateBackwardsness() {
		Block block;
		int prevdir = 0;
		int l = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		if (l == 0) {
			for (int x = -3; x <= 3; x++) {
				block = worldObj.getBlock(xCoord + x, yCoord - 3, zCoord);
				if (BlockUtils.getFluid(block) != null) {
					Vec3 vec = Vec3.createVectorHelper(0, 0, 0);
					block.velocityToAddToEntity(worldObj, xCoord + x, yCoord - 3, zCoord, null, vec);

					if (vec.xCoord != 0) {
						if (vec.xCoord < 0 && (prevdir == 0 || prevdir < 0)) {
							prevdir = -1;
						} else if (vec.xCoord > 0 && (prevdir == 0 || prevdir > 0)) {
							prevdir = 1;
						}
					}
				}
			}
		} else if (l == 1) {
			for (int x = -3; x <= 3; x++) {
				block = worldObj.getBlock(xCoord, yCoord - 3, zCoord + x);
				if (BlockUtils.getFluid(block) != null) {
					Vec3 vec = Vec3.createVectorHelper(0, 0, 0);
					block.velocityToAddToEntity(worldObj, xCoord, yCoord - 3, zCoord + x, null, vec);

					if (vec.zCoord != 0) {
						if (vec.zCoord < 0 && (prevdir == 0 || prevdir < 0)) {
							prevdir = -1;
						} else if (vec.zCoord > 0 && (prevdir == 0 || prevdir > 0)) {
							prevdir = 1;
						}
					}
				}
			}
		}

		if (prevdir > 0) {
			renderBackwards = true;
		} else if (prevdir < 0) {
			renderBackwards = false;
		}
	}

	private boolean isFlowInProperDirection(int rX, int rY, int rZ, int meta, Vec3 vec) {
		if (rY != -3 && vec.yCoord < 0) return true;
		if (meta == 0) {
			if (rY == -3) {
				if (vec.xCoord != 0) return true;
			}

		} else if (meta == 1) {
			if (rY == -3) {
				if (vec.zCoord != 0) return true;
			}
		}
		return false;
	}

	private float getLiquidDensity() {
		int numBlocks = 0;
		Block block;
		int l = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		if (l == 0) {
			for (int x = -3; x <= 3; x++) {
				for (int y = -3; y <= 3; y++) {
					block = worldObj.getBlock(xCoord + x, yCoord + y, zCoord);
					if (BlockUtils.getFluid(block) != null) {
						Vec3 vec = Vec3.createVectorHelper(0, 0, 0);
						block.velocityToAddToEntity(worldObj, xCoord + x, yCoord + y, zCoord, null, vec);
						if (isFlowInProperDirection(x, y, l, 0, vec)) numBlocks++;
					}
				}
			}
		} else if (l == 1) {
			for (int x = -3; x <= 3; x++) {
				for (int y = -3; y <= 3; y++) {
					block = worldObj.getBlock(xCoord, yCoord + y, zCoord + x);
					if (BlockUtils.getFluid(block) != null) {
						Vec3 vec = Vec3.createVectorHelper(0, 0, 0);
						block.velocityToAddToEntity(worldObj, xCoord, yCoord + y, zCoord + x, null, vec);
						if (isFlowInProperDirection(0, y, x, l, vec)) numBlocks++;
					}
				}
			}
		}
		return (float) (numBlocks / 28.0f);
	}

}
