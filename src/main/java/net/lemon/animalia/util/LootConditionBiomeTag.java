package net.lemon.animalia.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.lemon.animalia.registry.ModLootModifiers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record LootConditionBiomeTag(TagKey<Biome> tag) implements LootItemCondition {
    public static final MapCodec<LootConditionBiomeTag> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    TagKey.codec(Registries.BIOME).fieldOf("tag").forGetter(LootConditionBiomeTag::tag)).apply(instance, LootConditionBiomeTag::new)
    );

    @Override
    public @NotNull LootItemConditionType getType() {
        return ModLootModifiers.BIOME_TAG.get();
    }

    public boolean test(LootContext context) {
        Vec3 origin = context.getParamOrNull(LootContextParams.ORIGIN);
        return origin != null && context.getLevel().getBiome(BlockPos.containing(origin)).is(this.tag);
    }

    public static LootConditionBiomeTag.Builder biome(TagKey<Biome> tag) {
        return new LootConditionBiomeTag.Builder(tag);
    }

    public static class Builder implements LootItemCondition.Builder {
        private final TagKey<Biome> tag;

        public Builder(TagKey<Biome> tag) {
            this.tag = tag;
        }

        public LootConditionBiomeTag build() {
            return new LootConditionBiomeTag(this.tag);
        }
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<LootConditionBiomeTag> {
        @Override
        public void serialize(JsonObject json, LootConditionBiomeTag condition, JsonSerializationContext context) {
            CODEC.codec().encodeStart(JsonOps.INSTANCE, condition).result().ifPresent(element -> element.getAsJsonObject().entrySet().forEach(entry -> json.add(entry.getKey(), entry.getValue())));
        }

        @Override
        public LootConditionBiomeTag deserialize(JsonObject json, JsonDeserializationContext context) {
            return CODEC.codec().parse(JsonOps.INSTANCE, json).getOrThrow(false, error -> {});
        }
    }
}