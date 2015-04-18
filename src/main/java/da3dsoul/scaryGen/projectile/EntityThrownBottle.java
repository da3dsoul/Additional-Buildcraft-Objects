package da3dsoul.scaryGen.projectile;

import net.minecraft.entity.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import da3dsoul.scaryGen.items.ItemBottle;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityThrownBottle extends EntityPotion {
	ItemStack	itemstack	= null;

	public EntityThrownBottle(World par1World, ItemStack itemstack) {
		super(par1World);
		this.itemstack = itemstack;
	}

	public EntityThrownBottle(World p_i1792_1_, double p_i1792_2_, double p_i1792_4_, double p_i1792_6_, ItemStack p_i1792_8_) {
		super(p_i1792_1_, p_i1792_2_, p_i1792_4_, p_i1792_6_, p_i1792_8_);
		itemstack = p_i1792_8_;
	}

	public EntityThrownBottle(World par1World, EntityLivingBase par2EntityLivingBase, ItemStack itemstack) {
		super(par1World, par2EntityLivingBase, 0);
		this.itemstack = itemstack;
	}

	/**
	 * Called when this EntityThrowable hits a block or entity.
	 */
	protected void onImpact(MovingObjectPosition par1MovingObjectPosition) {
		if(par1MovingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			if(worldObj.getBlock(par1MovingObjectPosition.blockX, par1MovingObjectPosition.blockY, par1MovingObjectPosition.blockZ) == Blocks.portal) {
				return;
			}
		}
		if (par1MovingObjectPosition.entityHit != null) {

			if (!(par1MovingObjectPosition.entityHit instanceof EntityPlayer)) {
				if (!ItemBottle.hasCaptured(itemstack) && itemstack.stackSize == 1) {
					itemstack = ItemBottle.capture(itemstack, this, par1MovingObjectPosition.entityHit);
				}
			}
		} else if (ItemBottle.hasCaptured(itemstack)) {
			// NBTTagCompound mob =
			// itemstack.stackTagCompound.getCompoundTag("mob");
			int i = par1MovingObjectPosition.blockX;
			int j = par1MovingObjectPosition.blockY;
			int k = par1MovingObjectPosition.blockZ;
			int l = par1MovingObjectPosition.sideHit;
			ItemBottle.tryPlace(itemstack, worldObj, i, j, k, l);
		}
		drop();
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		super.onUpdate();
	}

	private void drop() {
		EntityItem item = new EntityItem(worldObj, posX, posY, posZ, itemstack);
		item.motionX = -motionX * 0.5D;
		item.motionZ = -motionZ * 0.5D;
		item.motionY = -motionY * 0.5D;
		if (!worldObj.isRemote) worldObj.spawnEntityInWorld(item);
		if (!this.worldObj.isRemote) {
			this.setDead();
		}
	}

	public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readEntityFromNBT(par1NBTTagCompound);

		if (par1NBTTagCompound.hasKey("Potion")) {
			this.itemstack = ItemStack.loadItemStackFromNBT(par1NBTTagCompound.getCompoundTag("Potion"));
		} else {
			this.setPotionDamage(par1NBTTagCompound.getInteger("potionValue"));
		}

		if (this.itemstack == null) {
			this.setDead();
		}
	}

	protected float func_70182_d() {
		return 1.5F;
	}

	protected float func_70183_g() {
		return 0.0F;
	}

	protected float getGravityVelocity() {
		return 0.03F;
	}

	/**
	 * (abstract) Protected helper method to write subclass entity data to NBT.
	 */
	public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeEntityToNBT(par1NBTTagCompound);

		if (this.itemstack != null) {
			par1NBTTagCompound.setTag("Potion", this.itemstack.writeToNBT(new NBTTagCompound()));
		}
	}
}
