package me.hypherionmc.storagedrawers.config;

import me.hypherionmc.storagedrawers.api.registry.IRenderRegistry;
import me.hypherionmc.storagedrawers.api.render.IRenderLabel;

import java.util.ArrayList;
import java.util.List;

public class RenderRegistry implements IRenderRegistry
{
    private List<IRenderLabel> registry = new ArrayList<>();

    @Override
    public void registerPreLabelRenderHandler (IRenderLabel renderHandler) {
        registry.add(renderHandler);
    }

    public List<IRenderLabel> getRenderHandlers () {
        return registry;
    }
}
