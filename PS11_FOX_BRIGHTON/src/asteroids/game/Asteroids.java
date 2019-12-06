package asteroids.game;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * The main class for the application.
 */
public class Asteroids
{
    /**
     * Launches a dialog that lets the user choose between a classic and an enhanced game of Asteroids.
     */
    public static void main (String[] args)
    {
        SwingUtilities.invokeLater( () -> chooseVersion());
    }

    /**
     * Interacts with the user to determine whether to run classic Asteroids or enhanced Asteroids.
     */
    private static void chooseVersion ()
    {
        String[] options = { "Classic", "Enhanced" };
        int choice = JOptionPane.showOptionDialog(null, "Which version would you like to run?", "Choose a Version",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (choice == 0)
        {
            new Controller(0);
        }
        else if (choice == 1)
        {
            /**
             * Enhanced Differences:
             * Better ship flame
             * Extra life every 5000 points
             * Alien Ship
             * -looks better
             * -after 6 seconds ditches zig-zag and follows you
             * -after 12 seconds, doubles speed every 12 seconds
             * High Score mechanic
             * PowerUp Mechanic
             * -every 7 - 12 seconds a powerup will spawn
             * -despawn after 5 seconds if not picked up
             * -3 types:
             * --Bullet Time give no bullet limit for 3 seconds
             * --Force Field spawns an invincible field that surrounds the ship for 3 seconds
             * --Double Points double the points awarded for 5 seconds
             * 
             */           
            new Controller(1);
        }
    }
}


