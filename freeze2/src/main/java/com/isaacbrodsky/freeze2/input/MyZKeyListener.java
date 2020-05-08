package com.isaacbrodsky.freeze2.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyZKeyListener implements KeyListener {
    public final Set<Integer> keysDown;
    public final Set<Integer> keysUp;
    public final Set<Integer> keysDownConsumed;
    public final List<KeyEvent> keysTyped;

    public MyZKeyListener() {
        this.keysDown = new HashSet<>();
        this.keysUp = new HashSet<>();
        this.keysDownConsumed = new HashSet<>();
        this.keysTyped = new ArrayList<>();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keysDown.add(e.getKeyCode());
        keysUp.remove(e.getKeyCode());
    }

    @Override
    public synchronized void keyReleased(KeyEvent e) {
        keysUp.add(e.getKeyCode());
    }

    @Override
    public synchronized void keyTyped(KeyEvent e) {
        if (!keysDown.contains(KeyEvent.VK_ESCAPE)) {
            if (e.getKeyChar() <= 0xFF) {
                keysTyped.add(e);
            } else {
                System.err.println(
                        "Warning: filtering key type "
                                + e.getKeyCode() + " ("
                                + e.getKeyChar()
                                + "). Is your keyboard non-US ASCII?");
            }
        }
    }

    public synchronized void beginFrame() {
        // Don't consume keys which have already been seen but are now up
        for (Integer key : keysDownConsumed) {
            if (keysUp.remove(key)) {
                keysDown.remove(key);
            }
        }
        keysDownConsumed.clear();
    }

    public synchronized void clearState() {
        keysTyped.clear();
        keysDownConsumed.addAll(keysDown);
        // Only remove keys from keysdown after they have been consumed at least once
        keysDown.removeAll(keysUp);
        keysUp.clear();
    }
}
