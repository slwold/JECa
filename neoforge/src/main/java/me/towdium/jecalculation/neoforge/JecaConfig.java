package me.towdium.jecalculation.neoforge;

import net.neoforged.neoforge.common.ModConfig;
import net.neoforged.neoforge.common.config.ConfigBuilder;
import net.neoforged.neoforge.common.config.ConfigHolder;
import net.neoforged.neoforge.common.config.ConfigValue;
import net.neoforged.neoforge.common.config.IConfigSpec;

/**
 * NeoForge 1.21.1 配置。
 *
 * TODO(迁移): NeoForge 21 废弃了 Forge 的 ForgeConfigSpec，改用 net.neoforged.neoforge.common.config
 * 下的 ConfigHolder / ConfigBuilder / IConfigSpec。下方写法基于 NeoForge 1.21.1 新配置 API，
 * 首次编译时请对照 net.neoforged.neoforge.common.config 包核实：
 *   - ConfigHolder.register(fileName, ModConfig.Type, Function<ConfigBuilder, ConfigBuilder>) 签名
 *   - ConfigBuilder.define(name, default) 返回 ConfigValue<T>
 * 若 API 有出入，以 NeoForge 1.21.1 官方文档为准。
 */
public class JecaConfig {
    public static final String PATH = "jecalculation/config.toml";

    public static final IConfigSpec<ConfigBuilder> SPEC = ConfigHolder.register(
            "jecalculation/config",
            ModConfig.Type.COMMON,
            builder -> {
                clientMode = builder.define("clientMode", false);
                return builder;
            }
    );

    public static ConfigValue<Boolean> clientMode;

    public static boolean isClientMode() {
        return Boolean.TRUE.equals(clientMode.get());
    }
}
