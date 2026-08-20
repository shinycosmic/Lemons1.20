package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.entity.bases.BottomWalkerSwimmerBase;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.ModTags;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Random;

public class CongolliEntity extends BottomWalkerSwimmerBase implements GeoEntity, Scannable {
    private static final EntityDataAccessor<Boolean> IDLE_SAND = SynchedEntityData.defineId(CongolliEntity.class, EntityDataSerializers.BOOLEAN);
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private int ambientTicks;
    private int idleSandLength = 30;
    private int sandTimer = idleSandLength;
    private int panicCooldown = 0;


    private final Random rand = new Random();


    public CongolliEntity(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.2f, 0.1f, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.getAvailableGoals().removeIf(
                g -> g.getGoal() instanceof BottomWalkerStrollGoal
        );
        this.goalSelector.addGoal(6, new BurstGoal(this));
        this.goalSelector.addGoal(6, new BurstPanicGoal(this));
    }

    private boolean isOnSeafloor() {
        return this.onGround();
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ModTags.Items.CRUSTACEAN;
    }

    @Override
    public Item getBreedingItem() {
        return ModItems.AMPHIPOD.get();
    }

    @Override
    public ActivityTime activityTime() {
        return ActivityTime.NONE;
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4D)
                .add(Attributes.MOVEMENT_SPEED, 0.4f)
                .build();
    }

    @Override
    public int getWalkTime() {
        return 2000 + random.nextInt(1000);
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
        return 0.4f;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (panicCooldown > 0) panicCooldown--;

        if(ambientTicks > 0) {
            ambientTicks = ambientTicks - rand.nextInt(3);
        }

        if(this.isWalking()) {
            this.getNavigation().stop();
        }

        //handle sand state
        if(!this.level().isClientSide) {
            if (ambientTicks <= 0 && !getIsIdleSand() && isWalking()) {
                sandTimer = idleSandLength;
                this.setIsIdleSand(true);
            }
            if (sandTimer > 0 && getIsIdleSand()) {
                sandTimer--;
            } else if(getIsIdleSand()){
                ambientTicks = rand.nextInt(1000)+300;
                this.setIsIdleSand(false);
            }
        }

    }

    @Override
    public void travel(Vec3 vec) {
        if (this.isWalking()) {
            super.travel(Vec3.ZERO);
            return;
        }
        super.travel(vec);
    }

    private void setIsIdleSand(boolean bool) {
        this.entityData.set(IDLE_SAND, bool);
    }

    private boolean getIsIdleSand() {
        return this.entityData.get(IDLE_SAND);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IDLE_SAND, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putBoolean("IdleSand", this.getIsIdleSand());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setIsIdleSand(pCompound.getBoolean("IdleSand"));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 10, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        AnimationController<T> controller = animationState.getController();

        if(this.isBaby()) {
            controller.setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (!this.isInWater() && !this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("flop", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (this.isWalking()) {
            if (this.isActuallyMoving()) {
                controller.setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            } else {
                if(this.getIsIdleSand()) {
                    controller.setAnimation(RawAnimation.begin().then("sand", Animation.LoopType.LOOP));
                    return PlayState.CONTINUE;
                }
                controller.setAnimation(RawAnimation.begin().then("alert", Animation.LoopType.LOOP));
                return PlayState.CONTINUE;
            }
        } else {
            // SWIMMING
            controller.setAnimation(RawAnimation.begin().then("swim", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (reason != MobSpawnType.BUCKET || dataTag == null || !dataTag.contains("BucketVarSize")) {
            this.setVarColor(1);
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        }
        this.ambientTicks = rand.nextInt(1000)+1000;
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public boolean isActuallyMoving() {
        Vec3 vel = getDeltaMovement();
        return vel.x * vel.x + vel.z * vel.z > 1.0E-6; // ignore vertical
    }

    public int getRestCooldown() {
        return 100;
    }

    public double getBurstPower() {
        return 0.17D;
    }

    public double getRandomUpBoost() {
        return this.getRandom().nextDouble();
    }

    private boolean isNearGround() {
        AABB box = this.getBoundingBox().move(0, -0.1, 0);
        return !this.level().noCollision(this, box);
    }

    private boolean isDirectionBlocked(double dx, double dz, CongolliEntity mob) {
        AABB box = mob.getBoundingBox().move(dx * 0.5, 0, dz * 0.5);
        return !mob.level().noCollision(mob, box);
    }

    public void burst(CongolliEntity mob, @Nullable Vec3 threatPos) {
        float baseYaw = mob.getYRot();
        float[] offsets = {
                0F,
                10F, -10F,
                25F, -25F,
                45F, -45F,
                70F, -70F,
                110F, -110F,
                180F
        };

        float bestAngle = baseYaw;
        double bestScore = Double.NEGATIVE_INFINITY;

        for (float offset : offsets) {
            float angle = baseYaw + offset;
            double rad = Math.toRadians(angle);

            double dx = -Math.sin(rad);
            double dz =  Math.cos(rad);

            Vec3 dir = new Vec3(dx, 0, dz);

            double score = 0;
            if (threatPos != null) {
                Vec3 away = mob.position().subtract(threatPos).normalize();
                score += dir.dot(away) * 3.0;
            }
            if (isDirectionBlocked(dx, dz, mob)) {
                score -= 8.0;
            }
            Vec3 forwardCheck = dir.scale(1.5);
            if (!isDirectionBlocked(forwardCheck.x, forwardCheck.z, mob)) {
                score += 2.0;
            }
            if (threatPos == null) {
                double forwardDot = dir.dot(new Vec3(-Math.sin(Math.toRadians(baseYaw)), 0, Math.cos(Math.toRadians(baseYaw))));
                if (forwardDot > 0) {
                    score += forwardDot * 2.0;
                } else {
                    score -= 1.0;
                }
            }
            score += (mob.getRandom().nextFloat() - 0.5) * 0.2;

            if (score > bestScore) {
                bestScore = score;
                bestAngle = angle;
            }
        }
        double rad = Math.toRadians(bestAngle);
        double strength = mob.getBurstPower();

        double dx = -Math.sin(rad) * strength;
        double dz = Math.cos(rad) * strength;
        double dy = mob.getRandomUpBoost();

        mob.setDeltaMovement(dx, dy, dz);
        mob.hasImpulse = true;

        mob.setYRot(bestAngle);
        mob.setYHeadRot(bestAngle);
        mob.yRotO = bestAngle;
        mob.yHeadRotO = bestAngle;
    }

    @Override
    public AppName getApp() {
        return AppName.FISH;
    }

    @Override
    public Component getTrivia() {
        return Component.translatable("trivia.animalia.pseudaphritis_urvillii");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.pseudaphritidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.perciformes");
    }

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

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.PSEUDAPHRITIS_URVILLII, Scannable.AppName.FISH, "Perciformes");
    }

    //Quick Hops instead of regular swimming
    static class BurstGoal extends Goal {
        private final CongolliEntity mob;
        private int cooldown;
        private boolean stopped;

        public BurstGoal(CongolliEntity mob) {
            this.mob = mob;
            this.cooldown = mob.getRestCooldown();
            this.stopped = true;
        }

        @Override
        public boolean canUse() {
            return mob.isWalking() && mob.isInWater() && mob.isNearGround();
        }

        @Override
        public boolean canContinueToUse() {
            return mob.isWalking() && mob.isInWater();
        }

        @Override
        public void stop() {
            this.stopped = true;
            this.cooldown = mob.getRestCooldown();
        }

        @Override
        public void tick() {
            if(stopped) {
                if(--cooldown <= 0 && mob.isNearGround() && mob.isWalking() && !mob.isActuallyMoving()) {
                    mob.burst(mob, null);
                    this.stopped = false;
                }
            } else  {
                if(mob.isNearGround()) {
                    stopped = true;
                    cooldown = mob.getRestCooldown();
                }
            }
        }
    }

    static class BurstPanicGoal extends Goal {
        private final CongolliEntity mob;
        private int panicTime;
        protected boolean isRunning;

        public BurstPanicGoal(CongolliEntity mob) {
            this.mob = mob;
        }

        @Override
        public boolean canUse() {
            if (mob.panicCooldown > 0) return false;
            if (!this.mob.isWalking()) return false;
            return shouldPanic();
        }

        protected boolean shouldPanic() {
            if (mob.getLastHurtByMob() != null || mob.isFreezing() || mob.isOnFire()) {
                return true;
            }
            AABB touchBox = mob.getBoundingBox().inflate(0.3);
            for (Player player : mob.level().players()) {
                if (!player.isSpectator() && player.getBoundingBox().intersects(touchBox)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void start() {
            this.isRunning = true;
            this.panicTime = 20 + mob.getRandom().nextInt(20);
            mob.panicCooldown = 80;

            triggerBurst();
        }

        @Override
        public void stop() {
            this.isRunning = false;
            this.panicTime = 0;
        }

        @Override
        public boolean canContinueToUse() {
            return this.isRunning && panicTime > 0 && mob.isInWater();
        }

        @Override
        public void tick() {
            panicTime--;
        }

        private void triggerBurst() {
            Player player = mob.level().getNearestPlayer(mob, 4.0);
            if(player != null) {
                mob.burst(mob, player.position());
            } else {
                mob.burst(mob, null);
            }
        }
    }
}
