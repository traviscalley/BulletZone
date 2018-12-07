package edu.unh.cs.cs619.bulletzone.powerup;

import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.PlayableObject;

public abstract class Powerup extends FieldEntity {
    public abstract void powerupPlayer(PlayableObject po);


    @Override
    public boolean powerupSpawnable() {
        return false;
    }

    @Override
    public boolean tankSpawnable() {
        return false;
    }

    @Override
    public void hit(Bullet bullet){}

    //for when powerup is ejected (maybe instaed have stack of state)
}
