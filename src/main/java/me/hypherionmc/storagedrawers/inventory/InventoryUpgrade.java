package me.hypherionmc.storagedrawers.inventory;

import me.hypherionmc.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class InventoryUpgrade implements Inventory
{
    private static final int upgradeCapacity = 7;

    private TileEntityDrawers tile;

    public InventoryUpgrade (TileEntityDrawers tileEntity) {
        tile = tileEntity;
    }

    @Override
    public int size () {
        return upgradeCapacity;
    }

    @Override
    public boolean isEmpty () {
        for (int i = 0; i < upgradeCapacity; i++) {
            if (!tile.upgrades().getUpgrade(i).isEmpty())
                return false;
        }

        return true;
    }

    @Override
    @Nonnull
    public ItemStack getStack (int slot) {
        return tile.upgrades().getUpgrade(slot);
    }

    @Override
    @Nonnull
    public ItemStack removeStack (int slot, int count) {
        ItemStack stack = tile.upgrades().getUpgrade(slot);
        if (count > 0)
            tile.upgrades().setUpgrade(slot, ItemStack.EMPTY);

        return stack;
    }

    @Override
    @Nonnull
    public ItemStack removeStack (int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack (int slot, @Nonnull ItemStack item) {
        tile.upgrades().setUpgrade(slot, item);
    }

    @Override
    public int getMaxCountPerStack () {
        return 1;
    }

    @Override
    public void markDirty () {
        tile.markDirty();
    }

    @Override
    public boolean canPlayerUse (PlayerEntity player) {
        return true;
    }

    @Override
    public void onOpen (PlayerEntity player) { }

    @Override
    public void onClose (PlayerEntity player) { }

    @Override
    public boolean isValid (int slot, @Nonnull ItemStack item) {
        return tile.upgrades().canAddUpgrade(item);
    }

    @Override
    public void clear () {

    }

    public boolean canAddUpgrade (@Nonnull ItemStack item) {
        return tile.upgrades().canAddUpgrade(item);
    }

    public boolean canRemoveStorageUpgrade (int slot) {
        return tile.upgrades().canRemoveUpgrade(slot);
    }
}
