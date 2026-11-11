package me.towdium.jecalculation.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * A packet in the mod's own terms. Architectury's NetworkChannel worked on 1.20 but registers
 * every channel again on each RegisterPayloadHandlersEvent, and NeoForge 21.1.2xx dispatches that
 * event once per mod — so the second dispatch throws "already registered". Each loader now carries
 * these over its native payload system instead.
 */
public interface IPacket {
    ResourceLocation id();

    void write(FriendlyByteBuf buf);

    /**
     * Runs on the receiving side, already on the main thread.
     */
    void handle(Player player);
}
