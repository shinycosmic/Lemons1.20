package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.ai.BottomDwellingGoal;
import net.lemon.animalia.entity.ai.FishFrySwimmingGoal;
import net.lemon.animalia.entity.aimove.BottomDwellingMoveHelperController;
import net.lemon.animalia.entity.bases.ActivityTime;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.spawning.SpawnBand;
import net.lemon.animalia.util.AnimaliaFunctionUtil;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class ToothfishEntity extends FishBase implements GeoEntity, Scannable {

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private static final float DISSOSTUCHUS_ELEGINOIDES_PIXEL = 37;

    public ToothfishEntity(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new BottomDwellingMoveHelperController(this);
    }

    @Override
    public boolean useSmoothControl() {
        return false;
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
    public SpawnBand spawnBand() {
        return SpawnBand.DEEP;
    }

    @Override
    public ItemStack getBucketItemStack() {
        if(this.getType() == ModEntities.CHILEANSEABASS.get()) {
            return new ItemStack(ModItems.CHILEANSEABASS_BUCKET.get());
        } else if(this.getType() == ModEntities.ELEGINOPS_MACLOVINUS.get()) {
            return new ItemStack(ModItems.ELEGINOPS_MACLOVINUS_BUCKET.get());
        } else if(this.getType() == ModEntities.PERCOPHIS_BRASILIENSIS.get()) {
            return new ItemStack(ModItems.PERCOPHIS_BRASILIENSIS_BUCKET.get());
        }
        return new ItemStack(Items.SALMON_BUCKET);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(5, new FishFrySwimmingGoal(this, 1.0D, 40));
        this.goalSelector.addGoal(7, new BottomDwellingGoal(this, 1.0D, 160, 8, 4));
        super.registerGoals();
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return super.getDefaultLootTable();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
        controllers.add(new AnimationController<>(this, "eat_controller", 0, this::eatPredicate));
    }

    private <T extends GeoAnimatable> PlayState eatPredicate(AnimationState<T> state) {
        if (this.isEating()) {
            state.getController().setAnimation(RawAnimation.begin().then("eat", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        if(this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then(this.getSwimAnim(), Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        if (!this.isInWater() && !this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("flop", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        animationState.getController().setAnimation(RawAnimation.begin().then(this.getSwimAnim(), Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    public String getSwimAnim() {
        return "swim";
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        this.setDeltaMovement(this.getDeltaMovement().scale(0.92D));
        super.travel(pTravelVector);
    }

    @Override
    public float genVarSizeMultiplier() {
        return AnimaliaFunctionUtil.getScaleForSize(DISSOSTUCHUS_ELEGINOIDES_PIXEL, this.genVarSize(70, 230, 80));
    }

    @Override
    public TagKey<Item> getFoodTag() {
        return ItemTags.FISHES;
    }

    /***
     * Eleginops maclovinus: Amphipod
     * Dissostichus eleginoides: Raw Fish
     * Percophis brasiliensis: Raw Fish
     */
    @Override
    public Item getBreedingItem() {
        if(this.getType() == ModEntities.ELEGINOPS_MACLOVINUS.get()) {
            return ModItems.AMPHIPOD.get();
        }
        return ModItems.RAW_FISH.get();
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if (reason != MobSpawnType.BUCKET) {
            this.setVarColor(1);
            this.setVarSizeMultiplier(this.genVarSizeMultiplier());
        }
        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public AppName getApp() {
        return AppName.FISH;
    }

    @Override
    public Component getTrivia() {
        if (this.getType() == ModEntities.CHILEANSEABASS.get()) {
            return Component.translatable("trivia.animalia.dissostichus_eleginoides");
        } else if (this.getType() == ModEntities.ELEGINOPS_MACLOVINUS.get()) {
            return Component.translatable("trivia.animalia.eleginops_maclovinus");
        } else if (this.getType() == ModEntities.PERCOPHIS_BRASILIENSIS.get()){
            return Component.translatable("trivia.animalia.percophis_brasiliensis");
        } else {
            return Component.translatable("debug.animalia.trivia");
        }
    }

    @Override
    public Component getFamily() {
        if (this.getType() == ModEntities.CHILEANSEABASS.get()) {
            return Component.translatable("family.animalia.nototheniidae");
        } else if (this.getType() == ModEntities.ELEGINOPS_MACLOVINUS.get()) {
            return Component.translatable("family.animalia.eleginopidae");
        } else if (this.getType() == ModEntities.PERCOPHIS_BRASILIENSIS.get()){
            return Component.translatable("family.animalia.percophidae");
        } else {
            return Component.translatable("debug.animalia.family");
        }
    }

    @Override
    public Component getOrder() {
        return Component.translatable("order.animalia.perciformes");
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.CHILEANSEABASS.get()) {
            return 22;
        } else if (this.getType() == ModEntities.ELEGINOPS_MACLOVINUS.get()) {
            return 42;
        } else if (this.getType() == ModEntities.PERCOPHIS_BRASILIENSIS.get()){
            return 84;
        } else {
            return Scannable.super.getScaleforGUI();
        }
    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        if(this.getType() == ModEntities.ELEGINOPS_MACLOVINUS.get()) {
            currScale *= (int) 2f;
        } else if(this.getType() == ModEntities.PERCOPHIS_BRASILIENSIS.get()) {
            currScale *= (int) 2f;
        }

        return currScale;
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.CHILEANSEABASS, Scannable.AppName.FISH, "Perciformes");
        HolonetEntities.register(ModEntities.ELEGINOPS_MACLOVINUS, Scannable.AppName.FISH, "Perciformes");
        HolonetEntities.register(ModEntities.PERCOPHIS_BRASILIENSIS, Scannable.AppName.FISH, "Perciformes");
    }
}
