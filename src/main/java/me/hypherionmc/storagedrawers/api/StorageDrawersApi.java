package me.hypherionmc.storagedrawers.api;

/**
 * Entry point for the public API.
 */
public class StorageDrawersApi
{
    private static IStorageDrawersApi instance;

    public static final String VERSION = "2.1.0";

    /**
     * API entry point.
     *
     * @return The {@link IStorageDrawersApi} instance or null if the API or Storage Drawers is unavailable.
     */
    public static IStorageDrawersApi instance () {
        if (instance == null) {
            try {
                Class classApi = Class.forName( "me.hypherionmc.storagedrawers.core.Api" );
                instance = (IStorageDrawersApi) classApi.getField("instance").get(null);
            }
            catch (Throwable t) {
                return null;
            }
        }

        return instance;
    }
}
