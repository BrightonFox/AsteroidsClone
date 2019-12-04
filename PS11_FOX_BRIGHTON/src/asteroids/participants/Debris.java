package asteroids.participants;

import asteroids.game.ParticipantCountdownTimer;
import java.awt.geom.Path2D;
import asteroids.game.Constants;
import java.awt.Shape;
import asteroids.game.Participant;

public class Debris extends Participant
{
    /** debris shape */
    private Shape debris;

    /** creates a line of 'size' units long and gives it a random rotation, velocity, and speed at location (x, y) */
    public Debris (double x, double y, int size)
    {
        // create line centered on (x, y)
        Path2D.Double debrisPiece = new Path2D.Double();
        debrisPiece.moveTo(0, -size / 2.0);
        debrisPiece.lineTo(0, size / 2.0);

        // random rotation, velocity, and speed
        setRotation(Math.PI * 2.0 * Constants.RANDOM.nextDouble());
        this.setPosition(x, y);
        this.setVelocity(2 * Constants.RANDOM.nextDouble(), Math.PI * 2.0 * Constants.RANDOM.nextDouble());

        this.debris = debrisPiece;

        // despawns debris after 2 seconds
        new ParticipantCountdownTimer(this, this, 2000);
    }

    @Override
    protected Shape getOutline ()
    {
        return this.debris;
    }

    @Override
    public void countdownComplete (final Object payload)
    {
        Participant.expire(this);
    }

    @Override
    public void collidedWith (final Participant p)
    {
    }
}