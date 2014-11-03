package da3dsoul.scaryGen.pathfinding_astar;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class FollowEntityMoveHelper
{
    private double posX;
    private double posY;
    private double posZ;

    /** The speed at which the entity should move */
    private double speed;
    private boolean update = false;

    public FollowEntityMoveHelper(Entity par1EntityLiving)
    {
        this.posX = par1EntityLiving.posX;
        this.posY = par1EntityLiving.posY;
        this.posZ = par1EntityLiving.posZ;
    }

    public boolean isUpdating()
    {
        return this.update;
    }

    public double getSpeed()
    {
        return this.speed;
    }

    /**
     * Sets the speed and location to move to
     */
    public void setMoveTo(double par1, double par3, double par5, double par7)
    {
        this.posX = par1;
        this.posY = par3;
        this.posZ = par5;
        this.speed = par7;
        this.update = true;
    }

    /**
     * Limits the given angle to a upper and lower limit.
     */
    @SuppressWarnings("unused")
	private float limitAngle(float par1, float par2, float par3)
    {
        float var4 = MathHelper.wrapAngleTo180_float(par2 - par1);

        if (var4 > par3)
        {
            var4 = par3;
        }

        if (var4 < -par3)
        {
            var4 = -par3;
        }

        return par1 + var4;
    }
    
    public Vec3 getDestination()
    {
    	return Vec3.createVectorHelper(posX, posY, posZ);
    }
}
