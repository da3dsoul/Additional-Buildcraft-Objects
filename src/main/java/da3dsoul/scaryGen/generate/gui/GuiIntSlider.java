package da3dsoul.scaryGen.generate.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiIntSlider extends GuiButton
{
	public GuiScaryGenCustomize parent;
	public final String option;
    private int currentValue;
    public boolean isBeingPressed;
    private final int sliderMinValue;
    private final int sliderMaxValue;
    @SuppressWarnings("unused")
	private static final String __OBFID = "CL_00000680";

    public GuiIntSlider(GuiScaryGenCustomize parent, int id, int xPosition, int yPosition, String option, int minValue, int maxValue)
    {
        super(id, xPosition, yPosition, 150, 20, "");
        this.parent = parent;
        this.currentValue = parent.worldType.getIntegerOption(option);
        this.sliderMinValue = minValue;
        this.sliderMaxValue = maxValue;
        this.displayString = option + ": " + currentValue;
        this.option = option;
    }

    /**
     * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over
     * this button.
     */
    public int getHoverState(boolean p_146114_1_)
    {
        return 0;
    }

    /**
     * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
     */
    protected void mouseDragged(Minecraft minecraft, int xPos, int p_146119_3_)
    {
        if (this.visible)
        {
            if (this.isBeingPressed)
            {
                this.currentValue = from1((float)(xPos - (this.xPosition + 4)) / (float)(this.width - 8));

                if (this.currentValue < sliderMinValue)
                {
                    this.currentValue = sliderMinValue;
                }

                if (this.currentValue > sliderMaxValue)
                {
                    this.currentValue = sliderMaxValue;
                }

                parent.worldType.setOption(option, currentValue);
                currentValue = parent.worldType.getIntegerOption(option);
                this.displayString = option + ": " + currentValue;
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int)(to1(this.currentValue) * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int)(to1(this.currentValue) * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        }
    }

    private float to1(int currentValue) {
		return (float)((float)currentValue - sliderMinValue) / ((float)sliderMaxValue - sliderMinValue);
	}

	private int from1(float f) {
		return Math.round(f * sliderMaxValue + sliderMinValue);
	}

	/**
     * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent
     * e).
     */
    public boolean mousePressed(Minecraft minecraft, int xPos, int p_146116_3_)
    {
        if (super.mousePressed(minecraft, xPos, p_146116_3_))
        {
            this.currentValue = from1((float)(xPos - (this.xPosition + 4)) / (float)(this.width - 8));

            if (this.currentValue < sliderMinValue)
            {
                this.currentValue = sliderMinValue;
            }

            if (this.currentValue > sliderMaxValue)
            {
                this.currentValue = sliderMaxValue;
            }

            parent.worldType.setOption(option, currentValue);
            currentValue = parent.worldType.getIntegerOption(option);
            this.displayString = option + ": " + currentValue;
            
            this.isBeingPressed = true;
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public boolean isMouseOver(int i, int j)
    {
    	return i >= this.xPosition && j >= this.yPosition && i < this.xPosition + this.width && j < this.yPosition + this.height;
    }
    
    public void addValue(int i)
    {
    	currentValue += i;
    	if (this.currentValue < sliderMinValue)
        {
            this.currentValue = sliderMinValue;
        }

        if (this.currentValue > sliderMaxValue)
        {
            this.currentValue = sliderMaxValue;
        }

        parent.worldType.setOption(option, currentValue);
        currentValue = parent.worldType.getIntegerOption(option);
        this.displayString = option + ": " + currentValue;
    }

    /**
     * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
     */
    public void mouseReleased(int p_146118_1_, int p_146118_2_)
    {
        this.isBeingPressed = false;
    }
}