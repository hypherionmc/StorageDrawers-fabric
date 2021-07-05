package me.hypherionmc.storagedrawers.block.tile.tiledata;


import net.minecraft.nbt.NbtCompound;

public abstract class TileDataShim implements INBTSerializable<NbtCompound>
{
    public abstract void read (NbtCompound tag);

    public abstract NbtCompound write (NbtCompound tag);

    @Override
    public NbtCompound serializeNBT () {
        NbtCompound tag = new NbtCompound();
        return write(tag);
    }

    @Override
    public void deserializeNBT (NbtCompound nbt) {
        read(nbt);
    }
}
