package da3dsoul.scaryGen.pathfinding_astar;

import java.util.ArrayList;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;

public class AStar_ThreadFindPath extends Thread {

	private FollowPathNavigate	parent;
	private FollowableEntity	theEntity;
	private double				destX;
	private double				destY;
	private double				destZ;
	private float				speed;

	public AStar_ThreadFindPath(FollowPathNavigate parentNavigate, FollowableEntity theEntity, double x, double y,
			double z, float speed) {
		this.setName("A*");
		setDaemon(true);
		parent = parentNavigate;
		this.theEntity = theEntity;
		destX = x;
		destY = y;
		destZ = z;
		this.speed = speed;
	}

	@Override
	public void run() {
		AStar_PathFinder finder = new AStar_PathFinder(theEntity);
		int i = MathHelper.floor_double(theEntity.posX);
		int j = MathHelper.floor_double(theEntity.posY);
		int k = MathHelper.floor_double(theEntity.posZ);
		ArrayList<ChunkCoordinates> list = finder.getWaypoints(new ChunkCoordinates(i,j,k), new ChunkCoordinates(MathHelper.floor_double(destX), MathHelper.floor_double(destY), MathHelper.floor_double(destZ)));
		if(list == null || list.isEmpty())
		{
			parent.setPath(null, 0F);
			return;
		}
		AStar_PathEntity path = new AStar_PathEntity((ChunkCoordinates[])(list).toArray(new ChunkCoordinates[0]));
		parent.setPath(path, speed);
	}

}
