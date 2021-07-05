package me.hypherionmc.storagedrawers.block;

import me.hypherionmc.storagedrawers.api.storage.INetworked;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;

public class BlockTrim extends Block implements INetworked
{
    public BlockTrim (AbstractBlock.Settings properties) {
        super(properties);
    }
}
