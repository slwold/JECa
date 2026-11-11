package me.towdium.jecalculation.network;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.entity.player.Player;

public class Network {
    @ExpectPlatform
    public static void sendToServer(IPacket packet) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void sendToPlayer(Player player, IPacket packet) {
        throw new AssertionError();
    }
}
