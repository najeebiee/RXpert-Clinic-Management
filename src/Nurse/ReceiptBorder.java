/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Nurse;

import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
/**
 *
 * @author USER
 */
public class ReceiptBorder implements Border {
  private int radius;
    private Color shadowColor = new Color(100, 149, 237, 100); // Cornflower blue shadow, semi-transparent
    private Color borderColor = new Color(0, 153, 153); // Teal border

    public ReceiptBorder(int radius) {
        this.radius = radius;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw shadow
        g2.setColor(shadowColor);
        g2.fill(new RoundRectangle2D.Double(x + 2, y + 2, width - 4, height - 4, radius, radius));

        // Draw border
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(2));
        g2.draw(new RoundRectangle2D.Double(x, y, width - 4, height - 4, radius, radius));

        // Draw background fill
        g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Double(x + 1, y + 1, width - 6, height - 6, radius - 2, radius - 2));

        g2.dispose();
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(radius + 5, radius + 5, radius + 5, radius + 5);
    }

    public boolean isBorderOpaque() {
        return false;
    }
}