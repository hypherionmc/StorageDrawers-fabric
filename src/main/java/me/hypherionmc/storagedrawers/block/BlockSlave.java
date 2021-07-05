package me.hypherionmc.storagedrawers.block;

import me.hypherionmc.storagedrawers.api.storage.INetworked;
import me.hypherionmc.storagedrawers.block.tile.TileEntitySlave;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlockSlave extends Block implements INetworked, BlockEntityProvider
{
    public BlockSlave (Block.Settings properties) {
        super(properties);
    }

    public void toggle (World world, BlockPos pos, PlayerEntity player, EnumKeyType keyType) {
        TileEntitySlave tile = getTileEntity(world, pos);
        if (tile == null)
            return;

        BlockPos controllerPos = tile.getControllerPos();
        if (controllerPos == null)
            return;

        Block block = world.getBlockState(controllerPos).getBlock();
        if (block instanceof BlockController) {
            BlockController controller = (BlockController)block;
            controller.toggle(world, controllerPos, player, keyType);
        }
    }

    public TileEntitySlave getTileEntity (World blockAccess, BlockPos pos) {
        BlockEntity tile = blockAccess.getBlockEntity(pos);
        return (tile instanceof TileEntitySlave) ? (TileEntitySlave) tile : null;
    }

    public TileEntitySlave getTileEntitySafe (World world, BlockPos pos) {
        TileEntitySlave tile = getTileEntity(world, pos);
        if (tile == null) {
            tile = createBlockEntity(world);
            world.setBlockEntity(pos, tile);
        }

        return tile;
    }

    @Nullable
    @Override
    public TileEntitySlave createBlockEntity(BlockView world) {
        return new TileEntitySlave();
    }
}
