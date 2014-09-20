
package da3dsoul.scaryGen.pathfinding_astar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.nbt.*;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;


// Referenced classes of package net.minecraft.src:
//            Entity

@SuppressWarnings("unused")
public class FollowableEntity extends EntityItem implements Comparable<FollowableEntity>
{

	public FollowableEntity(World par1World, boolean legacy) {
		super(par1World);
		useLegacy = legacy;
		if(!legacy)
		{
			this.moveHelper = new FollowEntityMoveHelper(this);
			this.navigator = new FollowPathNavigate(this, par1World, (float)128);
			setAIMoveSpeed(1.75F);
		}
	}

	public FollowableEntity(World par1World) {
		super(par1World);
		useLegacy = false;
		this.moveHelper = new FollowEntityMoveHelper(this);
		this.navigator = new FollowPathNavigate(this, par1World, (float)128);
		setAIMoveSpeed(1.75F);
	}
	
	public FollowableEntity(EntityItem item)
	{
		this(item.worldObj);
		this.setEntityItemStack(item.getEntityItem());
		this.lifespan = item.lifespan;
		this.setPosition(item.posX, item.posY, item.posZ);
		this.rotationYaw = item.rotationYaw;
	}

	protected boolean follows;

	protected Entity followTarget;

	protected FollowableEntity followingEntity;

	protected float moveForward;

	protected FollowEntityMoveHelper moveHelper;

	protected FollowPathNavigate navigator;

	private float AIMoveSpeed;

	public float intensivePurposeYaw;
	public float intensivePurposePitch;

	private int ticksUntilSearch = 0;
	
	private boolean canMove;

	public float getAIMoveSpeed()
	{
		return this.AIMoveSpeed;
	}

	public boolean getFollows()
	{
		return follows;
	}
	public Entity getFollowTarget()
	{
		return followTarget;
	}

	public FollowEntityMoveHelper getMoveHelper()
	{
		return this.moveHelper;
	}

	public FollowPathNavigate getNavigator()
	{
		return this.navigator;
	}

	public double getPosX() {
		return posX;
	}
	public double getPosY() {
		return posY;
	}
	public double getPosZ() {
		return posZ;
	}

	public boolean useLegacy = false;

	public Random getRNG()
	{
		return this.rand;
	}

	public float getSpeedModifier()
	{
		return 1;
	}

	public void onUpdate()
	{
		canMove = !follows;
		super.onUpdate();
		canMove = true;
		if(useLegacy)
		{
			updateLegacy();
		}else
		{
			updateFollowing();
		}
	}
	
	@Override
	public void moveEntity(double x, double y, double z)
	{
		
		if(!canMove)
		{
			motionX = motionY = motionZ = 0;
			return;
		}
		
		this.worldObj.theProfiler.startSection("move");
        this.ySize *= 0.4F;

        if (this.isInWeb)
        {
            this.isInWeb = false;
            x *= 0.25D;
            y *= 0.05000000074505806D;
            z *= 0.25D;
            this.motionX = 0.0D;
            this.motionY = 0.0D;
            this.motionZ = 0.0D;
        }

        double tMotionX = x;
        double tMotionY = y;
        double tMotionZ = z;

        List list = this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox.addCoord(x, y, z));

        for (int i = 0; i < list.size(); ++i)
        {
            y = ((AxisAlignedBB)list.get(i)).calculateYOffset(this.boundingBox, y);
        }

        this.boundingBox.offset(0.0D, y, 0.0D);

        if (!this.field_70135_K && tMotionY != y)
        {
            z = 0.0D;
            y = 0.0D;
            x = 0.0D;
        }

        int j;

        for (j = 0; j < list.size(); ++j)
        {
            x = ((AxisAlignedBB)list.get(j)).calculateXOffset(this.boundingBox, x);
        }

        this.boundingBox.offset(x, 0.0D, 0.0D);

        if (!this.field_70135_K && tMotionX != x)
        {
            z = 0.0D;
            y = 0.0D;
            x = 0.0D;
        }

        for (j = 0; j < list.size(); ++j)
        {
            z = ((AxisAlignedBB)list.get(j)).calculateZOffset(this.boundingBox, z);
        }

        this.boundingBox.offset(0.0D, 0.0D, z);

        if (!this.field_70135_K && tMotionZ != z)
        {
            z = 0.0D;
            y = 0.0D;
            x = 0.0D;
        }

        this.worldObj.theProfiler.endSection();
        this.worldObj.theProfiler.startSection("rest");
        this.posX = (this.boundingBox.minX + this.boundingBox.maxX) / 2.0D;
        this.posY = this.boundingBox.minY + (double)this.yOffset - (double)this.ySize;
        this.posZ = (this.boundingBox.minZ + this.boundingBox.maxZ) / 2.0D;
        this.isCollidedHorizontally = tMotionX != x || tMotionZ != z;
        this.isCollidedVertically = tMotionY != y;
        this.onGround = tMotionY != y && tMotionY < 0.0D;
        this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
        this.updateFallState(y, this.onGround);

        if (tMotionX != x)
        {
            this.motionX = 0.0D;
        }

        if (tMotionY != y)
        {
            this.motionY = 0.0D;
        }

        if (tMotionZ != z)
        {
            this.motionZ = 0.0D;
        }
        
        try
        {
            this.func_145775_I();
        }
        catch (Throwable throwable)
        {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Entity being checked for collision");
            this.addEntityCrashInfo(crashreportcategory);
            throw new ReportedException(crashreport);
        }

        this.worldObj.theProfiler.endSection();
	}

	/**
	 * set the movespeed used for the new AI system
	 */
	public void setAIMoveSpeed(float par1)
	{
		this.AIMoveSpeed = par1;
		this.setMoveForward(par1);
	}

	public void setFollows(boolean flag)
	{
		follows = flag;
	}

	public void setFollowTarget(Entity entity)
	{
		if(entity instanceof FollowableEntity)
		{
			((FollowableEntity)entity).followingEntity = this;
		}
		followTarget = entity;
	}

	public void setMoveForward(float par1)
	{
		this.moveForward = par1;
	}


	protected void entityInit() {
		super.entityInit();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound var1) {
		super.readEntityFromNBT(var1);

	}

	protected void updateFollowing()
	{
		if(followTarget != null) follows = true;
		if(follows)
		{
			if(followTarget != null)
			{
				if(followTarget instanceof EntityPlayer && ticksUntilSearch == 0)
				{
					@SuppressWarnings("unchecked")
					ArrayList<FollowableEntity> list = (ArrayList) worldObj.getEntitiesWithinAABB(da3dsoul.scaryGen.pathfinding_astar.FollowableEntity.class, AxisAlignedBB.getBoundingBox(followTarget.posX - 0.5D, followTarget.posY - 0.5D, followTarget.posZ - 0.5D, followTarget.posX + 0.5D, followTarget.posY + 0.5D, followTarget.posZ + 0.5D).expand(60D, 64D, 60D));
					ListIterator<FollowableEntity> it = list.listIterator();
					do
					{
						if(!it.hasNext()) break;
						FollowableEntity ent = it.next();
						if(ent == null || ent.followTarget == null)
						{
							it.remove();
							continue;
						}
						if(!ent.follows || !(ent.followTarget == followTarget))
						{
							it.remove();
							continue;
						}
					}while(true);
					Collections.sort(list);
					FollowableEntity prev = null;
					it = list.listIterator();
					boolean a = false;
					do
					{
						if(!it.hasNext()) break;
						FollowableEntity ent = it.next();
						if(ent == null || ent.followTarget == null) continue;
						if(!ent.follows || !(ent.followTarget == followTarget)) continue;
						if(a == false)
						{
							prev = ent;
							a = true;
							continue;
						}
						ent.followTarget = prev;
					}while(true);

					ticksUntilSearch = 400;
				}
				if(this.getDistanceSqToEntity(followTarget) > 0.5)
				{
					if(ticksUntilSearch == 0 || navigator.getPath() == null)
					{
						this.navigator.tryMoveToEntityLiving(followTarget, this.getAIMoveSpeed());
						ticksUntilSearch  = 400;
					}
					this.navigator.onUpdateNavigation();

					//this.moveEntityWithHeading(0F, this.moveForward);
					Vec3 vec3 = moveHelper.getDestination();

					double d = vec3.xCoord - posX;
					double d1 = vec3.yCoord - posY;
					double d2 = vec3.zCoord - posZ;
					Vec3 vec = Vec3.createVectorHelper(d, d1, d2);
					vec = vec.normalize();
					this.motionX = vec.xCoord * this.AIMoveSpeed / 20;
					this.motionY = vec.yCoord * this.AIMoveSpeed / 20;
					this.motionZ = vec.zCoord * this.AIMoveSpeed / 20;
					if(isCollidedHorizontally)
					{
						if(motionY > 0)
						{
							motionY += 0.5;
						}else if(motionY < 0)
						{
							motionY -= 0.5;
						}
					}
					float maxVal = 3.25F;
					motionX = MathHelper.clamp_float((float)motionX, -maxVal, maxVal);
					motionY = MathHelper.clamp_float((float)motionY, -maxVal, maxVal);
					motionZ = MathHelper.clamp_float((float)motionZ, -maxVal, maxVal);
					this.moveEntity(motionX, motionY, motionZ);
					if(ticksUntilSearch > 0) ticksUntilSearch--;

				}else
				{
					motionX = motionY = motionZ = 0;
				}
				motionX *= 0.8;
				motionY *= 0.8;
				motionZ *= 0.8;
				if(isClose(motionX, 0, 0.001F))
				{
					motionX = 0;
				}
				if(isClose(motionY, 0, 0.001F))
				{
					motionY = 0;
				}
				if(isClose(motionZ, 0, 0.001F))
				{
					motionZ = 0;
				}

			} else
			{
				follows = false;
			}
		} else
		{
			followTarget = null;
		}
	}

	protected void updateLegacy()
	{

		if(follows)
		{
			if(followTarget != null)
			{
				double followMotionXModifier = 0;
				double followMotionZModifier = 0;
				if(!isCollided)
				{
					motionY -= 0.080000000000000003D;
				}
				else
					if(!onGround && !isCollidedHorizontally && isCollidedVertically)
					{
						motionY = -0.3D;
						motionX = -followMotionXModifier * 6D;
						motionZ = -followMotionZModifier * 6D;
					} else
						if(isCollidedHorizontally && !isCollidedVertically)
						{
							motionY += 0.059999999999999998D;
							motionX = -followMotionXModifier * 6D;
							motionZ = -followMotionZModifier * 6D;
						} else
							if(!onGround && isCollidedHorizontally && isCollidedVertically)
								motionY -= 0.059999999999999998D;

				double d = 1024D;
				double d1 = (followTarget.posX - posX) / d;
				double d2 = ((followTarget.boundingBox.minY + 0.23000000000000001D) - posY) / d;
				double d3 = (followTarget.posZ - posZ) / d;
				double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
				double d5 = 1.0D - d4;
				if(d5 >= 0.0D)
				{
					d5 *= d5;
					followMotionXModifier = (d1 / d4) * this.AIMoveSpeed * 0.10000000000000001D;
					followMotionZModifier = (d3 / d4) * this.AIMoveSpeed * 0.10000000000000001D;
					motionX += followMotionXModifier;
					motionY += (d2 / d4) * 2D * 0.10000000000000001D;
					motionZ += followMotionZModifier;
				}
				this.func_145771_j(posX, posY, posZ);
				moveEntity(motionX, motionY, motionZ);
			} else
			{
				follows = false;
			}
		} else
		{
			followTarget = null;
		}
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound var1) {
		super.writeEntityToNBT(var1);

	}

	private boolean isClose(double d, double d1, float epsilon)
	{
		double d2 = d - d1;
		if(d2 < 0) d2 *= -1;
		if(d2 <= epsilon) return true;
		return false;
	}

	@Override
	public int compareTo(FollowableEntity otherEntity) {
		if(this.getDistanceSqToEntity(followTarget) < otherEntity.getDistanceSqToEntity(followTarget)) return -1;
		if(this.getDistanceSqToEntity(followTarget) > otherEntity.getDistanceSqToEntity(followTarget)) return 1;
		if(this.age > otherEntity.age) return 1;
		return 0;
	}

	public boolean doesCollideWithPlayer(EntityPlayer par1EntityPlayer){return false;}

	public void setDead()
	{
		//super.setDead();
		if(followTarget != null)
		{
			if(followingEntity != null)
				followingEntity.setFollowTarget(followTarget);
		}
		this.isDead = true;
	}


}
