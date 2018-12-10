package edu.unh.cs.cs619.bulletzone.Events;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.Game;
import edu.unh.cs.cs619.bulletzone.model.PlayableObject;
import edu.unh.cs.cs619.bulletzone.model.Ship;
import edu.unh.cs.cs619.bulletzone.model.Tank;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;

public abstract class PlayerUtilities
{
    private static Game game;
    public static void setGame(Game game1) {game = game1;}

    public static boolean turn(long id, Direction direction)
            throws TankDoesNotExistException
    {
        PlayableObject player = game.getPlayers().get(id);

        if (player instanceof Ship && player != null)
            return ShipUtilities.turn(id, direction);

        if (player instanceof Tank)
            return TankUtilities.turn(id, direction);

        return false;
    }

    public static boolean move(long id, Direction direction)
            throws TankDoesNotExistException
    {
        PlayableObject player = game.getPlayers().get(id);

        if (player instanceof Ship && player != null)
            return ShipUtilities.move(id, direction);

        if (player instanceof Tank)
            return TankUtilities.move(id, direction);

        return false;
    }

    public static boolean fire(long id, int bulletType)
            throws TankDoesNotExistException
    {
        PlayableObject player = game.getPlayers().get(id);

        if (player instanceof Ship && player != null)
            return ShipUtilities.fire(id, bulletType);

        if (player instanceof Tank)
            return TankUtilities.fire(id, bulletType);

        return false;
    }

    public static boolean ejectSoldier(long id)
            throws TankDoesNotExistException
    {
        PlayableObject player = game.getPlayers().get(id);

        if (player instanceof Tank && player != null)
            return TankUtilities.ejectSoldier(id);
        else
            return false;
    }
}
