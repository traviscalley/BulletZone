package edu.unh.cs.cs619.bulletzone.model;

public class Wall extends FieldEntity {
    private int health;

    public Wall(){
        this.health = 0;
    }

    public Wall(int destructValue){
        this.health = destructValue;
    }

    @Override
    public FieldEntity copy() {
        return new Wall();
    }

    @Override
    public int getIntValue() {
        return 50000000 + health*10;
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

    @Override
    public void hit(Bullet bullet){
        if(health != 0) {
            if(health <= bullet.getDamage())
                parent.clearField();
            else
                health -= bullet.getDamage();
        }
    }
}
