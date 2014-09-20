package da3dsoul.scaryGen.pathfinding_astar;

import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockTrapDoor;


public class AStar {
	private World worldObj;
	private DiagonalHeuristic heuristic;
	/**
	 * closedList The list of Nodes not searched yet, sorted by their distance to the goal as guessed by our heuristic.
	 */
	private ArrayList<AStar_Node> closedList;
	private SortedNodeList openList;
	private ArrayList<ChunkCoordinates> shortestPath;
	@SuppressWarnings("unused")
	private AStar_PathFinder finder;
	
	public ChunkCoordinates currentGoal;
	public ChunkCoordinates currentStart;
	public int range;
	

	public AStar(World world, DiagonalHeuristic heuristic, AStar_PathFinder find) {
		worldObj = world;
		finder = find;
		this.heuristic = heuristic;
		closedList = new ArrayList<AStar_Node>();
		openList = new SortedNodeList();
		range = (int) Math.round(Minecraft.getMinecraft().gameSettings.renderDistanceChunks * 3.5);
	}

	public ArrayList<ChunkCoordinates> calcShortestPath(ChunkCoordinates start, ChunkCoordinates goal) {
		//this.startX = startX;
		//this.startY = startY;
		//this.goalX = goalX;
		//this.goalY = goalY;

		//mark start and goal node

		//Check if the goal node is also an obstacle (if it is, it is impossible to find a path there)
		if (isObstacle(goal)) {
			return null;
		}
		currentStart = new ChunkCoordinates(start);
		currentGoal = new ChunkCoordinates(goal);
		AStar_Node startnode = new AStar_Node(this, start);
		startnode.setDistanceFromStart(0);
		if(dist(startnode, goal) > range) return null;
		openList.clear();
		closedList.clear();
		if(shortestPath != null) shortestPath.clear();
		openList.add(startnode);
		
		int cycles = 0;

		//while we haven't reached the goal yet
		while(openList.size() != 0) {
			cycles++;
			//get the first AStar_Node from non-searched AStar_Node list, sorted by lowest distance from our goal as guessed by our heuristic
			AStar_Node current = openList.getFirst();

			//Minecraft.getMinecraft().getLogAgent().logInfo("AStar node (current): "+current.getX()+","+current.getY()+","+current.getZ());
			
			// check if our current AStar_Node location is the goal AStar_Node. If it is, we are done.
			if(current.equals(currentGoal)) {
				return reconstructPath(current);
			}
			

			//move current AStar_Node to the closed (already searched) list
			openList.remove(current);
			closedList.add(current);

			if(cycles > 2500) break;
			//go through all the current Nodes neighbors and calculate if one should be our next step
			for(AStar_Node neighbor : current.getNeighborList()) {
				boolean neighborIsBetter;

				//if we have already searched this AStar_Node, don't bother and continue to the next one 
				if (closedList.contains(neighbor))
					continue;
				if(dist(neighbor, currentGoal) > range)
				{
					closedList.add(neighbor);
					continue;
				}
				//also just continue if the neighbor is an obstacle
				if (!isObstacle(neighbor.getPoint())) {

					// calculate how long the path is if we choose this neighbor as the next step in the path 
					float neighborDistanceFromStart = (current.getDistanceFromStart() + getDistanceBetween(current, neighbor));

					//add neighbor to the open list if it is not there
					if(!openList.contains(neighbor)) {
						openList.add(neighbor);
						neighborIsBetter = true;
						//if neighbor is closer to start it could also be better
					} else if(neighborDistanceFromStart < current.getDistanceFromStart()) {
						neighborIsBetter = true;
					} else {
						neighborIsBetter = false;
					}
					//Minecraft.getMinecraft().getLogAgent().logInfo("AStar node (neighbor): "+neighbor.x+","+neighbor.y+","+neighbor.z);
					// set neighbors parameters if it is better
					if (neighborIsBetter) {
						neighbor.setPreviousNode(current);
						neighbor.setDistanceFromStart(neighborDistanceFromStart);
						neighbor.setHeuristicDistanceFromGoal(heuristic.getEstimatedDistanceToGoal(neighbor.getPoint(), currentGoal));
					}
				}else
				{
					closedList.add(neighbor);
				}

			}
		}
		return null;
	}

	private ArrayList<ChunkCoordinates> reconstructPath(AStar_Node aStar_Node) {
		ArrayList<ChunkCoordinates> path = new ArrayList<ChunkCoordinates>();
		while(!(aStar_Node.getPreviousNode() == null)) {
			path.add(0,aStar_Node.getPoint());
			aStar_Node = aStar_Node.getPreviousNode();
		}
		this.shortestPath = path;
		return path;
	}
	
	public boolean isObstacle(ChunkCoordinates c)
	{
		Block id = worldObj.getBlock(c.posX, c.posY, c.posZ);
		if(id == null) return false;
		if(!id.getMaterial().blocksMovement()) return false;
		if(!id.isCollidable()) return false;
		if(id == Blocks.iron_door || id == Blocks.wooden_door)
		{
			if(((BlockDoor)id).func_150015_f(worldObj, c.posX, c.posY, c.posZ)) return false;
		}
		if(id == Blocks.trapdoor)
		{
			if(!BlockTrapDoor.func_150118_d(worldObj.getBlockMetadata(c.posX, c.posY, c.posZ))) return false;
		}
		if(id.getMaterial().blocksMovement()) return true;
		if(id.getCollisionBoundingBoxFromPool(worldObj, c.posX, c.posY, c.posZ) == null) return false;
		return true;
	}
	
	public float getDistanceBetween(AStar_Node node1, AStar_Node node2) {
		//if the nodes are on top or next to each other, return 1
		if (node1.getX() == node2.getX() || node1.getY() == node2.getY() || node1.getZ() == node2.getZ()){
			return 1;//*(mapHeight+mapWith);
		} else { //if they are diagonal to each other return diagonal distance: sqrt(1^2+1^2)
			return (float) 1.9;//*(mapHeight+mapWith);
		}
	}
	
	private double dist(AStar_Node node, ChunkCoordinates c)
	{
		return Math.sqrt((node.getX() - c.posX) * (node.getX() - c.posX) + (node.getY() - c.posY) * (node.getY() - c.posY) + (node.getX() - c.posX) * (node.getZ() - c.posZ));
	}

	private class SortedNodeList {

		private ArrayList<AStar_Node> list = new ArrayList<AStar_Node>();

		public AStar_Node getFirst() {
			return list.get(0);
		}

		public void clear() {
			list.clear();
		}

		public void add(AStar_Node aStar_Node) {
			if(list.contains(aStar_Node)) return;
			list.add(aStar_Node);
			Collections.sort(list);
		}

		public void remove(AStar_Node n) {
			list.remove(n);
		}

		public int size() {
			return list.size();
		}

		public boolean contains(AStar_Node n) {
			return list.contains(n);
		}
	}

}
