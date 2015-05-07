package abo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import abo.pipes.fluids.*;
import abo.pipes.items.*;
import buildcraft.api.transport.PipeManager;
import buildcraft.transport.stripes.StripesHandlerRightClick;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import da3dsoul.scaryGen.blocks.BlockLargeButton;
import da3dsoul.scaryGen.generate.BiomeStoneGen;
import da3dsoul.scaryGen.liquidXP.BlockLiquidXP;
import da3dsoul.scaryGen.liquidXP.WorldGenXPLake;
import da3dsoul.scaryGen.projectile.EntityThrownBottle;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.*;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.FluidContainerRegistry;
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
import abo.actions.ABOActionProvider;
import abo.actions.ActionSwitchOnPipe;
import abo.actions.ActionToggleOffPipe;
import abo.actions.ActionToggleOnPipe;
import abo.energy.BlockNull;
import abo.energy.BlockNullCollide;
import abo.energy.BlockWaterwheel;
import abo.energy.BlockWindmill;
import abo.energy.ItemWaterwheel;
import abo.gui.ABOGuiHandler;
import abo.pipes.power.PipePowerDirected;
import abo.pipes.power.PipePowerSwitch;
import abo.proxy.ABOProxy;
import buildcraft.BuildCraftCore;
import buildcraft.BuildCraftEnergy;
import buildcraft.BuildCraftTransport;
import buildcraft.api.core.IIconProvider;
import buildcraft.api.statements.IActionInternal;
import buildcraft.api.statements.StatementManager;
import buildcraft.core.CreativeTabBuildCraft;
import buildcraft.core.InterModComms;
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
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import da3dsoul.scaryGen.entity.EntityItemBat;
import da3dsoul.scaryGen.generate.WorldTypeScary;
import da3dsoul.scaryGen.items.ItemBottle;
import da3dsoul.scaryGen.items.ItemGoldenStaff;
import da3dsoul.scaryGen.pathfinding_astar.FollowableEntity;

import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAKE;

@Mod(modid = "Additional-Buildcraft-Objects", name = "Additional-Buildcraft-Objects", version = "MC" + ABO.MINECRAFT_VERSION + "-BC" + ABO.BUILDCRAFT_VERSION + ABO.VERSION, acceptedMinecraftVersions = "[1.7.2,1.8)", dependencies = "required-after:Forge@[10.13.2.1208,);required-after:BuildCraft|Transport;required-after:BuildCraft|Energy;required-after:BuildCraft|Silicon;required-after:BuildCraft|Factory;required-after:BuildCraft|Builders;after:LiquidXP")
public class ABO {
    public static final String VERSION = "release2.9.1";

    public static final String MINECRAFT_VERSION = "1.7.10";

    public static final String BUILDCRAFT_VERSION = "6.4";

    public static final String FORGE_VERSION = "10.13.2.1277";
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

    public static Block blockLargeButtonStone = null;
    public static Block blockLargeButtonWood = null;
    public static Item bottle = null;
    public static Item goldenstaff = null;

    // LiquidXP
    public static BlockLiquidXP blockLiquidXP;
    public static Item bucket;
    public static boolean spawnLakes = true;
    public static boolean respawnLakes = false;
    public static boolean spawnOrbs = true;
    public static int orbSpawnChance = 70;
    public static int orbLifetime = 50;
    public static int orbSize = 5;
    public static DamageSource experience = (new DamageSource("experience")).setDamageBypassesArmor().setMagicDamage().setDamageIsAbsolute();
    // LiquidXP

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
    private InventoryEnderChest theInventoryEnderChest = new InventoryEnderChest();

    // Mod Init Handling
    private boolean bucketEventCanceled = false;

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

    private static void addRecipe(Item item, int count, Object... ingredients) {

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

    @SuppressWarnings("deprecation")
    @EventHandler
    public void preinit(FMLPreInitializationEvent evt) {

        aboLog.info("Starting Additional-Buildcraft-Objects " + "MC" + MINECRAFT_VERSION + "-BC" + BUILDCRAFT_VERSION + VERSION);

        aboConfiguration = new Configuration(new File(evt.getModConfigurationDirectory(), "abo/main.conf"));

        double windmillScalar = 1;
        double waterwheelScalar = 1;

        try {
            aboConfiguration.load();

            windmillAnimations = aboConfiguration.get("Windmills", "WindmillAnimations", true).getBoolean(true);
            windmillAnimDist = aboConfiguration.get("Windmills", "WindmillAnimationDistance", 64).getInt();

            spawnLakes = aboConfiguration.get("LiquidXP", "SpawnExperieneLakes", spawnLakes).getBoolean();
            respawnLakes = aboConfiguration.get("LiquidXP", "RespawnExperieneLakes", respawnLakes).getBoolean();
            spawnOrbs = aboConfiguration.get("LiquidXP", "SpawnExperieneOrbs", spawnOrbs).getBoolean();
            orbSpawnChance = aboConfiguration.get("LiquidXP", "ExperieneOrbSpawnChance", orbSpawnChance).getInt();
            orbLifetime = aboConfiguration.get("LiquidXP", "ExperieneOrbLifetime", orbLifetime).getInt();
            orbSize = aboConfiguration.get("LiquidXP", "ExperieneOrbSize", orbSize).getInt();

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

            pipeFluidsReinforcedGolden = buildPipe(PipeFluidsReinforcedGolden.class, 1,
                    BuildCraftTransport.pipeFluidsGold, Blocks.obsidian);

            pipeFluidsReinforcedGoldenIron = buildPipe(PipeFluidsReinforcedGoldenIron.class, 1, pipeFluidsGoldenIron,
                    Blocks.obsidian);

            pipeFluidsBalance = buildPipe(PipeFluidsBalance.class, 1, BuildCraftTransport.pipeFluidsWood,
                    new ItemStack(BuildCraftEnergy.engineBlock, 1, 0), BuildCraftTransport.pipeFluidsWood);

            pipeItemsRoundRobin = buildPipe(PipeItemsRoundRobin.class, 1, BuildCraftTransport.pipeItemsStone,
                    Blocks.gravel);

            pipeItemsBounce = buildPipe(PipeItemsBounce.class);

            ArrayList<ItemStack> list = OreDictionary.getOres("cobblestone");
            if (list.size() >= 1) {
                for (ItemStack item : list) {
                    addRecipe(pipeItemsBounce, 1, BuildCraftTransport.pipeItemsStone, item);
                }
            }

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
            list = OreDictionary.getOres("plankWood");
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

            blockNull = new BlockNull().setLightOpacity(0).setBlockUnbreakable().setStepSound(Block.soundTypePiston)
                    .setBlockName("null");
            blockNullCollide = new BlockNullCollide().setLightOpacity(0).setBlockUnbreakable()
                    .setStepSound(Block.soundTypePiston).setBlockName("null");
            windmillBlock = new BlockWindmill(windmillScalar);
            waterwheelBlock = new BlockWaterwheel(waterwheelScalar);
            waterwheelItem = new ItemWaterwheel();

            if (Loader.isModLoaded("LiquidXP")) {
                BlockLiquidXP.init();
                GameRegistry.registerBlock(blockLiquidXP, "blockLiquidXP").setBlockName("blockLiquidXP");
            } else {
                blockLiquidXP = null;
            }

            GameRegistry.registerItem(waterwheelItem, "waterwheelItem");

            GameRegistry.addShapedRecipe(new ItemStack(waterwheelItem),
                    new Object[]{"CBC", "BAB", "CBC", Character.valueOf('A'), BuildCraftCore.ironGearItem,
                            Character.valueOf('B'), Blocks.stone, Character.valueOf('C'), Items.stick});

            GameRegistry.registerBlock(windmillBlock, "windmillBlock");
            GameRegistry.registerBlock(waterwheelBlock, "waterwheelBlock");
            GameRegistry.registerBlock(blockNull, "null");
            GameRegistry.registerBlock(blockNullCollide, "nullCollide");
            GameRegistry.addShapedRecipe(new ItemStack(windmillBlock),
                    new Object[]{"ABA", "BBB", "ABA", Character.valueOf('A'), BuildCraftCore.diamondGearItem,
                            Character.valueOf('B'), Items.iron_ingot});

            // scaryGen

            bottle = new ItemBottle();

            GameRegistry.addShapedRecipe(new ItemStack(bottle, 3),
                    new Object[]{" B ", "A A", " A ", Character.valueOf('A'), Blocks.glass, Character.valueOf('B'),
                            Blocks.planks});
            GameRegistry.registerItem(bottle, "MobBottle");

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

            goldenstaff = new ItemGoldenStaff();

            GameRegistry.addShapedRecipe(new ItemStack(goldenstaff),
                    new Object[]{"B", "A", "A", Character.valueOf('A'), Items.stick, Character.valueOf('B'),
                            new ItemStack(Items.dye, 1, 11)});
            GameRegistry.registerItem(goldenstaff, "GoldenStaff");

            blockLargeButtonWood = new BlockLargeButton(true).setBlockName("largebuttonwood");
            blockLargeButtonStone = new BlockLargeButton(false).setBlockName("largebuttonstone");

            GameRegistry.addShapedRecipe(new ItemStack(blockLargeButtonWood),
                    new Object[]{"AA", "AA", Character.valueOf('A'), Blocks.wooden_button});
            GameRegistry.addShapedRecipe(new ItemStack(blockLargeButtonStone),
                    new Object[]{"AA", "AA", Character.valueOf('A'), Blocks.stone_button});

            GameRegistry.registerBlock(blockLargeButtonWood, "largebuttonwood");
            GameRegistry.registerBlock(blockLargeButtonStone, "largebuttonstone");

            int id = 0;

            addEntity(FollowableEntity.class, "FollowableItem", ++id, false);
            addEntity(EntityItemBat.class, "ItemBat", ++id, true);

            LanguageRegistry.instance().addStringLocalization("entity.Additional-Buildcraft-Objects.ItemBat.name",
                    "Item Bat");

            new WorldTypeScary();

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

    @SubscribeEvent
    public void populate(PopulateChunkEvent.Pre event) {
        if (ABO.blockLiquidXP == null) return;
        if(!spawnLakes) return;
        if(!respawnLakes) return;
        if (event.rand.nextInt(16) == 0
                && TerrainGen.populate(event.chunkProvider, event.world, event.rand, event.chunkX, event.chunkZ, event.hasVillageGenerated, LAKE)) {
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
        if(!spawnLakes) return;
        if(respawnLakes) return;
        if(event.type != DecorateBiomeEvent.Decorate.EventType.LAKE) return;
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

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTextureStitchPre(TextureStitchEvent.Pre event) {
        if(ABO.blockLiquidXP != null) {
            BlockLiquidXP.initAprilFools(event);
        }
    }

    @SubscribeEvent
    public void playerUpdate(LivingEvent.LivingUpdateEvent event) {
        if (ABO.blockLiquidXP != null) {
            if (event.entityLiving instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.entityLiving;

                int L = player.experienceLevel;
                int x = (int) Math.floor(player.posX);
                int y = (int) Math.floor(player.posY);
                int z = (int) Math.floor(player.posZ);
                if (y < 256 && y > 0) {
                    if (((BlockLiquidXP) blockLiquidXP).isInXP(player)) {
                        int quanta = ((BlockLiquidXP) blockLiquidXP).getGreatestQuantaValue(player);
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
                            if (((BlockLiquidXP) blockLiquidXP).useXP(player.worldObj, x, y, z)) {
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
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (event.entityLiving instanceof EntityPlayer) {
                if (ABO.blockLiquidXP != null) {
                    if (BlockLiquidXP.onTryToUseBottle((EntityPlayer) event.entityLiving, event.x, event.y, event.z, event.face)) {
                        bucketEventCanceled = true;
                    }
                }
            }
        }
        if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
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

    @EventHandler
    public void init(FMLInitializationEvent evt) {

        loadRecipes();

        ABOProxy.proxy.registerTileEntities();
        ABOProxy.proxy.registerBlockRenderers();

        ABOProxy.proxy.registerEntities();

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ABOGuiHandler());

    }

    @EventHandler
    public void post(FMLPostInitializationEvent event) {
        BiomeStoneGen.init();
    }

    // Side Handling

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

    // Ender Pipe Handling

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

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void textureHook(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 1) {
            itemIconProvider.registerIcons(event.map);
        }
    }

    @Mod.EventHandler
    public void processIMCRequests(FMLInterModComms.IMCEvent event) {
        InterModComms.processIMC(event);
    }

    // Item Init Handling

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

    // Recipe Handling

    public InventoryEnderChest getInventoryEnderChest() {
        return this.theInventoryEnderChest;
    }

    private File getWorldDir() throws IOException {

        return new File(FMLCommonHandler.instance().getSavesDirectory().getCanonicalPath()
                + File.separator
                + FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld().getSaveHandler()
                .getWorldDirectoryName());

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

    private static class ABORecipe {
        boolean isShapeless = false;
        ItemStack result;
        Object[] input;
    }

}