package me.towdium.jecalculation.data.structure;

import com.mojang.serialization.Codec;
import me.towdium.jecalculation.data.label.labels.LPlaceholder;
import net.minecraft.nbt.CompoundTag;

/**
 * Author: Towdium
 * Date: 19-1-21
 */
public class RecordPlayer implements IRecord {
    public static final String KEY_RECIPES = "recipes";
    public static final String KEY_LAST = "last";

    // NeoForge 1.21.1 Attachment API 需要 Codec<RecordPlayer>：复用既有 CompoundTag 序列化逻辑，
    // 并保留原 Capability 反序列化时临时关闭 LPlaceholder.state 的行为。
    public static final Codec<RecordPlayer> RECORD_CODEC = CompoundTag.CODEC.xmap(
            tag -> {
                boolean s = LPlaceholder.state;
                LPlaceholder.state = false;
                RecordPlayer r = new RecordPlayer(tag);
                LPlaceholder.state = s;
                return r;
            },
            RecordPlayer::serialize
    );

    public Recipes recipes;
    public String last;  // last group edited

    public RecordPlayer() {
        recipes = new Recipes();
    }

    public RecordPlayer(CompoundTag nbt) {
        this();
        deserialize(nbt);
    }

    public void deserialize(CompoundTag tag) {
        recipes.deserialize(tag.getCompound(KEY_RECIPES));
        last = tag.getString(KEY_LAST);
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag ret = new CompoundTag();
        if (last != null) ret.putString(KEY_LAST, last);
        ret.put(KEY_RECIPES, recipes.serialize());
        return ret;
    }
}
