package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public final class Panel extends JPanel {
    private static final int INITIAL_PANEL_HEIGHT = 600;
    private static final int INITIAL_PANEL_WIDTH = 800;
    private static final int INITIAL_ICON_X_VELOCITY = 5;
    private static final int INITIAL_ICON_Y_VELOCITY = 3;
    private static final int ICON_WIDTH_RATIO = 3;
    private static final int ICON_HEIGHT_RATIO = 4;
    private static final int TIMER_DELAY_MS = 30;
    private static final Color[] ICON_COLORS = {
            new Color(255, 72, 72),
            new Color(72, 163, 255),
            new Color(64, 224, 160),
            new Color(255, 210, 63),
            new Color(255, 105, 180)
    };
    private final Icon icon;
    private final Timer timer;
    private final BufferedImage baseIconImage;
    private BufferedImage tintedIconImage;
    private Color currentIconColor;
    private int maxX;
    private int maxY;

    public Panel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(INITIAL_PANEL_WIDTH, INITIAL_PANEL_HEIGHT));

        int iconWidth = getIconWidthForPanel(INITIAL_PANEL_WIDTH);
        int iconHeight = getIconHeightForPanel(INITIAL_PANEL_HEIGHT);

        int iconX = (INITIAL_PANEL_WIDTH - iconWidth) / 2;
        int iconY = (INITIAL_PANEL_HEIGHT - iconHeight) / 2;
        icon = new Icon(iconX, iconY, iconWidth, iconHeight, INITIAL_ICON_X_VELOCITY, INITIAL_ICON_Y_VELOCITY);
        baseIconImage = loadIconImage();
        currentIconColor = ICON_COLORS[0];
        tintedIconImage = tintIcon(baseIconImage, currentIconColor);
        updateIconMetrics(INITIAL_PANEL_WIDTH, INITIAL_PANEL_HEIGHT);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                updateIconMetrics(getWidth(), getHeight());
            }
        });

        timer = new Timer(TIMER_DELAY_MS, event -> {
            updatePosition();
            repaint();
        });

        timer.start();

    }

    private void updatePosition() {
        icon.setX(icon.getX() + icon.getxVelocity());
        icon.setY(icon.getY() + icon.getyVelocity());

        handleIconWallCollision();
    }

    private void handleIconWallCollision() {
        boolean collided = false;

        if (icon.getX() < 0) {
            icon.setX(0);
            icon.setxVelocity(-icon.getxVelocity());
            collided = true;
        }
        if (icon.getX() >= maxX) {
            icon.setX(maxX);
            icon.setxVelocity(-icon.getxVelocity());
            collided = true;
        }
        if (icon.getY() < 0) {
            icon.setY(0);
            icon.setyVelocity(-icon.getyVelocity());
            collided = true;
        }
        if (icon.getY() >= maxY) {
            icon.setY(maxY);
            icon.setyVelocity(-icon.getyVelocity());
            collided = true;
        }

        if (collided) {
            updateIconColor();
        }
    }

    private void updateIconMetrics(int panelWidth, int panelHeight) {
        icon.setWidth(getIconWidthForPanel(panelWidth));
        icon.setHeight(getIconHeightForPanel(panelHeight));
        maxX = Math.max(panelWidth - icon.getWidth(), 0);
        maxY = Math.max(panelHeight - icon.getHeight(), 0);
        icon.setX(Math.min(icon.getX(), maxX));
        icon.setY(Math.min(icon.getY(), maxY));
    }

    private int getIconWidthForPanel(int panelWidth) {
        return Math.max(panelWidth / ICON_WIDTH_RATIO, 1);
    }

    private int getIconHeightForPanel(int panelHeight) {
        return Math.max(panelHeight / ICON_HEIGHT_RATIO, 1);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g.create();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawDvdIcon(graphics);
        } finally {
            graphics.dispose();
        }
    }

    private void drawDvdIcon(Graphics2D graphics) {
        graphics.drawImage(tintedIconImage, icon.getX(), icon.getY(), icon.getWidth(), icon.getHeight(), null);
    }

    private BufferedImage loadIconImage() {
        try {
            BufferedImage image = ImageIO.read(getClass().getResource("/dvd-icon.png"));
            if (image == null) {
                throw new IllegalStateException("Could not load /dvd-icon.png");
            }
            return image;
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load /dvd-icon.png", exception);
        }
    }

    private void updateIconColor() {
        Color nextColor = currentIconColor;
        while (ICON_COLORS.length > 1 && nextColor.equals(currentIconColor)) {
            nextColor = ICON_COLORS[ThreadLocalRandom.current().nextInt(ICON_COLORS.length)];
        }
        currentIconColor = nextColor;
        tintedIconImage = tintIcon(baseIconImage, currentIconColor);
    }

    private BufferedImage tintIcon(BufferedImage source, Color tint) {
        BufferedImage tintedImage = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = tintedImage.createGraphics();
        try {
            graphics.drawImage(source, 0, 0, null);
            graphics.setComposite(AlphaComposite.SrcAtop);
            graphics.setColor(tint);
            graphics.fillRect(0, 0, source.getWidth(), source.getHeight());
        } finally {
            graphics.dispose();
        }
        return tintedImage;
    }
}
