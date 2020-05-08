/**
 *
 */
package com.isaacbrodsky.freeze2.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

import com.isaacbrodsky.freeze2.ZGame;

/**
 * @author isaac
 * 
 */
public final class Renderer {
	public static final int DISPLAY_WIDTH = 60;
	public static final int DISPLAY_HEIGHT = 25;

	public static final ElementColoring SYS_COLOR = ElementColoring.forNames(
			NamedColor.WHITE, NamedColor.BLACK);

	private GraphicsBlock[][][] lastScreen;
	private GraphicsBlock[][] screen;
	private CharacterRenderer charRenderer;
	private BufferedImage[] bufs;
	private int curBuf;

	private int scaling;

	public Renderer() throws Exception {
		this(1);
	}

	public Renderer(int scaling) throws Exception {
		this.scaling = scaling;

		bufs = new BufferedImage[2];

		lastScreen = new GraphicsBlock[2][DISPLAY_WIDTH + Sidebar.SIDEBAR_WIDTH][DISPLAY_HEIGHT];
		clear();
		curBuf = 0;

		// create surfaces compatible with the display - if they aren't
		// then drawing to the display becomes an EXTREMELY heavy
		// operation (as opposed to nearly free!)
		GraphicsEnvironment env = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice device = env.getDefaultScreenDevice();
		GraphicsConfiguration config = device.getDefaultConfiguration();
		for (int i = 0; i < bufs.length; i++) {
			bufs[i] = config.createCompatibleImage(
					(DISPLAY_WIDTH + Sidebar.SIDEBAR_WIDTH)
							* CharacterRenderer.CHAR_WIDTH * scaling,
					DISPLAY_HEIGHT * CharacterRenderer.CHAR_HEIGHT * scaling);
		}

		charRenderer = new CharacterRenderer(
		// "out/",
				// ".png");
				this.getClass().getClassLoader(), "img/char/", ".png");
	}

	public void set(int x, int y, GraphicsBlock b) {
		if (x >= DISPLAY_WIDTH + Sidebar.SIDEBAR_WIDTH || y >= DISPLAY_HEIGHT
				|| x < 0 || y < 0)
			return;// TODO
		lastScreen[0][x][y] = null;
		lastScreen[1][x][y] = null;
		screen[x][y] = b;
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public GraphicsBlock get(int x, int y) {
		return screen[x][y];
	}

	public void renderFill(int x, int y, int w, int h, char c,
			ElementColoring color) {
		GraphicsBlock gb = new GraphicsBlock(color, c);
		for (int i = x; i < w + x; i++) {
			for (int j = y; j < h + y; j++) {
				set(i, j, gb);
			}
		}
	}

	public void renderText(int x, int y, int i, ElementColoring color) {
		renderText(x, y, String.valueOf((char) i), color);
	}

	public void renderText(int x, int y, String s, ElementColoring color) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (x >= DISPLAY_WIDTH + Sidebar.SIDEBAR_WIDTH) {
				y++;
				x = 0;
			}
			if (y >= DISPLAY_HEIGHT) {
				return;
			}

			set(x, y, new GraphicsBlock(color, c));
			x++;
		}
	}

	public void renderThrowable(Throwable t) {
		t.printStackTrace();

		int dispLine = 0;

		renderText(0, dispLine++, ZGame.APP, ElementColoring.forNames(NamedColor.GRAY,
				NamedColor.BLACK));
		renderText(0, dispLine++, t.toString(), ElementColoring.forNames(NamedColor.RED,
				NamedColor.BLACK));
		for (int i = t.toString().length(); i > DISPLAY_WIDTH; i -= DISPLAY_WIDTH) {
			if (i == t.toString().length())
				continue;
			dispLine++;
		}

		ElementColoring ec = ElementColoring.forNames(NamedColor.YELLOW, NamedColor.BLACK);

		StackTraceElement e[] = t.getStackTrace();
		for (int i = 0; i < e.length; i++) {
			renderText(0, dispLine++, "    " + e[i].toString(), ec);
		}
	}

	/**
	 * Prepares this rendering context for output (via renderOut(Graphics).
	 * 
	 * <p>
	 * This method must be called each time before renderOut.
	 * 
	 * @param blinking
	 * @param debugMode
	 */
	public void render(boolean blinking, boolean debugMode) {
		BufferedImage b = bufs[curBuf];
		Graphics2D g = (Graphics2D) b.getGraphics();

		for (int i = 0; i < DISPLAY_HEIGHT; i++) {
			for (int j = 0; j < DISPLAY_WIDTH + Sidebar.SIDEBAR_WIDTH; j++) {
				if (screen[j][i] == null)
					continue;
				else if (lastScreen[curBuf][j][i] != null
						&& screen[j][i].equals(lastScreen[curBuf][j][i]))
					continue;
				else
					charRenderer.render(g, j * CharacterRenderer.CHAR_WIDTH, i
							* CharacterRenderer.CHAR_HEIGHT, 1, screen[j][i]
							.getCharIndex(), screen[j][i].getColoring(),
							blinking);

				lastScreen[curBuf][j][i] = screen[j][i];
			}
		}

		g.dispose();
		curBuf++;
	}

	public void renderOut(Graphics g) {
		g.drawImage(bufs[curBuf - 1], 0, 0,
				(DISPLAY_WIDTH + Sidebar.SIDEBAR_WIDTH)
						* CharacterRenderer.CHAR_WIDTH * scaling,
				DISPLAY_HEIGHT * CharacterRenderer.CHAR_HEIGHT * scaling, 0, 0,
				(DISPLAY_WIDTH + Sidebar.SIDEBAR_WIDTH)
						* CharacterRenderer.CHAR_WIDTH, DISPLAY_HEIGHT
						* CharacterRenderer.CHAR_HEIGHT, null);
		if (curBuf == bufs.length)
			curBuf = 0;
	}

	/**
	 *
	 */
	public void clear() {
		screen = new GraphicsBlock[DISPLAY_WIDTH + Sidebar.SIDEBAR_WIDTH][DISPLAY_HEIGHT];
	}

	public int getScaling() {
		return scaling;
	}

	// TODO: flashing state is totally disconnected from the rest of the class
	private int flashing = 0;

	public ElementColoring getFlashingColor() {
		return new ElementColoring((flashing % 8) + 8 << 4);
	}

	public void flashingTick() {
		flashing++;
	}
}
