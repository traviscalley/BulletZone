package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Soldier extends PlayableObject
{
    private static final String TAG = "Soldier";

    public Soldier(long id, Direction direction, String ip) {
        super(id, direction, ip);

        // @TODO
        numberOfBullets = 0;
        allowedNumberOfBullets = 2;
        lastFireTime = 0;
        allowedFireInterval = 1500;
        lastMoveTime = 0;
        allowedMoveInterval = 500;
    }

    @Override
    public FieldEntity copy() {
        return new Tank(id, direction, ip);
    }

    @Override
    public void hit(int damage) {
        life = life - damage;
        System.out.println("Soldier life: " + id + " : " + life);
//		Log.d(TAG, "TankId: " + id + " hit -> life: " + life);

        if (life <= 0) {
//			Log.d(TAG, "Tank event");
            //eventBus.post(Tank.this);
            //eventBus.post(new Object());
        }
    }

    // @TODO
    @Override
    public int getIntValue() {
        return (int) (10000000 + 10000 * id + 10 * life + Direction
                .toByte(direction));
    }

    @Override
    public String toString() {
        return "T";
    }
}
