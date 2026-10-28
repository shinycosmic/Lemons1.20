package net.lemon.animalia.registry.worldgen;

import com.mojang.serialization.Codec;
import net.lemon.animalia.Animalia;
import net.lemon.animalia.registry.spawning.ModSpawns;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.ModifiableStructureInfo;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModStructureModifiers {
    public static final DeferredRegister<Codec<? extends StructureModifier>> STRUCTURE_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, Animalia.MODID);

    public static final RegistryObject<Codec<StructureSpawnModifier>> STRUCTURE_SPAWNS =
            STRUCTURE_MODIFIERS.register("structure_spawns", () -> Codec.unit(StructureSpawnModifier::new));

    public static void register(IEventBus eventBus) {
        STRUCTURE_MODIFIERS.register(eventBus);
    }

    public static class StructureSpawnModifier implements StructureModifier {
        @Override
        public void modify(Holder<Structure> structure, Phase phase, ModifiableStructureInfo.StructureInfo.Builder builder) {
            if (phase != Phase.ADD) {
                return;
            }
            for (ModSpawns.StructureSpawnEntry entry : ModSpawns.STRUCTURE_ENTRIES) {
                if (entry.matches(structure)) {
                    builder.getStructureSettings().getOrAddSpawnOverrides(entry.type().get().getCategory())
                            .addSpawn(new MobSpawnSettings.SpawnerData(entry.type().get(), entry.weight(), entry.minGroup(), entry.maxGroup()));
                }
            }
        }

        @Override
        public Codec<? extends StructureModifier> codec() {
            return STRUCTURE_SPAWNS.get();
        }
    }
}