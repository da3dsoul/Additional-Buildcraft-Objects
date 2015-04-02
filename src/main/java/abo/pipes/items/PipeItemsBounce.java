package abo.pipes.items;

import java.util.LinkedList;
import java.util.List;


import abo.PipeIconProvider;
import abo.PipeIcons;
import abo.pipes.ABOPipe;
import buildcraft.api.core.Position;
import buildcraft.transport.ISolidSideTile;
import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TravelingItem;
import buildcraft.transport.pipes.events.PipeEventItem;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.common.util.ForgeDirection;

public class PipeItemsBounce extends ABOPipe<PipeTransportItems> implements ISolidSideTile {
    private final int openTexture = PipeIcons.PipeItemsBounceOpen.ordinal();
    private final int closedTexture = PipeIcons.PipeItemsBounceClosed.ordinal();

    public PipeItemsBounce(Item itemID) {
        super(new PipeTransportItems(), itemID);
        transport.allowBouncing = true;
    }

    @Override
    public int getIconIndex(ForgeDirection direction) {
        if (container != null && container.getWorldObj() != null)
            return (container.getWorldObj().isBlockIndirectlyGettingPowered(container.xCoord, container.yCoord, container.zCoord) ? openTexture : closedTexture);
        return closedTexture;
    }

    public void eventHandler(PipeEventItem.FindDest event) {
        List possibleOrientations = event.destinations;

        if (container.getWorldObj().isBlockIndirectlyGettingPowered(container.xCoord, container.yCoord, container.zCoord)) {
            return;
        }

        TravelingItem item = event.item;

        // if unpowered - reverse all items
        LinkedList<ForgeDirection> reverse = new LinkedList<ForgeDirection>();

        reverse.add(item.input.getOpposite());

        event.destinations.clear();
        event.destinations.addAll(reverse);
    }

    @Override
    public boolean isSolidOnSide(ForgeDirection side) {
        if (getWorld()
                .getBlock(container.xCoord + side.offsetX, container.yCoord + side.offsetY,
                        container.zCoord + side.offsetZ).getMaterial().isReplaceable()
                || getWorld().getBlock(container.xCoord + side.offsetX, container.yCoord + side.offsetY,
                container.zCoord + side.offsetZ).getMaterial() == Material.circuits) { return true; }
        return false;
    }
}