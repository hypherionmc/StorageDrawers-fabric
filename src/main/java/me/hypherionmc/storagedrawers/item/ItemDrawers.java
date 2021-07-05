package me.hypherionmc.storagedrawers.item;

import me.hypherionmc.storagedrawers.block.BlockDrawers;
import me.hypherionmc.storagedrawers.config.CommonConfig;
import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemDrawers extends BlockItem
{
    public ItemDrawers (Block block, Item.Settings properties) {
        super(block, properties);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        BaseText textCapacity = new TranslatableText("tooltip.storagedrawers.drawers.capacity", getCapacityForBlock(stack));
        tooltip.add(new LiteralText("").append(textCapacity).formatted(Formatting.GRAY));

        if (stack.hasTag() && stack.getTag().contains("tile")) {
            LiteralText textSealed = new LiteralText("tooltip.storagedrawers.drawers.sealed");
            tooltip.add(new LiteralText("").append(textSealed).formatted(Formatting.YELLOW));
        }
    }

    @Override
    public Text getName() {
        return new TranslatableText(this.getTranslationKey() + ".desc");
    }

    private int getCapacityForBlock (@Nonnull ItemStack itemStack) {
        Block block = Block.getBlockFromItem(itemStack.getItem());
        if (block instanceof BlockDrawers) {
            BlockDrawers drawers = (BlockDrawers)block;
            return drawers.getStorageUnits() * CommonConfig.GENERAL.getBaseStackStorage();
        }

        return 0;
    }
}
