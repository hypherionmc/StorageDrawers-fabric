package me.hypherionmc.storagedrawers.block;

import me.hypherionmc.storagedrawers.block.tile.TileEntityDrawers;
import me.hypherionmc.storagedrawers.block.tile.TileEntityDrawersStandard;
import net.minecraft.block.Block;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class BlockStandardDrawers extends BlockDrawers
{
    //public static final EnumProperty<EnumBasicDrawer> BLOCK = EnumProperty.create("block", EnumBasicDrawer.class);


    //@SideOnly(Side.CLIENT)
    //private StatusModelData[] statusInfo;

    public BlockStandardDrawers (int drawerCount, boolean halfDepth, int storageUnits, Block.Settings properties) {
       super(drawerCount, halfDepth, storageUnits, properties);
    }

    public BlockStandardDrawers (int drawerCount, boolean halfDepth, Block.Settings properties) {
        super(drawerCount, halfDepth, calcUnits(drawerCount, halfDepth), properties);
    }

    private static int calcUnits (int drawerCount, boolean halfDepth) {
        return halfDepth ? 4 / drawerCount : 8 / drawerCount;
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public void initDynamic () {
        statusInfo = new StatusModelData[EnumBasicDrawer.values().length];
        for (EnumBasicDrawer type : EnumBasicDrawer.values()) {
            ResourceLocation location = new ResourceLocation(StorageDrawers.MOD_ID + ":models/dynamic/basicDrawers_" + type.getName() + ".json");
            statusInfo[type.getMetadata()] = new StatusModelData(type.getDrawerCount(), location);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public StatusModelData getStatusInfo (IBlockState state) {
        if (state != null) {
            EnumBasicDrawer info = state.getValue(BLOCK);
            if (info != null)
                return statusInfo[info.getMetadata()];
        }

        return null;
    }*/

    @Override
    protected int getDrawerSlot (Direction side, Vec3d hit) {
        if (getDrawerCount() == 1)
            return 0;
        if (getDrawerCount() == 2)
            return hitTop(hit.y) ? 0 : 1;

        if (hitLeft(side, hit.x, hit.z))
            return hitTop(hit.y) ? 0 : 2;
        else
            return hitTop(hit.y) ? 1 : 3;
    }

    @Nullable
    @Override
    public TileEntityDrawers createBlockEntity(BlockView world) {
        return TileEntityDrawersStandard.createEntity(getDrawerCount());
    }
}
