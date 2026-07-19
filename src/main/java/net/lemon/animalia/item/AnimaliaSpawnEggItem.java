package net.lemon.animalia.item;

import net.lemon.animalia.entity.bases.AnimaliaBreedableWater;
import net.lemon.animalia.entity.bases.FishBase;
import net.lemon.animalia.registry.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class AnimaliaSpawnEggItem extends ForgeSpawnEggItem {
    private static final Map<EntityType<?>, String> SCIENTIFIC_NAMES = new HashMap<>();

    public static void registerScientificName(Supplier<? extends EntityType<?>> type, String name) {
        SCIENTIFIC_NAMES.put(type.get(), name);
    }

    public static void registerScientificNames() {
        AnimaliaSpawnEggItem.registerScientificName(ModEntities.CHILEANSEABASS, "Dissostichus eleginoides");
        AnimaliaSpawnEggItem.registerScientificName(ModEntities.ELEGINOPS_MACLOVINUS, "Eleginops maclovinus");
        AnimaliaSpawnEggItem.registerScientificName(ModEntities.PSEUDAPHRITIS_URVILLII, "Pseudaphritis urvillii");
        AnimaliaSpawnEggItem.registerScientificName(ModEntities.PERCOPHIS_BRASILIENSIS, "Percophis brasiliensis");
        AnimaliaSpawnEggItem.registerScientificName(ModEntities.SYNBRANCHUS_MARMORATUS, "Synbranchus marmoratus");
        AnimaliaSpawnEggItem.registerScientificName(ModEntities.CHAUDHURIA_CAUDATA, "Chaudhuria caudata");
        AnimaliaSpawnEggItem.registerScientificName(ModEntities.MASTACEMBELUS_ARMATUS, "Mastacembelus armatus");
        AnimaliaSpawnEggItem.registerScientificName(ModEntities.MASTACEMBELUS_ERYTHROTAENIA, "Mastacembelus erythrotaenia");
        AnimaliaSpawnEggItem.registerScientificName(ModEntities.MACROGNATHUS_SIAMENSIS, "Macrognathus siamensis");
        AnimaliaSpawnEggItem.registerScientificName(ModEntities.MASTACEMBELUS_BRICHARDI, "Mastacembelus brichardi");
        AnimaliaSpawnEggItem.registerScientificName(ModEntities.SINOBDELLA_SINENSIS, "Sinobdella sinensis");
        AnimaliaSpawnEggItem.registerScientificName(ModEntities.RAKTHAMICHTHYS_INDICUS, "Rakthamichthys indicus");
        AnimaliaSpawnEggItem.registerScientificName(ModEntities.BETTA_SPLENDENS, "Betta splendens");
        AnimaliaSpawnEggItem.registerScientificName(ModEntities.NEMATISTIUS_PECTORALIS, "Nematistius pectoralis");
        AnimaliaSpawnEggItem.registerScientificName(ModEntities.TOXOTES_CHATAREUS, "Toxotes chatareus");
        AnimaliaSpawnEggItem.registerScientificName(ModEntities.POGONOPHRYNE_MARMORATA, "Pogonophryne marmorata");
        AnimaliaSpawnEggItem.registerScientificName(ModEntities.CHAENOCEPHALUS_ACERATUS, "Chaenocephalus aceratus");
    }

    public AnimaliaSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, Properties props) {
        super(type, backgroundColor, highlightColor, props);
    }

    @Override
    public Component getName(ItemStack stack) {
        EntityType<?> type = this.getType(stack.getTag());
        return Component.translatable("item.animalia.spawn_egg", type.getDescription());
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComp, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComp, isAdvanced);
        EntityType<?> type = this.getType(stack.getTag());

        String name = SCIENTIFIC_NAMES.get(type);
        if (name != null) {
            tooltipComp.add(Component.literal(name).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        }
    }
}