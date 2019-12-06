package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.*;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

/**
 * Represents asteroids
 */
public class EnhancedPowerUp extends Participant
{
    /** The type of the PowerUp (0 = bulletTime, 1 = forceField, 2 = doubleScore) */
    private int type;

    /** The outline of the PowerUp */
    private Shape outline;

    /** The game controller */
    private Controller controller;

    /**
     * Creates a powerup of the given type (0 = bulletTime, 1 = forceField, 2 = doubleScore) and gives it 5 seconds to
     * be retrieved
     */
    public EnhancedPowerUp (int type, double x, double y, Controller controller)
    {
        // Create the PowerUp
        this.controller = controller;
        this.type = type;
        setPosition(x, y);
        createPowerUpOutline(type);

        new ParticipantCountdownTimer(this, this, 5000);
    }

    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    public int getPowerUpType ()
    {
        return type;
    }

    /**
     * Creates the outline of the powerup based on its type
     */
    private void createPowerUpOutline (int type)
    {
        // This will contain the outline
        Path2D.Double poly = new Path2D.Double();

        // Fill out according to type

        // Bullet Time
        if (type == 0)
        {
            poly.moveTo(-10, -15);
            poly.lineTo(-10, 10);
            poly.lineTo(0, 5);
            poly.lineTo(0, 0);
            poly.lineTo(-5, -5);
            poly.lineTo(0, -10);
            poly.lineTo(-10, -15);
            poly.moveTo(0, -10);
            poly.lineTo(10, -10);
            poly.lineTo(5, -10);
            poly.lineTo(5, 15);
            poly.moveTo(15, -15);
            poly.lineTo(15, 15);
            poly.lineTo(-15, 15);
            poly.lineTo(-15, -15);
            poly.lineTo(-15, -15);
            poly.closePath();
        }

        // Force Field
        else if (type == 1)
        {
            poly.moveTo(15, -5);
            poly.lineTo(5, -15);
            poly.lineTo(-5, -15);
            poly.lineTo(-15, -5);
            poly.lineTo(-15, 5);
            poly.lineTo(-5, 15);
            poly.lineTo(5, 15);
            poly.lineTo(15, 5);
            poly.lineTo(15, -5);
            poly.moveTo(20, -10);
            poly.lineTo(10, -20);
            poly.lineTo(-10, -20);
            poly.lineTo(-20, -10);
            poly.lineTo(-20, 10);
            poly.lineTo(-10, 20);
            poly.lineTo(10, 20);
            poly.lineTo(20, 10);
            poly.lineTo(20, -10);
            poly.closePath();
        }

        // Double Score
        else if (type == 2)
        {
            poly.moveTo(-15, -10);
            poly.lineTo(-10, -15);
            poly.lineTo(-5, -15);
            poly.lineTo(0, -10);
            poly.lineTo(-15, 15);
            poly.lineTo(0, 15);
            poly.moveTo(15, -10);
            poly.lineTo(0, 10);
            poly.moveTo(0, -10);
            poly.lineTo(15, 10);
            poly.closePath();
        }

        // Save the outline
        outline = poly;
    }

    @Override
    public void countdownComplete (final Object payload)
    {
        Participant.expire(this);
    }

    /**
     * When a ship collides with a powerup, it expires and applies the buff.
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof Ship)
        {
            Participant.expire(this);

            controller.applyPowerUp(this.type);
        }
    }
}