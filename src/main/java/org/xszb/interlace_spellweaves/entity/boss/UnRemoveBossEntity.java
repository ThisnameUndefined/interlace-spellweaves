package org.xszb.interlace_spellweaves.entity.boss;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

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

    @Override
    public boolean isAlive() {
        return !this.getCanKill();
    }
}
