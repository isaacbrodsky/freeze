/**
 * 
 */
package com.isaacbrodsky.freeze;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.sound.midi.Instrument;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;

import com.isaacbrodsky.freeze.filehandling.LocalWorldList;
import com.isaacbrodsky.freeze.filehandling.ZLoader;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.graphics.CharacterRenderer;
import com.isaacbrodsky.freeze.graphics.ElementColoring;
import com.isaacbrodsky.freeze.graphics.GraphicsBlock;
import com.isaacbrodsky.freeze.graphics.Renderer;
import com.isaacbrodsky.freeze.graphics.Sidebar;
import com.isaacbrodsky.freeze.menus.CharInput;
import com.isaacbrodsky.freeze.menus.UIInteraction;
import com.isaacbrodsky.freeze.sound.MIDISoundThread;
import com.isaacbrodsky.freeze.utils.*;

/**
 * Provides test and utility methods
 * 
 * @author isaac
 * 
 */
public class ZTestMain {
	// public static void main(String[] args) {
	// System.exit(1);// don't call; this is for debug stuff
	// someMethod2();
	// }

	/**
	 * 
	 */
	private static void someMethod2() {
		// http://java.sun.com/products/java-media/sound/soundbanks.html

		try {
			int aaa = -4;
			StringBackedOutputStream sbos = new StringBackedOutputStream();
			UnsignedDataOutputStream out = new UnsignedDataOutputStream(sbos);
			out.writeLEShort(aaa);
			StringBackedInputStream sbis = new StringBackedInputStream(sbos
					.getData());
			UnsignedDataInputStream in = new UnsignedDataInputStream(sbis);
			System.out.println((int) in.readUnsignedLEShort());

		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			MidiDevice.Info[] inf = MidiSystem.getMidiDeviceInfo();
			for (int i = 0; i < inf.length; i++) {
				System.out.println(i + ": " + inf[i]);

				MidiDevice m = null;
				try {
					m = MidiSystem.getMidiDevice(inf[i]);
					m.open();
					if (m instanceof Synthesizer) {
						Synthesizer syn = (Synthesizer) m;
						Instrument[] instr = syn.getDefaultSoundbank()
								.getInstruments();

						syn.loadAllInstruments(syn.getDefaultSoundbank());
						for (int j = 0; j < instr.length; j++) {
							System.out.println(j + ": " + instr[j]);
						}
						final MidiChannel[] mc = syn.getChannels();

						for (int j = 0; j < mc.length; j++) {
							System.out.println(j + ": " + mc[j] + " "
									+ mc[j].getProgram());
							// mc[j].programChange(0, 1);

							MIDISoundThread.playChannel(mc[j], new int[] { 39,
									40, 39 }, new int[] { 127, 127, 127 },
									new int[] { 500, 500, 500 });
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (m != null)
						m.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	/**
	 * @param args
	 */
	public static void someMethod(String[] args) {
		ZLoader l = new ZLoader();
		try {
			GameController game = l
					.load(
							new LocalWorldList("/tmp/"),
							"TEST.ZZT");

			// codePageSplitter();
			Renderer r = new Renderer(1);
			CharacterRenderer cr = new CharacterRenderer("out/", ".png");
			renderTestPattern(r);
			r.renderText(1, 1, "Hello World", new ElementColoring(
					ElementColoring.colorFromName("WHITE"), ElementColoring
							.colorFromName("BLACK"), null));
			r.set(10, 3, new GraphicsBlock(new ElementColoring(ElementColoring
					.colorFromName("WHITE"), ElementColoring
					.colorFromName("DARKBLUE"), null), 2));
			r.render(true, false);
			BufferedImage img = new BufferedImage(60 * 8, 25 * 16,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) img.getGraphics();
			// r.renderOut(g);
			long start = System.currentTimeMillis();
			cr.render(g, 0, 0, 1, 2, new ElementColoring("RED", "DARKGRAY"),
					false);
			long end = System.currentTimeMillis();
			g.dispose();

			System.out.println("Frame totality in " + (end - start) + "ms");
			ImageIO.write(img, "png", new File("screen.png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void codePageSplitter() throws Exception {
		BufferedImage src = ImageIO.read(new File("codepage437.png"));
		int count = 0;
		byte r[] = { (byte) 168, 0 };
		byte g[] = { (byte) 168, 0 };
		byte b[] = { (byte) 168, 0 };
		IndexColorModel icm = new IndexColorModel(1, 2, r, g, b);
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 32; j++) {
				BufferedImage dest = new BufferedImage(8, 16,
						BufferedImage.TYPE_BYTE_INDEXED, icm);
				Graphics gr = dest.getGraphics();
				int sx = (j * 8) + j;
				int sy = i * 16;
				System.out.println(count + ": " + sx + " . " + sy);
				gr.drawImage(src, 0, 0, 8, 16, sx, sy, sx + 8, sy + 16, null);
				gr.dispose();
				ImageIO.write(dest, "PNG", new File("out/" + count + ".png"));
				count++;
			}
		}
	}

	/**
	 * @param renderer
	 */
	public static void renderTestPattern(Renderer r) {
		Random rand = new Random();
		int c = rand.nextInt(256), ca = 15, cb = 0;
		for (int y = 0; y < 25; y++) {
			for (int x = 0; x < 60; x++) {
				r.set(x, y, new GraphicsBlock(new ElementColoring(
						ElementColoring.colorFromCode(ca), ElementColoring
								.colorFromCode(cb), null), c));
				c++;
				ca--;
				if (ca == 0) {
					cb++;
					ca = 15;
				}
				if (cb == 16)
					cb = 0;
				if (c == 256)
					c = 0;

				// if (x == 0 || y == 0 || x == 59 || y == 24)
				// r.set(x, y, new GraphicsBlock(new ElementColoring(
				// ElementColoring.colorFromName("YELLOW"),
				// ElementColoring.colorFromName("BLACK"), null),
				// 178));
			}
		}
	}

	/**
	 * @param renderer
	 */
	public static void renderSidebarPattern(Renderer r) {
		Random rand = new Random();
		int c = rand.nextInt(256), ca = 15, cb = rand.nextInt(15);
		for (int y = 0; y < Sidebar.SIDEBAR_HEIGHT; y++) {
			for (int x = Sidebar.SIDEBAR_OFFSET; x < Sidebar.SIDEBAR_OFFSET
					+ Sidebar.SIDEBAR_WIDTH; x++) {
				r.set(x, y, new GraphicsBlock(new ElementColoring(
						ElementColoring.colorFromCode(ca), ElementColoring
								.colorFromCode(cb), null), c));
				c++;
				ca--;
				if (ca == 0) {
					cb++;
					ca = 15;
				}
				if (cb == 16)
					cb = 0;
				if (c == 256)
					c = 0;
			}
		}
	}

	/**
	 * @return
	 */
	public static UIInteraction generateASCIITable(boolean useHex) {
		final int CHAR_REF_HEIGHT = 20;

		StringBuilder sb = new StringBuilder();
		StringBuilder cur;

		ArrayList<StringBuilder> list = new ArrayList<StringBuilder>(
				CHAR_REF_HEIGHT);
		for (int i = 0; i < CHAR_REF_HEIGHT; i++)
			list.add(new StringBuilder());

		int y = 0;
		for (int i = 0; i < 256; i++) {
			cur = list.get(y);

			if (cur.length() != 0)
				cur = cur.append(" ");
			cur = cur.append((char) (i == 10 ? ' ' : i)).append(" ");
			cur = cur.append(TimeAndMathUtils.padInt(i, (useHex ? 2 : 3), '0',
					(useHex ? 16 : 10)));

			y++;
			if (y == CHAR_REF_HEIGHT) {
				y = 0;
			}
		}

		for (int i = 0; i < list.size(); i++) {
			cur = list.get(i);
			if (cur == null)
				continue;
			sb.append(cur).append('\n');
		}

		return new CharInput("Character Reference" + (useHex ? " (Hex)" : "")
				+ "\n" + sb.toString() + "Any key to continue", null, true,
				null, 0, false);
	}

}
