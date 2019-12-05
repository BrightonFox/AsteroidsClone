package asteroids.participants;

import static asteroids.game.Constants.*;
import java.awt.Shape;
import java.awt.geom.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import asteroids.destroyers.*;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

/**
 * Represents ships
 */
public class EnhancedShip extends Ship implements AsteroidDestroyer, AlienShipDestroyer
{
    /** The outline of the ship */
    private Shape outline;

    /** Game controller */
    private Controller controller;

    /** boolean to control ship flame */
    public boolean flame;

    /**
     * Constructs a ship at the specified coordinates that is pointed in the given direction.
     */
    public EnhancedShip (int x, int y, double direction, Controller controller)
    {
        super(x, y, direction, controller);
        this.controller = controller;
        setPosition(x, y);
        setRotation(direction);

        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(21, 0);
        poly.lineTo(-21, 12);
        poly.lineTo(-14, 10);
        poly.lineTo(-14, -10);
        poly.lineTo(-21, -12);
        poly.closePath();
        outline = poly;
    }

    /**
     * Returns the x-coordinate of the point on the screen where the ship's nose is located.
     */
    public double getXNose ()
    {
        Point2D.Double point = new Point2D.Double(20, 0);
        transformPoint(point);
        return point.getX();
    }

    /**
     * Returns the x-coordinate of the point on the screen where the ship's nose is located.
     */
    public double getYNose ()
    {
        Point2D.Double point = new Point2D.Double(20, 0);
        transformPoint(point);
        return point.getY();
    }

    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    /**
     * Customizes the base move method by imposing friction
     */
    @Override
    public void move ()
    {
        applyFriction(SHIP_FRICTION);
        super.move();
    }

    /**
     * Turns right by Pi/16 radians
     */
    public void turnRight ()
    {
        rotate(Math.PI / 16);
    }

    /**
     * Turns left by Pi/16 radians
     */
    public void turnLeft ()
    {
        rotate(-Math.PI / 16);
    }

    /**
     * Accelerates by SHIP_ACCELERATION and creates flame animation
     */
    public void accelerate ()
    {
        accelerate(SHIP_ACCELERATION);
        
        //toggles flame to create flicker
        if (flame)
        {
            makeFlame();
        }        
        else
        {
            makeSmallFlame();
        }
        
        //plays acceleration sound
        controller.playClip(controller.getThrustClip());
    }
    
    /**
     * Sets ship back to original shape
     */
    public void makeNoFlame ()
    {
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(21, 0);
        poly.lineTo(-21, 12);
        poly.lineTo(-14, 10);
        poly.lineTo(-14, -10);
        poly.lineTo(-21, -12);
        poly.closePath();
        outline = poly;
    }

    /**
     * Creates small flame on back of ship
     */
    private void makeSmallFlame ()
    {
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(21, 0);
        poly.lineTo(-21, 12);
        poly.lineTo(-14, 10);
        poly.lineTo(-17.75, 8);
        poly.lineTo(-16.25, 6);
        poly.lineTo(-21.5, 4);
        poly.lineTo(-18.5, 2);
        poly.lineTo(-26, 0);
        poly.lineTo(-18.5, -2);
        poly.lineTo(-21.5, -4);
        poly.lineTo(-16.25, -6);
        poly.lineTo(-17.75, -8);
        poly.lineTo(-14, -10);
        poly.lineTo(-21, -12);
        poly.closePath();
        outline = poly;
        flame = true;
    }

    /**
     * Creates large flame on back of ship
     */
    private void makeFlame ()
    {
        Path2D.Double poly = new Path2D.Double();
        poly.moveTo(21, 0);
        poly.lineTo(-21, 12);
        poly.lineTo(-14, 10);
        poly.lineTo(-19, 8);
        poly.lineTo(-17, 6);
        poly.lineTo(-24, 4);
        poly.lineTo(-20, 2);
        poly.lineTo(-30, 0);
        poly.lineTo(-20, -2);
        poly.lineTo(-24, -4);
        poly.lineTo(-17, -6);
        poly.lineTo(-19, -8);
        poly.lineTo(-14, -10);
        poly.lineTo(-21, -12);
        poly.closePath();
        outline = poly;
        flame = false;
    }

    /**
     * When a Ship collides with a ShipDestroyer, it expires
     */
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof ShipDestroyer)
        {
            // Expire the ship from the game
            Participant.expire(this);

            // Tell the controller the ship was destroyed
            controller.shipDestroyed();
            
            // Spawn debris from destroyed ship
            this.controller.addParticipant(new Debris(this.getX(), this.getY(), 21));
            this.controller.addParticipant(new Debris(this.getX(), this.getY(), 21));
            this.controller.addParticipant(new Debris(this.getX(), this.getY(), 8));
        }
    }

    /**
     * This method is invoked when a ParticipantCountdownTimer completes its countdown.
     */
    @Override
    public void countdownComplete (Object payload)
    {
        // Give a burst of acceleration, then schedule another
        // burst for 200 msecs from now.
        if (payload.equals("move"))
        {
            accelerate();
            new ParticipantCountdownTimer(this, "move", 200);
        }
    }
    
    /**
     * Creates an audio clip from a sound file.
     */
    public Clip createClip (String soundFile)
    {
        // Opening the sound file this way will work no matter how the
        // project is exported. The only restriction is that the
        // sound files must be stored in a package.
        try (BufferedInputStream sound = new BufferedInputStream(getClass().getResourceAsStream(soundFile)))
        {
            // Create and return a Clip that will play a sound file. There are
            // various reasons that the creation attempt could fail. If it
            // fails, return null.
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(sound));
            return clip;
        }
        catch (LineUnavailableException e)
        {
            return null;
        }
        catch (IOException e)
        {
            return null;
        }
        catch (UnsupportedAudioFileException e)
        {
            return null;
        }
    }
}
