package me.hypherionmc.storagedrawers.api.storage;

import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public interface IDrawer
{
    /**
     * Gets an ItemStack of size 1 representing the type, metadata, and tags of the stored items.
     * The returned ItemStack should not be modified for any reason.  Make a copy if you need to store or modify it.
     *
     * To emphasize, DO NOT MODIFY THE ITEM STACK RETURNED BY THIS METHOD.
     */
    @Nonnull
    ItemStack getStoredItemPrototype ();

    /**
     * Sets the type of the stored item and initializes it to 0.  Any existing item will be replaced.
     *
     * @param itemPrototype An ItemStack representing the type, metadata, and tags of the item to store.
     * @return The IDrawer actually set with the prototype.  Some drawer groups can redirect a set operation to another member.
     */
    @Nonnull
    IDrawer setStoredItem (@Nonnull ItemStack itemPrototype);

    /**
     * Sets the type of the stored item and initializes it to the given amount.  Any existing item will be replaced.
     *
     * @param itemPrototype An ItemStack representing the type, metadata, and tags of the item to store.
     * @param amount The amount of items stored in this drawer.
     * @return The IDrawer actually set with the prototype.  Some drawer groups can redirect a set operation to another member.
     */
    @Nonnull
    default IDrawer setStoredItem (@Nonnull ItemStack itemPrototype, int amount) {
        IDrawer drawer = setStoredItem(itemPrototype);
        drawer.setStoredItemCount(amount);
        return drawer;
    }

    /**
     * Gets the number of items stored in this drawer.
     */
    int getStoredItemCount ();

    /**
     * Sets the number of items stored in this drawer.  Triggers syncing of inventories and client data.
     * Setting a drawer's count to 0 may also result in the item type being cleared, depending in implementation.
     *
     * @param amount The new amount of items stored in this drawer.
     */
    void setStoredItemCount (int amount);

    /**
     * Adds or removes a given amount from the number of items stored in this drawer.
     *
     * @param amount The amount to add (positive) or subtract (negative).
     * @return 0 if the full adjustment was committed, or a positive value representing the remainder if the full
     * amount couldn't be added or subtracted.
     */
    default int adjustStoredItemCount (int amount) {
        if (amount > 0) {
            int insert = Math.min(amount, getRemainingCapacity());
            setStoredItemCount(getStoredItemCount() + insert);
            return amount - insert;
        } else if (amount < 0) {
            int stored = getStoredItemCount();
            int destroy = Math.min(Math.abs(amount), getStoredItemCount());
            setStoredItemCount(stored - destroy);
            return Math.abs(amount + destroy);
        } else {
            return 0;
        }
    }

    /**
     * Gets the maximum number of items that can be stored in this drawer.
     * This value will vary depending on the max stack size of the stored item type.
     */
    default int getMaxCapacity () {
        return getMaxCapacity(getStoredItemPrototype());
    }

    /**
     * Gets the maximum number of items that could be stored in this drawer if it held the given item.
     *
     * @param itemPrototype The item type to query.  Pass the empty stack to get the max capacity for an empty slot.
     */
    int getMaxCapacity (@Nonnull ItemStack itemPrototype);

    /**
     * Gets the maximum number of items that would be accepted for storage by this drawer.
     *
     * Because a drawer may be able to handle items in excess of its full capacity, this value may be larger than
     * the result of getMaxCapacity().
     */
    default int getAcceptingMaxCapacity (@Nonnull ItemStack itemPrototype) {
        return getMaxCapacity(itemPrototype);
    }

    /**
     * Gets the number of items that could still be added to this drawer before it is full.
     */
    int getRemainingCapacity ();

    /**
     * Gets the number of additional items that would be accepted by this drawer.
     *
     * Because a drawer may be able to handle items in excess of its full capacity, this value may be larger than
     * the result of getRemainingCapacity().
     */
    default int getAcceptingRemainingCapacity () {
        return getRemainingCapacity();
    }

    /**
     * Gets the max stack size of the item type stored in this drawer.
     */
    default int getStoredItemStackSize () {
        @Nonnull ItemStack protoStack = getStoredItemPrototype();
        if (protoStack.isEmpty())
            return 0;

        return protoStack.getItem().getMaxItemCount(protoStack);
    }

    /**
     * Gets whether or not an item of the given type and data can be stored in this drawer using a custom matching
     * predicate.  If matchPredicate is null, the drawer's default item matching rules are applied instead.
     *
     * Some attributes, like locking status, are considered regardless of the predicate.
     *
     * @param itemPrototype     An ItemStack representing the type, metadata, and tags of an item.
     * @param matchPredicate    A custom predicate for testing the stored ItemStack for equivalence.
     * @return
     */
    boolean canItemBeStored (@Nonnull ItemStack itemPrototype, Predicate<ItemStack> matchPredicate);

    default boolean canItemBeStored (@Nonnull ItemStack itemPrototype) {
        return canItemBeStored(itemPrototype, null);
    }

    /**
     * Gets whether or not an item of the given type and data can be extracted from this drawer using a custom matching
     * predicate.  If matchPredicate is null, the drawer's default item matching rules are applied instead.
     *
     * This is intended to allow outbound conversions of compatible items, as defined by what the drawer or predicate
     * considers to be an equivalent item.
     *
     * @param itemPrototype     An ItemStack representing the type, metadata, and tags of an item.
     * @param matchPredicate    A custom predicate for testing the stored ItemStack for equivalence.
     */
    boolean canItemBeExtracted (@Nonnull ItemStack itemPrototype, Predicate<ItemStack> matchPredicate);

    default boolean canItemBeExtracted (@Nonnull ItemStack itemPrototype) {
        return canItemBeExtracted(itemPrototype, null);
    }

    /**
     * Gets whether or not the drawer has items.
     * A drawer set with an item type and 0 count is not considered empty.
     */
    boolean isEmpty ();

    default boolean isEnabled () {
        return true;
    }
}
