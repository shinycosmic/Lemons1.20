package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.ai.ThreatGoal;
import net.lemon.animalia.entity.bases.ActivityTime;
import net.lemon.animalia.entity.bases.BottomWalkerSwimmerBase;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.entity.bases.interfaces.ICanThreat;
import net.lemon.animalia.registry.ModEntities;
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
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class CrayfishEntity extends BottomWalkerSwimmerBase implements GeoEntity, Scannable, ICanThreat {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> THREAT_PHASE = SynchedEntityData.defineId(CrayfishEntity.class, EntityDataSerializers.INT);
    private static final int EXIT_ANIM_TICKS = 15;
    private static final int ATTACK_ANIM_TICKS = 20;
    private int attackCooldown;

    public CrayfishEntity(EntityType<? extends FishBase> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.getAvailableGoals().removeIf(g -> g.getGoal() instanceof AvoidEntityGoal);
        this.goalSelector.addGoal(2, new ThreatGoal(this, 5.0D, 200, EXIT_ANIM_TICKS, ThreatGoal.ThreatOutcome.FLEE));
    }

    @Override
    public String getScientificName() {
        return "";
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return null;
    }

    @Override
    public Item getBreedingItem() {
        return null;
    }

    @Override
    public ActivityTime activityTime() {
        return ActivityTime.NONE;
    }

    @Override
    public AppName getApp() {
        return AppName.FIELD;
    }

    @Override
    public Component getTrivia() {
        return null;
    }

    @Override
    public Component getFamily() {
        return null;
    }

    @Override
    public Component getOrder() {
        return null;
    }

    @Override
    public ItemStack getBucketItemStack() {
        return null;
    }

    //TODO
    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.PSEUDAPHRITIS_URVILLII.get()) {
            return 60;
        } else {
            return Scannable.super.getScaleforGUI();
        }
    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        return (int) (currScale * 1.4f);
    }

    //TODO
    public static void registerHolonet(){
//        HolonetEntities.register(ModEntities.PSEUDAPHRITIS_URVILLII, Scannable.AppName.FIELD, "Perciformes");
    }

    @Override
    public int getWalkTime() {
        return 4000 + random.nextInt(1000);
    }

    @Override
    public int getSwimTime() {
        return 500 + random.nextInt(1000);
    }

    @Override
    public float getSwimSpeed() {
        if(this.isWalking()) {
            return 1.8f;
        }
        return 0.6f;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean hasSwimToWalkTransition() {
        return false;
    }

    @Override
    public int getMaxHeadXRot() {
        return 1;
    }

    @Override
    public int getMaxHeadYRot() {
        return 1;
    }

    @Override
    public int getThreatPhase() {
        return 0;
    }

    @Override
    public void setThreatPhase(int phase) {

    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (reason != MobSpawnType.BUCKET) {
            this.setVarColor(1);
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }
}
