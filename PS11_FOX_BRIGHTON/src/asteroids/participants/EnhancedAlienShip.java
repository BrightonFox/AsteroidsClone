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
public class EnhancedAlienShip extends AlienShip implements AsteroidDestroyer, ShipDestroyer
{
    /** The outline of the alien ship */
    private Shape outline;

    /** The size of the alien ship */
    private int size;

    /** Game controller */
    private Controller controller;

    private boolean changeDirection;

    private boolean followShip;
    
    private boolean speedUp;

    /**
     * Spawns an alien ship outside of the screen of the given size
     */
    public EnhancedAlienShip (int size, Controller controller)
    {
        super(size, controller);

        this.controller = controller;

        this.size = size;

        changeDirection = false;
        followShip = false;
        speedUp = false;

        final Path2D.Double poly = new Path2D.Double();
        // top section
        poly.moveTo(-10.0, -0.0);
        poly.lineTo(-10.0, -8.0);
        poly.lineTo(-8.0, -14.0);
        poly.lineTo(-4.0, -18.0);
        poly.lineTo(0.0, -19.0);
        poly.lineTo(4.0, -18.0);
        poly.lineTo(8.0, -14.0);
        poly.lineTo(10.0, -8.0);
        poly.lineTo(10.0, -0.0);
        // mid section
        poly.moveTo(20.0, 5.0);
        poly.lineTo(15.0, 0.0);
        poly.lineTo(-15.0, 0.0);
        poly.lineTo(-20.0, 5.0);
        poly.lineTo(-15.0, 10.0);
        poly.lineTo(15.0, 10.0);
        poly.lineTo(20.0, 5.0);
        // bottom section
        poly.moveTo(-10.0, 10.0);
        poly.lineTo(-8.0, 14.0);
        poly.lineTo(-4.0, 18.0);
        poly.lineTo(0.0, 19.0);
        poly.lineTo(4.0, 18.0);
        poly.lineTo(8.0, 14.0);
        poly.lineTo(10.0, 10.0);
        // alien
        poly.moveTo(-4.0, 0.0);
        poly.lineTo(-4.0, -4.0);
        poly.lineTo(-1.0, -6.0);
        poly.lineTo(-1.0, -7.0);
        poly.lineTo(-2.0, -8.0);
        poly.lineTo(-4.0, -12.0);
        poly.lineTo(-4.0, -14.0);
        poly.lineTo(-3.0, -16.0);
        poly.lineTo(0.0, -17.0);
        poly.lineTo(3.0, -16.0);
        poly.lineTo(4.0, -14.0);
        poly.lineTo(4.0, -12.0);
        poly.lineTo(2.0, -8.0);
        poly.lineTo(1.0, -7.0);
        poly.lineTo(1.0, -6.0);
        poly.lineTo(4.0, -4.0);
        poly.lineTo(4.0, 0.0);
        poly.closePath();
        this.outline = poly;

        new ParticipantCountdownTimer(this, "enhancedDirectionChange", 1500); // timer to set direction change constant to 1
        new ParticipantCountdownTimer(this, "followShip", 6000);
        new ParticipantCountdownTimer(this, "speedUp", 12000);
        new ParticipantCountdownTimer(this, "enhancedFire", 1250);

        double scale = ALIENSHIP_SCALE[this.size]; // determines scale based on ship size
        poly.transform(AffineTransform.getScaleInstance(scale, scale));

        this.setPosition(0, 0); // spawns alien ship off screen
        this.setVelocity(2, Constants.RANDOM.nextInt(2) * Math.PI); // randomly sets direction
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
        if (speedUp)
        {
            this.setSpeed(this.getSpeed() * 2);
            speedUp = false;
        }
        if (followShip)
        {
            if (controller.getShip() != null)
            {
                this.setDirection(Math.atan2(controller.getShip().getY() - this.getY(),
                        controller.getShip().getX() - this.getX()));
            }
        }
        if (changeDirection && !followShip)
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
        if (payload.equals("enhancedDirectionChange"))
        {
            changeDirection = true;
        }
        if (payload.equals("enhancedFire"))
        {
            fireEnhancedAlienBullet();
        }
        if (payload.equals("followShip"))
        {
            followShip = true;
        }
        if (payload.equals("speedUp"))
        {
            speedUp = true;
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

    public void fireEnhancedAlienBullet ()
    {
        if (controller.getShip() != null && size == 0)
        {
            AlienBullet alienBullet = new AlienBullet((int) this.getX(), (int) this.getY(),
                    Math.atan2(controller.getShip().getY() - this.getY(), controller.getShip().getX() - this.getX()),
                    controller);
            this.controller.addParticipant(alienBullet);
            new ParticipantCountdownTimer(this, "enhancedFire", 1500);
        }
        else if (controller.getShip() != null && size == 1)
        {
            AlienBullet alienBullet = new AlienBullet((int) this.getX(), (int) this.getY(),
                    2 * Math.PI * RANDOM.nextDouble(), controller);
            this.controller.addParticipant(alienBullet);
            new ParticipantCountdownTimer(this, "enhancedFire", 1500);
        }
    }
}
