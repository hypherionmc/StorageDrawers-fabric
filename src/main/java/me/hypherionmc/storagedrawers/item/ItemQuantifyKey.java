package me.hypherionmc.storagedrawers.item;

import me.hypherionmc.storagedrawers.api.storage.IDrawerAttributesModifiable;
import net.minecraft.item.Item;

public class ItemQuantifyKey extends ItemKey
{
    public ItemQuantifyKey (Item.Settings properties) {
        super(properties);
    }

    @Override
    protected void handleDrawerAttributes (IDrawerAttributesModifiable attrs) {
        attrs.setIsShowingQuantity(!attrs.isShowingQuantity());
    }
}
