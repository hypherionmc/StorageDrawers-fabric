package me.hypherionmc.storagedrawers.item;

import me.hypherionmc.storagedrawers.config.CommonConfig;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ItemUpgradeStorage extends ItemUpgrade
{
    private static int storageGroupId;
    static {
        storageGroupId = ItemUpgrade.getNextGroupId();
    }

    public final EnumUpgradeStorage level;

    public ItemUpgradeStorage (EnumUpgradeStorage level, Item.Settings properties) {
        this(level, properties, storageGroupId);
    }

    protected ItemUpgradeStorage (EnumUpgradeStorage level, Item.Settings properties, int groupId) {
        super(properties, groupId);

        setAllowMultiple(true);
        this.level = level;
    }

    @Override
    public Text getName(ItemStack stack) {
        int mult = CommonConfig.UPGRADES.getLevelMult(level.getLevel());
        return new TranslatableText("item.storagedrawers.storage_upgrade.desc", mult);
    }

}
