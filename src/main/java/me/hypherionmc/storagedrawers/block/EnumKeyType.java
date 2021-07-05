package me.hypherionmc.storagedrawers.block;

import net.minecraft.util.StringIdentifiable;

import javax.annotation.Nonnull;

public enum EnumKeyType implements StringIdentifiable
{
    DRAWER(0, "drawer"),
    CONCEALMENT(1, "concealment"),
    PERSONAL(2, "personal"),
    QUANTIFY(3, "quantify");

    private static final EnumKeyType[] META_LOOKUP;

    private final int meta;
    private final String name;

    EnumKeyType (int meta, String name) {
        this.meta = meta;
        this.name = name;
    }

    public int getMetadata () {
        return meta;
    }

    public static EnumKeyType byMetadata (int meta) {
        if (meta < 0 || meta >= META_LOOKUP.length)
            meta = 0;
        return META_LOOKUP[meta];
    }

    @Override
    @Nonnull
    public String asString () {
        return name;
    }

    @Override
    public String toString () {
        return asString();
    }

    static {
        META_LOOKUP = new EnumKeyType[values().length];
        for (EnumKeyType type : values()) {
            META_LOOKUP[type.getMetadata()] = type;
        }
    }
}
