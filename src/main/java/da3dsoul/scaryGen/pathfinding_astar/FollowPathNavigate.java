package da3dsoul.scaryGen.pathfinding_astar;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.util.*;
import net.minecraft.block.Block;

public class FollowPathNavigate
{
	private FollowableEntity theEntity;
	private World worldObj;

	/** The PathEntity being followed. */
	private AStar_PathEntity currentPath;
	private float speed;

	/**
	 * The number of blocks (extra) +/- in each axis that get pulled out as cache for the pathfinder's search space
	 */
	@SuppressWarnings("unused")
	private float pathSearchRange;
	@SuppressWarnings("unused")
	private boolean noSunPathfind = false;

	/** Time, in number of ticks, following the current path */
	private int totalTicks;

	/**
	 * The time when the last position check was done (to detect successful movement)
	 */
	@SuppressWarnings("unused")
	private int ticksAtLastPos;

	/**
	 * Coordinates of the entity's position last time a check was done (part of monitoring getting 'stuck')
	 */
	private Vec3 lastPosCheck = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);

	/**
	 * Specifically, if a wooden door block is even considered to be passable by the pathfinder
	 */
	@SuppressWarnings("unused")
	private boolean canPassOpenWoodenDoors = true;

	/** If door blocks are considered passable even when closed */
	private boolean canPassClosedWoodenDoors = false;

	/** If water blocks are avoided (at least by the pathfinder) */
	private boolean avoidsWater = false;

	/**
	 * If the entity can swim. Swimming AI enables this and the pathfinder will also cause the entity to swim straight
	 * upwards when underwater
	 */
	@SuppressWarnings("unused")
	private boolean canSwim = false;
	@SuppressWarnings("unused")
	private boolean canFly = false;
	
	public FollowPathNavigate(FollowableEntity par1EntityLiving, World par2World, float par3)
	{
		this.theEntity = par1EntityLiving;
		this.worldObj = par2World;
		this.pathSearchRange = par3;
	}

	public void setAvoidsWater(boolean par1)
	{
		this.avoidsWater = par1;
	}

	public boolean getAvoidsWater()
	{
		return this.avoidsWater;
	}

	public void setBreakDoors(boolean par1)
	{
		this.canPassClosedWoodenDoors = par1;
	}

	/**
	 * Sets if the entity can enter open doors
	 */
	 public void setEnterDoors(boolean par1)
	 {
		 this.canPassOpenWoodenDoors = par1;
	 }

	 /**
	  * Returns true if the entity can break doors, false otherwise
	  */
	 public boolean getCanBreakDoors()
	 {
		 return this.canPassClosedWoodenDoors;
	 }

	 /**
	  * Sets if the path should avoid sunlight
	  */
	 public void setAvoidSun(boolean par1)
	 {
		 this.noSunPathfind = par1;
	 }

	 /**
	  * Sets the speed
	  */
	 public void setSpeed(float par1)
	 {
		 this.speed = par1;
	 }

	 /**
	  * Sets if the entity can swim
	  */
	 public void setCanSwim(boolean par1)
	 {
		 this.canSwim = par1;
	 }

	 public void setCanFly(boolean par1)
	 {
		 this.canFly = par1;
	 }

	 /**
	  * Try to find and set a path to XYZ. Returns true if successful.
	  */
	 public void tryMoveToXYZ(double x, double y, double z, float speed)
	 {
		 runPathFindToXYZ(x, y, z, speed);
	 }
	 
	 private void runPathFindToXYZ(double destX, double destY, double destZ, float speed)
	 {
		 AStar_ThreadFindPath thread = new AStar_ThreadFindPath(this, theEntity, destX, destY, destZ, speed);
		 thread.start();
	 }

	 /**
	  * Try to find and set a path to EntityLiving. Returns true if successful.
	  */
	 public void tryMoveToEntityLiving(Entity par1EntityLiving, float par2)
	 {
		 runPathFindToXYZ(par1EntityLiving.posX, par1EntityLiving.posY + 0.5D, par1EntityLiving.posZ, par2);
	 }

	 /**
	  * sets the active path data if path is 100% unique compared to old path, checks to adjust path for sun avoiding
	  * ents and stores end coords
	  */
	 public boolean setPath(AStar_PathEntity par1PathEntity, float par2)
	 {
		 if (par1PathEntity == null)
		 {
			 this.currentPath = null;
			 return false;
		 }
		 else
		 {
			 if (!par1PathEntity.isSamePath(this.currentPath))
			 {
				 this.currentPath = par1PathEntity;
			 }

			 if (this.currentPath.getCurrentPathLength() == 0)
			 {
				 return false;
			 }
			 else
			 {
				 this.speed = par2;
				 Vec3 var3 = this.getEntityPosition();
				 this.ticksAtLastPos = this.totalTicks;
				 this.lastPosCheck.xCoord = var3.xCoord;
				 this.lastPosCheck.yCoord = var3.yCoord;
				 this.lastPosCheck.zCoord = var3.zCoord;
				 return true;
			 }
		 }
	 }

	 /**
	  * gets the actively used PathEntity
	  */
	 public AStar_PathEntity getPath()
	 {
		 return this.currentPath;
	 }

	 public void onUpdateNavigation()
	 {
		 ++this.totalTicks;

		 if (!this.noPath())
		 {
			 if (this.canNavigate())
			 {
				 this.pathFollow();
			 }

			 if (!this.noPath())
			 {
				 Vec3 var1 = this.currentPath.getPosition(this.theEntity);

				 if (var1 != null)
				 {
					 //Minecraft.getMinecraft().getLogAgent().logInfo("Next Path Coord:" + var1.xCoord + ", " + var1.yCoord + ", " + var1.zCoord);
					 this.theEntity.getMoveHelper().setMoveTo(var1.xCoord, var1.yCoord, var1.zCoord, this.speed);
				 }
			 }
		 }
	 }

	 private void pathFollow()
	 {
		 Vec3 var1 = this.getEntityPosition();
		 int var2 = this.currentPath.getCurrentPathLength();
		 if(true)
		 {
			 for (int var3 = this.currentPath.getCurrentPathIndex(); var3 < this.currentPath.getCurrentPathLength(); ++var3)
			 {
				 if (this.currentPath.getChunkCoordinatesFromIndex(var3).posY != (int)var1.yCoord)
				 {
					 var2 = var3;
					 break;
				 }
			 }
		 }

		 float var8 = this.theEntity.width * 2;
		 int var4;

		 for (var4 = this.currentPath.getCurrentPathIndex(); var4 < var2; ++var4)
		 {
			 if (var1.squareDistanceTo(this.currentPath.getVectorFromIndex(this.theEntity, var4)) < (double)var8)
			 {
				 this.currentPath.setCurrentPathIndex(var4 + 1);
			 }
		 }
	 }

	 /**
	  * If null path or reached the end
	  */
	 public boolean noPath()
	 {
		 return this.currentPath == null || this.currentPath.isFinished();
	 }

	 /**
	  * sets active PathEntity to null
	  */
	 public void clearPathEntity()
	 {
		 this.currentPath = null;
	 }

	 private Vec3 getEntityPosition()
	 {
		 return Vec3.createVectorHelper(this.theEntity.posX, this.theEntity.posY, this.theEntity.posZ);
	 }


	 /**
	  * If on ground or swimming and can swim
	  */
	 private boolean canNavigate()
	 {
		 return true;
	 }

	 /**
	  * Returns true if the entity is in water or lava, false otherwise
	  */
	 @SuppressWarnings("unused")
	private boolean isInFluid()
	 {
		 return this.theEntity.isInWater() || this.theEntity.handleLavaMovement();
	 }


	 /**
	  * Returns true if an entity does not collide with any solid blocks at the position. Args: xOffset, yOffset,
	  * zOffset, entityXSize, entityYSize, entityZSize, originPosition, vecX, vecZ
	  */
	 @SuppressWarnings("unused")
	private boolean isPositionClear(int par1, int par2, int par3, int par4, int par5, int par6, Vec3 par7Vec3, double par8, double par10)
	 {
		 for (int var12 = par1; var12 < par1 + par4; ++var12)
		 {
			 for (int var13 = par2; var13 < par2 + par5; ++var13)
			 {
				 for (int var14 = par3; var14 < par3 + par6; ++var14)
				 {
					 double var15 = (double)var12 + 0.5D - par7Vec3.xCoord;
					 double var17 = (double)var14 + 0.5D - par7Vec3.zCoord;

					 if (var15 * par8 + var17 * par10 >= 0.0D)
					 {
						 Block var19 = this.worldObj.getBlock(var12, var13, var14);

						 if (var19 != null && !var19.getBlocksMovement(this.worldObj, var12, var13, var14))
						 {
							 return false;
						 }
					 }
				 }
			 }
		 }

		 return true;
	 }
}
