package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * This class represents a player display
 */
public class PlayerIndicator extends JPanel
{

    private Color color;

    public PlayerIndicator()
    {
        this.color = Color.LIGHT_GRAY;
        setPreferredSize(new Dimension(40, 40));
    }

    /**
     * Sets the color of the player
     * @param color the color of the player
     */
    public void setColor(Color color)
    {
        this.color = color;
        repaint();

    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(color);
        g.fillOval(5, 5, 25, 25);

    }

    /**
     * Returns the color of the player
     * @return the color of the player
     */
    public Color getColor()
    {
        return this.color;
    }

}
