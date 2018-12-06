package edu.unh.cs.cs619.bulletzone.powerup;

import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.PlayableObject;
import edu.unh.cs.cs619.bulletzone.model.Soldier;

public class PowerRack extends FieldEntity implements Powerup {
    private final int RACK_CAPACITY = 3;

    @Override
    public int getIntValue() {
        return 8000;
    }

    @Override
    public FieldEntity copy() {
        return new PowerRack();
    }

    @Override
    public boolean powerupSpawnable() {
        return false;
    }

    @Override
    public void powerupPlayer(PlayableObject po) {
        if(po instanceof Soldier){
            po.setAllowedMoveInterval(200);
            po.setAllowedTurnInterval(200);
        }

        po.setPowerupLimit(po.getPowerupLimit() + RACK_CAPACITY);
    }
}
