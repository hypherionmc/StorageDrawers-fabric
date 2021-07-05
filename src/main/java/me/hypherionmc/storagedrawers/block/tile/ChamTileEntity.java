package me.hypherionmc.storagedrawers.block.tile;

import me.hypherionmc.storagedrawers.StorageDrawers;
import me.hypherionmc.storagedrawers.block.tile.tiledata.TileDataShim;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeTileEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ChamTileEntity extends BlockEntity implements IForgeTileEntity
{
    private NbtCompound failureSnapshot;
    private List<TileDataShim> fixedShims;
    private List<TileDataShim> portableShims;

    public ChamTileEntity (BlockEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public boolean hasDataPacket () {
        return true;
    }

    public boolean dataPacketRequiresRenderUpdate () {
        return false;
    }

    public void injectData (TileDataShim shim) {
        if (fixedShims == null)
            fixedShims = new ArrayList<TileDataShim>();
        fixedShims.add(shim);
    }

    public void injectPortableData (TileDataShim shim) {
        if (portableShims == null)
            portableShims = new ArrayList<TileDataShim>();
        portableShims.add(shim);
    }

    @Override
    public void fromTag(BlockState state, NbtCompound tag) {
        super.fromTag(state, tag);

        failureSnapshot = null;

        try {
            readFixed(tag);
            readPortable(tag);
        }
        catch (Throwable t) {
            trapLoadFailure(t, tag);
        }
    }
    
    public final void read (NbtCompound tag) {
        read(null, tag);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);

        if (failureSnapshot != null) {
            restoreLoadFailure(tag);
            return tag;
        }

        try {
            tag = writeFixed(tag);
            tag = writePortable(tag);
        }
        catch (Throwable t) {
            StorageDrawers.log.error("Tile Save Failure.", t);
        }

        return tag;
    }

    public void readPortable (NbtCompound tag) {
        if (portableShims != null) {
            for (TileDataShim shim : portableShims)
                shim.read(tag);
        }
    }

    public NbtCompound writePortable (NbtCompound tag) {
        if (portableShims != null) {
            for (TileDataShim shim : portableShims)
                tag = shim.write(tag);
        }

        return tag;
    }

    protected void readFixed (NbtCompound tag) {
        if (fixedShims != null) {
            for (TileDataShim shim : fixedShims)
                shim.read(tag);
        }
    }

    protected NbtCompound writeFixed (NbtCompound tag) {
        if (fixedShims != null) {
            for (TileDataShim shim : fixedShims)
                tag = shim.write(tag);
        }

        return tag;
    }

    private void trapLoadFailure (Throwable t, NbtCompound tag) {
        failureSnapshot = tag.copy();
        StorageDrawers.log.error("Tile Load Failure.", t);
    }

    private void restoreLoadFailure (NbtCompound tag) {
        for (String key : failureSnapshot.getKeys()) {
            if (!tag.contains(key))
                tag.put(key, failureSnapshot.get(key).copy());
        }
    }

    protected boolean loadDidFail () {
        return failureSnapshot != null;
    }

    @Override
    public final NbtCompound getUpdateTag () {
        NbtCompound tag = new NbtCompound();
        write(tag);

        return tag;
    }

    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return hasDataPacket() ? new BlockEntityUpdateS2CPacket(getPos(), 0, getUpdateTag()) : null;
    }

    @Override
    public final void onDataPacket (ClientConnection net, BlockEntityUpdateS2CPacket pkt) {
        if (pkt != null && pkt.getNbt() != null)
            read(pkt.getNbt());

        if (dataPacketRequiresRenderUpdate() && getWorld().isClient) {
            BlockState state = getWorld().getBlockState(getPos());
            getWorld().updateListeners(getPos(), state, state, 3);
        }
    }

    /**
     * Calls server to sync data with client, update neighbors, and cause a delayed render update.
     */
    public void markBlockForUpdate () {
        if (getWorld() != null && !getWorld().isClient) {
            BlockState state = getWorld().getBlockState(pos);
            getWorld().updateListeners(pos, state, state, 3);
        }
    }

    public void markBlockForUpdateClient () {
        if (getWorld() != null && getWorld().isClient) {
            BlockState state = getWorld().getBlockState(pos);
            getWorld().updateListeners(pos, state, state, 3);
        }
    }

    /**
     * Causes immediate render update when called client-side, or delayed render update when called server-side.
     * Does not sync tile data or notify neighbors of any state change.
     */
    public void markBlockForRenderUpdate () {
        if (getWorld() == null)
            return;

        //if (getWorld().isRemote)
        //    getWorld().markBlockRangeForRenderUpdate(pos, pos);
        //else {
        BlockState state = getWorld().getBlockState(pos);
        getWorld().updateListeners(pos, state, state, 2);
        //}
    }
}
