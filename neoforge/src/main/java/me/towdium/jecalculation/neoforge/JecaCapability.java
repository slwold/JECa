package me.towdium.jecalculation.neoforge;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import me.towdium.jecalculation.data.structure.RecordPlayer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

/**
 * Player record storage. Forge capabilities were removed in 1.21, so the record is kept as a
 * data attachment that holds a live, mutable container — same lifetime semantics as before.
 *
 * Author: Towdium
 * Date: 18-9-24
 */
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JecaCapability {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, JustEnoughCalculation.MODID);

    public static final Supplier<AttachmentType<Container>> CAPABILITY_RECORD = ATTACHMENTS.register(
            "record",
            () -> AttachmentType.serializable(Container::new).copyOnDeath().build());

    public static RecordPlayer getRecord(Player player) {
        return player.getData(CAPABILITY_RECORD).getRecord();
    }

    public static class Container implements INBTSerializable<CompoundTag> {
        RecordPlayer record;

        public RecordPlayer getRecord() {
            if (record == null) record = new RecordPlayer();
            return record;
        }

        public void setRecord(RecordPlayer r) {
            record = r;
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider) {
            return getRecord().serialize();
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
            boolean s = LPlaceholder.state;
            LPlaceholder.state = false;
            setRecord(new RecordPlayer(nbt));
            LPlaceholder.state = s;
        }
    }
}
