package abo;

import abo.actions.ABOActionProvider;
import abo.actions.ActionSwitchOnPipe;
import abo.actions.ActionToggleOffPipe;
import abo.actions.ActionToggleOnPipe;
import abo.energy.*;
import abo.gui.ABOGuiHandler;
import abo.machines.BlockAccelerator;
import abo.pipes.fluids.*;
import abo.pipes.items.*;
import abo.pipes.power.PipePowerDirected;
import abo.pipes.power.PipePowerSwitch;
import abo.proxy.ABOProxy;
import abo.robots.ABOBoardNBT;
import abo.robots.BoardRobotStoner;
import abo.robots.BoardRobotWoody;
import buildcraft.BuildCraftCore;
import buildcraft.BuildCraftTransport;
import buildcraft.api.boards.RedstoneBoardRegistry;
import buildcraft.api.core.IIconProvider;
import buildcraft.api.robots.RobotManager;
import buildcraft.api.statements.IActionInternal;
import buildcraft.api.statements.StatementManager;
import buildcraft.api.transport.PipeManager;
import buildcraft.core.BCCreativeTab;
import buildcraft.core.InterModComms;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.ItemPipe;
import buildcraft.transport.Pipe;
import buildcraft.transport.PipeTransportFluids;
import buildcraft.transport.stripes.StripesHandlerRightClick;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import da3dsoul.AutoCompressor.CompressionEventHandler;
import da3dsoul.ImplicationEventHandler;
import da3dsoul.ShapeGen.ShapeGen;
import da3dsoul.scaryGen.SaveHandler.EnderInventorySaveHandler;
import da3dsoul.scaryGen.blocks.BlockLargeButton;
import da3dsoul.scaryGen.blocks.BlockNoCrossing;
import da3dsoul.scaryGen.blocks.BlockSandStone;
import da3dsoul.scaryGen.blocks.ItemSandStone;
import da3dsoul.scaryGen.generate.BiomeStoneGen;
import da3dsoul.scaryGen.generate.ChunkProviderScary;
import da3dsoul.scaryGen.generate.GeostrataGen.Ore.COFH.COFHOverride;
import da3dsoul.scaryGen.generate.WorldTypeScary;
import da3dsoul.scaryGen.generate.WorldTypeScaryBOP;
import da3dsoul.scaryGen.items.ItemAutoCompressor;
import da3dsoul.scaryGen.items.ItemBottle;
import da3dsoul.scaryGen.items.ItemGoldenStaff;
import da3dsoul.scaryGen.items.ItemThermalImplication;
import da3dsoul.scaryGen.liquidXP.BlockLiquidXP;
import da3dsoul.scaryGen.liquidXP.WorldGenXPLake;
import da3dsoul.scaryGen.projectile.EntityThrownBottle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.*;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Mod(modid = "Additional-Buildcraft-Objects", name = "Additional-Buildcraft-Objects", version = "${version}", acceptedMinecraftVersions = "[1.7.2,1.8)", dependencies = "required-after:Forge@[10.13.2.1208,);required-after:BuildCraft|Transport;required-after:BuildCraft|Energy;required-after:BuildCraft|Silicon;required-after:BuildCraft|Factory;required-after:BuildCraft|Builders;after:BuildCraft|Robotics;after:LiquidXP;after:GeoStrata;after:CoFHCore;after:ThermalFoundation;after:ThermalExpansion;after:BiomesOPlenty;after:ExtraUtilities")
public class ABO{
    public static Configuration aboConfiguration;
    public static Logger aboLog = LogManager
            .getLogger("Additional-Buildcraft-Objects");
    public static Item pipeFluidsValve = null;
    public static Item pipeFluidsGoldenIron = null;
    public static Item pipeFluidsReinforcedGolden = null;
    public static Item pipeFluidsReinforcedGoldenIron = null;
    public static Item pipeFluidsInsertion = null;
    public static Item pipeFluidsBalance = null;
    public static Item pipeFluidsDrain = null;
    public static Item pipeItemsRoundRobin = null;
    public static Item pipeItemsBounce = null;
    public static Item pipeItemsDivide = null;
    public static Item pipeItemsCompactor = null;
    public static Item pipeItemsInsertion = null;
    public static Item pipeItemsExtraction = null;
    public static Item pipeItemsEnderExtraction = null;
    public static Item pipeItemsCrossover = null;
    public static Item pipePowerSwitch = null;
    public static Item pipePowerIron = null;

    public static BlockWindmill windmillBlock;
    public static BlockWaterwheel waterwheelBlock;
    public static Block blockNull = null;
    public static Block blockNullCollide = null;
    public static Block acceleratorBlock = null;

    public static Block blockLargeButtonStone = null;
    public static Block blockLargeButtonWood = null;
    public static Block sandStone;
    public static Item bottle = null;
    public static Item goldenstaff = null;
    public static HashMap<Integer, ShapeGen> shapeGens = new HashMap<Integer, ShapeGen>();
    public static boolean useMobBottle;
    public static boolean useYellowDye;

    public static boolean geostrataInstalled = false;
    public static boolean cofhInstalled = false;

    // LiquidXP
    public static BlockLiquidXP blockLiquidXP;
    //public static Item bucket;
    public static boolean spawnLakes = true;
    public static boolean respawnLakes = false;
    public static boolean spawnOrbs = true;
    public static int orbSpawnChance = 70;
    public static int orbLifetime = 50;
    public static int orbSize = 5;
    public static DamageSource experience = (new DamageSource("experience")).setDamageBypassesArmor().setMagicDamage().setDamageIsAbsolute();
    // LiquidXP

    // Thermal Foundation
    public static Item thermalImplication;
    // Thermal Foundation

    // Extra Utilities
    public static Item autoCompressor;
    // Extra Utilities

    public static int actionSwitchOnPipeID = 128;
    public static IActionInternal actionSwitchOnPipe = null;
    public static int actionToggleOnPipeID = 129;
    public static IActionInternal actionToggleOnPipe = null;
    public static int actionToggleOffPipeID = 130;
    public static IActionInternal actionToggleOffPipe = null;
    public static boolean windmillAnimations;
    public static int windmillAnimDist;
    public static Item waterwheelItem;
    public static String loadedEnderInventory = "";
    @Instance("Additional-Buildcraft-Objects")
    public static ABO instance;
    public static boolean valveConnectsStraight;
    public static boolean valvePhysics;
    private static LinkedList<ABORecipe> aboRecipes = new LinkedList<ABORecipe>();

    public IIconProvider itemIconProvider = new ItemIconProvider();
    public IIconProvider pipeIconProvider = new PipeIconProvider();
    public InventoryEnderChest theInventoryEnderChest = new InventoryEnderChest();

    // Mod Init Handling
    private boolean bucketEventCanceled = false;
    private EnderInventorySaveHandler enderInventorySaveHandler = null;
    public static Block brickNoCrossing;

    // Start

    @SuppressWarnings("deprecation")
    @EventHandler
    public void preinit(FMLPreInitializationEvent evt) {

        aboLog.info("Starting Additional-Buildcraft-Objects " + "${version}");

        aboConfiguration = new Configuration(new File(evt.getModConfigurationDirectory(), "abo/main.conf"));

        double windmillScalar = 1;
        double waterwheelScalar = 1;

        geostrataInstalled = Loader.isModLoaded("GeoStrata");
        cofhInstalled = Loader.isModLoaded("CoFHCore");

        if(ABO.geostrataInstalled && ABO.cofhInstalled) {
            ABO.aboLog.info("COFH is Loaded");
            COFHOverride.overrideCOFHWordGen(0);
        }

        try {
            initFluidCapacities();

            aboConfiguration.load();

            useMobBottle = aboConfiguration.get("Misc", "AllowMobBottles", true).getBoolean(true);
            useYellowDye = aboConfiguration.get("Misc", "AllowYellowDye", true).getBoolean(true);

            windmillAnimations = aboConfiguration.get("Windmills", "WindmillAnimations", true).getBoolean(true);
            windmillAnimDist = aboConfiguration.get("Windmills", "WindmillAnimationDistance", 64).getInt();

            spawnLakes = aboConfiguration.get("LiquidXP", "SpawnExperienceLakes", spawnLakes).getBoolean();
            respawnLakes = aboConfiguration.get("LiquidXP", "RespawnExperienceLakes", respawnLakes).getBoolean();
            spawnOrbs = aboConfiguration.get("LiquidXP", "SpawnExperienceOrbs", spawnOrbs).getBoolean();
            orbSpawnChance = aboConfiguration.get("LiquidXP", "ExperienceOrbSpawnChance", orbSpawnChance).getInt();
            orbLifetime = aboConfiguration.get("LiquidXP", "ExperienceOrbLifetime", orbLifetime).getInt();
            orbSize = aboConfiguration.get("LiquidXP", "ExperienceOrbSize", orbSize).getInt();

            windmillScalar = aboConfiguration.get("Windmills", "WindmillEnergyScalar", 1.0).getDouble();
            waterwheelScalar = aboConfiguration.get("Windmills", "WaterwheelEnergyScalar", 1.0).getDouble();

            valveConnectsStraight = aboConfiguration.get("Misc", "ValvePipeOnlyConnectsStraight", true)
                    .getBoolean(true);

            valvePhysics = aboConfiguration.get("Misc", "ValvePipeUsesGravityPhysics", true).getBoolean(true);

            pipeFluidsValve = buildPipe(PipeFluidsValve.class, 1, BuildCraftTransport.pipeFluidsWood,
                    BuildCraftTransport.pipeGate);

            pipeFluidsDrain = buildPipe(PipeFluidsDrain.class, 1, BuildCraftTransport.pipeFluidsWood,
                    Items.iron_ingot);

            pipeFluidsGoldenIron = buildPipe(PipeFluidsGoldenIron.class, 1, BuildCraftTransport.pipeFluidsGold,
                    BuildCraftTransport.pipeFluidsIron);

            pipeFluidsReinforcedGolden = buildPipe(PipeFluidsReinforcedGolden.class, 1, BuildCraftTransport.pipeFluidsGold, Blocks.obsidian);

            pipeFluidsReinforcedGoldenIron = buildPipe(PipeFluidsReinforcedGoldenIron.class, 1, pipeFluidsGoldenIron, Blocks.obsidian);

            pipeFluidsBalance = buildPipe(PipeFluidsBalance.class, 1, BuildCraftTransport.pipeFluidsWood,
                    new ItemStack(BuildCraftCore.engineBlock, 1, 0), BuildCraftTransport.pipeFluidsWood);

            // Item Pipes

            pipeItemsRoundRobin = buildPipe(PipeItemsRoundRobin.class, 1, BuildCraftTransport.pipeItemsStone,
                    Blocks.gravel);

            pipeItemsBounce = buildPipe(PipeItemsBounce.class);

            pipeItemsDivide = buildPipe(PipeItemsDivide.class);

            addRecipe(pipeItemsDivide, 1, BuildCraftTransport.pipeItemsStone, Items.wooden_sword);
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

            pipeItemsEnderExtraction = buildPipe(PipeItemsEnderExtraction.class, 1, ABO.pipeItemsExtraction,
                    Items.ender_pearl);

            pipeItemsCrossover = buildPipe(PipeItemsCrossover.class, 1, BuildCraftTransport.pipeItemsStone,
                    BuildCraftTransport.pipeItemsIron);

            pipePowerSwitch = buildPipe(PipePowerSwitch.class, 1, BuildCraftTransport.pipePowerGold, Blocks.lever);

            pipePowerIron = buildPipe(PipePowerDirected.class, 1, new ItemStack(BuildCraftTransport.pipeGate, 1),
                    BuildCraftTransport.pipePowerGold);

            blockNull = new BlockNull().setLightOpacity(0).setBlockUnbreakable().setStepSound(Block.soundTypePiston)
                    .setBlockName("null");
            blockNullCollide = new BlockNullCollide().setLightOpacity(0).setBlockUnbreakable()
                    .setStepSound(Block.soundTypePiston).setBlockName("null");
            windmillBlock = new BlockWindmill(windmillScalar);
            waterwheelBlock = new BlockWaterwheel(waterwheelScalar);
            waterwheelItem = new ItemWaterwheel();

            acceleratorBlock = new BlockAccelerator().setBlockName("blockAccelerator");

            if (Loader.isModLoaded("LiquidXP")) {
                BlockLiquidXP.preinit();
                GameRegistry.registerBlock(blockLiquidXP, "blockLiquidXP").setBlockName("blockLiquidXP");
            } else {
                blockLiquidXP = null;
            }

            if (Loader.isModLoaded("ThermalFoundation")) {
                thermalImplication = new ItemThermalImplication().init();
                ImplicationEventHandler.initialize();
            } else {
                thermalImplication = null;
            }

            if (Loader.isModLoaded("ExtraUtilities")) {
                autoCompressor = new ItemAutoCompressor().init();
                CompressionEventHandler.initialize();
            } else {
                autoCompressor = null;
            }

            sandStone = (new BlockSandStone()).setStepSound(Block.soundTypePiston).setHardness(0.8F).setBlockName("sandStone").setBlockTextureName("sandstone");

            ItemBlock sandStoneItem = (new ItemMultiTexture(sandStone, sandStone, BlockSandStone.iconNames)).setUnlocalizedName("sandStone");
            GameRegistry.registerBlock(sandStone, ItemSandStone.class, "sandStone");

            GameRegistry.registerItem(waterwheelItem, "waterwheelItem");

            addFullRecipe(new ItemStack(ABO.waterwheelItem),
                    new Object[]{"CBC", "BAB", "CBC", 'A', BuildCraftCore.ironGearItem,
                            'B', Blocks.stone, 'C', Items.stick});

            GameRegistry.registerBlock(windmillBlock, "windmillBlock");
            GameRegistry.registerBlock(waterwheelBlock, "waterwheelBlock");
            GameRegistry.registerBlock(acceleratorBlock, "acceleratorBlock");
            addFullRecipe(new ItemStack(acceleratorBlock),
                    new Object[]{"ABA", "ACA", "DED", 'A', BuildCraftCore.ironGearItem,
                            'B', BuildCraftCore.diamondGearItem, 'C', Items.clock, 'D', Blocks.stone, 'E', Items.redstone});

            brickNoCrossing = (new BlockNoCrossing(Material.rock)).setHardness(2.0F).setResistance(10.0F).setStepSound(Block.soundTypePiston).setBlockName("brickNoCrossing").setCreativeTab(CreativeTabs.tabBlock).setBlockTextureName("brick");

            GameRegistry.registerBlock(brickNoCrossing, "brickNoCrossing");
            addFullRecipe(new ItemStack(ABO.brickNoCrossing), new Object[] { "#", '#', new ItemStack(Blocks.brick_block) });
            addFullRecipe(new ItemStack(Blocks.brick_block), new Object[] { "#", '#', new ItemStack(ABO.brickNoCrossing) });
            GameRegistry.registerBlock(blockNull, "null");
            GameRegistry.registerBlock(blockNullCollide, "nullCollide");
            addFullRecipe(new ItemStack(windmillBlock),
                    new Object[]{"ABA", "BBB", "ABA", 'A', BuildCraftCore.diamondGearItem,
                            'B', Items.iron_ingot});

            // scaryGen

            bottle = new ItemBottle();
            GameRegistry.registerItem(bottle, "MobBottle");
            if(useMobBottle) {
                addFullRecipe(new ItemStack(bottle, 3, 0),
                        new Object[]{" B ", "A A", " A ", 'A', Blocks.glass, 'B', Blocks.planks});

                PipeManager.registerStripesHandler(new StripesHandlerRightClick() {
                    @Override
                    public boolean shouldHandle(ItemStack stack) {
                        return stack.getItem() == ABO.bottle;
                    }
                });
                BlockDispenser.dispenseBehaviorRegistry.putObject(ABO.bottle, new BehaviorProjectileDispense() {

                    @Override
                    public ItemStack dispenseStack(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
                        World world = p_82487_1_.getWorld();
                        IPosition iposition = BlockDispenser.func_149939_a(p_82487_1_);
                        EnumFacing enumfacing = BlockDispenser.func_149937_b(p_82487_1_.getBlockMetadata());
                        IProjectile iprojectile = new EntityThrownBottle(world, iposition.getX(), iposition.getY(), iposition.getZ(), p_82487_2_.splitStack(1));
                        iprojectile.setThrowableHeading((double) enumfacing.getFrontOffsetX(), (double) ((float) enumfacing.getFrontOffsetY() + 0.1F), (double) enumfacing.getFrontOffsetZ(), this.func_82500_b(), this.func_82498_a());
                        world.spawnEntityInWorld((Entity) iprojectile);
                        return p_82487_2_;
                    }

                    @Override
                    protected IProjectile getProjectileEntity(World p_82499_1_, IPosition p_82499_2_) {
                        return null;
                    }
                });
            }

            goldenstaff = new ItemGoldenStaff();
            GameRegistry.registerItem(goldenstaff, "GoldenStaff");
            if(useYellowDye) {
                addFullRecipe(new ItemStack(goldenstaff, 1, 0),
                        new Object[]{"B", "A", "A", Character.valueOf('A'), Items.stick, Character.valueOf('B'),
                                new ItemStack(Items.dye, 1, 11)});
                addFullRecipe(new ItemStack(goldenstaff, 1, 0),
                        new Object[]{"  B", " A ", "A  ", Character.valueOf('A'), Items.stick, Character.valueOf('B'),
                                new ItemStack(Items.dye, 1, 11)});
            }

            blockLargeButtonWood = new BlockLargeButton(true).setBlockName("largebuttonwood");
            blockLargeButtonStone = new BlockLargeButton(false).setBlockName("largebuttonstone");

            GameRegistry.registerBlock(blockLargeButtonWood, "largebuttonwood");
            GameRegistry.registerBlock(blockLargeButtonStone, "largebuttonstone");

            addFullRecipe(new ItemStack(blockLargeButtonWood, 1, 0),
                    new Object[]{"AA", "AA", Character.valueOf('A'), Blocks.wooden_button});
            addFullRecipe(new ItemStack(blockLargeButtonStone, 1, 0),
                    new Object[]{"AA", "AA", Character.valueOf('A'), Blocks.stone_button});

            LanguageRegistry.instance().addStringLocalization("entity.Additional-Buildcraft-Objects.ItemBat.name",
                    "Item Bat");

            new WorldTypeScary();

            if (Loader.isModLoaded("BiomesOPlenty")) new WorldTypeScaryBOP();

            // end scaryGen

            actionSwitchOnPipe = new ActionSwitchOnPipe(actionSwitchOnPipeID);
            actionToggleOnPipe = new ActionToggleOnPipe(actionToggleOnPipeID);
            actionToggleOffPipe = new ActionToggleOffPipe(actionToggleOffPipeID);

            StatementManager.registerActionProvider(new ABOActionProvider());

            FMLCommonHandler.instance().bus().register(this);
            MinecraftForge.EVENT_BUS.register(this);
            MinecraftForge.TERRAIN_GEN_BUS.register(this);

            if (Loader.isModLoaded("BuildCraft|Robotics")) {
                RedstoneBoardRegistry.instance.registerBoardType(new ABOBoardNBT("additional-buildcraft-objects:stoner",
                        "stoner", BoardRobotStoner.class, "blue"), 32000);
                RedstoneBoardRegistry.instance.registerBoardType(new ABOBoardNBT("additional-buildcraft-objects:woody",
                        "woody", BoardRobotWoody.class, "blue"), 32000);
            }

        } finally {
            if (aboConfiguration.hasChanged()) aboConfiguration.save();
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent evt) {

        ArrayList<ItemStack> list = OreDictionary.getOres("cobblestone");
        if (list.size() >= 1) {
            for (ItemStack item : list) {
                addRecipe(pipeItemsBounce, 1, BuildCraftTransport.pipeItemsStone, item);
            }
        }

        list = OreDictionary.getOres("plankWood");
        if (list.size() >= 1) {
            for (ItemStack item : list) {
                addRecipe(pipeItemsExtraction, 1, BuildCraftTransport.pipeItemsWood, item);
            }
        }

        loadRecipes();

        if (blockLiquidXP != null) {
            BlockLiquidXP.init();
        }

        ABOProxy.proxy.registerTileEntities();
        ABOProxy.proxy.registerBlockRenderers();

        ABOProxy.proxy.registerEntities();

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ABOGuiHandler());

        for(Object o : GameData.getBlockRegistry().getKeys()) {
            String s = (String)o;
            if(s.split(":")[0].equals("Additional-Buildcraft-Objects")) {
                Block b = GameData.getBlockRegistry().get(s);
                if(b == null) continue;
                if(s.contains("sandStone") || s.contains("accelerator")) continue;
                FMLInterModComms.sendMessage("BuildCraft|Transport", "blacklist-facade", new ItemStack(b));
            } else if(s.split(":")[0].equals("GalaxySpace")) {
                Block b = GameData.getBlockRegistry().get(s);
                if(b == null) continue;
                if(b.isOpaqueCube()) continue;
                FMLInterModComms.sendMessage("BuildCraft|Transport", "blacklist-facade", new ItemStack(b));
            }
        }
        if (Loader.isModLoaded("BuildCraft|Robotics")) {
            RobotManager.registerAIRobot(BoardRobotStoner.class, "boardRobotStoner", "abo.robots.BoardRobotStoner");
            RobotManager.registerAIRobot(BoardRobotWoody.class, "boardRobotWoody", "abo.robots.BoardRobotWoody");
        }
    }

    @EventHandler
    public void post(FMLPostInitializationEvent event) {
        BiomeStoneGen.init();
    }

    @EventHandler
    public void aboutToStart(FMLServerAboutToStartEvent event) {
        shapeGens.clear();
        ShapeGen.stopping = false;
    }

    @EventHandler
    public void start(FMLServerStartingEvent event) {
        loadEnderInventory();
    }

    @EventHandler
    public void stop(FMLServerStoppingEvent event) {
        ShapeGen.stopping = true;
        Integer[] dims = DimensionManager.getIDs();
        for(int world : dims) {
            ShapeGen.getShapeGen(world).writeToNBT();
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {

    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void decorateBiome(DecorateBiomeEvent.Decorate event) {
        if(!ChunkProviderScary.genSurfaceFeatures) {
            switch (event.type) {
                case BIG_SHROOM: event.setResult(Event.Result.DENY); break;
                case CACTUS: event.setResult(Event.Result.DENY); break;
                case DEAD_BUSH: event.setResult(Event.Result.DENY); break;
                case FLOWERS: event.setResult(Event.Result.DENY); break;
                case GRASS: event.setResult(Event.Result.DENY); break;
                case LAKE: event.setResult(Event.Result.DENY); break;
                case LILYPAD: event.setResult(Event.Result.DENY); break;
                case PUMPKIN: event.setResult(Event.Result.DENY); break;
                case REED: event.setResult(Event.Result.DENY); break;
                case SHROOM: event.setResult(Event.Result.DENY); break;
                case TREE: event.setResult(Event.Result.DENY); break;
                case CUSTOM: event.setResult(Event.Result.DENY); break;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void populateBiome(PopulateChunkEvent.Populate event) {
        if(!ChunkProviderScary.genSurfaceFeatures) {
            switch (event.type) {
                case ICE: event.setResult(Event.Result.DENY); break;
                case LAKE: event.setResult(Event.Result.DENY); break;
                case LAVA: event.setResult(Event.Result.DENY); break;
                case CUSTOM: event.setResult(Event.Result.DENY); break;
            }
        }
    }

    @SubscribeEvent
    public void tickWorld(TickEvent.WorldTickEvent event) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null) {
            if (event.phase == TickEvent.Phase.START) {
                if (enderInventorySaveHandler == null) {
                    WorldServer maxPasses = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0);
                    enderInventorySaveHandler = (EnderInventorySaveHandler) maxPasses.mapStorage.loadData(EnderInventorySaveHandler.class, "EnderInventory");
                    if (enderInventorySaveHandler == null) {
                        enderInventorySaveHandler = new EnderInventorySaveHandler("EnderInventory");
                        maxPasses.mapStorage.setData("EnderInventory", enderInventorySaveHandler);
                    }
                }

                if(event.world != null && !event.world.isRemote) {
                    ShapeGen.getShapeGen(event.world).tick();
                }
            }
        }
    }


    @SubscribeEvent
    public void populate(PopulateChunkEvent.Populate event) {
        if (ABO.blockLiquidXP == null) return;
        if(event.hasVillageGenerated) return;
        if(event.type != PopulateChunkEvent.Populate.EventType.LAKE) return;
        if (!spawnLakes) return;
        if (!respawnLakes) return;
        if (event.rand.nextInt(16) == 0
                && TerrainGen.populate(event.chunkProvider, event.world, event.rand, event.chunkX, event.chunkZ, event.hasVillageGenerated, PopulateChunkEvent.Populate.EventType.LAKE)) {
            int k1 = event.chunkX + event.rand.nextInt(16) + 8;
            int l1 = 45 + event.rand.nextInt(211);
            int i2 = event.chunkZ + event.rand.nextInt(16) + 8;
            if (event.world.getWorldInfo().getVanillaDimension() != -1) {
                new WorldGenXPLake().generate(event.world, event.rand, k1, l1, i2);
            }
        }
    }

    @SubscribeEvent
    public void decorate(DecorateBiomeEvent.Decorate event) {
        if (ABO.blockLiquidXP == null) return;
        if (!spawnLakes) return;
        if (respawnLakes) return;
        if (event.type != DecorateBiomeEvent.Decorate.EventType.LAKE) return;
        if (event.rand.nextInt(16) == 0
                && TerrainGen.decorate(event.world, event.rand, event.chunkX, event.chunkZ, event.type)) {
            int k1 = event.chunkX + event.rand.nextInt(16) + 8;
            int l1 = 45 + event.rand.nextInt(211);
            int i2 = event.chunkZ + event.rand.nextInt(16) + 8;
            if (event.world.getWorldInfo().getVanillaDimension() != -1) {
                new WorldGenXPLake().generate(event.world, event.rand, k1, l1, i2);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void playerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (ABO.blockLiquidXP != null) {
            if (event.entityLiving instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.entityLiving;

                int L = player.experienceLevel;
                int x = (int) Math.floor(player.posX);
                int y = (int) Math.floor(player.posY);
                int z = (int) Math.floor(player.posZ);
                if (y < 256 && y > 0) {
                    if (blockLiquidXP.isInXP(player)) {
                        int quanta = blockLiquidXP.getGreatestQuantaValue(player);
                        if (player.ticksExisted % 20 == 0) {
                            if (!player.capabilities.isCreativeMode) {
                                int targetLevel = blockLiquidXP.getLevelTarget(player.worldObj, x, y, z, quanta);
                                if (L < targetLevel) {
                                    player.attackEntityFrom(experience, targetLevel - L);
                                }
                                player.addExhaustion(1.0F);
                            }
                        }
                        if (player.isDead) return;
                        if (player.worldObj.rand.nextInt(100) == 0) {
                            if (blockLiquidXP.useXP(player.worldObj, x, y, z)) {
                                player.addExperience(1000);
                                if (!player.worldObj.isRemote)
                                    player.worldObj.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.orb", 0.1F, 0.5F * ((player.worldObj.rand.nextFloat() - player.worldObj.rand.nextFloat()) * 0.7F + 1.8F));
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onRightClick(PlayerInteractEvent event) {
        if(event.world.isRemote) return;
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (event.entityLiving instanceof EntityPlayer) {
                if (ABO.blockLiquidXP != null) {
                    if (BlockLiquidXP.onTryToUseBottle((EntityPlayer) event.entityLiving, event.x, event.y, event.z, event.face)) {
                        bucketEventCanceled = true;
                    } else if(BlockLiquidXP.tryToPlaceFromBucket(event.entityPlayer, event.x, event.y, event.z, event.face))
                    {
                        bucketEventCanceled = true;
                    }

                }
                if (event.entityPlayer.inventory.getCurrentItem() != null && event.entityPlayer.inventory.getCurrentItem().getItem() == Items.dye && event.entityPlayer.inventory.getCurrentItem().getItemDamage() == 15) {
                    Block var5 = event.world.getBlock(event.x, event.y, event.z);
                    World world = event.world;
                    boolean success = false;

                    if (isBlockDirtAndFree(world, event.x, event.y, event.z)) {
                        BiomeGenBase biome = world.getBiomeGenForCoords(event.x, event.z);
                        Block block1 = Blocks.grass;
                        int meta = 0;
                        if (biome == BiomeGenBase.mushroomIsland || biome == BiomeGenBase.mushroomIslandShore)
                            block1 = Blocks.mycelium;
                        if (biome == BiomeGenBase.megaTaiga || biome == BiomeGenBase.megaTaigaHills) meta = 1;
                        success = world.setBlock(event.x, event.y, event.z, block1, meta, 3) && randomizeGrass(world, event.x, event.y, event.z);
                    }

                    if (success && !event.entityPlayer.capabilities.isCreativeMode && world.rand.nextInt(20) == 0) {
                        event.entityPlayer.inventory.getCurrentItem().stackSize--;
                        if (event.entityPlayer.inventory.getCurrentItem().stackSize <= 0)
                            event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, null);
                    }
                }
            }
        }
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
            if (event.entityPlayer instanceof EntityPlayer && (event.entityPlayer.getCommandSenderName().equalsIgnoreCase("da3dsoul") || event.entityPlayer.getCommandSenderName().equalsIgnoreCase("balmung42")) && event.entityPlayer.inventory.getCurrentItem() != null && event.entityPlayer.inventory.getCurrentItem().getItem() instanceof ItemDye) {
                ItemStack itemstack = event.entityPlayer.getCurrentEquippedItem();

                int range = 150;
                EntityPlayer entityplayer = event.entityPlayer;
                World world = entityplayer.worldObj;

                if (itemstack.getItemDamage() == 4) {
                    ChunkCoordinates chuck = getLooking(world, entityplayer, range);

                    if (chuck == null) {
                        return;
                    }

                    int i = chuck.posX;
                    int j = chuck.posY;
                    int k = chuck.posZ;

                    if (!world.canMineBlock(entityplayer, i, j, k)) {
                        return;
                    }

                    ShapeGen.getShapeGen(world).shuffle(i, j, k, 3, -1, true, true, true);
                } else if (itemstack.getItemDamage() == 6) {
                    ChunkCoordinates chuck = getLooking(world, entityplayer, range);

                    if (chuck == null) {
                        return;
                    }

                    int i = chuck.posX;
                    int j = chuck.posY;
                    int k = chuck.posZ;

                    if (!world.canMineBlock(entityplayer, i, j, k)) {
                        return;
                    }
                    ShapeGen.getShapeGen(world).blendNewOld(world, i, j, k, 5, false, true);
                } else if (itemstack.getItemDamage() == 10) {
                    ChunkCoordinates chuck = getLooking(world, entityplayer, range);

                    if (chuck == null) {
                        return;
                    }

                    int i = chuck.posX;
                    int j = chuck.posY;
                    int k = chuck.posZ;

                    if (!world.canMineBlock(entityplayer, i, j, k)) {
                        return;
                    }
                    ShapeGen.getShapeGen(world).blendNewOld(world, i, j, k, 4, true, true);
                } else if (itemstack.getItemDamage() == 12) {
                    ChunkCoordinates chuck = getLookingOffset(world, entityplayer, range, 1);

                    if (chuck == null) {
                        return;
                    }

                    int i = chuck.posX;
                    int j = chuck.posY;
                    int k = chuck.posZ;

                    if (!world.canMineBlock(entityplayer, i, j, k)) {
                        return;
                    }
                    ShapeGen.getShapeGen(world).makeLaputa(i, 160, k);
                }
            }

            if (blockLiquidXP != null) {
                if (BlockLiquidXP.onTryToUseBottle((EntityPlayer) event.entityLiving, event.x, event.y, event.z, event.face)) {
                    bucketEventCanceled = true;
                }
            }
            if (bucketEventCanceled) {
                event.setCanceled(true);
                bucketEventCanceled = false;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTextureStitchPre(TextureStitchEvent.Pre event) {
        if (ABO.blockLiquidXP != null) {
            BlockLiquidXP.initAprilFools(event);
        }
    }

    @EventHandler
    public void processIMCRequests(FMLInterModComms.IMCEvent event) {
        InterModComms.processIMC(event);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void textureHook(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 1) {
            itemIconProvider.registerIcons(event.map);
        }
    }

    // End

    // Start

    private void loadEnderInventory() {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
            try {
                File file = new File(getWorldDir(), "ABO-EnderInventory.nbt");
                NBTTagCompound nbt = CompressedStreamTools.read(file);
                if (nbt != null) {
                    NBTTagList list = nbt.getTagList("EnderInventory", 10);
                    theInventoryEnderChest.loadInventoryFromNBT(list);
                }
                file.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public InventoryEnderChest getInventoryEnderChest() {
        return this.theInventoryEnderChest;
    }

    // End

    // Start

    public void loadRecipes() {
        // Add pipe recipes
        for (ABORecipe recipe : aboRecipes) {
            if (recipe.isShapeless) {
                GameRegistry.addShapelessRecipe(recipe.result, recipe.input);
            } else {
                GameRegistry.addShapedRecipe(recipe.result, recipe.input);
            }

        }
    }

    private static class ABORecipe {
        boolean isShapeless = false;
        ItemStack result;
        Object[] input;
    }

    // End

    // Start

    public static ChunkCoordinates getLooking(World world, EntityPlayer entityplayer, int range) {
        return getLookingOffset(world, entityplayer, range, 0);
    }

    public static ChunkCoordinates getLookingOffset(World world, EntityPlayer entityplayer, int range, int off) {
        float f = entityplayer.rotationPitch;
        float f1 = entityplayer.rotationYaw;
        double d = entityplayer.posX;
        double d1 = (entityplayer.posY + 1.6200000000000001D) - (double) entityplayer.yOffset;
        double d2 = entityplayer.posZ;
        Vec3 vec3d = Vec3.createVectorHelper(d, d1, d2);
        float f2 = MathHelper.cos(-f1 * 0.01745329F - (float) Math.PI);
        float f3 = MathHelper.sin(-f1 * 0.01745329F - (float) Math.PI);
        float f4 = -MathHelper.cos(-f * 0.01745329F);
        float f5 = MathHelper.sin(-f * 0.01745329F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d3 = range;
        Vec3 vec3d1 = vec3d.addVector((double) f6 * d3, (double) f5 * d3, (double) f7 * d3);
        MovingObjectPosition movingobjectposition = world.rayTraceBlocks(vec3d, vec3d1, false);

        if (movingobjectposition == null) {
            return null;
        }

        if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            int i = movingobjectposition.blockX;
            int j = movingobjectposition.blockY;
            int k = movingobjectposition.blockZ;

            if (movingobjectposition.sideHit == 0) {
                j -= off;
            }

            if (movingobjectposition.sideHit == 1) {
                j += off;
            }

            if (movingobjectposition.sideHit == 2) {
                k -= off;
            }

            if (movingobjectposition.sideHit == 3) {
                k += off;
            }

            if (movingobjectposition.sideHit == 4) {
                i -= off;
            }

            if (movingobjectposition.sideHit == 5) {
                i += off;
            }

            return new ChunkCoordinates(i, j, k);
        }

        return null;
    }

    private boolean randomizeGrass(World world, int i, int j, int k) {
        boolean success = false;
        Random random = new Random();
        int l1 = random.nextInt(4) + 1;

        int x = i;
        int z = k;

        do {
            int l = random.nextInt(2);

            if (random.nextInt(2) == 0) {
                switch (l) {
                    case 0:
                        x++;
                        break;

                    case 1:
                        z++;
                        break;
                }
            } else {
                switch (l) {
                    case 0:
                        x--;
                        break;
                    case 1:
                        z--;
                        break;
                }
            }
            int y = j - 3;

            while (!isBlockDirtAndFree(world, x, y, z) && y <= j + 6) {
                y++;
            }

            if (isBlockDirtAndFree(world, x, y, z)) {
                BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
                Block block1 = Blocks.grass;
                int meta = 0;
                if (biome == BiomeGenBase.mushroomIsland || biome == BiomeGenBase.mushroomIslandShore)
                    block1 = Blocks.mycelium;
                if (biome == BiomeGenBase.megaTaiga || biome == BiomeGenBase.megaTaigaHills) meta = 1;
                if (world.setBlock(x, y, z, block1, meta, 3)) success = true;
            }

            l1--;
        }
        while (l1 > 0);

        return success;
    }

    private boolean isBlockDirtAndFree(World world, int i, int j, int k) {
        if (world.getBlock(i, j, k) != Blocks.dirt) {
            return false;
        }

        return !(world.getBlockLightValue(i, j + 1, k) < 4 && world.getBlockLightOpacity(i, j + 1, k) > 2);

    }

    public static File getWorldDir() {
        try {
            return new File(FMLCommonHandler.instance().getSavesDirectory().getCanonicalPath()
                    + File.separator
                    + FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().getSaveHandler()
                    .getWorldDirectoryName());
        } catch (IOException e) {
            return null;
        }
    }

    // End

    // Start

    public static ItemPipe buildPipe(Class<? extends Pipe<?>> clas, int count, Object... ingredients) {
        ItemPipe res = buildPipe(clas);

        addRecipe(res, count, ingredients);

        return res;
    }

    public static ItemPipe buildPipe(Class<? extends Pipe<?>> clas) {

        ItemPipe res = BlockGenericPipe.registerPipe(clas, BCCreativeTab.get("pipes"));
        res.setUnlocalizedName(clas.getSimpleName());
        ABOProxy.proxy.registerPipe(res);

        return res;
    }

    public static void addRecipe(Item item, int count, Object... ingredients) {

        if (ingredients.length == 3) {
            for (int i = 0; i < 17; i++) {
                ABORecipe recipe = new ABORecipe();
                recipe.result = new ItemStack(item, count, i);
                if (ingredients[0] instanceof ItemPipe && ingredients[2] instanceof ItemPipe) {
                    recipe.input = new Object[]{"ABC", 'A', new ItemStack((ItemPipe) ingredients[0], 1, i), 'B',
                            ingredients[1], 'C', new ItemStack((ItemPipe) ingredients[2], 1, i)};
                } else {
                    recipe.input = new Object[]{"ABC", 'A', ingredients[0], 'B', ingredients[1], 'C', ingredients[2]};
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
                recipe.input = new Object[]{left, right};

                aboRecipes.add(recipe);
            }
        }
    }

    public static void addShapelessRecipe(ItemStack stack, Object... ingredients) {
        ABORecipe recipe = new ABORecipe();

        recipe.isShapeless = true;
        recipe.result = stack;
        recipe.input = ingredients;

        aboRecipes.add(recipe);
    }

    public static void addFullRecipe(ItemStack stack, Object[] ingredients) {
        ABORecipe recipe = new ABORecipe();
        recipe.result = stack;
        recipe.input = ingredients;
        aboRecipes.add(recipe);
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

    private void initFluidCapacities() {
        PipeTransportFluids.fluidCapacities.put(PipeFluidsGoldenIron.class, Integer.valueOf(8 * BuildCraftTransport.pipeFluidsBaseFlowRate));
        PipeTransportFluids.fluidCapacities.put(PipeFluidsInsertion.class, Integer.valueOf(8 * BuildCraftTransport.pipeFluidsBaseFlowRate));

        PipeTransportFluids.fluidCapacities.put(PipeFluidsReinforcedGolden.class, Integer.valueOf(2 * FluidContainerRegistry.BUCKET_VOLUME));
        PipeTransportFluids.fluidCapacities.put(PipeFluidsReinforcedGoldenIron.class, Integer.valueOf(2 * FluidContainerRegistry.BUCKET_VOLUME));
        PipeTransportFluids.fluidCapacities.put(PipeFluidsBalance.class, Integer.valueOf(2 * FluidContainerRegistry.BUCKET_VOLUME));
        PipeTransportFluids.fluidCapacities.put(PipeFluidsValve.class, Integer.valueOf(2 * FluidContainerRegistry.BUCKET_VOLUME));
        PipeTransportFluids.fluidCapacities.put(PipeFluidsDrain.class, Integer.valueOf(2 * FluidContainerRegistry.BUCKET_VOLUME));

    }

    // End
}