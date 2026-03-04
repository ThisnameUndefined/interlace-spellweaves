package org.xszb.interlace_spellweaves.entity.goal;

import io.redspace.ironsspellbooks.api.entity.IMagicEntity;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.entity.mobs.goals.SpellBarrageGoal;

public class WarmSpellGoal extends SpellBarrageGoal {
    protected int spellWarmup;
    protected int spellStopTime;

    protected WarmSpellGoal(IMagicEntity abstractSpellCastingMob, AbstractSpell spell, int minLevel, int maxLevel, int pAttackIntervalMin, int pAttackIntervalMax, int projectileCount, int spellWarmup, int spellStopTime) {
        super(abstractSpellCastingMob,spell,minLevel,maxLevel,pAttackIntervalMin,pAttackIntervalMax,projectileCount);
        this.spellWarmup = spellWarmup;
        this.spellStopTime = spellStopTime;
    }

//
//
//    @Override
//    public void tick() {
//        this.spellWarmup--;
//        this.mob.setHealth(this.spellWarmup);
//        if (this.spellWarmup <= 0) {
//            super.tick();
//        }
//        if (this.spellWarmup <= -this.spellStopTime) {
//            super.stop();
//        }
//    }
}
