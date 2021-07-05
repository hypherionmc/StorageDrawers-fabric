package me.hypherionmc.storagedrawers.api.render;


import me.hypherionmc.storagedrawers.api.storage.IDrawerGroup;
import net.minecraft.block.entity.BlockEntity;

public interface IRenderLabel
{
    void render (BlockEntity tileEntity, IDrawerGroup drawerGroup, int slot, float brightness, float partialTickTime);
}
