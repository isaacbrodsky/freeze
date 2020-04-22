/**
 * 
 */
package com.isaacbrodsky.freeze.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

/**
 * @author isaac
 * 
 */
public class CharacterRenderer {
	public static final int CHAR_WIDTH = 8;
	public static final int CHAR_HEIGHT = 16;
	public static final int NUM_CHARS = 256;

	private BufferedImage[] chars;

	private CharacterRenderer() {
		chars = new BufferedImage[NUM_CHARS];
	}

	public CharacterRenderer(ClassLoader cl, String loc, String ext)
			throws Exception {
		this();
		for (int i = 0; i < NUM_CHARS; i++) {
			loadImage(cl.getResource(loc + i + ext), i);
		}
	}

	/**
	 * Create a new character renderer. This constructor loads 256 images from
	 * the location supplied in loc by taking loc+num+ext for each num (0-255).
	 * 
	 * @param loc
	 *            Base location for the character images
	 * @param ext
	 *            Additional extension for each character image (you must
	 *            include "." if you want it as part of the extension)
	 * @throws Exception
	 *             A character image does does meet expected formatting
	 *             requirements.
	 */
	public CharacterRenderer(String loc, String ext) throws Exception {
		this();
		for (int i = 0; i < NUM_CHARS; i++) {

			loadImage(new File(loc + i + ext).toURI().toURL(), i);
		}
	}

	/**
	 * @param loc
	 * @param ext
	 * @param i
	 * @throws IOException
	 * @throws Exception
	 */
	private void loadImage(URL loc, int i) throws IOException, Exception {
		chars[i] = ImageIO.read(loc);

		if (chars[i].getWidth() != CHAR_WIDTH
				|| chars[i].getHeight() != CHAR_HEIGHT)
			throw new Exception("Width or height of char #" + i
					+ " does not meet expected. (Got: " + chars[i].getWidth()
					+ "/" + chars[i].getHeight() + " instead of " + CHAR_WIDTH
					+ "/" + CHAR_HEIGHT + ")");
		if (!(chars[i].getColorModel() instanceof IndexColorModel))
			throw new Exception("Char #" + i + " has a non-indexed color model");
	}

	/**
	 * Paints an individual character
	 * 
	 * @param g
	 *            Graphics object to paint to
	 * @param x
	 *            X location (top-left)
	 * @param y
	 *            Y location (top-left)
	 * @param scaling
	 *            Scaling for this character. Scaling values of less than 1 are
	 *            not allowed.
	 * @param charIndex
	 *            Character index in the current character set to paint
	 * @param color
	 *            Coloring information
	 * @param blinkOn
	 *            If true, only the background color will be painted, the
	 *            character/foreground will not be used
	 * @throws ArrayIndexOutOfBoundsException
	 *             charIndex < 0 or charIndex > 255
	 * @throws IllegalArgumentException
	 *             Scaling value < 1
	 */
	public void render(Graphics g, int x, int y, int scaling, int charIndex,
			ElementColoring color, boolean blinkOn)
			throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
		if (charIndex < 0 || charIndex >= chars.length)
			throw new ArrayIndexOutOfBoundsException(
					"Attempted use a char above 255 or below 0");
		if (scaling < 1)
			throw new IllegalArgumentException("Scaling value may only be >0");

		BufferedImage img = chars[charIndex];

		// System.out.println(createColorModel(color.fore, color.back));
		// System.out.println(img.getColorModel());

		int xStart = x * scaling;
		int yStart = y * scaling;
		if (color.getBlinking() && blinkOn) {
			g.setColor(color.getBack());
			g.fillRect(xStart, yStart, xStart + (CHAR_WIDTH * scaling), yStart
					+ (CHAR_HEIGHT * scaling));
		} else {
			BufferedImage coloredImg = new BufferedImage(createColorModel(color
					.getFore(), color.getBack()), img.getRaster(), false, null);

			g.drawImage(coloredImg, xStart, yStart, xStart
					+ (CHAR_WIDTH * scaling), yStart + (CHAR_HEIGHT * scaling),
					0, 0, CHAR_WIDTH, CHAR_HEIGHT, null);
		}
	}

	/**
	 * Creates a 2-color color model to adjust the foreground and background
	 * colors of a character image to match those supplied in fc and bc.
	 * 
	 * <p>
	 * From: http://helpdesk.objects.com.au/java/changing-the-colormodel-of-a-
	 * bufferedimage
	 * 
	 * @param fc
	 *            New foreground color. This will replace the image's first
	 *            color.
	 * @param bc
	 *            New background color. This will replace the image's second
	 *            color.
	 * @return A color model consisting of fc and bc, in that order.
	 */
	private static ColorModel createColorModel(Color fc, Color bc) {
		byte[] r = { (byte) fc.getRed(), (byte) bc.getRed() };
		byte[] g = { (byte) fc.getGreen(), (byte) bc.getGreen() };
		byte[] b = { (byte) fc.getBlue(), (byte) bc.getBlue() };

		return new IndexColorModel(1, 2, r, g, b);
	}
}
