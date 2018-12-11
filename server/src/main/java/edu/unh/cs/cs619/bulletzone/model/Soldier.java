package edu.unh.cs.cs619.bulletzone.model;

public class Soldier extends PlayableObject
{
    private static final String TAG = "Soldier";

    public Soldier(long id, Direction direction, String ip, Game game) {
        super(id, direction, ip, game);
        numberOfBullets = 0;
        allowedNumberOfBullets = 6;
        lastFireTime = 0;
        allowedFireInterval = 250;
        lastMoveTime = 0;
        allowedMoveInterval = 1000;
        life = 25;
        stateStack.push(makeConfig());
    }

    @Override
    public FieldEntity copy() {
        return new Soldier(id, direction, ip, game);
    }

    @Override
    public int getIntValue() {
        return (int)(20000000 + id * 10000 + life * 10 + Direction.toByte(direction));
    }

    @Override
    public String toString() {
        return "T";
    }
}
