package edu.unh.cs.cs619.bulletzone.model;

public class Coast extends FieldEntity{
    @Override
    public int getIntValue()
    {
        return 4000;
    }

    @Override
    public FieldEntity copy()
    {
        return new Coast();
    }

    public boolean powerupSpawnable(){return true;}
    @Override
    public boolean tankSpawnable() {
        return true;
    }
}
