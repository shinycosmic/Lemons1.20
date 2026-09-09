package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.ai.ClimbPanicGoal;
import net.lemon.animalia.entity.bases.AnimaliaLandBase;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.ModTags;
import net.lemon.animalia.util.AnimaliaFunctionUtil;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class CrossarchusEntity extends AnimaliaLandBase implements GeoEntity, Scannable {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public CrossarchusEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public Item getBreedingItem() {
        return ModItems.TERMITE.get();
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ModTags.Items.INVERTEBRATE;
    }

    @Override
    public ActivityTime activityTime() {
        return ActivityTime.DIURNAL;
    }

    @Override
    public AppName getApp() {
        return AppName.FIELD;
    }

    @Override
    public Component getTrivia() {
        return Component.translatable("trivia.animalia.crossarchus");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.herpestidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.carnivora");
    }

    @Override
    public int getScaleforGUI() {
//        if (this.getType() == ModEntities.SMUTSIA_GIGANTEA.get()) {
//            return 22;
//        }
        return Scannable.super.getScaleforGUI();
    }

    public static void registerHolonet(){
//        HolonetEntities.register(ModEntities.SMUTSIA_GIGANTEA, AppName.FIELD, "Pholidota");
    }

    @Override
    public float genVarSizeMultiplier() {
//        if (this.getType() == ModEntities.SMUTSIA_GIGANTEA.get()) {
//            return AnimaliaFunctionUtil.getScaleForSize(39, 137);
//        }
        return 1;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new ClimbPanicGoal(this, 1.6D, 200, 8.0D, 16, 6, BlockTags.LOGS));
    }

    @Override
    public boolean canClimb() {
        return true;
    }

    @Override
    public boolean isClimbableBlock(BlockState state) {
        return state.is(BlockTags.LOGS);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (dataTag == null) {
            this.setVarColor(1);
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }
}
