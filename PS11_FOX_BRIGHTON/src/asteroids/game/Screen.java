package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.*;
import javax.swing.*;

/**
 * The area of the display in which the game takes place.
 */
@SuppressWarnings("serial")
public class Screen extends JPanel
{
    /** Legend that is displayed across the screen */
    private String legend;
    
    private int level;

    /** Game controller */
    private Controller controller;

    private int score;    
    
    private Font bigText = new Font(Font.SANS_SERIF, Font.PLAIN, 120);
    
    private Font smallText = new Font(Font.SANS_SERIF, Font.PLAIN, 60);
    

    /**
     * Creates an empty screen
     */
    public Screen (Controller controller)
    {
        this.controller = controller;
        legend = "";
        level = 0;

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
        this.level = level;
    }
    
    public void setScore (int score)
    {
        this.score = score;
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
        for (Participant p: controller)
        {
            p.draw(g);
        }
        
        this.setFont(bigText);
        // Draw the legend across the middle of the panel
        int size = g.getFontMetrics().stringWidth(legend);
        g.drawString(legend, (SIZE - size) / 2, SIZE / 2);

        this.setFont(smallText);
        // font is fluid, refer to Piazza
        g.drawString(""+score, 0, 120);
        
        g.drawString(""+level, SIZE - 100, 120);
        
        this.setFont(bigText);
    }
}
