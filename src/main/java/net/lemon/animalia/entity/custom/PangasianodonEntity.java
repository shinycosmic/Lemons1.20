package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.ai.GrazeGoal;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.registry.ModBlocks;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class PangasianodonEntity extends FishBase implements GeoEntity, Scannable {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public PangasianodonEntity(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public float getSwimSpeed() {
        if(this.isBaby()) {
            return 1.2f;
        }
        return 1.2f;
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12D)
                .add(Attributes.MOVEMENT_SPEED, 0.4f)
                .build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(6, new GrazeGoal<>(this, 1.0D));
        super.registerGoals();
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ModTags.Items.AQUATIC_PLANT;
    }

    @Override
    public Item getBreedingItem() {
        return ModBlocks.ALGAE_MAT.get().asItem();
    }

    @Override
    public ActivityTime activityTime() {
        return ActivityTime.NONE;
    }

    @Override
    public AppName getApp() {
        return AppName.FISH;
    }

    @Override
    public Component getTrivia() {
        return Component.translatable("trivia.wildlifeexplorers.pangasianodon_gigas");
    }

    @Override
    public Component getFamily() {
        return Component.translatable("family.wildlifeexplorers.pangasiidae");
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.wildlifeexplorers.siluriformes");
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.PANGASIANODON_GIGAS.get()) {
            return 18;
        } else {
            return Scannable.super.getScaleforGUI();
        }
    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        return (int) (currScale * 0.65f);
    }

    @Override
    public int getXOffsetForGUI() {
        int offset = 0;
        if(this.getType() == ModEntities.PANGASIANODON_GIGAS.get()) {
            offset = -5;
        }
        return offset;
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.PANGASIANODON_GIGAS, Scannable.AppName.FISH, "Siluriformes");
    }

    @Override
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.PANGASIANODON_GIGAS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(49, this.genVarSize(230, 300, 280));
        }
        return 1;
    }

    @Override
    public double getGrazeReachSqr() {
        return 0.4d;
    }

    @Override
    public int getGrazeLength() {
        return 100 + this.random.nextInt(200);
    }

    @Override
    public boolean isGrazableBlock(BlockState state) {
        return state.is(ModBlocks.ALGAE_MAT.get());
    }

    @Override
    public boolean shouldJumpOnFlop() {
        return false;
    }

    @Override
    protected <T extends GeoAnimatable> boolean specialAnim(AnimationController<T> controller) {
        if (this.isGrazing()) {
            controller.setAnimation(RawAnimation.begin().then("graze", Animation.LoopType.LOOP));
            return true;
        }
        return false;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }


}
