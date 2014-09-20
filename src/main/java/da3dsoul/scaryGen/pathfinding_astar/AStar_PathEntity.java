package da3dsoul.scaryGen.pathfinding_astar;

import net.minecraft.util.*;
import net.minecraft.entity.Entity;

public class AStar_PathEntity
{
    /** The actual points in the path */
    private final ChunkCoordinates[] points;

    /** PathEntity Array Index the Entity is currently targeting */
    private int currentPathIndex;

    /** The total length of the path */
    private int pathLength;

    public AStar_PathEntity(ChunkCoordinates[] par1ArrayOfChunkCoordinates)
    {
        this.points = par1ArrayOfChunkCoordinates;
        this.pathLength = par1ArrayOfChunkCoordinates.length;
    }

    /**
     * Directs this path to the next point in its array
     */
    public void incrementPathIndex()
    {
        ++this.currentPathIndex;
    }

    /**
     * Returns true if this path has reached the end
     */
    public boolean isFinished()
    {
        return this.currentPathIndex >= this.pathLength;
    }

    /**
     * returns the last ChunkCoordinates of the Array
     */
    public ChunkCoordinates getFinalChunkCoordinates()
    {
        return this.pathLength > 0 ? this.points[this.pathLength - 1] : null;
    }

    /**
     * return the ChunkCoordinates located at the specified PathIndex, usually the current one
     */
    public ChunkCoordinates getChunkCoordinatesFromIndex(int par1)
    {
        return this.points[par1];
    }

    public int getCurrentPathLength()
    {
        return this.pathLength;
    }

    public void setCurrentPathLength(int par1)
    {
        this.pathLength = par1;
    }

    public int getCurrentPathIndex()
    {
        return this.currentPathIndex;
    }

    public void setCurrentPathIndex(int par1)
    {
        this.currentPathIndex = par1;
    }

    /**
     * Gets the vector of the ChunkCoordinates associated with the given index.
     */
    public Vec3 getVectorFromIndex(Entity par1Entity, int par2)
    {
        double var3 = (double)this.points[par2].posX + (double)((int)(par1Entity.width + 1.0F)) * 0.5D;
        double var5 = (double)this.points[par2].posY;
        double var7 = (double)this.points[par2].posZ + (double)((int)(par1Entity.width + 1.0F)) * 0.5D;
        return Vec3.createVectorHelper(var3, var5, var7);
    }

    /**
     * returns the current PathEntity target node as Vec3D
     */
    public Vec3 getPosition(Entity par1Entity)
    {
        return this.getVectorFromIndex(par1Entity, this.currentPathIndex);
    }

    /**
     * Returns true if the EntityPath are the same. Non instance related equals.
     */
    public boolean isSamePath(AStar_PathEntity par1PathEntity)
    {
        if (par1PathEntity == null)
        {
            return false;
        }
        else if (par1PathEntity.points.length != this.points.length)
        {
            return false;
        }
        else
        {
            for (int var2 = 0; var2 < this.points.length; ++var2)
            {
                if (this.points[var2].posX != par1PathEntity.points[var2].posX || this.points[var2].posY != par1PathEntity.points[var2].posY || this.points[var2].posZ != par1PathEntity.points[var2].posZ)
                {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * Returns true if the final ChunkCoordinates in the PathEntity is equal to Vec3D coords.
     */
    public boolean isDestinationSame(Vec3 par1Vec3)
    {
        ChunkCoordinates var2 = this.getFinalChunkCoordinates();
        return var2 == null ? false : var2.posX == (int)par1Vec3.xCoord && var2.posY == (int)par1Vec3.yCoord && var2.posZ == (int)par1Vec3.zCoord;
    }
}
