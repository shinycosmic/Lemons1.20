package net.lemon.animalia.entity.custom;

import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.entity.bases.helpers.ActivityTime;
import net.lemon.animalia.registry.ModBlocks;
import net.lemon.animalia.registry.ModEntities;
import net.lemon.animalia.registry.ModItems;
import net.lemon.animalia.registry.ModTags;
import net.lemon.animalia.util.AnimaliaFunctionUtil;
import net.lemon.animalia.util.HolonetEntities;
import net.lemon.animalia.util.Scannable;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;

public class CavefishEntity extends FishBase implements GeoEntity, Scannable {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private final int AMBLYOPSIS_HOOSIERI_PIXEL = 16;
    private final int CYPRINODON_DIABOLIS_PIXEL = 14;

    public CavefishEntity(EntityType<? extends FishBase> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public TagKey<Item> getFoodTag() {
        if(this.getType() == ModEntities.AMBLYOPSIS_HOOSIERI.get()) {
            return ModTags.Items.CRUSTACEAN;
        }
        return ModTags.Items.AQUATIC_PLANT;
    }

    @Override
    public Item getBreedingItem() {
        if(this.getType() == ModEntities.AMBLYOPSIS_HOOSIERI.get()) {
            return ModItems.AMPHIPOD.get();
        }
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
        if(this.getType() == ModEntities.AMBLYOPSIS_HOOSIERI.get()) {
            return Component.translatable("trivia.animalia.amblyopsis_hoosieri");
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_ANATIROSTRIS.get()) {
            return Component.translatable("trivia.animalia.sinocyclocheilus_anatirostris");
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_HYALINUS.get()) {
            return Component.translatable("trivia.animalia.sinocyclocheilus_hyalinus");
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_LONGICORNUS.get()) {
            return Component.translatable("trivia.animalia.sinocyclocheilus_longicornus");
        } else if(this.getType() == ModEntities.CYPRINODON_DIABOLIS.get()) {
            return Component.translatable("trivia.animalia.cyprinodon_diabolis");
        }
        return Component.translatable("debug.animalia.trivia");
    }

    //todo
    @Override
    public Component getFamily() {
        if(this.getType() == ModEntities.AMBLYOPSIS_HOOSIERI.get()) {
            return Component.translatable("family.animalia.amblyopsis_hoosieri");
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_ANATIROSTRIS.get()) {
            return Component.translatable("family.animalia.sinocyclocheilus_anatirostris");
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_HYALINUS.get()) {
            return Component.translatable("family.animalia.sinocyclocheilus_hyalinus");
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_LONGICORNUS.get()) {
            return Component.translatable("family.animalia.sinocyclocheilus_longicornus");
        } else if(this.getType() == ModEntities.CYPRINODON_DIABOLIS.get()) {
            return Component.translatable("family.animalia.cyprinodon_diabolis");
        }
        return Component.translatable("debug.animalia.family");
    }

    @Override
    public Component getOrder() {
        if(this.getType() == ModEntities.AMBLYOPSIS_HOOSIERI.get()) {
            return Component.translatable("order.animalia.amblyopsis_hoosieri");
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_ANATIROSTRIS.get()) {
            return Component.translatable("order.animalia.sinocyclocheilus_anatirostris");
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_HYALINUS.get()) {
            return Component.translatable("order.animalia.sinocyclocheilus_hyalinus");
        } else if(this.getType() == ModEntities.SINOCYCLOCHEILUS_LONGICORNUS.get()) {
            return Component.translatable("order.animalia.sinocyclocheilus_longicornus");
        } else if(this.getType() == ModEntities.CYPRINODON_DIABOLIS.get()) {
            return Component.translatable("order.animalia.cyprinodon_diabolis");
        }
        return Component.translatable("debug.animalia.order");
    }

    @Override
    public int getScaleforGUI() {
        if (this.getType() == ModEntities.MASTACEMBELUS_ARMATUS.get()) {
            return 54;
        } else if (this.getType() == ModEntities.MASTACEMBELUS_ERYTHROTAENIA.get()) {
            return 50;
        } else if (this.getType() == ModEntities.MACROGNATHUS_SIAMENSIS.get()){
            return 90;
        } else if (this.getType() == ModEntities.MASTACEMBELUS_BRICHARDI.get()){
            return 88;
        } else if (this.getType() == ModEntities.SINOBDELLA_SINENSIS.get()){
            return 88;
        }
        return Scannable.super.getScaleforGUI();
    }

    @Override
    public int getScaleforDetailGUI() {
        int currScale = Scannable.super.getScaleforDetailGUI();
        if(this.getType() == ModEntities.MASTACEMBELUS_ARMATUS.get()) {
            currScale *= 1.6f;
        } else if(this.getType() == ModEntities.MASTACEMBELUS_ERYTHROTAENIA.get()) {
            currScale *= 1.6f;
        } else if(this.getType() == ModEntities.MACROGNATHUS_SIAMENSIS.get()) {
            currScale *= 1.75f;
        } else if(this.getType() == ModEntities.MASTACEMBELUS_BRICHARDI.get()) {
            currScale *= 1.75f;
        } else if(this.getType() == ModEntities.SINOBDELLA_SINENSIS.get()) {
            currScale *= 1.7f;
        }

        return currScale;
    }

    @Override
    public float genVarSizeMultiplier() {
        if (this.getType() == ModEntities.AMBLYOPSIS_HOOSIERI.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(AMBLYOPSIS_HOOSIERI_PIXEL, this.genVarSize(45, 65, 60));
        } else if (this.getType() == ModEntities.CYPRINODON_DIABOLIS.get()) {
            return AnimaliaFunctionUtil.getScaleForSize(CYPRINODON_DIABOLIS_PIXEL, this.genVarSize(41, 55, 45));
        }
        return 1;
    }

    public static void registerHolonet(){
        HolonetEntities.register(ModEntities.AMBLYOPSIS_HOOSIERI, Scannable.AppName.FISH, "Synbranchiformes");
        HolonetEntities.register(ModEntities.CYPRINODON_DIABOLIS, Scannable.AppName.FISH, "Synbranchiformes");
        HolonetEntities.register(ModEntities.SINOCYCLOCHEILUS_ANATIROSTRIS, Scannable.AppName.FISH, "Synbranchiformes");
        HolonetEntities.register(ModEntities.SINOCYCLOCHEILUS_HYALINUS, Scannable.AppName.FISH, "Synbranchiformes");
        HolonetEntities.register(ModEntities.SINOCYCLOCHEILUS_LONGICORNUS, Scannable.AppName.FISH, "Synbranchiformes");

    }

    @Override
    public ItemStack getBucketItemStack() {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
