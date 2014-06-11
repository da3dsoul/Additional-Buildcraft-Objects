/** 
 * Copyright (C) 2011-2013 Flow86
 * 
 * AdditionalBuildcraftObjects is open-source.
 *
 * It is distributed under the terms of my Open Source License. 
 * It grants rights to read, modify, compile or run the code. 
 * It does *NOT* grant the right to redistribute this software or its 
 * modifications in any form, binary or source, except if expressively
 * granted by the copyright holder.
 */

package abo;

import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.logging.Level;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.world.WorldEvent;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import abo.actions.ABOActionProvider;
import abo.actions.ActionSwitchOnPipe;
import abo.actions.ActionToggleOffPipe;
import abo.actions.ActionToggleOnPipe;
import abo.energy.BlockWindmill;
import abo.gui.ABOGuiHandler;
import abo.items.ABOItem;
import abo.items.ItemGateSettingsDuplicator;
import abo.network.ABOPacketHandler;
import abo.pipes.fluids.PipeFluidsBalance;
import abo.pipes.fluids.PipeFluidsDistribution;
import abo.pipes.fluids.PipeFluidsGoldenIron;
import abo.pipes.fluids.PipeFluidsPump;
import abo.pipes.fluids.PipeFluidsValve;
import abo.pipes.items.PipeItemsCompactor;
import abo.pipes.items.PipeItemsCrossover;
import abo.pipes.items.PipeItemsDivide;
import abo.pipes.items.PipeItemsEnderExtraction;
import abo.pipes.items.PipeItemsExtraction;
import abo.pipes.items.PipeItemsInsertion;
import abo.pipes.items.PipeItemsRoundRobin;
import abo.pipes.power.PipePowerDirected;
import abo.pipes.power.PipePowerDistribution;
import abo.pipes.power.PipePowerSwitch;
import abo.proxy.ABOProxy;
import abo.triggers.ABOTriggerProvider;
import abo.triggers.TriggerEngineSafe;

import buildcraft.BuildCraftCore;
import buildcraft.BuildCraftEnergy;
import buildcraft.BuildCraftTransport;
import buildcraft.BuildCraftSilicon;
import buildcraft.api.core.BCLog;
import buildcraft.api.core.IIconProvider;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.IAction;
import buildcraft.api.gates.ITrigger;
import buildcraft.core.CreativeTabBuildCraft;
import buildcraft.core.DefaultProps;
import buildcraft.core.InterModComms;
import buildcraft.core.network.BuildCraftPacket;
import buildcraft.core.network.PacketHandler;
import buildcraft.core.proxy.CoreProxy;
import buildcraft.energy.ItemEngine;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.FMLOutboundHandler.OutboundTarget;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * @author Flow86
 * 
 */
@Mod(modid = "Additional-Buildcraft-Objects", name = "Additional-Buildcraft-Objects", version = "@ABO_VERSION@", acceptedMinecraftVersions = "[1.7.2,1.8)", dependencies = "required-after:Forge@[10.12.1.1079,);required-after:BuildCraft|Transport;required-after:BuildCraft|Energy;required-after:BuildCraft|Silicon")
public class ABO {
	public static final String VERSION = "2.0.2";

	public static final String MINECRAFT_VERSION = "1.7.2";

	public static final String BUILDCRAFT_VERSION = "6.0.15";

	public static final String FORGE_VERSION = "10.12.1.1085";

	public IIconProvider itemIconProvider = new ItemIconProvider();
	public IIconProvider pipeIconProvider = new PipeIconProvider();

	public static ABOConfiguration aboConfiguration;
	public static Logger aboLog = LogManager.getLogger("Additional-Buildcraft-Objects");

	public static Item itemGateSettingsDuplicator = null;

	public static Item pipeFluidsValve = null;

	public static Item pipeFluidsGoldenIron = null;

	public static Item pipeFluidsBalance = null;

	public static Item pipeFluidsDiamond = null;

	public static Item pipeItemsRoundRobin = null;

	public static Item pipeItemsDivide = null;

	public static Item pipeItemsCompactor = null;

	public static Item pipeItemsInsertion = null;

	public static Item pipeItemsExtraction = null;

	public static Item pipeItemsEnderExtraction = null;

	public static Item pipeItemsCrossover = null;

	public static Item pipePowerSwitch = null;

	public static Item pipePowerIron = null;

	public static Item pipeDistributionConductive = null;

	public static BlockWindmill windmillBlock;

	public static int triggerEngineSafeID = 128;
	public static ITrigger triggerEngineSafe = null;

	public static int actionSwitchOnPipeID = 128;
	public static IAction actionSwitchOnPipe = null;

	public static int actionToggleOnPipeID = 129;
	public static IAction actionToggleOnPipe = null;

	public static int actionToggleOffPipeID = 130;
	public static IAction actionToggleOffPipe = null;
	
	public static boolean windmillAnimations;
	public static byte windmillAnimDist;
	
	public static String loadedEnderInventory = "";

	private InventoryEnderChest theInventoryEnderChest = new InventoryEnderChest();

	@Instance("Additional-Buildcraft-Objects")
	public static ABO instance;

	public EnumMap<Side, FMLEmbeddedChannel> channels;

	@EventHandler
	public void preInitialize(FMLPreInitializationEvent evt) {

		//aboLog.setParent(FMLLog.getLogger());
		aboLog.info("Starting Additional-Buildcraft-Objects #@BUILD_NUMBER@ " + VERSION + " (Built for Minecraft"+MINECRAFT_VERSION+" with Buildcraft "+BUILDCRAFT_VERSION+" and Forge "+FORGE_VERSION);
		aboLog.info("Copyright (c) Flow86, 2011-2013");

		aboConfiguration = new ABOConfiguration(new File(evt.getModConfigurationDirectory(), "abo/main.conf"));
		try {
			aboConfiguration.load();
			
			windmillAnimations = aboConfiguration.get("Misc", "WindmillAnimations", true).getBoolean(true);
			windmillAnimDist = (byte) aboConfiguration.get("Misc", "AnimateWindmillDistance", 64).getInt(64);

			pipeFluidsValve = buildPipe(PipeFluidsValve.class, "Valve Pipe", 1, BuildCraftTransport.pipeFluidsWood, BuildCraftTransport.pipeGate, null);

			pipeFluidsGoldenIron = buildPipe(PipeFluidsGoldenIron.class, "Golden Iron Waterproof Pipe", 1, BuildCraftTransport.pipeFluidsGold,
					BuildCraftTransport.pipeFluidsIron, null);

			pipeFluidsBalance = buildPipe(PipeFluidsBalance.class, "Balancing Waterproof Pipe", 1, BuildCraftTransport.pipeFluidsWood, new ItemStack(
					BuildCraftEnergy.engineBlock, 1, 0), BuildCraftTransport.pipeFluidsWood);

			pipeFluidsDiamond = buildPipe(PipeFluidsDistribution.class, "Diamond Waterproof Pipe", 1, BuildCraftTransport.pipeItemsDiamond, BuildCraftTransport.pipeWaterproof,
					null);

			pipeItemsRoundRobin = buildPipe(PipeItemsRoundRobin.class, "RoundRobin Transport Pipe", 1, BuildCraftTransport.pipeItemsStone, Blocks.gravel, null);

			pipeItemsDivide = buildPipe(PipeItemsDivide.class, "Division Transport Pipe");

			addRecipe(pipeItemsDivide, 1, BuildCraftTransport.pipeItemsStone, Items.stone_sword, null);
			addRecipe(pipeItemsDivide, 1, BuildCraftTransport.pipeItemsStone, Items.iron_sword, null);
			addRecipe(pipeItemsDivide, 1, BuildCraftTransport.pipeItemsStone, Items.golden_sword, null);
			addRecipe(pipeItemsDivide, 1, BuildCraftTransport.pipeItemsStone, Items.diamond_sword, null);

			pipeItemsCompactor = buildPipe(PipeItemsCompactor.class, "Compactor Pipe", 1, BuildCraftTransport.pipeItemsStone, Blocks.piston, null);

			pipeItemsInsertion = buildPipe(PipeItemsInsertion.class, "Insertion Pipe", 1, BuildCraftTransport.pipeItemsIron, new ItemStack(Items.dye, 1, 2), null);

			pipeItemsExtraction = buildPipe(PipeItemsExtraction.class, "Extraction Transport Pipe", 1, BuildCraftTransport.pipeItemsWood, Blocks.planks, null);
			pipeItemsEnderExtraction = buildPipe(PipeItemsEnderExtraction.class, "Ender Extraction Transport Pipe", 1, ABO.pipeItemsExtraction, Items.ender_pearl, null);

			pipeItemsCrossover = buildPipe(PipeItemsCrossover.class, "Crossover Transport Pipe", 1, BuildCraftTransport.pipeItemsStone, BuildCraftTransport.pipeItemsIron, null);

			pipePowerSwitch = buildPipe(PipePowerSwitch.class, "Power Switch Pipe", 1, BuildCraftTransport.pipePowerGold, Blocks.lever, null);

			pipePowerIron = buildPipe(PipePowerDirected.class, "Directed Kinesis Pipe", 1, new ItemStack(BuildCraftTransport.pipeGate, 1, 1),
					BuildCraftTransport.pipePowerGold, null);

			pipeDistributionConductive = buildPipe(PipePowerDistribution.class, "Distribution Conductive Pipe", 2, pipePowerIron, BuildCraftTransport.pipeItemsDiamond,
					pipePowerIron);

			itemGateSettingsDuplicator = createItem(ItemGateSettingsDuplicator.class, "Gate Settings Duplicator", BuildCraftCore.wrenchItem,
					new ItemStack(BuildCraftSilicon.redstoneChipset, 1, 4), null);

			windmillBlock = new BlockWindmill();
			GameRegistry.registerBlock(windmillBlock, "windmillBlock");
			GameRegistry.addShapedRecipe(new ItemStack(windmillBlock), new Object[] { "ABA", "BBB", "ABA", Character.valueOf('A'), BuildCraftCore.diamondGearItem, Character.valueOf('B'), Items.iron_ingot });

			triggerEngineSafe = new TriggerEngineSafe(triggerEngineSafeID);
			actionSwitchOnPipe = new ActionSwitchOnPipe(actionSwitchOnPipeID);
			actionToggleOnPipe = new ActionToggleOnPipe(actionToggleOnPipeID);
			actionToggleOffPipe = new ActionToggleOffPipe(actionToggleOffPipeID);

			ActionManager.registerActionProvider(new ABOActionProvider());
			ActionManager.registerTriggerProvider(new ABOTriggerProvider());

			FMLCommonHandler.instance().bus().register(this);
			MinecraftForge.EVENT_BUS.register(this);
		} finally {
			if (aboConfiguration.hasChanged())
				aboConfiguration.save();
		}
	}

	@EventHandler
	public void load(FMLInitializationEvent evt) {

		channels = NetworkRegistry.INSTANCE.newChannel
				("ABO", new ABOPacketHandler());

		loadRecipes();

		ABOProxy.proxy.registerTileEntities();
		ABOProxy.proxy.registerBlockRenderers();

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ABOGuiHandler());
	}

	@SubscribeEvent
	public void save(WorldEvent.Save event)
	{
		saveEnderInventory(event.world.isRemote);
	}
	
	@SubscribeEvent
	public void stop(WorldEvent.Unload event)
	{
		saveEnderInventory(event.world.isRemote);
		loadedEnderInventory = "";
	}

	@SubscribeEvent
	public void load(WorldEvent.Load event) {
		if(loadedEnderInventory != event.world.getSaveHandler().getWorldDirectoryName())
		{
			loadEnderInventory(event.world.isRemote);
			loadedEnderInventory = event.world.getSaveHandler().getWorldDirectoryName();
		}
		
	}

	private void loadEnderInventory(boolean isRemote)
	{
		if(!isRemote)
		{
			try {
				File file = new File(getWorldDir(), "ABO-EnderInventory.nbt");
				NBTTagCompound nbt = CompressedStreamTools.read(file);
				if(nbt != null)
				{
					NBTTagList list = nbt.getTagList("EnderInventory", 10);
					theInventoryEnderChest.loadInventoryFromNBT(list);
				}
			} catch (IOException e) {e.printStackTrace();}
		}
	}

	private void saveEnderInventory(boolean isRemote)
	{
		if(!isRemote)
		{
			try {
				File file = new File(getWorldDir(), "ABO-EnderInventory.nbt");
				NBTTagCompound nbt = new NBTTagCompound();
				NBTTagList list = theInventoryEnderChest.saveInventoryToNBT();
				nbt.setTag("EnderInventory", list);
				CompressedStreamTools.write(nbt, file);
			} catch (IOException e) {e.printStackTrace();}
		}
	}

	private File getWorldDir() throws IOException
	{

		return new File(FMLCommonHandler.instance().getSavesDirectory().getCanonicalPath() + File.separator + FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().getSaveHandler().getWorldDirectoryName());

	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Pre event) {
		if (event.map.getTextureType() == 1) {
			// pipeIconProvider.registerIcons(event.map);
			itemIconProvider.registerIcons(event.map);
		}
	}

	private static class ABORecipe {
		Item itemID;
		boolean isPipe = false;
		boolean isShapeless = false;
		ItemStack result;
		Object[] input;
	}

	private static LinkedList<ABORecipe> aboRecipes = new LinkedList<ABORecipe>();

	private static Item createItem(Class<? extends ABOItem> clazz, String descr, Object ingredient1, Object ingredient2, Object ingredient3) {
		String name = Character.toLowerCase(clazz.getSimpleName().charAt(0)) + clazz.getSimpleName().substring(1);

		Item item = null;
		try {
			item = clazz.getConstructor().newInstance();
		} catch (Throwable t) {
			t.printStackTrace();
		}

		if (item == null)
			return item;

		item.setUnlocalizedName(clazz.getSimpleName());
		GameRegistry.registerItem(item, item.getUnlocalizedName().replace("item.", ""));
		LanguageRegistry.addName(item, descr);

		addRecipe(item, 1, ingredient1, ingredient2, ingredient3);

		return item;
	}

	private static void addRecipe(Item item, int count, Object ingredient1, Object ingredient2, Object ingredient3) {
		if (ingredient1 != null && ingredient2 != null && ingredient3 != null) {
			addRecipe(item, count, false, new Object[] { "ABC", Character.valueOf('A'), ingredient1, Character.valueOf('B'), ingredient2, Character.valueOf('C'), ingredient3 });
		} else if (ingredient1 != null && ingredient2 != null) {
			addRecipe(item, count, true, new Object[] { ingredient1, ingredient2 });
		}
	}

	private static void addRecipe(Item item, int count, boolean isShapeless, Object[] ingredients) {
		// Add appropriate recipe to temporary list
		ABORecipe recipe = new ABORecipe();

		recipe.isPipe = (item instanceof ItemPipe);
		recipe.itemID = item;
		recipe.isShapeless = isShapeless;
		recipe.input = ingredients;
		recipe.result = new ItemStack(item, count);

		aboRecipes.add(recipe);
	}

	public static ItemPipe buildPipe(Class<? extends Pipe> clas,
			String descr, int count, Object... ingredients) {
		//String name = Character.toLowerCase(clas.getSimpleName().charAt(0)) + clas.getSimpleName().substring(1);

		ItemPipe res = buildPipe(clas, descr);

		addRecipe(res, count, ingredients[0], ingredients[1], ingredients[2]);

		return res;
	}

	public static ItemPipe buildPipe(Class<? extends Pipe> clas,
			String descr) {
		//String name = Character.toLowerCase(clas.getSimpleName().charAt(0)) + clas.getSimpleName().substring(1);

		ItemPipe res = BlockGenericPipe.registerPipe(clas, CreativeTabBuildCraft.PIPES);
		res.setUnlocalizedName(clas.getSimpleName());

		return res;
	}

	private static ItemPipe createPipe(Class<? extends Pipe> clazz, String descr) {
		String name = Character.toLowerCase(clazz.getSimpleName().charAt(0)) + clazz.getSimpleName().substring(1);

		ItemPipe pipe = BlockGenericPipe.registerPipe(clazz, CreativeTabBuildCraft.PIPES);
		pipe.setUnlocalizedName(clazz.getSimpleName());
		LanguageRegistry.addName(pipe, descr);
		GameRegistry.registerItem(pipe, pipe.getUnlocalizedName().replace("item.", ""));

		return pipe;
	}

	private static Item createPipe(Class<? extends Pipe> clazz, String descr, int count, boolean isShapeless, Object[] ingredients) {
		ItemPipe pipe = createPipe(clazz, descr);

		addRecipe(pipe, count, isShapeless, ingredients);

		return pipe;
	}

	private static Item createPipe(Class<? extends Pipe> clazz, String descr, int count, Object ingredient1, Object ingredient2, Object ingredient3) {
		ItemPipe pipe = createPipe(clazz, descr);

		addRecipe(pipe, count, ingredient1, ingredient2, ingredient3);

		return pipe;
	}

	public void loadRecipes() {
		// Add pipe recipes
		for (ABORecipe recipe : aboRecipes) {
			if (recipe.isShapeless) {
				GameRegistry.addShapelessRecipe(recipe.result, recipe.input);
			} else {
				GameRegistry.addRecipe(recipe.result, recipe.input);
			}

			if (recipe.isPipe)
				ABOProxy.proxy.registerPipe(recipe.itemID);
		}
	}

	public void sendToServer(BuildCraftPacket packet) {
		try {
			channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.TOSERVER);
			channels.get(Side.CLIENT).writeOutbound(packet);
		} catch (Throwable t) {
			BCLog.logger.log(Level.WARNING, "sentToServer crash", t);
		}
	}

	@Mod.EventHandler
	public void processIMCRequests(FMLInterModComms.IMCEvent event) {
		InterModComms.processIMC(event);
	}

	public InventoryEnderChest getInventoryEnderChest()
	{
		return this.theInventoryEnderChest;
	}
}