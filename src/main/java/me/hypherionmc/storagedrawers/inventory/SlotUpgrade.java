package me.hypherionmc.storagedrawers.inventory;

import me.hypherionmc.storagedrawers.core.ModItems;
import me.hypherionmc.storagedrawers.item.EnumUpgradeStorage;
import me.hypherionmc.storagedrawers.item.ItemUpgradeStorage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import javax.annotation.Nonnull;

public class SlotUpgrade extends Slot
{
    public SlotUpgrade (Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canInsert(@Nonnull ItemStack stack) {
        if (stack.isEmpty())
            return false;

        if (inventory instanceof InventoryUpgrade)
            return ((InventoryUpgrade) inventory).canAddUpgrade(stack);

        return false;
    }

    @Override
    public boolean canTakeItems (PlayerEntity player) {
        if (inventory instanceof InventoryUpgrade) {
            ItemStack stack = getStack();
            if (stack.getItem() instanceof ItemUpgradeStorage) {
                EnumUpgradeStorage upgrade = ((ItemUpgradeStorage)stack.getItem()).level;
                return ((InventoryUpgrade) inventory).canRemoveStorageUpgrade(getSlotIndex());
            }

            if (player != null && !player.isCreative()) {
                if (stack.getItem() == ModItems.CREATIVE_STORAGE_UPGRADE || stack.getItem() == ModItems.CREATIVE_VENDING_UPGRADE)
                    return false;
            }
        }

        return true;
    }

    public boolean canTakeStack () {
        return canTakeStack(null);
    }
}
