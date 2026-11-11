# JEC 迁移到 1.21.1 NeoForge — 脚手架与改动说明

> 基于 `JustEnoughCalculation` 的 `1.20.1` 分支，对照 `JustEnoughItems@1.21.1` 与 `architectury-api@1.21` 真实源码生成。
> 仓库工作副本：`/workspace/jec120`（已可直接在此编译排错）。

## 0. 结论

**可行，中等偏高工作量。** 真正动手后确认：NeoForge 1.21.1 不只是改包名，
**能力系统（Capability → Attachment API）、配置系统（ForgeConfigSpec → ConfigHolder）、事件总线（`FMLJavaModLoadingContext.get()` 移除）都做了破坏性重构**。
这些已在本脚手架里按 NeoForge 21 API 重写（能力系统已完整改好；配置系统写法需首次编译时对照官方包核实）。
JEI 15 → 19 的核心 API 形态基本保留，仅 `PlatformFluidHelper.create` 签名变了（`create(Fluid,int)` → `create(Holder<Fluid>,long)`）。

---

## 1. 新建：neoforge 模块（核心交付物）

目录 `neoforge/`，由 `forge/` 复制并改造而来：

```
neoforge/
├── build.gradle                       # neoForge{} + architectury-neoforge + shadow 8.1.1
├── gradle.properties                  # loom.platform=neoforge
├── src/main/java/.../neoforge/
│   ├── JustEnoughCalculationNeoForge.java   # @Mod 入口（构造器注入 IEventBus + ModContainer）
│   ├── JecaCapability.java                  # ★ Capability → Attachment API 重写
│   ├── JecaConfig.java                      # ★ ForgeConfigSpec → ConfigHolder（需核实 API）
│   └── JecaREIPluginNeoForge.java           # REI 客户端插件（neoforge 注解）
├── src/main/java/.../utils/neoforge/
│   └── UtilitiesImpl.java                   # 流体反射改 neoforge 包；getCap 改 NeoForgeCaps
└── src/main/resources/
    ├── META-INF/neoforge.mods.toml          # modLoader="neoforge"，依赖版本区间更新
    ├── pack.mcmeta                          # pack_format=34 + neoforge: 前缀 key
    └── data/jecalculation/recipes/*.json     # 配方（forge: 标签在 NeoForge 仍可用）
```

`neoforge` 模块相对 `forge` 的差异清单：

| 文件 | 关键改动 |
|---|---|
| `JustEnoughCalculationNeoForge` | `net.minecraftforge.*` → `net.neoforged.*`；`FMLJavaModLoadingContext.get()` 移除 → 改用 `@Mod` 构造器参数 `IEventBus`/`ModContainer`；`ModLoadingContext.get().registerConfig` → `ModContainer.registerConfig`；`EventBuses` 仍用 `dev.architectury.platform.forge`（architectury 13 的 neoforge 模块复用 forge 包路径） |
| `JecaCapability` | **整套重写**：`ICapabilityProvider`/`LazyOptional`/`AttachCapabilitiesEvent`/`PlayerEvent.Clone` 全部删除，改用 `AttachmentType` + `DeferredRegister(NeoForgeRegistries.ATTACHMENT_TYPES)` + `copyOnDeath()`；访问改为 `player.getData(RECORD)` |
| `JecaConfig` | `ForgeConfigSpec` → `ConfigHolder.register(...)`，新增 `clientMode` 为 `ConfigValue<Boolean>` |
| `JecaREIPluginNeoForge` | `net.minecraftforge.api.distmarker` → `net.neoforged.api.distmarker`；REI 客户端注解改为 `me.shedaniel.rei.neoforge.REIPluginClient`（包名需核实） |
| `UtilitiesImpl` | 流体反射类改 `net.neoforged.neoforge.fluids.FluidStack`；`getCap` 的 NBT key 由 `ForgeCaps` → `NeoForgeCaps`；`FluidStackHooksForge.fromForge` 仍可用（architectury neoforge 复用 forge 包路径，参数已是 neoforge FluidStack） |

---

## 2. 修改：根配置与 common

| 文件 | 改动 |
|---|---|
| `settings.gradle` | 新增 `include("neoforge")`；pluginManagement 增加 `maven.neoforged.net/releases` 与 `api.modrinth.com/maven` |
| `gradle.properties` | `minecraft_version=1.21.1`；新增 `neoforge_version=21.1.172`；`architectury_version=13.0.11`；`jei_version=19.21.2.313`；`enabled_platforms` 追加 `neoforge`；`rei_version` 占位（需核实 1.21.1 真实号） |
| `build.gradle` | loom `1.3-SNAPSHOT` → `1.7-SNAPSHOT`；Java `release`/`toolchain` 17 → **21**；parchment 改为 1.21.1（暂默认关闭，避免坐标错误阻断构建）；新增 neoforge maven |
| `common/.../compat/jei/JecaJEIPlugin.java` | `showRecipe` 里 `create(fluid,int)` → `create(BuiltInRegistries.FLUID.wrapAsHolder(fluid), amount)`；`onRuntimeAvailable` 新增 `Platform.isNeoForge()` 分支反射 `net.neoforged.neoforge.fluids.FluidStack` |
| `common/.../data/structure/RecordPlayer.java` | 新增 `RECORD_CODEC = CompoundTag.CODEC.xmap(...)`，包装既有 CompoundTag 序列化，供 Attachment API 使用 |

> `common/build.gradle` 的 JEI 依赖 `jei-${minecraft_version}-common-api` 会自动推导为 `jei-1.21.1-common-api`，无需手改。

---

## 3. 对照 JEI 1.21.1（已核实保留的 API）

在 `JustEnoughItems@1.21.1` 源码中确认以下 JEC 用到的调用**仍然存在**（形态稳定）：
- `IJeiRuntime.getIngredientListOverlay()/getBookmarkOverlay()/getJeiHelpers()/getRecipeManager()/getRecipesGui()`
- `IIngredientListOverlay.getIngredientUnderMouse()`、`IBookmarkOverlay.getIngredientUnderMouse()`
- `IJeiHelpers.getPlatformFluidHelper()`、`IFocusFactory.createFocus()`、`IIngredientManager.getIngredientTypeChecked()`
- `IGuiHandlerRegistration.addGlobalGuiHandler()`、`IRecipeTransferRegistration.addUniversalRecipeTransferHandler()`
- `IRecipeManager.createRecipeCategoryLookup()`（JEC 第 143 行 `transferRecipe` 仍在用）
- `IRecipeTransferHandler` 接口签名（`getContainerClass/getMenuType/getRecipeType/transferRecipe`）保留，仅 `transferRecipe` 旧 6 参重载被标 `@deprecated`（仍可用，新签名 `transferRecipe(IRecipeTransferContext, boolean)` 可选迁移）

**唯一确认的 breaking change**：`IPlatformFluidHelper.create` 由
`create(Fluid, int)` 变为 `create(Holder<Fluid>, long)`（已在 `JecaJEIPlugin.showRecipe` 处理）。

---

## 4. 仍需手动核实 / 收尾的 TODO（编译期会暴露）

1. **`JecaConfig` 配置 API**（中风险）：`ConfigHolder.register(fileName, ModConfig.Type, Function<ConfigBuilder,ConfigBuilder>)` 的精确签名与 `ConfigBuilder.define` 返回类型，请对照 NeoForge 1.21.1 的 `net.neoforged.neoforge.common.config` 包核实。
2. **`UtilitiesImpl.getCap / createItemStackWithCap`**（中风险）：NeoForge 用 Attachment 取代 NBT 能力注入，`ItemStack.serializeNBT()` 是否仍产出 `NeoForgeCaps` 子标签、三参 `ItemStack(Item,int,CompoundTag)` 构造是否存在/行为，需实测。这两项关系到"带能力物品"的检测逻辑。
3. **REI 1.21.1 坐标与注解包**：`RoughlyEnoughItems-neoforge` 坐标、`me.shedaniel.rei.neoforge.REIPluginClient` 包名需按实际发布核对（`rei_version` 当前为占位 `16.0.0`）。
4. **`neoforge_version=21.1.172`**：请 pin 到已知可用的构建（最新发布约 `21.1.265`），仅影响构建解析。
5. **`forge/fabric/quilt` 模块未改**：本项目把 `common` 整体升到了 1.21.1（JEI 19 + Architectury 13），因此这三个模块也必须同步做与 neoforge 模块相同的 1.21.1 破坏性修复（config / capability → attachment / 入口 `FMLJavaModLoadingContext` 移除），否则全平台构建会失败。neoforge 模块即它们的参考实现。

---

## 5. 推荐验证流程（先孤立验证 neoforge）

由于 `common` 已升 1.21.1，建议**先单独验证 neoforge**，再回头补 forge/fabric/quilt：

1. 临时在 `settings.gradle` 只保留 `include("common")` 与 `include("neoforge")`；`gradle.properties` 的 `enabled_platforms` 暂设为 `neoforge`。
2. 编译：`gradlew :neoforge:build` 或先 `gradlew :neoforge:genSources` 排错。
3. 实测重点：合成计算器的"从 JEI 转移配方"、"流体 ingredient"、"玩家记录（Attachment 持久化/死亡保留）"、"客户端模式配置"。
4. 验证通过后，再按 neoforge 模块的改法逐个迁移 `forge` / `fabric` / `quilt`。

---

## 6. 关键依赖版本（已核实）

| 组件 | 1.20.1（基线） | 1.21.1（目标） |
|---|---|---|
| Minecraft | 1.20.1 | 1.21.1 |
| Architectury | 9.1.12 | 13.0.11 |
| JEI | 15.2.0.27 | 19.21.2.313 |
| NeoForge | — | 21.1.x |
| Java | 17 | **21** |
| Architectury Loom | 1.3-SNAPSHOT | 1.7-SNAPSHOT |
