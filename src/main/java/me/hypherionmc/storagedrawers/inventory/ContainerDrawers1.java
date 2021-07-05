package me.hypherionmc.storagedrawers.inventory;

import me.hypherionmc.storagedrawers.block.tile.TileEntityDrawers;
import me.hypherionmc.storagedrawers.core.ModContainers;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;

public class ContainerDrawers1 extends ContainerDrawers
{
    private static final int[][] slotCoordinates = new int[][] {
        { 80, 36 }
    };

    public ContainerDrawers1 (int windowId, PlayerInventory playerInv, PacketByteBuf data) {
        super(ModContainers.DRAWER_CONTAINER_1, windowId, playerInv, data);
    }

    public ContainerDrawers1 (int windowId, PlayerInventory playerInventory, TileEntityDrawers tileEntity) {
        super(ModContainers.DRAWER_CONTAINER_1, windowId, playerInventory, tileEntity);
    }

    @Override
    protected int getStorageSlotX (int slot) {
        return slotCoordinates[slot][0];
    }

    @Override
    protected int getStorageSlotY (int slot) {
        return slotCoordinates[slot][1];
    }
}
