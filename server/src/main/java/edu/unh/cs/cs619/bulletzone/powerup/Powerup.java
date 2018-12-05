package edu.unh.cs.cs619.bulletzone.powerup;

import edu.unh.cs.cs619.bulletzone.model.PlayableObject;

public interface Powerup {
    public void powerupPlayer(PlayableObject po);

    //for when powerup is ejected (maybe instaed have stack of state)
    public void unpowerupPlayer(PlayableObject po);
}
