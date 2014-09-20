package da3dsoul.scaryGen.pathfinding_astar;

import java.awt.Point;
import java.util.ArrayList;

import net.minecraft.util.ChunkCoordinates;

public class BresenhamsLine {
	public static ArrayList<Point> getPointsOnLine(Point p1, Point p2) {
		
		Point a = (Point) p1.clone();
		Point b = (Point) p2.clone();
		
		ArrayList<Point> pointsOnLine = new ArrayList<Point>();
		
		boolean steep = Math.abs(b.y - a.y) > Math.abs(b.x - a.x);
		
		if(steep) {
			// swap(a.x, a.y)
			int temp;
			temp = a.x;
			a.x = a.y;
			a.y = temp;
			// swap(b.x, b.y)
			temp = a.x;
			b.x = b.y;
			b.y = temp;
		}
		if(a.x > b.x) {
			// swap(a.x, b.x)
			int temp;
			temp = a.x;
			a.x = b.x;
			b.x = temp;
			// swap(a.y, b.y)
			temp = a.y;
			a.y = b.y;
			b.y = temp;
		}
		
		int deltaX = b.x - a.x;
		int deltaY = Math.abs(b.y - a.y);
		int error = deltaX/2;
		
		int yStep;
		int y = a.y;
		if (a.y < b.y) 
			yStep = 1;
		else
			yStep = -1;
		
		for(int x=a.x; x<=b.x; x++) {
			if(steep)
				pointsOnLine.add(new Point(y,x));
			else
				pointsOnLine.add(new Point(x,y));
			error = error - deltaY;
			if(error<0) {
				y = y + yStep;
				error = error + deltaX;
			}
		}
					
		return pointsOnLine;
		
//		function line(x0, x1, y0, y1)
//	     boolean steep := abs(y1 - y0) > abs(x1 - a.x)
//	     if steep then
//	         swap(a.x, y0)
//	         swap(x1, y1)
//	     if a.x > x1 then
//	         swap(a.x, x1)
//	         swap(y0, y1)
//	     int deltax := x1 - a.x
//	     int deltay := abs(y1 - y0)
//	     int error := deltax / 2
//	     int ystep
//	     int y := y0
//	     if y0 < y1 then ystep := 1 else ystep := -1
//	     for x from a.x to b.x
//	         if steep then plot(y,x) else plot(x,y)
//	         error := error - deltay
//	         if error < 0 then
//	             y := y + ystep
//	             error := error + deltax

	}
	
	public static ArrayList<ChunkCoordinates> getPointsOnLine3D(ChunkCoordinates a, ChunkCoordinates b) {
        return getPointsOnLine3D(a.posX, a.posY, a.posZ, b.posX, b.posY, b.posZ);
    }

    /**
     * Generates a 3D Bresenham line between the given coordinates.
     *
     * @param startx
     * @param starty
     * @param startz
     * @param endx
     * @param endy
     * @param endz
     * @return
     */
    public static ArrayList<ChunkCoordinates> getPointsOnLine3D(int startx, int starty, int startz, int endx, int endy, int endz) {
    	ArrayList<ChunkCoordinates> result = new ArrayList<ChunkCoordinates>();

        int dx = endx - startx;
        int dy = endy - starty;
        int dz = endz - startz;

        int ax = Math.abs(dx) << 1;
        int ay = Math.abs(dy) << 1;
        int az = Math.abs(dz) << 1;

        int signx = (int) Math.signum(dx);
        int signy = (int) Math.signum(dy);
        int signz = (int) Math.signum(dz);

        int x = startx;
        int y = starty;
        int z = startz;

        int deltax, deltay, deltaz;
        if (ax >= Math.max(ay, az)) /* x dominant */ {
            deltay = ay - (ax >> 1);
            deltaz = az - (ax >> 1);
            while (true) {
                result.add(new ChunkCoordinates(x, y, z));
                if (x == endx) {
                    return result;
                }

                if (deltay >= 0) {
                    y += signy;
                    deltay -= ax;
                }

                if (deltaz >= 0) {
                    z += signz;
                    deltaz -= ax;
                }

                x += signx;
                deltay += ay;
                deltaz += az;
            }
        } else if (ay >= Math.max(ax, az)) /* y dominant */ {
            deltax = ax - (ay >> 1);
            deltaz = az - (ay >> 1);
            while (true) {
                result.add(new ChunkCoordinates(x, y, z));
                if (y == endy) {
                    return result;
                }

                if (deltax >= 0) {
                    x += signx;
                    deltax -= ay;
                }

                if (deltaz >= 0) {
                    z += signz;
                    deltaz -= ay;
                }

                y += signy;
                deltax += ax;
                deltaz += az;
            }
        } else if (az >= Math.max(ax, ay)) /* z dominant */ {
            deltax = ax - (az >> 1);
            deltay = ay - (az >> 1);
            while (true) {
                result.add(new ChunkCoordinates(x, y, z));
                if (z == endz) {
                    return result;
                }

                if (deltax >= 0) {
                    x += signx;
                    deltax -= az;
                }

                if (deltay >= 0) {
                    y += signy;
                    deltay -= az;
                }

                z += signz;
                deltax += ax;
                deltay += ay;
            }
        }
        return result;
    }
	
}