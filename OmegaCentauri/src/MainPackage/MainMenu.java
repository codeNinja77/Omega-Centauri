package MainPackage;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.sound.sampled.Clip;

/**
 * @author Michael Kieburtz
 * @author Davis Freeman
 */
public class MainMenu {

    private GameStartListener startListener;

    private MediaLoader loader;
    private ArrayList<String> imagePaths = new ArrayList<String>();
    private ArrayList<String> soundPaths = new ArrayList<String>();

    private ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();
    private ArrayList<Clip> sounds = new ArrayList<Clip>();

    private ArrayList<TwinklingStarChunk> stars = new ArrayList<TwinklingStarChunk>();

    private Settings settings;

    private int STARTNOHOVER = 0;
    private final int STARTHOVER = 1; 
    private final int CLOSENOHOVER = 2;
    private final int CLOSEHOVER = 3;
    private final int SETTINGSNOHOVER = 4;
    private final int SETTINGSHOVER = 5;

    private boolean startHover = false;
    private boolean closeHover = false;
    private boolean settingsHover = false;

    private Rectangle startRectangle;
    private Rectangle closeRectangle;
    private Rectangle settingsRectangle;

    private boolean active;

    private Point size;
    private Dimension screenSize;

    private Rectangle screenRect;

    public MainMenu(OmegaCentauri game) {
        active = true;
        loader = new MediaLoader();
        startListener = game;

        imagePaths.add("resources/StartButtonNoHover.png");
        imagePaths.add("resources/StartButtonHover.png");
        imagePaths.add("resources/CloseButtonNoHover.png");
        imagePaths.add("resources/CloseButtonHover.png");
        imagePaths.add("resources/SettingsButtonNoHover.png");
        imagePaths.add("resources/SettingsButtonHover.png");

        images = loader.loadImages(imagePaths);
        images = Calculator.toCompatibleImages(images);

        soundPaths.add("resources/Mouseclick.wav");
        sounds = loader.loadSounds(soundPaths);

        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        size = new Point(game.getWidth(), game.getHeight());

        settings = new Settings(screenSize, new Dimension(size.x, size.y));

        screenRect = new Rectangle(0, 0, size.x, size.y);

        // load enough stars for the entire screen;
        for (int x = 0; x < screenSize.width; x += 100) {
            for (int y = 0; y < screenSize.height; y += 100) {
                stars.add(new TwinklingStarChunk(x, y));
            }
        }

        setRects();
    }

    public void draw(Graphics g) {
        if (!settings.isActive()) {
            BufferedImage drawingImage = new BufferedImage(screenSize.width, screenSize.height, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = drawingImage.createGraphics();

            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, size.x, size.y);

            for (TwinklingStarChunk s : stars) {
                if (s.getBoundingRect().intersects(screenRect)) {
                    s.draw(g2d);
                }
            }

            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, size.y - 100, size.x, size.y - 100);

            g2d.setColor(Color.RED);

            if (startHover) {
                g2d.drawImage(images.get(STARTHOVER), startRectangle.x, startRectangle.y, null);
            } else {
                g2d.drawImage(images.get(STARTNOHOVER), startRectangle.x, startRectangle.y, null);
            }

            if (closeHover) {
                g2d.drawImage(images.get(CLOSEHOVER), closeRectangle.x, closeRectangle.y, null);
            } else {
                g2d.drawImage(images.get(CLOSENOHOVER), closeRectangle.x, closeRectangle.y, null);
            }

            if (settingsHover) {
                g2d.drawImage(images.get(SETTINGSHOVER), settingsRectangle.x, settingsRectangle.y, null);
            } else {
                g2d.drawImage(images.get(SETTINGSNOHOVER), settingsRectangle.x, settingsRectangle.y, null);
            }

            g2d.setColor(Color.CYAN);
            g2d.drawLine(0, size.y - 100, size.x, size.y - 100);
            //g2d.drawLine(settingsRectangle.x, settingsRectangle.y + settingsRectangle.height, size.x, settingsRectangle.y + settingsRectangle.height);

//        g2d.setColor(Color.RED);
//        g2d.draw(startRectangle);
//        g2d.draw(closeRectangle);
//        g2d.draw(settingsRectangle);
            g.drawImage(drawingImage, 0, 0, null);
        } else {
            settings.draw(g);
        }
    }
    
    public void checkMousePressed(Point location)
    {
        if (!settings.isActive() && active) {
            if (startRectangle.contains(location)) {
                active = false;
                startListener.gameStart();
                sounds.get(0).setFramePosition(0);
                sounds.get(0).start();
            }
            if (closeRectangle.contains(location)) {
//                sounds.get(0).setFramePosition(0);
//                sounds.get(0).start();                
                System.exit(0);
            }
            if (settingsRectangle.contains(location)) {
                sounds.get(0).setFramePosition(0);
                sounds.get(0).start();
                settings.setActive(true);
            }
        }
        else if (settings.isActive())
        {
            settings.checkMousePressed(location);
        }
    }

    public void checkMouseExited()
    {
        if (!settings.isActive() && active) {
            startHover = false;
            closeHover = false;
            settingsHover = false;
        } 
        else if (settings.isActive())
        {
            settings.checkMouseExited();
        }if (!settings.isActive() && active) {
            startHover = false;
            closeHover = false;
            settingsHover = false;
        } 
        else if (settings.isActive())
        {
            settings.checkMouseExited();
        }
    }
    
    public void checkMouseMoved(Point location)
    {
        if (!settings.isActive() && active) {
            if (startRectangle.contains(location)) {
                startHover = true;
            } else if (closeRectangle.contains(location)) {
                closeHover = true;
            } else if (settingsRectangle.contains(location)) {
                settingsHover = true;
            } else {
                startHover = false;
                closeHover = false;
                settingsHover = false;
            }
        } else if (settings.isActive())
        {
            settings.checkMouseMoved(location);
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Point getSize() {
        return size;
    }

    public void setSize(int width, int height) {
        size.setLocation(width, height);
        settings.setWindowSize(new Dimension(width, height));
        setRects();
    }

    private void setRects() {

        startRectangle = new Rectangle(
                size.x / 2  - images.get(STARTNOHOVER).getWidth() / 2,
                size.y - 75,
                images.get(STARTNOHOVER).getWidth(),
                images.get(STARTNOHOVER).getHeight()
        );

        closeRectangle = new Rectangle(
                size.x - 100 - images.get(CLOSENOHOVER).getWidth(),
                size.y - 75,
                images.get(CLOSENOHOVER).getWidth(),
                images.get(CLOSENOHOVER).getHeight()
        );

        settingsRectangle = new Rectangle(
                100,
                size.y - 75,
                images.get(SETTINGSNOHOVER).getWidth(),
                images.get(SETTINGSNOHOVER).getHeight()
        );

        screenRect.setBounds(0, 0, size.x, size.y);
    }

    public Settings getSettings() {
        return settings;
    }
}
