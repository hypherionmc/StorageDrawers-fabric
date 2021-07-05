package me.hypherionmc.storagedrawers.core;

import me.hypherionmc.storagedrawers.api.IStorageDrawersApi;

public class Api implements IStorageDrawersApi
{
    public static IStorageDrawersApi instance;

    public Api () {
        instance = this;
    }

    /*@Override
    public IRenderRegistry renderRegistry () {
        return StorageDrawers.renderRegistry;
    }

    @Override
    public IWailaRegistry wailaRegistry () {
        return StorageDrawers.wailaRegistry;
    }*/
}
