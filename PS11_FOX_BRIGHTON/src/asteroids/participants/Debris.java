package asteroids.participants;

import asteroids.game.ParticipantCountdownTimer;
import java.awt.geom.Path2D;
import asteroids.game.Constants;
import java.awt.Shape;
import asteroids.game.Participant;

public class Debris extends Participant
{
    private Shape debris;

    public Debris (double x, double y, int size)
    {
        Path2D.Double debrisPiece = new Path2D.Double();
        debrisPiece.moveTo(0, -size / 2.0);
        debrisPiece.lineTo(0, size / 2.0);

        this.setPosition(x, y);
        this.setVelocity(2 * Constants.RANDOM.nextDouble(), Math.PI * 2.0 * Constants.RANDOM.nextDouble());

        this.debris = debrisPiece;

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