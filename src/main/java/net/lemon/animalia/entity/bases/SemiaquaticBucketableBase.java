package net.lemon.animalia.entity.bases;

import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Bucketable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Objects;

public abstract class SemiaquaticBucketableBase extends SemiaquaticBase implements Bucketable {
    private static final EntityDataAccessor<Boolean> FROM_BUCKET = SynchedEntityData.defineId(SemiaquaticBucketableBase.class, EntityDataSerializers.BOOLEAN);

    protected SemiaquaticBucketableBase(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public boolean fromBucket() {
        return this.entityData.get(FROM_BUCKET);
    }

    public void setFromBucket(boolean pFromBucket) {
        this.entityData.set(FROM_BUCKET, pFromBucket);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FROM_BUCKET, false);
    }

    @Override
    public ItemStack getBucketItemStack() {
        String name = EntityType.getKey(this.getType()).getPath();
        return new ItemStack(Objects.requireNonNull(ModEntities.BUCKET_MAP.get(name), () -> "No bucket registered for " + name).get());
    }

    @Override
    public void saveToBucketTag(ItemStack stack) {
        CompoundTag compoundTag = stack.getOrCreateTag();
        compoundTag.putFloat("BucketVarSize", this.getVarSizeMultiplier());
        compoundTag.putInt("Age", this.eatAge());
        compoundTag.putInt("BucketGender", this.getGender());
        compoundTag.putBoolean("BucketBold", this.isBold());
        compoundTag.putInt("BucketVarColor", this.getVarColor());
        compoundTag.putBoolean("BucketBaby", this.isBaby());
    }

    public void loadFromBucketTag(CompoundTag pTag) {
        if(pTag != null) {
            if(pTag.contains("BucketVarSize")) {
                this.setVarSizeMultiplier(pTag.getFloat("BucketVarSize"));
            }
            if(pTag.contains("Age")) {
                this.setEatAge(pTag.getInt("Age"));
            }
            if(pTag.contains("BucketGender")) {
                this.setGender(pTag.getInt("BucketGender"));
            }
            if(pTag.contains("BucketBold")) {
                this.setBold(pTag.getBoolean("BucketBold"));
            }
            if(pTag.contains("BucketVarColor")){
                this.setVarColor(pTag.getInt("BucketVarColor"));
            }
        }
        Bucketable.loadDefaultDataFromBucketTag(this, pTag);
    }

    public SoundEvent getPickupSound() {
        return SoundEvents.BUCKET_FILL_FISH;
    }

    protected SoundEvent getSwimSound() {
        return SoundEvents.FISH_SWIM;
    }
}
