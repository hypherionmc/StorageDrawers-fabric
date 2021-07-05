package me.hypherionmc.storagedrawers.item;

import net.minecraft.item.Item;

public class ItemUpgradeRedstone extends ItemUpgrade
{
    private static int redstoneGroupId;
    static {
        redstoneGroupId = ItemUpgrade.getNextGroupId();
    }

    public final EnumUpgradeRedstone type;

    public ItemUpgradeRedstone (EnumUpgradeRedstone type, Item.Settings properties) {
        this(type, properties, redstoneGroupId);
    }

    protected ItemUpgradeRedstone (EnumUpgradeRedstone type, Item.Settings properties, int groupId) {
        super(properties, groupId);

        this.type = type;
    }
}