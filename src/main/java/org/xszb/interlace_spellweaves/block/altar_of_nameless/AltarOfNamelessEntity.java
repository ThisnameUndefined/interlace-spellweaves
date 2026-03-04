package org.xszb.interlace_spellweaves.block.altar_of_nameless;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.xszb.interlace_spellweaves.registries.RegistryBlock;

public class AltarOfNamelessEntity extends BlockEntity {
    public int cooldownProgress = 0;
    public static final int MAX_COOLDOWN = 300 * 20;

    public AltarOfNamelessEntity(BlockPos pos, BlockState state) {
        super(RegistryBlock.ALTAR_OF_NAMELESE_TILE.get(), pos, state);
    }

    public void startCooling() {
        this.cooldownProgress = 0;
        this.setChanged();
    }

    public static void commonTick(Level level, BlockPos pos, BlockState state, AltarOfNamelessEntity entity) {
        if (!state.getValue(AltarOfNamelessBlock.POWERED)) {
            entity.cooldownProgress++;
            if (entity.cooldownProgress >= MAX_COOLDOWN) {
                if (!level.isClientSide) {
                    level.setBlock(pos, state.setValue(AltarOfNamelessBlock.LIT, true).setValue(AltarOfNamelessBlock.POWERED, true), 3);
                    entity.cooldownProgress = 0;
                }
            }
        }

        if (level.isClientSide && state.getValue(AltarOfNamelessBlock.POWERED)) {
            if (level.random.nextFloat() < 0.3F) {
                double x = pos.getX() + 0.5 + (level.random.nextDouble() - 0.5) * 0.5;
                double y = pos.getY() + 1.2;
                double z = pos.getZ() + 0.5 + (level.random.nextDouble() - 0.5) * 0.5;
                level.addParticle(net.minecraft.core.particles.ParticleTypes.PORTAL,
                        x, y, z,
                        (level.random.nextDouble() - 0.5) * 0.1,
                        -level.random.nextDouble() * 0.1,
                        (level.random.nextDouble() - 0.5) * 0.1);
            }
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.cooldownProgress = nbt.getInt("cooldownProgress");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("cooldownProgress", this.cooldownProgress);
    }




}