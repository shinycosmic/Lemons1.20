package net.lemon.animalia.block.entities;

import net.lemon.animalia.block.FilterTrapBlock;
import net.lemon.animalia.registry.ModBlockEntities;
import net.lemon.animalia.client.screens.FilterTrapMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FilterTrapBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(6);
    private static final int OUTPUT_SLOT1 = 0;
    private static final int OUTPUT_SLOT2 = 1;
    private static final int OUTPUT_SLOT3 = 2;
    private static final int OUTPUT_SLOT4 = 3;
    private static final int OUTPUT_SLOT5 = 4;
    private static final int FUEL_SLOT = 5;
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    protected final ContainerData data;
    private int timer = 0;
    private int dropTimer = 3000;

    public FilterTrapBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.FILTER_TRAP_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> FilterTrapBlockEntity.this.timer;
                    case 1 -> FilterTrapBlockEntity.this.dropTimer;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> FilterTrapBlockEntity.this.timer = pValue;
                    case 1 -> FilterTrapBlockEntity.this.dropTimer = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.animalia.filter_trap");
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new FilterTrapMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("filter_trap.timer", timer);
        pTag.putInt("filter_trap.dropTimer", dropTimer);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        timer = pTag.getInt("filter_trap.timer");
        dropTimer = pTag.getInt("filter_trap.dropTimer");
    }

    public ItemStack getRenderStack() {
        if(!itemHandler.getStackInSlot(OUTPUT_SLOT1).isEmpty()) {
            return itemHandler.getStackInSlot(OUTPUT_SLOT1);
        } else if(!itemHandler.getStackInSlot(OUTPUT_SLOT2).isEmpty()) {
            return itemHandler.getStackInSlot(OUTPUT_SLOT2);
        } else if(!itemHandler.getStackInSlot(OUTPUT_SLOT3).isEmpty()) {
            return itemHandler.getStackInSlot(OUTPUT_SLOT3);
        } else if(!itemHandler.getStackInSlot(OUTPUT_SLOT4).isEmpty()) {
            return itemHandler.getStackInSlot(OUTPUT_SLOT4);
        } else if(!itemHandler.getStackInSlot(OUTPUT_SLOT5).isEmpty()) {
            return itemHandler.getStackInSlot(OUTPUT_SLOT5);
        } else {
            return null;
        }
    }

    public void tick(Level level1, BlockPos pos, BlockState state1) {
        if(placedInWater() && hasFuel()) {
            timer++;
            setChanged(level1, pos, state1);

            if (timer >= dropTimer) {
                ItemStack drop = getDrop();
                if (!drop.isEmpty()) {
                    addDropToInventory(drop);
                    consumeFuel();
                }
                resetTimer();
            }
        } else {
            resetTimer();
        }
    }

    private boolean hasFuel() {
        ItemStack fuel = itemHandler.getStackInSlot(FUEL_SLOT);
        return !fuel.isEmpty() && fuel.is(net.minecraft.tags.ItemTags.FISHES);
    }

    private void consumeFuel() {
        itemHandler.extractItem(FUEL_SLOT, 1, false);
        setChanged();
    }

    private static final ResourceLocation LOOT_TABLE = new ResourceLocation("animalia", "drops/filter_trap");

    private ItemStack getDrop() {
        if(!(level instanceof ServerLevel serverLevel)) {
            return ItemStack.EMPTY;
        }

        LootTable table = serverLevel.getServer()
                .getLootData()
                .getLootTable(LOOT_TABLE);

        LootParams params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
                .create(LootContextParamSets.CHEST);

        List<ItemStack> drops = table.getRandomItems(params);

        return drops.isEmpty() ? ItemStack.EMPTY : drops.get(0);
    }

    private void resetTimer() {
        timer = 0;
    }

    private void addDropToInventory(ItemStack stack) {
        for(int i = 0; i < itemHandler.getSlots(); i++) {
            stack = itemHandler.insertItem(i, stack, false);

            if(stack.isEmpty()) {
                setChanged();
                return;
            }
        }
    }

    private boolean placedInWater() {
        return getBlockState().getValue(FilterTrapBlock.WATERLOGGED);
    }

}
