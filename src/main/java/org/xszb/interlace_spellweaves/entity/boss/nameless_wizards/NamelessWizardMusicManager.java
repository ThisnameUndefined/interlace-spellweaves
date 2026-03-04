package org.xszb.interlace_spellweaves.entity.boss.nameless_wizards;

import io.redspace.ironsspellbooks.config.ClientConfigs;
import io.redspace.ironsspellbooks.registries.SoundRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;
import org.xszb.interlace_spellweaves.registries.RegistrySound;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class NamelessWizardMusicManager {
    @Nullable
    private static NamelessWizardMusicManager INSTANCE;
    static final SoundSource SOUND_SOURCE = SoundSource.MUSIC;

    static final int INTRO_LENGTH_MS = 24533;

    NamelessWizardsEntity boss;
    final SoundManager soundManager;

    FadeableSoundInstance introSound;
    FadeableSoundInstance loopSound;

    Set<FadeableSoundInstance> layers = new HashSet<>();

    private long startTime;
    private boolean hasPlayedIntro = false;
    private boolean finishing = false;

    private NamelessWizardMusicManager(NamelessWizardsEntity boss) {
        this.boss = boss;
        this.soundManager = Minecraft.getInstance().getSoundManager();

        this.introSound = new FadeableSoundInstance(RegistrySound.NAMELESS_WIZARD_INTRO.get(), SOUND_SOURCE, false);
        this.loopSound = new FadeableSoundInstance(RegistrySound.NAMELESS_WIZARD_LOOP.get(), SOUND_SOURCE, true);

        init();
    }

    private void init() {
        soundManager.stop(null, SoundSource.MUSIC);
        addLayer(introSound);
        this.startTime = System.currentTimeMillis();
        this.hasPlayedIntro = false;
    }
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (INSTANCE != null && event.phase == TickEvent.Phase.START && !Minecraft.getInstance().isPaused()) {
            INSTANCE.tick();
        }
    }
    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event) {
        if (NamelessWizardMusicManager.INSTANCE != null && event.getSound() != null) {
            if (event.getSound().getSource() == SoundSource.MUSIC) {
                String soundPath = event.getSound().getLocation().getPath();

                if (!soundPath.equals(RegistrySound.NAMELESS_WIZARD_LOOP.get().getLocation().getPath()) && !soundPath.contains(RegistrySound.NAMELESS_WIZARD_INTRO.get().getLocation().getPath())) {
                    event.setSound(null);
                }
            }
        }
    }

    private void tick() {
        if (isDone() || finishing) return;

        if (boss.isRemoved() || boss.getActType() == NamelessWizardsEntity.ActType.DEAD) {
            stopLayers();
            finishing = true;
            return;
        }

        if (!hasPlayedIntro) {
            long elapsed = System.currentTimeMillis() - startTime;
            if (!soundManager.isActive(introSound) || elapsed >= (INTRO_LENGTH_MS - 79)) {
                hasPlayedIntro = true;
                layers.remove(introSound);
                addLayer(loopSound);
            }
        }

        layers.forEach(this::applyVolumeLogic);
    }

    private void applyVolumeLogic(FadeableSoundInstance sound) {
        float target = 1.0f;
        if (boss.getActType() == NamelessWizardsEntity.ActType.PHASE) {
            target = 0.3f;
        } else if (boss.getActType() == NamelessWizardsEntity.ActType.BREAK2) {
            target = 0.1f;
        }
        if (boss.getActType() == NamelessWizardsEntity.ActType.DEAD) {
            target = 0.1f;
        }

        sound.setFadeTarget(target);
    }

    private void addLayer(FadeableSoundInstance soundInstance) {
        layers.removeIf(sound -> sound.isStopped() || !soundManager.isActive(sound));

        soundManager.play(soundInstance);
        layers.add(soundInstance);
    }

    private boolean isDone() {
        if (layers.isEmpty()) return true;
        for (FadeableSoundInstance sound : layers) {
            if (!sound.isStopped() && soundManager.isActive(sound)) {
                return false;
            }
        }
        return true;
    }

    public void stopLayers() {
        layers.forEach(FadeableSoundInstance::triggerStop);
    }

    public static void createOrResumeInstance(NamelessWizardsEntity boss) {
        if (INSTANCE == null || INSTANCE.isDone()) {
            if (ClientConfigs.ENABLE_BOSS_MUSIC.get()) {
                INSTANCE = new NamelessWizardMusicManager(boss);
            }
        } else {
            INSTANCE.triggerResume(boss);
        }
    }

    public void triggerResume(NamelessWizardsEntity boss) {
        if (boss.getUUID().equals(this.boss.getUUID())) {
            this.boss = boss;
        }
        if (!this.boss.isRemoved()) {
            layers.forEach(sound -> {
                sound.triggerStart();
                if (!soundManager.isActive(sound)) {
                    soundManager.play(sound);
                }
            });
            finishing = false;
        }
    }

    public static void stop(NamelessWizardsEntity boss) {
        if (INSTANCE != null && INSTANCE.boss.getUUID().equals(boss.getUUID())) {
            INSTANCE.stopLayers();
            INSTANCE.finishing = true;
        }
    }

    public static void hardStop() {
        if (INSTANCE != null) {
            INSTANCE.layers.forEach(INSTANCE.soundManager::stop);
            INSTANCE = null;
        }
    }
}