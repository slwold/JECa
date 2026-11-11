package me.towdium.jecalculation.network.packets;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.Controller;
import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import me.towdium.jecalculation.data.structure.RecordPlayer;
import me.towdium.jecalculation.network.IPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class PRecord implements IPacket {
    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(JustEnoughCalculation.MODID, "record");

    public static final String KEY_RECIPES = "recipes";
    public static final String KEY_LAST = "last";
    RecordPlayer record;

    public PRecord(RecordPlayer record) {
        this.record = record;
    }

    public PRecord() {
    }

    public PRecord(FriendlyByteBuf buf) {
        CompoundTag tag = Objects.requireNonNull(buf.readNbt());
        LPlaceholder.state = false;
        record = new RecordPlayer(tag);
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeNbt(record.serialize());
    }

    @Override
    public void handle(Player player) {
        Controller.setRecordsServer(record);
    }
}
