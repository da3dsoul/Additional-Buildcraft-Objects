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

package abo.pipes.items;

import java.util.LinkedList;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraftforge.common.util.ForgeDirection;

import abo.ABO;
import abo.PipeIconProvider;
import abo.pipes.ABOPipe;

import buildcraft.api.core.Position;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile;
import buildcraft.api.transport.IPipeTile.PipeType;
import buildcraft.core.inventory.InvUtils;
import buildcraft.core.inventory.StackHelper;
import buildcraft.core.inventory.Transactor;
import buildcraft.core.inventory.TransactorSimple;
import buildcraft.transport.BlockGenericPipe;
import buildcraft.transport.IPipeConnectionForced;
import buildcraft.transport.IPipeTransportItemsHook;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TileGenericPipe;
import buildcraft.transport.TravelingItem;
import buildcraft.transport.pipes.events.PipeEventItem;

/**
 * This pipe will always prefer to insert it's objects into a tile that is not a pipe over another pipe.
 * 
 * @author Scott Chamberlain (Leftler) ported to BC > 2.2 by Flow86
 */
public class PipeItemsInsertion extends ABOPipe<PipeTransportItems> implements IPipeTransportItemsHook {

	public PipeItemsInsertion(Item itemID) {
		super(new PipeTransportItems(), itemID);

		transport.allowBouncing = true;
	}

	@Override
	public int getIconIndex(ForgeDirection direction) {
		return PipeIconProvider.PipeItemsInsertion;
	}
	
	@Override
	public boolean blockActivated(EntityPlayer entityplayer) {
		TileEntity tile = null;
		for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
		{
			tile = getWorld().getTileEntity(container.xCoord + side.offsetX, container.yCoord + side.offsetY, container.zCoord + side.offsetZ);
			if(tile != null && tile instanceof TileEntityEnderChest) break;
		}
		if(tile == null)
		{
			return false;
		}
		if (getWorld().isRemote)
        {
            return true;
        }
        else
        {
            ABO.instance.getInventoryEnderChest().func_146031_a((TileEntityEnderChest) tile);
            entityplayer.displayGUIChest(ABO.instance.getInventoryEnderChest());
            return true;
        }
	}

	@Override
	public LinkedList<ForgeDirection> filterPossibleMovements(LinkedList<ForgeDirection> possibleOrientations, Position pos, TravelingItem item) {
		LinkedList<ForgeDirection> nonPipesList = new LinkedList<ForgeDirection>();
		LinkedList<ForgeDirection> pipesList = new LinkedList<ForgeDirection>();

		item.blacklist.add(item.input.getOpposite());

		for (ForgeDirection o : possibleOrientations) {
			if (!item.blacklist.contains(o) && container.pipe.outputOpen(o)) {
				if (transport.canReceivePipeObjects(o, item)) {

					TileEntity entity = container.getTile(o);
					if (entity instanceof IPipeTile)
						pipesList.add(o);
					else
						nonPipesList.add(o);
				}
			}
		}

		if (!nonPipesList.isEmpty())
			return nonPipesList;

		return pipesList;
	}

	@Override
	public void entityEntered(TravelingItem item, ForgeDirection orientation) {
	}

	@Override
	public void readjustSpeed(TravelingItem item) {
		transport.defaultReajustSpeed(item);
	}

	@Override
	public boolean canPipeConnect(TileEntity tile, ForgeDirection side) {
		if(tile instanceof TileEntityEnderChest) return true;
		return super.canPipeConnect(tile, side);
	}
	
	public void eventHandler(PipeEventItem.ReachedCenter event) {
		TileEntity tile = null;
		TravelingItem item = event.item;
		for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
		{
			tile = getWorld().getTileEntity(container.xCoord + side.offsetX, container.yCoord + side.offsetY, container.zCoord + side.offsetZ);
			if(tile != null && tile instanceof TileEntityEnderChest) break;
		}
		if(tile == null)
		{
			return;
		}
		if (tile instanceof TileEntityEnderChest) {
			if (!container.getWorldObj().isRemote) {
				if (item.getInsertionHandler().canInsertItem(item, (IInventory) ABO.instance.getInventoryEnderChest())) {
					ItemStack added = new TransactorSimple(InvUtils.getInventory((IInventory) ABO.instance.getInventoryEnderChest())).add(item.getItemStack(), item.output.getOpposite(), true);
					item.getItemStack().stackSize -= added.stackSize;
				}

				if (item.getItemStack().stackSize <= 0) {
					transport.items.scheduleRemoval(item);
				}
			}
		}
	}
	
	
}
