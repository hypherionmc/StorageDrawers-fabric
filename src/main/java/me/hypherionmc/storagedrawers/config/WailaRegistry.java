package me.hypherionmc.storagedrawers.config;

import me.hypherionmc.storagedrawers.api.registry.IWailaRegistry;
import me.hypherionmc.storagedrawers.api.registry.IWailaTooltipHandler;

import java.util.ArrayList;
import java.util.List;

public class WailaRegistry implements IWailaRegistry
{
    private List<IWailaTooltipHandler> registry = new ArrayList<>();

    @Override
    public void registerTooltipHandler (IWailaTooltipHandler handler) {
        registry.add(handler);
    }

    public List<IWailaTooltipHandler> getTooltipHandlers () {
        return registry;
    }
}
