package edu.unh.cs.cs619.bulletzone.Events;

import java.util.Timer;
import java.util.TimerTask;

import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.Soldier;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Wall;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.unh.cs.cs619.bulletzone.model.Direction.fromByte;
import static edu.unh.cs.cs619.bulletzone.model.Direction.toByte;

public abstract class SoldierUtilities
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
    {
        synchronized (monitor) {
            checkNotNull(direction);

            // Find user
            Soldier soldier = game.getTanks().get(tankId).getSoldier();

            byte rotation;
            if(direction == Direction.Right)
                rotation = 2;
            else if(direction == Direction.Left) {
                if (soldier.getDirection() == Direction.Up)
                    rotation = -6;
                else
                    rotation = -2;
            }
            else
                return false;

            byte dir = toByte(soldier.getDirection());
            dir += rotation;
            dir %= 8;
            dir = (byte)Math.abs(dir);

            soldier.setDirection(fromByte(dir));
            return true;
        }
    }

    public static boolean move(long tankId, Direction direction)
    {
        synchronized (monitor) {
            // Find tank

            Soldier soldier = game.getTanks().get(tankId).getSoldier();

            int terrainType = soldier.getParent().getEntity().getIntValue();

            long millis = System.currentTimeMillis();
            if(millis < soldier.getLastMoveTime())
                return false;

            soldier.setLastMoveTime(millis + soldier.getAllowedMoveInterval());

            FieldHolder parent = soldier.getParent();

            if(direction == Direction.Up)
                direction = soldier.getDirection();
            else if(direction == Direction.Down)
                direction = fromByte((byte)((toByte(soldier.getDirection()) + 4)%8));
            else
                return false;

            FieldHolder nextField = parent.getNeighbor(direction);
            checkNotNull(parent.getNeighbor(direction), "Neightbor is not available");

            boolean isCompleted;

            if (!nextField.isPresent())
            {
                parent.clearField();
                nextField.setFieldEntity(soldier);

                isCompleted = true;
            }
            else if (nextField.isPresent() && nextField.getEntity() instanceof Tank)
            {
                Tank cur = (Tank) nextField.getEntity();
                if (cur.getId() == soldier.getId())
                {
                    parent.clearField();
                    cur.reenter();

                    isCompleted = true;
                }
                else
                    isCompleted = false;
            }
            else
            {
                isCompleted = false;
            }

            return isCompleted;
        }
    }
    public static boolean fire(long tankId, int bulletType)
    {
        synchronized (monitor) {

            // Find tank
            Soldier soldier = game.getTanks().get(tankId).getSoldier();

            if(soldier.getNumberOfBullets() >= soldier.getAllowedNumberOfBullets())
                return false;

            long millis = System.currentTimeMillis();
            if(millis < soldier.getLastFireTime()/*>tank.getAllowedFireInterval()*/){
                return false;
            }

            Direction direction = soldier.getDirection();
            FieldHolder parent = soldier.getParent();
            soldier.setNumberOfBullets(soldier.getNumberOfBullets() + 1);

            if(!(bulletType>=1 && bulletType<=3)) {
                System.out.println("Bullet type must be 1, 2 or 3, set to 1 by default.");
                bulletType = 1;
            }

            soldier.setLastFireTime(millis + bulletDelay[bulletType - 1]);

            int bulletId=0;
            if(trackActiveBullets[0]==0){
                bulletId = 0;
                trackActiveBullets[0] = 1;
            }else if(trackActiveBullets[1]==0){
                bulletId = 1;
                trackActiveBullets[1] = 1;
            }

            // Create a new bullet to fire
            final Bullet bullet = new Bullet(tankId, direction, 5);
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

                        System.out.println("Active Bullet: "+soldier.getNumberOfBullets()+"---- Bullet ID: "+bullet.getIntValue());
                        FieldHolder currentField = bullet.getParent();
                        Direction direction = bullet.getDirection();
                        FieldHolder nextField = currentField.getNeighbor(direction);

                        if (bullet.getDamage() != 0)
                            prev_dmg = bullet.getDamage();
                        bullet.setDamage(prev_dmg);

                        // Is the bullet visible on the field?
                        boolean isVisible = currentField.isPresent()
                                && (currentField.getEntity() == bullet);

                        if (nextField.isPresent()) {
                            // Something is there, hit it
                            nextField.getEntity().hit(bullet.getDamage());



                            if ( nextField.getEntity() instanceof  Tank)
                            {
                                Tank t = (Tank) nextField.getEntity();

                                if (t.getId() == bullet.getBulletId()) {
                                    bullet.setDamage(0);
                                }

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
                            else if (nextField.getEntity() instanceof Soldier)
                            {
                                Soldier s = (Soldier) nextField.getEntity();

                                if (s.getId() == bullet.getBulletId()) {
                                    bullet.setDamage(0);
                                }

                                if (s.getLife() <= 0)
                                {
                                    s.getParent().clearField();
                                    s.setParent(null);
                                    game.removeTank(s.getId());
                                }
                            }

                            if (isVisible) {
                                // Remove bullet from field
                                currentField.clearField();
                            }
                            trackActiveBullets[bullet.getBulletId()]=0;
                            soldier.setNumberOfBullets(soldier.getNumberOfBullets()-1);
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
