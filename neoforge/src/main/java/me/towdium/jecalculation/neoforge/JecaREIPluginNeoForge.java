package me.towdium.jecalculation.neoforge;

// TODO(迁移): REI 1.21 的 neoforge 平台客户端插件注解包名需核实。
//   参考写法为 me.shedaniel.rei.neoforge.REIPluginClient；若 REI 发行坐标/packages 有变动请调整。
import me.shedaniel.rei.neoforge.REIPluginClient;
import me.towdium.jecalculation.compat.rei.JecaREIPlugin;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@REIPluginClient
public class JecaREIPluginNeoForge extends JecaREIPlugin {
}
