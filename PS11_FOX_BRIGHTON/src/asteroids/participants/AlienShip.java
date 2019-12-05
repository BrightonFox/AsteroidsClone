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

    private boolean changeDirection;

    /**
     * Spawns an alien ship outside of the screen of the given size
     */
    public AlienShip (int size, Controller controller)
    {
        this.controller = controller;

        this.size = size;

        changeDirection = false;

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

        new ParticipantCountdownTimer(this, "directionChange", 1500); // timer to set direction change constant to 1
        new ParticipantCountdownTimer(this, "fire", 1250);

        double scale = ALIENSHIP_SCALE[this.size]; // determines scale based on ship size
        poly.transform(AffineTransform.getScaleInstance(scale, scale));

        this.setPosition(0, 0); // spawns alien ship off screen
        this.setVelocity(2, Constants.RANDOM.nextInt(2) * Math.PI); // randomly sets velocity        
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
     * Returns the size of the alien ship
     */
    public int getSize ()
    {
        return size;
    }

    /**
     * 
     */
    @Override
    public void move ()
    {
        super.move();
        if (changeDirection)
        {
            changeDirection = false;
            if (Math.cos(this.getDirection()) > 0.0)
            {
                this.setDirection(Constants.RANDOM.nextInt(3) - 1);
            }
            else
            {
                this.setDirection(Math.PI + Constants.RANDOM.nextInt(3) - 1.0);
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
            changeDirection = true;
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
            // Inform the controller
            controller.alienShipDestroyed(this);

            // Spawn debris from destroyed ship
            this.controller.addParticipant(new Debris(this.getX(), this.getY(), 17));
            this.controller.addParticipant(new Debris(this.getX(), this.getY(), 17));
            this.controller.addParticipant(new Debris(this.getX(), this.getY(), 12));
            this.controller.addParticipant(new Debris(this.getX(), this.getY(), 12));
            this.controller.addParticipant(new Debris(this.getX(), this.getY(), 6));
            this.controller.addParticipant(new Debris(this.getX(), this.getY(), 6));
        }
    }

    public void fireAlienBullet ()
    {
        if (controller.getShip() != null && size == 0)
        {
            AlienBullet alienBullet = new AlienBullet((int) this.getX(), (int) this.getY(),
                    Math.atan2(controller.getShip().getY() - this.getY(), controller.getShip().getX() - this.getX()),
                    controller);
            this.controller.addParticipant(alienBullet);
            new ParticipantCountdownTimer(this, "fire", 1500);
        }
        else if (controller.getShip() != null && size == 1)
        {
            AlienBullet alienBullet = new AlienBullet((int) this.getX(), (int) this.getY(),
                    2 * Math.PI * RANDOM.nextDouble(), controller);
            this.controller.addParticipant(alienBullet);
            new ParticipantCountdownTimer(this, "fire", 1500);
        }
    }
}
