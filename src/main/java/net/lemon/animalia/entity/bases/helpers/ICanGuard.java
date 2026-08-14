package net.lemon.animalia.entity.bases.helpers;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;

public interface ICanGuard {
    int GUARD_PHASE_NONE = 0;
    int GUARD_PHASE_ENTERING = 1;
    int GUARD_PHASE_GUARDING = 2;
    int GUARD_PHASE_EXITING = 3;

    enum GuardActivation {
        PROXIMITY, ATTACK, BOTH
    }

    int getGuardPhase();
    void setGuardPhase(int phase);

    GuardActivation getGuardActivation();

    default boolean isGuarding() {
        return this.getGuardPhase() != GUARD_PHASE_NONE;
    }

    default boolean wantsToGuard() {
        return false;
    }

    default void urgeGuard(int ticks) {
    }

    default void clearGuardUrge() {
    }

    default int getGuardReAttackWindow() {
        return 100;
    }

    default int getGuardInLength() {
        return 0;
    }

    default int getGuardOutLength() {
        return 0;
    }

    default float getGuardedDamageMultiplier() {
        return 0.1f;
    }

    default float getGuardTransitionDamageMultiplier() {
        return 0.6f;
    }

    default boolean guardProtectsFrom(DamageSource source) {
        return source.getEntity() != null;
    }

    default boolean guardTriggersFrom(DamageSource source) {
        return source.getEntity() != null && !source.is(DamageTypeTags.IS_FIRE);
    }

    default float getGuardDamageMultiplier(DamageSource source) {
        if (!this.guardProtectsFrom(source)) {
            return 1.0f;
        }
        int phase = this.getGuardPhase();
        if (phase == GUARD_PHASE_GUARDING) {
            return this.getGuardedDamageMultiplier();
        }
        if (phase == GUARD_PHASE_ENTERING || phase == GUARD_PHASE_EXITING) {
            return this.getGuardTransitionDamageMultiplier();
        }
        return 1.0f;
    }

    @Nullable
    default SoundEvent getGuardSound() {
        return null;
    }

    default boolean isHiding() {
        return false;
    }

    default boolean canStartGuarding() {
        PathfinderMob mob = (PathfinderMob) this;
        return !this.isHiding() && mob.onGround() && !mob.isInWater();
    }

    default void onGuardTick(LivingEntity threat) {
    }
}