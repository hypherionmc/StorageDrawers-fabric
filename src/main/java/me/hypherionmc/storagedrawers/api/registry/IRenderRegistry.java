package me.hypherionmc.storagedrawers.api.registry;


import me.hypherionmc.storagedrawers.api.render.IRenderLabel;

public interface IRenderRegistry
{
    void registerPreLabelRenderHandler (IRenderLabel renderHandler);
}
