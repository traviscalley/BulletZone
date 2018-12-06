package edu.unh.cs.cs619.bulletzone.model;

public class Water extends FieldEntity{
    @Override
    public int getIntValue()
    {
        return 5000;
    }

    @Override
    public FieldEntity copy()
    {
        return new Water();
    }

    public boolean powerupSpawnable(){return false;}
    @Override
    public boolean tankSpawnable() {
        return false;
    }
}
