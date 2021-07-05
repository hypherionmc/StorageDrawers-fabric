package me.hypherionmc.storagedrawers.block;

import me.hypherionmc.storagedrawers.api.storage.IDrawerGroup;
import me.hypherionmc.storagedrawers.api.storage.INetworked;
import me.hypherionmc.storagedrawers.block.tile.TileEntityDrawers;
import me.hypherionmc.storagedrawers.block.tile.TileEntityDrawersComp;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockCompDrawers extends BlockDrawers implements INetworked
{
    public static final EnumProperty<EnumCompDrawer> SLOTS = EnumProperty.of("slots", EnumCompDrawer.class);

    //@SideOnly(Side.CLIENT)
    //private StatusModelData statusInfo;

    public BlockCompDrawers (int storageUnits, Block.Settings properties) {
        super(3, false, storageUnits, properties);
        this.setDefaultState(getDefaultState()
            .with(SLOTS, EnumCompDrawer.OPEN1));
    }

    public BlockCompDrawers (Block.Settings properties) {
        this(32, properties);
    }

    @Override
    protected void appendProperties (StateManager.Builder<Block, BlockState> builder) {
        builder.add(SLOTS);
    }

    /*@Override
    @SideOnly(Side.CLIENT)
    public void initDynamic () {
        ResourceLocation location = new ResourceLocation(StorageDrawers.MOD_ID + ":models/dynamic/compDrawers.json");
        statusInfo = new StatusModelData(3, location);
    }*/

    /*@Override
    public StatusModelData getStatusInfo (IBlockState state) {
        return statusInfo;
    }*/

    @Override
    protected int getDrawerSlot(Direction side, Vec3d hit) {
        if (hitTop(hit.y))
            return 0;

        if (hitLeft(side, hit.x, hit.z))
            return 1;
        else
            return 2;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack) {
        super.onPlaced(world, pos, state, entity, stack);

        TileEntityDrawers tile = getTileEntity(world, pos);
        if (tile != null) {
            IDrawerGroup group = tile.getGroup();
            for (int i = group.getDrawerCount() - 1; i >= 0; i--) {
                if (!group.getDrawer(i).isEmpty()) {
                    world.setBlockState(pos, state.with(SLOTS, EnumCompDrawer.byOpenSlots(i + 1)), 3);
                    break;
                }
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new TileEntityDrawersComp.Slot3();
    }

}
