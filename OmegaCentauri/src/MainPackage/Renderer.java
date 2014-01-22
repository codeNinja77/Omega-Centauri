package MainPackage;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

// @author Michael Kieburtz and Davis Freeman
public class Renderer {

    private File fontFile;
    private Font fpsFont;

    public Renderer(int cameraWidth, int cameraHeight) {

        fontFile = new File("src/resources/BlackHoleBB_ital.ttf");

        try {
            fpsFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(36f);
        } catch (FontFormatException ex) {
            System.err.println("Bad font");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.err.println("Bad file");
            ex.printStackTrace();
        }

    }

    public void drawScreen(Graphics g, Player player, double xRot, double yRot, int fps,
            ArrayList<StarChunk> stars, Camera camera, ArrayList<Shot> shots, String version) {

        BufferedImage bufferedImage = new BufferedImage(camera.getSize().x, camera.getSize().y, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics(); // turns it into 2d graphics


        // draw backround rectangle
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, 1000, 1000);

        // enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw stars
        for (StarChunk starChunk : stars) {
            if (camera.insideView(starChunk.getLocation(), starChunk.getSize())) {
                starChunk.draw(g2d, camera.getLocation());
            }
        }
        // draw fps info
        g2d.drawImage(player.getImage(4), null, 0, 0);
        g2d.setFont(fpsFont.deriveFont(32f));
        g2d.setColor(Color.CYAN);

        g2d.drawString(String.valueOf(fps), 155, 33);

        g2d.setFont(fpsFont.deriveFont(60f));

        g2d.drawString("FPS:", 10, 50);
        
        // draw version info'
        g2d.setFont(new Font("Arial", Font.TRUETYPE_FONT, 12));
        g2d.setColor(Color.WHITE);
        g2d.drawString("Version: " + version, 890, 10);

        // move and draw the bullets
        try {
            for (Shot shot : shots) {
                
            if (camera.insideView(shot.getLocation(), shot.getSize())) {
                shot.draw(g2d, camera.getLocation());
            }
        }
            
        } catch (java.util.ConcurrentModificationException ex) {
            System.err.println("Concurrent Modification Execption occured");
        }
        // draw the minimap
        g2d.setColor(Color.BLACK);
        g2d.fillRect(794, 372, 200, 200);

        g2d.setColor(new Color(0, 255, 0, 50));
        g2d.fillRect(794, 372, 200, 200);

        g2d.setColor(Color.GREEN);
        g2d.drawRect(794, 372, 200, 200);

        g2d.setColor(Color.CYAN);
        Ellipse2D.Double minimapPlayer = new Ellipse2D.Double(794 + 100 + player.getLocation().x / 100, 372 + 100 + player.getLocation().y / 100, 1, 1);
        g2d.draw(minimapPlayer);
        
        
        // draw the player
        
        player.draw(g2d, camera.getLocation());
        
        g.drawImage(bufferedImage, 0, 0, null);
        
        g2d.dispose();
        g.dispose();
    }

    public void drawLauncher(Graphics g, BufferedImage startButtonImage) {
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.drawImage(startButtonImage, 100, 0, null);
        g2d.dispose();
        g.dispose();
    }

    public void drawLoadingScreen(Graphics g, int percentDone, int width, int height) {

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();

        // enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.BLUE);
        g2d.drawRect((width / 2) - 200, (height / 2) - 50, 400, 10);
        g2d.setColor(Color.CYAN);
        g2d.fillRect((width / 2) - 200, (height / 2) - 50, percentDone * 4, 10);

        g2d.setFont(fpsFont);
        g2d.setColor(new Color(0x00CECE)); // hex codes rock
        g2d.drawString("Loading...", width / 2 - 75, height / 2 - 75);

        g.drawImage(bufferedImage, 0, 0, null);
        
        g2d.dispose();
        g.dispose();
    }
}
