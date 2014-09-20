package da3dsoul.scaryGen.pathfinding_astar;


import java.util.ArrayList;

import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class AStar_PathFinder {

	World worldObj;
	
	private ChunkCoordinates start;
	private ChunkCoordinates goal;
	private AStar aStar;
	
	public AStar_PathFinder(FollowableEntity entity)
	{
		worldObj = entity.worldObj;
		start = new ChunkCoordinates(MathHelper.floor_double(entity.posX), MathHelper.floor_double(entity.posY), MathHelper.floor_double(entity.posZ));
		DiagonalHeuristic heuristic = new DiagonalHeuristic();
		aStar = new AStar(worldObj, heuristic, this);
	}
	
	public ArrayList<ChunkCoordinates> getWaypoints(ChunkCoordinates start, ChunkCoordinates goal) 
	{
		this.start = start;
		this.goal = goal;
		ArrayList<ChunkCoordinates> shortestPath = aStar.calcShortestPath(start, goal);
		if(shortestPath == null || shortestPath.isEmpty())
		{
			//Minecraft.getMinecraft().getLogAgent().logInfo("Fuck This Shit I'm goin home.");
			return null;
		}
		ArrayList<ChunkCoordinates> waypoints = calculateWayPoints(shortestPath);
		if(waypoints == null || waypoints.isEmpty())
		{
			//Minecraft.getMinecraft().getLogAgent().logInfo("Fuck This Shit I'm goin home.");
			return null;
		}
		return waypoints;
	}
	
	private ArrayList<ChunkCoordinates> calculateWayPoints(ArrayList<ChunkCoordinates> shortestPath) {
		ArrayList<ChunkCoordinates> waypoints = new ArrayList<ChunkCoordinates>();
		
		shortestPath.add(0,start);
		shortestPath.add(goal);
		
		ChunkCoordinates p1 = shortestPath.get(0);
		int p1Number = 0;
		waypoints.add(p1);
		
		ChunkCoordinates p2 = shortestPath.get(1);
		int p2Number = 1;
		
		while(!p2.equals(shortestPath.get(shortestPath.size()-1))) {
			if(lineClear(p1, p2)) {
				//make p2 the next point in the path
				p2Number++;
				p2 = shortestPath.get(p2Number);
			} else {
				p1Number = p2Number-1;
				p1 = shortestPath.get(p1Number);
				waypoints.add(p1);
				p2Number++;
				p2 = shortestPath.get(p2Number);
			}
		}
		waypoints.add(p2);
		
		return waypoints;
	}
	
	private boolean lineClear(ChunkCoordinates a, ChunkCoordinates b) {
		ArrayList<ChunkCoordinates> pointsOnLine = BresenhamsLine.getPointsOnLine3D(a, b);
		for(ChunkCoordinates p : pointsOnLine) {
			if(aStar.isObstacle(p)) {
				return false;
			}
		}
		return true;
	}
}
