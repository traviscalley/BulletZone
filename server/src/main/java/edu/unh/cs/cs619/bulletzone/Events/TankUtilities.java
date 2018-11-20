package edu.unh.cs.cs619.bulletzone.Events;

import java.util.Timer;
import java.util.TimerTask;

import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Wall;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.unh.cs.cs619.bulletzone.model.Direction.fromByte;
import static edu.unh.cs.cs619.bulletzone.model.Direction.toByte;

public abstract class TankUtilities
{
    /**
     * Bullet step time in milliseconds
     */
    private static final int BULLET_PERIOD = 200;

    /**
     * Bullet's impact effect [life]
     */
    private static final int BULLET_DAMAGE = 1;

    /**
     * Tank's default life [life]
     */
    private static final int TANK_LIFE = 100;

    private static int bulletDamage[] = {10, 30, 50};
    private static int bulletDelay[] = {500, 1000, 1500};
    private static int trackActiveBullets[] = {0, 0};
    private static final Timer timer = new Timer();
    private static final Object monitor = new Object();
    private static Game game;

    public static void setGame(Game game1) {game = game1;}

    public static boolean turn(long tankId, Direction direction)
            throws TankDoesNotExistException
    {
        synchronized (monitor) {
            checkNotNull(direction);

            // Find user
            Tank tank = game.getTanks().get(tankId);
            if (tank == null) {
                throw new TankDoesNotExistException(tankId);
            }

            long millis = System.currentTimeMillis();
            if(millis < tank.getLastMoveTime())
                return false;

            tank.setLastMoveTime(millis+tank.getAllowedMoveInterval());

            byte rotation;
            if(direction == Direction.Right)
                rotation = 2;
            else if(direction == Direction.Left) {
                if (tank.getDirection() == Direction.Up)
                    rotation = -6;
                else
                    rotation = -2;
            }
            else
                return false;

            byte dir = toByte(tank.getDirection());
            dir += rotation;
            dir %= 8;
            dir = (byte)Math.abs(dir);

            tank.setDirection(fromByte(dir));
            return true;
        }
    }

    public static boolean move(long tankId, Direction direction)
            throws TankDoesNotExistException {
        synchronized (monitor) {
            // Find tank

            Tank tank = game.getTanks().get(tankId);
            if (tank == null) {
                throw new TankDoesNotExistException(tankId);
            }

            int terrainType = tank.getParent().getEntity().getIntValue();

            switch (terrainType)
            {
                case 2000: // hill
                    tank.setAllowedMoveInterval(1000);
                    break;
                default:
                    tank.setAllowedMoveInterval(500);
            }

            long millis = System.currentTimeMillis();
            if(millis < tank.getLastMoveTime())
                return false;

            tank.setLastMoveTime(millis + tank.getAllowedMoveInterval());

            FieldHolder parent = tank.getParent();

            if(direction == Direction.Up)
                direction = tank.getDirection();
            else if(direction == Direction.Down)
                direction = fromByte((byte)((toByte(tank.getDirection()) + 4)%8));
            else
                return false;

            FieldHolder nextField = parent.getNeighbor(direction);
            checkNotNull(parent.getNeighbor(direction), "Neightbor is not available");

            boolean isCompleted;

            if (!nextField.isPresent())
            {
                parent.clearField();
                nextField.setFieldEntity(tank);
                tank.setParent(nextField);

                isCompleted = true;
            }
            else
            {
                isCompleted = false;
            }

            return isCompleted;
        }
    }

    public static boolean fire(long tankId, int bulletType)
            throws TankDoesNotExistException {
        synchronized (monitor) {

            // Find tank
            Tank tank = game.getTanks().get(tankId);
            if (tank == null) {
                throw new TankDoesNotExistException(tankId);
            }

            if(tank.getNumberOfBullets() >= tank.getAllowedNumberOfBullets())
                return false;

            long millis = System.currentTimeMillis();
            if(millis < tank.getLastFireTime()/*>tank.getAllowedFireInterval()*/){
                return false;
            }

            Direction direction = tank.getDirection();
            FieldHolder parent = tank.getParent();
            tank.setNumberOfBullets(tank.getNumberOfBullets() + 1);

            if(!(bulletType>=1 && bulletType<=3)) {
                System.out.println("Bullet type must be 1, 2 or 3, set to 1 by default.");
                bulletType = 1;
            }

            tank.setLastFireTime(millis + bulletDelay[bulletType - 1]);

            int bulletId=0;
            if(trackActiveBullets[0]==0){
                bulletId = 0;
                trackActiveBullets[0] = 1;
            }else if(trackActiveBullets[1]==0){
                bulletId = 1;
                trackActiveBullets[1] = 1;
            }

            // Create a new bullet to fire
            final Bullet bullet = new Bullet(tankId, direction, bulletDamage[bulletType-1]);
            // Set the same parent for the bullet.
            // This should be only a one way reference.
            bullet.setParent(parent);
            bullet.setBulletId(bulletId);

            // TODO make it nicer
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    synchronized (monitor) {
                        System.out.println("Active Bullet: "+tank.getNumberOfBullets()+"---- Bullet ID: "+bullet.getIntValue());
                        FieldHolder currentField = bullet.getParent();
                        Direction direction = bullet.getDirection();
                        FieldHolder nextField = currentField
                                .getNeighbor(direction);

                        // Is the bullet visible on the field?
                        boolean isVisible = currentField.isPresent()
                                && (currentField.getEntity() == bullet);

                        if (nextField.isPresent()) {
                            // Something is there, hit it
                            nextField.getEntity().hit(bullet.getDamage());

                            if ( nextField.getEntity() instanceof  Tank){
                                Tank t = (Tank) nextField.getEntity();
                                System.out.println("tank is hit, tank life: " + t.getLife());
                                if (t.getLife() <= 0 ){
                                    t.getParent().clearField();
                                    t.setParent(null);
                                    game.removeTank(t.getId());
                                }
                            }
                            else if ( nextField.getEntity() instanceof Wall){
                                Wall w = (Wall) nextField.getEntity();
                                if (w.getIntValue() >1000 && w.getIntValue()<=2000 ){
                                    game.getHolderGrid().get(w.getPos()).clearField();
                                }
                            }

                            if (isVisible) {
                                // Remove bullet from field
                                currentField.clearField();
                            }
                            trackActiveBullets[bullet.getBulletId()]=0;
                            tank.setNumberOfBullets(tank.getNumberOfBullets()-1);
                            cancel();

                        } else {
                            if (isVisible) {
                                // Remove bullet from field
                                currentField.clearField();
                            }

                            nextField.setFieldEntity(bullet);
                            bullet.setParent(nextField);
                        }
                    }
                }
            }, 0, BULLET_PERIOD);

            return true;
        }
    }
}
