package edu.unh.cs.cs619.bulletzone.Events;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Timer;

import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.Ship;
import edu.unh.cs.cs619.bulletzone.repository.InMemoryGameRepository;

import static org.junit.Assert.*;

public class ShipUtilitiesTest {

    @Mock
    Timer timer;
    Game game;

    @InjectMocks
    InMemoryGameRepository repo;

    @Test
    public void turn() {
        Ship ship = repo.joinShip("http://stman1.cs.unh.edu:6192/games");
    }

    @Test
    public void move() {
    }

    @Test
    public void fire() {
    }
}