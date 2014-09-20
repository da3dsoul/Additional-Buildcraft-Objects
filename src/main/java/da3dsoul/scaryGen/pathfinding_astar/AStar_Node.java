package da3dsoul.scaryGen.pathfinding_astar;

import java.util.ArrayList;
import net.minecraft.util.*;

public class AStar_Node implements Comparable<AStar_Node> {
	/* Nodes that this is connected to */
	float distanceFromStart;
	float heuristicDistanceFromGoal;
	private AStar_Node previousNode;
	private int x;
	private int y;
	private int z;
	
	private AStar aStar;
	
	AStar_Node(AStar astar, int x, int y, int z) {
		aStar = astar;
		this.x = x;
		this.y = y;
		this.z = z;
		this.distanceFromStart = Integer.MAX_VALUE;
	}
	
	AStar_Node(AStar astar, ChunkCoordinates c) {
		aStar = astar;
		this.x = c.posX;
		this.y = c.posY;
		this.z = c.posZ;
		this.distanceFromStart = Integer.MAX_VALUE;
	}
	
	AStar_Node (AStar astar, int x, int y, int z, int distanceFromStart, boolean isObstical, boolean isStart, boolean isGoal) {
		aStar = astar;
		this.x = x;
		this.y = y;
		this.z = z;
		this.distanceFromStart = distanceFromStart;
	}
	
	public ArrayList<AStar_Node> getNeighborList() {
		ArrayList<AStar_Node> neighborList = new ArrayList<AStar_Node>();
		for(int c = 0; c < 6; c++)
		{
			int i = 0;
			int j = 0;
			int k = 0;
			switch(c)
			{
			case 0: j = -1; break;
			case 1: j = 1; break;
			case 2: i = -1; break;
			case 3: i = 1; break;
			case 4: k = -1; break;
			case 5: k = 1;
			}
					if(isInBounds(i,j,k)) neighborList.add(new AStar_Node(aStar, i + x, j + y, k + z));

		}
		return neighborList;
	}
	
	private boolean isInBounds(int i, int j, int k)
	{
		if(i + x < Math.min(aStar.currentGoal.posX, aStar.currentStart.posX) - aStar.range) return false;
		if(j + y < Math.min(aStar.currentGoal.posY, aStar.currentStart.posY) - aStar.range) return false;
		if(k + z < Math.min(aStar.currentGoal.posZ, aStar.currentStart.posZ) - aStar.range) return false;
		if(i + x > Math.max(aStar.currentGoal.posX, aStar.currentStart.posX) + aStar.range) return false;
		if(j + y > Math.max(aStar.currentGoal.posY, aStar.currentStart.posY) + aStar.range) return false;
		if(k + z > Math.max(aStar.currentGoal.posZ, aStar.currentStart.posZ) + aStar.range) return false;
		return true;
	}

	public float getDistanceFromStart() {
		return distanceFromStart;
	}

	public void setDistanceFromStart(float f) {
		this.distanceFromStart = f;
	}

	public AStar_Node getPreviousNode() {
		return previousNode;
	}

	public void setPreviousNode(AStar_Node previousNode) {
		this.previousNode = previousNode;
	}
	
	public float getHeuristicDistanceFromGoal() {
		return heuristicDistanceFromGoal;
	}

	public void setHeuristicDistanceFromGoal(float heuristicDistanceFromGoal) {
		this.heuristicDistanceFromGoal = heuristicDistanceFromGoal;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}
	
	public ChunkCoordinates getPoint() {
		return new ChunkCoordinates(x,y,z);
	}

	public boolean equals(Object obj) {
		if(obj instanceof AStar_Node)
		{
			AStar_Node aStar_Node = (AStar_Node)obj;
			if(aStar_Node.x != this.x) return false;
			if(aStar_Node.y != this.y) return false;
			if(aStar_Node.z != this.z) return false;
			return true;
		}else if(obj instanceof ChunkCoordinates)
		{
			ChunkCoordinates c = (ChunkCoordinates)obj;
			if(c.posX != this.x) return false;
			if(c.posY != this.y) return false;
			if(c.posZ != this.z) return false;
			return true;
		}else
		{
			return false;
		}
	}

	public int compareTo(AStar_Node otherNode) {
		float thisTotalDistanceFromGoal = heuristicDistanceFromGoal + distanceFromStart;
		float otherTotalDistanceFromGoal = otherNode.getHeuristicDistanceFromGoal() + otherNode.getDistanceFromStart();
		
		if (thisTotalDistanceFromGoal < otherTotalDistanceFromGoal) {
			return -1;
		} else if (thisTotalDistanceFromGoal > otherTotalDistanceFromGoal) {
			return 1;
		} else {
			return 0;
		}
	}
	
	
	
	@Override
	public int hashCode() {
		return this.x + this.z << 8 + this.y << 16;
	}

	public static double calcEuclidianDistance(AStar_Node node1, AStar_Node node2)
	{
		return Math.sqrt(Math.pow(node1.x - node2.x, 2) + Math.pow(node1.y - node2.y, 2) + Math.pow(node1.z - node2.z, 2));
	}
	
	public static double calcEuclidianDistance(int x1, int y1, int z1, int x2, int y2, int z2)
	{
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2) + Math.pow(z1 - z2, 2));
	}
}