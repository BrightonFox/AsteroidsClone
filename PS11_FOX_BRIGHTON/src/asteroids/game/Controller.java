package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import asteroids.participants.AlienShip;
import asteroids.participants.Asteroid;
import asteroids.participants.Bullet;
import asteroids.participants.EnhancedAlienShip;
import asteroids.participants.EnhancedBullet;
import asteroids.participants.EnhancedForceField;
import asteroids.participants.EnhancedShip;
import asteroids.participants.EnhancedPowerUp;
import asteroids.participants.Ship;

/**
 * Controls a game of Asteroids.
 */
public class Controller implements KeyListener, ActionListener, Iterable<Participant>
{
    private String version;

    /** The state of all the Participants */
    private ParticipantState pstate;

    /** The ship (if one is active) or null (otherwise) */
    private Ship ship;

    /** When this timer goes off, it is time to refresh the animation */
    private Timer refreshTimer;

    /** When this timer goes off, it is time to spawn an alien ship **/
    private Timer alienShipSpawnTimer;

    /**
     * The time at which a transition to a new stage of the game should be made. A transition is scheduled a few seconds
     * in the future to give the user time to see what has happened before doing something like going to a new level or
     * resetting the current level.
     */
    private long transitionTime;

    /** Number of lives left */
    private int lives;

    /** The game display */
    private Display display;

    /** number of bullets on screen */
    private int bulletCount = 0;

    /** Status of right key */
    private boolean rightPressed;

    /** Status of left key */
    private boolean leftPressed;

    /** Status of up key */
    private boolean upPressed;

    /** Status of space bar */
    private boolean spacePressed;

    /** Current 'level' of game */
    private int level;

    /** player's current score */
    private int score;

    /** Used to play background music */
    private int beatSwitch;

    /** sound file for shooting */
    private Clip fireClip;

    /** sound file for thrust */
    private Clip thrustClip;

    /** sound file for ship destruction */
    private Clip bangShipClip;

    /** sound file for big alien */
    private Clip saucerBigClip;

    /** sound file for small alien */
    private Clip saucerSmallClip;

    /** sound file for alien destruction */
    private Clip bangAlienShipClip;

    /** sound file for large asteroid destruction */
    private Clip bangLargeClip;

    /** sound file for medium asteroid destruction */
    private Clip bangMediumClip;

    /** sound file for small asteroid destruction */
    private Clip bangSmallClip;

    /** sound file for background music */
    private Clip beat1Clip;

    /** sound file for background music */
    private Clip beat2Clip;

    /** timer for background music */
    private Timer beatTimer;

    /** enhanced version extra life reward memory */
    private int enhancedExtraLifeReward;

    /** enhanced version high score memory */
    private int enhancedHiScore;

    /** enhanced version powerUp */
    private EnhancedPowerUp enhancedPowerUp;

    /** enhanced version power up spawn timer */
    private Timer enhancedPowerUpSpawnTimer;

    /** enhanced version bullet time powerup */
    private boolean enhancedBulletTime;

    /** enhanced version bullet time powerup timer */
    private Timer enhancedBulletTimeTimer;

    /** enhanced version force field powerup timer */
    private Timer enhancedForceFieldTimer;

    /** enhanced version double score powerup */
    private boolean enhancedDoubleScore;

    /** enhanced version double score powerup timer */
    private Timer enhancedDoubleScoreTimer;

    /** enhanced version force field object */
    private EnhancedForceField forceField;

    /**
     * Constructs a controller to coordinate the game and screen
     */
    public Controller (int version)
    {
        // Set game type
        if (version == 1)
        {
            this.version = "Enhanced";
        }
        else
        {
            this.version = "Classic";
        }

        // Initialize the ParticipantState
        pstate = new ParticipantState();

        // Set up the refresh timer.
        refreshTimer = new Timer(FRAME_INTERVAL, this);

        // Set up the alienship spawn timer
        alienShipSpawnTimer = new Timer(RANDOM.nextInt(5001) + 5000, this);

        // Set up background music
        beatTimer = new Timer(1000, this);
        beatSwitch = 0;

        // Clear the transitionTime
        transitionTime = Long.MAX_VALUE;

        // Record the display object
        display = new Display(this);

        // Bring up the splash screen and start the timers
        splashScreen();
        display.setVisible(true);
        refreshTimer.start();
        alienShipSpawnTimer.start();
        beatTimer.start();

        // stores sound files in prepared variables
        fireClip = createClip("/sounds/fire.wav");
        thrustClip = createClip("/sounds/thrust.wav");
        beat1Clip = createClip("/sounds/beat1.wav");
        beat2Clip = createClip("/sounds/beat2.wav");
        bangShipClip = createClip("/sounds/bangShip.wav");
        bangSmallClip = createClip("/sounds/bangSmall.wav");
        bangMediumClip = createClip("/sounds/bangMedium.wav");
        bangLargeClip = createClip("/sounds/bangLarge.wav");
        saucerBigClip = createClip("/sounds/saucerBig.wav");
        saucerSmallClip = createClip("/sounds/saucerSmall.wav");
        bangAlienShipClip = createClip("/sounds/bangAlienShip.wav");

        // Set Up enhanced version
        if (version == 1)
        {
            enhancedHiScore = 0;

            // Set up powerup spawn system
            enhancedPowerUpSpawnTimer = new Timer(RANDOM.nextInt(5001) + 7000, this);
            enhancedPowerUpSpawnTimer.start();
        }
    }

    /**
     * This makes it possible to use an enhanced for loop to iterate through the Participants being managed by a
     * Controller.
     */
    @Override
    public Iterator<Participant> iterator ()
    {
        return pstate.iterator();
    }

    /**
     * Returns the ship, or null if there isn't one
     */
    public Ship getShip ()
    {
        return ship;
    }

    /**
     * Configures the game screen to display the splash screen
     */
    private void splashScreen ()
    {
        // Clear the screen, reset the level and score, and display the legend
        clear();
        level = 1;
        score = 0;
        display.setLegend("Asteroids");

        // Place four asteroids near the corners of the screen.
        placeAsteroids(4);
    }

    /**
     * The game is over. Displays a message to that effect.
     */
    private void finalScreen ()
    {
        if (version.equals("Enhanced"))
        {
            if (score > enhancedHiScore)
            {
                enhancedHiScore = score;
            }
        }
        display.setLegend(GAME_OVER);
        display.removeKeyListener(this);
    }

    /**
     * Place a new ship in the center of the screen. Remove any existing ship first.
     */
    private void placeShip ()
    {
        // Place a new ship
        Participant.expire(ship);
        if (getVersion().equals("Enhanced"))
        {
            ship = new EnhancedShip(SIZE / 2, SIZE / 2, -Math.PI / 2, this);
        }
        else
        {
            ship = new Ship(SIZE / 2, SIZE / 2, -Math.PI / 2, this);
        }
        addParticipant(ship);
        display.setLegend("");
    }

    /**
     * Place a new Alien ship off screen
     */
    private void placeAlienShip ()
    {
        // Place an alien ship based on level every 5-10 seconds
        if (getVersion().equals("Enhanced"))
        {
            if (level == 2)
            {
                addParticipant(new EnhancedAlienShip(1, this)); // spawns medium ship

                // loop "saucerBig" sound
                loopClip(saucerBigClip);

                alienShipSpawnTimer = new Timer(RANDOM.nextInt(5001) + 5000, this); // resets spawn timer to have
                                                                                    // truly
                                                                                    // random spawn times
            }
            else if (level >= 3) // spawns small ship
            {
                addParticipant(new EnhancedAlienShip(0, this));

                // loop "saucerSmall" sound
                loopClip(saucerSmallClip);

                alienShipSpawnTimer = new Timer(RANDOM.nextInt(5001) + 5000, this); // resets spawn timer to have
                                                                                    // truly random spawn times
            }
        }

        else
        {
            if (level == 2)
            {
                addParticipant(new AlienShip(1, this)); // spawns medium ship

                // loop "saucerBig" sound
                loopClip(saucerBigClip);

                alienShipSpawnTimer = new Timer(RANDOM.nextInt(5001) + 5000, this); // resets spawn timer to have
                                                                                    // truly
                                                                                    // random spawn times
            }
            else if (level >= 3) // spawns small ship
            {
                addParticipant(new AlienShip(0, this));

                // loop "saucerSmall" sound
                loopClip(saucerSmallClip);

                alienShipSpawnTimer = new Timer(RANDOM.nextInt(5001) + 5000, this); // resets spawn timer to have
                                                                                    // truly
                                                                                    // random spawn times
            }
        }
    }

    /**
     * Place a new power-up randomly on the screen. Remove any existing power-up first.
     */
    private void placePowerUp ()
    {
        if (version.equals("Enhanced"))
        {
            enhancedPowerUp = new EnhancedPowerUp(RANDOM.nextInt(3), SIZE * RANDOM.nextDouble(),
                    SIZE * RANDOM.nextDouble(), this);
            enhancedPowerUpSpawnTimer.restart();
            addParticipant(enhancedPowerUp);
        }
    }

    /**
     * Creates bullet object headed in direction of ship
     */
    public void fireBullet ()
    {
        // limit number of bullets on screen to 8
        if (ship != null && bulletCount <= BULLET_LIMIT)
        {
            if (enhancedBulletTime)
            {
                addParticipant(
                        new EnhancedBullet((int) ship.getXNose(), (int) ship.getYNose(), ship.getRotation(), this));
            }
            else
            {
                bulletNumAdjust(1);
                addParticipant(new Bullet((int) ship.getXNose(), (int) ship.getYNose(), ship.getRotation(), this));
            }

            // play "fire" sound
            playClip(fireClip);
        }
    }

    /**
     * Place an asteroids near corners of the screen. Gives them a random velocity and rotation.
     */
    private void placeAsteroids (int numOfAsteroids)
    {
        for (int i = 0; i < numOfAsteroids; i++)
        {
            if (i % 4 == 0)
            {
                addParticipant(new Asteroid(RANDOM.nextInt(4), 2, EDGE_OFFSET + RANDOM.nextInt(50) - 25,
                        EDGE_OFFSET + RANDOM.nextInt(50) - 25, this));
            }
            if (i % 4 == 1)
            {
                addParticipant(new Asteroid(RANDOM.nextInt(4), 2, SIZE - EDGE_OFFSET + RANDOM.nextInt(50) - 25,
                        EDGE_OFFSET + RANDOM.nextInt(50) - 25, this));
            }
            if (i % 4 == 2)
            {
                addParticipant(new Asteroid(RANDOM.nextInt(4), 2, EDGE_OFFSET + RANDOM.nextInt(50) - 25,
                        SIZE - EDGE_OFFSET + RANDOM.nextInt(50) - 25, this));
            }
            if (i % 4 == 3)
            {
                addParticipant(new Asteroid(RANDOM.nextInt(4), 2, SIZE - EDGE_OFFSET + RANDOM.nextInt(50) - 25,
                        SIZE - EDGE_OFFSET + RANDOM.nextInt(50) - 25, this));
            }
        }
    }

    /**
     * Clears the screen so that nothing is displayed
     */
    private void clear ()
    {
        pstate.clear();
        display.setLegend("");
        ship = null;
    }

    /**
     * Sets things up and begins a new game.
     */
    private void initialScreen ()
    {
        // Clear the screen
        clear();

        // Place asteroids
        placeAsteroids(4);

        // Place the ship
        placeShip();

        // Reset statistics
        lives = 3;
        level = 1;
        score = 0;

        display.setLives(lives);
        display.setLevel(level);
        display.setScore(score);

        // Start listening to events (but don't listen twice)
        display.removeKeyListener(this);
        display.addKeyListener(this);

        // Give focus to the game screen
        display.requestFocusInWindow();
    }

    /**
     * Sets things up for a new level
     */
    private void nextLevel ()
    {
        clear(); // clears participants from screen

        // end looping sounds
        saucerBigClip.stop();
        saucerSmallClip.stop();

        level++; // increments level
        display.setLevel(level); // sets new level

        bulletCount = 0; // resets bullet amount counter

        placeAsteroids(level + 3); // spawns adequate number of asteroids
        placeShip(); // places player ship

        alienShipSpawnTimer.start(); // to catch when a ship is still alive and a level increases

        beatTimer = new Timer(1050 - 50 * level, this);
        beatTimer.start();
    }

    /**
     * Adds a new Participant
     */
    public void addParticipant (Participant p)
    {
        pstate.addParticipant(p);
    }

    /**
     * The ship has been destroyed
     */
    public void shipDestroyed ()
    {
        // reset key statuses
        rightPressed = false;
        leftPressed = false;
        upPressed = false;

        // Null out the ship
        ship = null;

        // Display a legend
        display.setLegend("Ouch!");

        // Decrement lives
        lives--;

        // Change visual life count
        display.setLives(lives);

        // play "bangShip" sound
        playClip(bangShipClip);

        // Since the ship was destroyed, schedule a transition
        scheduleTransition(END_DELAY);
    }

    /**
     * An asteroid has been destroyed
     */
    public void asteroidDestroyed (Asteroid a)

    {
        // adds points for destroying asteroid
        scoreAdd(ASTEROID_SCORE[a.getSize()]);
        display.setScore(score);

        // creates two new asteroids of smaller size and plays appropriate sound
        if (a.getSize() == 2)
        {
            addParticipant(new Asteroid((int) (Math.random() * 3), 1, a.getX(), a.getY(), this));
            addParticipant(new Asteroid((int) (Math.random() * 3), 1, a.getX(), a.getY(), this));

            // play "bangLarge" sound
            playClip(bangLargeClip);
        }
        else if (a.getSize() == 1)
        {
            addParticipant(new Asteroid((int) (Math.random() * 3), 0, a.getX(), a.getY(), this));
            addParticipant(new Asteroid((int) (Math.random() * 3), 0, a.getX(), a.getY(), this));

            // play "bangMedium" sound
            playClip(bangMediumClip);
        }
        else
        {
            // play "bangSmall" sound
            playClip(bangSmallClip);
        }

        // Expire the asteroid
        Participant.expire(a);

        // If all the asteroids are gone, schedule a transition
        if (countAsteroids() == 0)
        {
            scheduleTransition(END_DELAY);

            nextLevel();
        }
    }

    public void alienShipDestroyed (AlienShip a)
    {
        // adds points for destroying asteroid
        scoreAdd(ALIENSHIP_SCORE[a.getSize()]);

        // stop alien sounds
        saucerBigClip.stop();
        saucerSmallClip.stop();

        // play "bangAlienShip" sound
        playClip(bangAlienShipClip);

        // updates display
        display.setScore(score);
        // removes ship from participants
        Participant.expire(a);
        // starts timer to spawn next ship
        alienShipSpawnTimer.start();
    }

    /**
     * Schedules a transition m msecs in the future
     */
    private void scheduleTransition (int m)
    {
        transitionTime = System.currentTimeMillis() + m;
    }

    /**
     * This method will be invoked because of button presses and timer events.
     */
    @Override
    public void actionPerformed (ActionEvent e)
    {
        // The start button has been pressed. Stop whatever we're doing
        // and bring up the initial screen
        if (e.getSource() instanceof JButton)
        {
            initialScreen();
        }

        // Time to spawn in an alien ship
        else if (e.getSource() == alienShipSpawnTimer)
        {
            placeAlienShip();
        }

        // Time to spawn in apower up
        else if (e.getSource() == enhancedPowerUpSpawnTimer)
        {
            placePowerUp();
        }

        // Time to stop bullet time
        else if (e.getSource() == enhancedBulletTimeTimer)
        {
            enhancedBulletTime = false;
            enhancedBulletTimeTimer.stop();
        }

        // Time to despawn force field
        else if (e.getSource() == enhancedForceFieldTimer)
        {
            Participant.expire(forceField);
            enhancedForceFieldTimer.stop();
        }

        // Time to stop double score power up
        else if (e.getSource() == enhancedDoubleScoreTimer)
        {
            enhancedDoubleScore = false;
            enhancedDoubleScoreTimer.stop();
        }

        // Time to play the beat
        else if (e.getSource() == beatTimer)
        {
            if (beatSwitch % 2 == 0)
            {
                playClip(beat1Clip);
                beatTimer.start();
                beatSwitch++;
            }
            else
            {
                playClip(beat2Clip);
                beatTimer.start();
                beatSwitch++;
            }
        }

        // Time to refresh the screen and deal with keyboard input
        else if (e.getSource() == refreshTimer)
        {
            // checks status of all important keyboard inputs each frame
            if (spacePressed && ship != null)
            {
                fireBullet();
            }

            if (rightPressed && ship != null)
            {
                ship.turnRight();
            }

            if (leftPressed && ship != null)
            {
                ship.turnLeft();
            }

            if (upPressed && ship != null)
            {
                ship.accelerate();
            }

            // It may be time to make a game transition
            performTransition();

            // Move the participants to their new locations
            pstate.moveParticipants();

            // Refresh screen
            display.refresh();
        }
    }

    /**
     * If the transition time has been reached, transition to a new state
     */
    private void performTransition ()
    {
        // Do something only if the time has been reached
        if (transitionTime <= System.currentTimeMillis())
        {
            // Clear the transition time
            transitionTime = Long.MAX_VALUE;

            // If there are no lives left, the game is over. Show the final screen.
            if (lives <= 0)
            {
                beatTimer.stop();
                finalScreen();
            }
            // If there are lives left, respawn new ship
            else if (this.ship == null)
            {
                this.placeShip();
            }
        }
    }

    /**
     * Returns the number of asteroids that are active participants
     */
    private int countAsteroids ()
    {
        int count = 0;
        for (Participant p : this)
        {
            if (p instanceof Asteroid)
            {
                count++;
            }
        }
        return count;
    }

    /**
     * If a key of interest is pressed, record that it is down.
     */
    @Override
    public void keyPressed (KeyEvent e)
    {
        if ((e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_S
                || e.getKeyCode() == KeyEvent.VK_DOWN) && ship != null)
        {
            spacePressed = true;
        }

        if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && ship != null)
        {
            rightPressed = true;
        }

        if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && ship != null)
        {
            leftPressed = true;
        }

        if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) && ship != null)
        {
            upPressed = true;
        }
    }

    @Override
    public void keyTyped (KeyEvent e)
    {
    }

    @Override
    public void keyReleased (KeyEvent e)
    {
        if ((e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_S
                || e.getKeyCode() == KeyEvent.VK_DOWN) && ship != null)
        {
            spacePressed = false;
        }

        if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && ship != null)
        {
            rightPressed = false;
        }

        if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && ship != null)
        {
            leftPressed = false;
        }

        if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) && ship != null)
        {
            upPressed = false;
            ship.makeNoFlame();
        }
    }

    public void bulletNumAdjust (int adjustment)
    {
        bulletCount += adjustment;
    }

    public void scoreAdd (int scoreAdd)
    {
        this.score += scoreAdd;
        if (getVersion().contentEquals("Enhanced"))
        {
            if (enhancedDoubleScore)
            {
                this.score += scoreAdd;
            }
            this.enhancedExtraLifeReward += scoreAdd;
            if (this.enhancedExtraLifeReward >= 5000)
            {
                this.lives++;
                this.enhancedExtraLifeReward -= 5000;
                display.setLives(lives);
            }
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

    public Clip getThrustClip ()
    {
        return thrustClip;
    }

    /** plays clip */
    public void playClip (Clip clip)
    {
        if (clip.isRunning())
        {
            clip.stop();
        }
        clip.setFramePosition(0);
        clip.start();
    }

    /** loops clip */
    public void loopClip (Clip clip)
    {
        if (clip.isRunning())
        {
            clip.stop();
        }
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public String getVersion ()
    {
        return version;
    }

    public int getEnhancedHiScore ()
    {
        return enhancedHiScore;
    }

    public void applyPowerUp (int type)
    {
        if (type == 0) // BulletTime powerup
        {
            enhancedBulletTime = true;
            enhancedBulletTimeTimer = new Timer(3000, this);
            enhancedBulletTimeTimer.start();
        }
        else if (type == 1) // ForceField powerup
        {
            forceField = new EnhancedForceField((int) ship.getX(), (int) ship.getY(), this);
            addParticipant(forceField);
            enhancedForceFieldTimer = new Timer(3000, this);
            enhancedForceFieldTimer.start();
        }
        else if (type == 2) // DoubleScore powerup
        {
            enhancedDoubleScore = true;
            enhancedDoubleScoreTimer = new Timer(5000, this);
            enhancedDoubleScoreTimer.start();
        }
    }
}
