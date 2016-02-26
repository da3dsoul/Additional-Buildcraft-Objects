package abo.machines;

import abo.ABO;
import buildcraft.api.core.ISerializable;
import buildcraft.api.core.Position;
import buildcraft.api.tiles.ITileAreaProvider;
import buildcraft.core.LaserKind;
import buildcraft.core.lib.EntityBlock;
import buildcraft.core.lib.RFBattery;
import buildcraft.core.lib.block.TileBuildCraft;
import buildcraft.core.lib.utils.LaserUtils;
import buildcraft.core.proxy.CoreProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.BlockFluidBase;

import java.util.*;

public class TileAccelerator extends TileBuildCraft implements ITileAreaProvider {

    public Origin origin = null;
    private EntityBlock[][] lasers;

    public TileAccelerator() {
        this.setBattery(new RFBattery(64000,64000,0));
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if(origin != null && origin.originWrapper != null && origin.originWrapper.getMarker(worldObj) == this && origin.isValid(worldObj)) {

            if (this.getBattery() != null && this.getBattery().getEnergyStored() >= 1) {
                tickNeighbors();
                tickEntities();
            }
        }
    }

    @Override
    public void initialize() {
        super.initialize();
        tryConnection();
    }

    @Override
    public void onChunkUnload() {
        destroyLasers(false);
    }

    public void tryConnection() {
        if(!this.worldObj.isRemote) {
            if(this.origin == null) addVert(new TileWrapper(this));
            int i;
            int j;
            int k;
            TileAccelerator tileAccelerator;
            for(i = 1; i < 256; i++) {
                Block block = worldObj.getBlock(xCoord + i, yCoord, zCoord);
                if(block == ABO.acceleratorBlock) {
                    TileAccelerator tile = (TileAccelerator) worldObj.getTileEntity(xCoord + i, yCoord, zCoord);

                    if(!mergeOrigin(tile)) break;

                    tileAccelerator = origin.originWrapper.getMarker(worldObj);
                    if (!this.origin.otherVerts.contains(new TileWrapper(tile)) && !origin.originWrapper.equals(new TileWrapper(tile))) {
                        tileAccelerator.addVert(new TileWrapper(tile));
                        break;
                    } else {
                        break;
                    }
                }
                block = worldObj.getBlock(xCoord - i, yCoord, zCoord);
                if(block == ABO.acceleratorBlock) {
                    TileAccelerator tile = (TileAccelerator) worldObj.getTileEntity(xCoord - i, yCoord, zCoord);

                    if(!mergeOrigin(tile)) break;

                    tileAccelerator = origin.originWrapper.getMarker(worldObj);
                    if (!this.origin.otherVerts.contains(new TileWrapper(tile)) && !origin.originWrapper.equals(new TileWrapper(tile))) {
                        tileAccelerator.addVert(new TileWrapper(tile));
                        break;
                    } else {
                        break;
                    }
                }
            }
            for(k = 1; k < 256; k++) {
                Block block = worldObj.getBlock(xCoord, yCoord, zCoord + k);
                if(block == ABO.acceleratorBlock) {
                    TileAccelerator tile = (TileAccelerator) worldObj.getTileEntity(xCoord, yCoord, zCoord + k);

                    if(!mergeOrigin(tile)) break;

                    tileAccelerator = origin.originWrapper.getMarker(worldObj);
                    if (!this.origin.otherVerts.contains(new TileWrapper(tile)) && !origin.originWrapper.equals(new TileWrapper(tile))) {
                        tileAccelerator.addVert(new TileWrapper(tile));
                        break;
                    } else {
                        break;
                    }
                }
                block = worldObj.getBlock(xCoord, yCoord, zCoord - k);
                if(block == ABO.acceleratorBlock) {
                    TileAccelerator tile = (TileAccelerator) worldObj.getTileEntity(xCoord, yCoord, zCoord - k);

                    if(!mergeOrigin(tile)) break;

                    tileAccelerator = origin.originWrapper.getMarker(worldObj);
                    if (!this.origin.otherVerts.contains(new TileWrapper(tile)) && !origin.originWrapper.equals(new TileWrapper(tile))) {
                        tileAccelerator.addVert(new TileWrapper(tile));
                        break;
                    } else {
                        break;
                    }
                }
            }
            for(j = 1; j < 256; j++) {
                Block block;
                if(yCoord + j < 256) {
                    block = worldObj.getBlock(xCoord, yCoord + j, zCoord);
                    if (block == ABO.acceleratorBlock) {
                        TileAccelerator tile = (TileAccelerator) worldObj.getTileEntity(xCoord, yCoord + j, zCoord);

                        if(!mergeOrigin(tile)) break;

                        tileAccelerator = origin.originWrapper.getMarker(worldObj);
                        if (!this.origin.otherVerts.contains(new TileWrapper(tile)) && !origin.originWrapper.equals(new TileWrapper(tile))) {
                            tileAccelerator.addVert(new TileWrapper(tile));
                            break;
                        } else {
                            break;
                        }
                    }
                }
                if(yCoord - j > 1) {
                    block = worldObj.getBlock(xCoord, yCoord - j, zCoord);
                    if (block == ABO.acceleratorBlock) {
                        TileAccelerator tile = (TileAccelerator) worldObj.getTileEntity(xCoord, yCoord - j, zCoord);

                        if(!mergeOrigin(tile)) break;

                        tileAccelerator = origin.originWrapper.getMarker(worldObj);
                        if (!this.origin.otherVerts.contains(new TileWrapper(tile)) && !origin.originWrapper.equals(new TileWrapper(tile))) {
                            tileAccelerator.addVert(new TileWrapper(tile));
                            break;
                        } else {
                            break;
                        }
                    }
                }
            }
            this.origin.originWrapper.getMarker(worldObj).sendNetworkUpdate();
            for(TileWrapper tile : origin.otherVerts) {
                if(tile == null) continue;
                TileAccelerator accelerator = tile.getMarker(worldObj);
                if(accelerator == null) continue;
                accelerator.sendNetworkUpdate();
            }
        }
    }

    private void updateMinMax() {
        if(origin == null || origin.originWrapper == null) return;
        origin.xMax = Math.max(origin.xMax, origin.originWrapper.x);
        origin.yMax = Math.max(origin.yMax, origin.originWrapper.y);
        origin.zMax = Math.max(origin.zMax, origin.originWrapper.z);
        origin.xMin = Math.min(origin.xMin, origin.originWrapper.x);
        origin.yMin = Math.min(origin.yMin, origin.originWrapper.y);
        origin.zMin = Math.min(origin.zMin, origin.originWrapper.z);

        for(TileWrapper tile : origin.otherVerts) {
            origin.xMax = Math.max(origin.xMax, tile.x);
            origin.yMax = Math.max(origin.yMax, tile.y);
            origin.zMax = Math.max(origin.zMax, tile.z);
            origin.xMin = Math.min(origin.xMin, tile.x);
            origin.yMin = Math.min(origin.yMin, tile.y);
            origin.zMin = Math.min(origin.zMin, tile.z);
        }
    }

    public boolean mergeOrigin(TileAccelerator tile) {
        if(worldObj == null) return false;
        if(tile == null) return false;
        if(origin == null) {
            origin = new Origin();
            origin.originWrapper = new TileWrapper(this);
        }

        TileSet<TileWrapper> set = origin.otherVerts;
        if(tile.origin != null && tile.origin.originWrapper != null && !tile.origin.originWrapper.equals(origin.originWrapper)){
            set.addAll(tile.origin.otherVerts);
        }

        if(tile.origin == null || tile.origin.originWrapper == null) {
            tile.origin = origin;
        }

        TileWrapper origin1 = origin.originWrapper;
        TileWrapper origin2 = tile.origin.originWrapper;

        set.add(origin1);
        set.add(origin2);


        ArrayList<TileWrapper> sorted = new ArrayList<TileWrapper>();
        sorted.addAll(set);
        Collections.sort(sorted);

        Iterator<TileWrapper> it2 = sorted.iterator();
        do {
            if(!it2.hasNext()) break;
            TileWrapper wrapper = it2.next();
            if(wrapper == null || wrapper.getMarker(worldObj) == null) {
                it2.remove();
                continue;
            }
        } while(true);

        TileWrapper newOrigin = sorted.get(0);
        if(newOrigin == null) return false;

        set.remove(newOrigin);

        if(set.size() <= 7) {

            origin = new Origin();
            origin.originWrapper = newOrigin;
            origin.otherVerts = set;
            origin.originWrapper.getMarker(worldObj).origin = origin;
            Iterator<TileWrapper> it = origin.otherVerts.iterator();
            do {
                if(!it.hasNext()) break;
                TileWrapper vert = it.next();
                if(vert == null) {
                    it.remove();
                    continue;
                }
                TileAccelerator tileAccelerator = vert.getMarker(worldObj);
                if(tileAccelerator == null) {
                    it.remove();
                    continue;
                }
                tileAccelerator.origin = origin;
            } while(true);

            mergeBatteries();
            updateMinMax();
            return true;
        }
        return false;
    }

    public void mergeBatteries() {
        int energy = 0;
        TileAccelerator accelerator;
        if(origin == null || origin.originWrapper == null || worldObj == null) return;
        TileWrapper tile = origin.originWrapper;
        TileAccelerator originaccelerator = (TileAccelerator) worldObj.getTileEntity(tile.x,tile.y,tile.z);
        if(originaccelerator == null || originaccelerator.getBattery() == null) return;

        for(TileWrapper tile2 : origin.otherVerts) {
            accelerator = (TileAccelerator) worldObj.getTileEntity(tile2.x,tile2.y,tile2.z);
            if(accelerator == null || accelerator.getBattery() == null) continue;
            energy += accelerator.getBattery().getEnergyStored();
            accelerator.setBattery(originaccelerator.getBattery());
        }
        originaccelerator.getBattery().addEnergy(0, energy, false);
    }

    public void addVert(TileWrapper tile) {
        if(tile == null) return;

        if(origin == null) origin = new Origin();
        if(origin.originWrapper == null) origin.originWrapper = tile;
        if(worldObj == null){
            origin.otherVerts.add(tile);
            return;
        }

        TileAccelerator marker = tile.getMarker(worldObj);
        if(marker == null) return;
        if(tile.getMarker(worldObj) == this || tile.equals(origin.originWrapper)) return;

        origin.otherVerts.add(tile);
        mergeBatteries();

        if(origin.otherVerts.size() < 7) {
            tile.getMarker(worldObj).tryConnection();
        }
    }

    public void destroyLasers(boolean recurse) {
        if (this.lasers != null) {
            for (int i = 0; i < lasers.length; i++) {
                EntityBlock[] o = this.lasers[i];
                int var2 = o.length;

                for (int var3 = 0; var3 < var2; ++var3) {
                    EntityBlock entity = o[var3];
                    if (entity != null) {
                        CoreProxy.proxy.removeEntity(entity);
                    }
                }
            }
        }

        this.lasers = new EntityBlock[12][12];

        if (recurse){
            if (this.origin != null) {
                if (this.origin.originWrapper != null && worldObj != null) return;
                if(this.origin.originWrapper.getMarker(worldObj) != this && !this.origin.originWrapper.equals(new TileWrapper(this))) {
                    this.origin.originWrapper.getMarker(worldObj).destroyLasers(false);
                }

                    for (TileWrapper tile : this.origin.otherVerts) {
                        if(tile == null) continue;
                        if (tile.getMarker(worldObj) != null) {
                            if(tile.getMarker(worldObj) == this || tile.equals(new TileWrapper(this))) continue;
                            tile.getMarker(worldObj).destroyLasers(false);
                        }
                    }
            }
        }
    }

    private void createLasers() {
        destroyLasers(true);

        if(!(new TileWrapper(this)).equals(origin.originWrapper)) return;

        updateMinMax();

        lasers[0] = LaserUtils.createLaserBox(worldObj, xMin()-0.5D, yMin()-0.5D, zMin()-0.5D, xMin()+0.5D, yMin()+0.5D, zMax()+0.5D, LaserKind.Blue);
        lasers[1] = LaserUtils.createLaserBox(worldObj, xMax()-0.5D, yMin()-0.5D, zMin()-0.5D, xMax()+0.5D, yMin()+0.5D, zMax()+0.5D, LaserKind.Blue);
        lasers[2] = LaserUtils.createLaserBox(worldObj, xMin()-0.5D, yMin()-0.5D, zMin()-0.5D, xMax()+0.5D, yMin()+0.5D, zMin()+0.5D, LaserKind.Blue);
        lasers[3] = LaserUtils.createLaserBox(worldObj, xMin()-0.5D, yMin()-0.5D, zMax()-0.5D, xMax()+0.5D, yMin()+0.5D, zMax()+0.5D, LaserKind.Blue);

        lasers[4] = LaserUtils.createLaserBox(worldObj, xMin()-0.5D, yMax()-0.5D, zMin()-0.5D, xMin()+0.5D, yMax()+0.5D, zMax()+0.5D, LaserKind.Blue);
        lasers[5] = LaserUtils.createLaserBox(worldObj, xMax()-0.5D, yMax()-0.5D, zMin()-0.5D, xMax()+0.5D, yMax()+0.5D, zMax()+0.5D, LaserKind.Blue);
        lasers[6] = LaserUtils.createLaserBox(worldObj, xMin()-0.5D, yMax()-0.5D, zMin()-0.5D, xMax()+0.5D, yMax()+0.5D, zMin()+0.5D, LaserKind.Blue);
        lasers[7] = LaserUtils.createLaserBox(worldObj, xMin()-0.5D, yMax()-0.5D, zMax()-0.5D, xMax()+0.5D, yMax()+0.5D, zMax()+0.5D, LaserKind.Blue);

        lasers[8] = LaserUtils.createLaserBox(worldObj, xMin()-0.5D, yMin()-0.5D, zMin()-0.5D, xMin()+0.5D, yMax()+0.5D, zMin()+0.5D, LaserKind.Blue);
        lasers[9] = LaserUtils.createLaserBox(worldObj, xMax()-0.5D, yMin()-0.5D, zMin()-0.5D, xMax()+0.5D, yMax()+0.5D, zMin()+0.5D, LaserKind.Blue);
        lasers[10] = LaserUtils.createLaserBox(worldObj, xMin()-0.5D, yMin()-0.5D, zMax()-0.5D, xMin()+0.5D, yMax()+0.5D, zMax()+0.5D, LaserKind.Blue);
        lasers[11] = LaserUtils.createLaserBox(worldObj, xMax()-0.5D, yMin()-0.5D, zMax()-0.5D, xMax()+0.5D, yMax()+0.5D, zMax()+0.5D, LaserKind.Blue);
    }

    public int xMin() {
        return this.origin != null?this.origin.xMin:this.xCoord;
    }

    public int yMin() {
        return this.origin != null?this.origin.yMin:this.yCoord;
    }

    public int zMin() {
        return this.origin != null?this.origin.zMin:this.zCoord;
    }

    public int xMax() {
        return this.origin != null?this.origin.xMax:this.xCoord;
    }

    public int yMax() {
        return this.origin != null?this.origin.yMax:this.yCoord;
    }

    public int zMax() {
        return this.origin != null?this.origin.zMax:this.zCoord;
    }

    public void removeFromWorld() {

    }

    public boolean isValidFromLocation(int x, int y, int z) {
        int equal = (x == this.xCoord?1:0) + (y == this.yCoord?1:0) + (z == this.zCoord?1:0);
        int touching = 0;

        updateMinMax();

        if(equal != 0 && equal != 3) {
            if(x >= this.xMin() - 1 && x <= this.xMax() + 1 && y >= this.yMin() - 1 && y <= this.yMax() + 1 && z >= this.zMin() - 1 && z <= this.zMax() + 1) {
                if(x >= this.xMin() && x <= this.xMax() && y >= this.yMin() && y <= this.yMax() && z >= this.zMin() && z <= this.zMax()) {
                    return false;
                } else {
                    if(this.xMin() - x == 1 || x - this.xMax() == 1) {
                        ++touching;
                    }

                    if(this.yMin() - y == 1 || y - this.yMax() == 1) {
                        ++touching;
                    }

                    if(this.zMin() - z == 1 || z - this.zMax() == 1) {
                        ++touching;
                    }

                    return touching == 1;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void invalidate() {
        super.invalidate();
        this.destroy();
    }

    public void destroy() {
        if(origin != null && origin.originWrapper != null) {
            TileAccelerator tileAccelerator = origin.originWrapper.getMarker(worldObj);
            if(tileAccelerator != null && tileAccelerator != this) {
                tileAccelerator.destroy();
                return;
            }

            destroyLasers(true);

            boolean shouldskip = isInvalid();

            for(TileWrapper tile : origin.otherVerts) {
                if(shouldskip) {
                    shouldskip = false;
                    continue;
                }
                TileAccelerator tileAccelerator1 = tile.getMarker(worldObj);
                if(tileAccelerator1 != null) {
                    tileAccelerator1.origin = null;
                    tileAccelerator1.setBattery(new RFBattery(64000,64000,0));
                }
            }
        }
    }

    private void tickEntities() {
        List<Entity> list = worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(xMin(),yMin(),zMin(),xMax(),yMax(),zMax()).offset(0.5D,0.5D,0.5D).expand(0D,1D,0D));
        for(Entity entity : list) {
            if (entity instanceof EntityPlayer) continue;
            if (this.getBattery().getEnergyStored() >= 1) {
                this.getBattery().useEnergy(1, 1, false);
                entity.onUpdate();
            } else return;
        }
    }

    private void tickNeighbors()
    {
        for (int x = this.xMin(); x <= this.xMax(); x++)
        {
            for (int y = this.yMin(); y <= this.yMax(); y++)
            {
                for (int z = this.zMin(); z <= this.zMax(); z++)
                {
                    if(x == xMin() && z == zMin()) continue;
                    if(x == xMax() && z == zMin()) continue;
                    if(x == xMin() && z == zMax()) continue;
                    if(x == xMax() && z == zMax()) continue;
                    if(!this.tickBlock(x, y, z)) return;
                }
            }
        }
    }

    private boolean tickBlock(final int x, final int y, final int z)
    {
        final Block block = this.worldObj.getBlock(x, y, z);

        if (block == null)
            return true;

        if (block instanceof BlockFluidBase)
            return true;

        if (block.getTickRandomly())
        {
            for (int i = 0; i < 1; i++)
            {
                if (true)
                {

                    if (this.getBattery().getEnergyStored() >= 1) {
                        this.getBattery().useEnergy(1, 1, false);
                        block.updateTick(this.worldObj, x, y, z, worldObj.rand);
                    } else return false;
                }
                else
                {
                    block.updateTick(this.worldObj, x, y, z, worldObj.rand);
                }
            }
        }

        if (block.hasTileEntity(this.worldObj.getBlockMetadata(x, y, z)))
        {
            final TileEntity tile = this.worldObj.getTileEntity(x, y, z);
            if (tile != null && !tile.isInvalid())
            {

                for (int i = 0; i < 1; i++)
                {
                    if (tile.isInvalid())
                        break;

                    if (true)
                    {
                        if (this.getBattery().getEnergyStored() >= 1) {
                            this.getBattery().useEnergy(1, 1, false);
                            tile.updateEntity();
                        } else return false;
                    }
                    else
                    {
                        tile.updateEntity();
                    }
                }
            }
        }
        return true;
    }


    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        if(nbttagcompound.hasKey("origin")) {
            Position pos = new Position(nbttagcompound.getCompoundTag("origin"));
            addVert(new TileWrapper((int)pos.x,(int)pos.y,(int)pos.z));
        }
        if(nbttagcompound.hasKey("otherVerts")) {
            NBTTagList list = nbttagcompound.getTagList("otherVerts", Constants.NBT.TAG_COMPOUND);
            for(int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound tagCompound = list.getCompoundTagAt(i);
                Position pos = new Position(tagCompound);
                addVert(new TileWrapper((int)pos.x,(int)pos.y,(int)pos.z));
            }
        }
    }

    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        if(this.origin != null && origin.originWrapper != null) {
            NBTTagCompound vectO = new NBTTagCompound();
            (new Position(this.origin.originWrapper.getMarker(this.worldObj))).writeToNBT(vectO);
            nbttagcompound.setTag("origin", vectO);

            if(this.origin.originWrapper.getMarker(this.worldObj) == this) {
                NBTTagList list = new NBTTagList();
                for (TileWrapper tileWrapper : origin.otherVerts) {
                    NBTTagCompound vect = new NBTTagCompound();
                    (new Position((double) tileWrapper.x, (double) tileWrapper.y, (double) tileWrapper.z)).writeToNBT(vect);
                    list.appendTag(vect);
                }
                nbttagcompound.setTag("otherVerts", list);
            }
        }
    }

    public void writeData(ByteBuf stream) {
        if(origin == null) return;
        this.origin.writeData(stream);
    }

    public void readData(ByteBuf stream) {
        if(origin == null) origin = new Origin();
        this.origin.readData(stream);

        destroyLasers(true);

        if(origin.isValid(worldObj) && (new TileWrapper(this)).equals(origin.originWrapper)) {
            this.createLasers();
        }
    }


    public static class Origin implements ISerializable {
        public TileWrapper originWrapper = null;
        public TileSet<TileWrapper> otherVerts = new TileSet<TileWrapper>();
        public int xMin = Short.MAX_VALUE;
        public int yMin = Short.MAX_VALUE;
        public int zMin = Short.MAX_VALUE;
        public int xMax = Short.MIN_VALUE;
        public int yMax = Short.MIN_VALUE;
        public int zMax = Short.MIN_VALUE;

        public Origin() {
        }

        public boolean isValid(World world) {
            if(originWrapper == null) return false;
            if(otherVerts.size() == 8) {
                otherVerts.remove(originWrapper);
                if(otherVerts.size() != 7) return false;
            }
            if(otherVerts.size() != 7) return false;
            if(!checkRect(world)) return false;
            return true;
        }

        private boolean checkRect(World worldObj) {
            if(!(originWrapper.x == xMax || originWrapper.x == xMin)) return false;
            if(!(originWrapper.y == yMax || originWrapper.y == yMin)) return false;
            if(!(originWrapper.z == zMax || originWrapper.z == zMin)) return false;
            if(worldObj.getBlock(originWrapper.x, originWrapper.y, originWrapper.z) != ABO.acceleratorBlock) return false;
            for(TileWrapper w : otherVerts) {
                if(!(w.x == xMax || w.x == xMin)) return false;
                if(!(w.y == yMax || w.y == yMin)) return false;
                if(!(w.z == zMax || w.z == zMin)) return false;
                if(worldObj.getBlock(w.x,w.y,w.z) != ABO.acceleratorBlock) return false;
            }
            return true;
        }

        public void writeData(ByteBuf stream) {
            this.originWrapper.writeData(stream);

            stream.writeInt(this.xMin);
            stream.writeShort(this.yMin);
            stream.writeInt(this.zMin);
            stream.writeInt(this.xMax);
            stream.writeShort(this.yMax);
            stream.writeInt(this.zMax);
            stream.writeByte(this.otherVerts.size());

            for(int i = 0; i < otherVerts.size(); i++) {
                TileWrapper tw = otherVerts.get(i);
                tw.writeData(stream);
            }
        }

        public void readData(ByteBuf stream) {
            this.originWrapper = new TileWrapper(stream);

            this.xMin = stream.readInt();
            this.yMin = stream.readShort();
            this.zMin = stream.readInt();
            this.xMax = stream.readInt();
            this.yMax = stream.readShort();
            this.zMax = stream.readInt();
            this.otherVerts = new TileSet<TileWrapper>();
            byte size = stream.readByte();

            for(byte i = 0; i < size; i++) {
                otherVerts.add(new TileWrapper(stream));
            }
        }
    }

    public static class TileWrapper implements ISerializable, Comparable {
        public final int x;
        public final int y;
        public final int z;
        private TileAccelerator marker;

        public TileWrapper(ByteBuf stream) {
            this(stream.readInt(), stream.readShort(), stream.readInt());
        }

        public TileWrapper(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public TileWrapper(TileAccelerator tile) {
            this.x = tile.xCoord;
            this.y = tile.yCoord;
            this.z = tile.zCoord;
            this.marker = tile;
        }

        @Override
        public int compareTo(Object o) {
            if(o instanceof TileWrapper) {
                if(y < ((TileWrapper) o).y) return -1;
                if(x < ((TileWrapper) o).x) return -1;
                if(z < ((TileWrapper) o).z) return -1;

                if(y > ((TileWrapper) o).y) return 1;
                if(x > ((TileWrapper) o).x) return 1;
                if(z > ((TileWrapper) o).z) return 1;
                if(this.equals(o)) return 0;
            }
            return -1;
        }

        public TileAccelerator getMarker(World world) {
            if(this.marker == null) {
                TileEntity tile = world.getTileEntity(this.x, this.y, this.z);
                if(tile instanceof TileAccelerator) {
                    this.marker = (TileAccelerator)tile;
                }
            }

            return this.marker;
        }

        public void readData(ByteBuf stream) {

        }

        public void writeData(ByteBuf stream) {
            stream.writeInt(this.x);
            stream.writeShort(this.y);
            stream.writeInt(this.z);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof TileWrapper) {
                if(this.x == ((TileWrapper) obj).x) {
                    if(this.y == ((TileWrapper) obj).y) {
                        if(this.z == ((TileWrapper) obj).z) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.x + this.z << 8 + this.y << 16;
        }
    }

    public static class TileSet<E> extends ArrayList<E> {
        @Override
        public boolean add(E o) {
            if(this.contains(o)) return false;
            return super.add(o);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            boolean ret = true;
            for(E tile : c) {
                ret = ret && this.add(tile);
            }
            return ret;
        }

        /**
         *
         * @param o
         * @return true if there was a replacement, false if just an add
         */
        public boolean overwrite(E o) {
            if(this.contains(o)) {
                super.add(o);
                return true;
            }
            return super.add(o);
        }
    }
}
