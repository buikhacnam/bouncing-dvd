package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public final class Panel extends JPanel {
    private static final int INITIAL_PANEL_HEIGHT = 600;
    private static final int INITIAL_PANEL_WIDTH = 600;
    private static final int INITIAL_ICON_X_VELOCITY = 5;
    private static final int INITIAL_ICON_Y_VELOCITY = 3;
    private static final int ICON_SIZE = 20;
    private static final int TIMER_DELAY_MS = 16;
    private final Icon icon;
    private final Timer timer;
    private int maxX;
    private int maxY;

    public Panel() {
        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(INITIAL_PANEL_WIDTH, INITIAL_PANEL_HEIGHT));
        updateCollisionBounds(INITIAL_PANEL_WIDTH, INITIAL_PANEL_HEIGHT);

        int iconX = (INITIAL_PANEL_WIDTH - ICON_SIZE) / 2;
        int iconY = (INITIAL_PANEL_HEIGHT - ICON_SIZE) / 2;
        icon = new Icon(iconX, iconY, ICON_SIZE, ICON_SIZE, INITIAL_ICON_X_VELOCITY, INITIAL_ICON_Y_VELOCITY);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                updateCollisionBounds(getWidth(), getHeight());
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
        if (icon.getX() < 0) {
            icon.setX(0);
            icon.setxVelocity(-icon.getxVelocity());
        }
        if (icon.getX() >= maxX) {
            icon.setX(maxX);
            icon.setxVelocity(-icon.getxVelocity());
        }
        if (icon.getY() < 0) {
            icon.setY(0);
            icon.setyVelocity(-icon.getyVelocity());
        }
        if (icon.getY() >= maxY) {
            icon.setY(maxY);
            icon.setyVelocity(-icon.getyVelocity());
        }
    }

    private void updateCollisionBounds(int panelWidth, int panelHeight) {
        maxX = Math.max(panelWidth - ICON_SIZE, 0);
        maxY = Math.max(panelHeight - ICON_SIZE, 0);
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
        graphics.setColor(Color.RED);
        graphics.fillOval(icon.getX(), icon.getY(), icon.getWidth(), icon.getHeight());
    }
}
