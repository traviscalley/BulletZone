package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Ship extends PlayableObject
{
    public Ship(long id, Direction direction, String ip)
    {
        super(id, direction, ip);
        numberOfBullets = 0;
        allowedNumberOfBullets = 8;
        lastFireTime = 0;
        lastMoveTime = 0;
        allowedMoveInterval = 750;
        life = 100;
        stateStack.push(makeConfig());
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    @Override
    public FieldEntity copy() {
        return new Tank(id, direction, ip);
    }

    @Override
    public int getIntValue()
    {
        return (int) (20000000 + 10000 * id + 10 * life + Direction
                .toByte(direction));
    }
}
