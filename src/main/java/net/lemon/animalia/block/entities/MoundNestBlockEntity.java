package net.lemon.animalia.block.entities;

import net.lemon.animalia.item.FishEggItem;
import net.lemon.animalia.registry.ModBlockEntities;
import net.lemon.animalia.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class MoundNestBlockEntity extends BlockEntity {

    @Nullable
    private EntityType<?> eggSpecies;
    private boolean hasEgg;

    public MoundNestBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MOUND_NEST_BE.get(), pos, state);
    }

    public void setEgg(EntityType<?> species) {
        this.eggSpecies = species;
        this.hasEgg = true;
        this.setChanged();
    }

    public void clearEgg() {
        this.eggSpecies = null;
        this.hasEgg = false;
        this.setChanged();
    }

    public boolean hasEgg() {
        return this.hasEgg;
    }

    @Nullable
    public EntityType<?> getEggSpecies() {
        return this.eggSpecies;
    }

    public void dropEgg(Level level, BlockPos pos) {
        if (!level.isClientSide() && this.hasEgg && this.eggSpecies != null) {
            ItemStack egg = new ItemStack(ModItems.MOUND_FISH_EGG.get());
            FishEggItem.setEntity(egg, this.eggSpecies);
            Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, egg);
            this.clearEgg();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("HasEgg", this.hasEgg);
        if (this.eggSpecies != null) {
            ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(this.eggSpecies);
            if (key != null) {
                tag.putString("EggSpecies", key.toString());
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.hasEgg = tag.getBoolean("HasEgg");
        if (tag.contains("EggSpecies")) {
            String id = tag.getString("EggSpecies");
            this.eggSpecies = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(id));
        }
    }
}