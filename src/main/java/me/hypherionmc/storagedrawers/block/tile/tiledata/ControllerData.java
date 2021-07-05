package me.hypherionmc.storagedrawers.block.tile.tiledata;

import me.hypherionmc.storagedrawers.block.tile.TileEntityController;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class ControllerData extends TileDataShim
{
    private BlockPos controllerCoord;

    @Override
    public void read (NbtCompound tag) {
        controllerCoord = null;
        if (tag.contains("Controller", 10)) {
            NbtCompound ctag = tag.getCompound("Controller");
            controllerCoord = new BlockPos(ctag.getInt("x"), ctag.getInt("y"), ctag.getInt("z"));
        }
    }

    @Override
    public NbtCompound write (NbtCompound tag) {
        if (controllerCoord != null) {
            NbtCompound ctag = new NbtCompound();
            ctag.putInt("x", controllerCoord.getX());
            ctag.putInt("y", controllerCoord.getY());
            ctag.putInt("z", controllerCoord.getZ());
            tag.put("Controller", ctag);
        }

        return tag;
    }

    public BlockPos getCoord () {
        return controllerCoord;
    }

    public TileEntityController getController (BlockEntity host) {
        if (controllerCoord == null)
            return null;

        BlockEntity te = host.getWorld().getBlockEntity(controllerCoord);
        if (!(te instanceof TileEntityController)) {
            controllerCoord = null;
            host.markDirty();
            return null;
        }

        return (TileEntityController)te;
    }

    public boolean bindCoord (BlockPos pos) {
        if (controllerCoord == null || !controllerCoord.equals(pos)) {
            controllerCoord = pos;
            return true;
        }

        return false;
    }
}
