package asteroids.participants;

import asteroids.destroyers.*;
import asteroids.game.Controller;
import asteroids.game.Participant;

/**
 * Represents bullet
 */
public class EnhancedBullet extends Bullet implements AsteroidDestroyer, AlienShipDestroyer
{
    /**
     * Constructs bullet at specified coordinates, headed in specified direction at BULLET_SPEED Calls Countdown
     * Complete after BULLET_DURATION milliseconds
     */
    public EnhancedBullet (int x, int y, double direction, Controller controller)
    {
        super(x, y, direction, controller);
    }

    /**
     * Expires bullet after BULLET_DURATION milliseconds
     */
    @Override
    public void countdownComplete (final Object payload)
    {
        Participant.expire(this);
    }

    /**
     * When a Bullet collides with a ShipDestroyer, it expires
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof ShipDestroyer)
        {
            // Expire the bullet from the game
            Participant.expire(this);
        }
    }
}