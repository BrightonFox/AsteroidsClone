package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.event.*;
import java.util.Iterator;
import javax.swing.*;
import asteroids.participants.AlienShip;
import asteroids.participants.Asteroid;
import asteroids.participants.Bullet;
import asteroids.participants.Ship;

/**
 * Controls a game of Asteroids.
 */
public class Controller implements KeyListener, ActionListener, Iterable<Participant>
{
    /** The state of all the Participants */
    private ParticipantState pstate;

    /** The ship (if one is active) or null (otherwise) */
    private Ship ship;

    /** When this timer goes off, it is time to refresh the animation */
    private Timer refreshTimer;

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

    /**
     * Constructs a controller to coordinate the game and screen
     */
    public Controller ()
    {
        // Initialize the ParticipantState
        pstate = new ParticipantState();

        // Set up the refresh timer.
        refreshTimer = new Timer(FRAME_INTERVAL, this);

        // Clear the transitionTime
        transitionTime = Long.MAX_VALUE;

        // Record the display object
        display = new Display(this);

        // Bring up the splash screen and start the refresh timer
        splashScreen();
        display.setVisible(true);
        refreshTimer.start();
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
        ship = new Ship(SIZE / 2, SIZE / 2, -Math.PI / 2, this);
        addParticipant(ship);
        display.setLegend("");
    }

    /**
     * Places an asteroid near one corner of the screen. Gives it a random velocity and rotation.
     */
    private void placeAsteroids (int numOfAsteroids)
    {
        
        
        
        
        addParticipant(new AlienShip(1, this));
        
        
        
        
        
        
        
        
        for (int i = 0; i < numOfAsteroids; i++)
        {
            if (i % 4 == 0)
            {
                addParticipant(new Asteroid(RANDOM.nextInt(4), 2, EDGE_OFFSET + RANDOM.nextInt(50) - 25, EDGE_OFFSET + RANDOM.nextInt(50) - 25, this));
            }
            if (i % 4 == 1)
            {
                addParticipant(new Asteroid(RANDOM.nextInt(4), 2, SIZE - EDGE_OFFSET + RANDOM.nextInt(50) - 25, EDGE_OFFSET + RANDOM.nextInt(50) - 25, this));
            }
            if (i % 4 == 2)
            {
                addParticipant(new Asteroid(RANDOM.nextInt(4), 2, EDGE_OFFSET + RANDOM.nextInt(50) - 25, SIZE - EDGE_OFFSET + RANDOM.nextInt(50) - 25, this));
            }
            if (i % 4 == 3)
            {
                addParticipant(new Asteroid(RANDOM.nextInt(4), 2, SIZE - EDGE_OFFSET + RANDOM.nextInt(50) - 25, SIZE - EDGE_OFFSET + RANDOM.nextInt(50) - 25, this));
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
    private void nextLevel() {
        clear();
        level++;
        display.setLevel(level);
        
        bulletCount = 0;
        
        placeAsteroids(level + 3);
        placeShip();
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

        // Since the ship was destroyed, schedule a transition
        scheduleTransition(END_DELAY);
    }

    /**
     * An asteroid has been destroyed
     */
    public void asteroidDestroyed (Asteroid a)
    {     
        // adds points for destroying asteroid
        score += ASTEROID_SCORE[a.getSize()];
         display.setScore(score);       
        
        // creates two new asteroids of smaller size
        if (a.getSize() == 2)
        {
            addParticipant(
                    new Asteroid((int) (Math.random() * 3), 1, a.getX(), a.getY(), this));
            addParticipant(
                    new Asteroid((int) (Math.random() * 3), 1, a.getX(), a.getY(), this));
        }
        else if (a.getSize() == 1)
        {
            addParticipant(
                    new Asteroid((int) (Math.random() * 3), 0, a.getX(), a.getY(), this));
            addParticipant(
                    new Asteroid((int) (Math.random() * 3), 0, a.getX(), a.getY(), this));
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
        if ((e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) && ship != null)
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
        if ((e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_S || e.getKeyCode() == KeyEvent.VK_DOWN) && ship != null)
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

    /**
     * Creates bullet object headed in direction of ship
     */
    public void fireBullet ()
    {
        // limit number of bullets on screen to 8
        if (ship != null && bulletCount <= BULLET_LIMIT)
        {
            bulletNumAdjust(1);
            addParticipant(new Bullet((int) ship.getXNose(), (int) ship.getYNose(), ship.getRotation(), this));
        }
    }

    public void bulletNumAdjust (int adjustment)
    {
        this.bulletCount += adjustment;
    }
    
    public void scoreAdd (int scoreAdd)
    {
        this.score += scoreAdd;
    }
}
