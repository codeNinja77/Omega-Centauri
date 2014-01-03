package MainPackage;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

// @author Michael Kieburtz and Davis Freeman
public class Renderer {

    private File fontFile;
    private Font fpsFont;
    private DecimalFormat df = new DecimalFormat("0.#");
    
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

    public void drawScreen(Graphics g, Player player, double xRot, double yRot, double fps,
            ArrayList<StarChunk> stars, Camera camera, ArrayList<Shot> shots) {

        
        BufferedImage bufferedImage = new BufferedImage(camera.getSize().x, camera.getSize().y, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = (Graphics2D)bufferedImage.getGraphics(); // turns it into 2d graphics
        
        
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

        g2d.drawString(String.valueOf(df.format(fps)), 155, 33);

        g2d.setFont(fpsFont.deriveFont(60f));

        g2d.drawString("FPS:", 10, 50);
        
        // move and draw the bullets
        for (Shot shot : shots)
        {
            shot.updateLocation();
            if (camera.insideView(shot.getLocation(), shot.getSize()))
                shot.draw(g2d, camera.getLocation());
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

        // transform the player and draw it
        AffineTransform origXform = g2d.getTransform();
        AffineTransform newXform = (AffineTransform) (origXform.clone());

        g2d.setPaint(new TexturePaint(player.getImage(), new Rectangle2D.Float(0, 0, player.getImage().getWidth(), player.getImage().getHeight())));
        newXform.setToIdentity();

        newXform.rotate(Math.toRadians(player.getAngle()), xRot, yRot);

        g2d.setTransform(newXform);

        g2d.drawImage(player.getImage(), (int) (player.getLocation().x - camera.getLocation().x),
                (int) (player.getLocation().y - camera.getLocation().y), null);
        
        g.drawImage(bufferedImage, 0, 0, null);
       
    }

    public void drawLauncher(Graphics g, BufferedImage startButtonImage) {
        Graphics2D g2d = (Graphics2D)g;
        
        g2d.drawImage(startButtonImage, 100, 0, null);
    }

    public void drawLoadingScreen(Graphics g, int percentDone, int width, int height) {
        
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        Graphics2D g2d = (Graphics2D)bufferedImage.getGraphics();
        
        // enable anti-aliasing
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.RED);
        g2d.drawRect((width / 2) - 100, (height / 2) - 50, 200, 50);
        g2d.setColor(Color.GREEN);
        g2d.fillRect((width / 2) - 100, (height / 2) - 50, percentDone * 2, 50);

        g2d.setFont(fpsFont);
        g2d.setColor(Color.CYAN);
        g2d.drawString("Loading...", width / 2 - 75, height / 2 - 75);
        
        g.drawImage(bufferedImage, 0, 0, null);
    }

    
}
