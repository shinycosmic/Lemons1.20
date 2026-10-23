package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.bases.AnimaliaLandBase;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.entity.bases.helpers.ICanThreat;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModTags;
import net.lemon.animalia.util.AnimaliaFunctionUtil;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class HystrixCristataEntity extends AnimaliaLandBase implements GeoEntity, Scannable, ICanThreat {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> THREAT_PHASE = SynchedEntityData.defineId(AilurusFulgensEntity.class, EntityDataSerializers.INT);


    public HystrixCristataEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(THREAT_PHASE, THREAT_PHASE_NONE);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10D)
                .add(Attributes.MOVEMENT_SPEED, 0.1f)
                .build();
    }

    @Override
    public int getScaleforGUI() {
        return 22;
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.HYSTRIX_CRISTATA, AppName.FIELD, "Hystricomorpha");
    }

    //this one is calculated based on head to body, not including tail as i couldnt find reliable full body size estimates
    @Override
    public float genVarSizeMultiplier() {
        return AnimaliaFunctionUtil.getScaleForSize(17, 86);
    }

    @Override
    public Item getBreedingItem() {
        return Items.WHEAT_SEEDS;
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ModTags.Items.FRUITS_SEEDS;
    }

    @Override
    public ActivityTime activityTime() {
        return ActivityTime.NOCTURNAL;
    }

    @Override
    public boolean isSoftActivity() {
        return true;
    }

    @Override
    public int getThreatPhase() { return this.entityData.get(THREAT_PHASE); }

    @Override
    public void setThreatPhase(int phase) { this.entityData.set(THREAT_PHASE, phase); }

    @Override
    public boolean canStartThreatening() {return !this.isAttached() && this.onGround() && !this.isEating();}


    @Override
    public AppName getApp() {
        return AppName.FIELD;
    }

    @Override
    public Component getTrivia() {
        return Component.translatable("trivia.animalia.hystrix_cristata");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.hystricidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.rodentia");
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

    /***
     * Idles ref
     * 0- head lowered, twitch
     * 1- head lowered shaking, twitch
     * 2- sniff ground, neg
     * 3- blink, twitch
     * 4- raise quills, twitch
     * threatStomp- random stomp during threat
     */
}
