package edu.unh.cs.cs619.bulletzone.Events;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class TankUtilitiesTest {

    @InjectMocks
    InMemoryGameRepository repo;
    Game game;

    @Test
    public void setGame() {
    }

    @Test
    public void turn() throws Exception{
        Tank tank = repo.join("http://stman1.cs.unh.edu:6192/games");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertTrue(TankUtilities.turn(tank.getId(), Direction.Right));
        Assert.assertTrue(tank.getDirection() == Direction.Right);
        Assert.assertFalse(TankUtilities.turn(tank.getId(), Direction.Right));
        Thread.sleep(500);
        Assert.assertTrue(TankUtilities.turn(tank.getId(), Direction.Right));

        //short interval
        Thread.sleep(400);
        Assert.assertFalse(TankUtilities.turn(tank.getId(), Direction.Right));
        Thread.sleep(100);
        Assert.assertTrue(TankUtilities.turn(tank.getId(), Direction.Right));
    }

    @Test
    public void move() throws Exception{
        Tank tank = repo.join("http://stman1.cs.unh.edu:6192/games");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());

        Assert.assertTrue(TankUtilities.move(tank.getId(), Direction.Up));
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertFalse(TankUtilities.move(tank.getId(), Direction.Up));
        Thread.sleep( 500 );
        Assert.assertTrue(TankUtilities.move(tank.getId(), Direction.Up));

        //short interval
        Thread.sleep( 400 );
        Assert.assertFalse(TankUtilities.move(tank.getId(), Direction.Up));
        Thread.sleep(100);
        Assert.assertTrue(TankUtilities.move(tank.getId(), Direction.Up));
    }

    @Test
    public void ejectSoldier() {
    }

    @Test
    public void ejectPowerup() {
    }

    @Test
    public void fire() throws Exception {
        Tank tank = repo.join("http://stman1.cs.unh.edu:6192/games");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getParent());

        Assert.assertTrue(TankUtilities.fire(tank.getId(), 1));
        //test interval that is too short
        Thread.sleep(400);
        Assert.assertFalse(TankUtilities.fire(tank.getId(), 1));
        //remaining wait time until valid fire
        Thread.sleep( 100 );
        Assert.assertTrue(TankUtilities.fire(tank.getId(), 1));



    }
}