package me.towdium.jecalculation.neoforge;

import me.towdium.jecalculation.JecaCommand;
import me.towdium.jecalculation.JustEnoughCalculation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@Mod(JustEnoughCalculation.MODID)
public class JustEnoughCalculationNeoForge {
    public JustEnoughCalculationNeoForge(IEventBus modBus, ModContainer container) {
        JecaCapability.ATTACHMENTS.register(modBus);
        //noinspection InstantiationOfUtilityClass
        new JustEnoughCalculation();
        container.registerConfig(ModConfig.Type.COMMON, JecaConfig.common, JecaConfig.PATH);
    }

    @EventBusSubscriber(modid = JustEnoughCalculation.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
    public static class ClientEvents {
        @SubscribeEvent
        public static void registerClientCommands(RegisterClientCommandsEvent event) {
            JecaCommand.register(event.getDispatcher());
        }
    }
}
