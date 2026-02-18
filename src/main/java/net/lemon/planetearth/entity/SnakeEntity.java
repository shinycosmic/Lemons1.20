package net.lemon.planetearth.entity;

import com.google.common.collect.ImmutableMap;
import net.lemon.planetearth.item.ModItems;
import net.lemon.planetearth.util.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.awt.*;
import java.util.Map;

public class SnakeEntity extends Animal implements GeoEntity {
    private AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Integer> VARIANT_ID = SynchedEntityData.defineId(SnakeEntity.class, EntityDataSerializers.INT);
    public static final Map<Integer, MutableComponent> ENTITY_VARIANTS = ImmutableMap.<Integer, MutableComponent>builder()
            .put(1, Component.translatable("entity.snake.ocellatedpampassnake"))
            .build();

    public SnakeEntity(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new BreedGoal(this, 1.15f));
        this.goalSelector.addGoal(2, new TemptGoal(this, 1.15f, Ingredient.of(ModItems.RAW_RODENT.get()), false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.1f));
        this.goalSelector.addGoal(0, new LookAtPlayerGoal(this, Player.class, 1f));
        this.goalSelector.addGoal(0, new RandomLookAroundGoal(this));

    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 3D)
                .add(Attributes.FOLLOW_RANGE, 16D)
                .add(Attributes.MOVEMENT_SPEED, 0.2f);
    }

    public int getVariant() {
        return Mth.clamp(this.entityData.get(VARIANT_ID), 1,1);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT_ID, variant);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT_ID, 1);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Variant", this.getVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setVariant(pCompound.getInt("Variant"));
    }

    public float getBaseSize() {
        switch(this.getVariant()) {
            case 1:
                return 0.5f;
            default:
                return 1;
        }
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return ModEntities.OCELLATED_PAMPAS_SNAKE.get().create(pLevel);
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(ModItems.RAW_RODENT.get());
    }

    private <E extends GeoAnimatable> PlayState predicate(AnimationState<E> state) {
        RawAnimation controller = RawAnimation.begin();
        if(state.isMoving()) {
            controller.thenLoop("slither");
            return PlayState.CONTINUE;
        }

        controller.thenLoop("scent");
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        return 0;
    }

    protected SoundEvent getAmbientSound() { return SoundEvents.TURTLE_AMBIENT_LAND; }

    protected SoundEvent getDeathSound() { return SoundEvents.TURTLE_DEATH; }

    protected SoundEvent getHurtSound() { return SoundEvents.TURTLE_HURT; }

    protected float getSoundVolume() { return 0.2f; }


}
