package com.zackmurry.zmdb.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import static java.awt.event.KeyEvent.VK_ESCAPE;

public class KeyDetector extends KeyAdapter implements KeyListener {
    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == VK_ESCAPE) {
            //System.exit(0);
        }
    }
}

