package da3dsoul.scaryGen.items;

import java.util.ArrayList;
import java.util.List;

import da3dsoul.scaryGen.projectile.EntityThrownBottle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.world.World;

public class ItemBottle extends ItemGlassBottle {
	public ItemBottle() {
		super();
	}

	/**
	 * Called whenever this item is equipped and the right mouse button is
	 * pressed. Args: itemStack, world, entityPlayer
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {

		MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, entityplayer, false);

		if (movingobjectposition == null || hasCaptured(itemstack)) {
			EntityThrownBottle bottle = new EntityThrownBottle(world, entityplayer, itemstack.splitStack(1));
			if (!world.isRemote) world.spawnEntityInWorld(bottle);
			return itemstack;
		}
		if (hasCaptured(itemstack)) { return itemstack; }
		return itemstack;
	}

	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer par2EntityPlayer, World world, int i, int j, int k,
			int l, float par8, float par9, float par10) {
		return tryPlace(itemstack, world, i, j, k, l);
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
        try {
            ItemStack item;
            if (stack.stackSize > 1) {
                item = stack.splitStack(1);
                if (player instanceof EntityPlayer) {
                    item = capture(item, player, entity);
                    if (item == null) return false;
                    if (!((EntityPlayer) player).inventory.addItemStackToInventory(item)) {
                        ((EntityPlayer) player).dropPlayerItemWithRandomChoice(item, false);
                    }
                }
            } else {
                stack = capture(stack, player, entity);
            }
            return true;
        } catch(Throwable t) {
            t.printStackTrace();
            return false;
        }
	}

	public static ItemStack capture(ItemStack itemstack, Entity user, Entity usedon) {
		if (itemstack == null || usedon == null) return null;
		if (!hasCaptured(itemstack) && !(usedon instanceof EntityPlayer) && itemstack.stackSize == 1) {
            if(EntityList.getEntityString(usedon) == null || EntityList.getEntityString(usedon).isEmpty()) return itemstack;
			NBTTagCompound mob = new NBTTagCompound();
			if (usedon instanceof EntityLivingBase) {
				EntityLivingBase ent = (EntityLivingBase) usedon;
				if (ent.isDead) ent.setHealth(2);
			}
			if (usedon.riddenByEntity != null) usedon.riddenByEntity = null;
			usedon.writeToNBT(mob);
			mob.setString("id", EntityList.getEntityString(usedon));

			NBTTagCompound item = itemstack.stackTagCompound;
			if (item == null) item = new NBTTagCompound();
			item.setTag("mob", mob);
			itemstack.setTagCompound(item);
			usedon.setDead();

		}
		return itemstack;
	}

	public static boolean hasCaptured(ItemStack itemstack) {
		if (itemstack != null) {
			if (itemstack.stackTagCompound != null) {
				if (itemstack.stackTagCompound.hasKey("mob")) { return true; }
			}
		}
		return false;
	}

    public String getUnlocalizedMobName(ItemStack itemstack) {
        if (itemstack == null) return "";
        if (hasCaptured(itemstack)) {
            NBTTagCompound mob = itemstack.stackTagCompound.getCompoundTag("mob");

            return mob.getString("id");
        }
        return "";
    }

    public String getLocalizedMobName(ItemStack itemstack) {
        if (hasCaptured(itemstack)) {
            NBTTagCompound mob = itemstack.stackTagCompound.getCompoundTag("mob");

            String s = "entity." + mob.getString("id") + ".name";
            String s1 = t("entity." + mob.getString("id"));
            if(s1 == null || s1.isEmpty() || s.equals(s1)) return "";
            return s1;
        }
        return "";
    }

    public boolean mobHasCustomName(ItemStack itemstack) {
        if(!hasCaptured(itemstack)) return false;
        NBTTagCompound tag = itemstack.stackTagCompound.getCompoundTag("mob");
        if (tag.hasKey("CustomName") && tag.getString("CustomName").length() > 0){
            return true;
        }
        return false;
    }

    public String getMobCustomName(ItemStack itemstack) {
        if(!hasCaptured(itemstack)) return "";
        NBTTagCompound tag = itemstack.stackTagCompound.getCompoundTag("mob");
        if (tag.hasKey("CustomName") && tag.getString("CustomName").length() > 0){
            return tag.getString("CustomName");
        }
        return "";
    }

	public static boolean tryPlace(ItemStack itemstack, World world, int i, int j, int k, int l) {
		if (itemstack == null) return false;
		if (hasCaptured(itemstack) && itemstack.stackSize == 1) {
			NBTTagCompound mob = itemstack.stackTagCompound.getCompoundTag("mob");
			Entity ent = EntityList.createEntityFromNBT(mob, world);

			if (ent == null) {
				if (itemstack.stackTagCompound.hasKey("mob")) {
					itemstack.stackTagCompound.removeTag("mob");
				}
				return false;
			}
			ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>(1);
			if (ent instanceof EntityMinecart) {
				boolean flag = false;
				if (l != 0) {
					Block i1 = world.getBlock(i, j, k);
					i1.addCollisionBoxesToList(world, i, j, k,
							AxisAlignedBB.getBoundingBox(i - 0.5, j + 0.475, k - 0.5, i + 0.5, j + 1.25, k + 0.5),
							list, null);
					if (list.isEmpty() || !i1.canCollideCheck(world.getBlockMetadata(i, j, k), false)) flag = true;
				}
				if (!flag) {
					i += Facing.offsetsXForSide[l];
					j += Facing.offsetsYForSide[l];
					k += Facing.offsetsZForSide[l];
				}
				if (BlockRailBase.func_150051_a(world.getBlock(i, j, k))) {
					if (itemstack.stackTagCompound.hasKey("display")) {
						NBTTagCompound var2 = itemstack.stackTagCompound.getCompoundTag("display");

						if (var2.hasKey("Name")) {
							((EntityMinecart) ent).setMinecartName(var2.getString("Name"));
						}
					}

					ent.setLocationAndAngles(i + 0.5, j + 0.5, k + 0.5, ent.rotationYaw, ent.rotationPitch);
					ent.timeUntilPortal = ent.getMaxInPortalTime();

					if (!world.isRemote) world.spawnEntityInWorld(ent);

					itemstack.stackTagCompound.removeTag("mob");
					if (!itemstack.stackTagCompound.hasKey("display")) itemstack.stackTagCompound = null;
					return true;
				} else {
					return false;
				}
			}

			Block i1 = world.getBlock(i, j, k);
			boolean flag = false;
			if (l != 0) {
				i1.addCollisionBoxesToList(world, i, j, k,
						AxisAlignedBB.getBoundingBox(i - 0.5, j + 0.475, k - 0.5, i + 0.5, j + 1.25, k + 0.5), list,
						null);
				if (list.isEmpty() || !i1.canCollideCheck(world.getBlockMetadata(i, j, k), false)) flag = true;
			}
			double d = 0.0D;

			if (l == 1) {
				for (AxisAlignedBB bb : list) {
					if (bb != null && bb.maxY - j > d) d = bb.maxY - j;
				}
			} else if (l == 0) {
				if (!flag) {
					d = -ent.height + 0.0625D;
				} else {
					for (AxisAlignedBB bb : list) {
						if (bb != null && bb.maxY - j > d) d = bb.maxY - j;
					}
				}
			} else if (!flag) {
				i += Facing.offsetsXForSide[l];
				k += Facing.offsetsZForSide[l];
				list.clear();
				i1.addCollisionBoxesToList(world, i, j, k,
						AxisAlignedBB.getBoundingBox(i - 0.5, j + 0.475, k - 0.5, i + 0.5, j + 1.25, k + 0.5), list,
						null);
				if (!(list.isEmpty() || !i1.canCollideCheck(world.getBlockMetadata(i, j, k), false))) {
					for (AxisAlignedBB bb : list) {
						if (bb != null && bb.maxY - j > d) d = bb.maxY - j;
					}
				}
			}

			ent.setLocationAndAngles(i + 0.5, j + d, k + 0.5,
					MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360F), 0.0F);
			if (ent instanceof EntityLiving) {
				((EntityLiving) ent).rotationYawHead = ((EntityLiving) ent).rotationYaw;
				((EntityLiving) ent).renderYawOffset = ((EntityLiving) ent).rotationYaw;
				((EntityLiving) ent).playLivingSound();
			}
			if (ent instanceof EntityLiving && itemstack.stackTagCompound.hasKey("display")) {
				NBTTagCompound var2 = itemstack.stackTagCompound.getCompoundTag("display");

				if (var2.hasKey("Name")) {
					((EntityLiving) ent).setCustomNameTag(var2.getString("Name"));
					((EntityLiving) ent).setAlwaysRenderNameTag(true);
                    ((EntityLiving) ent).func_110163_bv();
				}

				itemstack.stackTagCompound.removeTag("display");
			}
			if (!world.isRemote) world.spawnEntityInWorld(ent);

			itemstack.stackTagCompound.removeTag("mob");
			if (!itemstack.stackTagCompound.hasKey("display")) itemstack.stackTagCompound = null;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String getUnlocalizedNameInefficiently(ItemStack par1ItemStack) {
		return getUnlocalizedName(par1ItemStack);
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
        return "item.MobBottleEmpty";
	}

    @Override
    public String getItemStackDisplayName(ItemStack par1ItemStack) {
        if(hasCaptured(par1ItemStack)){
            if(!getLocalizedMobName(par1ItemStack).isEmpty()) {
                if(mobHasCustomName(par1ItemStack)){
                    return t("item.MobBottleEmpty") + " (" + getMobCustomName(par1ItemStack) + ")";
                } else {
                    return t("item.MobBottleEmpty") + " (" + getLocalizedMobName(par1ItemStack) + ")";
                }
            }else{
                if(mobHasCustomName(par1ItemStack)){
                    return t("item.MobBottleEmpty") + " (" + getMobCustomName(par1ItemStack) + ")";
                } else {
                    return t("item.MobBottleFull");
                }
            }
        }else{
            return t("item.MobBottleEmpty");
        }
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean isAdvanced) {

		if(hasCaptured(itemstack) && getLocalizedMobName(itemstack) == ""){
			if(GuiScreen.isShiftKeyDown()) {
                list.add(t("item.MobBottleNoEntityName0"));
				list.add(t("item.MobBottleNoEntityName1"));
				list.add(t("item.MobBottleNoEntityName2") + " " + getUnlocalizedMobName(itemstack));
            } else {
				list.add(t("item.MobBottlePressShift"));
			}
        }
    }

    private String t(String s) {
        return ("" + StatCollector.translateToLocal(s + ".name")).trim();
    }
}
