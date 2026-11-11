package me.towdium.jecalculation.network.neoforge;

import me.towdium.jecalculation.network.IPacket;
import me.towdium.jecalculation.network.packets.PCalculator;
import me.towdium.jecalculation.network.packets.PEdit;
import me.towdium.jecalculation.network.packets.PRecord;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Map;
import java.util.function.Function;

/**
 * NeoForge's native payload transport. The packets themselves stay platform independent — this
 * class only maps them onto payload types and moves the bytes.
 */
public class NetworkImpl {
    public static final CustomPacketPayload.Type<Payload> EDIT_TYPE =
            new CustomPacketPayload.Type<>(PEdit.ID);
    public static final CustomPacketPayload.Type<Payload> CALCULATOR_TYPE =
            new CustomPacketPayload.Type<>(PCalculator.ID);
    public static final CustomPacketPayload.Type<Payload> RECORD_TYPE =
            new CustomPacketPayload.Type<>(PRecord.ID);

    private static final Map<ResourceLocation, CustomPacketPayload.Type<Payload>> BY_ID = Map.of(
            PEdit.ID, EDIT_TYPE,
            PCalculator.ID, CALCULATOR_TYPE,
            PRecord.ID, RECORD_TYPE);

    public static void sendToServer(IPacket packet) {
        PacketDistributor.sendToServer(wrap(packet));
    }

    public static void sendToPlayer(Player player, IPacket packet) {
        if (player instanceof ServerPlayer serverPlayer)
            PacketDistributor.sendToPlayer(serverPlayer, wrap(packet));
    }

    private static Payload wrap(IPacket packet) {
        CustomPacketPayload.Type<Payload> type = BY_ID.get(packet.id());
        if (type == null) throw new IllegalArgumentException("Unregistered packet: " + packet.id());
        return new Payload(type, packet);
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(EDIT_TYPE, codec(EDIT_TYPE, PEdit::new), NetworkImpl::handle);
        registrar.playToServer(CALCULATOR_TYPE, codec(CALCULATOR_TYPE, PCalculator::new), NetworkImpl::handle);
        registrar.playToClient(RECORD_TYPE, codec(RECORD_TYPE, PRecord::new), NetworkImpl::handle);
    }

    private static <B extends FriendlyByteBuf> StreamCodec<B, Payload> codec(
            CustomPacketPayload.Type<Payload> type, Function<FriendlyByteBuf, ? extends IPacket> factory) {
        return StreamCodec.of(
                (buf, payload) -> payload.packet().write(buf),
                buf -> new Payload(type, factory.apply(buf)));
    }

    private static void handle(Payload payload, IPayloadContext ctx) {
        ctx.enqueueWork(() -> payload.packet().handle(ctx.player()));
    }

    public record Payload(CustomPacketPayload.Type<Payload> type, IPacket packet)
            implements CustomPacketPayload {
    }
}
