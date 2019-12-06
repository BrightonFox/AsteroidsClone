package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.Path2D;
import asteroids.destroyers.AlienShipDestroyer;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.game.Controller;
import asteroids.game.Participant;

/**
 * Represent a Force Fields that surrounds the ship
 */
public class EnhancedForceField extends Participant implements AsteroidDestroyer, AlienShipDestroyer
{

    /** The outline of the force field */
    private Shape outline;

    /** Game controller */
    private Controller controller;

    /**
     * Constructs a forcefield at the specified coordinates
     */
    public EnhancedForceField (int x, int y, Controller controller)
    {
        setPosition(x, y);

        this.controller = controller;

        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(30, -20);
        poly.lineTo(20, -30);
        poly.lineTo(-20, -30);
        poly.lineTo(-30, -20);
        poly.lineTo(-30, 20);
        poly.lineTo(-20, 30);
        poly.lineTo(20, 30);
        poly.lineTo(30, 20);
        poly.lineTo(30, -20);
        poly.moveTo(35, -25);
        poly.lineTo(25, -35);
        poly.lineTo(-25, -35);
        poly.lineTo(-35, -25);
        poly.lineTo(-35, 25);
        poly.lineTo(-25, 35);
        poly.lineTo(25, 35);
        poly.lineTo(35, 25);
        poly.lineTo(35, -25);
        poly.closePath();
        this.outline = poly;
    }

    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    /** Force Field will follow the ship */
    public void move ()
    {
        if (controller.getShip() != null)
        {
            super.move();
            this.setPosition(controller.getShip().getX(), controller.getShip().getY());
        }
    }

    /** Force Field is invincible */
    @Override
    public void collidedWith (Participant p)
    {
    }

}
