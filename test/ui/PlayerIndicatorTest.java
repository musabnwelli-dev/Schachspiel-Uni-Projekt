package ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;

import org.junit.jupiter.api.Test;

class PlayerIndicatorTest
{

    @Test
    void testGetColorAfterSetColor()
    {
        PlayerIndicator playerIndicator = new PlayerIndicator();
        playerIndicator.setColor(Color.RED);
        assertEquals(Color.RED, playerIndicator.getColor());
    }

}
