package MainPackage;

/**
 * @author Michael Kieburtz
 * @author Davis Freeman
 */
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.concurrent.*;

public abstract class EnemyShip extends Ship {

    private Point2D.Double playerLocation = new Point2D.Double(0, 0);
    private Point dimensions = new Point(0, 0);
    private ArrayList<EnemyShip> others = new ArrayList<EnemyShip>();
    //private boolean incorrectAngle = false;
    private boolean movingAway;
    private int id; // for use with formations

    private double targetingAngle = 0;

    public EnemyShip(int x, int y, Type shipType, double baseMaxVel, double maxVel,
            double angleIncrement, double acceleration, int shootingDelay, int health, int id) // delegate assigning images to the types of ships
    {
        super(x, y, shipType, baseMaxVel, maxVel, angleIncrement, acceleration, shootingDelay, health);
        this.id = id;
    }

    protected void update(Player player, Point2D.Double cameraLocation, ArrayList<EnemyShip> otherShips) {
        shield.setRegenRate(.05);
        // main AI goes here
        this.playerLocation = player.getLocation();
        this.dimensions.x = player.getActiveImage().getWidth();
        this.dimensions.y = player.getActiveImage().getHeight();

        // move in the direction of the ship if it is far away
        // and shoot if it is in range.
        double distanceToPlayer = Calculator.getDistance(location, player.getLocation());

        double angleToPlayer = Calculator.getAngleBetweenTwoPoints(location, player.getLocation());
        //System.out.println(angleToPlayer + " " + faceAngle);

        //System.out.println(angleToPlayer);
        others = (ArrayList<EnemyShip>) otherShips.clone();
        others.remove(this);
        
        // this block sets movingAway
        if (!movingAway)
            targetingAngle = angleToPlayer;
        
        if (!movingAway && (distanceToPlayer < 200 || hull < 30))
        {
            movingAway = true;
            if (hull < 30)
                targetingAngle = (angleToPlayer + 180) % 360;
            else if (distanceToPlayer < 200)
            {
                targetingAngle = (angleToPlayer + 90) % 360;
            }
        } else if (distanceToPlayer > 400 && movingAway)
        {
            movingAway = false;
            targetingAngle = angleToPlayer;
        } else if (distanceToPlayer < 250 && movingAway)
        {
            if (Math.abs((angleToPlayer + 180) % 360 - faceAngle) > 5)
                targetingAngle = (angleToPlayer + 180) % 360;
        }
        
        // this block performs logic based on movingAway
        if (!movingAway)
        {
            for (EnemyShip ship : others)
            {
                if (Calculator.getDistance(location, ship.getLocation()) < 200) {
                    double angle = Calculator.getAngleBetweenTwoPoints(location, ship.getLocation()); 
                    if (id < ship.getID())
                        targetingAngle = angle > faceAngle ? targetingAngle - 45 : targetingAngle + 45;
                }
            }
            
            rotateToAngle(targetingAngle);
            
            if (distanceToPlayer > 200)
                move(ShipState.Thrusting);
            else
                move(ShipState.Drifting);
            
            if (Math.abs(angleToPlayer - faceAngle) < 45)
            {
                shoot(cameraLocation);
            }
        } else
        {
            if (Math.abs(faceAngle - targetingAngle) >= 5)
                rotateToAngle(targetingAngle);
            
            if (distanceToPlayer > 200)
                move(ShipState.Thrusting);
            else
                move(ShipState.Drifting);
        }
        
        // regen shield
        if (shield.getHealth() <= 100) {
            shield.setHealth(shield.getHealth() + shield.getRegenRate());
        }
    }

    protected void rotateToAngle(double angle) {
        double[] distances = Calculator.getDistancesBetweenAngles(faceAngle, angle);

        if (Math.abs(angle - faceAngle) >= 5) {
            if (distances[0] < distances[1]) {
                if (distances[0] > angleIcrement) {
                    rotate(ShipState.TurningLeft);
                }
            } else {
                if (distances[1] > angleIcrement) {
                    rotate(ShipState.TurningRight);
                }
            }
        }
    }

    @Override
    public void shoot(Point2D.Double cameraLocation) {

        if (canshoot) {
            Random rand = new Random();

            double angle = 360 - faceAngle + rand.nextInt(10) - 5;

            Point2D.Double ShotStartingVel
                    = new Point2D.Double(movementVelocity.x + Calculator.CalcAngleMoveX(angle) * 20,
                            movementVelocity.y + Calculator.CalcAngleMoveY(angle) * 20);

            Point2D.Double ShotStartingPos = new Point2D.Double(
                    Calculator.getScreenLocationMiddleForPlayer(cameraLocation, location, activeImage.getWidth(), activeImage.getHeight()).x - 2.5
                    + Calculator.CalcAngleMoveX(angle) * 20,
                    Calculator.getScreenLocationMiddleForPlayer(cameraLocation, location, activeImage.getWidth(), activeImage.getHeight()).y - 8 + Calculator.CalcAngleMoveY(angle) * 20);

            shots.add(new PulseShot(5, 100, false, ShotStartingPos, ShotStartingVel, angle, true, cameraLocation));
            canshoot = false;

            ex.schedule(new ShootingService(), shootingDelay, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void draw(Graphics2D g2d, Camera camera) {
        super.draw(g2d, camera);

        Point2D.Double middleOfPlayer = Calculator.getScreenLocationMiddleForPlayer(camera.getLocation(), playerLocation, dimensions.x, dimensions.y);
        Point2D.Double middleOfSelf = Calculator.getScreenLocationMiddleForPlayer(camera.getLocation(), location, activeImage.getWidth(), activeImage.getHeight());

//        for (EnemyShip s : others) {
//            g2d.setColor(Color.BLUE);
//
//            g2d.drawLine((int) (location.x - camera.getLocation().x), (int) (location.y - camera.getLocation().y),
//                    (int) ((middleOfSelf.x + Math.cos(Math.toRadians(targetingAngle)) * Calculator.getDistance(location, playerLocation)) - camera.getLocation().x),
//                    (int) ((middleOfSelf.y - Math.sin(Math.toRadians(targetingAngle)) * Calculator.getDistance(location, playerLocation)) - camera.getLocation().y));
//        }

        shield.draw(g2d, camera.getLocation(), location);

        Rectangle2D.Float paintRectShield = new Rectangle2D.Float((float) (camera.getSize().x - (camera.getSize().x - 10)),
                (float) (camera.getSize().y - 85), (float) shield.getHealth() * 1.5f, 5f);

        GradientPaint paintShield = new GradientPaint(paintRectShield.x, paintRectShield.y, Color.BLUE, paintRectShield.x + paintRectShield.width,
                paintRectShield.y + paintRectShield.height, Color.CYAN);

        Rectangle2D.Float paintRectHull = new Rectangle2D.Float((float) (camera.getSize().x - (camera.getSize().x - 10)),
                (float) (camera.getSize().y - 55), (float) hull * 1.5f, 5f);

        GradientPaint paintHull = new GradientPaint(paintRectHull.x, paintRectHull.y, new Color(100, 0, 0), paintRectHull.x + paintRectHull.width,
                paintRectHull.y + paintRectHull.height, new Color(255, 0, 0));

        g2d.setPaint(paintShield);
        g2d.fill(paintRectShield);

        g2d.setPaint(paintHull);
        g2d.fill(paintRectHull);
    }
    
    public boolean isMovingAway()
    {
        return movingAway;
    }
    
    public int getID()
    {
        return id;
    }
}
