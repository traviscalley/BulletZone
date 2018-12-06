package edu.unh.cs.cs619.bulletzone.model;

public class Wall extends FieldEntity {
    int destructValue;

    public Wall(){
        this.destructValue = 1000;
    }

    public Wall(int destructValue){
        this.destructValue = destructValue;
    }

    @Override
    public FieldEntity copy() {
        return new Wall();
    }

    @Override
    public int getIntValue() {
        return destructValue;
    }

    @Override
    public String toString() {
        return "W";
    }

    @Override
    public boolean powerupSpawnable(){return false;}
    @Override
    public boolean tankSpawnable() {
        return false;
    }

}
