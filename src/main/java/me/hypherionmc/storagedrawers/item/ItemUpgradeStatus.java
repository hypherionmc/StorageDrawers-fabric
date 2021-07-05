package me.hypherionmc.storagedrawers.item;

import net.minecraft.item.Item;

public class ItemUpgradeStatus extends ItemUpgrade
{
    private static int statusGroupId;
    static {
        statusGroupId = ItemUpgrade.getNextGroupId();
    }

    public final EnumUpgradeStatus level;

    public ItemUpgradeStatus (EnumUpgradeStatus status, Item.Settings properties) {
        this(status, properties, statusGroupId);
    }

    protected ItemUpgradeStatus (EnumUpgradeStatus status, Item.Settings properties, int groupId) {
        super(properties, groupId);

        this.level = status;
    }
}
