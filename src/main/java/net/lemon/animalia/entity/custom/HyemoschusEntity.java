package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.bases.SemiaquaticBase;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModTags;
import net.lemon.animalia.util.AnimaliaFunctionUtil;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class HyemoschusEntity extends SemiaquaticBase implements GeoEntity, Scannable {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public HyemoschusEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8D)
                .add(Attributes.MOVEMENT_SPEED, 0.1f)
                .build();
    }

    @Override
    public Item getBreedingItem() {
        return Items.APPLE;
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
    public AppName getApp() {
        return AppName.FIELD;
    }

    @Override
    public Component getTrivia() {
        return Component.translatable("trivia.animalia.hyemoschus_aquaticus");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.animalia.tragulidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.artiodactyla");
    }

    @Override
    public int getScaleforGUI() {
        return 22;

    }

    public static void registerHolonet(){
//        HolonetEntities.register(ModEntities.HYEMOSCHUS_AQUATICUS, AppName.FIELD, "Artiodactyla");
    }

    @Override
    public float genVarSizeMultiplier() {
//        if (this.getType() == ModEntities.HYEMOSCHUS_AQUATICUS.get()) {
//            return AnimaliaFunctionUtil.getScaleForSize(21, 85);
//        }
        return 1;
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


}
