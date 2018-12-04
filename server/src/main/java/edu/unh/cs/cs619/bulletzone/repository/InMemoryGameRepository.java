package edu.unh.cs.cs619.bulletzone.repository;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import edu.unh.cs.cs619.bulletzone.Events.PlayerUtilities;
import edu.unh.cs.cs619.bulletzone.Events.SoldierUtilities;
import edu.unh.cs.cs619.bulletzone.Events.TankUtilities;
import edu.unh.cs.cs619.bulletzone.model.Bullet;
import edu.unh.cs.cs619.bulletzone.model.Coast;
import edu.unh.cs.cs619.bulletzone.model.DebrisField;
import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.FieldEntity;
import edu.unh.cs.cs619.bulletzone.model.FieldHolder;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.Hill;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.PlayableObject;
import edu.unh.cs.cs619.bulletzone.model.Ship;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Wall;
import edu.unh.cs.cs619.bulletzone.model.Water;

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
    private static final int SHIP_LIFE = 100;
    private final AtomicLong idGenerator = new AtomicLong();
    private boolean isTank = false;
    private final Object monitor = new Object();
    private Game game = null;

    private Random random = new Random();

    public void setSelectBool(boolean isTank)
    {
        this.isTank = isTank;
    }

    public Ship joinShip(String ip)
    {
        synchronized (this.monitor) {
            Ship ship;
            if (game == null) {
                this.create();
            }

            if( (ship = game.getShip(ip)) != null){
                return ship;
            }

            Long id = this.idGenerator.getAndIncrement();

            ship = new Ship(id, Direction.Up, ip);
            ship.setLife(SHIP_LIFE);

            Random random = new Random();
            int x, y;

            // This may run for forever.. If there is no free space.
            for (; ; ) {
                x = random.nextInt(FIELD_DIM);
                y = random.nextInt(FIELD_DIM);
                FieldHolder fieldElement = game.getHolderGrid().get(x * FIELD_DIM + y);
                if (fieldElement != null && fieldElement.getEntity() instanceof Water) {
                    fieldElement.setFieldEntity(ship);
                    ship.setParent(fieldElement);
                    break;
                }
            }

            game.addShip(ip, ship);

            return ship;
        }
    }

    public Tank joinTank(String ip)
    {
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

            // This may run for forever.. If there is no free space.
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
    public PlayableObject join(String ip) {
        synchronized (this.monitor) {
            if (isTank)
                return joinTank(ip);
            else
                return joinShip(ip);
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
            if (!this.game.getTanks().containsKey(tankId) ||
                    !this.game.getShips().containsKey(tankId)) {
                throw new TankDoesNotExistException(tankId);
            }

            System.out.println("leave() called, tank ID: " + tankId);

            if (isTank) {
                Tank tank = game.getTanks().get(tankId);
                FieldHolder parent = tank.getParent();
                parent.clearField();
                game.removeTank(tankId);
            }
            else {
                Ship ship = game.getShips().get(tankId);
                FieldHolder parent = ship.getParent();
                parent.clearField();
                game.removeShip(tankId);
            }
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
            PlayerUtilities.setGame(game);

            createFieldHolderGrid(game);

            int seed = new Random().nextInt(256);
            loadMap(game, seed);

            // Test // TODO XXX Remove & integrate map loader
            /*game.getHolderGrid().get(1).setFieldEntity(new Wall());
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
            game.getHolderGrid().get(106).setFieldEntity(new Hill());*/
        }
    }

    private void loadMap(Game game, int seed){
        final int coastNeed = 20;
        final int waterNeed = 20;
        final int hillNeed = 1;
        final int debrisNeed = 1;

        int coasts = 0;
        int waters = 0;
        int hills = 0;
        int debriss = 0;


        synchronized (this.monitor){
            ArrayList<FieldHolder> grid = game.getHolderGrid();

            //make a small cross of water
            grid.get(seed).setFieldEntity(new Water());
            waters++;
            try {
                grid.get(seed).getNeighbor(Direction.Up).setFieldEntity(new Water());
                waters++;
                grid.get(seed).getNeighbor(Direction.Down).setFieldEntity(new Water());
                waters++;
                grid.get(seed).getNeighbor(Direction.Right).setFieldEntity(new Water());
                waters++;
                grid.get(seed).getNeighbor(Direction.Left).setFieldEntity(new Water());
                waters++;
            }
            catch (Exception e){}// neighbor was off the edge, ignore

            ArrayList<Integer> path = new ArrayList<>(256);
            for(int i = 0; i < 256; i++)
                path.add(i);
            Collections.shuffle(path);
            Collections.shuffle(path);

            //generate the map
            while(waters < waterNeed || hills < hillNeed || debriss < debrisNeed)
            {
                for(int cell : path)
                {
                    if(grid.get(cell).isPresent())
                        continue;

                    if(hills < 5 && 0.01 > Math.random()){
                        grid.get(cell).setFieldEntity(new Hill());
                        hills++;
                    }
                    else if(debriss < 5 && 0.01 > Math.random()){
                        grid.get(cell).setFieldEntity(new DebrisField());
                        debriss++;
                    }
                    else if(random.nextInt(3) < valueSurround(grid.get(cell), 5000)){
                        grid.get(cell).setFieldEntity(new Water());
                        waters++;
                    }
                }
                //go through path and look at neighbors of grid
            }
            //while(coasts < coastNeed)
            //{
                for(int cell : path)
                {
                    if(grid.get(cell).isPresent())
                        continue;
                    //if(Math.abs(waterSurround(grid.get(cell)) - 4) < random.nextInt(3)) {
                    if(valueSurround(grid.get(cell), 5000) >= 2 || random.nextInt(3) < valueSurround(grid.get(cell), 4000)){
                        grid.get(cell).setFieldEntity(new Coast());
                        coasts++;
                    }
                }
            //}
        }
    }

    public int valueSurround(FieldHolder holder, int value){
        synchronized (this.monitor) {
            int sum = 0;
            //sum += holder.getNeighbor().getNeighbor(Direction.Up).getNeighbor(Direction.Left).getEntity()

            try {
                sum += (holder.getNeighbor(Direction.Left).getEntity().getIntValue() == value) ? 1 : 0;
            }catch (Exception e){}
            try {
                sum += (holder.getNeighbor(Direction.Up).getEntity().getIntValue() == value)?1:0;
            }catch (Exception e){}
            try {
                sum += (holder.getNeighbor(Direction.Right).getEntity().getIntValue() == value)?1:0;
            }catch (Exception e){}
            try {
                sum += (holder.getNeighbor(Direction.Down).getEntity().getIntValue() == value)?1:0;
            }catch (Exception e){}
            try {

                sum += (holder.getNeighbor(Direction.Up).getNeighbor(Direction.Left).getEntity().getIntValue() == value)?1:0;
            }catch (Exception e){}
            try {
                sum += (holder.getNeighbor(Direction.Up).getNeighbor(Direction.Right).getEntity().getIntValue() == value)?1:0;
            }catch (Exception e){}
            try {

                sum += (holder.getNeighbor(Direction.Down).getNeighbor(Direction.Left).getEntity().getIntValue() == value)?1:0;
            }catch (Exception e){}
            try {
                sum += (holder.getNeighbor(Direction.Down).getNeighbor(Direction.Right).getEntity().getIntValue() == value)?1:0;
            }catch (Exception e){}


            return sum;
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
