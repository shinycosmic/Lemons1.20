package net.lemon.animalia.entity.bases.helpers;
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

    @Nullable
    default SoundEvent getThreatSound() {
        return null;
    }

    default boolean canStartThreatening() {
        return true;
    }

    default int getThreatCooldown() { return 200; }

    //use this to add mid threat actions like attacking.
    default void onThreatTick(LivingEntity threat) {
    }

    default void onThreatFlee(LivingEntity threat) {
        ((PathfinderMob) this).setLastHurtByMob(threat);
    }
}
