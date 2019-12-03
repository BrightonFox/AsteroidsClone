package asteroids.participants;

import static asteroids.game.Constants.*;
import java.awt.Shape;
import java.awt.geom.*;
import asteroids.destroyers.*;
import asteroids.game.Constants;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

/**
 * Represents ships
 */
public class AlienShip extends Participant implements AsteroidDestroyer, ShipDestroyer
{
    /** The outline of the alien ship */
    private Shape outline;

    /** The size of the alien ship */
    private int size;

    /** Game controller */
    private Controller controller;

    private int changeDirection;

    /**
     * Spawns an alien ship outside of the screen of the given size
     */
    public AlienShip (int size, Controller controller)
    {
        this.controller = controller;

        this.size = size;

        changeDirection = 0;

        final Path2D.Double poly = new Path2D.Double();
        poly.moveTo(20.0, 0.0);
        poly.lineTo(10.0, 10.0);
        poly.lineTo(-10.0, 10.0);
        poly.lineTo(-20.0, 0.0);
        poly.lineTo(20.0, 0.0);
        poly.lineTo(-20.0, 0.0);
        poly.lineTo(-10.0, -10.0);
        poly.lineTo(10.0, -10.0);
        poly.lineTo(20.0, 0.0);
        poly.moveTo(-10.0, -10.0);
        poly.lineTo(-5.0, -20.0);
        poly.lineTo(5.0, -20.0);
        poly.lineTo(10.0, -10.0);
        poly.closePath();
        this.outline = poly;

        new ParticipantCountdownTimer(this, "directionChange", 1500);
        new ParticipantCountdownTimer(this, "fire", 1250);

        double scale = ALIENSHIP_SCALE[this.size];
        poly.transform(AffineTransform.getScaleInstance(scale, scale));

        this.setPosition(0, 0);
        this.setVelocity(2, Constants.RANDOM.nextInt(2) * Math.PI);
    }

    /**
     * Returns the x-coordinate of the point on the screen where the ship's center is located.
     */
    public double getX ()
    {
        Point2D.Double point = new Point2D.Double(0, 0);
        transformPoint(point);
        return point.getX();
    }

    /**
     * Returns the x-coordinate of the point on the screen where the ship's center is located.
     */
    public double getY ()
    {
        Point2D.Double point = new Point2D.Double(0, 0);
        transformPoint(point);
        return point.getY();
    }

    /**
     * 
     */
    @Override
    public void move ()
    {
        super.move();
        if (this.changeDirection == 1)
        {
            this.changeDirection = 0;
            if (Math.cos(this.getDirection()) > 0.0)
            {
                this.setDirection(Constants.RANDOM.nextInt(3) - 1);
            }
            else
            {
                this.setDirection(3.141592653589793 + Constants.RANDOM.nextInt(3) - 1.0);
            }
            new ParticipantCountdownTimer(this, "directionChange", 1500);
        }
    }

    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    @Override
    public void countdownComplete (Object payload)
    {
        if (payload.equals("directionChange"))
        {
            this.changeDirection = 1;
        }
        else if (payload.equals("fire"))
        {
            fireAlienBullet();
        }
    }

    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof AlienShipDestroyer)
        {
            Participant.expire(this);
        }
    }

    public void fireAlienBullet ()
    {
        AlienBullet alienBullet = new AlienBullet((int) this.getX(), (int) this.getY(), Constants.RANDOM.nextDouble() * 2 * Math.PI, this.controller);
        this.controller.addParticipant(alienBullet);
        new ParticipantCountdownTimer(this, "fire", 1500);
    }
}
