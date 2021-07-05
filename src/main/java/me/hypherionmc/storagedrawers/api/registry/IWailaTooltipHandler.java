package me.hypherionmc.storagedrawers.api.registry;


import me.hypherionmc.storagedrawers.api.storage.IDrawer;

public interface IWailaTooltipHandler
{
    String transformItemName (IDrawer drawer, String defaultName);
}
