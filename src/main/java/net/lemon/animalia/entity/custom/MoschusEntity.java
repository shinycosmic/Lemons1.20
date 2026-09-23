package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.bases.AnimaliaLandBase;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.entity.bases.helpers.ICanThreat;
import net.lemon.animalia.registry.AnimaliaSound;
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
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class MoschusEntity extends AnimaliaLandBase implements GeoEntity, Scannable, ICanThreat {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final EntityDataAccessor<Integer> THREAT_PHASE = SynchedEntityData.defineId(MoschusEntity.class, EntityDataSerializers.INT);
    private LandPanicGoal landPanic;
    private boolean wasGrazing;
    private int barkCooldown;

    public MoschusEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(THREAT_PHASE, THREAT_PHASE_NONE);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 4D)
                .add(Attributes.MOVEMENT_SPEED, 0.1f)
                .build();
    }

    @Override
    public int getScaleforGUI() {
        return 22;
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.MOSCHUS_MOSCHIFERUS, AppName.FIELD, "Ruminantia");

    }

    @Override
    public float genVarSizeMultiplier() {
//        if (this.getType() == ModEntities.MOSCHUS_MOSCHIFERUS.get()) {
//            return AnimaliaFunctionUtil.getScaleForSize(22, 35);
//        }
        int desiredCm = this.getGender() == 0 ? 90 : 100;
        return AnimaliaFunctionUtil.getScaleForSize(28, desiredCm); //TODO placeholder
    }

    @Override
    public Item getBreedingItem() {
        return Items.GRASS;
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ItemTags.LEAVES;
    }

    @Override
    public ActivityTime activityTime() {
        return ActivityTime.NOCTURNAL;
    }

    @Override
    public AppName getApp() {
        return AppName.FIELD;
    }

    @Override
    public Component getTrivia() {
        return Component.translatable("trivia.animalia.moschus_moschiferus");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.moschidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.artiodactyla");
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


    /**
     * Idles ref:
     *  0- munch
     *  1- slight down
     *  2- more down
     *  3- threat second pose (threatEar)
     *  4- bark (WIP)
     */

    @Override
    public int getThreatPhase() {return this.entityData.get(THREAT_PHASE);}

    @Override
    public void setThreatPhase(int phase) { this.entityData.set(THREAT_PHASE, phase); }

    @Override
    public int getThreatCooldown() { return 60; }

    @Override
    public void onThreatFlee(LivingEntity threat) {
        this.landPanic.panicFrom(threat.position());
        if (this.getCurrTwitchIdle() >= 0) {
            this.setTwitchTicks(0);
            this.setCurrTwitchIdle(-1);
        }
    }

    @Override
    public void playerTouch(Player player) {
        if (!this.level().isClientSide && !player.isCreative() && !player.isSpectator()) {
            if (this.isAsleep()) {
                this.setSleepPhase(SLEEP_PHASE_NONE);
                this.setCurrentSleepIdle(-1);
            }
            this.landPanic.panicFrom(player.position());
        }
        super.playerTouch(player);
    }


    @Override
    public boolean isGrazableBlock(BlockState state) { return state.is(ModTags.Blocks.FORAGEABLE); }

    @Override
    public boolean canGraze() { return super.canGraze() && !this.isBaby(); }

    @Override
    public int getGrazeSearchHeight() { return 1; }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.wasGrazing && !this.isGrazing()) { //right after ending graze, play the munching twitch idle
                this.setCurrTwitchIdle(0);
                this.setTwitchTicks(20);
            }
            this.wasGrazing = this.isGrazing();
            if (this.barkCooldown > 0) {
                this.barkCooldown--;
            } else if (this.getThreatPhase() == THREAT_PHASE_DISPLAY && this.getNavigation().isDone()
                    && this.level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), 4.0D, true) != null) {
                this.setCurrTwitchIdle(5);
                this.setTwitchTicks(15);
                this.playSound(AnimaliaSound.MUNTIACUS_MUNTJAK_BARK.get());
                this.barkCooldown = 120 + this.random.nextInt(121);
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean result = super.hurt(source, amount);
        if (!this.level().isClientSide && result && this.isAlive()) {
            Vec3 from = source.getEntity() != null ? source.getEntity().position() : this.position();
            this.landPanic.panicFrom(from);
        }
        return result;
    }
}
