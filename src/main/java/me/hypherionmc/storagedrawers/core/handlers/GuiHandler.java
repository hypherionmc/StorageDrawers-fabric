package me.hypherionmc.storagedrawers.core.handlers;/*package me.hypherionmc.storagedrawers.core.handlers;

import me.hypherionmc.storagedrawers.block.tile.TileEntityDrawers;
import me.hypherionmc.storagedrawers.block.tile.TileEntityFramingTable;
import me.hypherionmc.storagedrawers.client.gui.GuiDrawers;
import me.hypherionmc.storagedrawers.client.gui.GuiFraming;
import me.hypherionmc.storagedrawers.inventory.ContainerDrawers;
import me.hypherionmc.storagedrawers.inventory.ContainerFramingTable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
    public static int drawersGuiID = 0;
    public static int framingGuiID = 1;

    @Override
    public Object getServerGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        if (tile instanceof TileEntityDrawers)
            return new ContainerDrawers(player.inventory, (TileEntityDrawers) tile);
        if (tile instanceof TileEntityFramingTable)
            return new ContainerFramingTable(player.inventory, (TileEntityFramingTable) tile);

        return null;
    }

    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
        if (tile instanceof TileEntityDrawers)
            return new GuiDrawers(player.inventory, (TileEntityDrawers) tile);
        if (tile instanceof TileEntityFramingTable)
            return new GuiFraming(player.inventory, (TileEntityFramingTable) tile);

        return null;
    }
}
*/