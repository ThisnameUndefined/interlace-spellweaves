package org.xszb.interlace_spellweaves.entity.boss.nameless_wizards;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class FadeableSoundInstance extends AbstractTickableSoundInstance {
    boolean starting = false;
    private int transitionTicks;
    private final float baseVolume;
    private boolean triggerEnd = false;
    private static final int START_TRANSITION_TIME = 40;
    private static final int END_TRANSITION_TIME = 40;
    private float targetVolume = 1.0f; // 默认目标音量
    private float currentFadeMultiplier = 1.0f; // 当前淡入淡出的乘数

    protected FadeableSoundInstance(SoundEvent soundEvent, SoundSource source, boolean loop) {
        super(soundEvent, source, SoundInstance.createUnseededRandom());
        this.attenuation = Attenuation.NONE;
        this.looping = loop;
        this.delay = 0;
        this.volume = 1;
        this.baseVolume = this.volume;
        this.starting = false;
    }

    public void setFadeTarget(float volume) {
        this.targetVolume = volume;
    }

    @Override
    public void tick() {
        if (transitionTicks > 0) {
            transitionTicks--;
        }
        if (starting) {
            this.volume = 1f - ((float) transitionTicks / START_TRANSITION_TIME);
            if (transitionTicks == 0) {
                starting = false;
            }
        }
        if (triggerEnd) {
            this.volume = ((float) transitionTicks / END_TRANSITION_TIME);
            if (transitionTicks == 0) {
                this.stop();
            }
        }

        float fadeSpeed = 0.02f; // 每 tick 改变的步长，可根据需要调整
        if (currentFadeMultiplier < targetVolume) {
            currentFadeMultiplier = Math.min(targetVolume, currentFadeMultiplier + fadeSpeed);
        } else if (currentFadeMultiplier > targetVolume) {
            currentFadeMultiplier = Math.max(targetVolume, currentFadeMultiplier - fadeSpeed);
        }

        this.volume = this.baseVolume * currentFadeMultiplier;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    public void triggerStop() {
        this.triggerEnd = true;
        if (volume < 1f) {
            transitionTicks = (int) (END_TRANSITION_TIME * volume);
        } else {
            transitionTicks = END_TRANSITION_TIME;
        }
    }

    public void triggerStart() {
        this.triggerEnd = false;
        if (volume < 1f) {
            transitionTicks = (int) (START_TRANSITION_TIME * volume);
        } else {
            transitionTicks = START_TRANSITION_TIME;
        }
        starting = true;
    }
}
