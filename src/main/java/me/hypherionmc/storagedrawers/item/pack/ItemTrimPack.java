package me.hypherionmc.storagedrawers.item.pack;
/*
import com.google.common.base.Function;
import me.hypherionmc.storagedrawers.item.ItemTrim;
import me.hypherionmc.storagedrawers.block.pack.BlockTrimPack;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class ItemTrimPack extends ItemTrim
{
    public ItemTrimPack (final Block block) {
        super(block, new Function() {
            @Nullable
            @Override
            public Object apply (Object input) {
                ItemStack stack = (ItemStack)input;
                String[] unlocalizedNames = getUnlocalizedNames(block);
                return unlocalizedNames[stack.getMetadata()];
            }
        });
    }

    protected ItemTrimPack (Block block, String[] unlocalizedNames) {
        super(block, unlocalizedNames);
    }

    protected ItemTrimPack (Block block, String[] unlocalizedNames) {
        super(block, unlocalizedNames);
    }

    private static String[] getUnlocalizedNames (Block block) {
        if (block instanceof BlockTrimPack)
            return ((BlockTrimPack) block).getUnlocalizedNames();
        else
            return new String[16];
    }
}
*/