package me.hypherionmc.storagedrawers.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;

public class SlotStorage extends Slot
{
    public SlotStorage (Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canTakeItems (PlayerEntity player) {
        return false;
    }
}
