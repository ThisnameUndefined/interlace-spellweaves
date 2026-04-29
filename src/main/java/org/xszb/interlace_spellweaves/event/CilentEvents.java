package org.xszb.interlace_spellweaves.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.xszb.interlace_spellweaves.InterlaceSpellWeaves;
import org.xszb.interlace_spellweaves.registries.RegistryEffect;

@Mod.EventBusSubscriber(modid = InterlaceSpellWeaves.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CilentEvents {

    @SubscribeEvent
    public static void beforeLivingRender(RenderLivingEvent.Pre<? extends LivingEntity, ? extends EntityModel<? extends LivingEntity>> event) {
        var player = Minecraft.getInstance().player;
        if (player == null)
            return;

        var livingEntity = event.getEntity();
        if (livingEntity.hasEffect(RegistryEffect.RIME_VEIL.get()) && livingEntity.isInvisibleTo(player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide() && entity.isAlive()) {

            if (entity.hasEffect(RegistryEffect.ICE_PLATE.get()) && entity.tickCount % 3 == 0) {

                int particleCount = 1;

                for (int i = 0; i < particleCount; i++) {
                    var random = entity.getRandom();

                    double offsetX = (random.nextDouble() - 0.5) * entity.getBbWidth();
                    double offsetY = random.nextDouble() * entity.getBbHeight();
                    double offsetZ = (random.nextDouble() - 0.5) * entity.getBbWidth();

                    double spawnX = entity.getX() + offsetX;
                    double spawnY = entity.getY() + offsetY;
                    double spawnZ = entity.getZ() + offsetZ;

                    double motionX = offsetX * 0.1 + (random.nextDouble() - 0.5) * 0.05;
                    double motionY = (random.nextDouble() * 0.1);
                    double motionZ = offsetZ * 0.1 + (random.nextDouble() - 0.5) * 0.05;

                    entity.level().addParticle(
                            ParticleTypes.SNOWFLAKE,
                            spawnX, spawnY, spawnZ,
                            motionX, motionY, motionZ
                    );
                }
            }
        }
    }
}
