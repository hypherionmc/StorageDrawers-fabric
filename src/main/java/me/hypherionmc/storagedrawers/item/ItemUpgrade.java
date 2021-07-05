package me.hypherionmc.storagedrawers.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemUpgrade extends Item
{
    private static int nextGroupId = 0;

    private boolean allowMultiple;
    private int groupId;

    public ItemUpgrade (Item.Settings properties) {
        this(properties, getNextGroupId());
    }

    protected ItemUpgrade (Item.Settings properties, int groupId) {
        super(properties);
        this.groupId = groupId;
    }

    protected static int getNextGroupId () {
        int groupId = nextGroupId;
        nextGroupId += 1;
        return groupId;
    }

    public int getUpgradeGroup() {
        return groupId;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(new LiteralText("").append(getTranslationKey()).formatted(Formatting.GRAY));
    }

    @Override
    public Text getName() {
        return new TranslatableText(this.getTranslationKey() + ".desc");
    }

    public void setAllowMultiple (boolean allow) {
        allowMultiple = allow;
    }

    public boolean getAllowMultiple () {
        return allowMultiple;
    }
}
