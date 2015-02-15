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

public class TileWaterwheel extends TileEngine {

	static final float						MAX_OUTPUT				= 1.5f;
	static final float						MIN_OUTPUT				= MAX_OUTPUT / 3;
	public float							TARGET_OUTPUT			= 0.1f;
	private float							BIOME_OUTPUT			= 0.125f;
	private float							DESIGN_OUTPUT			= 0.125f;

	public float							realCurrentOutput		= 0;

	public static float						windmillScalar;

	private int								burnTime				= 0;
	private int								totalBurnTime			= 0;

	private int								tickCount				= 0;

	public float							animProgress			= 0;

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
	public void initialize() {
		super.initialize();
		updateTargetOutputFirst();
		initDirection();
		sendNetworkUpdate();
	}

	@Override
	public void readData(ByteBuf stream) {
		int flags = stream.readUnsignedByte();
		energyStage = EnergyStage.values()[flags & 0x07];
		isRedstonePowered = (flags & 0x08) != 0;
		orientation = ForgeDirection.getOrientation(stream.readByte());
		renderBackwards = stream.readBoolean();
	}

	@Override
	public void writeData(ByteBuf stream) {
		stream.writeByte(energyStage.ordinal() | (isRedstonePowered ? 8 : 0));
		stream.writeByte(orientation.ordinal());
		stream.writeBoolean(renderBackwards);
	}

	private void initDirection() {

	}

	@Override
	public void updateEntity() {
		animProgress += getPistonSpeed() / 6;
		if (animProgress >= 1) animProgress = 0;
		super.updateEntity();
	}

	@Override
	public boolean isBurning() {
		return isRedstonePowered;
	}

	@Override
	public int maxEnergyReceived() {
		return 0;
	}

	@Override
	public int maxEnergyExtracted() {
		return getMaxEnergy() / 10;
	}

	@Override
	public int getMaxEnergy() {
		return 100000;
	}

	@Override
	public int calculateCurrentOutput() {
		updateTargetOutput();
		realCurrentOutput = realCurrentOutput + (TARGET_OUTPUT - realCurrentOutput) / 200;
		return Math.round(realCurrentOutput);
	}

	private void updateTargetOutput() {
		if (isRedstonePowered) {
			float x = getLiquidDensity();
			DESIGN_OUTPUT = MathHelper.clamp_float((float) (-2.021 * x * x + 2.021 * x), 0, 0.5f);
			TARGET_OUTPUT = (float) (0.125f + BIOME_OUTPUT + DESIGN_OUTPUT + (getWorldObj().rainingStrength / 8f))
					* 10000 * windmillScalar;
		} else {
			TARGET_OUTPUT = 0;
		}
	}

	private void updateTargetOutputFirst() {
		BiomeGenBase biome = getWorldObj().getBiomeGenForCoords(xCoord, zCoord);
		BIOME_OUTPUT = (float) MathUtils.clamp(biome.rainfall, 0f, 1f) / 4;

		updateTargetOutput();
	}

	public void checkRedstonePower() {
		isRedstonePowered = canGetWind();
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

	public float getPistonSpeed() {
		if (!isRedstonePowered) return 0;
		switch (getEnergyStage()) {
			case BLUE:
				return 0.01F;
			case GREEN:
				return 0.02F;
			case YELLOW:
				return 0.03F;
			case RED:
				return 0.04F;
			default:
				return 0.01F;
		}
	}

	@Override
	public double getMaxRenderDistanceSquared() {
		if (ABO.windmillAnimations && ABO.windmillAnimDist > 64) {
			return (double) (ABO.windmillAnimDist * ABO.windmillAnimDist);
		} else {
			return 4096.0D;
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return (INFINITE_EXTENT_AABB);
	}

	private int getPowerToExtract() {
		TileEntity tile = getTile(orientation);

		if (tile instanceof IEnergyHandler) {
			IEnergyHandler handler = (IEnergyHandler) tile;
			int maxEnergy = handler.receiveEnergy(orientation.getOpposite(), Math.round(this.energy), true);
			return extractEnergy(in(maxEnergy * 1000 * windmillScalar), false);
		} else {
			return 0;
		}
	}

	private int in(float a) {
		return Math.round(a);
	}

	protected void sendPower() {
		TileEntity tile = getTile(orientation);
		if (isPoweredTile(tile, orientation)) {
			int extracted = getPowerToExtract();

			if (tile instanceof IEnergyHandler) {
				IEnergyHandler handler = (IEnergyHandler) tile;
				if (extracted > 0) {
					int neededRF = handler.receiveEnergy(orientation.getOpposite(),
							(int) Math.round(extracted * windmillScalar) / 1000, false);

					extractEnergy(neededRF / 1000, true);
				}
			}
		}
	}

	private boolean canGetWind() {
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

	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (!isOrientationValid()) switchOrientation(true);
	}

	public boolean isOrientationValid() {
		TileEntity tile = getTile(orientation);

		return isPoweredTile(tile, orientation);
	}

	public boolean switchOrientation(boolean preferPipe) {
		if (preferPipe && switchOrientationDo(true)) {
			return true;
		} else {
			return switchOrientationDo(false);
		}
	}

	private boolean switchOrientationDo(boolean pipesOnly) {
		int l = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		for (int i = orientation.ordinal() + 1; i <= orientation.ordinal() + 6; ++i) {
			ForgeDirection o = ForgeDirection.VALID_DIRECTIONS[i % 6];
			if (o == ForgeDirection.UP || o == ForgeDirection.DOWN) continue;
			if (l == 0 && (o == ForgeDirection.EAST || o == ForgeDirection.WEST)) continue;
			if (l == 1 && (o == ForgeDirection.NORTH || o == ForgeDirection.SOUTH)) continue;
			TileEntity tile = getTile(o);

			if ((!pipesOnly || tile instanceof IPipeTile) && isPoweredTile(tile, o)) {
				orientation = o;
				getWorldObj().markBlockForUpdate(xCoord, yCoord, zCoord);
				getWorldObj().notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord,
						worldObj.getBlock(xCoord, yCoord, zCoord));
				getWorldObj().notifyBlocksOfNeighborChange(xCoord + o.offsetX, yCoord + o.offsetY, zCoord + o.offsetZ,
						worldObj.getBlock(xCoord, yCoord, zCoord));

				if (tile instanceof TileGenericPipe) {
					((TileGenericPipe) tile).scheduleNeighborChange();
					((TileGenericPipe) tile).updateEntity();
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public void burn() {
		if (burnTime > 0) {
			burnTime = burnTime - 1;

			int output = calculateCurrentOutput();
			currentOutput = output; // Comment out for constant power
			addEnergy(output);
		}

		if (tickCount % 60 == 0) {
			checkRedstonePower();
		}

		if (burnTime == 0 && isRedstonePowered) {
			burnTime = totalBurnTime = 1200;
		} else {
			if (tickCount >= 1199) {
				updateTargetOutput();
				tickCount = 0;
			} else {
				tickCount++;
			}

		}
	}

	public int getScaledBurnTime(int i) {
		return (int) (((float) burnTime / (float) totalBurnTime) * i);
	}

	// RF

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return this.extractEnergy(maxExtract, !simulate);
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		if (!(from == orientation)) { return 0; }

		return energy;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		return this.getMaxEnergy();
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return from == orientation;
	}

}
