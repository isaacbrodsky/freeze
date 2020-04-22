/**
 * 
 */
package com.isaacbrodsky.freeze.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.net.URL;

import javax.swing.JApplet;

import com.isaacbrodsky.freeze.ZGame;

/**
 * Hosts a <code>ZGame</code> instance in a <code>JApplet</code>
 * 
 * @author isaac
 * 
 */
public class JAppletHost extends JApplet implements ZHost {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4748007447087728732L;

	private ZGame z;

	@Override
	public void init() {
		setFocusTraversalKeysEnabled(false);

		// TODO
		if (getParameter("fullscreen") != null) {
			if (Boolean.parseBoolean(getParameter("fullscreen"))) {
				new JFrameHost(true, null);
				return;
			}
		}
		if (getParameter("windowed") != null) {
			if (Boolean.parseBoolean(getParameter("windowed"))) {
				new JFrameHost(false, null);
				return;
			}
		}
		z = new ZGame(this);
		z.init();
		setLayout(new BorderLayout());
		add(z, BorderLayout.CENTER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.ui.ZHost#getHostComponenet()
	 */
	@Override
	public Component getHostComponenet() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.ui.ZHost#getDialogParent()
	 */
	@Override
	public Component getDialogParent() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.ui.ZHost#quit()
	 */
	@Override
	public void quit() {
		try {
			String url = getParameter("outPage");
			if (url != null)
				getAppletContext().showDocument(new URL(url));
		} catch (Exception e) {

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.ui.ZHost#setTitle(java.lang.String)
	 */
	public void setTitle(String t) {
		try {
			getAppletContext().showDocument(
					new URL("javascript:window.document.title=\"" + t + "\";"));
		} catch (Exception e) {

		}
	}
}
