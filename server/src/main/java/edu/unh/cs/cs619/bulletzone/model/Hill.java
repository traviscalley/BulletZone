package edu.unh.cs.cs619.bulletzone.model;

public class Hill extends FieldEntity
{
    @Override
    public int getIntValue()
    {
        return 2000;
    }

    @Override
    public FieldEntity copy()
    {
        return new Hill();
    }

    public boolean powerupSpawnable(){return false;}
}
