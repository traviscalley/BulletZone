package edu.unh.cs.cs619.bulletzone.powerup;

import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.PlayableObject;

public class AntiGrav extends FieldEntity implements Powerup {
    @Override
    public int getIntValue() {
        return 6 * 10000000;
    }

    @Override
    public FieldEntity copy() {
        return new AntiGrav();
    }

    @Override
    public boolean powerupSpawnable() {
        return false;
    }
    @Override
    public boolean tankSpawnable() {
        return false;
    }

    @Override
    public void powerupPlayer(PlayableObject po) {
        po.setAllowedMoveInterval((int)po.getAllowedMoveInterval()/2);
        //slows firing? lets halve firing rate
        po.setAllowedFireInterval((int)po.getAllowedFireInterval()*2);
        ///po. add this to list
        //po.addPowerup(this);

    }
}
