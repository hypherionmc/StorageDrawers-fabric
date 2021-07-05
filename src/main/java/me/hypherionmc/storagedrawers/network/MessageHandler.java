package me.hypherionmc.storagedrawers.network;

import me.hypherionmc.storagedrawers.StorageDrawers;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class MessageHandler
{
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder
        .named(new ResourceLocation(StorageDrawers.MOD_ID, "main_channel"))
        .networkProtocolVersion(() -> PROTOCOL_VERSION)
        .clientAcceptedVersions(PROTOCOL_VERSION::equals)
        .serverAcceptedVersions(PROTOCOL_VERSION::equals)
        .simpleChannel();

    public static void init() {
        INSTANCE.registerMessage(0, CountUpdateMessage.class, CountUpdateMessage::encode, CountUpdateMessage::decode, CountUpdateMessage::handle);
    }
}
