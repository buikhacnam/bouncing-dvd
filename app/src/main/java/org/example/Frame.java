package org.example;

import javax.swing.*;

public final class Frame extends JFrame {

    public Frame() {
        super("Retro DVD Bouncer");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        setResizable(false);
//        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setContentPane(new Panel());
        pack();
        setVisible(true);
    }
}
