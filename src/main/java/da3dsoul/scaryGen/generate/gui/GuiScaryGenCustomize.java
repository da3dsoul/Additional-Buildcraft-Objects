package da3dsoul.scaryGen.generate.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import da3dsoul.scaryGen.generate.WorldTypeScary;

@SideOnly(Side.CLIENT)
public class GuiScaryGenCustomize extends GuiScreen {
	private final GuiCreateWorld	parentScreen;
	public final WorldTypeScary		worldType;
	private GuiTextField			textField;
	private GuiButton				checkButton;
	private String					origOptions;
	private String					lastText;
	@SuppressWarnings("unused")
	private static final String		__OBFID	= "CL_00000693";

	public GuiScaryGenCustomize(GuiCreateWorld parentScreen, WorldTypeScary worldType) {
		this.parentScreen = parentScreen;
		this.worldType = worldType;
        origOptions = worldType.optionsToString();
        if(!parentScreen.field_146334_a.isEmpty() && !parentScreen.field_146334_a.equals(worldType.optionsToString())){
            origOptions = parentScreen.field_146334_a;
            worldType.setOptionsFromString(parentScreen.field_146334_a);
        }
	}

	public void updateScreen() {
		this.textField.updateCursorCounter();
	}

	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	protected void keyTyped(char charCode, int keyCode) {
		if (this.textField.isFocused()) {
			this.textField.textboxKeyTyped(charCode, keyCode);
			if (textField.getText().trim() != lastText) {
				checkButton.enabled = true;
				checkButton.displayString = "?";
			}

			if (keyCode == 28 || keyCode == 156 && checkButton.enabled) {
				this.actionPerformed((GuiButton) this.buttonList.get(6));
			}
		}

	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@SuppressWarnings("unchecked")
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		// Sliders are 150 wide and 20 tall
		// Buttons are 200 wide and 20 tall
		GuiBox boxRoot = GuiBox.atCenterOfGui(this, 450, 128);
		GuiBox[][] grid = boxRoot.getRowsAndColumnsOf(2, 1);
		GuiBox col1 = grid[0][0].insetHalved(25);
		GuiBox col2 = grid[1][0].insetHalved(25);

		int y = col1.startY;

		this.buttonList.clear();
		this.buttonList.add(new GuiIntSlider(this, 2, col1.startX, y, "index", 0, 6));
		this.buttonList.add(new GuiIntSlider(this, 3, col2.startX, y, "heightLevel", 4, 255));
		y += 32;
		this.buttonList.add(new GuiIntSlider(this, 4, col1.startX, y, "oceanLevel", 0, 255));
		this.buttonList.add(new GuiIntSlider(this, 5, col2.startX, y, "cloudLevel", 4, 300));
		y += 32;
        this.buttonList.add(new GuiIntSlider(this, 6, col1.startX, y, "geostrataGen", 0, 1));
        this.buttonList.add(new GuiIntSlider(this, 8, col2.startX, y, "genSurfaceFeatures", 0, 1));
        //this.buttonList.add(new GuiIntSlider(this, 7, col2.startX, y, "colorTolerance", 20, 255));
        y += 32;
		this.textField = new GuiTextField(this.fontRendererObj, col1.startX, y, 375, 20);
		this.textField.setMaxStringLength(512);
		this.textField.setFocused(true);
		this.textField.setText(worldType.getOceanReplacement());
		this.buttonList.add(checkButton = new GuiButton(7, col1.startX + 400, y, 25, 20, "?"));
		y += 32;

		this.buttonList.add(0, new GuiButton(0, col1.startX, y, 200, 20, I18n.format("gui.done", new Object[0])));
		this.buttonList.add(1, new GuiButton(1, col2.startX, y, 200, 20, I18n.format("gui.cancel", new Object[0])));

	}

	protected void actionPerformed(GuiButton buttonPressed) {
		if (buttonPressed.id == 0) {
			parentScreen.field_146334_a = worldType.optionsToString();
			this.mc.displayGuiScreen(parentScreen);
		} else if (buttonPressed.id == 1) {
			worldType.setOptionsFromString(origOptions);
			parentScreen.field_146334_a = worldType.optionsToString();
			this.mc.displayGuiScreen(parentScreen);
		} else if (buttonPressed.id == 6) {
			if (worldType.setOceanReplacement(textField.getText().trim())) {
				checkButton.displayString = "OK";
			} else {
				checkButton.displayString = "X";
			}
			checkButton.enabled = false;
			lastText = textField.getText().trim();
		}

	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
		this.drawDefaultBackground();
		this.textField.drawTextBox();
		super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		int k = Mouse.getEventDWheel();
		if (k != 0) {

			int i = Mouse.getEventX() * this.width / this.mc.displayWidth;
			int j = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
			
			if (k < 0) {
				k = -1;
			}
			if(k > 0)
			{
				k = 1;
			}

			for (Object slider : buttonList) {
				if (!(slider instanceof GuiIntSlider)) continue;
				if (((GuiIntSlider) slider).isMouseOver(i, j)) {
					((GuiIntSlider) slider).addValue(k);
				}
			}
		}
	}
}