package edu.unh.cs.cs619.bulletzone.model;

public class DebrisField extends FieldEntity {
    @Override
    public int getIntValue() {
        return 2 * 100000000;
    }

    @Override
    public FieldEntity copy()
    {
        return new DebrisField();
    }

    public boolean powerupSpawnable(){return false;}
    @Override
    public boolean tankSpawnable() {
        return false;
    }
}
