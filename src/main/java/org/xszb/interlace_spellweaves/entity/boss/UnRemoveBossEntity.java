package org.xszb.interlace_spellweaves.entity.boss;

import io.redspace.ironsspellbooks.entity.mobs.abstract_spell_casting_mob.AbstractSpellCastingMob;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import org.xszb.interlace_spellweaves.entity.boss.nameless_wizards.NamelessWizardsEntity;
import org.xszb.interlace_spellweaves.mixin.EntityAccessor;
import org.xszb.interlace_spellweaves.util.EntityUtil;

public class UnRemoveBossEntity extends BossEntity {
    protected UnRemoveBossEntity(EntityType<? extends PathfinderMob> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    private static final EntityDataAccessor<Boolean> CANKILL = SynchedEntityData.defineId(UnRemoveBossEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> NOTDIE = SynchedEntityData.defineId(UnRemoveBossEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> YOU_CHEAT = SynchedEntityData.defineId(UnRemoveBossEntity.class, EntityDataSerializers.BOOLEAN);


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(NOTDIE, false);
        this.entityData.define(CANKILL, false);
        this.entityData.define(YOU_CHEAT, false);
    }

    public boolean getCanKill() {return  this.entityData.get(CANKILL);}

    public void setCanKill (boolean bool){ this.entityData.set(CANKILL, bool); }

    public boolean getCanDie(){return this.entityData.get(NOTDIE);}

    public void setCanDie (boolean bool){ this.entityData.set(NOTDIE, bool); }

    public void setAntiCheatMode (boolean cheatMode){
        this.entityData.set(YOU_CHEAT, cheatMode);
    }

    public boolean getIsAntiCheatMode(){return this.entityData.get(YOU_CHEAT);}
}
