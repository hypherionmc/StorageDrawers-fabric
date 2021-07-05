package me.hypherionmc.storagedrawers.integration.ae2;//package me.hypherionmc.storagedrawers.integration.ae2;

//import me.hypherionmc.storagedrawers.api.storage.IDrawerGroup;
//import net.minecraft.tileentity.TileEntity;

//public class DrawerExternalStorageHandler // implements IExternalStorageHandler
//{
    /*private IStorageBusMonitorFactory sbmFactory;

    public DrawerExternalStorageHandler (IStorageBusMonitorFactory factory) {
        sbmFactory = factory;
    }

    @Override
    public boolean canHandle (TileEntity te, ForgeDirection d, StorageChannel channel, BaseActionSource mySrc) {
        return channel == StorageChannel.ITEMS && te instanceof IDrawerGroup;
    }

    @Override
    public IMEInventory getInventory (TileEntity te, ForgeDirection d, StorageChannel channel, BaseActionSource src) {
        if (sbmFactory != null && channel == StorageChannel.ITEMS)
            return sbmFactory.createStorageBusMonitor(new DrawerMEInventory((IDrawerGroup) te), src);

        return null;
    }*/
//}
