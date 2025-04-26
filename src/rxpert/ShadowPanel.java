package rxpert;

import java.awt.*;
import javax.swing.*;

public class ShadowPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing for smoother rendering
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Shadow settings
        int shadowHeight = 8; // Height of the shadow below the panel
        Color shadowColor = new Color(0, 0, 0, 40); // Subtle transparent shadow

        // Draw shadow
        g2d.setColor(shadowColor);
        g2d.fillRect(0, getHeight(), getWidth(), shadowHeight);

        // Fill the main panel (top bar)
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
