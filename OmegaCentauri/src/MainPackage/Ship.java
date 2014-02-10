package MainPackage;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.TimerTask;
import javax.sound.sampled.Clip;

/**
 * @author Michael Kieburtz
 * @author Davis Freeman
 */

public abstract class Ship {

    protected int hull;
    protected int fuel;
    protected int power;
    protected Type type;
    protected double faceAngle = 360.0; // maybe move to Ship Class
    protected double moveAngle = 0.0;
    protected Point2D.Double location;
    protected Point2D.Double nextLocation;
    protected Point2D.Double velocity = new Point2D.Double(0, 0);
    protected Rectangle2D.Double hitbox;
    protected String name;
    protected double baseMaxVel;
    protected double maxVel;
    protected double angleIcrement;
    protected double acceleration = .15;
    
    // File -> FileInputStream -> ImageIO -> buffered image
    
    protected ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
    protected ArrayList<Clip> sounds = new ArrayList<Clip>();
    protected BufferedImage activeImage;
    protected ArrayList<String> imagePaths = new ArrayList<String>();
    protected ArrayList<String> soundPaths = new ArrayList<String>();
    protected MediaLoader mediaLoader = new MediaLoader();
    protected ArrayList<Shot> shots = new ArrayList<Shot>();
    
    protected boolean canshoot = true;
    protected java.util.Timer shootingTimer;
    protected int shootingDelay;
    
    public Ship(int x, int y, Type shipType, double baseMaxVel, double maxVel,
            double angleIncrement, double acceleration, int shootingDelay) {
        location = new Point2D.Double(x, y);
        nextLocation = new Point2D.Double();
        type = shipType;

        this.baseMaxVel = baseMaxVel;
        this.maxVel = maxVel;
        this.angleIcrement = angleIncrement;
        this.acceleration = acceleration;
        this.shootingDelay = shootingDelay;
        
        shootingTimer = new java.util.Timer();
    }

    public BufferedImage getImage() {
        return activeImage;
    }

    public BufferedImage getImage(int index) {
        return this.images.get(index);
    }

    public void draw(Graphics2D g2d, Point2D.Double cameraLocation) {
        AffineTransform original = g2d.getTransform();
        AffineTransform transform = (AffineTransform) original.clone();

        transform.setToIdentity();
        transform.rotate(Math.toRadians(faceAngle),
                getScreenLocation(cameraLocation).x + activeImage.getWidth() / 2,
                getScreenLocation(cameraLocation).y + activeImage.getHeight() / 2);

        transform.translate(getScreenLocation(cameraLocation).x, getScreenLocation(cameraLocation).y);

        transform.scale(1, 1);
        
        updateHitbox(cameraLocation);
        
//        g2d.setColor(Color.WHITE);
//        g2d.draw(hitbox);
        
        g2d.drawImage(activeImage, transform, null);

    }

    public Point2D.Double getScreenLocation(Point2D.Double cameraLocation) {
        double x = location.x - cameraLocation.x;
        double y = location.y - cameraLocation.y;

        return new Point2D.Double(x, y);
    }

    public Point2D.Double getScreenLocationMiddle(Point2D.Double cameraLocation) {
        double x = getScreenLocation(cameraLocation).x + cameraLocation.x + activeImage.getWidth() / 2;
        double y = getScreenLocation(cameraLocation).y + cameraLocation.y + activeImage.getHeight() / 2;

        return new Point2D.Double(x, y);
    }

    protected void move(boolean thrusting) {

        moveAngle = faceAngle - 90;

        if (thrusting) {
            velocity.x += Calculator.CalcAngleMoveX(moveAngle) * acceleration;

            if (velocity.x > maxVel) {
                velocity.x = maxVel;
            } else if (velocity.x < -maxVel) {
                velocity.x = -maxVel;
            }

            velocity.y += Calculator.CalcAngleMoveY(moveAngle) * acceleration;

            if (velocity.y > maxVel) {
                velocity.y = maxVel;
            } else if (velocity.y < -maxVel) {
                velocity.y = -maxVel;
            }
        }

        velocity.x *= .99;
        velocity.y *= .99;

        if (!thrusting) {
            if (Math.abs(velocity.x) < .1) {
                velocity.x = 0;
            }

            if (Math.abs(velocity.y) < .1) {
                velocity.y = 0;
            }
        }

        updatePosition();

    }

    protected void updatePosition() {
        location.x += velocity.x;
        location.y += velocity.y;
    }

    public void shoot(Point2D.Double cameraLocation) {
        playSound(0);

        Point2D.Double ShotStartingVel =
                new Point2D.Double(velocity.x + Calculator.CalcAngleMoveX(faceAngle - 90) * 20,
                velocity.y + Calculator.CalcAngleMoveY(faceAngle - 90) * 20);

        Point2D.Double ShotStartingPos = new Point2D.Double(getScreenLocationMiddle(cameraLocation).x - 2.5 +
                Calculator.CalcAngleMoveX(faceAngle - 90) * 20,
                getScreenLocationMiddle(cameraLocation).y - 8 + Calculator.CalcAngleMoveY(faceAngle - 90) * 20);


        shots.add(new PulseShot(5, 100, false, ShotStartingPos, ShotStartingVel, faceAngle, false)); // enemies ovveride
        canshoot = false;
        shootingTimer.schedule(new ShootingTimerTask(), shootingDelay);
    }

    public Point2D.Double getLocation() {
        return location;
    }

    public void rotate(boolean positive) {

        if (positive) {
            faceAngle += angleIcrement;
            if (faceAngle > 360) {
                faceAngle = faceAngle - 360;
            }
        } else {
            faceAngle -= angleIcrement;
            if (faceAngle <= 0) {
                faceAngle = 360 + faceAngle;
            }
        }
    }

    protected void playSound(int index) {
        sounds.get(index).setFramePosition(0);
        // do some random stuff to kill a little time
        int x;
        int y;
        for (x = 0; x < 5; x++) {
            y = x;
        }

        sounds.get(index).start();
    }

    public ArrayList<Shot> getShots() {
        return shots;
    }
    
    public void setUpHitbox(Point2D.Double cameraLocation)
    {
        try {
            hitbox = new Rectangle2D.Double(getScreenLocation(cameraLocation).x, getScreenLocation(cameraLocation).y,
                activeImage.getWidth(), activeImage.getHeight());
        } catch (NullPointerException e) {
            System.err.println("activeimage not initialized!");
        }
    }
    
    protected void updateHitbox(Point2D.Double cameraLocation)
    {
        hitbox.x = getScreenLocation(cameraLocation).x;
        hitbox.y = getScreenLocation(cameraLocation).y;
    }
    
    public boolean canShoot()
    {
        return canshoot;
    }
    
    protected class ShootingTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            canshoot = true;
        }
    }
}
