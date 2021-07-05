package me.hypherionmc.storagedrawers.security;

import me.hypherionmc.storagedrawers.api.security.ISecurityProvider;

import java.util.HashMap;
import java.util.Map;

public class SecurityRegistry
{
    private Map<String, ISecurityProvider> registry = new HashMap<String, ISecurityProvider>();

    public void registerProvider (ISecurityProvider provider) {
        registry.put(provider.getProviderID(), provider);
    }

    public ISecurityProvider getProvider (String name) {
        return registry.get(name);
    }
}
