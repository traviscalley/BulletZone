package edu.unh.cs.cs619.bulletzone.powerup;

import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.PlayableObject;
import edu.unh.cs.cs619.bulletzone.model.Soldier;

public class PowerRack extends FieldEntity implements Powerup {

    private final int RACK_CAPACITY = 3;
    private Powerup[] powerups = new Powerup[RACK_CAPACITY];
    private int holding = 0;


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
        po.addPowerup(this);

        if(po instanceof Soldier){
            po.setAllowedMoveInterval(200);
            po.setAllowedTurnInterval(200);
        }

    }

    @Override
    public void unpowerupPlayer(PlayableObject po) {
        //recursive??
        //might as well jsut reset the playable object

    }

    //use this with if(addpowerup(po)){po.powerup(playableobject)}
    //powerup player is done in plaer object class
    public boolean addPowerup(Powerup power)
    {
        if(!isFull()){
            powerups[++holding] = power;
            return true;
        }
        else{
            for(int i = 0; i < RACK_CAPACITY; i++) {
                //better way?
                if(powerups[i] instanceof PowerRack){
                    if(((PowerRack)powerups[i]).addPowerup(power)){
                        return true;
                    }
                }
            }
            return false;
        }
    }

    //runs through the list backwards and recurses if it needs to
    public void removePowerup(){
        for(int i = RACK_CAPACITY - 1; i >= 0; i++){
            if(powerups[i] instanceof PowerRack){
                ((PowerRack)powerups[i]).removePowerup();
            }
            else if(powerups[i] != null){
                powerups[i] = null;
                holding--;
            }
        }
    }

    public boolean isFull(){
        return holding == RACK_CAPACITY-1;
    }


}
