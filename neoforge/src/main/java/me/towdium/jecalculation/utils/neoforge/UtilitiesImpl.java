package me.towdium.jecalculation.utils.neoforge;

import dev.architectury.fluid.FluidStack;
import me.towdium.jecalculation.data.structure.RecordPlayer;
import me.towdium.jecalculation.neoforge.JecaCapability;
import me.towdium.jecalculation.neoforge.JecaConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class UtilitiesImpl {
    /**
     * Item capabilities no longer exist in 1.21 — the data component patch covers what this
     * dimension used to express, so a label never carries cap data any more.
     */
    public static CompoundTag getCap(ItemStack itemStack) {
        return null;
    }

    public static ItemStack createItemStackWithCap(Item item, int count, CompoundTag cap) {
        return new ItemStack(item, count);
    }

    public static RecordPlayer getRecord(Player player) {
        return JecaCapability.getRecord(player);
    }

    public static boolean isClientMode() {
        return JecaConfig.clientMode.get();
    }

    public static boolean areCapsCompatible(ItemStack itemStack, ItemStack itemStack1) {
        return ItemStack.isSameItemSameComponents(itemStack, itemStack1);
    }

    public static FluidStack createFluidStackFromJeiIngredient(Object object) {
        if (object instanceof net.neoforged.neoforge.fluids.FluidStack fluidStack)
            return FluidStack.create(fluidStack.getFluidHolder(), fluidStack.getAmount(), fluidStack.getComponentsPatch());
        return null;
    }
}
