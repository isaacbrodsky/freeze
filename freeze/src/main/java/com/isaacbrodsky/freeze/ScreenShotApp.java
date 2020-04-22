/**
 * 
 */
package com.isaacbrodsky.freeze;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import com.isaacbrodsky.freeze.filehandling.Loader;
import com.isaacbrodsky.freeze.filehandling.LocalWorldList;
import com.isaacbrodsky.freeze.filehandling.SuperZLoader;
import com.isaacbrodsky.freeze.filehandling.ZLoader;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.graphics.CharacterRenderer;
import com.isaacbrodsky.freeze.graphics.Renderer;

/**
 * @author isaac
 * 
 */
public class ScreenShotApp {
	private File openedFile;

	private void setOpenFile(File f) {
		openedFile = f;
	}

	public ScreenShotApp() {
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

		final JFrame f = new JFrame(ZGame.APP_SHORT + " ScreenShot Util");
		final JButton loadButton = new JButton("LOAD WORLD...");
		final JComboBox boardsList = new JComboBox();
		final JButton snapButton = new JButton("Snap!");

		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					boardsList.removeAllItems();

					JFileChooser fc = new JFileChooser();
					if (fc.showOpenDialog(f) == JFileChooser.APPROVE_OPTION) {
						File nFile = fc.getSelectedFile();

						Loader l = null;
						if (nFile.getAbsolutePath().endsWith("szt"))
							l = new SuperZLoader();
						else
							l = new ZLoader();
						GameController game = l.load(new LocalWorldList(nFile
								.getParent()
								+ "/"), nFile.getName());

						for (int i = 0; i < game.getBoardList().size(); i++) {
							Board b = game.getBoardList().get(i);
							boardsList.addItem(i + ": "
									+ b.getState().boardName);
						}

						boardsList.setSelectedIndex(game.getState().startBoard);

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
					if (boardsList.getSelectedIndex() != -1
							&& openedFile != null) {
						JFileChooser fc = new JFileChooser();
						fc.setSelectedFile(new File(openedFile
								.getAbsolutePath()
								+ ".screenshot.png"));
						if (fc.showSaveDialog(f) == JFileChooser.APPROVE_OPTION) {
							File nFile = fc.getSelectedFile();

							renderBoard(openedFile, boardsList
									.getSelectedIndex(), nFile
									.getAbsolutePath());
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
		f.add(boardsList, BorderLayout.CENTER);
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
				System.out.print("Board or -1: ");
				int b = Integer.parseInt(br.readLine());
				System.out.print("Out file: ");
				String out = br.readLine();

				if (f == null || out == null)
					return; // to satisfy findbugs

				renderBoard(new File(f), b, out);

				return;
			}

			new ScreenShotApp().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void renderBoard(File file, int b, String out) {
		try {
			ZLoader l = new ZLoader();
			GameController game = l.load(new LocalWorldList(file.getParent()
					+ "/"), file.getName());

			Renderer r = new Renderer(1);

			if (b != -1)
				game.setBoard(b);
			else
				game.setBoard(game.getState().startBoard);
			// 640 * 480
			BufferedImage bimg = new BufferedImage(CharacterRenderer.CHAR_WIDTH
					* game.getBoard().getWidth(), CharacterRenderer.CHAR_HEIGHT
					* game.getBoard().getHeight(),
					BufferedImage.TYPE_4BYTE_ABGR);

			game.render(r, false);
			game.render(r, false);
			game.render(r, false); // prevent flashing
			r.render(false, false);
			Graphics g = bimg.getGraphics();
			r.renderOut(g);
			ImageIO.write(bimg, "png", new FileOutputStream(new File(out)));
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}
}
