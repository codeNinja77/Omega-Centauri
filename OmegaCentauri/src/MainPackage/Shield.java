package MainPackage;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * @author Michael Kieburtz
 * @author Davis Freeman
 */
public class Shield {

    private ArrayList<String> imagePaths = new ArrayList<String>();
    private ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
    private BufferedImage activeImage;
    private MediaLoader loader = new MediaLoader();
    private double angle;
    private int opacity = 0;
    private double[] scaling = new double[2];
    private double health;
    private double regenRate;
    private int strengh;

    public Shield(double angle, Point2D.Double location, Point2D.Double cameraLocation, boolean enemy, Point size) {
        this.angle = angle;
        health = 100;
        imagePaths.add(enemy ? "resources/FILLERshieldEnemy.png" : "resources/FILLERshield.png");
        images = loader.loadImages(imagePaths);
        images = Calculator.toCompatibleImages(images);
        activeImage = images.get(0);
        scaling[0] = (double)size.x / activeImage.getWidth();
        scaling[1] = (double)size.y / activeImage.getHeight();
    }

    public void draw(Graphics2D g2d, Point2D.Double cameraLocation, Point2D.Double instanceLocation) {
        if (opacity > 0) {
            
            int rule = AlphaComposite.SRC_OVER;
            
            Composite originalComposite = g2d.getComposite();
            Composite comp = AlphaComposite.getInstance(rule, (float)opacity/100);
            
            AffineTransform originalAffineTransform = g2d.getTransform();
            AffineTransform transform = (AffineTransform) originalAffineTransform.clone();
            
            
            g2d.setComposite(comp);

            transform.translate(instanceLocation.x + 1 - cameraLocation.x, instanceLocation.y - cameraLocation.y);
            
            transform.scale(scaling[0], scaling[1]);
            
            g2d.drawImage(activeImage, transform, null);
            
            g2d.setComposite(originalComposite);
        }
    }

    public void activate(double damage) {
        opacity = 100;
        health -= damage;
    }
    
    public double getHealth()
    {
        return health;
    }
    
    public void setHealth(double x)
    {
        Shield.this.health = x;
    }
    
    public boolean isActive()
    {
        return opacity > 0;
    }
    
    public void decay()
    {
        opacity -= 1;
    }
    
    public double getRegenRate()
    {
        return regenRate;
    }
    
    public void setRegenRate(double regenRate)
    {
        this.regenRate = regenRate;
    }
}
