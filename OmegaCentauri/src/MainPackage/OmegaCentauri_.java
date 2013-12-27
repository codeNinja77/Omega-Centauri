package MainPackage;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

// @author Michael Kieburtz
public class OmegaCentauri_ extends Game {

    private boolean forward, rotateRight, rotateLeft = false;
    private final java.util.Timer timer = new java.util.Timer();
    private final int timerDelay;
    private final Renderer renderer;
    private final Panel panel = new Panel(1000, 600); // this will be changed when we do resolution things
    private final Point2D.Double middleOfPlayer = new Point2D.Double();
    private double FPS = 0;
    private ArrayList<Long> updateTimes = new ArrayList<Long>();
    private boolean paused = false;
    private boolean loading = false;
    private int starChunksLoaded = 0;
    private final Point screenSize = new Point(10000, 10000);
    private Camera camera;
    private ArrayList<StarChunk> stars = new ArrayList<StarChunk>();
    
    private int[] yPositions = {-10000, -10000, 0, 0}; // starting y positions

    public OmegaCentauri_(int width, int height, int desiredFrameRate, Renderer renderer) {
        this.renderer = renderer;
        camera = new Camera(width, height);
        loading = true;


        timerDelay = 15;

        player = new Player(0, 0, MainPackage.Type.Fighter);

        timer.schedule(new MovementTimer(player), timerDelay);

        middleOfPlayer.x = camera.getLocation().x - player.getLocation().x + player.getImage().getWidth() / 2;
        middleOfPlayer.y = camera.getLocation().y - player.getLocation().y + player.getImage().getHeight() / 2;
        setUpWindow(width, height);
    }

    private void setUpWindow(int width, int height) {
        setSize(width, height);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addKeyListener(this);
        setTitle("Omega Centauri");
        add(panel);
        setContentPane(panel);

    }

    private class MovementTimer extends TimerTask {

        Player player;

        public MovementTimer(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            FPS = getFrameRate();

            if (loading) {
                // load 100 starChunks from each quadrant
                // load all the horizontal star chunks from each quadrant
                // then move down 100 to the next chunk down

                // quadrant 1

                /*  _______
                 * |___|_x_|
                 * |___|___|
                 */

                if (yPositions[0] < 0) {

                    for (int x = 1; x < screenSize.x; x = x + 100) {

                        stars.add(new StarChunk(x, yPositions[0]));
                        starChunksLoaded++;
                    }
                    
                    yPositions[0] += 100;
                }
                // quadrant 2

                /*  _______
                 * |_x_|___|
                 * |___|___|
                 */
                
                
                for (int x = -1; x > -screenSize.x; x = x - 100) {

                    stars.add(new StarChunk(x, yPositions[1]));
                    starChunksLoaded++;
                }

                yPositions[1] += 100;

                // quadrant 3

                /*  _______
                 * |___|___|
                 * |_x_|___|
                 */
                for (int x = -1; x < -screenSize.x; x = x - 100) {

                    stars.add(new StarChunk(x, yPositions[2]));
                    starChunksLoaded++;
                }

                yPositions[2] += 100;

                // quadrant 4

                /*  _______
                 * |___|___|
                 * |___|_x_|
                 */
                
                if (yPositions[3] < 10000)
                {
                for (int x = 1; x < screenSize.x; x = x + 100) {

                    stars.add(new StarChunk(x, yPositions[3]));
                    starChunksLoaded++;
                }

                yPositions[3] += 100;
                }

                if (starChunksLoaded >= ((100 * 100) * 4)) {
                    loading = false;
                }

                repaint();
            } else {
                if (forward) {

                    player.move(true);
                    middleOfPlayer.x = player.getLocation().x + player.getImage().getWidth() / 2;
                    middleOfPlayer.y = player.getLocation().y + player.getImage().getHeight() / 2;
                    repaint();
                }
                if (rotateRight) {

                    player.rotate(true); // positive
                    repaint();
                }

                if (rotateLeft) {
                    player.rotate(false); // negitive
                    repaint();
                }

                if (!forward && player.isMoving()) {
                    player.move(false);
                    repaint();
                }

            }
            camera.getLocation().x = player.getLocation().x - (getWidth() / 2);
            camera.getLocation().y = player.getLocation().y - (getHeight() / 2);

            middleOfPlayer.x = player.getLocation().x - camera.getLocation().x + player.getImage().getWidth() / 2;
            middleOfPlayer.y = player.getLocation().y - camera.getLocation().y + player.getImage().getHeight() / 2;
            
            repaint();
            
            timer.schedule(new MovementTimer(player), timerDelay);
            
        }
    }
    int keyCode;

    @Override
    public void CheckKeyPressed(KeyEvent e) {
        keyCode = e.getKeyCode();
        /*
         * 0 = stationary
         * 1 = both thrusters
         * 2 = right thruster
         * 3 = left thruster
         */
        switch (keyCode) {
            case KeyEvent.VK_W: {
                forward = true;
                player.changeImage(1);
            }
            break;

            case KeyEvent.VK_D: {
                rotateRight = true;
                if (!forward) {
                    player.changeImage(3);
                }
                if (rotateLeft) {
                    player.changeImage(0);
                }

            }
            break;

            case KeyEvent.VK_A: {
                rotateLeft = true;
                if (!forward) {
                    player.changeImage(2);
                }
                if (rotateRight) {
                    player.changeImage(0);
                }
            }
            break;

            case KeyEvent.VK_SHIFT: {
                player.speedBoost();
            }
            break;
                
            case KeyEvent.VK_SPACE: {
                player.shoot(new Point2D.Double(middleOfPlayer.x + camera.getLocation().x,
                        middleOfPlayer.y + camera.getLocation().y), player.getAngle() - 90);
                
            }

        } // end switch

    } // end method

    @Override
    public void CheckKeyReleased(KeyEvent e) {
        keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_W: {
                forward = false;
                player.changeImage(0);
                if (rotateRight) {
                    player.changeImage(3);
                } else if (rotateLeft) {
                    player.changeImage(2);
                }
            }
            break;

            case KeyEvent.VK_D: {
                rotateRight = false;
                if (!forward) {
                    player.changeImage(0);
                } else {
                    player.changeImage(1);
                }
            }
            break;

            case KeyEvent.VK_A: {
                rotateLeft = false;
                if (!forward) {
                    player.changeImage(0);
                } else {
                    player.changeImage(1);
                }
            }

            break;

            case KeyEvent.VK_SHIFT: {
                player.stopSpeedBoosting();
            }

        } // end switch
    }

    public class Panel extends JPanel {

        int width;
        int height;

        public Panel(int width, int height) {
            this.width = width;
            this.height = height;
            setSize(width, height);
            setVisible(true);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (!loading) {
                renderer.drawScreen(g, player, middleOfPlayer.x, middleOfPlayer.y, Math.ceil(FPS), stars, camera, player.getShots());
            } else {
                renderer.drawLoadingScreen(g, starChunksLoaded / 400, width, height);
            }
        }
    }

    private float getFrameRate() {
        long time = System.currentTimeMillis();

        updateTimes.add(new Long(time));

        float timeInSec = (time - updateTimes.get(0)) / 1000f;

        float fps = 30f / timeInSec;

        if (updateTimes.size() == 31) {
            updateTimes.remove(0);
        }

        return fps;
    }
}