package da3dsoul.scaryGen.generate;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import da3dsoul.scaryGen.generate.gui.GuiScaryGenCustomize;

public class WorldTypeScary extends WorldType {

	private byte index;
	public int heightLevel;
	public int oceanLevel;
	public Block oceanReplacement;
	public int cloudLevel;
	
	public WorldTypeScary() {
		super("scaryGen");
		index = 1;
		heightLevel = 128;
		oceanLevel = 63;
		oceanReplacement = Blocks.water;
		cloudLevel = 128;
		
	}

	@Override
	public IChunkProvider getChunkGenerator(World world, String generatorOptions) {
		setOptionsFromString(generatorOptions);
		return new ChunkProviderScary(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled(), index, heightLevel, oceanLevel, oceanReplacement);
	}
	
	@SideOnly(Side.CLIENT)
    public void onCustomizeButton(Minecraft instance, GuiCreateWorld guiCreateWorld)
    {
            instance.displayGuiScreen(new GuiScaryGenCustomize(guiCreateWorld, this));
    }

    /**
     * Should world creation GUI show 'Customize' button for this world type?
     * @return if this world type has customization parameters
     */
    public boolean isCustomizable()
    {
        return true;
    }
    
    public float getCloudHeight()
    {
        return cloudLevel;
    }
    
    public void setOptionsFromString(String generatorOptions)
    {
    	try
    	{
    		String[] option = generatorOptions.split("\n");
    		if(option.length > 0)
    		{
    			index = (byte) Integer.parseInt(option[0]);
    			heightLevel = Integer.parseInt(option[1]);
    			oceanLevel = Integer.parseInt(option[2]);
    			try {
    				oceanReplacement = Block.getBlockFromName(option[3]);
    			} catch (Exception e) {
    				oceanReplacement = Blocks.water;
    			}
    			cloudLevel = Integer.parseInt(option[4]);
    		}
    	}catch(Throwable t){}
    }
    
    public String optionsToString()
    {
    	StringBuilder b = new StringBuilder();
    	b.append(index).append("\n");
    	b.append(heightLevel).append("\n");
    	b.append(oceanLevel).append("\n");
    	
    	b.append(getOceanReplacement()).append("\n");
    	
    	b.append(cloudLevel);
    	return b.toString();
    }
    
    public int getIntegerOption(String option)
    {
    	if(option == "index")
    	{
    		return index;
    	} else if(option == "heightLevel")
    	{
    		return heightLevel;
    	} else if(option == "oceanLevel")
    	{
    		return oceanLevel;
    	} else if(option == "cloudLevel")
    	{
    		return cloudLevel;
    	}
    	return -1;
    }
    
    public String getOceanReplacement()
    {
    	return Block.blockRegistry.getNameForObject(oceanReplacement);
    }
    
    public void setOption(String option, int value)
    {
    	if(option == "index")
    	{
    		index = (byte) value;
    	} else if(option == "heightLevel")
    	{
    		heightLevel = value;
    	} else if(option == "oceanLevel")
    	{
    		oceanLevel = value;
    	} else if(option == "cloudLevel")
    	{
    		cloudLevel = value;
    	}
    }
    
    public boolean setOceanReplacement(String block)
    {
    	try {
    		Block block1;
    		if((block1 = Block.getBlockFromName(block)) != null)
    		{
    			oceanReplacement = block1;
    			return true;
    		}else
    		{
    			return false;
    		}
		} catch (Exception e) {
			oceanReplacement = Blocks.water;
			return false;
		}
    }
    
    public boolean isBlockString(String block)
    {
    	return Block.blockRegistry.getObject(block) != null;
    }

}
