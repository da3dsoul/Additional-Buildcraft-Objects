package abo.energy;

import abo.ABO;
import buildcraft.api.transport.IPipeTile;
import buildcraft.energy.TileEngine;
import buildcraft.transport.TileGenericPipe;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.client.FMLClientHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileConstantPowerProvider extends TileEngine {


    static final float						MAX_OUTPUT				= 1.5f;
    static final float						MIN_OUTPUT				= MAX_OUTPUT / 3;
    public float							TARGET_OUTPUT			= 0.1f;

    public float							realCurrentOutput		= 0;

    public static float						windmillScalar;

    private int								burnTime				= 0;
    private int								totalBurnTime			= 0;

    private int								tickCount				= 0;

    public float							animProgress			= 0;
    protected int radialSymmetryParts;

    public TileConstantPowerProvider() {
        super();
    }

    public TileConstantPowerProvider(float scalar) {
        this();
        windmillScalar = scalar;
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
    }

    @Override
    public void writeData(ByteBuf stream) {
        stream.writeByte(energyStage.ordinal() | (isRedstonePowered ? 8 : 0));
        stream.writeByte(orientation.ordinal());
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
        return currentOutput;
    }

    @Override
    public int getMaxEnergy() {
        return currentOutput;
    }

    @Override
    public int calculateCurrentOutput() {
        updateTargetOutput();
        realCurrentOutput = realCurrentOutput + (TARGET_OUTPUT - realCurrentOutput) / 200;
        return Math.round(realCurrentOutput);
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

    protected abstract void updateTargetOutput();

    protected abstract void updateTargetOutputFirst();

    protected void initDirection(){};

    private int getPowerToExtract() {
        TileEntity tile = getTile(orientation);

        if (tile instanceof IEnergyHandler) {
            IEnergyHandler handler = (IEnergyHandler) tile;
            int maxEnergy = handler.receiveEnergy(orientation.getOpposite(), in(this.currentOutput / 1000), true);
            return in(maxEnergy * windmillScalar);
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
                    int neededRF = handler.receiveEnergy(orientation.getOpposite(),	extracted, false);
                }
            }
        }
    }

    public void checkRedstonePower() {
        isRedstonePowered = canGetWind();
    }

    protected abstract boolean canGetWind();

    // make all carriages render as far as the render distance
    @Override
    public double getMaxRenderDistanceSquared() {
        if(ABO.windmillAnimations && ABO.windmillAnimDist > 64)
        {
            return (double) ABO.windmillAnimDist * (double) ABO.windmillAnimDist;
        }else {
            return (16 * FMLClientHandler.instance().getClient().gameSettings.renderDistanceChunks)
                    * (16 * FMLClientHandler.instance().getClient().gameSettings.renderDistanceChunks);
        }
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return (INFINITE_EXTENT_AABB);
    }

    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!isOrientationValid()) switchOrientation(true);
    }

    public boolean isOrientationValid() {
        return isOrientationValid(orientation);
    }

    public boolean isOrientationValid(ForgeDirection o) {
        TileEntity tile = getTile(o);

        return isPoweredTile(tile, o);
    }

    public boolean switchOrientation(boolean preferPipe) {
        if (preferPipe && switchOrientationDo(true)) {
            return true;
        } else {
            return switchOrientationDo(false);
        }
    }

    protected boolean switchOrientationDo(boolean pipesOnly) {
        for (int i = orientation.ordinal() + 1; i <= orientation.ordinal() + 6; ++i) {
            ForgeDirection o = ForgeDirection.VALID_DIRECTIONS[i % 6];
            if(!isOrientationValid(o)) continue;
            TileEntity tile = getTile(o);

            if ((!pipesOnly || tile instanceof IPipeTile)) {
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
    public void updateEntity() {
        animProgress += getPistonSpeed() / radialSymmetryParts;
        if (animProgress >= 1) animProgress = 0;
        super.updateEntity();
    }

    @Override
    public void burn() {
        if (burnTime > 0) {
            burnTime = burnTime - 1;

            int output = calculateCurrentOutput();
            currentOutput = output; // Comment out for constant power

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

        return currentOutput;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from) {
        return currentOutput;
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return from == orientation;
    }
}
