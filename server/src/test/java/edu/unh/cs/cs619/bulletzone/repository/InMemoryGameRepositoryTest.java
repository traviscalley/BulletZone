package edu.unh.cs.cs619.bulletzone.repository;

import edu.unh.cs.cs619.bulletzone.Events.TankUtilities;
import edu.unh.cs.cs619.bulletzone.model.Game;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.PlayableObject;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryGameRepositoryTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @InjectMocks
    InMemoryGameRepository repo;
    Game game;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testJoin() throws Exception {
        PlayableObject tank = repo.join("http://stman1.cs.unh.edu:6192/games");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());
    }

    @Test
    public void testTurn() throws Exception {
        PlayableObject tank = repo.join("http://stman1.cs.unh.edu:6192/games");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());
        Assert.assertTrue(TankUtilities.turn(tank.getId(), Direction.Right));
        Assert.assertTrue(tank.getDirection() == Direction.Right);

//        thrown.expect(TankDoesNotExistException.class);
//        thrown.expectMessage("Tank '1000' does not exist");
//        repo.turn(1000, Direction.Right);
    }

    @Test
    public void testMove() throws Exception {
        PlayableObject tank = repo.join("http://stman1.cs.unh.edu:6192/games");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getDirection());
        Assert.assertTrue(tank.getDirection() == Direction.Up);
        Assert.assertNotNull(tank.getParent());
        Assert.assertTrue(TankUtilities.move(tank.getId(), Direction.Up));
        Assert.assertTrue(tank.getDirection() == Direction.Up);

    }

    @Test
    public void testFire() throws Exception {
        PlayableObject tank = repo.join("http://stman1.cs.unh.edu:6192/games");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getParent());
        Assert.assertTrue(TankUtilities.fire(tank.getId(), 1));

    }

    @Test
    public void testLeave() throws Exception {
        PlayableObject tank = repo.join("http://stman1.cs.unh.edu:6192/games");
        Assert.assertNotNull(tank);
        Assert.assertTrue(tank.getId() >= 0);
        Assert.assertNotNull(tank.getParent());
        int id = (int) tank.getId();
    }
}