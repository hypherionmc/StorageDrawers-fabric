package me.hypherionmc.storagedrawers.api.security;

import com.mojang.authlib.GameProfile;
import me.hypherionmc.storagedrawers.api.storage.attribute.IProtectable;

public interface ISecurityProvider
{
    String getProviderID ();

    boolean hasOwnership (GameProfile profile, IProtectable target);

    boolean hasAccess (GameProfile profile, IProtectable target);
}
