package me.towdium.jecalculation.neoforge;

import dev.architectury.platform.Platform;
// Architectury 13.x 的 neoforge 平台模块复用 forge 包路径的 EventBuses
import dev.architectury.platform.forge.EventBuses;
import me.towdium.jecalculation.JecaCommand;
import me.towdium.jecalculation.JustEnoughCalculation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@Mod(JustEnoughCalculation.MODID)
@Mod.EventBusSubscriber
public class JustEnoughCalculationNeoForge {
    public JustEnoughCalculationNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        // NeoForge 21 移除了 FMLJavaModLoadingContext.get()，事件总线改为 @Mod 构造器参数注入
        EventBuses.registerModEventBus(JustEnoughCalculation.MODID, modEventBus);
        //noinspection InstantiationOfUtilityClass
        new JustEnoughCalculation();
        // NeoForge 21 用 ModContainer.registerConfig 取代 ModLoadingContext.get().registerConfig
        modContainer.registerConfig(ModConfig.Type.COMMON, JecaConfig.SPEC,
                Platform.getConfigFolder().resolve(JecaConfig.PATH).toString());
        // 注册附件（能力）到模组事件总线
        JecaCapability.register(modEventBus);
    }

    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        JecaCommand.register(event.getDispatcher());
    }
}
