package me.hypherionmc.storagedrawers.integration;/*package me.hypherionmc.storagedrawers.integration;

import me.hypherionmc.chameleon.integration.IntegrationRegistry;
import me.hypherionmc.storagedrawers.StorageDrawers;
import net.minecraftforge.fml.common.Loader;

public class LocalIntegrationRegistry
{
    private static LocalIntegrationRegistry instance;

    static {
        IntegrationRegistry reg = instance();
        if (Loader.isModLoaded("waila") && StorageDrawers.config.cache.enableWailaIntegration)
            reg.add(new Waila());
        //if (Loader.isModLoaded("Thaumcraft") && StorageDrawers.config.cache.enableThaumcraftIntegration)
        //    reg.add(new Thaumcraft());
        //if (Loader.isModLoaded("appliedenergistics2") && StorageDrawers.config.cache.enableAE2Integration)
        //    reg.add(new AppliedEnergistics());
        //if (Loader.isModLoaded("crafttweaker") && StorageDrawers.config.cache.enableMineTweakerIntegration)
        //    reg.add(new MineTweaker());
    }

    private IntegrationRegistry registry;

    private LocalIntegrationRegistry () {
        registry = new IntegrationRegistry(StorageDrawers.MOD_ID);
    }

    public static IntegrationRegistry instance () {
        if (instance == null)
            instance = new LocalIntegrationRegistry();

        return instance.registry;
    }
}
*/