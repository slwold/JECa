package me.towdium.jecalculation.neoforge;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import me.towdium.jecalculation.data.structure.RecordPlayer;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

/**
 * NeoForge 1.21.1 能力系统已从 Capability（ICapabilityProvider / LazyOptional / AttachCapabilitiesEvent）
 * 重构为 Attachment API。这里用 AttachmentType 把 RecordPlayer 直接挂到 Player 上，
 * 并用 copyOnDeath() 取代原先的 PlayerEvent.Clone 逻辑。
 *
 * 注意：RecordPlayer 需提供一个 Codec<RecordPlayer>（已在 common 模块 RecordPlayer 中新增 RECORD_CODEC 字段），
 * 用其既有的 CompoundTag 序列化逻辑包装而成。
 */
@Mod.EventBusSubscriber
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JecaCapability {
    public static final DeferredRegister<net.neoforged.neoforge.attachment.AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, JustEnoughCalculation.MODID);

    public static final Supplier<net.neoforged.neoforge.attachment.AttachmentType<RecordPlayer>> RECORD =
            ATTACHMENTS.register("record", () -> net.neoforged.neoforge.attachment.AttachmentType.builder(RecordPlayer::new)
                    .serialize(RecordPlayer.RECORD_CODEC)
                    .copyOnDeath()
                    .build());

    public static RecordPlayer getRecord(Player player) {
        return player.getData(RECORD);
    }

    /** 在 @Mod 构造器中调用，把附件注册到模组事件总线 */
    public static void register(IEventBus modEventBus) {
        ATTACHMENTS.register(modEventBus);
    }
}
