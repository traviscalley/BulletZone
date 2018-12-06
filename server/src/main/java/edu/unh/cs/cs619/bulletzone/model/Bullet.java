package edu.unh.cs.cs619.bulletzone.model;

public class Bullet extends FieldEntity {

    private long tankId;
    private Direction direction;
    private int damage, bulletId;

    public Bullet(long tankId, Direction direction, int damage) {
        this.damage = damage;
        this.setTankId(tankId);
        this.setDirection(direction);
    }

    @Override
    public int getIntValue() {
        return (int)(4 * 10000000 + tankId * 10000 + damage * 10 + Direction.toByte(direction));
    }

    @Override
    public String toString() {
        return "B";
    }

    @Override
    public FieldEntity copy() {
        return new Bullet(tankId, direction, damage);
    }

    public long getTankId() {
        return tankId;
    }

    public void setTankId(long tankId) {
        this.tankId = tankId;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setBulletId(int bulletId) {
        this.bulletId = bulletId;
    }

    public int getBulletId() {
        return bulletId;
    }

    @Override
    public boolean powerupSpawnable() {
        return false;
    }

    @Override
    public boolean tankSpawnable() {
        return false;
    }
}

