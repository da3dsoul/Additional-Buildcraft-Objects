package da3dsoul.scaryGen.ModInteraction.LiquidXP;

import abo.ABO;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.Random;

public class LiquidXPProxy {
    public static void preinit()
    {
        BlockLiquidXP.preinit();
        GameRegistry.registerBlock(ABO.blockLiquidXP, "blockLiquidXP").setBlockName("blockLiquidXP");
    }

    public static void init()
    {
        BlockLiquidXP.init();
    }

    public static boolean rightClick_Block(PlayerInteractEvent event, boolean bucketEventCanceled)
    {
        if (BlockLiquidXP.onTryToUseBottle((EntityPlayer) event.entityLiving, event.x, event.y, event.z, event.face)) {
            bucketEventCanceled = true;
        } else if(BlockLiquidXP.tryToPlaceFromBucket(event.entityPlayer, event.x, event.y, event.z, event.face))
        {
            bucketEventCanceled = true;
        }
        return bucketEventCanceled;
    }

    public static boolean rightClick_Air(PlayerInteractEvent event, boolean bucketEventCanceled)
    {
        if (BlockLiquidXP.onTryToUseBottle((EntityPlayer) event.entityLiving, event.x, event.y, event.z, event.face)) {
            bucketEventCanceled = true;
        }
        return bucketEventCanceled;
    }

    public static void populate(PopulateChunkEvent.Populate event)
    {
        if(event.hasVillageGenerated) return;
        if(event.type != PopulateChunkEvent.Populate.EventType.LAKE) return;
        if (!ABO.spawnLakes) return;
        if (!ABO.respawnLakes) return;
        if (event.rand.nextInt(16) == 0
                && TerrainGen.populate(event.chunkProvider, event.world, event.rand, event.chunkX, event.chunkZ, event.hasVillageGenerated, PopulateChunkEvent.Populate.EventType.LAKE)) {
            generateLake(event.world, event.rand, event.chunkX, event.chunkZ);
        }
    }

    public static void decorate(DecorateBiomeEvent.Decorate event)
    {
        if (!ABO.spawnLakes) return;
        if (ABO.respawnLakes) return;
        if (event.type != DecorateBiomeEvent.Decorate.EventType.LAKE) return;
        if (event.rand.nextInt(16) == 0
                && TerrainGen.decorate(event.world, event.rand, event.chunkX, event.chunkZ, event.type)) {
            generateLake(event.world, event.rand, event.chunkX, event.chunkZ);
        }
    }

    private static void generateLake(World world, Random rand, int chunkX, int chunkZ) {
        int k1 = chunkX + rand.nextInt(16) + 8;
        int l1 = 45 + rand.nextInt(211);
        int i2 = chunkZ + rand.nextInt(16) + 8;
        if (world.getWorldInfo().getVanillaDimension() != -1) {
            new WorldGenXPLake().generate(world, rand, k1, l1, i2);
        }
    }

    public static void playerUpdate(LivingEvent.LivingUpdateEvent event)
    {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entityLiving;

            int L = player.experienceLevel;
            int x = (int) Math.floor(player.posX);
            int y = (int) Math.floor(player.posY);
            int z = (int) Math.floor(player.posZ);
            if (y < 256 && y > 0) {
                if (((BlockLiquidXP) ABO.blockLiquidXP).isInXP(player)) {
                    int quanta = ((BlockLiquidXP) ABO.blockLiquidXP).getGreatestQuantaValue(player);
                    if (player.ticksExisted % 20 == 0) {
                        if (!player.capabilities.isCreativeMode) {
                            int targetLevel = ((BlockLiquidXP) ABO.blockLiquidXP).getLevelTarget(player.worldObj, x, y, z, quanta);
                            if (L < targetLevel) {
                                player.attackEntityFrom(ABO.experience, targetLevel - L);
                            }
                            player.addExhaustion(1.0F);
                        }
                    }
                    if (player.isDead) return;
                    if (player.worldObj.rand.nextInt(100) == 0) {
                        if (((BlockLiquidXP) ABO.blockLiquidXP).useXP(player.worldObj, x, y, z)) {
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
