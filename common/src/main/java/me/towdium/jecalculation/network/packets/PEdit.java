package me.towdium.jecalculation.network.packets;

import me.towdium.jecalculation.JustEnoughCalculation;
import me.towdium.jecalculation.data.structure.Recipe;
import me.towdium.jecalculation.network.IPacket;
import me.towdium.jecalculation.utils.Utilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class PEdit implements IPacket {
    public static final ResourceLocation ID =
            ResourceLocation.fromNamespaceAndPath(JustEnoughCalculation.MODID, "edit");

    static final String KEY_OLD = "old";
    static final String KEY_NEW = "new";
    static final String KEY_INDEX = "index";
    static final String KEY_RECIPE = "recipe";

    String old, neu;
    int index;
    Recipe recipe;

    public PEdit() {
    }

    // set recipe: Y Y/null Y Y
    // rename group: Y Y -1 null
    // add recipe: Y null -1 Y
    // remove recipe: Y null Y null
    // remove group: Y null -1 null
    public PEdit(String neu, @Nullable String old, int index, @Nullable Recipe recipe) {
        this.neu = neu;
        this.old = old;
        this.index = index;
        this.recipe = recipe;
    }

    public PEdit(FriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
        tag = tag == null ? new CompoundTag() : tag;
        old = tag.contains(KEY_OLD) ? tag.getString(KEY_OLD) : null;
        neu = tag.getString(KEY_NEW);
        index = tag.getInt(KEY_INDEX);
        recipe = tag.contains(KEY_RECIPE) ? new Recipe(tag.getCompound(KEY_RECIPE)) : null;
    }

    @Override
    public ResourceLocation id() {
        return ID;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        CompoundTag tag = new CompoundTag();
        if (old != null) tag.putString(KEY_OLD, old);
        tag.putString(KEY_NEW, neu);
        tag.putInt(KEY_INDEX, index);
        if (recipe != null) tag.put(KEY_RECIPE, recipe.serialize());
        buf.writeNbt(tag);
    }

    @Override
    public void handle(Player player) {
        Utilities.getRecord(player).recipes.modify(neu, old, index, recipe);
        Utilities.getRecord(player).last = old;
    }
}
