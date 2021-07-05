package me.hypherionmc.storagedrawers.inventory;

import me.hypherionmc.storagedrawers.block.tile.TileEntityDrawers;
import me.hypherionmc.storagedrawers.core.ModContainers;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;

public class ContainerDrawers4 extends ContainerDrawers
{
    private static final int[][] slotCoordinates = new int[][] {
        { 67, 23 }, { 93, 23 }, { 67, 49 }, { 93, 49 }
    };

    public ContainerDrawers4 (int windowId, PlayerInventory playerInv, PacketByteBuf data) {
        super(ModContainers.DRAWER_CONTAINER_4, windowId, playerInv, data);
    }

    public ContainerDrawers4 (int windowId, PlayerInventory playerInventory, TileEntityDrawers tileEntity) {
        super(ModContainers.DRAWER_CONTAINER_4, windowId, playerInventory, tileEntity);
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
