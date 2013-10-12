package MainPackage;

import java.awt.*;
import java.awt.geom.*;
// @author Michael Kieburtz

public class Renderer {
    
    
    @SuppressWarnings("empty-statement")
    public Renderer() {
        ;
    }

    public void drawScreen(Graphics g, Player player, int xRot, int yRot, Ellipse2D.Double playerCircle) {
        // g.drawImage(player.getImage(), player.getLocation().x, player.getLocation().y, null);

        Graphics2D g2d = (Graphics2D) g; // turns it into 2d graphics
        AffineTransform origXform = g2d.getTransform();
        AffineTransform newXform = (AffineTransform) (origXform.clone());

        //center of rotation is center of the panel

        newXform.rotate(Math.toRadians(player.getAngle()), xRot, yRot);
        g2d.setTransform(newXform);
        g2d.drawImage(player.getImage(), player.getLocation().x, player.getLocation().y, null);
        g2d.draw(playerCircle);
        
        /*
         * a point on the outer edge of a circle given the rectangle bounding
         * box (cx, cy), the radius (r) and the angle where the ship is pointing
         * (a) is
         * x = cx + r + Math.cos(Math.toRadians(a));
         * y is cy + r + Math.sin(Math.toRadians(a));
         * 
         * 
         */
    }
}
