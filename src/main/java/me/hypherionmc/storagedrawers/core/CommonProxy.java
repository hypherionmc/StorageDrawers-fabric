package me.hypherionmc.storagedrawers.core;

import me.hypherionmc.storagedrawers.StorageDrawers;
import me.hypherionmc.storagedrawers.block.BlockDrawers;
import me.hypherionmc.storagedrawers.block.tile.TileEntityDrawers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;


public class CommonProxy
{
    public final Identifier iconConcealmentOverlayResource = new Identifier(StorageDrawers.MOD_ID + ":blocks/overlay/shading_concealment");
    public final Identifier iconIndicatorCompOnResource = new Identifier(StorageDrawers.MOD_ID + ":blocks/indicator/indicator_comp_on");
    public final Identifier iconIndicatorCompOffResource = new Identifier(StorageDrawers.MOD_ID + ":blocks/indicator/indicator_comp_off");

    public final Identifier[] iconIndicatorOnResource = new Identifier[] {
        null,
        new Identifier(StorageDrawers.MOD_ID + ":blocks/indicator/indicator_1_on"),
        new Identifier(StorageDrawers.MOD_ID + ":blocks/indicator/indicator_2_on"),
        null,
        new Identifier(StorageDrawers.MOD_ID + ":blocks/indicator/indicator_4_on"),
    };
    public final Identifier[] iconIndicatorOffResource = new Identifier[] {
        null,
        new Identifier(StorageDrawers.MOD_ID + ":blocks/indicator/indicator_1_off"),
        new Identifier(StorageDrawers.MOD_ID + ":blocks/indicator/indicator_2_off"),
        null,
        new Identifier(StorageDrawers.MOD_ID + ":blocks/indicator/indicator_4_off"),
    };

    public CommonProxy () {
        MinecraftForge.EVENT_BUS.addListener(this::playerLeftClick);
        MinecraftForge.EVENT_BUS.addListener(this::playerRightClick);
    }

    public void registerRenderers ()
    { }

    public void updatePlayerInventory (PlayerEntity player) {
        if (player instanceof ServerPlayerEntity)
            ((ServerPlayerEntity) player).sendContainerToPlayer(player.container);
    }

    private void playerLeftClick (PlayerInteractEvent.LeftClickBlock event) {
        //if (event.getWorld().isRemote) {
            BlockPos pos = event.getPos();
            BlockState state = event.getWorld().getBlockState(pos);
            Block block = state.getBlock();
            if (block instanceof BlockDrawers) {
                if (event.getPlayer().isCreative()) {
                    if (!((BlockDrawers) block).creativeCanBreakBlock(state, event.getWorld(), pos, event.getPlayer())) {
                        state.onBlockClicked(event.getWorld(), pos, event.getPlayer());
                        event.setCanceled(true);
                    }
                }
            }
        //}
    }

    private void playerRightClick (PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() == Hand.MAIN_HAND && event.getItemStack().isEmpty()) {
            TileEntity tile = event.getWorld().getTileEntity(event.getPos());
            if (tile instanceof TileEntityDrawers) {
                event.setUseBlock(Event.Result.ALLOW);
            }
        }
    }
}
