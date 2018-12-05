package edu.unh.cs.cs619.bulletzone.powerup;

import edu.unh.cs.cs619.bulletzone.model.FieldEntity;

public class PowerupFactory {
    private static PowerupFactory _instance;

    public static PowerupFactory getInstance() {
        if(_instance == null)
            _instance = new PowerupFactory();
        return _instance;
    }

    private PowerupFactory() {
    }

    public FieldEntity makePowerup(int type){
        switch(type){
            case 6:
                return new AntiGrav();
            case 7:
                return new FusionReactor();
            case 8:
                return new PowerRack();
            default:
                throw new IllegalArgumentException();
        }
    }
}
