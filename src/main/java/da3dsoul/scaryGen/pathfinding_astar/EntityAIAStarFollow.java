package da3dsoul.scaryGen.pathfinding_astar;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAIAStarFollow extends EntityAIBase
{
    private EntityTameable thePet;
    private EntityLivingBase theOwner = null;
    World theWorld;
    private float speed;
    private FollowPathNavigate petPathfinder;
    private int field_75343_h;
    float maxDist;
    float minDist;
    private boolean field_75344_i;
    
    public EntityAIAStarFollow(EntityTameable par1EntityLiving, double p_i1625_2_, float p_i1625_4_, float p_i1625_5_)
    {
        this.thePet = par1EntityLiving;
        this.theWorld = par1EntityLiving.worldObj;
        this.speed = thePet.getAIMoveSpeed();
        this.petPathfinder = new FollowPathNavigate(par1EntityLiving, theWorld, p_i1625_5_);
        this.minDist = p_i1625_4_;
        this.maxDist = p_i1625_5_;
        this.setMutexBits(3);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLivingBase entitylivingbase = this.thePet.getOwner();

        if (entitylivingbase == null)
        {
            return false;
        }
        else if (this.thePet.isSitting())
        {
            return false;
        }
        else if (this.thePet.getDistanceSqToEntity(entitylivingbase) < (double)(this.minDist * this.minDist))
        {
            return false;
        }
        else
        {
            this.theOwner = entitylivingbase;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return shouldExecute() && !this.petPathfinder.noPath() && this.thePet.getDistanceSqToEntity(this.theOwner) > (double)(this.maxDist * this.maxDist) && !this.thePet.isSitting();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.field_75343_h = 0;
        this.field_75344_i = petPathfinder.getAvoidsWater();
        this.petPathfinder.setAvoidsWater(false);
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.theOwner = null;
        this.petPathfinder.clearPathEntity();
        this.petPathfinder.setAvoidsWater(this.field_75344_i);
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        

        if (!this.thePet.isSitting())
        {
        	
        	if(!tryToMove())
        	{
        		this.thePet.getLookHelper().setLookPositionWithEntity(this.theOwner, 10.0F, (float)this.thePet.getVerticalFaceSpeed());
        	}
        	
            if (--this.field_75343_h <= 0)
            {
                this.field_75343_h = 200;

                this.petPathfinder.tryMoveToEntityLiving(this.theOwner, (float)this.speed);
                
                /*if ()
                {
                    if (!this.thePet.getLeashed())
                    {
                        if (this.thePet.getDistanceSqToEntity(this.theOwner) >= 144.0D)
                        {
                            int i = MathHelper.floor_double(this.theOwner.posX) - 2;
                            int j = MathHelper.floor_double(this.theOwner.posZ) - 2;
                            int k = MathHelper.floor_double(this.theOwner.boundingBox.minY);

                            for (int l = 0; l <= 4; ++l)
                            {
                                for (int i1 = 0; i1 <= 4; ++i1)
                                {
                                    if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && World.doesBlockHaveSolidTopSurface(this.theWorld, i + l, k - 1, j + i1) && !this.theWorld.getBlock(i + l, k, j + i1).isNormalCube() && !this.theWorld.getBlock(i + l, k + 1, j + i1).isNormalCube())
                                    {
                                        this.thePet.setLocationAndAngles((double)((float)(i + l) + 0.5F), (double)k, (double)((float)(j + i1) + 0.5F), this.thePet.rotationYaw, this.thePet.rotationPitch);
                                        this.petPathfinder.clearPathEntity();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }*/
            }
            this.petPathfinder.onUpdateNavigation();
        }
    }
    
    private boolean tryToMove()
    {
    	
    	double x = Double.NaN;
    	double y = Double.NaN;
    	double z = Double.NaN;
		
    	if(thePet instanceof IFollowable)
    	{
    		Vec3 vec = ((IFollowable)thePet).getDest();
    		x = vec.xCoord;
    		y = vec.yCoord;
    		z = vec.zCoord;
    	}
    	
		if(x == Double.NaN) return false;
		if(y == Double.NaN) return false;
		if(z == Double.NaN) return false;

		double d = x - this.thePet.posX;
		double d1 = y - this.thePet.posY;
		double d2 = z - this.thePet.posZ;
		Vec3 vec = Vec3.createVectorHelper(d, d1, d2);
		vec = vec.normalize();
		this.thePet.motionX = vec.xCoord * this.thePet.getAIMoveSpeed() / 20;
		this.thePet.motionY = vec.yCoord * this.thePet.getAIMoveSpeed() / 20;
		this.thePet.motionZ = vec.zCoord * this.thePet.getAIMoveSpeed() / 20;
		if(this.thePet.isCollidedHorizontally)
		{
			if(this.thePet.motionY > 0)
			{
				this.thePet.motionY += 0.5;
			}else if(this.thePet.motionY < 0)
			{
				this.thePet.motionY -= 0.5;
			}
		}
		float maxVal = 3.25F;
		this.thePet.motionX = MathHelper.clamp_float((float)this.thePet.motionX, -maxVal, maxVal);
		this.thePet.motionY = MathHelper.clamp_float((float)this.thePet.motionY, -maxVal, maxVal);
		this.thePet.motionZ = MathHelper.clamp_float((float)this.thePet.motionZ, -maxVal, maxVal);
		
		this.thePet.getLookHelper().setLookPosition(x+0.5F, y+0.5F, z+0.5F, 180, (float)this.thePet.getVerticalFaceSpeed());
		
		thePet.moveForward = 0.5F;
		
		thePet.moveEntity(thePet.motionX, thePet.motionY, thePet.motionZ);
		
		return true;
    }
    
    
}
