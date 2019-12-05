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
     * uses a boolean to check if it is time for the ship to move
     * unsure, need brighton's explanation
     */
    @Override
    public void move ()
    {
        super.move();
        if (changeDirection) // checks that it is time to move
        {
            changeDirection = false; // resets time to move variable
            if (Math.cos(this.getDirection()) > 0.0) // checks where ship is heading
            {
                this.setDirection(Constants.RANDOM.nextInt(3) - 1);
            }
            else
            {
                this.setDirection(Math.PI + Constants.RANDOM.nextInt(3) - 1.0);
            }
            new ParticipantCountdownTimer(this, "directionChange", 1500); // resets direction change
        }
    }

    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    /**
     * handles countdown's and constant setting/bullet firing speed
     */
    @Override
    public void countdownComplete (Object payload)
    {
        if (payload.equals("directionChange")) // handles zig zaging of alien ships
        {
            changeDirection = true; // is checked every frame and enters and if loop when true
        }
        else if (payload.equals("fire")) // handles firing of alien ships
        {
            fireAlienBullet();
        }
    }

    /**
     * handles all actions on ship collision and spawns debris at alien ships death location
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof AlienShipDestroyer)
        {
            // Inform the controller
            controller.alienShipDestroyed(this);

            // Spawn debris from destroyed ship
            controller.addParticipant(new Debris(this.getX(), this.getY(), 17));
            controller.addParticipant(new Debris(this.getX(), this.getY(), 17));
            controller.addParticipant(new Debris(this.getX(), this.getY(), 12));
            controller.addParticipant(new Debris(this.getX(), this.getY(), 12));
            controller.addParticipant(new Debris(this.getX(), this.getY(), 6));
            controller.addParticipant(new Debris(this.getX(), this.getY(), 6));
        }
    }

    /**
     * fires bullets that collide with asteroids and the player
     * uses different targetting methods for different sizes
     */
    public void fireAlienBullet () // controls bullet participant creation
    {
        if (controller.getShip() != null) // checks that ship exists so no null pointer exceptions are thrown
        {
            if (size == 0) // checks that the alien ship is "small" so the correct targeting method is used
            {
                // creates an alien bullet at the alien ships x and y coordinates, toward the player ship in degrees
                // with up
                // to 5 degree error in accuracy
                controller
                        .addParticipant(new AlienBullet((int) this.getX(), (int) this.getY(),
                                Math.toDegrees(Math.atan2(controller.getShip().getY() - this.getY(),
                                        controller.getShip().getX() - this.getX())) + RANDOM.nextInt(11) - 5,
                                controller));
                new ParticipantCountdownTimer(this, "fire", 1500); // sets timer to activate fireAlienBullet in
                                                                   // countdownComplete()
            }
            else if (size == 1)
            {
                controller.addParticipant(new AlienBullet((int) this.getX(), (int) this.getY(), // creates an alien
                                                                                                // bullet
                                                                                                // at the alien ships x
                                                                                                // and
                                                                                                // y coordinates, firing
                                                                                                // in
                                                                                                // random directions
                        2 * Math.PI * RANDOM.nextDouble(), controller));
                new ParticipantCountdownTimer(this, "fire", 1500); // sets timer to activate fireAlienBullet in
                                                                   // countdownComplete()
            }
        }
    }
}
