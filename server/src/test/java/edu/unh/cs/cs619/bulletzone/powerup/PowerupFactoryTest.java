package edu.unh.cs.cs619.bulletzone.powerup;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import edu.unh.cs.cs619.bulletzone.model.FieldEntity;

import static org.junit.Assert.*;

public class PowerupFactoryTest {

    @Mock
    PowerupFactory factory;

    @Test
    public void makePowerupAntiGrav() {
        factory = PowerupFactory.getInstance();
        FieldEntity p = factory.makePowerup(6);
        Assert.assertTrue(p.getIntValue() == 60000000);
    }

    @Test
    public void makePowerupFusionReactor() {
        factory = PowerupFactory.getInstance();
        FieldEntity p = factory.makePowerup(7);
        Assert.assertTrue(p.getIntValue() == 70000000);
    }

    @Test
    public void makePowerupPowerRack() {
        factory = PowerupFactory.getInstance();
        FieldEntity p = factory.makePowerup(8);
        Assert.assertTrue(p.getIntValue() == 80000000);
    }
}