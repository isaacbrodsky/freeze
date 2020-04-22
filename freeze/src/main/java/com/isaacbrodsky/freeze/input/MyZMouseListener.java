/**
 *
 */
package com.isaacbrodsky.freeze.input;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;

public class MyZMouseListener implements MouseMotionListener, MouseListener {
	private final HashSet<Integer> mousestate;

	private int mx, my;
	private int lastx, lasty;
	private boolean on;

	/**
	 * @param zGame
	 */
	public MyZMouseListener() {
		mousestate = new HashSet<Integer>();

		on = false;
	}

	public boolean clearState() {
		boolean b = false;
		synchronized (mousestate) {
			b |= mousestate.remove(KeyEvent.VK_RIGHT);
			b |= mousestate.remove(KeyEvent.VK_LEFT);
			b |= mousestate.remove(KeyEvent.VK_DOWN);
			b |= mousestate.remove(KeyEvent.VK_UP);
			b |= mousestate.remove(KeyEvent.VK_SHIFT);
		}
		return b;
	}

	public boolean getState(int key) {
		synchronized (mousestate) {
			return mousestate.contains(key);
		}
	}

	@Override
	public void mouseEntered(MouseEvent me) {
		mx = me.getX();
		my = me.getY();
		lastx = 0;
		lasty = 0;
		on = true;
	}

	@Override
	public void mouseExited(MouseEvent me) {
		on = false;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			synchronized (mousestate) {
				mousestate.add(KeyEvent.VK_SHIFT);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			synchronized (mousestate) {
				mousestate.remove(KeyEvent.VK_SHIFT);
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (!on)
			return;

		int changeX = mx - e.getX();
		int changeY = my - e.getY();

		int totalX = lastx + changeX;
		int totalY = lasty + changeY;

		if (Math.abs(totalX) > 10 || Math.abs(totalY) > 10) {
			synchronized (mousestate) {
				if (Math.abs(changeX) > Math.abs(changeY)) {
					// x change

					if (changeX > 0) {
						// right
						mousestate.add(KeyEvent.VK_LEFT);
					} else {
						// left
						mousestate.add(KeyEvent.VK_RIGHT);
					}
				} else {
					// y change

					if (changeY > 0) {
						// down
						mousestate.add(KeyEvent.VK_UP);
					} else {
						// up
						mousestate.add(KeyEvent.VK_DOWN);
					}
				}
				lastx = lasty = 0;
			}
		} else {
			lastx = totalX;
			lasty = totalY;
		}

		mx = e.getX();
		my = e.getY();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			mousePressed(e);
			mouseMoved(e);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}
}