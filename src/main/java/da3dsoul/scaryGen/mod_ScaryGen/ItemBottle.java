package da3dsoul.scaryGen.mod_ScaryGen;

import da3dsoul.scaryGen.projectile.EntityThrownBottle;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;


public class ItemBottle extends ItemGlassBottle
{
	public ItemBottle()
	{
		super();
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
	{

		MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, entityplayer, true);

		if (movingobjectposition == null || hasCaptured(itemstack))
		{
			EntityThrownBottle bottle = new EntityThrownBottle(world, entityplayer, itemstack.splitStack(1));
			if(!world.isRemote) world.spawnEntityInWorld(bottle);
			return itemstack;
		}
		if (hasCaptured(itemstack))
		{
			return itemstack;
		}
		return super.onItemRightClick(itemstack, world, entityplayer);
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer par2EntityPlayer, World world, int i, int j, int k, int l, float par8, float par9, float par10)
	{
		return tryPlace(itemstack, world, i, j, k, l);
	}

	@Override
	public boolean hitEntity(ItemStack itemstack, EntityLivingBase usedon, EntityLivingBase user)
	{
		float i = 1;
		if(user instanceof EntityPlayer)
		{
			float var2 = (float)user.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
			float var4 = 0;
			if (var2 > 0.0F || var4 > 0.0F)
            {
                boolean var5 = user.fallDistance > 0.0F && !user.onGround && !user.isOnLadder() && !user.isInWater() && !user.isPotionActive(Potion.blindness) && user.ridingEntity == null;

                if (var5 && var2 > 0.0F)
                {
                    var2 *= 1.5F;
                }

                var2 += var4;
                i = var2;
            }
		}
		usedon.heal(i);
		itemstack = capture(itemstack, usedon);

		return false;
	}

	public static ItemStack capture(ItemStack itemstack, Entity usedon)
	{
		if(itemstack == null || usedon == null) return null;
		if (!hasCaptured(itemstack) && !(usedon instanceof EntityPlayer) && itemstack.stackSize == 1)
		{
			NBTTagCompound mob = new NBTTagCompound();
			if(usedon instanceof EntityLivingBase)
			{
				EntityLivingBase ent = (EntityLivingBase)usedon;
				if(ent.isDead) ent.setHealth(2);
			}
			usedon.writeToNBT(mob);
			mob.setString("id", EntityList.getEntityString(usedon));

			NBTTagCompound item = itemstack.stackTagCompound;
			if(item == null) item = new NBTTagCompound();
			item.setTag("mob", mob);
			if(usedon instanceof EntityLiving)
			{
			if(((EntityLiving)usedon).getCustomNameTag() != null && ((EntityLiving)usedon).getCustomNameTag().length() > 0)
            {
            	if (!item.hasKey("display"))
                {
                    item.setTag("display", new NBTTagCompound());
                }

                item.getCompoundTag("display").setString("Name", ((EntityLiving)usedon).getCustomNameTag());
            }
			}
			itemstack.setTagCompound(item);
			usedon.setDead();

		}
		return itemstack;
	}

	public static boolean hasCaptured(ItemStack itemstack)
	{
		if(itemstack != null)
		{
			if (itemstack.stackTagCompound != null)
			{
				if (itemstack.stackTagCompound.hasKey("mob"))
				{
					return true;
				}
			}
		}
		return false;
	}

	public static boolean tryPlace(ItemStack itemstack, World world, int i, int j, int k, int l)
	{
		if(itemstack == null) return false;
		if (hasCaptured(itemstack) && itemstack.stackSize == 1)
		{
			NBTTagCompound mob = itemstack.stackTagCompound.getCompoundTag("mob");
			Entity ent = EntityList.createEntityFromNBT(mob, world);

			if (ent == null)
			{
				if(itemstack.stackTagCompound.hasKey("mob"))
				{
					itemstack.stackTagCompound.removeTag("mob");
				}
				return false;
			}

			Block i1 = world.getBlock(i, j, k);
			boolean flag = false;
			if(l != 0)
			{
				if(i1.getBlockBoundsMaxY() < j + 0.5 || !i1.canCollideCheck(world.getBlockMetadata(i, j, k), false) || i1.getCollisionBoundingBoxFromPool(world, i, j, k) == null) flag = true;
			}
			if(!flag)
			{
				i += Facing.offsetsXForSide[l];
				j += Facing.offsetsYForSide[l];
				k += Facing.offsetsZForSide[l];
			}
			double d = 0.0D;

			if (l == 1 && i1 == Blocks.fence || i1 == Blocks.nether_brick_fence)
			{
				d = 0.5D;
			}

			ent.setLocationAndAngles(i, j + d, k, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360F), 0.0F);
			if(ent instanceof EntityLiving)
			{
				((EntityLiving)ent).rotationYawHead = ((EntityLiving)ent).rotationYaw;
				((EntityLiving)ent).renderYawOffset = ((EntityLiving)ent).rotationYaw;
				((EntityLiving)ent).playLivingSound();
			}
			if (ent instanceof EntityLiving && itemstack.stackTagCompound.hasKey("display"))
			{
				NBTTagCompound var2 = itemstack.stackTagCompound.getCompoundTag("display");

				if (var2.hasKey("Name"))
				{
					((EntityLiving)ent).setCustomNameTag(var2.getString("Name"));
					((EntityLiving)ent).setAlwaysRenderNameTag(itemstack.stackTagCompound.hasKey("display"));
				}

				itemstack.stackTagCompound.removeTag("display");
			}
			if (!world.isRemote) world.spawnEntityInWorld(ent);
			
			itemstack.stackTagCompound.removeTag("mob");
			if(!itemstack.stackTagCompound.hasKey("display")) itemstack.stackTagCompound = null;
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public String getUnlocalizedNameInefficiently(ItemStack par1ItemStack) {
		return getUnlocalizedName(par1ItemStack);
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return hasCaptured(par1ItemStack) ? "item.MobBottleFull" : "item.MobBottleEmpty";
	}
	
	

}
