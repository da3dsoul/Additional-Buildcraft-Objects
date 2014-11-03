package da3dsoul.scaryGen.pathfinding_astar;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

public interface IFollowable {
	
	public float getAIMoveSpeed();
	public boolean getFollows();
	public Entity getFollowTarget();

	public FollowPathNavigate getNavigator(boolean nil);
	
	public Vec3 getDest();
	
	public void setDest(Vec3 vec);

	public double getPosX();
	public double getPosY();
	public double getPosZ();
	
	public void setAIMoveSpeed(float par1);

	public void setFollows(boolean flag);

	public void setFollowTarget(Entity entity);

	public void setMoveForward(float par1);
	
	public boolean doesCollideWithPlayer(EntityPlayer par1EntityPlayer);

}
