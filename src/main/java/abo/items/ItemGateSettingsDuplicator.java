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

package abo.items;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import buildcraft.api.gates.ActionManager;
import buildcraft.api.gates.GateExpansionController;
import buildcraft.api.gates.GateExpansions;
import buildcraft.api.gates.IGateExpansion;
import buildcraft.api.gates.TriggerParameter;
import buildcraft.core.gui.AdvancedSlot;
import buildcraft.core.utils.StringUtils;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.Gate;
import buildcraft.transport.Pipe;
import buildcraft.transport.gates.GateDefinition.GateLogic;
import buildcraft.transport.gates.GateDefinition.GateMaterial;

/**
 * @author Flow86
 * 
 */
public class ItemGateSettingsDuplicator extends ABOItem {

	public ItemGateSettingsDuplicator() {
		super();
		setMaxStackSize(1);
		setNoRepair();
		setFull3D();
	}

	@Override
	public boolean onItemUseFirst(ItemStack itemStack, EntityPlayer entityPlayer, World worldObj, int x, int y, int z, int side, float var8, float var9, float var10) {
		if (worldObj.isRemote)
			return super.onItemUseFirst(itemStack, entityPlayer, worldObj, x, y, z, side, var8, var9, var10);

		Pipe pipe = BlockGenericPipe.getPipe(worldObj, x, y, z);
		
		if(BlockGenericPipe.isValid(pipe))
		{
			if(pipe.hasGate())
			{
				if(itemStack.hasTagCompound())
				{
					if(areGatesEqual(itemStack, pipe.gate))
					{
						pasteSettings(itemStack, pipe.gate);
						return true;
					}
				}else
				{
					itemStack.setTagCompound(copySettings(pipe.gate));
					return true;
				}
			}
		}else if(worldObj.getBlock(x, y, z) == Blocks.redstone_block)
		{
			itemStack.stackTagCompound = null;
			return true;
		}

		return super.onItemUseFirst(itemStack, entityPlayer, worldObj, x, y, z, side, var8, var9, var10);
	}
	
	private boolean areGatesEqual(ItemStack itemStack, Gate gate)
	{
		NBTTagCompound itemNBT = itemStack.getTagCompound();
		NBTTagCompound nbt = itemNBT.getCompoundTag("GateIdentifier");
		if (nbt.hasKey("material")) {
			try {
				if(gate.material != GateMaterial.valueOf(nbt.getString("material"))) return false;
			} catch (IllegalArgumentException ex) {
				return false;
			}
		}
		if (nbt.hasKey("logic")) {
			try {
				if(gate.logic != GateLogic.valueOf(nbt.getString("logic"))) return false;
			} catch (IllegalArgumentException ex) {
				return false;
			}
		}
		NBTTagList exList = nbt.getTagList("expansions", Constants.NBT.TAG_COMPOUND);
		BiMap<IGateExpansion, GateExpansionController> expansions = HashBiMap.create();
		for (int i = 0; i < exList.tagCount(); i++) {
			NBTTagCompound conNBT = exList.getCompoundTagAt(i);
			IGateExpansion ex = GateExpansions.getExpansion(conNBT.getString("type"));
			if (ex != null) {
				GateExpansionController con = ex.makeController(gate.pipe.container);
				con.readFromNBT(conNBT.getCompoundTag("data"));
				expansions.put(ex, con);
			}
		}
		if(!gate.expansions.equals(expansions)) return false;
		return true;
	}
	
	private void pasteSettings(ItemStack itemStack, Gate gate)
	{
		NBTTagCompound itemNBT = itemStack.getTagCompound();
		NBTTagCompound data = itemNBT.getCompoundTag("GateSettings");
		for (int i = 0; i < 8; ++i) {
			if (data.hasKey("trigger[" + i + "]")) {
				gate.triggers[i] = ActionManager.triggers.get(data.getString("trigger[" + i + "]"));
			}
			if (data.hasKey("action[" + i + "]")) {
				gate.actions[i] = ActionManager.actions.get(data.getString("action[" + i + "]"));
			}
			if (data.hasKey("triggerParameters[" + i + "]")) {
				gate.triggerParameters[i] = new TriggerParameter();
				gate.triggerParameters[i].readFromNBT(data.getCompoundTag("triggerParameters[" + i + "]"));
			}
		}
	}
	
	private NBTTagCompound copySettings(Gate gate)
	{
		NBTTagCompound itemNBT = new NBTTagCompound();
		NBTTagCompound gateIdentifier = new NBTTagCompound();
		gateIdentifier.setString("material", gate.material.name());
		gateIdentifier.setString("logic", gate.logic.name());
		NBTTagList exList = new NBTTagList();
		for (GateExpansionController con : gate.expansions.values()) {
			NBTTagCompound conNBT = new NBTTagCompound();
			conNBT.setString("type", con.getType().getUniqueIdentifier());
			NBTTagCompound conData = new NBTTagCompound();
			con.writeToNBT(conData);
			conNBT.setTag("data", conData);
			exList.appendTag(conNBT);
		}
		gateIdentifier.setTag("expansions", exList);
		itemNBT.setTag("GateIdentifier", gateIdentifier);
		
		NBTTagCompound gateSettings = new NBTTagCompound();
		for (int i = 0; i < 8; ++i) {
			if (gate.triggers[i] != null) {
				gateSettings.setString("trigger[" + i + "]", gate.triggers[i].getUniqueTag());
			}
			if (gate.actions[i] != null) {
				gateSettings.setString("action[" + i + "]", gate.actions[i].getUniqueTag());
			}
			if (gate.triggerParameters[i] != null) {
				NBTTagCompound cpt = new NBTTagCompound();
				gate.triggerParameters[i].writeToNBT(cpt);
				gateSettings.setTag("triggerParameters[" + i + "]", cpt);
			}
		}
		itemNBT.setTag("GateSettings", gateSettings);
		return itemNBT;
	}
}
