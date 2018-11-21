package edu.unh.cs.cs619.bulletzone.repository;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.Events.SoldierUtilities;
import edu.unh.cs.cs619.bulletzone.Events.TankUtilities;
import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.DebrisField;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.Hill;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Wall;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.unh.cs.cs619.bulletzone.model.Direction.fromByte;
import static edu.unh.cs.cs619.bulletzone.model.Direction.toByte;

@Component
public class InMemoryGameRepository implements GameRepository {

    /**
     * Field dimensions
     */
    private static final int FIELD_DIM = 16;

    /**
     * Tank's default life [life]
     */
    private static final int TANK_LIFE = 100;
    private final AtomicLong idGenerator = new AtomicLong();
    private final Object monitor = new Object();
    private Game game = null;

    @Override
    public Tank join(String ip) {
        synchronized (this.monitor) {
            Tank tank;
            if (game == null) {
                this.create();
            }

            if( (tank = game.getTank(ip)) != null){
                return tank;
            }

            Long tankId = this.idGenerator.getAndIncrement();

            tank = new Tank(tankId, Direction.Up, ip);
            tank.setLife(TANK_LIFE);

            Random random = new Random();
            int x;
            int y;

            // This may run for forever.. If there is no free space. XXX
            for (; ; ) {
                x = random.nextInt(FIELD_DIM);
                y = random.nextInt(FIELD_DIM);
                FieldHolder fieldElement = game.getHolderGrid().get(x * FIELD_DIM + y);
                if (!fieldElement.isPresent()) {
                    fieldElement.setFieldEntity(tank);
                    tank.setParent(fieldElement);
                    break;
                }
            }

            game.addTank(ip, tank);

            return tank;
        }
    }

    @Override
    public int[][] getGrid() {
        synchronized (this.monitor) {
            if (game == null) {
                this.create();
            }
        }
        return game.getGrid2D();
    }

    @Override
    public void leave(long tankId)
            throws TankDoesNotExistException {
        synchronized (this.monitor) {
            if (!this.game.getTanks().containsKey(tankId)) {
                throw new TankDoesNotExistException(tankId);
            }

            System.out.println("leave() called, tank ID: " + tankId);

            Tank tank = game.getTanks().get(tankId);
            FieldHolder parent = tank.getParent();
            parent.clearField();
            game.removeTank(tankId);
        }
    }

    public void create() {
        if (game != null) {
            return;
        }
        synchronized (this.monitor) {

            this.game = new Game();
            TankUtilities.setGame(game);
            SoldierUtilities.setGame(game);

            createFieldHolderGrid(game);

            // Test // TODO XXX Remove & integrate map loader
            game.getHolderGrid().get(1).setFieldEntity(new Wall());
            game.getHolderGrid().get(2).setFieldEntity(new Wall());
            game.getHolderGrid().get(3).setFieldEntity(new Wall());

            game.getHolderGrid().get(17).setFieldEntity(new Wall());
            game.getHolderGrid().get(33).setFieldEntity(new Wall(1500, 33));
            game.getHolderGrid().get(49).setFieldEntity(new Wall(1500, 49));
            game.getHolderGrid().get(65).setFieldEntity(new Wall(1500, 65));

            game.getHolderGrid().get(34).setFieldEntity(new Wall());
            game.getHolderGrid().get(66).setFieldEntity(new Wall(1500, 66));

            game.getHolderGrid().get(35).setFieldEntity(new Wall());
            game.getHolderGrid().get(51).setFieldEntity(new Wall());
            game.getHolderGrid().get(67).setFieldEntity(new Wall(1500, 67));

            game.getHolderGrid().get(5).setFieldEntity(new Wall());
            game.getHolderGrid().get(21).setFieldEntity(new Wall());
            game.getHolderGrid().get(37).setFieldEntity(new Wall());
            game.getHolderGrid().get(53).setFieldEntity(new Wall());
            game.getHolderGrid().get(69).setFieldEntity(new Wall(1500, 69));

            game.getHolderGrid().get(7).setFieldEntity(new Wall());
            game.getHolderGrid().get(23).setFieldEntity(new Wall());
            game.getHolderGrid().get(39).setFieldEntity(new Wall());
            game.getHolderGrid().get(71).setFieldEntity(new Wall(1500, 71));

            game.getHolderGrid().get(8).setFieldEntity(new Wall());
            game.getHolderGrid().get(40).setFieldEntity(new Wall());
            game.getHolderGrid().get(72).setFieldEntity(new Wall(1500, 72));

            game.getHolderGrid().get(9).setFieldEntity(new Wall());
            game.getHolderGrid().get(25).setFieldEntity(new Wall());
            game.getHolderGrid().get(41).setFieldEntity(new Wall());
            game.getHolderGrid().get(57).setFieldEntity(new Wall());
            game.getHolderGrid().get(73).setFieldEntity(new Wall());

            game.getHolderGrid().get(199).setFieldEntity(new DebrisField());
            game.getHolderGrid().get(200).setFieldEntity(new DebrisField());
            game.getHolderGrid().get(201).setFieldEntity(new DebrisField());

            game.getHolderGrid().get(105).setFieldEntity(new Hill());
            game.getHolderGrid().get(104).setFieldEntity(new Hill());
            game.getHolderGrid().get(103).setFieldEntity(new Hill());
            game.getHolderGrid().get(106).setFieldEntity(new Hill());
        }
    }

    private void createFieldHolderGrid(Game game) {
        synchronized (this.monitor) {
            game.getHolderGrid().clear();
            for (int i = 0; i < FIELD_DIM * FIELD_DIM; i++) {
                game.getHolderGrid().add(new FieldHolder());
            }

            FieldHolder targetHolder;
            FieldHolder rightHolder;
            FieldHolder downHolder;

            // Build connections
            for (int i = 0; i < FIELD_DIM; i++) {
                for (int j = 0; j < FIELD_DIM; j++) {
                    targetHolder = game.getHolderGrid().get(i * FIELD_DIM + j);
                    rightHolder = game.getHolderGrid().get(i * FIELD_DIM
                            + ((j + 1) % FIELD_DIM));
                    downHolder = game.getHolderGrid().get(((i + 1) % FIELD_DIM)
                            * FIELD_DIM + j);

                    targetHolder.addNeighbor(Direction.Right, rightHolder);
                    rightHolder.addNeighbor(Direction.Left, targetHolder);

                    targetHolder.addNeighbor(Direction.Down, downHolder);
                    downHolder.addNeighbor(Direction.Up, targetHolder);
                }
            }
        }
    }

}
