package MainPackage;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.*;

// @author Michael Kieburtz
public class Game {
    private ArrayList<EnemyShip> enemyShips = new ArrayList<EnemyShip>();
    private ArrayList<Ally> allyShips = new ArrayList<Ally>();
    private Player player;
    
    GameWindow window;
    
    public Game(int WindowWidth, int WindowHeight)
    {
        player = new Player(((WindowWidth / 2) - 25),((WindowHeight/ 2) - 25), Type.Fighter);
        window = new GameWindow(WindowWidth, WindowHeight, this);
        System.out.println(player.getLocation());
    }
    public Player getPlayer()
    {
        return player;
    }
    
    public void movePlayer(int x, int y)
    {
        player.moveTo(x, y);
    }
    
    public void movePlayer(boolean Slowingdown, double driftAngle, boolean pressing)
    {
        player.move(Slowingdown, driftAngle, pressing);
    }
    
    public void movePlayerRelitive(int dx, int dy)
    {
        player.moveRelitive(dx, dy);
    }
    
    public void rotatePlayer(boolean positive)
    {
        player.rotate(positive);
    }
    public void rotatePlayer(double amount)
    {
        player.rotate(amount);
    }
}
