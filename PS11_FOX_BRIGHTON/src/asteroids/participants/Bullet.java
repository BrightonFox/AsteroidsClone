package asteroids.participants;

import static asteroids.game.Constants.*;
import java.awt.Shape;
import java.awt.geom.*;
import asteroids.destroyers.*;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

/**
 * Represents ships
 */
public class Bullet extends Participant implements AsteroidDestroyer
{
    private Shape outline;
    
    private Controller controller;
    
    public Bullet(int x, int y, double direction, Controller controller)
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
        applyFriction(SHIP_FRICTION);
        super.move();
    }
    
    @Override
    public void countdownComplete(final Object payload)
    {
        controller.bulletCount--;
        
        Participant.expire(this);
    }
    
    @Override
    public void collidedWith (Participant p)
    { 
    }
}