package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.*;
import javax.swing.*;
import asteroids.participants.Ship;

/**
 * The area of the display in which the game takes place.
 */
@SuppressWarnings("serial")
public class Screen extends JPanel
{
    /** Legend that is displayed across the screen */
    private String legend;

    private String level;

    private String score;

    private int lives;

    /** Game controller */
    private Controller controller;

    private Font bigText = new Font(Font.SANS_SERIF, Font.PLAIN, 120);

    private Font smallText = new Font(Font.SANS_SERIF, Font.PLAIN, 40);

    private Font smallerText = new Font(Font.SANS_SERIF, Font.PLAIN, 15);

    private Ship lifeShip;

    /**
     * Creates an empty screen
     */
    public Screen (Controller controller)
    {
        this.controller = controller;
        legend = "";
        level = "";
        score = "0";
        lives = 0;
        lifeShip = new Ship(0, 0, -(Math.PI / 2), null);
        setPreferredSize(new Dimension(SIZE, SIZE));
        setMinimumSize(new Dimension(SIZE, SIZE));
        setBackground(Color.black);
        setForeground(Color.white);
        setFocusable(true);
    }

    /**
     * Set the legend
     */
    public void setLegend (String legend)
    {
        this.legend = legend;
    }

    public void setLevel (int level)
    {
        this.level = "" + level;
    }

    public void setScore (int score)
    {
        this.score = "" + score;
    }

    public void setLives (int lives)
    {
        this.lives = lives;
    }

    /**
     * Paint the participants onto this panel
     */
    @Override
    public void paintComponent (Graphics graphics)
    {
        // Use better resolution
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Do the default painting
        super.paintComponent(g);

        // Draw each participant in its proper place
        for (Participant p : controller)
        {
            p.draw(g);
        }

        g.setFont(bigText);
        // Draw the legend across the middle of the panel
        int size = g.getFontMetrics().stringWidth(legend);
        g.drawString(legend, (SIZE - size) / 2, SIZE / 2);

        g.setFont(smallText);

        g.drawString("" + score, 10, 50);

        g.drawString("" + level, SIZE - 30, 50);

        g.setFont(smallerText);
        g.drawString("SCORE", 10, 15);
        g.drawString("LEVEL", SIZE - 50, 15);

        for (int i = 0; i < lives; i++)
        {
            lifeShip.setPosition(20 + i * 30, 75);
            lifeShip.move();
            lifeShip.draw(g);
        }

        if (controller.getVersion().equals("Enhanced"))
        {
            g.setFont(smallText);

            g.drawString("" + controller.getEnhancedHiScore(), 10, SIZE - 10);

            g.setFont(smallerText);

            g.drawString("HI-SCORE", 10, SIZE - 50);
        }

    }
}
