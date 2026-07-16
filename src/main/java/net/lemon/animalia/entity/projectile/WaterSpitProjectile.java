package net.lemon.animalia.entity.projectile;

import net.lemon.animalia.Animalia;
import net.lemon.animalia.registry.ModEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class WaterSpitProjectile extends Projectile {
    private static final ResourceLocation LOOT_TABLE = new ResourceLocation(Animalia.MODID, "drops/archerfish_spit");
    private static final int MAX_LIFETIME = 60;
    private int life = 0;

    public WaterSpitProjectile(EntityType<? extends WaterSpitProjectile> type, Level level) {
        super(type, level);
    }

    public WaterSpitProjectile(Level level, double x, double y, double z) {
        super(ModEntities.WATER_SPIT.get(), level);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();

        if (++this.life > MAX_LIFETIME) {
            this.discard();
            return;
        }

        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS) {
            this.onHit(hit);
        }

        Vec3 velocity = this.getDeltaMovement();
        this.setPos(this.getX() + velocity.x, this.getY() + velocity.y, this.getZ() + velocity.z);

        // Gravity only applies in air
        if (!this.isInWater()) {
            if (!this.isNoGravity()) {
                this.setDeltaMovement(velocity.x, velocity.y - 0.06, velocity.z);
            }
            this.setDeltaMovement(this.getDeltaMovement().scale(0.99));
        }

        this.updateRotation();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (!this.level().isClientSide) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            LootTable table = serverLevel.getServer().getLootData().getLootTable(LOOT_TABLE);
            LootParams params = new LootParams.Builder(serverLevel)
                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(result.getBlockPos()))
                    .create(LootContextParamSets.EMPTY);

            for (ItemStack stack : table.getRandomItems(params)) {
                Block.popResource(this.level(), result.getBlockPos(), stack);
            }
        }
        this.discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        this.discard();
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        return super.canHitEntity(entity) && (this.getOwner() == null || !entity.is(this.getOwner()));
    }
}