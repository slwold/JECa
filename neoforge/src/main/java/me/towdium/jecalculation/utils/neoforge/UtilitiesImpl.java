package me.towdium.jecalculation.utils.neoforge;

import dev.architectury.fluid.FluidStack;
// Architectury 13.x 的 neoforge 平台模块复用了 forge 包路径的流体 hooks，
// 因此这里仍引用 dev.architectury.hooks.fluid.forge.FluidStackHooksForge（其参数已为 neoforge FluidStack）。
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import me.towdium.jecalculation.data.structure.RecordPlayer;
import me.towdium.jecalculation.neoforge.JecaCapability;
import me.towdium.jecalculation.neoforge.JecaConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class UtilitiesImpl {
    public static CompoundTag getCap(ItemStack itemStack) {
        // TODO(迁移): NeoForge 1.21.1 能力系统改为 Attachment，ItemStack.serializeNBT() 是否仍产出
        //   "NeoForgeCaps" 子标签需实测确认（Forge 1.20.1 为 "ForgeCaps"）。
        //   若 Attachment 不通过 NBT 序列化，此处逻辑需要改为读取对应 Attachment。
        CompoundTag nbt = itemStack.serializeNBT();
        return nbt.contains("NeoForgeCaps") ? nbt.getCompound("NeoForgeCaps") : null;
    }

    public static ItemStack createItemStackWithCap(Item item, int count, CompoundTag cap) {
        // TODO(迁移): Forge 的 ItemStack(Item, int, CompoundTag) 三参构造在 NeoForge 行为/存在性需核实。
        //   若 Attachment 不再通过 NBT 注入能力数据，需要改用 Attachment API 写入。
        return new ItemStack(item, count, cap);
    }

    public static RecordPlayer getRecord(Player player) {
        return JecaCapability.getRecord(player);
    }

    public static boolean isClientMode() {
        return JecaConfig.isClientMode();
    }

    public static boolean areCapsCompatible(ItemStack itemStack, ItemStack itemStack1) {
        return itemStack.areCapsCompatible(itemStack1);
    }

    public static FluidStack createFluidStackFromJeiIngredient(Object object) {
        // NeoForge 流体类为 net.neoforged.neoforge.fluids.FluidStack（Forge 为 net.minecraftforge.fluids.FluidStack）
        if (object instanceof net.neoforged.neoforge.fluids.FluidStack fluidStack)
            return FluidStackHooksForge.fromForge(fluidStack);
        return null;
    }
}
