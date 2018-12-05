package edu.unh.cs.cs619.bulletzone.powerup;

import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.PlayableObject;

public class FusionReactor extends FieldEntity implements Powerup {
    @Override
    public int getIntValue() {
        return 7000;
    }

    @Override
    public FieldEntity copy() {
        return new FusionReactor();
    }

    @Override
    public boolean powerupSpawnable() {
        return false;
    }

    @Override
    public void powerupPlayer(PlayableObject po) {
        po.setAllowedFireInterval((int)po.getAllowedFireInterval()/2);
        po.setAllowedNumberOfBullets(po.getAllowedNumberOfBullets()*2);
        //slows move? lets halve moving rate
        po.setAllowedMoveInterval((int)po.getAllowedMoveInterval()*2);
        ///po. add this to list
        po.addPowerup(this);


    }

    @Override
    public void unpowerupPlayer(PlayableObject po) {
        po.setAllowedFireInterval((int)po.getAllowedFireInterval()*2);
        po.setAllowedNumberOfBullets(po.getAllowedNumberOfBullets()/2);
        //slows move? lets halve moving rate
        po.setAllowedMoveInterval((int)po.getAllowedMoveInterval()/2);

    }
}
