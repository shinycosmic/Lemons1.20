package net.lemon.animalia.entity.custom;

import com.google.common.collect.ImmutableMap;
import net.lemon.animalia.entity.ai.BottomDwellingGoal;
import net.lemon.animalia.entity.ai.FishFrySwimmingGoal;
import net.lemon.animalia.entity.ai.PelagicRandomSwimGoal;
import net.lemon.animalia.entity.bases.AnimaliaBreedableWater;
import net.lemon.animalia.item.BettaFishEggItem;
import net.lemon.animalia.item.FishEggItem;
import net.minecraft.network.chat.Component;
import net.lemon.animalia.entity.bases.ActivityTime;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.entity.custom.traits.BettaTraits;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.util.ColorUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

import java.util.*;
import java.util.Arrays;
import java.util.Map;

public class BettaEntity extends FishBase implements GeoEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    public BettaTraits traits;
    private static final EntityDataAccessor<Integer> PRIMARY_COLOR = SynchedEntityData.defineId(BettaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> SECONDARY_COLOR = SynchedEntityData.defineId(BettaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> THIRD_COLOR = SynchedEntityData.defineId(BettaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PATTERN_PRESET = SynchedEntityData.defineId(BettaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_BUTTERFLY = SynchedEntityData.defineId(BettaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BODY_PRESET = SynchedEntityData.defineId(BettaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> CAUDAL_PRESET = SynchedEntityData.defineId(BettaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DORSAL_PRESET = SynchedEntityData.defineId(BettaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> ANAL_PRESET = SynchedEntityData.defineId(BettaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PELVIC_PRESET = SynchedEntityData.defineId(BettaEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> SPECIAL_VARIANT = SynchedEntityData.defineId(BettaEntity.class, EntityDataSerializers.BOOLEAN);
    private static final Map<String, String> BETTA_SPECIALS = ImmutableMap.<String, String>builder()
            .put("dragon_tall_hm_tall_tall_dragon_white_black_x_y", "silver_dragon")
            .put("dragon_tall_hm_tall_tall_dragon_white_yellow_x_y", "gold_dragon")
            .put("solid_tall_hm_tall_tall_bicolor_pink_white_x_n", "cotton_candy")
            .put("dragon_medium_hm_medium_medium_dragon_turquoise_black_x_y", "emerald_alien")
            .put("dragon_medium_hm_medium_medium_dragon_brown_black_x_y", "copper_alien")
            .put("dragon_medium_hm_medium_medium_dragon_blue_black_x_y", "blue_alien")
            .put("marble_medium_hm_medium_short_marble_red_blue_white_y", "koi")
            .put("solid_tall_hm_tall_medium_bicolor_black_yellow_x_n", "black_mustard_gas")
            .put("solid_tall_hm_tall_medium_bicolor_blue_yellow_x_n", "blue_mustard_gas")
            .put("marble_medium_hm_short_short_marble_black_turquoise_black_n", "green_galaxy")
            .put("solid_tall_rose_tall_tall_butterfly_brown_yellow_x_y", "chocolate_rostail")
            .put("solid_tall_crown_tall_tall_solid_black_blue_x_n", "black_orchid")
            .put("marble_medium_hm_medium_short_marble_black_white_black_n", "samurai")
            .put("marble_medium_hm_medium_short_marble_black_white_red_y", "vampire_samurai")
            .put("marble_tall_hm_tall_tall_marble_blue_orange_red_y", "fancy")
            .put("marble_tall_hm_tall_tall_marble_blue_yellow_red_y", "fancy_gold_dust")
            .put("dragon_tall_hm_tall_tall_dragon_blue_orange_red_y", "fancy_gold_dust")
            .put("marble_tall_hm_tall_tall_marble_blue_black_blue_n", "avatar")
            .put("marble_tall_hm_tall_tall_marble_blue_black_red_y", "avatar_nebula")
            .put("marble_tall_hm_tall_tall_marble_blue_black_red_n", "avatar_fancy")
            .put("marble_medium_hm_medium_short_marble_orange_yellow_black_y", "tiger_koi")
            .put("marble_medium_spade_short_short_marble_blue_white_blue_y", "coelacanth").build();


    public BettaEntity(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
    }

    @Override
    public boolean useSmoothControl() {
        return true;
    }

    @Override
    public String getScientificName() {
        return "Betta splendens";
    }

    @Override
    public ActivityTime activityTime() {
        return ActivityTime.NONE;
    }

    public static AttributeSupplier setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 2D)
                .add(Attributes.MOVEMENT_SPEED, 1.25f)
                .build();
    }

    @Override
    public ItemStack getBucketItemStack() {
        return new ItemStack(ModItems.BETTA_SPLENDENS_BUCKET.get());
    }

    @Override
    protected ResourceLocation getDefaultLootTable() {
        return super.getDefaultLootTable();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        if(!this.isInWater()) {
            return PlayState.CONTINUE;
        } else if(this.isBaby()) {
            animationState.getController().setAnimation(RawAnimation.begin().then("betta.swim", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        animationState.getController().setAnimation(RawAnimation.begin().then(this.getSwimAnim(), Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    public String getSwimAnim() {
        return "betta.swim";
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
    public TagKey<Item> getFoodTag() {
        return ItemTags.FISHES;
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return stack.is(ModItems.FISH_FOOD.get());
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        if (stack.getItem() instanceof SpawnEggItem egg &&
                egg.getType(stack.getTag()) == this.getType()) {
            return InteractionResult.FAIL;
        }
        return super.mobInteract(pPlayer, pHand);
    }

    @Override
    protected void onOffspringSpawnedFromEgg(Player pPlayer, Mob pChild) {
        super.onOffspringSpawnedFromEgg(pPlayer, pChild);
    }


    public ColorUtil getPrimaryColor() {
        return ColorUtil.fromId(this.entityData.get(PRIMARY_COLOR));
    }

    public ColorUtil getSecondaryColor() {
        return ColorUtil.fromId(this.entityData.get(SECONDARY_COLOR));
    }

    public ColorUtil getThirdColor() {
        return ColorUtil.fromId(this.entityData.get(THIRD_COLOR));
    }

    public BettaTraits.PatternPreset getPatternPreset() {
        return BettaTraits.PatternPreset.fromId(this.entityData.get(PATTERN_PRESET));
    }

    public BettaTraits.CaudalPreset getCaudalPreset() {
        return BettaTraits.CaudalPreset.fromId(this.entityData.get(CAUDAL_PRESET));
    }

    public BettaTraits.BodyPreset getBodyPreset() {
        return BettaTraits.BodyPreset.fromId(this.entityData.get(BODY_PRESET));
    }

    public BettaTraits.DorsalPreset getDorsalPreset() {
        return BettaTraits.DorsalPreset.fromId(this.entityData.get(DORSAL_PRESET));
    }

    public BettaTraits.AnalPreset getAnalPreset() {
        return BettaTraits.AnalPreset.fromId(this.entityData.get(ANAL_PRESET));
    }

    public BettaTraits.PelvicPreset getPelvicPreset() {
        return BettaTraits.PelvicPreset.fromId(this.entityData.get(PELVIC_PRESET));
    }

    public boolean isButterfly() {
        return this.entityData.get(IS_BUTTERFLY);
    }

    public boolean isSpecialVariant() {
        return this.entityData.get(SPECIAL_VARIANT);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.entityData.set(PRIMARY_COLOR, pCompound.getInt("PrimaryColor"));
        this.entityData.set(SECONDARY_COLOR, pCompound.getInt("SecondaryColor"));
        this.entityData.set(THIRD_COLOR, pCompound.getInt("ThirdColor"));
        this.entityData.set(PATTERN_PRESET, pCompound.getInt("PatternPreset"));
        this.entityData.set(IS_BUTTERFLY, pCompound.getBoolean("IsButterfly"));
        this.entityData.set(BODY_PRESET, pCompound.getInt("BodyPreset"));
        this.entityData.set(CAUDAL_PRESET, pCompound.getInt("CaudalPreset"));
        this.entityData.set(DORSAL_PRESET, pCompound.getInt("DorsalPreset"));
        this.entityData.set(ANAL_PRESET, pCompound.getInt("AnalPreset"));
        this.entityData.set(PELVIC_PRESET, pCompound.getInt("PelvicPreset"));
        this.entityData.set(SPECIAL_VARIANT, pCompound.getBoolean("SpecialVariant"));

        buildTraits();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putInt("PrimaryColor", this.entityData.get(PRIMARY_COLOR));
        pCompound.putInt("SecondaryColor", this.entityData.get(SECONDARY_COLOR));
        pCompound.putInt("ThirdColor", this.entityData.get(THIRD_COLOR));
        pCompound.putInt("PatternPreset", this.entityData.get(PATTERN_PRESET));
        pCompound.putBoolean("IsButterfly", this.entityData.get(IS_BUTTERFLY));
        pCompound.putInt("BodyPreset", this.entityData.get(BODY_PRESET));
        pCompound.putInt("CaudalPreset", this.entityData.get(CAUDAL_PRESET));
        pCompound.putInt("DorsalPreset", this.entityData.get(DORSAL_PRESET));
        pCompound.putInt("AnalPreset", this.entityData.get(ANAL_PRESET));
        pCompound.putInt("PelvicPreset", this.entityData.get(PELVIC_PRESET));
        pCompound.putBoolean("SpecialVariant", this.entityData.get(SPECIAL_VARIANT));
        super.addAdditionalSaveData(pCompound);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PRIMARY_COLOR, 0);
        this.entityData.define(SECONDARY_COLOR, 0);
        this.entityData.define(THIRD_COLOR, 0);
        this.entityData.define(PATTERN_PRESET, 0);
        this.entityData.define(IS_BUTTERFLY, false);
        this.entityData.define(BODY_PRESET, 0);
        this.entityData.define(CAUDAL_PRESET, 0);
        this.entityData.define(DORSAL_PRESET, 0);
        this.entityData.define(ANAL_PRESET, 0);
        this.entityData.define(PELVIC_PRESET, 0);
        this.entityData.define(SPECIAL_VARIANT, false);
    }

    @Override
    public void saveToBucketTag(ItemStack stack) {
        CompoundTag compoundTag = stack.getOrCreateTag();
        compoundTag.putInt("PrimaryColor", this.entityData.get(PRIMARY_COLOR));
        compoundTag.putInt("SecondaryColor", this.entityData.get(SECONDARY_COLOR));
        compoundTag.putInt("ThirdColor", this.entityData.get(THIRD_COLOR));
        compoundTag.putInt("PatternPreset", this.entityData.get(PATTERN_PRESET));
        compoundTag.putBoolean("IsButterfly", this.entityData.get(IS_BUTTERFLY));
        compoundTag.putInt("BodyPreset", this.entityData.get(BODY_PRESET));
        compoundTag.putInt("CaudalPreset", this.entityData.get(CAUDAL_PRESET));
        compoundTag.putInt("DorsalPreset", this.entityData.get(DORSAL_PRESET));
        compoundTag.putInt("AnalPreset", this.entityData.get(ANAL_PRESET));
        compoundTag.putInt("PelvicPreset", this.entityData.get(PELVIC_PRESET));
        compoundTag.putBoolean("SpecialVariant", this.entityData.get(SPECIAL_VARIANT));
        compoundTag.putString("BucketBettaName", this.isSpecialVariant() ? this.traits.specialTexture : "n");
        super.saveToBucketTag(stack);
    }

    @Override
    public void onSyncedDataUpdated(List<SynchedEntityData.DataValue<?>> pDataValues) {
        super.onSyncedDataUpdated(pDataValues);
    }

    @Override
    public void loadFromBucketTag(CompoundTag pTag) {
        super.loadFromBucketTag(pTag);
        if(pTag != null) {
            if(pTag.contains("PrimaryColor")) {
                this.entityData.set(PRIMARY_COLOR, pTag.getInt("PrimaryColor"));
            }
            if(pTag.contains("SecondaryColor")) {
                this.entityData.set(SECONDARY_COLOR, pTag.getInt("SecondaryColor"));
            }
            if(pTag.contains("ThirdColor")) {
                this.entityData.set(THIRD_COLOR, pTag.getInt("ThirdColor"));
            }
            if(pTag.contains("PatternPreset")){
                this.entityData.set(PATTERN_PRESET, pTag.getInt("PatternPreset"));
            }
            if(pTag.contains("IsButterfly")){
                this.entityData.set(IS_BUTTERFLY, pTag.getBoolean("IsButterfly"));
            }
            if(pTag.contains("BodyPreset")){
                this.entityData.set(BODY_PRESET, pTag.getInt("BodyPreset"));
            }
            if(pTag.contains("CaudalPreset")){
                this.entityData.set(CAUDAL_PRESET, pTag.getInt("CaudalPreset"));
            }
            if(pTag.contains("DorsalPreset")){
                this.entityData.set(DORSAL_PRESET, pTag.getInt("DorsalPreset"));
            }
            if(pTag.contains("AnalPreset")){
                this.entityData.set(ANAL_PRESET, pTag.getInt("AnalPreset"));
            }
            if(pTag.contains("PelvicPreset")){
                this.entityData.set(PELVIC_PRESET, pTag.getInt("PelvicPreset"));
            }
            if(pTag.contains("SpecialVariant")){
                this.entityData.set(SPECIAL_VARIANT, pTag.getBoolean("SpecialVariant"));
            }
        }
        this.buildTraits();
    }

    //TODO DEBUG special variants
    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag dataTag) {
        if(reason == MobSpawnType.SPAWN_EGG) {
            buildTraitsRandom();
//            buildTraitsSpecial();
        } else if(reason != MobSpawnType.BUCKET){
            buildTraitsWild();
        }

        return super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
    }

    @Override
    public Component getDisplayName() {
        return resolveName(this.traits.patternPreset, this.traits.primaryColor, this.traits.secondaryColor, this.traits.caudalPreset, this.traits.specialTexture, this.traits.isSpecialVariant);
    }

    //GENE INHERITANCE WOOOO!!!

    /**
     * Resolves attribute rules and builds the traits object.
     * Also resolves special texture checks.
     */
    public void buildTraits() {
        resolveAttributes();

        this.traits = new BettaTraits(
                getPrimaryColor(), getSecondaryColor(),
                getThirdColor(), getPatternPreset(),
                this.isButterfly(), getBodyPreset(),
                getDorsalPreset(), getCaudalPreset(),
                getAnalPreset(), getPelvicPreset()
        );

        this.traits.specialTexture = checkSpecialVariant(this.traits);
        if(this.traits.specialTexture != null) {
            this.traits.isSpecialVariant = true;
            this.entityData.set(SPECIAL_VARIANT, true);
        } else {
            this.entityData.set(SPECIAL_VARIANT, false);
        }

    }

    public BettaTraits getTraitsClient() {
        this.traits = new BettaTraits(
                getPrimaryColor(), getSecondaryColor(),
                getThirdColor(), getPatternPreset(),
                this.isButterfly(), getBodyPreset(),
                getDorsalPreset(), getCaudalPreset(),
                getAnalPreset(), getPelvicPreset()
        );

        this.traits.specialTexture = checkSpecialVariant(this.traits);
        if(this.traits.specialTexture != null) {
            this.traits.isSpecialVariant = true;
        }
        return this.traits;
    }

    /**
     * For spawn egg spawns
     */
    public void buildTraitsRandom(){
        this.entityData.set(PATTERN_PRESET, BettaTraits.PatternPreset.values()[(int)(Math.random() * BettaTraits.PatternPreset.values().length)].getId());
        this.entityData.set(BODY_PRESET, BettaTraits.BodyPreset.values()[(int)(Math.random() * BettaTraits.BodyPreset.values().length)].getId());
        this.entityData.set(DORSAL_PRESET, BettaTraits.DorsalPreset.values()[(int)(Math.random() * BettaTraits.DorsalPreset.values().length)].getId());
        this.entityData.set(CAUDAL_PRESET, BettaTraits.CaudalPreset.values()[(int)(Math.random() * BettaTraits.CaudalPreset.values().length)].getId());
        this.entityData.set(ANAL_PRESET, BettaTraits.AnalPreset.values()[(int)(Math.random() * BettaTraits.AnalPreset.values().length)].getId());
        this.entityData.set(PELVIC_PRESET, BettaTraits.PelvicPreset.values()[(int)(Math.random() * BettaTraits.PelvicPreset.values().length)].getId());
        this.entityData.set(PRIMARY_COLOR, ColorUtil.randomNonNone(this.random).getId());
        this.entityData.set(SECONDARY_COLOR, ColorUtil.randomNonNone(this.random).getId());
        this.entityData.set(THIRD_COLOR, ColorUtil.randomNonNone(this.random).getId());
        this.entityData.set(IS_BUTTERFLY, Math.random() < 0.5);

        buildTraits();
    }

    /**
     * for natural spawning
     */
    public void buildTraitsWild(){
        ColorUtil[] allowed = {ColorUtil.RED, ColorUtil.BLUE, ColorUtil.GREEN};
        this.entityData.set(PATTERN_PRESET, BettaTraits.PatternPreset.BUTTERFLY.getId());
        this.entityData.set(BODY_PRESET, BettaTraits.BodyPreset.SOLID.getId());
        this.entityData.set(DORSAL_PRESET, BettaTraits.DorsalPreset.WILD.getId());
        this.entityData.set(CAUDAL_PRESET, BettaTraits.CaudalPreset.PLAKAT.getId());
        this.entityData.set(ANAL_PRESET, BettaTraits.AnalPreset.WILD.getId());
        this.entityData.set(PELVIC_PRESET, BettaTraits.PelvicPreset.SHORT.getId());
        this.entityData.set(PRIMARY_COLOR, ColorUtil.BROWN.getId());
        this.entityData.set(SECONDARY_COLOR, allowed[(int)(Math.random() * allowed.length)].getId());
        this.entityData.set(THIRD_COLOR, ColorUtil.NONE.getId());
        this.entityData.set(IS_BUTTERFLY, false);
        this.entityData.set(SPECIAL_VARIANT, false);

        buildTraits();
    }
    /**
     * for Testing special variants
     */
    public void buildTraitsSpecial(){
        ColorUtil[] allowed = {ColorUtil.ORANGE, ColorUtil.BLACK, ColorUtil.YELLOW};
        this.entityData.set(PATTERN_PRESET, BettaTraits.PatternPreset.MARBLE.getId());
        this.entityData.set(BODY_PRESET, BettaTraits.BodyPreset.MARBLE.getId());
        this.entityData.set(DORSAL_PRESET, BettaTraits.DorsalPreset.TALL.getId());
        this.entityData.set(CAUDAL_PRESET, BettaTraits.CaudalPreset.HM.getId());
        this.entityData.set(ANAL_PRESET, BettaTraits.AnalPreset.TALL.getId());
        this.entityData.set(PELVIC_PRESET, BettaTraits.PelvicPreset.TALL.getId());
        this.entityData.set(PRIMARY_COLOR, ColorUtil.BLUE.getId());
        this.entityData.set(SECONDARY_COLOR, allowed[(int)(Math.random() * allowed.length)].getId());
        this.entityData.set(THIRD_COLOR, ColorUtil.RED.getId());
        this.entityData.set(IS_BUTTERFLY, true);

        buildTraits();
    }

    public static Component resolveName(BettaTraits.PatternPreset pp, ColorUtil pc, ColorUtil sc, BettaTraits.CaudalPreset cc, String specialTexture, boolean isSpecial) {
        if (isSpecial) {
            return Component.translatable("entity.animalia.betta_splendens."+specialTexture);
        }

        Component primaryColor = Component.translatable("util.animalia.color."+pc.name().toLowerCase());
        Component secondaryColor = Component.translatable("util.animalia.color."+sc.name().toLowerCase());
        Component patternPreset = Component.translatable("entity.animalia.betta_splendens_preset."+pp.name().toLowerCase());
        Component caudalPreset = Component.translatable("entity.animalia.betta_splendens_preset."+cc.name().toLowerCase());

        switch(pp) {
            case SOLID:
                return Component.translatable("entity.animalia.betta_splendens.twoComp", primaryColor, caudalPreset);
            case DRAGON:
                return Component.translatable("entity.animalia.betta_splendens.threeComp", caudalPreset, primaryColor, patternPreset);
            case MARBLE:
                return Component.translatable("entity.animalia.betta_splendens.threeComp", caudalPreset, primaryColor, patternPreset);
            case BICOLOR:
                return Component.translatable("entity.animalia.betta_splendens.threeComp", caudalPreset, primaryColor, secondaryColor);
            case PIEBALD:
                return Component.translatable("entity.animalia.betta_splendens.twoComp", caudalPreset, patternPreset);
            case BUTTERFLY:
                return Component.translatable("entity.animalia.betta_splendens.fourComp", caudalPreset, primaryColor, secondaryColor, patternPreset);
            case CAMBODIAN:
                return Component.translatable("entity.animalia.betta_splendens.twoComp", caudalPreset, patternPreset);
            case MULTICOLOR: default:
                return Component.translatable("entity.animalia.betta_splendens.twoComp", caudalPreset, patternPreset);
        }
    }

    /**
     * this method resolves attributes and enforces certain combination rules
     * This audits faulty body/butterfly presets
     * Overrides happen here.
     */
    public void resolveAttributes() {
        BettaTraits.PatternPreset pattern = BettaTraits.PatternPreset.fromId(this.entityData.get(PATTERN_PRESET));
        BettaTraits.BodyPreset body = BettaTraits.BodyPreset.fromId(this.entityData.get(BODY_PRESET));
        ColorUtil primaryColor = ColorUtil.fromId(this.entityData.get(PRIMARY_COLOR));
        ColorUtil thirdColor = ColorUtil.fromId(this.entityData.get(THIRD_COLOR));
        boolean isButterflyCheck = this.entityData.get(IS_BUTTERFLY);

        switch (pattern){
            case SOLID, BICOLOR:
                body = BettaTraits.BodyPreset.SOLID;
                isButterflyCheck = false;
                break;
            case MULTICOLOR:
                body = BettaTraits.BodyPreset.MARBLE;
                isButterflyCheck = true;
                if(thirdColor == ColorUtil.NONE) thirdColor = primaryColor;
                break;
            case CAMBODIAN:
                body = BettaTraits.BodyPreset.SOLID;
                primaryColor = ColorUtil.WHITE;
                break;
            case MARBLE:
                body = BettaTraits.BodyPreset.MARBLE;
                if(thirdColor == ColorUtil.NONE) thirdColor = primaryColor;
                break;
            case BUTTERFLY:
                body = BettaTraits.BodyPreset.SOLID;
                isButterflyCheck = true;
                break;
            case PIEBALD:
                body = BettaTraits.BodyPreset.PIEBALD;
                primaryColor = ColorUtil.WHITE;
                break;
            case DRAGON:
                body = BettaTraits.BodyPreset.DRAGON;
                break;
        }

        this.entityData.set(BODY_PRESET, body.getId());
        this.entityData.set(IS_BUTTERFLY, isButterflyCheck);
        this.entityData.set(PRIMARY_COLOR, primaryColor.getId());
        this.entityData.set(THIRD_COLOR, thirdColor.getId());
    }


    /***
     * Breeding helper method, takes both parent's traits objects and randomly decides what to pass on before calling build traits
     * we can pass on any of the following: Pattern, Colors, Fins. IsButterfly resolved here, Body resolved in resolveAttributes()
     * job here is to audit color and inheritance rules
     */
    public void passGenes(ServerLevel level, BettaEntity fish) {
        BettaTraits p2 = fish.traits;
        //begin by passing on this' traits, will change based on random number
        BettaTraits.DorsalPreset passedDorsal = this.traits.dorsalPreset;
        BettaTraits.CaudalPreset passedCaudal = this.traits.caudalPreset;
        BettaTraits.PelvicPreset passedPelvic = this.traits.pelvicPreset;
        BettaTraits.BodyPreset passedBody = this.traits.bodyPreset;
        BettaTraits.AnalPreset passedAnal = this.traits.analPreset;
        ColorUtil passedPrimary = this.traits.primaryColor;
        ColorUtil passedSecondary = this.traits.secondaryColor;
        ColorUtil passedThird = this.traits.thirdColor;
        Boolean isButterfly = this.traits.isButterfly;
        BettaTraits.PatternPreset passedPattern = this.traits.patternPreset;

        //Pass on all genes by rolling numbers
        //0 = from this, 1 = from fish
        passedPelvic = this.random.nextInt(2) == 1 ? fish.traits.pelvicPreset : passedPelvic;
        passedAnal = this.random.nextInt(2) == 1 ? fish.traits.analPreset : passedAnal;
        passedBody = this.random.nextInt(2) == 1 ? fish.traits.bodyPreset : passedBody;
        passedCaudal = this.random.nextInt(2) == 1 ? fish.traits.caudalPreset : passedCaudal;
        passedDorsal = this.random.nextInt(2) == 1 ? fish.traits.dorsalPreset : passedDorsal;
        passedPattern = this.random.nextInt(2) == 1 ? fish.traits.patternPreset : passedPattern;
        passedPrimary = this.random.nextInt(2) == 1 ? fish.traits.primaryColor : passedPrimary;
        passedSecondary = this.random.nextInt(2) == 1 ? fish.traits.secondaryColor : passedSecondary;
        passedThird = this.random.nextInt(2) == 1 ? fish.traits.thirdColor : passedThird;
        isButterfly = this.random.nextInt(2) == 1 ? fish.traits.isButterfly : isButterfly;

        //handle mutations
        passedDorsal = tryMutateDorsal(this.traits, fish.traits,passedDorsal);
        passedCaudal = tryMutateCaudal(this.traits, fish.traits,passedCaudal);
        passedAnal = tryMutateAnal(this.traits, fish.traits, passedAnal);
        passedPelvic = tryMutatePelvic(this.traits, fish.traits, passedPelvic);
        passedPattern = tryMutatePattern(this.traits, fish.traits, passedPattern, passedPrimary, passedSecondary, passedThird);
        passedPrimary = tryMutateColor(this.traits.primaryColor, fish.traits.primaryColor, passedPrimary, passedSecondary);
        passedSecondary = tryMutateColor(this.traits.secondaryColor, fish.traits.secondaryColor, passedSecondary, passedPrimary);
        passedThird = tryMutateThirdColor(this.traits, fish.traits, passedThird, passedPrimary, passedSecondary, passedPattern);


        //create an egg, and set the egg entity to this.
        ItemStack egg = new ItemStack(getEggItem());
        BettaFishEggItem.setEntity(egg, this.getType());

        //create baby traits from the passed genes
        BettaTraits babyTraits = new BettaTraits(
                passedPrimary, passedSecondary, passedThird,
                passedPattern, isButterfly, passedBody, passedDorsal,
                passedCaudal, passedAnal, passedPelvic
        );

        //call in-trait attribute resolver to pass correct traits to the item NBT
        babyTraits.resolveAttributes();
        babyTraits.specialTexture = this.checkSpecialVariant(babyTraits);
        if(babyTraits.specialTexture != null){
            babyTraits.isSpecialVariant = true;
        }

        //Here is where we set genes
        CompoundTag eggTag = egg.getOrCreateTag();
        eggTag.putInt("PrimaryColor", babyTraits.primaryColor.getId());
        eggTag.putInt("SecondaryColor", babyTraits.secondaryColor.getId());
        eggTag.putInt("ThirdColor", babyTraits.thirdColor.getId());
        eggTag.putInt("PatternPreset", babyTraits.patternPreset.getId());
        eggTag.putBoolean("IsButterfly", babyTraits.isButterfly);
        eggTag.putInt("BodyPreset", babyTraits.bodyPreset.getId());
        eggTag.putInt("CaudalPreset", babyTraits.caudalPreset.getId());
        eggTag.putInt("DorsalPreset", babyTraits.dorsalPreset.getId());
        eggTag.putInt("AnalPreset", babyTraits.analPreset.getId());
        eggTag.putInt("PelvicPreset", babyTraits.pelvicPreset.getId());
        eggTag.putBoolean("SpecialVariant", babyTraits.isSpecialVariant);
        eggTag.putString("SpecialName", babyTraits.isSpecialVariant ? babyTraits.specialTexture : "n");

        this.spawnAtLocation(egg);
    }

    /***
     * the below set of methods run the mutation rules, and mutate the passed gene if rules ring true.
     * @return
     */
    private BettaTraits.DorsalPreset tryMutateDorsal(BettaTraits traits, BettaTraits traits1, BettaTraits.DorsalPreset passedDorsal) {
        boolean shouldMutate = Math.random() < 0.03;
        if(!shouldMutate) return passedDorsal;
        double rand = Math.random();
        BettaTraits.DorsalPreset mutatedTrait = passedDorsal;

        switch(passedDorsal) {
            case WILD:
                mutatedTrait = BettaTraits.DorsalPreset.MEDIUM;
                break;
            case MEDIUM:
                mutatedTrait = rand < 0.5 ? BettaTraits.DorsalPreset.TALL : BettaTraits.DorsalPreset.WILD;
                break;
            case TALL:
                mutatedTrait = BettaTraits.DorsalPreset.MEDIUM;
        }

        return mutatedTrait;

    }

    private BettaTraits.CaudalPreset tryMutateCaudal(BettaTraits traits, BettaTraits traits1, BettaTraits.CaudalPreset passedCaudal) {
        boolean shouldMutate = Math.random() < 0.03;
        if(!shouldMutate) return passedCaudal;
        BettaTraits.CaudalPreset p1Caudal = traits.caudalPreset;
        BettaTraits.CaudalPreset p2Caudal = traits1.caudalPreset;

        Set<BettaTraits.CaudalPreset> candidates = new HashSet<>();

        // Double: HM or Rose AND both parents same
        if ((p1Caudal == p2Caudal) &&
                (p1Caudal == BettaTraits.CaudalPreset.HM || p1Caudal == BettaTraits.CaudalPreset.ROSE)) {
            candidates.add(BettaTraits.CaudalPreset.DOUBLE);
        }

        // Rose: HM, Crown, Double AND at least one Crown
        if ((p1Caudal == BettaTraits.CaudalPreset.CROWN || p2Caudal == BettaTraits.CaudalPreset.CROWN) &&
                (p1Caudal == BettaTraits.CaudalPreset.HM || p2Caudal == BettaTraits.CaudalPreset.HM ||
                        p1Caudal == BettaTraits.CaudalPreset.DOUBLE || p2Caudal == BettaTraits.CaudalPreset.DOUBLE ||
                        p1Caudal == BettaTraits.CaudalPreset.CROWN || p2Caudal == BettaTraits.CaudalPreset.CROWN)) {
            candidates.add(BettaTraits.CaudalPreset.ROSE);
        }

        // Spade: Veil or HM AND one parent is Plakat
        if ((p1Caudal == BettaTraits.CaudalPreset.PLAKAT || p2Caudal == BettaTraits.CaudalPreset.PLAKAT) &&
                (p1Caudal == BettaTraits.CaudalPreset.VEIL || p2Caudal == BettaTraits.CaudalPreset.VEIL ||
                        p1Caudal == BettaTraits.CaudalPreset.HM || p2Caudal == BettaTraits.CaudalPreset.HM)) {
            candidates.add(BettaTraits.CaudalPreset.SPADE);
        }

        if (has(p1Caudal, p2Caudal, BettaTraits.CaudalPreset.PLAKAT) ||
                has(p1Caudal, p2Caudal, BettaTraits.CaudalPreset.ROSE) ||
                has(p1Caudal, p2Caudal, BettaTraits.CaudalPreset.DOUBLE) ||
                has(p1Caudal, p2Caudal, BettaTraits.CaudalPreset.HM)) {
            candidates.add(BettaTraits.CaudalPreset.VEIL);
        }

        if (has(p1Caudal, p2Caudal, BettaTraits.CaudalPreset.HM) ||
                has(p1Caudal, p2Caudal, BettaTraits.CaudalPreset.COMB)) {
            candidates.add(BettaTraits.CaudalPreset.CROWN);
        }

        if (has(p1Caudal, p2Caudal, BettaTraits.CaudalPreset.HM) ||
                has(p1Caudal, p2Caudal, BettaTraits.CaudalPreset.CROWN)) {
            candidates.add(BettaTraits.CaudalPreset.COMB);
        }

        if (has(p1Caudal, p2Caudal, BettaTraits.CaudalPreset.VEIL) ||
                has(p1Caudal, p2Caudal, BettaTraits.CaudalPreset.CROWN) ||
                has(p1Caudal, p2Caudal, BettaTraits.CaudalPreset.COMB) ||
                has(p1Caudal, p2Caudal, BettaTraits.CaudalPreset.ROSE)) {
            candidates.add(BettaTraits.CaudalPreset.HM);
        }

        if (has(p1Caudal, p2Caudal, BettaTraits.CaudalPreset.HM) ||
                has(p1Caudal, p2Caudal, BettaTraits.CaudalPreset.VEIL)) {
            candidates.add(BettaTraits.CaudalPreset.PLAKAT);
        }

        //Halfmoon tail is dominant, so we deal with that first
        if (candidates.contains(BettaTraits.CaudalPreset.HM)) {
            if (Math.random() < 0.5) {
                return BettaTraits.CaudalPreset.HM;
            } else {
                candidates.remove(BettaTraits.CaudalPreset.HM);
                if (!candidates.isEmpty()) {
                    BettaTraits.CaudalPreset[] arr = candidates.toArray(new BettaTraits.CaudalPreset[0]);
                    return arr[(int)(Math.random() * arr.length)];
                }

                return BettaTraits.CaudalPreset.HM;
            }
        }

        //Halfmoon is not possible, pick random
        if (!candidates.isEmpty()) {
            BettaTraits.CaudalPreset[] arr = candidates.toArray(new BettaTraits.CaudalPreset[0]);
            return arr[(int)(Math.random() * arr.length)];
        }

        return passedCaudal;

    }

    private BettaTraits.AnalPreset tryMutateAnal(BettaTraits traits, BettaTraits traits1, BettaTraits.AnalPreset passedAnal) {
        boolean shouldMutate = Math.random() < 0.03;
        if(!shouldMutate) return passedAnal;
        double rand = Math.random();
        BettaTraits.AnalPreset mutatedTrait = passedAnal;

        switch(passedAnal) {
            case WILD:
                mutatedTrait = rand < 0.5 ? BettaTraits.AnalPreset.SHORT : BettaTraits.AnalPreset.MEDIUM;
                break;
            case SHORT:
                mutatedTrait = rand < 0.5 ? BettaTraits.AnalPreset.WILD : BettaTraits.AnalPreset.MEDIUM;
                break;
            case MEDIUM:
                mutatedTrait = rand < 0.5 ? BettaTraits.AnalPreset.SHORT : BettaTraits.AnalPreset.TALL;
                break;
            case TALL:
                mutatedTrait = rand < 0.5 ? BettaTraits.AnalPreset.MEDIUM : BettaTraits.AnalPreset.SHORT;
        }

        return mutatedTrait;

    }

    private BettaTraits.PelvicPreset tryMutatePelvic(BettaTraits traits, BettaTraits traits1, BettaTraits.PelvicPreset passedPelvic) {
        boolean shouldMutate = Math.random() < 0.01;
        if(!shouldMutate) return passedPelvic;
        double rand = Math.random();
        BettaTraits.PelvicPreset mutatedTrait = passedPelvic;

        switch(passedPelvic) {
            case SHORT:
                mutatedTrait = rand < 0.5 ? BettaTraits.PelvicPreset.TINY : BettaTraits.PelvicPreset.MEDIUM;
                break;
            case TINY:
                mutatedTrait = BettaTraits.PelvicPreset.SHORT;
                break;
            case MEDIUM:
                mutatedTrait = rand < 0.5 ? BettaTraits.PelvicPreset.SHORT : BettaTraits.PelvicPreset.TALL;
                break;
            case TALL:
                mutatedTrait = BettaTraits.PelvicPreset.MEDIUM;
        }

        return mutatedTrait;

    }

    private BettaTraits.PatternPreset tryMutatePattern(BettaTraits traits, BettaTraits traits1, BettaTraits.PatternPreset passedPattern, ColorUtil colorP, ColorUtil colorS, ColorUtil thirdColor) {
        boolean shouldMutate = Math.random() < 0.03;
        if(!shouldMutate) return passedPattern;
        BettaTraits.PatternPreset p1Pattern = traits.patternPreset;
        BettaTraits.PatternPreset p2Pattern = traits1.patternPreset;

        //Dragon - super rare, overrides all other checks
        if(Math.random() < 0.1) {
            if(passedPattern == BettaTraits.PatternPreset.MARBLE) {
                return Math.random() < 0.5 ? BettaTraits.PatternPreset.DRAGON : BettaTraits.PatternPreset.MULTICOLOR;
            } else if(passedPattern == BettaTraits.PatternPreset.MULTICOLOR) {
                return BettaTraits.PatternPreset.DRAGON;
            }
        }

        //Multicolor - very rare, from Bicolor, Marble, Butterfly
        if(Math.random() < 0.1 && colorP != colorS) {
            if(passedPattern == BettaTraits.PatternPreset.BICOLOR || passedPattern == BettaTraits.PatternPreset.MARBLE ||
                    passedPattern == BettaTraits.PatternPreset.BUTTERFLY) {
                return BettaTraits.PatternPreset.MULTICOLOR;
            }
        }

        //Build candidate pool for remaining patterns
        List<BettaTraits.PatternPreset> candidates = new ArrayList<>();

        //Solid: from Butterfly when Primary==Secondary, from Cambodian when Secondary==White
        if(passedPattern == BettaTraits.PatternPreset.BUTTERFLY && colorP == colorS) {
            candidates.add(BettaTraits.PatternPreset.SOLID);
        } else if(passedPattern == BettaTraits.PatternPreset.CAMBODIAN && colorS == ColorUtil.WHITE && colorP == colorS) {
            candidates.add(BettaTraits.PatternPreset.SOLID);
        }

        //Cambodian: from Bicolor, Solid, Piebald, Butterfly when Primary==White and Primary!=Secondary
        if(colorP == ColorUtil.WHITE && colorP != colorS && (
                passedPattern == BettaTraits.PatternPreset.BICOLOR || passedPattern == BettaTraits.PatternPreset.SOLID ||
                passedPattern == BettaTraits.PatternPreset.PIEBALD || passedPattern == BettaTraits.PatternPreset.BUTTERFLY)) {
            candidates.add(BettaTraits.PatternPreset.CAMBODIAN);
        }

        //Piebald: from Cambodian, Bicolor, Marble, Butterfly when Secondary==White
        if(colorS == ColorUtil.WHITE && colorP != colorS && (
                passedPattern == BettaTraits.PatternPreset.BICOLOR || passedPattern == BettaTraits.PatternPreset.MARBLE ||
                passedPattern == BettaTraits.PatternPreset.CAMBODIAN || passedPattern == BettaTraits.PatternPreset.BUTTERFLY)) {
            candidates.add(BettaTraits.PatternPreset.PIEBALD);
        }

        //Marble: from Butterfly, Bicolor, Multicolor, Dragon when Primary!=Secondary and ThirdColor is White or None
        if(colorP != colorS && (thirdColor == ColorUtil.WHITE || thirdColor == ColorUtil.NONE)) {
            if(passedPattern == BettaTraits.PatternPreset.BUTTERFLY || passedPattern == BettaTraits.PatternPreset.BICOLOR ||
                passedPattern == BettaTraits.PatternPreset.MULTICOLOR || passedPattern == BettaTraits.PatternPreset.DRAGON) {
                candidates.add(BettaTraits.PatternPreset.MARBLE);
            }
        }

        //Bicolor/Butterfly: from Any when Primary!=Secondary. Solid parent biases toward Bicolor.
        if(colorP != colorS) {
            if(p1Pattern == BettaTraits.PatternPreset.SOLID || p2Pattern == BettaTraits.PatternPreset.SOLID) {
                candidates.add(BettaTraits.PatternPreset.BICOLOR);
                candidates.add(BettaTraits.PatternPreset.BICOLOR); // weighted: 2/3 Bicolor, 1/3 Butterfly
                candidates.add(BettaTraits.PatternPreset.BUTTERFLY);
            } else {
                candidates.add(BettaTraits.PatternPreset.BICOLOR);
                candidates.add(BettaTraits.PatternPreset.BUTTERFLY);
            }
        }

        if(!candidates.isEmpty()) {
            return candidates.get((int)(Math.random() * candidates.size()));
        }

        return passedPattern;
    }

    private ColorUtil tryMutateColor(ColorUtil p1Color, ColorUtil p2Color, ColorUtil passedColor, ColorUtil passedotherColor) {
        if (passedColor == ColorUtil.NONE) return ColorUtil.RED; //THis shouldnt happen but this is a safety
        boolean shouldMutate = Math.random() < 0.03;
        boolean didMutate = false; // used for less strict rules
        if(!shouldMutate) return passedColor;
        ColorUtil mutatedTrait = passedColor;

        //All Mutation Rules: if none match perform colorswap mutation
        //start with colorswap check, 15% chance
        if(Math.random() < 0.15) return passedotherColor;

        //Yellow
        if(isPair(p1Color, p2Color, ColorUtil.RED, ColorUtil.ORANGE)) return ColorUtil.YELLOW;

        //Turquoise
        if(isPair(p1Color, p2Color, ColorUtil.GREEN, ColorUtil.BLUE)) return ColorUtil.TURQUOISE;

        //Lavender
        if(isPair(p1Color, p2Color, ColorUtil.RED, ColorUtil.BLUE)) return ColorUtil.LAVENDER;

        //Purple
        if(isPair(p1Color, p2Color, ColorUtil.LAVENDER, ColorUtil.BLUE)) return ColorUtil.PURPLE;

        //Pink
        if(isPair(p1Color, p2Color, ColorUtil.RED, ColorUtil.WHITE) || isPair(p1Color, p2Color, ColorUtil.RED, ColorUtil.YELLOW)) {
            return ColorUtil.PINK;
        }

        //Red
        if (p1Color == ColorUtil.ORANGE || p2Color == ColorUtil.ORANGE) {
            didMutate = true;
            mutatedTrait = ColorUtil.RED;
        }

        //Blue
        if (p1Color == ColorUtil.TURQUOISE || p2Color == ColorUtil.TURQUOISE) {
            if(didMutate) mutatedTrait = Math.random() > 0.5 ? mutatedTrait : ColorUtil.BLUE;
            else {
                mutatedTrait = ColorUtil.BLUE;
                didMutate = true;
            }
        }

        //Green
        if (p1Color == ColorUtil.BROWN || p2Color == ColorUtil.BROWN) {
            if(didMutate) mutatedTrait = Math.random() > 0.5 ? mutatedTrait : ColorUtil.GREEN;
            else {
                mutatedTrait = ColorUtil.GREEN;
                didMutate = true;
            }
        }

        //Orange
        if (p1Color == ColorUtil.RED || p2Color == ColorUtil.RED) {
            if(didMutate) mutatedTrait = Math.random() > 0.5 ? mutatedTrait : ColorUtil.ORANGE;
            else {
                mutatedTrait = ColorUtil.ORANGE;
                didMutate = true;
            }
        }

        //White + Black
        if(Math.random() < 0.1) {
            mutatedTrait = Math.random() > 0.5 ? ColorUtil.BLACK : ColorUtil.WHITE;
            didMutate = true;
        }

        //if everything fails somehow, mutate to brown
        if(!didMutate) {
            mutatedTrait = ColorUtil.BROWN;
        }

        return mutatedTrait;
    }

    //this also resolves the Color rules
    private ColorUtil tryMutateThirdColor(BettaTraits traits, BettaTraits traits1, ColorUtil passedThirdColor, ColorUtil colorP, ColorUtil colorS, BettaTraits.PatternPreset passedPattern) {
        ColorUtil mutatedTrait = passedThirdColor;
        //This is a quick and dirty way to ensure third color in multicolors is always a color of some kind
        if(mutatedTrait == ColorUtil.NONE) {
            return colorP;
        }
        boolean shouldMutate = Math.random() < 0.03;
        if(!shouldMutate) return passedThirdColor;
        double rand = Math.random();

        switch (passedPattern) {
            case MULTICOLOR, MARBLE:
                if (passedThirdColor == colorS || passedThirdColor == colorP) {
                    mutatedTrait = ColorUtil.randomNonNone(this.random);
                }
                break;
            default:
                mutatedTrait = passedThirdColor;

        }

        return mutatedTrait;

    }

    public Item getEggItem() {
        return ModItems.BETTA_FISH_EGG.get();
    }

    /***
     * Drops ITEM_EGG. this will use both parents and call passGenes
     * @param level
     * @param fish
     */
    @Override
    public void spawnChildFromBreeding(ServerLevel level, AnimaliaBreedableWater fish) {
        if(!(fish instanceof BettaEntity)) {
            System.out.println("Partner not a Betta, how did this happen?");
            return;
        }

        this.passGenes(level, (BettaEntity) fish);
        this.setAge(6000);
        fish.setAge(6000);
        this.resetLove();
        fish.resetLove();
    }

    //checks against a hashmap defined at top of this class. returns Null if not found
    private String checkSpecialVariant(BettaTraits traits){
        String key = getSpecialKey(traits);
        return BETTA_SPECIALS.get(key);
    }

    private String getSpecialKey(BettaTraits t) {
        boolean usesThirdColor = (t.patternPreset == BettaTraits.PatternPreset.MARBLE ||
                                  t.patternPreset == BettaTraits.PatternPreset.MULTICOLOR);
        return t.bodyPreset.name().toLowerCase()
                + "_" + t.dorsalPreset.name().toLowerCase()
                + "_" + t.caudalPreset.name().toLowerCase()
                + "_" + t.analPreset.name().toLowerCase()
                + "_" + t.pelvicPreset.name().toLowerCase()
                + "_" + t.patternPreset.name().toLowerCase()
                + "_" + t.primaryColor.name().toLowerCase()
                + "_" + t.secondaryColor.name().toLowerCase()
                + "_" + (usesThirdColor ? t.thirdColor.name().toLowerCase() : "x")
                + "_" + (t.isButterfly ? "y" : "n");
    }

    //Method is called from the item egg class, and syncs traits between item NBT and betta's SynchedData
    public void syncTraits(CompoundTag itemTag) {
        if(itemTag != null) {
            if(itemTag.contains("PrimaryColor")) {
                this.entityData.set(PRIMARY_COLOR, itemTag.getInt("PrimaryColor"));
            }
            if(itemTag.contains("SecondaryColor")) {
                this.entityData.set(SECONDARY_COLOR, itemTag.getInt("SecondaryColor"));
            }
            if(itemTag.contains("ThirdColor")) {
                this.entityData.set(THIRD_COLOR, itemTag.getInt("ThirdColor"));
            }
            if(itemTag.contains("PatternPreset")){
                this.entityData.set(PATTERN_PRESET, itemTag.getInt("PatternPreset"));
            }
            if(itemTag.contains("IsButterfly")){
                this.entityData.set(IS_BUTTERFLY, itemTag.getBoolean("IsButterfly"));
            }
            if(itemTag.contains("BodyPreset")){
                this.entityData.set(BODY_PRESET, itemTag.getInt("BodyPreset"));
            }
            if(itemTag.contains("CaudalPreset")){
                this.entityData.set(CAUDAL_PRESET, itemTag.getInt("CaudalPreset"));
            }
            if(itemTag.contains("DorsalPreset")){
                this.entityData.set(DORSAL_PRESET, itemTag.getInt("DorsalPreset"));
            }
            if(itemTag.contains("AnalPreset")){
                this.entityData.set(ANAL_PRESET, itemTag.getInt("AnalPreset"));
            }
            if(itemTag.contains("PelvicPreset")){
                this.entityData.set(PELVIC_PRESET, itemTag.getInt("PelvicPreset"));
            }
            if(itemTag.contains("SpecialVariant")){
                this.entityData.set(SPECIAL_VARIANT, itemTag.getBoolean("SpecialVariant"));
            }
        }
        this.buildTraits();
    }

    //Helper Methods
    private boolean isPair(ColorUtil a, ColorUtil b, ColorUtil x, ColorUtil y) {
        return (a == x && b == y) || (a == y && b == x);
    }


    private boolean has(BettaTraits.CaudalPreset a, BettaTraits.CaudalPreset b, BettaTraits.CaudalPreset target) {
        return a == target || b == target;
    }

    private boolean both(BettaTraits.CaudalPreset a, BettaTraits.CaudalPreset b) {
        return a == b;
    }
}
