package me.hypherionmc.storagedrawers.inventory;

import me.hypherionmc.storagedrawers.api.storage.IDrawer;
import me.hypherionmc.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SlotDrawer extends Slot
{
    private static Inventory emptyInventory = new EmptyInventory();
    private ContainerDrawers container;
    private final IDrawerGroup group;
    private final IDrawer drawer;

    public SlotDrawer (ContainerDrawers container, IDrawerGroup drawerGroup, int index, int xPosition, int yPosition) {
        super(emptyInventory, index, xPosition, yPosition);
        this.container = container;
        this.group = drawerGroup;
        this.drawer = group.getDrawer(index);
    }

    @Override
    public boolean canInsert (@Nonnull ItemStack stack) {
        return !stack.isEmpty() && drawer.canItemBeStored(stack);
    }

    @Override
    @Nonnull
    public ItemStack getStack () {
        ItemStack stack = ItemStackHelper.encodeItemStack(drawer.getStoredItemPrototype(), drawer.getStoredItemCount());
        container.setLastAccessedItem(stack);
        return stack;
    }

    @Override
    public void setStack (@Nonnull ItemStack stack) {
        IDrawer target = drawer.setStoredItem(stack);
        stack = ItemStackHelper.decodeItemStack(stack);
        target.setStoredItemCount(stack.getCount());
    }

    @Override
    public void onQuickTransfer (@Nonnull ItemStack p_75220_1_, @Nonnull ItemStack p_75220_2_) {

    }

    @Override
    public int getMaxItemCount (@Nonnull ItemStack stack) {
        return Math.min(stack.getMaxCount(), drawer.getRemainingCapacity());
    }

    @Override
    public boolean canTakeItems (PlayerEntity playerIn) {
        return false;
    }

    @Override
    @Nonnull
    public ItemStack takeStack (int amount) {
        int withdraw = Math.min(amount, drawer.getStoredItemCount());
        drawer.setStoredItemCount(withdraw);

        ItemStack stack = drawer.getStoredItemPrototype().copy();
        stack.setCount(drawer.getStoredItemCount() - withdraw);
        return stack;
    }

    public IDrawerGroup getDrawerGroup () {
        return group;
    }

    @Override
    public boolean isSameInventory (Slot other) {
        return other instanceof SlotDrawer && ((SlotDrawer) other).getDrawerGroup() == group;
    }

    static class EmptyInventory implements SidedInventory, Inventory {

        @Override
        public int[] getAvailableSlots(Direction side) {
            return new int[0];
        }

        @Override
        public boolean canInsert(int slot, ItemStack stack, @org.jetbrains.annotations.Nullable Direction dir) {
            return false;
        }

        @Override
        public boolean canExtract(int slot, ItemStack stack, Direction dir) {
            return false;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public ItemStack getStack(int slot) {
            return null;
        }

        @Override
        public ItemStack removeStack(int slot, int amount) {
            return null;
        }

        @Override
        public ItemStack removeStack(int slot) {
            return null;
        }

        @Override
        public void setStack(int slot, ItemStack stack) {

        }

        @Override
        public void markDirty() {

        }

        @Override
        public boolean canPlayerUse(PlayerEntity player) {
            return false;
        }

        @Override
        public void clear() {

        }
    }
}
