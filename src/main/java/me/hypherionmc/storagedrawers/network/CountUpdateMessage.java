package me.hypherionmc.storagedrawers.network;

import me.hypherionmc.storagedrawers.StorageDrawers;
import me.hypherionmc.storagedrawers.block.tile.TileEntityDrawers;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class CountUpdateMessage
{
    private int x;
    private int y;
    private int z;
    private int slot;
    private int count;

    private boolean failed;

    public CountUpdateMessage (BlockPos pos, int slot, int count) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.slot = slot;
        this.count = count;
        this.failed = false;
    }

    private CountUpdateMessage (boolean failed) {
        this.failed = failed;
    }

    public static CountUpdateMessage decode (ByteBuf buf) {
        try {
            int x = buf.readInt();
            int y = buf.readShort();
            int z = buf.readInt();
            int slot = buf.readByte();
            int count = buf.readInt();
            return new CountUpdateMessage(new BlockPos(x, y, z), slot, count);
        }
        catch (IndexOutOfBoundsException e) {
            StorageDrawers.log.error("CountUpdateMessage: Unexpected end of packet.\nMessage: " + ByteBufUtil.hexDump(buf, 0, buf.writerIndex()), e);
            return new CountUpdateMessage(true);
        }
    }

    public static void encode (CountUpdateMessage msg, PacketBuffer buf) {
        buf.writeInt(msg.x);
        buf.writeShort(msg.y);
        buf.writeInt(msg.z);
        buf.writeByte(msg.slot);
        buf.writeInt(msg.count);
    }

    public static void handle(CountUpdateMessage msg, Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> handleClient(msg, ctx.get()));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(CountUpdateMessage msg, NetworkEvent.Context ctx) {
        if (!msg.failed) {
            World world = Minecraft.getInstance().world;
            if (world != null) {
                BlockPos pos = new BlockPos(msg.x, msg.y, msg.z);
                TileEntity tileEntity = world.getTileEntity(pos);
                if (tileEntity instanceof TileEntityDrawers) {
                    ((TileEntityDrawers) tileEntity).clientUpdateCount(msg.slot, msg.count);
                }
            }
        }
        ctx.setPacketHandled(true);
    }
}
