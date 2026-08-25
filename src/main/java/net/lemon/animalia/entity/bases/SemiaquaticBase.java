package net.lemon.animalia.entity.bases;

import net.lemon.animalia.entity.ai.SchoolBoidGoal;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public abstract class SemiaquaticBase extends AnimaliaLandBase{
    protected SemiaquaticBase(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Player.class, 8.0F, 1.6D, 1.4D, EntitySelector.NO_SPECTATORS::test));
        this.goalSelector.getAvailableGoals().removeIf(
                g -> g.getGoal() instanceof FloatGoal
                        || g.getGoal() instanceof WaterAvoidingRandomStrollGoal
        );
        super.registerGoals();
    }


    static class SemiaquaticBreedGoal extends BreedGoal {
        private final SemiaquaticBase semiaquatic;

        SemiaquaticBreedGoal(SemiaquaticBase semiaquatic, double pSpeedModifier) {
            super(semiaquatic, pSpeedModifier);
            this.semiaquatic = semiaquatic;
        }

        public boolean canUse() {
            return super.canUse() && !this.semiaquatic.isPregnant();
        }

        protected void breed() {
            ServerPlayer serverplayer = this.animal.getLoveCause();
            if (serverplayer == null && this.partner.getLoveCause() != null) {
                serverplayer = this.partner.getLoveCause();
            }

            if (serverplayer != null) {
                serverplayer.awardStat(Stats.ANIMALS_BRED);
                CriteriaTriggers.BRED_ANIMALS.trigger(serverplayer, this.animal, this.partner, (AgeableMob)null);
            }

            this.semiaquatic.setPregnant(true);
            this.animal.setAge(6000);
            this.partner.setAge(6000);
            this.animal.resetLove();
            this.partner.resetLove();
            RandomSource randomsource = this.animal.getRandom();
            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                this.level.addFreshEntity(new ExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), randomsource.nextInt(7) + 1));
            }

        }
    }
}
