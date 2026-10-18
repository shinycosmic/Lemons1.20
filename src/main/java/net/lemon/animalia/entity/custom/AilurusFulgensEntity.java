package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.bases.AnimaliaLandBase;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.entity.bases.helpers.ICanThreat;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.util.AnimaliaFunctionUtil;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.BlockTags;
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
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class AilurusFulgensEntity extends AnimaliaLandBase implements GeoEntity, Scannable, ICanThreat {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> THREAT_PHASE = SynchedEntityData.defineId(AilurusFulgensEntity.class, EntityDataSerializers.INT);

    public AilurusFulgensEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(THREAT_PHASE, THREAT_PHASE_NONE);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 7D)
                .add(Attributes.MOVEMENT_SPEED, 0.1f)
                .build();
    }

    @Override
    public int getScaleforGUI() {
        return 22;
    }

    public static void registerHolonet(){
//        HolonetEntities.register(ModEntities.CROSSARCHUS_OBSCURUS, AppName.FIELD, "Feliformia");
    }

    @Override
    public float genVarSizeMultiplier() {
        return AnimaliaFunctionUtil.getScaleForSize(39, 115);
    }

    @Override
    public Item getBreedingItem() {
        return Items.BAMBOO;
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return null;
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
    public boolean canClimb() {
        return true;
    }

    @Override
    public boolean babyClimbEnabled() {
        return false;
    }

    @Override
    public boolean isClimbableBlock(BlockState state) {
        return state.is(BlockTags.LOGS);
    }

    @Override
    public int getThreatPhase() { return this.entityData.get(THREAT_PHASE); }

    @Override
    public void setThreatPhase(int phase) { this.entityData.set(THREAT_PHASE, phase); }

    @Override
    public boolean canStartThreatening() {return !this.isAttached() && this.onGround();}

    @Override
    public AppName getApp() {
        return AppName.FIELD;
    }

    @Override
    public Component getTrivia() {
        return Component.translatable("trivia.animalia.ailurus_fulgens");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.ailuridae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.carnivora");
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
     * 0- blink
     * 1- pant
     * 2- yawn
     * idleSit0- sniff
     * idleRest0- sniff
     * idleRest1- pant
     * dreamL- ear twitch left
     * dreamR- ear twitch right
     */

    /***
     * Eating ref
     * must be hand-fed, if hand fed (like vanilla panda) eat plays first (this is the pick up the item from your hand with its mouth)
     * then play eating, which the item you gave it is rendered in its hands as it eats.
     */

    /***
     * threat ref
     * two types: this animal should proximity panic away from any player that approaches it without crouching by default.
     * if it is bold -> play threat instead. once threat expires, initiate a 1 minecraft day cooldown. Bold red pandas can threat players but cannot panic from players
     */

    /***
     * climb ref
     * Climbing is normal pathfinding behavior
     * Red Panda does not climb during panics. it only climbs during normal pathfinding and should be allowed to pathfind up walls, letting it escape holes.
     */
}
