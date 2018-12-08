package edu.unh.cs.cs619.bulletzone.repository;

import com.google.common.base.Optional;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
//import java.util.Optional;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

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
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.Wall;
import edu.unh.cs.cs619.bulletzone.model.Water;
import edu.unh.cs.cs619.bulletzone.powerup.Powerup;
import edu.unh.cs.cs619.bulletzone.powerup.PowerupFactory;

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
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final Object monitor = new Object();
    private Game game = null;
    private static final Timer timer = new Timer();

    private Random random = new Random();

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

            tank = new Tank(tankId, Direction.Up, ip, game);
            tank.setLife(TANK_LIFE);

            Random random = new Random();
            int x;
            int y;

            // This may run for forever.. If there is no free space. XXX
            for (; ; ) {
                x = random.nextInt(FIELD_DIM);
                y = random.nextInt(FIELD_DIM);
                FieldHolder fieldElement = game.getHolderGrid().get(x * FIELD_DIM + y);
                FieldHolder terrainElement = game.getTerrainGrid().get(x * FIELD_DIM + y);

                if (!fieldElement.isPresent() && (!terrainElement.isPresent() || terrainElement.getEntity().tankSpawnable())) {
                    fieldElement.setFieldEntity(tank);
                    //tank.setParent(fieldElement);
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

            createFieldGrids(game);

            int seed = new Random().nextInt(256);
            loadMap(game, seed);

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    //theres a 1 in 20 chance of running every second, so avg 20s
                    if (random.nextInt(10) == 0)
                        addPowerUp(random.nextInt(256));
                }
            }, 0, 1000);

        }
    }

    private void addPowerUp(int target) {
        FieldHolder field = game.getHolderGrid().get(target);
        FieldHolder terrain = game.getTerrainGrid().get(target);
        if(!field.isPresent() && (!terrain.isPresent() || terrain.getEntity().powerupSpawnable())){
            FieldEntity idk = PowerupFactory.getInstance().makePowerup(random.nextInt(3)+6);
            field.setFieldEntity(idk);
        }
    }


    private void loadMap(Game game, int seed){
        final int coastNeed = 20;
        final int waterNeed = 20;
        final int hillNeed = 1;
        final int debrisNeed = 1;
        final int wallNeed = 7;

        int coasts = 0;
        int waters = 0;
        int hills = 0;
        int debriss = 0;
        int walls = 0;

        synchronized (this.monitor){
            ArrayList<FieldHolder> grid = game.getTerrainGrid();

            //make a small cross of water
            grid.get(seed).setFieldEntity(new Water());

            waters++;
            try {
                for(int i = 0; i < 8; i+=2) {
                    grid.get(seed).getNeighbor(Direction.fromByte((byte)i)).setFieldEntity(new Water());
                    waters++;
                }

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
                    else if(random.nextInt(3) < valueSurround(grid.get(cell), 300000000)){
                        grid.get(cell).setFieldEntity(new Water());
                        waters++;
                    }
                }
                //go through path and look at neighbors of grid
            }
            while(coasts < coastNeed)
            {
                for(int cell : path)
                {
                    if(grid.get(cell).isPresent())
                        continue;
                    //if(Math.abs(waterSurround(grid.get(cell)) - 4) < random.nextInt(3)) {
                    if(valueSurround(grid.get(cell), 300000000) >= 2 || random.nextInt(3) < valueSurround(grid.get(cell), 400000000)){
                        grid.get(cell).setFieldEntity(new Coast());
                        coasts++;
                    }
                }
            }

            ArrayList<FieldHolder> holderGrid = game.getHolderGrid();

            int firstWall = random.nextInt(256);
            while(grid.get(firstWall).isPresent() || holderGrid.get(firstWall).isPresent())
                firstWall = random.nextInt(256);


            holderGrid.get(firstWall).setFieldEntity(new Wall());
            walls++;

            while(walls < wallNeed) {
                for (int cell : path) {
                    //i'm not gonna make walls on the coast
                    if (grid.get(cell).isPresent() || holderGrid.get(cell).isPresent())
                        continue;

                    //generate walls
                    FieldHolder up = holderGrid.get(cell).getNeighbor(Direction.Up);
                    FieldHolder right = holderGrid.get(cell).getNeighbor(Direction.Right);

                    if ((up.isPresent() && up.getEntity() instanceof Wall) ||
                            (right.isPresent() && right.getEntity() instanceof Wall)){

                        if(random.nextBoolean())
                            holderGrid.get(cell).setFieldEntity(new Wall());
                        else
                            holderGrid.get(cell).setFieldEntity(new Wall(999));//not sure what value here
                        walls++;
                    }
                }
            }
        }
    }

    public int valueSurround(FieldHolder holder, int value){
        synchronized (this.monitor) {
            int sum = 0;

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

    private void createFieldGrids(Game game) {
        synchronized (this.monitor) {
            game.getHolderGrid().clear();
            game.getTerrainGrid().clear();
            for (int i = 0; i < FIELD_DIM * FIELD_DIM; i++) {
                game.getHolderGrid().add(new FieldHolder());
                game.getTerrainGrid().add(new FieldHolder());
            }

            FieldHolder targetHolder;
            FieldHolder rightHolder;
            FieldHolder downHolder;

            // Build connections
            for (int i = 0; i < FIELD_DIM; i++) {
                for (int j = 0; j < FIELD_DIM; j++) {
                    //player part
                    targetHolder = game.getHolderGrid().get(i * FIELD_DIM + j);
                    rightHolder = game.getHolderGrid().get(i * FIELD_DIM
                            + ((j + 1) % FIELD_DIM));
                    downHolder = game.getHolderGrid().get(((i + 1) % FIELD_DIM)
                            * FIELD_DIM + j);

                    targetHolder.addNeighbor(Direction.Right, rightHolder);
                    rightHolder.addNeighbor(Direction.Left, targetHolder);

                    targetHolder.addNeighbor(Direction.Down, downHolder);
                    downHolder.addNeighbor(Direction.Up, targetHolder);

                    //terrain part
                    targetHolder = game.getTerrainGrid().get(i * FIELD_DIM + j);
                    rightHolder = game.getTerrainGrid().get(i * FIELD_DIM
                            + ((j + 1) % FIELD_DIM));
                    downHolder = game.getTerrainGrid().get(((i + 1) % FIELD_DIM)
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
