package net.lemon.animalia.entity.bases.interfaces;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.Nullable;

public interface ICanThreat {
    int THREAT_PHASE_NONE = 0;
    int THREAT_PHASE_DISPLAY = 1;
    int THREAT_PHASE_LEAVING = 2;

    int getThreatPhase();

    void setThreatPhase(int phase);

    default boolean isThreatening() {
        return this.getThreatPhase() != THREAT_PHASE_NONE;
    }

    /** Override to play a sound when the display starts. Null is silent. */
    @Nullable
    default SoundEvent getThreatSound() {
        return null;
    }

    /** Override to gate the display, e.g. !isHiding(), onGround(), isWalking(). */
    default boolean canStartThreatening() {
        return true;
    }

    /**
     * Called every tick during the DISPLAY phase with the current threat.
     * Default does nothing. Override for contact reactions, e.g. a crayfish
     * pinching a threat that walks into its hitbox mid-display.
     */
    default void onThreatTick(LivingEntity threat) {
    }

    /**
     * Called when the FLEE outcome fires. Default marks the threat as the last
     * attacker so an existing PanicGoal picks it up. Override for mobs with
     * retaliation goals (HurtByTargetGoal) or custom flee behavior.
     */
    default void onThreatFlee(LivingEntity threat) {
        ((PathfinderMob) this).setLastHurtByMob(threat);
    }
}
