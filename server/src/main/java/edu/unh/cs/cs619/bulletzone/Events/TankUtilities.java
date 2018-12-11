package edu.unh.cs.cs619.bulletzone.Events;

import java.util.Timer;
import java.util.TimerTask;

import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.Coast;
import edu.unh.cs.cs619.bulletzone.model.DebrisField;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.Hill;
import edu.unh.cs.cs619.bulletzone.model.PlayableObject;
import edu.unh.cs.cs619.bulletzone.model.Soldier;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Wall;
import edu.unh.cs.cs619.bulletzone.model.Water;
import edu.unh.cs.cs619.bulletzone.powerup.Powerup;

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
            Tank tank = null;
            PlayableObject tmp = game.getPlayers().get(tankId);
            if (tmp instanceof Tank)
                tank = (Tank)tmp;

            if (tank == null) {
                throw new TankDoesNotExistException(tankId);
            }

            if (tank.getEjected() == true)
                return SoldierUtilities.turn(tankId, direction);

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
            FieldEntity prev = null;

            // Find user
            Tank tank = null;
            PlayableObject tmp = game.getPlayers().get(tankId);
            if (tmp instanceof Tank)
                tank = (Tank)tmp;

            if (tank == null) {
                throw new TankDoesNotExistException(tankId);
            }

            if (tank.getEjected())
                return SoldierUtilities.move(tankId, direction);

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

                isCompleted = true;
            }
            else if (nextField.getEntity() instanceof DebrisField ||
                     nextField.getEntity() instanceof Hill ||
                     nextField.getEntity() instanceof Coast)
            {
                parent.clearField();
                nextField.setFieldEntity(tank);

                isCompleted = true;
            }
            else if (nextField.isPresent() && nextField.getEntity() instanceof Powerup){
                Powerup p = (Powerup) nextField.getEntity();

                tank.addPowerup(p);

                parent.clearField();
                nextField.setFieldEntity(tank);

                isCompleted = true;
            }
            else
            {
                isCompleted = false;
            }

            return isCompleted;
        }
    }

    public static boolean ejectSoldier(long tankId)
            throws TankDoesNotExistException
    {
        synchronized (monitor) {
            // Find user
            Tank tank = null;
            PlayableObject tmp = game.getPlayers().get(tankId);
            if (tmp instanceof Tank)
                tank = (Tank)tmp;
            if (tank == null)
                throw new TankDoesNotExistException(tankId);

            return tank.eject();
        }
    }

    public static boolean ejectPowerup(long tankId)
            throws TankDoesNotExistException
    {
        synchronized (monitor) {
            // Find user
            Tank tank = null;
            PlayableObject tmp = game.getPlayers().get(tankId);
            if (tmp instanceof Tank)
                tank = (Tank)tmp;
            if (tank == null)
                throw new TankDoesNotExistException(tankId);

            return tank.remove1Powerup();
        }
    }

    public static boolean fire(long tankId, int bulletType)
            throws TankDoesNotExistException {
        synchronized (monitor) {

            // Find user
            Tank tank;
            PlayableObject tmp = game.getPlayers().get(tankId);
            if (tmp instanceof Tank)
                tank = (Tank)tmp;
            else
                tank = null;

            if (tank == null) {
                throw new TankDoesNotExistException(tankId);
            }

            if (tank.getEjected() == true)
                return SoldierUtilities.fire(tankId, bulletType);

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
                        int prev_dmg = bullet.getDamage();

                        System.out.println("Active Bullet: "+tank.getNumberOfBullets()+"---- Bullet ID: "+bullet.getIntValue());
                        FieldHolder currentField = bullet.getParent();
                        Direction direction = bullet.getDirection();
                        FieldHolder nextField = currentField
                                .getNeighbor(direction);

                        if (bullet.getDamage() != 0)
                            prev_dmg = bullet.getDamage();
                        bullet.setDamage(prev_dmg);

                        // Is the bullet visible on the field?
                        boolean isVisible = currentField.isPresent()
                                && (currentField.getEntity() == bullet);

                        if (nextField.isPresent()) {
                            // Something is there, hit it
                            nextField.getEntity().hit(bullet);

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
                        }
                    }
                }
            }, 0, BULLET_PERIOD);

            return true;
        }
    }
}
