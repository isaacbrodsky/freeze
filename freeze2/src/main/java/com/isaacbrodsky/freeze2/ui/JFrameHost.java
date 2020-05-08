/**
 * 
 */
package com.isaacbrodsky.freeze2.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.UIManager;

import com.isaacbrodsky.freeze2.ZGame;

/**
 * Hosts a <code>ZGame</code> instance in its own <code>JFrame</code>
 * 
 * @author isaac
 */
public class JFrameHost implements ZHost {
	private HashMap<String, String> params;

	private JFrame f;
	private ZGame z;

	public JFrameHost(boolean fullscreen, HashMap<String, String> params) {
		this.params = params;

		f = new JFrame();
		f.setTitle(ZGame.APP_SHORT);
		// TODO: Listen for window close and prompt for confirmation
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setFocusTraversalKeysEnabled(false);
		f.setResizable(false);
		f.setBackground(Color.BLACK);
		f.setIgnoreRepaint(true);// who knows
		try {
			f.setIconImage(ImageIO.read(getClass().getClassLoader()
					.getResource("img/ico/zappico.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		z = new ZGame(this);
		z.init();
		z.setPreferredSize(new Dimension(640 * z.getScaling(), 400 * z.getScaling()));

		f.add(z);

		if (fullscreen) {
			GraphicsEnvironment environment = GraphicsEnvironment
					.getLocalGraphicsEnvironment();
			GraphicsDevice device = environment.getDefaultScreenDevice();
			DisplayMode dm = device.getDisplayMode();

			f.setUndecorated(true);
			f.setResizable(false);
			f.setSize(dm.getWidth(), dm.getHeight());
			device.setFullScreenWindow(f);
		} else {
			f.pack();
		}
		f.setVisible(true);
	}

	public static void main(String[] args) {
		boolean fullscreen = false;
		if (java.awt.GraphicsEnvironment.isHeadless()) {
			System.err.println("Could not create user interface.");
			System.exit(1);
		}

		HashMap<String, String> params = new HashMap<String, String>();
		for (int i = 0; i < args.length; i++) {
			if (args[i].equalsIgnoreCase("fullscreen")) {
				fullscreen = true;
			} else {
				String key = args[i];
				String val = "";
				i++;
				if (i < args.length)
					val = args[i];
				params.put(key, val);
			}
		}

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		new JFrameHost(fullscreen, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze2.ui.ZHost#getParent()
	 */
	@Override
	public Component getHostComponenet() {
		return f;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze2.ui.ZHost#getParameter(java.lang.String)
	 */
	@Override
	public String getParameter(String name) {
		if (params != null)
			return params.get(name);
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze2.ui.ZHost#getDialogParent()
	 */
	@Override
	public Component getDialogParent() {
		return f;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze2.ui.ZHost#quit()
	 */
	@Override
	public void quit() {
		System.exit(0);// drastic
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze2.ui.ZHost#setTitle(java.lang.String)
	 */
	public void setTitle(String t) {
		f.setTitle(t);
	}
}
