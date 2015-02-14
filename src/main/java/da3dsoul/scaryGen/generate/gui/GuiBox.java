package da3dsoul.scaryGen.generate.gui;

import net.minecraft.client.gui.GuiScreen;

public class GuiBox {

	public int height;
	public int width;
	public int startX;
	public int startY;
	public int endX;
	public int endY;
	public GuiScreen parentScreen;

	public GuiBox(GuiScreen parent)
	{
		parentScreen = parent;
	}
	
	public static GuiBox fromEndPoints(GuiScreen parent, int sX, int sY, int eX, int eY) {
		GuiBox to = new GuiBox(parent);
		to.startX = sX;
		to.startY = sY;
		to.endX = eX;
		to.endY = eY;
		to.width = eX - sX;
		to.height = eY - sY;
		return to;
	}
	
	public static GuiBox fromStartAndDims(GuiScreen parent, int sX, int sY, int W, int H) {
		GuiBox to = new GuiBox(parent);
		to.startX = sX;
		to.startY = sY;
		to.endX = sX + W;
		to.endY = sY + H;
		to.width = W;
		to.height = H;
		return to;
	}
	
	public static GuiBox fromEndAndDims(GuiScreen parent, int eX, int eY, int W, int H) {
		GuiBox to = new GuiBox(parent);
		to.endX = eX;
		to.endY = eY;
		to.width = W;
		to.height = H;
		to.startX = eX - W;
		to.startY = eY - H;
		return to;
	}
	
	public static GuiBox fromCenterAndDims(GuiScreen parent, int cX, int cY, int W, int H) {
		GuiBox to = new GuiBox(parent);
		to.startX = cX - W / 2;
		to.startY = cY - H / 2;
		to.endX = cX + W / 2;
		to.endY = cY + H / 2;
		to.width = W;
		to.height = H;
		return to;
	}
	
	public static GuiBox atCenterOfGui(GuiScreen parent, int W, int H) {
		int cX = parent.width / 2;
		int cY = parent.height / 2;
		return fromCenterAndDims(parent, cX, cY, W, H);
	}
	
	public static GuiBox insetEven(GuiBox parent, int inset)
	{
		return fromCenterAndDims(parent.parentScreen, parent.getCenterX(), parent.getCenterY(), parent.width - inset, parent.height - inset);
	}
	
	public static GuiBox outsetEven(GuiBox parent, int outset)
	{
		return fromCenterAndDims(parent.parentScreen, parent.getCenterX(), parent.getCenterY(), parent.width + outset, parent.height + outset);
	}
	
	public GuiBox insetEven(int inset)
	{
		return fromCenterAndDims(parentScreen, getCenterX(), getCenterY(), width - inset, height - inset);
	}
	
	public GuiBox outsetEven(int outset)
	{
		return fromCenterAndDims(parentScreen, getCenterX(), getCenterY(), width + outset, height + outset);
	}
	
	public static GuiBox insetHalved(GuiBox parent, int inset)
	{
		return fromCenterAndDims(parent.parentScreen, parent.getCenterX(), parent.getCenterY(), parent.width - inset / 2, parent.height - inset / 2);
	}
	
	public static GuiBox outsetHalved(GuiBox parent, int outset)
	{
		return fromCenterAndDims(parent.parentScreen, parent.getCenterX(), parent.getCenterY(), parent.width + outset / 2, parent.height + outset / 2);
	}
	
	public GuiBox insetHalved(int inset)
	{
		return fromCenterAndDims(parentScreen, getCenterX(), getCenterY(), width - inset / 2, height - inset / 2);
	}
	
	public GuiBox outsetHalved(int outset)
	{
		return fromCenterAndDims(parentScreen, getCenterX(), getCenterY(), width + outset / 2, height + outset / 2);
	}
	
	public GuiBox[][] getRowsAndColumnsOf(int xDivisions, int yDivisions)
	{
		GuiBox[][] out = new GuiBox[xDivisions][yDivisions];
		int newWidths = width / xDivisions;
		int newHeights = height / yDivisions;
		for(int x = 0; x < xDivisions; x++)
		{
			int sX = startX + x * newWidths;
			for(int y = 0; y < yDivisions; y++)
			{
				int sY = startY + y * newHeights;
				out[x][y] = fromStartAndDims(parentScreen, sX, sY, newWidths, newHeights);
			}
		}
		return out;
	}
	
	public int getCenterX()
	{
		return startX + width / 2;
	}
	
	public int getCenterY()
	{
		return startY + height / 2;
	}

}
