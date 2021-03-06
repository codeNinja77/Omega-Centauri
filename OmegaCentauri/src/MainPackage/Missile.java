package MainPackage;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * @author Kieburtz
 */
public class Missile extends PhysicalShot 
{    
    public Missile(int damage, Point2D.Double location,
            Point2D.Double startingVel, double angle, Ship targetShip, Ship owner)
    {
        super(damage, 2000, location, startingVel, angle, owner);

        activeImage = resources.getImageForObject("resources/Missile.png");

        this.location = location;
        faceAngle = angle;
        this.targetShip = targetShip;
        setUpHitbox();
        
        explosion = new Explosion(Explosion.Type.missile, new Dimension(activeImage.getWidth(), activeImage.getHeight()));
    }
}
