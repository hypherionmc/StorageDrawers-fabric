package me.hypherionmc.storagedrawers.block.tile.tiledata;

import me.hypherionmc.storagedrawers.api.capabilities.IItemRepository;
import me.hypherionmc.storagedrawers.api.storage.*;
import me.hypherionmc.storagedrawers.api.storage.attribute.LockAttribute;
import me.hypherionmc.storagedrawers.capabilities.DrawerItemHandler;
import me.hypherionmc.storagedrawers.capabilities.DrawerItemRepository;
import me.hypherionmc.storagedrawers.inventory.ItemStackHelper;
import me.hypherionmc.storagedrawers.util.CompactingHelper;
import me.hypherionmc.storagedrawers.util.ItemStackMatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Stack;
import java.util.function.Predicate;

public class FractionalDrawerGroup extends TileDataShim implements IDrawerGroup
{

    // TODO REPLACE THIS WITH WHATEVER
    @CapabilityInject(IItemHandler.class)
    public static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;
    @CapabilityInject(IItemRepository.class)
    public static Capability<IItemRepository> ITEM_REPOSITORY_CAPABILITY = null;


    private FractionalStorage storage;
    private FractionalDrawer[] slots;
    private int[] order;

    // TODO REPLACE THIS WITH WHATEVER
    private final LazyOptional<IItemHandler> itemHandler;
    private final LazyOptional<IItemRepository> itemRepository;

    public FractionalDrawerGroup (int slotCount) {

        // TODO REPLACE THIS WITH WHATEVER
        itemHandler = LazyOptional.of(() -> new DrawerItemHandler(this));
        itemRepository = LazyOptional.of(() -> new DrawerItemRepository(this));

        storage = new FractionalStorage(this, slotCount);

        slots = new FractionalDrawer[slotCount];
        order = new int[slotCount];

        for (int i = 0; i < slotCount; i++) {
            slots[i] = new FractionalDrawer(storage, i);
            order[i] = i;
        }
    }

    // TODO REPLACE THIS WITH WHATEVER
    public void setCapabilityProvider (ICapabilityProvider capProvider) {
        storage.setCapabilityProvider(capProvider);
    }

    @Override
    public int getDrawerCount () {
        return slots.length;
    }

    @Nonnull
    @Override
    public IFractionalDrawer getDrawer (int slot) {
        if (slot < 0 || slot >= slots.length)
            return Drawers.DISABLED_FRACTIONAL;

        return slots[slot];
    }

    @Nonnull
    @Override
    public int[] getAccessibleDrawerSlots () {
        return order;
    }

    public int getPooledCount () {
        return storage.getPooledCount();
    }

    public void setPooledCount (int count) {
        storage.setPooledCount(count);
    }

    @Override
    public void read (NbtCompound tag) {
        if (tag.contains("Drawers"))
            storage.deserializeNBT(tag.getCompound("Drawers"));
    }

    @Override
    public NbtCompound write (NbtCompound tag) {
        tag.put("Drawers", storage.serializeNBT());
        return tag;
    }

    // TODO REPLACE THIS WITH WHATEVER
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability (@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (capability == ITEM_HANDLER_CAPABILITY)
            return itemHandler.cast();
        if (capability == ITEM_REPOSITORY_CAPABILITY)
            return itemRepository.cast();

        return LazyOptional.empty();
    }

    public void syncAttributes () {
        storage.syncAttributes();
    }

    protected World getWorld () { return null; }

    protected void log (String message) { }

    protected int getStackCapacity () {
        return 0;
    }

    protected void onItemChanged () { }

    protected void onAmountChanged () { }


    // TODO REPLACE THIS WITH WHATEVER
    private static class FractionalStorage implements INBTSerializable<CompoundNBT>
    {
        @CapabilityInject(IDrawerAttributes.class)
        static Capability<IDrawerAttributes> ATTR_CAPABILITY = null;

        private FractionalDrawerGroup group;
        private int slotCount;
        private ItemStack[] protoStack;
        private int[] convRate;
        private ItemStackMatcher[] matchers;
        private int pooledCount;

        IDrawerAttributes attrs;

        public FractionalStorage (FractionalDrawerGroup group, int slotCount) {
            this.group = group;
            this.slotCount = slotCount;

            protoStack = new ItemStack[slotCount];
            matchers = new ItemStackMatcher[slotCount];

            for (int i = 0; i < slotCount; i++) {
                protoStack[i] = ItemStack.EMPTY;
                matchers[i] = ItemStackMatcher.EMPTY;
            }

            convRate = new int[slotCount];

            attrs = EmptyDrawerAttributes.EMPTY;
        }

        public void setCapabilityProvider (ICapabilityProvider capProvider) {
            attrs = capProvider.getCapability(ATTR_CAPABILITY, null).orElse(EmptyDrawerAttributes.EMPTY);
        }

        public int getPooledCount () {
            return pooledCount;
        }

        public void setPooledCount (int count) {
            if (pooledCount != count) {
                pooledCount = count;
                group.onAmountChanged();
            }
        }

        @Nonnull
        public ItemStack getStack (int slot) {
            return protoStack[slot];
        }

        @Nonnull
        public ItemStack baseStack () {
            return protoStack[0];
        }

        public int baseRate () {
            return convRate[0];
        }

        public IFractionalDrawer setStoredItem (int slot, @Nonnull ItemStack itemPrototype) {
            itemPrototype = ItemStackHelper.getItemPrototype(itemPrototype);
            if (itemPrototype.isEmpty()) {
                reset();
                return group.getDrawer(slot);
            }

            if (baseRate() == 0) {
                populateSlots(itemPrototype);
                for (int i = 0; i < slotCount; i++) {
                    if (ItemStackMatcher.areItemsEqual(protoStack[i], itemPrototype)) { // TODO: ItemStackOreMatcher
                        slot = i;
                        pooledCount = 0;
                    }
                }

                group.onItemChanged();
            }

            return group.getDrawer(slot);
        }

        public int getStoredCount (int slot) {
            if (convRate[slot] == 0)
                return 0;

            if (attrs.isUnlimitedVending())
                return Integer.MAX_VALUE;

            return pooledCount / convRate[slot];
        }

        public void setStoredItemCount (int slot, int amount) {
            if (convRate[slot] == 0)
                return;

            if (attrs.isUnlimitedVending())
                return;

            int oldCount = pooledCount;

            pooledCount = (pooledCount % convRate[slot]) + convRate[slot] * amount;
            pooledCount = Math.min(pooledCount, getMaxCapacity(0) * convRate[0]);
            pooledCount = Math.max(pooledCount, 0);

            if (pooledCount == oldCount)
                return;

            if (pooledCount == 0 && !attrs.isItemLocked(LockAttribute.LOCK_POPULATED))
                reset();
            else
                group.onAmountChanged();
        }

        public int adjustStoredItemCount (int slot, int amount) {
            if (convRate[slot] == 0 || amount == 0)
                return Math.abs(amount);

            if (amount > 0) {
                if (attrs.isUnlimitedVending())
                    return 0;

                int poolMax = getMaxCapacity(0) * convRate[0];
                if (poolMax < 0)
                    poolMax = Integer.MAX_VALUE;

                int canAdd = (poolMax - pooledCount) / convRate[slot];
                int willAdd = Math.min(amount, canAdd);
                if (willAdd > 0) {
                    pooledCount += convRate[slot] * willAdd;
                    group.onAmountChanged();
                }

                if (attrs.isVoid())
                    return 0;

                return amount - willAdd;
            }
            else {
                amount = -amount;

                int canRemove = pooledCount / convRate[slot];
                int willRemove = Math.min(amount, canRemove);
                if (willRemove == 0)
                    return amount;

                pooledCount -= willRemove * convRate[slot];

                if (pooledCount == 0 && !attrs.isItemLocked(LockAttribute.LOCK_POPULATED))
                    reset();
                else
                    group.onAmountChanged();

                return amount - willRemove;
            }
        }

        public int getMaxCapacity (int slot) {
            if (baseStack().isEmpty() || convRate[slot] == 0)
                return 0;

            if (attrs.isUnlimitedStorage() || attrs.isUnlimitedVending())
                return Integer.MAX_VALUE / convRate[slot];

            return baseStack().getItem().getItemStackLimit(baseStack()) * group.getStackCapacity() * (baseRate() / convRate[slot]);
        }

        public int getMaxCapacity (int slot, @Nonnull ItemStack itemPrototype) {
            if (attrs.isUnlimitedStorage() || attrs.isUnlimitedVending()) {
                if (convRate[slot] == 0)
                    return Integer.MAX_VALUE;
                return Integer.MAX_VALUE / convRate[slot];
            }

            if (baseStack().isEmpty()) {
                int itemStackLimit = 64;
                if (!itemPrototype.isEmpty())
                    itemStackLimit = itemPrototype.getItem().getItemStackLimit(itemPrototype);
                return itemStackLimit * group.getStackCapacity();
            }

            if (ItemStackMatcher.areItemsEqual(protoStack[slot], itemPrototype)) // TODO: ItemStackOreMatcher
                return getMaxCapacity(slot);

            return 0;
        }

        public int getAcceptingMaxCapacity (int slot, @Nonnull ItemStack itemPrototype) {
            if (attrs.isVoid())
                return Integer.MAX_VALUE;

            return getMaxCapacity(slot, itemPrototype);
        }

        public int getRemainingCapacity (int slot) {
            if (baseStack().isEmpty() || convRate[slot] == 0)
                return 0;

            if (attrs.isUnlimitedVending())
                return Integer.MAX_VALUE;

            int rawMaxCapacity = getMaxCapacity(0) * baseRate();
            int rawRemaining = rawMaxCapacity - pooledCount;

            return rawRemaining / convRate[slot];
        }

        public int getAcceptingRemainingCapacity (int slot) {
            if (baseStack().isEmpty() || convRate[slot] == 0)
                return 0;

            if (attrs.isUnlimitedVending() || attrs.isVoid())
                return Integer.MAX_VALUE;

            int rawMaxCapacity = getMaxCapacity(0) * baseRate();
            int rawRemaining = rawMaxCapacity - pooledCount;

            return rawRemaining / convRate[slot];
        }

        public boolean isEmpty (int slot) {
            return protoStack[slot].isEmpty();
        }

        public boolean isEnabled (int slot) {
            if (baseStack().isEmpty())
                return true;

            return !protoStack[slot].isEmpty();
        }

        public boolean canItemBeStored (int slot, @Nonnull ItemStack itemPrototype, Predicate<ItemStack> predicate) {
            if (protoStack[slot].isEmpty() && protoStack[0].isEmpty() && !attrs.isItemLocked(LockAttribute.LOCK_EMPTY))
                return true;

            if (predicate == null)
                return matchers[slot].matches(itemPrototype);
            return predicate.test(protoStack[slot]);
        }

        public boolean canItemBeExtracted (int slot, @Nonnull ItemStack itemPrototype, Predicate<ItemStack> predicate) {
            if (protoStack[slot].isEmpty())
                return false;

            if (predicate == null)
                return matchers[slot].matches(itemPrototype);
            return predicate.test(protoStack[slot]);
        }

        public int getConversionRate (int slot) {
            if (baseStack().isEmpty() || convRate[slot] == 0)
                return 0;

            return convRate[0] / convRate[slot];
        }

        public int getStoredItemRemainder (int slot) {
            if (convRate[slot] == 0)
                return 0;

            if (slot == 0)
                return pooledCount / baseRate();

            return (pooledCount / convRate[slot]) % (convRate[slot - 1] / convRate[slot]);
        }

        public boolean isSmallestUnit (int slot) {
            if (baseStack().isEmpty() || convRate[slot] == 0)
                return false;

            return convRate[slot] == 1;
        }

        private void reset () {
            pooledCount = 0;

            for (int i = 0; i < slotCount; i++) {
                protoStack[i] = ItemStack.EMPTY;
                matchers[i] = ItemStackMatcher.EMPTY;
                convRate[i] = 0;
            }

            group.onItemChanged();
        }

        private void populateSlots (@Nonnull ItemStack itemPrototype) {
            World world = group.getWorld();
            if (world == null) {
                protoStack[0] = itemPrototype;
                convRate[0] = 1;
                matchers[0] = new ItemStackMatcher(protoStack[0]);
                //matchers[0] = attrs.isDictConvertible()
                //    ? new ItemStackOreMatcher(protoStack[0])
                //    : new ItemStackMatcher(protoStack[0]);

                return;
            }

            CompactingHelper compacting = new CompactingHelper(world);
            Stack<CompactingHelper.Result> resultStack = new Stack<>();

            @Nonnull ItemStack lookupTarget = itemPrototype;
            for (int i = 0; i < slotCount - 1; i++) {
                CompactingHelper.Result lookup = compacting.findHigherTier(lookupTarget);
                if (lookup.getStack().isEmpty())
                    break;

                resultStack.push(lookup);
                lookupTarget = lookup.getStack();
            }

            int index = 0;
            for (int n = resultStack.size(); index < n; index++) {
                CompactingHelper.Result result = resultStack.pop();
                populateRawSlot(index, result.getStack(), result.getSize());
                group.log("Picked candidate " + result.getStack().toString() + " with conv=" + result.getSize());

                for (int i = 0; i < index; i++)
                    convRate[i] *= result.getSize();
            }

            if (index == slotCount)
                return;

            populateRawSlot(index++, itemPrototype, 1);

            lookupTarget = itemPrototype;
            for (; index < slotCount; index++) {
                CompactingHelper.Result lookup = compacting.findLowerTier(lookupTarget);
                if (lookup.getStack().isEmpty())
                    break;

                populateRawSlot(index, lookup.getStack(), 1);
                group.log("Picked candidate " + lookup.getStack().toString() + " with conv=" + lookup.getSize());

                for (int i = 0; i < index; i++)
                    convRate[i] *= lookup.getSize();

                lookupTarget = lookup.getStack();
            }
        }

        private void populateRawSlot (int slot, @Nonnull ItemStack itemPrototype, int rate) {
            protoStack[slot] = itemPrototype;
            convRate[slot] = rate;
            matchers[slot] = new ItemStackMatcher(protoStack[slot]);
            //matchers[slot] = attrs.isDictConvertible()
            //    ? new ItemStackOreMatcher(protoStack[slot])
            //    : new ItemStackMatcher(protoStack[slot]);
        }

        private void normalizeGroup () {
            for (int limit = slotCount - 1; limit > 0; limit--) {
                for (int i = 0; i < limit; i++) {
                    if (protoStack[i].isEmpty()) {
                        protoStack[i] = protoStack[i + 1];
                        matchers[i] = matchers[i + 1];
                        convRate[i] = convRate[i + 1];

                        protoStack[i + 1] = ItemStack.EMPTY;
                        matchers[i + 1] = ItemStackMatcher.EMPTY;
                        convRate[i + 1] = 0;
                    }
                }
            }

            int minConvRate = Integer.MAX_VALUE;
            for (int i = 0; i < slotCount; i++) {
                if (convRate[i] > 0)
                    minConvRate = Math.min(minConvRate, convRate[i]);
            }

            if (minConvRate > 1) {
                for (int i = 0; i < slotCount; i++)
                    convRate[i] /= minConvRate;

                pooledCount /= minConvRate;
            }
        }


        // TODO REPLACE THIS WITH WHATEVER
        @Override
        public CompoundNBT serializeNBT () {
            ListNBT itemList = new ListNBT();
            for (int i = 0; i < slotCount; i++) {
                if (protoStack[i].isEmpty())
                    continue;

                CompoundNBT itemTag = new CompoundNBT();
                protoStack[i].write(itemTag);

                CompoundNBT slotTag = new CompoundNBT();
                slotTag.putByte("Slot", (byte)i);
                slotTag.putInt("Conv", convRate[i]);
                slotTag.put("Item", itemTag);

                itemList.add(slotTag);
            }

            CompoundNBT tag = new CompoundNBT();
            tag.putInt("Count", pooledCount);
            tag.put("Items", itemList);

            return tag;
        }

        @Override
        public void deserializeNBT (CompoundNBT tag) {
            for (int i = 0; i < slotCount; i++) {
                protoStack[i] = ItemStack.EMPTY;
                matchers[i] = ItemStackMatcher.EMPTY;
                convRate[i] = 0;
            }

            pooledCount = tag.getInt("Count");

            ListNBT itemList = tag.getList("Items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < itemList.size(); i++) {
                CompoundNBT slotTag = itemList.getCompound(i);
                int slot = slotTag.getByte("Slot");

                protoStack[slot] = ItemStack.read(slotTag.getCompound("Item"));
                convRate[slot] = slotTag.getByte("Conv");

                matchers[slot] = new ItemStackMatcher(protoStack[slot]);
                //matchers[slot] = attrs.isDictConvertible()
                //    ? new ItemStackOreMatcher(protoStack[slot])
                //    : new ItemStackMatcher(protoStack[slot]);
            }

            // TODO: We should only need to normalize if we had blank items with a conv rate, but this fixes blocks that were saved broken
            normalizeGroup();
        }

        public void syncAttributes () {
            for (int i = 0; i < slotCount; i++) {
                if (!protoStack[i].isEmpty()) {
                    matchers[i] = new ItemStackMatcher(protoStack[i]);
                    //matchers[i] = attrs.isDictConvertible()
                    //    ? new ItemStackOreMatcher(protoStack[i])
                    //    : new ItemStackMatcher(protoStack[i]);
                }
            }
        }
    }

    private static class FractionalDrawer implements IFractionalDrawer
    {
        private FractionalStorage storage;
        private int slot;

        public FractionalDrawer (FractionalStorage storage, int slot) {
            this.storage = storage;
            this.slot = slot;
        }

        @Nonnull
        @Override
        public ItemStack getStoredItemPrototype () {
            return storage.getStack(slot);
        }

        @Nonnull
        @Override
        public IDrawer setStoredItem (@Nonnull ItemStack itemPrototype) {
            if (ItemStackHelper.isStackEncoded(itemPrototype))
                itemPrototype = ItemStackHelper.decodeItemStackPrototype(itemPrototype);

            return storage.setStoredItem(slot, itemPrototype);
        }

        @Override
        public int getStoredItemCount () {
            return storage.getStoredCount(slot);
        }

        @Override
        public void setStoredItemCount (int amount) {
            storage.setStoredItemCount(slot, amount);
        }

        @Override
        public int adjustStoredItemCount (int amount) {
            return storage.adjustStoredItemCount(slot, amount);
        }

        @Override
        public int getMaxCapacity () {
            return storage.getMaxCapacity(slot);
        }

        @Override
        public int getMaxCapacity (@Nonnull ItemStack itemPrototype) {
            return storage.getMaxCapacity(slot, itemPrototype);
        }

        @Override
        public int getAcceptingMaxCapacity (@Nonnull ItemStack itemPrototype) {
            return storage.getAcceptingMaxCapacity(slot, itemPrototype);
        }

        @Override
        public int getRemainingCapacity () {
            return storage.getRemainingCapacity(slot);
        }

        @Override
        public int getAcceptingRemainingCapacity () {
            return storage.getAcceptingRemainingCapacity(slot);
        }

        @Override
        public boolean canItemBeStored (@Nonnull ItemStack itemPrototype, Predicate<ItemStack> matchPredicate) {
            return storage.canItemBeStored(slot, itemPrototype, matchPredicate);
        }

        @Override
        public boolean canItemBeExtracted (@Nonnull ItemStack itemPrototype, Predicate<ItemStack> matchPredicate) {
            return storage.canItemBeExtracted(slot, itemPrototype, matchPredicate);
        }

        @Override
        public boolean isEmpty () {
            return storage.isEmpty(slot);
        }

        @Override
        public boolean isEnabled () {
            return storage.isEnabled(slot);
        }

        @Override
        public int getConversionRate () {
            return storage.getConversionRate(slot);
        }

        @Override
        public int getStoredItemRemainder () {
            return storage.getStoredItemRemainder(slot);
        }

        @Override
        public boolean isSmallestUnit () {
            return storage.isSmallestUnit(slot);
        }
    }
}
