package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static com.google.common.base.Preconditions.checkNotNull;

public class Tank extends PlayableObject
{
    private static final String TAG = "Tank";
    private Soldier soldier;
    private boolean isEjected = false;

    public Tank(long id, Direction direction, String ip) {
        super(id, direction, ip);
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
        return new Tank(id, direction, ip);
    }

    @Override
    public void hit(int damage) {
        life = life - damage;
        System.out.println("Tank life: " + id + " : " + life);
//		Log.d(TAG, "TankId: " + id + " hit -> life: " + life);

        if (life <= 0) {
//			Log.d(TAG, "Tank event");
            //eventBus.post(Tank.this);
            //eventBus.post(new Object());
        }
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
        if (isEjected)
            return false;

        soldier = new Soldier(id, Direction.Up, ip);

        FieldHolder nextField = parent.getNeighbor(direction);
        checkNotNull(parent.getNeighbor(direction), "Neightbor is not available");

        if (!nextField.isPresent())
        {
            nextField.setFieldEntity(soldier);
            isEjected = true;
        }

        return isEjected;
    }

    public boolean reenter()
    {
        if (!isEjected)
            return false;

        isEjected = false;

        return isEjected;
    }
}
