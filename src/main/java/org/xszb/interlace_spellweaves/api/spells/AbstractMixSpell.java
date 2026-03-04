package org.xszb.interlace_spellweaves.api.spells;

import io.redspace.ironsspellbooks.api.spells.AbstractSpell;

public abstract class AbstractMixSpell extends AbstractSpell {
    public boolean allowLooting() {
        return false;
    }

    public boolean allowCrafting() {
        return false;
    }
}
