package abo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Random;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import abo.actions.ABOActionProvider;
import abo.actions.ActionSwitchOnPipe;
import abo.actions.ActionToggleOffPipe;
import abo.actions.ActionToggleOnPipe;
import abo.energy.BlockNull;
import abo.energy.BlockWaterwheel;
import abo.energy.BlockWindmill;
import abo.gui.ABOGuiHandler;
import abo.network.ABOPacketHandler;
import abo.pipes.fluids.PipeFluidsBalance;
import abo.pipes.fluids.PipeFluidsInsertion;
import abo.pipes.fluids.PipeFluidsGoldenIron;
import abo.pipes.fluids.PipeFluidsReinforcedGolden;
import abo.pipes.fluids.PipeFluidsReinforcedGoldenIron;
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
import buildcraft.BuildCraftCore;
import buildcraft.BuildCraftEnergy;
import buildcraft.BuildCraftTransport;
import buildcraft.api.core.BCLog;
import buildcraft.api.core.IIconProvider;
import buildcraft.api.statements.IActionInternal;
import buildcraft.api.statements.StatementManager;
import buildcraft.core.CreativeTabBuildCraft;
import buildcraft.core.InterModComms;
import buildcraft.core.network.BuildCraftPacket;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.FMLOutboundHandler.OutboundTarget;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import da3dsoul.scaryGen.entity.EntityItemBat;
import da3dsoul.scaryGen.mod_ScaryGen.ItemBottle;
import da3dsoul.scaryGen.mod_ScaryGen.ItemGoldenStaff;
import da3dsoul.scaryGen.pathfinding_astar.FollowableEntity;

@Mod(modid = "Additional-Buildcraft-Objects", name = "Additional-Buildcraft-Objects", version = "2.0.2+", acceptedMinecraftVersions = "[1.7.2,1.8)", dependencies = "required-after:Forge@[10.12.1.1079,);required-after:BuildCraft|Transport;required-after:BuildCraft|Energy;required-after:BuildCraft|Silicon;required-after:BuildCraft|Factory;required-after:BuildCraft|Builders")
public class ABO {
	public static final String					VERSION							= "2.0.2+";

	public static final String					MINECRAFT_VERSION				= "1.7.10";

	public static final String					BUILDCRAFT_VERSION				= "6.3.0";

	public static final String					FORGE_VERSION					= "10.13.2.1277";

	public IIconProvider						itemIconProvider				= new ItemIconProvider();
	public IIconProvider						pipeIconProvider				= new PipeIconProvider();

	public static ABOConfiguration				aboConfiguration;
	public static Logger						aboLog							= LogManager
																						.getLogger("Additional-Buildcraft-Objects");

	public static Item							itemGateSettingsDuplicator		= null;

	public static Item							pipeFluidsValve					= null;

	public static Item							pipeFluidsGoldenIron			= null;

	public static Item							pipeFluidsReinforcedGolden		= null;

	public static Item							pipeFluidsReinforcedGoldenIron	= null;

	public static Item							pipeFluidsInsertion				= null;

	public static Item							pipeFluidsBalance				= null;

	public static Item							pipeItemsRoundRobin				= null;

	public static Item							pipeItemsDivide					= null;

	public static Item							pipeItemsCompactor				= null;

	public static Item							pipeItemsInsertion				= null;

	public static Item							pipeItemsExtraction				= null;

	public static Item							pipeItemsEnderExtraction		= null;

	public static Item							pipeItemsCrossover				= null;

	public static Item							pipePowerSwitch					= null;

	public static Item							pipePowerIron					= null;

	public static Item							pipeDistributionConductive		= null;

	public static Item							bottle							= null;
	public static Item							goldenstaff						= null;

	public static BlockWindmill					windmillBlock;

	public static BlockWaterwheel				waterwheelBlock;

	public static Block							blockNull;

	public static int							actionSwitchOnPipeID			= 128;
	public static IActionInternal				actionSwitchOnPipe				= null;

	public static int							actionToggleOnPipeID			= 129;
	public static IActionInternal				actionToggleOnPipe				= null;

	public static int							actionToggleOffPipeID			= 130;
	public static IActionInternal				actionToggleOffPipe				= null;

	public static boolean						windmillAnimations;
	public static byte							windmillAnimDist;

	public static String						loadedEnderInventory			= "";

	private InventoryEnderChest					theInventoryEnderChest			= new InventoryEnderChest();

	@Instance("Additional-Buildcraft-Objects")
	public static ABO							instance;

	public EnumMap<Side, FMLEmbeddedChannel>	channels;

	public static boolean						valveConnectsStraight;
	public static boolean						valvePhysics;

	// Mod Init Handling

	@SuppressWarnings("deprecation")
	@EventHandler
	public void preInitialize(FMLPreInitializationEvent evt) {

		aboLog.info("Starting Additional-Buildcraft-Objects #@BUILD_NUMBER@ " + VERSION + " (Built for Minecraft"
				+ MINECRAFT_VERSION + " with Buildcraft " + BUILDCRAFT_VERSION + " and Forge " + FORGE_VERSION);
		aboLog.info("Copyright (c) Flow86, 2011-2013");

		aboConfiguration = new ABOConfiguration(new File(evt.getModConfigurationDirectory(), "abo/main.conf"));

		float windmillScalar = 1;

		try {
			aboConfiguration.load();

			windmillAnimations = aboConfiguration.get("Misc", "WindmillAnimations", true).getBoolean(true);
			windmillAnimDist = (byte) aboConfiguration.get("Misc", "AnimateWindmillDistance", 64).getInt(64);

			windmillScalar = (float) aboConfiguration.get("Windmills", "WindmillEnergyScalar", 1.0).getDouble(1.0);

			valveConnectsStraight = aboConfiguration.get("Misc", "ValvePipeOnlyConnectsStraight", true)
					.getBoolean(true);

			valvePhysics = aboConfiguration.get("Misc", "ValvePipeUsesGravityPhysics", true).getBoolean(true);

			pipeFluidsValve = buildPipe(PipeFluidsValve.class, 1, BuildCraftTransport.pipeFluidsWood,
					BuildCraftTransport.pipeGate);

			pipeFluidsGoldenIron = buildPipe(PipeFluidsGoldenIron.class, 1, BuildCraftTransport.pipeFluidsGold,
					BuildCraftTransport.pipeFluidsIron);

			pipeFluidsReinforcedGolden = buildPipe(PipeFluidsReinforcedGolden.class, 1,
					BuildCraftTransport.pipeFluidsGold, Blocks.obsidian);

			pipeFluidsReinforcedGoldenIron = buildPipe(PipeFluidsReinforcedGoldenIron.class, 1, pipeFluidsGoldenIron,
					Blocks.obsidian);

			pipeFluidsBalance = buildPipe(PipeFluidsBalance.class, 1, BuildCraftTransport.pipeFluidsWood,
					new ItemStack(BuildCraftEnergy.engineBlock, 1, 0), BuildCraftTransport.pipeFluidsWood);

			pipeItemsRoundRobin = buildPipe(PipeItemsRoundRobin.class, 1, BuildCraftTransport.pipeItemsStone,
					Blocks.gravel);

			pipeItemsDivide = buildPipe(PipeItemsDivide.class);

			addRecipe(pipeItemsDivide, 1, BuildCraftTransport.pipeItemsStone, Items.stone_sword);
			addRecipe(pipeItemsDivide, 1, BuildCraftTransport.pipeItemsStone, Items.iron_sword);
			addRecipe(pipeItemsDivide, 1, BuildCraftTransport.pipeItemsStone, Items.golden_sword);
			addRecipe(pipeItemsDivide, 1, BuildCraftTransport.pipeItemsStone, Items.diamond_sword);

			pipeItemsCompactor = buildPipe(PipeItemsCompactor.class, 1, BuildCraftTransport.pipeItemsStone,
					Blocks.piston);

			pipeItemsInsertion = buildPipe(PipeItemsInsertion.class, 1, BuildCraftTransport.pipeItemsIron,
					new ItemStack(Items.dye, 1, 2));

			pipeFluidsInsertion = buildPipe(PipeFluidsInsertion.class, 1, BuildCraftTransport.pipeFluidsIron,
					new ItemStack(Items.dye, 1, 2));

			pipeItemsExtraction = buildPipe(PipeItemsExtraction.class);
			ArrayList<ItemStack> list = OreDictionary.getOres("plankWood");
			if (list.size() >= 1) {
				for (ItemStack item : list) {
					addRecipe(pipeItemsExtraction, 1, BuildCraftTransport.pipeItemsWood, item);
				}
			}

			pipeItemsEnderExtraction = buildPipe(PipeItemsEnderExtraction.class, 1, ABO.pipeItemsExtraction,
					Items.ender_pearl);

			pipeItemsCrossover = buildPipe(PipeItemsCrossover.class, 1, BuildCraftTransport.pipeItemsStone,
					BuildCraftTransport.pipeItemsIron);

			pipePowerSwitch = buildPipe(PipePowerSwitch.class, 1, BuildCraftTransport.pipePowerGold, Blocks.lever);

			pipePowerIron = buildPipe(PipePowerDirected.class, 1, new ItemStack(BuildCraftTransport.pipeGate, 1),
					BuildCraftTransport.pipePowerGold);

			pipeDistributionConductive = buildPipe(PipePowerDistribution.class, 2, pipePowerIron,
					BuildCraftTransport.pipeItemsDiamond, pipePowerIron);

			blockNull = new BlockNull().setLightOpacity(0).setBlockUnbreakable().setStepSound(Block.soundTypePiston)
					.setBlockName("null").setBlockTextureName("additional-buildcraft-objects:null");
			windmillBlock = new BlockWindmill(windmillScalar);
			waterwheelBlock = new BlockWaterwheel(windmillScalar);

			GameRegistry.registerBlock(windmillBlock, "windmillBlock");
			GameRegistry.registerBlock(waterwheelBlock, "waterwheelBlock");
			GameRegistry.registerBlock(blockNull, "null");
			GameRegistry.addShapedRecipe(new ItemStack(windmillBlock),
					new Object[] { "ABA", "BBB", "ABA", Character.valueOf('A'), BuildCraftCore.diamondGearItem,
							Character.valueOf('B'), Items.iron_ingot });

			// scaryGen

			bottle = new ItemBottle();

			GameRegistry.addShapedRecipe(new ItemStack(bottle, 3),
					new Object[] { " B ", "A A", " A ", Character.valueOf('A'), Blocks.glass, Character.valueOf('B'),
							Blocks.planks });
			GameRegistry.registerItem(bottle, "MobBottle");

			goldenstaff = new ItemGoldenStaff();

			GameRegistry.addShapedRecipe(new ItemStack(goldenstaff),
					new Object[] { "B", "A", "A", Character.valueOf('A'), Items.stick, Character.valueOf('B'),
							new ItemStack(Items.dye, 1, 11) });
			GameRegistry.registerItem(goldenstaff, "GoldenStaff");

			int id = 0;

			addEntity(FollowableEntity.class, "FollowableItem", ++id, false);
			addEntity(EntityItemBat.class, "ItemBat", ++id, true);

			LanguageRegistry.instance().addStringLocalization("entity.Additional-Buildcraft-Objects.ItemBat.name",
					"Item Bat");

			// end scaryGen

			actionSwitchOnPipe = new ActionSwitchOnPipe(actionSwitchOnPipeID);
			actionToggleOnPipe = new ActionToggleOnPipe(actionToggleOnPipeID);
			actionToggleOffPipe = new ActionToggleOffPipe(actionToggleOffPipeID);

			StatementManager.registerActionProvider(new ABOActionProvider());

			FMLCommonHandler.instance().bus().register(this);
			MinecraftForge.EVENT_BUS.register(this);
		} finally {
			if (aboConfiguration.hasChanged()) aboConfiguration.save();
		}
	}

	@EventHandler
	public void load(FMLInitializationEvent evt) {

		channels = NetworkRegistry.INSTANCE.newChannel("ABO", new ABOPacketHandler());

		loadRecipes();

		ABOProxy.proxy.registerTileEntities();
		ABOProxy.proxy.registerBlockRenderers();

		ABOProxy.proxy.registerEntities();

		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ABOGuiHandler());
	}

	@SuppressWarnings("unchecked")
	private void addEntity(Class<? extends Entity> entityClass, String name, int entityID, boolean addEgg) {

		EntityRegistry.registerModEntity(entityClass, name, entityID, instance, 128, 3, true);
		if (addEgg) {
			long seed = name.hashCode();
			Random rand = new Random(seed);
			int primaryColor = rand.nextInt() * 16777215;
			int secondaryColor = rand.nextInt() * 16777215;
			EntityList.entityEggs.put(Integer.valueOf(entityID), new EntityList.EntityEggInfo(entityID, primaryColor,
					secondaryColor));
		}
	}

	@SubscribeEvent
	public void save(WorldEvent.Save event) {
		saveEnderInventory(event.world.isRemote);
	}

	@SubscribeEvent
	public void stop(WorldEvent.Unload event) {
		saveEnderInventory(event.world.isRemote);
		loadedEnderInventory = "";
	}

	@SubscribeEvent
	public void load(WorldEvent.Load event) {
		if (loadedEnderInventory != event.world.getSaveHandler().getWorldDirectoryName()) {
			loadEnderInventory(event.world.isRemote);
			loadedEnderInventory = event.world.getSaveHandler().getWorldDirectoryName();
		}

	}

	// Side Handling

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Pre event) {
		if (event.map.getTextureType() == 1) {
			itemIconProvider.registerIcons(event.map);
		}
	}

	public void sendToServer(BuildCraftPacket packet) {
		try {
			channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(OutboundTarget.TOSERVER);
			channels.get(Side.CLIENT).writeOutbound(packet);
		} catch (Throwable t) {
			BCLog.logger.log(Level.WARN, "sentToServer crash", t);
		}
	}

	@Mod.EventHandler
	public void processIMCRequests(FMLInterModComms.IMCEvent event) {
		InterModComms.processIMC(event);
	}

	// Ender Pipe Handling

	private void loadEnderInventory(boolean isRemote) {
		if (!isRemote) {
			try {
				File file = new File(getWorldDir(), "ABO-EnderInventory.nbt");
				NBTTagCompound nbt = CompressedStreamTools.read(file);
				if (nbt != null) {
					NBTTagList list = nbt.getTagList("EnderInventory", 10);
					theInventoryEnderChest.loadInventoryFromNBT(list);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void saveEnderInventory(boolean isRemote) {
		if (!isRemote) {
			try {
				File file = new File(getWorldDir(), "ABO-EnderInventory.nbt");
				NBTTagCompound nbt = new NBTTagCompound();
				NBTTagList list = theInventoryEnderChest.saveInventoryToNBT();
				nbt.setTag("EnderInventory", list);
				CompressedStreamTools.write(nbt, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public InventoryEnderChest getInventoryEnderChest() {
		return this.theInventoryEnderChest;
	}

	private File getWorldDir() throws IOException {

		return new File(FMLCommonHandler.instance().getSavesDirectory().getCanonicalPath()
				+ File.separator
				+ FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().getSaveHandler()
						.getWorldDirectoryName());

	}

	// Item Init Handling

	public static ItemPipe buildPipe(Class<? extends Pipe> clas, int count, Object... ingredients) {
		ItemPipe res = buildPipe(clas);

		addRecipe(res, count, ingredients);

		return res;
	}

	public static ItemPipe buildPipe(Class<? extends Pipe> clas) {

		ItemPipe res = BlockGenericPipe.registerPipe(clas, CreativeTabBuildCraft.PIPES);
		res.setUnlocalizedName(clas.getSimpleName());
		ABOProxy.proxy.registerPipe(res);

		return res;
	}

	// Recipe Handling

	private static class ABORecipe {
		boolean		isShapeless	= false;
		ItemStack	result;
		Object[]	input;
	}

	private static LinkedList<ABORecipe>	aboRecipes	= new LinkedList<ABORecipe>();

	private static void addRecipe(Item item, int count, Object... ingredients) {

		if (ingredients.length == 3) {
			for (int i = 0; i < 17; i++) {
				ABORecipe recipe = new ABORecipe();
				recipe.result = new ItemStack(item, count, i);
				if (ingredients[0] instanceof ItemPipe && ingredients[2] instanceof ItemPipe) {
					recipe.input = new Object[] { "ABC", 'A', new ItemStack((ItemPipe) ingredients[0], 1, i), 'B',
							ingredients[1], 'C', new ItemStack((ItemPipe) ingredients[2], 1, i) };
				} else {
					recipe.input = new Object[] { "ABC", 'A', ingredients[0], 'B', ingredients[1], 'C', ingredients[2] };
				}

				aboRecipes.add(recipe);
			}
		} else if (ingredients.length == 2) {
			for (int i = 0; i < 17; i++) {
				ABORecipe recipe = new ABORecipe();

				Object left = ingredients[0];
				Object right = ingredients[1];

				if (ingredients[0] instanceof ItemPipe) {
					left = new ItemStack((Item) left, 1, i);
				}

				if (ingredients[1] instanceof ItemPipe) {
					right = new ItemStack((Item) right, 1, i);
				}

				recipe.isShapeless = true;
				recipe.result = new ItemStack(item, 1, i);
				recipe.input = new Object[] { left, right };

				aboRecipes.add(recipe);
			}
		}
	}

	public void loadRecipes() {
		// Add pipe recipes
		for (ABORecipe recipe : aboRecipes) {
			if (recipe.isShapeless) {
				GameRegistry.addShapelessRecipe(recipe.result, recipe.input);
			} else {
				GameRegistry.addRecipe(recipe.result, recipe.input);
			}

		}
	}

}