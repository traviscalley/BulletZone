package edu.unh.cs.cs619.bulletzone.Events;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.Soldier;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SoldierUtilitiesTest {

    @InjectMocks
    InMemoryGameRepository repo;
    Game game;

    @Test
    public void turn() throws Exception{
        Tank tank = repo.joinTank("http://stman1.cs.unh.edu:6192/games");
        Assert.assertTrue(tank.eject());

        Soldier s = tank.getSoldier();
        Assert.assertNotNull(s);
        Assert.assertTrue(s.getDirection() == Direction.Up);

        Assert.assertTrue(TankUtilities.turn(tank.getId(), Direction.Left));
        Assert.assertTrue(s.getDirection() == Direction.Left);
    }

    @Test
    public void move() throws Exception{
        Tank tank = repo.joinTank("http://stman1.cs.unh.edu:6192/games");
        Assert.assertTrue(tank.eject());

        Soldier s = tank.getSoldier();
        Assert.assertNotNull(s);

        Assert.assertTrue(TankUtilities.move(tank.getId(), Direction.Up));
        Assert.assertFalse(TankUtilities.move(tank.getId(), Direction.Up));
        Thread.sleep(1000);

        Assert.assertTrue(TankUtilities.move(tank.getId(), Direction.Up));
        Thread.sleep( 750);
        Assert.assertFalse(TankUtilities.move(tank.getId(), Direction.Up));
        Thread.sleep(250);
        Assert.assertTrue(TankUtilities.move(tank.getId(), Direction.Up));
    }

    @Test
    public void fire() throws Exception{
        Tank tank = repo.joinTank("http://stman1.cs.unh.edu:6192/games");
        Assert.assertTrue(tank.eject());

        Soldier s = tank.getSoldier();
        Assert.assertNotNull(s);

        Assert.assertTrue(TankUtilities.fire(tank.getId(), 1));
        Assert.assertFalse(TankUtilities.fire(tank.getId(), 1));

        /*
        this test fails for the expected fire interval of 250,
        but it passes for 500 (the tank fire interval).
        there may be an issue with how fire is called from TankUtilities.
         */
        Thread.sleep(250);
        Assert.assertTrue(TankUtilities.fire(tank.getId(), 1));
        /*Thread.sleep(200);
        Assert.assertFalse(TankUtilities.fire(tank.getId(), 1));
        Thread.sleep(50);
        Assert.assertTrue(TankUtilities.fire(tank.getId(), 1));*/
    }
}