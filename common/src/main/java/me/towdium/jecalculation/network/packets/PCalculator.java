package me.towdium.jecalculation.network.packets;

import me.towdium.jecalculation.JecaItem;
import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.network.IPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class PCalculator implements IPacket {
    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(JustEnoughCalculation.MODID, "calculator");

    ItemStack stack;
    int slot;

    public PCalculator(FriendlyByteBuf buf) {
        stack = ItemStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf) buf);
        slot = buf.readInt();
    }

    public PCalculator(ItemStack stack, int slot) {
        this.stack = stack;
        this.slot = slot;
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        ItemStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf) buf, stack);
        buf.writeInt(slot);
    }

    @Override
    public void handle(Player player) {
        Inventory inventory = player.getInventory();
        ItemStack calculator = inventory.getItem(slot);
        if (!calculator.isEmpty() && calculator.getItem() instanceof JecaItem) {
            inventory.setItem(slot, stack);
            return;
        }
        calculator = inventory.offhand.get(0);
        if (!calculator.isEmpty() && calculator.getItem() instanceof JecaItem) {
            inventory.offhand.set(0, stack);
        }
    }
}
