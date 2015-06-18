package MainPackage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * @author Michael Kieburtz
 */
public class EnemyMediumFighter extends EnemyShip 
{

    private int id;

    private Turret[] turrets = new Turret[2];
    private int shootingDelayMissile;
    private int shootingDelayTurret;

    private Resources resources;
    
    private boolean right = true;
    
    private boolean canShootMissile = true;
    private boolean canShootTurret = true;
    
    public EnemyMediumFighter(int x, int y, Type shipType, double maxVel, double maxAngleVelocity,
            double angleIncrement, double acceleration, int shootingDelayTurret, 
            int shootingDelayMissile, int health, int id, Player player, Resources resources) 
    {
        super(x, y, shipType, maxVel, maxAngleVelocity, angleIncrement, acceleration, shootingDelayTurret, health);

        this.id = id;
        this.resources = resources;
       
        activeImage = resources.getImageForObject("resources/MediumEnemyFighter.png");

        shield = new Shield(location, true,
                new Point(activeImage.getWidth(), activeImage.getHeight()), 15, 150, resources, false);

        setUpHitbox();

        turrets[0] = new Turret(25, 335, 45, new Point2D.Double(93, 115), new Dimension(activeImage.getWidth(), activeImage.getHeight()),
                new Point2D.Double(65, 70), 65,faceAngle, this, resources);

        turrets[1] = new Turret(25, 315, 35, new Point2D.Double(93, 240), new Dimension(activeImage.getWidth(), activeImage.getHeight()),
                new Point2D.Double(95, 75), -65, faceAngle, this, resources);

        this.targetShip = player;
        
        this.shootingDelayMissile = shootingDelayMissile;
        this.shootingDelayTurret = shootingDelayTurret;

        ex.schedule(new ShootingService(), shootingDelayMissile, TimeUnit.MILLISECONDS);
    }
    
    @Override
    public void setUpHitbox()
    {
        try 
        {
            shieldHitbox = new EllipseHitbox(activeImage.getWidth() + 50, activeImage.getHeight() + 50); // constants added to the end compensate for the wings
            Point2D.Double points[] = new Point2D.Double[8];
            points[0] = new Point2D.Double(27, 0);
            points[1] = new Point2D.Double(activeImage.getWidth(), 0);
            points[2] = new Point2D.Double(activeImage.getWidth(), activeImage.getHeight());
            points[3] = new Point2D.Double(27, activeImage.getHeight());
            points[4] = new Point2D.Double(27, activeImage.getHeight() - 172);
            points[5] = new Point2D.Double(0, activeImage.getHeight() - 172);
            points[6] = new Point2D.Double(0, activeImage.getHeight() - 202);
            points[7] = new Point2D.Double(27, activeImage.getHeight() - 202);
            hullHitbox = new ShapeHitbox(points, new Point2D.Double(activeImage.getWidth() / 2, activeImage.getHeight() / 2));
        } 
        catch (NullPointerException ex) 
        {
            System.err.println("active image not initialized!");
        }
    }
    
    @Override
    protected void updateHitbox()
    {
        super.updateHitbox();
        hullHitbox.moveToLocation(Calculator.getGameLocationMiddle(location, activeImage.getWidth(), activeImage.getHeight()));
        hullHitbox.rotateToAngle(faceAngle);
    }

    @Override
    public void shoot() {
        if (canShootTurret)
        {
            if (right)
            {
                shots.add(turrets[0].shoot(movementVelocity));
                right = !right;
            }
            else
            {
                shots.add(turrets[1].shoot(movementVelocity));
                right = !right;
            }
            canShootTurret = false;
            ex.schedule(new ShootingServiceTurrets(), shootingDelayTurret, TimeUnit.MILLISECONDS);
        }
        
        if (canShootMissile) {

            double angle = faceAngle;

            Point2D.Double startingLocation = Calculator.getGameLocationMiddle(location, activeImage.getWidth(), activeImage.getHeight());
            
            shots.add(new Missile(60, startingLocation, null, 360 - angle, targetShip, this, resources));

            canShootMissile = false;

            ex.schedule(new ShootingServiceMisisle(), shootingDelayMissile, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        AffineTransform original = g2d.getTransform();

        super.draw(g2d);

        for (Turret t : turrets) 
        {
            t.draw(g2d, location);
        }

        g2d.setTransform(original);

        Point2D.Double middle = new Point2D.Double(Calculator.getScreenLocationMiddle(gameData.getCameraLocation(), location, activeImage.getWidth(), activeImage.getHeight()).x,
                    Calculator.getScreenLocationMiddle(gameData.getCameraLocation(), location, activeImage.getWidth(), activeImage.getHeight()).y);
        
        shield.draw(g2d, location, middle, middle, faceAngle);
//        g2d.setColor(Color.red);
//        hullHitbox.draw(g2d, camera.getLocation());
    }

    @Override
    public void update(Player player, ArrayList<EnemyShip> otherShips) {

        double distance = Calculator.getDistance(location, player.getLocation());

        double angleToPlayer = Calculator.getAngleBetweenTwoPoints(Calculator.getGameLocationMiddle(location, activeImage.getWidth(), activeImage.getHeight()),
                player.getLocation());

        rotateToAngle(angleToPlayer);
        
        for (Turret t : turrets) 
        {
            t.update(Calculator.getGameLocationMiddle(player.getLocation(), player.getActiveImage().getWidth(), player.getActiveImage().getHeight()),
                    Calculator.getGameLocationMiddle(location, activeImage.getWidth(), activeImage.getHeight()),
                    faceAngle);

        }

        if (Math.abs(angleToPlayer - faceAngle) <= 45)
        {
            shoot();
        }

        if (distance > 500)
        {
            move(MovementState.Thrusting);
        } 
        else
        {
            move(MovementState.Drifting);
        }
    }

    @Override
    public int getID() 
    {
        return id;
    }

    @Override
    public Point2D.Double getLocation() 
    {
        return location;
    }

    @Override
    protected void changeImage(StateChange change) {
        // only one image so far
    }

    class ShootingServiceMisisle implements Runnable 
    {

        @Override
        public void run() 
        {
            canShootMissile = true;
        }
    }

    class ShootingServiceTurrets implements Runnable 
    {

        @Override
        public void run()
        {
            canShootTurret = true;
        }
    }
}
