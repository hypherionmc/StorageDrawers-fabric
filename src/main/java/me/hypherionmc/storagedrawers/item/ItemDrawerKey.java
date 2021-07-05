package me.hypherionmc.storagedrawers.item;

import me.hypherionmc.storagedrawers.api.storage.IDrawerAttributesModifiable;
import me.hypherionmc.storagedrawers.api.storage.attribute.LockAttribute;
import net.minecraft.item.Item;


public class ItemDrawerKey extends ItemKey
{
    public ItemDrawerKey (Item.Settings properties) {
        super(properties);
    }

    @Override
    protected void handleDrawerAttributes (IDrawerAttributesModifiable attrs) {
        boolean locked = attrs.isItemLocked(LockAttribute.LOCK_POPULATED);
        attrs.setItemLocked(LockAttribute.LOCK_EMPTY, !locked);
        attrs.setItemLocked(LockAttribute.LOCK_POPULATED, !locked);
    }
}
