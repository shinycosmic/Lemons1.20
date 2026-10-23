package net.lemon.animalia.entity.bases.helpers;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public interface ICanThreat {
    int THREAT_PHASE_NONE = 0;
    int THREAT_PHASE_DISPLAY = 1;
    int THREAT_PHASE_EXITING = 2;

    int getThreatPhase();

    void setThreatPhase(int phase);

    default boolean isThreatening() {
        return this.getThreatPhase() != THREAT_PHASE_NONE;
    }

    @Nullable
    default SoundEvent getThreatSound() {
        return null;
    }

    default boolean canStartThreatening() {
        return true;
    }

    default boolean canThreat(Player player) {
        return !((IBold) this).isBold() && !player.isCrouching();
    }

    default int getThreatCooldown() { return 200; }

    //use this to add mid threat actions like attacking.
    default void onThreatTick(LivingEntity threat) {
    }

    default void onThreatFlee(LivingEntity threat) {
        ((PathfinderMob) this).setLastHurtByMob(threat);
    }

    default void onThreatStart(LivingEntity threat) {
    }

    default boolean isThreatGone(LivingEntity threat, double threatRange) {
        return ((PathfinderMob) this).distanceTo(threat) > threatRange;
    }

    default boolean shouldEndThreat() {
        return false;
    }

    default boolean onThreatHurt(LivingEntity threat) {
        return true;
    }
}
