package net.lemon.animalia.datagen;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagGenerator extends EntityTypeTagsProvider {

    public ModEntityTypeTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper
            existingFileHelper) {
        super(output, lookupProvider, Animalia.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ModTags.EntityTypes.FORAGE_FISH).add(ModEntities.LEPTOBRAMA_MUELLERI.get());

        tag(ModTags.EntityTypes.PREDATORS).add(ModEntities.CARCHARHINUS_BREVIPINNA.get());
    }
}