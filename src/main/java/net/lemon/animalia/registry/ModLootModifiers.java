package net.lemon.animalia.registry;

import com.mojang.serialization.Codec;
import net.lemon.animalia.Animalia;
import net.lemon.animalia.util.AddItemModifier;
import net.lemon.animalia.util.LootConditionBiomeTag;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, Animalia.MODID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_ITEM =
            LOOT_MODIFIER_SERIALIZERS.register("add_item", AddItemModifier.CODEC);

    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITIONS =
            DeferredRegister.create(Registries.LOOT_CONDITION_TYPE, Animalia.MODID);

    public static final RegistryObject<LootItemConditionType> BIOME_TAG =
            LOOT_CONDITIONS.register("biome_tag", () -> new LootItemConditionType(new LootConditionBiomeTag.Serializer()));

    public static void register(IEventBus eventBus) {
        LOOT_MODIFIER_SERIALIZERS.register(eventBus);
        LOOT_CONDITIONS.register(eventBus);
    }
}
