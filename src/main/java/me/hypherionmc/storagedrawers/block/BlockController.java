package me.hypherionmc.storagedrawers.block;

import me.hypherionmc.storagedrawers.api.storage.INetworked;
import me.hypherionmc.storagedrawers.api.storage.attribute.LockAttribute;
import me.hypherionmc.storagedrawers.block.tile.TileEntityController;
import me.hypherionmc.storagedrawers.config.CommonConfig;
import me.hypherionmc.storagedrawers.core.ModItems;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class BlockController extends HorizontalFacingBlock implements INetworked, BlockEntityProvider
{
    public BlockController (Block.Settings properties) {
        super(properties);
    }

    @Override
    protected void appendProperties (StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public ActionResult onUse (BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Direction blockDir = state.get(FACING);
        TileEntityController te = getTileEntitySafe(world, pos);

        ItemStack item = player.getActiveItem();
        if (!item.isEmpty() && toggle(world, pos, player, item.getItem()))
            return ActionResult.SUCCESS;

        if (blockDir != hit.getSide())
            return ActionResult.CONSUME;

        if (!world.isClient) {
            if (CommonConfig.GENERAL.debugTrace.get() && item.isEmpty())
                te.printDebugInfo();

            te.interactPutItemsIntoInventory(player);
        }

        return ActionResult.SUCCESS;
    }

    public boolean toggle (World world, BlockPos pos, PlayerEntity player, Item item) {
        if (world.isClient || item == null)
            return false;

        if (item == ModItems.DRAWER_KEY)
            toggle(world, pos, player, EnumKeyType.DRAWER);
        //else if (item == ModItems.shroudKey)
        //    toggle(world, pos, player, EnumKeyType.CONCEALMENT);
        else if (item == ModItems.QUANTIFY_KEY)
            toggle(world, pos, player, EnumKeyType.QUANTIFY);
        //else if (item == ModItems.personalKey)
        //    toggle(world, pos, player, EnumKeyType.PERSONAL);
        else
            return false;

        return true;
    }

    public void toggle (World world, BlockPos pos, PlayerEntity player, EnumKeyType keyType) {
        if (world.isClient)
            return;

        TileEntityController te = getTileEntitySafe(world, pos);
        if (te == null)
            return;

        switch (keyType) {
            case DRAWER:
                te.toggleLock(EnumSet.allOf(LockAttribute.class), LockAttribute.LOCK_POPULATED, player.getGameProfile());
                break;
            //case CONCEALMENT:
            //    te.toggleShroud(player.getGameProfile());
            //    break;
            case QUANTIFY:
                te.toggleQuantified(player.getGameProfile());
                break;
            //case PERSONAL:
            //    String securityKey = ModItems.personalKey.getSecurityProviderKey(0);
            //    ISecurityProvider provider = StorageDrawers.securityRegistry.getProvider(securityKey);

            //    te.toggleProtection(player.getGameProfile(), provider);
            //    break;
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.isClient)
            return;

        TileEntityController te = getTileEntitySafe(world, pos);
        if (te == null)
            return;

        te.updateCache();

        world.getBlockTickScheduler().schedule(pos, this, 100);
    }

    public TileEntityController getTileEntity (WorldAccess blockAccess, BlockPos pos) {
        BlockEntity tile = blockAccess.getBlockEntity(pos);
        return (tile instanceof TileEntityController) ? (TileEntityController) tile : null;
    }

    public TileEntityController getTileEntitySafe (World world, BlockPos pos) {
        TileEntityController tile = getTileEntity(world, pos);
        if (tile == null) {
            tile = createBlockEntity(world);
            world.setBlockEntity(pos, tile);
        }

        return tile;
    }

    @Nullable
    @Override
    public TileEntityController createBlockEntity(BlockView world) {
        return new TileEntityController();
    }
}
