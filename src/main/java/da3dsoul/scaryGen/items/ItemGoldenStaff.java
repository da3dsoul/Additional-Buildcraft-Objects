package da3dsoul.scaryGen.items;

import java.util.List;
import java.util.ListIterator;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import da3dsoul.scaryGen.pathfinding_astar.FollowableEntity;

public class ItemGoldenStaff extends Item {

	public ItemGoldenStaff() {
		super();
		setCreativeTab(CreativeTabs.tabMaterials);
		setUnlocalizedName("GoldenStaff");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		this.itemIcon = iconRegister.registerIcon("additional-buildcraft-objects:" + "GoldenStaff");
	}

	@SuppressWarnings("unchecked")
	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		List list = world.getEntitiesWithinAABB(net.minecraft.entity.item.EntityItem.class, AxisAlignedBB.getBoundingBox(entityplayer.posX - 0.5D, entityplayer.posY - 0.5D, entityplayer.posZ - 0.5D, entityplayer.posX + 0.5D, entityplayer.posY + 0.5D, entityplayer.posZ + 0.5D).expand(60D, 64D, 60D));
		list.addAll(world.getEntitiesWithinAABB(net.minecraft.entity.item.EntityXPOrb.class, AxisAlignedBB.getBoundingBox(entityplayer.posX - 0.5D, entityplayer.posY - 0.5D, entityplayer.posZ - 0.5D, entityplayer.posX + 0.5D, entityplayer.posY + 0.5D, entityplayer.posZ + 0.5D).expand(60D, 64D, 60D)));
		ListIterator it = list.listIterator();

		int i = 0;
		
		int range = 48;
		try
		{
			range = (int) Math.round(FMLClientHandler.instance().getClient().gameSettings.renderDistanceChunks * 5.5);
		}catch(Throwable t) {
			range = (int) Math.round(FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getViewDistance() * 5.5);	
		}
		do
		{
			if (!it.hasNext() || i > 64)
			{
				break;
			}
			Entity item = (Entity) it.next();
			if(hasFireProtection(itemstack))
			{
                if(Math.sqrt(item.posX - entityplayer.posX * item.posX - entityplayer.posX + item.posY - entityplayer.posY * item.posY - entityplayer.posY + item.posZ - entityplayer.posZ * item.posZ - entityplayer.posZ) > range) continue;

                if(item instanceof EntityItem)
                {
                    List<EntityItem> list2 = world.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(item.posX - 0.5D, item.posY - 0.5D, item.posZ - 0.5D, item.posX + 0.5D, item.posY + 0.5D, item.posZ + 0.5D).expand(10D, 10D, 10D));
                    for(EntityItem item2 : list2)
                    {
                        if(item2 == item) continue;
                    }
                    FollowableEntity item1 = new FollowableEntity((EntityItem)item, true);
                    item1.setFollowTarget(entityplayer);
                    item.setDead();
                    if(!world.isRemote) world.spawnEntityInWorld(item1);
                }else
                {
                    item.onCollideWithPlayer(entityplayer);
                    entityplayer.xpCooldown = 0;
                }
                i++;
			}else if(hasRespiration(itemstack))
			{
				if(Math.sqrt(item.posX - entityplayer.posX * item.posX - entityplayer.posX + item.posY - entityplayer.posY * item.posY - entityplayer.posY + item.posZ - entityplayer.posZ * item.posZ - entityplayer.posZ) > range) continue;

				if(item instanceof EntityItem)
				{
					List<EntityItem> list2 = world.getEntitiesWithinAABB(EntityItem.class, AxisAlignedBB.getBoundingBox(item.posX - 0.5D, item.posY - 0.5D, item.posZ - 0.5D, item.posX + 0.5D, item.posY + 0.5D, item.posZ + 0.5D).expand(10D, 10D, 10D));
					for(EntityItem item2 : list2)
					{
						if(item2 == item) continue;
					}
					FollowableEntity item1 = new FollowableEntity((EntityItem)item);
					item1.setFollowTarget(entityplayer);
					item.setDead();
					if(!world.isRemote) world.spawnEntityInWorld(item1);
				}else
				{
					item.onCollideWithPlayer(entityplayer);
					entityplayer.xpCooldown = 0;
				}
				i++;
			} else {
                item.onCollideWithPlayer(entityplayer);
                entityplayer.xpCooldown = 0;
            }
		}
		while (true);
		return itemstack;
	}

	private boolean hasRespiration(ItemStack itemstack)
    {
        if(!itemstack.isItemEnchanted()) return false;
        if(EnchantmentHelper.getEnchantmentLevel(Enchantment.respiration.effectId, itemstack) <= 0) return false;
        return true;
    }

    private boolean hasFireProtection(ItemStack itemstack)
    {
        if(!itemstack.isItemEnchanted()) return false;
        if(EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, itemstack) <= 0) return false;
        return true;
    }

}
