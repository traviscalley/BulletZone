package edu.unh.cs.cs619.bulletzone.model;

public class Soldier extends PlayableObject
{
    private static final String TAG = "Soldier";

    public Soldier(long id, Direction direction, String ip) {
        super(id, direction, ip);
        numberOfBullets = 0;
        allowedNumberOfBullets = 6;
        lastFireTime = 0;
        allowedFireInterval = 250;
        lastMoveTime = 0;
        allowedMoveInterval = 1000;
        life = 25;
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

    @Override
    public int getIntValue() {
        return (int) (1000000 + 1000 * id + 10 * life + Direction
                .toByte(direction));
    }

    @Override
    public String toString() {
        return "T";
    }
}
