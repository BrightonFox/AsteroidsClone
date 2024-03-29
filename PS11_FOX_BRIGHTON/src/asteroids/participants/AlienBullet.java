package asteroids.participants;

import static asteroids.game.Constants.*;
import java.awt.Shape;
import java.awt.geom.*;
import asteroids.destroyers.*;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

/**
 * Represents bullet
 */
public class AlienBullet extends Participant implements ShipDestroyer, AsteroidDestroyer
{
    /** The outline of the bullet */
    private Shape outline;
    
    /** Game controller */
    @SuppressWarnings("unused")
    private Controller controller;
    
    /**
     * Constructs bullet at specified coordinates, headed in specified direction at BULLET_SPEED
     * Calls Countdown Complete after BULLET_DURATION milliseconds
     */
    public AlienBullet(int x, int y, double direction, Controller controller)
    {
        this.controller = controller;
        setPosition(x, y);
        setVelocity(BULLET_SPEED, direction);
        
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(0.5, 0);
        poly.lineTo(-0.5, -0.5);
        poly.lineTo(0, 0);
        poly.lineTo(-0.5, 0.5);
        poly.closePath();
        outline = poly;
        
        new ParticipantCountdownTimer(this, this, BULLET_DURATION);
    }
    
    @Override
    protected Shape getOutline ()
    {
        return outline;
    }
    
    @Override
    public void move ()
    {
        super.move();
    }
    
    /**
     * Expires bullet after BULLET_DURATION milliseconds
     */
    @Override
    public void countdownComplete(final Object payload)
    {
        Participant.expire(this);
    }
    
    /**
     * When an AlienBullet collides with an AlienShipDestroyer, it expires
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof AlienShipDestroyer)
        {
            // Expire the bullet from the game
            Participant.expire(this);
        }
    }
}