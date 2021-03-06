package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static com.google.common.base.Preconditions.checkNotNull;

public class Tank extends PlayableObject
{
    private static final String TAG = "Tank";
    private Soldier soldier;
    private boolean isEjected = false;
    private long lastEjectTime;

    public Tank(long id, Direction direction, String ip, Game game) {
        super(id, direction, ip, game);
        numberOfBullets = 0;
        allowedNumberOfBullets = 2;
        lastFireTime = 0;
        allowedFireInterval = 1500;
        lastMoveTime = 0;
        allowedMoveInterval = 500;
        life = 100;
        stateStack.push(makeConfig());
    }

    @Override
    public FieldEntity copy() {
        return new Tank(id, direction, ip, game);
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    @Override
    public int getIntValue() {
        return (int)(1 * 10000000 + id * 10000 + life * 10 + Direction.toByte(direction));
    }

    @Override
    public String toString() {
        return "T";
    }

    public boolean getEjected() {return isEjected;}

    public Soldier getSoldier() {return soldier;}

    public boolean eject()
    {
        long curTime = System.currentTimeMillis();
        if (curTime - lastEjectTime <= 3000 || isEjected)
            return false;

        soldier = new Soldier(id, direction, ip, game);

        FieldHolder nextField = parent.getNeighbor(direction);
        checkNotNull(parent.getNeighbor(direction), "Neightbor is not available");

        if (!nextField.isPresent())
        {
            nextField.setFieldEntity(soldier);
            isEjected = true;
            lastEjectTime = System.currentTimeMillis();
        }

        return isEjected;
    }

    public boolean reenter()
    {
        if (!isEjected)
            return false;

        isEjected = false;
        soldier = null;

        return isEjected;
    }
}
