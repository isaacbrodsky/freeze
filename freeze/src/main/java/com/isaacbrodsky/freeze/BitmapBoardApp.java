/**
 * 
 */
package com.isaacbrodsky.freeze;

import static com.isaacbrodsky.freeze.game.ZBoard.BOARD_DEPTH;
import static com.isaacbrodsky.freeze.game.ZBoard.BOARD_HEIGHT;
import static com.isaacbrodsky.freeze.game.ZBoard.BOARD_WIDTH;
import static com.isaacbrodsky.freeze.graphics.CharacterRenderer.CHAR_HEIGHT;
import static com.isaacbrodsky.freeze.graphics.CharacterRenderer.CHAR_WIDTH;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.isaacbrodsky.freeze.elements.Element;
import com.isaacbrodsky.freeze.elements.ElementResolver;
import com.isaacbrodsky.freeze.elements.Player;
import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.elements.data.Stats;
import com.isaacbrodsky.freeze.filehandling.ZSaver;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.ZBoard;
import com.isaacbrodsky.freeze.graphics.ElementColoring;
import com.isaacbrodsky.freeze.utils.UnsignedDataOutputStream;

/**
 * @author isaac
 * 
 */
public class BitmapBoardApp {

	private File openedFile;

	private void setOpenFile(File f) {
		openedFile = f;
	}

	public BitmapBoardApp() {
		openedFile = null;
	}

	/**
	 * Shows UI
	 */
	public void go() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		final JFrame f = new JFrame(ZGame.APP_SHORT + " BirmapBoard Util");
		final JButton loadButton = new JButton("LOAD IMAGE...");
		final JButton snapButton = new JButton("Save to .BRD...");

		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser fc = new JFileChooser();
					if (fc.showOpenDialog(f) == JFileChooser.APPROVE_OPTION) {
						File nFile = fc.getSelectedFile();

						setOpenFile(nFile);
					}
				} catch (Exception exc) {
					exc.printStackTrace();
					JOptionPane.showMessageDialog(f,
							"Error: " + exc.toString(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		snapButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (openedFile != null) {
						JFileChooser fc = new JFileChooser();
						fc.setSelectedFile(new File(openedFile.getParent()
								+ openedFile.getName().substring(
										0,
										Math.min(openedFile.getName().length(),
												8)) + ".brd"));
						if (fc.showSaveDialog(f) == JFileChooser.APPROVE_OPTION) {
							File nFile = fc.getSelectedFile();

							convertImage(openedFile, nFile);
						}
					}
				} catch (Exception exc) {
					exc.printStackTrace();
					JOptionPane.showMessageDialog(f,
							"Error: " + exc.toString(), "Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.add(loadButton, BorderLayout.NORTH);
		f.add(snapButton, BorderLayout.SOUTH);

		f.pack();
		f.setVisible(true);
	}

	public static void main(String args[]) {
		try {
			if (java.awt.GraphicsEnvironment.isHeadless()
					|| (args.length > 0 && args[0].equalsIgnoreCase("/cons"))) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						System.in));
				System.out.print("File: ");
				String f = br.readLine();
				System.out.print("Out file: ");
				String out = br.readLine();

				if (f == null || out == null)
					return; // to satisfy findbugs
				
				convertImage(new File(f), new File(out));

				return;
			}

			new BitmapBoardApp().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void convertImage(File inF, File outF) throws Exception {
		BufferedImage img = null;

		img = ImageIO.read(inF);

		// 60*8
		// 25*16

		Element[][][] elements = new Element[BOARD_WIDTH][BOARD_HEIGHT][BOARD_DEPTH];

		final int w = CHAR_WIDTH, h = CHAR_HEIGHT;
		ElementResolver resolve = new ElementResolver(EmuMode.ZZT);

		for (int x = 0; x < BOARD_WIDTH; x++) {
			for (int y = 0; y < BOARD_HEIGHT; y++) {
				BlockData b = getBlockData(img, x, y, w, h);

				elements[x][y][0] = resolveBlockData(resolve, b, x, y);
			}
		}

		Player p = new Player();
		p.createInstance(new SaveData(0x04, 0X1F));
		p.setXY(0, 0);
//		p.setStats(Stats.DEFAULT_PLAYER_STATS);
		p.setStats(new Stats());
		elements[0][0][0] = p;

		Board board = new ZBoard(elements, inF.getName(), 255, 0, 0, 0, 0, 0,
				0, "", 0, 0, 0, 0, 0);
		board.setACE(p);

		ZSaver sav = new ZSaver();
		UnsignedDataOutputStream out = new UnsignedDataOutputStream(
				new FileOutputStream(outF));
		sav.saveBoard(board, out);
		out.close();

		System.out.println("Done!");
	}

	/**
	 * @param b
	 * @return
	 */
	private static Element resolveBlockData(ElementResolver resolve,
			BlockData b, int x, int y) {
		Element ret = null;

		int fore = ElementColoring.codeFromColor(b.a) & 0x0F;
		int back = (ElementColoring.codeFromColor(b.b) & 0x0F) << 4;
		int type = 0x1B;
		// if (b.intensity >= 32 && b.intensity <= 96 && back == 0) {
		// type = 0x17;
		// }
		if (fore == 0 && back == 0) {
			type = 0x00; // Empty
		}

		ret = resolve.resolve(type, (fore | back) & 0x7F, x, y);

		return ret;
	}

	/**
	 * @param img
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	private static BlockData getBlockData(BufferedImage img, int x, int y,
			int w, int h) {
		HashMap<Color, Integer> cd = new HashMap<Color, Integer>(16);

		x = x * w;
		y = y * h;

		for (int i = x; i < x + w; i++) {
			for (int j = y; j < y + h; j++) {
				Color c = new Color(img.getRGB(i, j));

				if (cd.containsKey(c)) {
					int num = cd.remove(c);
					cd.put(c, num + 1);
				} else {
					cd.put(c, 1);
				}
			}
		}

		Color highest = findHighest(cd);
		int hInt = cd.get(highest);
		cd.remove(highest);
		Color nextHighest = findHighest(cd);
		int nHInt;
		if (nextHighest == null) {
			nHInt = 0;
			nextHighest = highest;
		} else {
			nHInt = cd.get(nextHighest);
		}

		BlockData dat = new BlockData(highest, nextHighest, hInt - nHInt);

		return dat;
	}

	private static Color findHighest(HashMap<Color, Integer> cd) {
		int i = 0;
		Color ret = null;
		for (Color c : cd.keySet()) {
			if (cd.get(c) > i) {
				ret = c;
				i = cd.get(ret);
			}
		}

		return ret;
	}

	private static class BlockData {
		public Color a, b;
		public int intensity;

		public BlockData(Color a, Color b, int i) {
			this.a = a;
			this.b = b;
			this.intensity = i;
		}
	}
}
