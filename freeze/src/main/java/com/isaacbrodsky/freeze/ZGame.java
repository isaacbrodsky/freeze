/**
 *
 */
package com.isaacbrodsky.freeze;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.isaacbrodsky.freeze.elements.Element;
import com.isaacbrodsky.freeze.elements.Player;
import com.isaacbrodsky.freeze.elements.ZObject;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.elements.oop.OOPHelpers;
import com.isaacbrodsky.freeze.filehandling.DefaultSuperZWorldCreator;
import com.isaacbrodsky.freeze.filehandling.DefaultZWorldCreator;
import com.isaacbrodsky.freeze.filehandling.Hiscores;
import com.isaacbrodsky.freeze.filehandling.Loader;
import com.isaacbrodsky.freeze.filehandling.LocalWorldList;
import com.isaacbrodsky.freeze.filehandling.Saver;
import com.isaacbrodsky.freeze.filehandling.SuperZLoader;
import com.isaacbrodsky.freeze.filehandling.SuperZSaver;
import com.isaacbrodsky.freeze.filehandling.WorldList;
import com.isaacbrodsky.freeze.filehandling.ZLoader;
import com.isaacbrodsky.freeze.filehandling.ZSaver;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.game.SuperZGameController;
import com.isaacbrodsky.freeze.game.editor.EditorController;
import com.isaacbrodsky.freeze.game.editor.EditorMode;
import com.isaacbrodsky.freeze.graphics.ElementColoring;
import com.isaacbrodsky.freeze.graphics.Renderer;
import com.isaacbrodsky.freeze.graphics.Sidebar;
import com.isaacbrodsky.freeze.input.MyZMouseListener;
import com.isaacbrodsky.freeze.menus.Menu;
import com.isaacbrodsky.freeze.menus.MenuCallback;
import com.isaacbrodsky.freeze.menus.MenuUtils;
import com.isaacbrodsky.freeze.menus.MultiInput;
import com.isaacbrodsky.freeze.menus.SelectInput;
import com.isaacbrodsky.freeze.menus.TextInput;
import com.isaacbrodsky.freeze.menus.TypingInteraction;
import com.isaacbrodsky.freeze.menus.UIInteraction;
import com.isaacbrodsky.freeze.menus.Menu.SendMode;
import com.isaacbrodsky.freeze.ui.ZHost;
import com.isaacbrodsky.freeze.utils.TimeAndMathUtils;

/**
 * TODO break stuff out as much as possible ?
 * <p>
 * 
 * @author isaac
 */
public class ZGame extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 3492010434372148040L;

	/**
	 *
	 */
	private static final ElementColoring DEBUG_COLOR = new ElementColoring(
			"BLACK", "GRAY");
	private static final ElementColoring DEBUG_ALERT_COLOR = new ElementColoring(
			"DARKRED", "GRAY");

	public static final String APP_SHORT = "Freeze v. 1.0.0alpha";
	public static final String APP = APP_SHORT
			+ ", Copyright 2011, 2020 Isaac Brodsky";

	/**
	 * Name of current game file
	 */
	private String gameFile;

	private WorldList worldList;

	/**
	 * Rendering sub system
	 */
	private Renderer renderer;

	/**
	 * Used in the initialization process to coordinate where menu items appear
	 * on the screen.
	 * 
	 * <p>
	 * Come to think of it, I could replace the initialization thing with a
	 * <code>MultiInput</code>
	 */
	private int debugLine;

	/**
	 * Current controller system
	 */
	private GameController game = null;

	/**
	 * Used as part of the input system as a poor, poor, terrible input polling
	 * system.
	 */
	private HashSet<Integer> keystates;

	/**
	 * Used as part of the menu and input system
	 */
	private ArrayList<KeyEvent> keytypes;

	/**
	 * This object will not be null, even if mouse input is disabled. If mouse
	 * input is disabled, this object will simply not have been connected to
	 * receive any events.
	 */
	private MyZMouseListener mouse;

	/**
	 * 
	 */
	private MasterLoopThread loop;

	private int blinkingCounter;
	private boolean blinking;

	// fps stuff
	private long usedTime;
	private long startTime;
	private long currTime;
	private int psps = 0;
	private long lastFps = -1;

	// menu stuff
	private UIInteraction overMenu;
	private UIInteraction overInput;

	// state
	private GameAppState state;
	private EmuMode emu;

	/**
	 * If this is <code>null</code> no error has occured, if set then an error
	 * has occured and the current game should terminate if it has not already
	 * done so.
	 * 
	 * <p>
	 * TODO centralize error reporting
	 */
	private Throwable inError = null;

	/**
	 * Debug
	 */
	private long start = 0, end = 0;
	private boolean redrawBoard = false, debugMode = false;

	/**
	 * Up reference to the enclosing system
	 */
	private ZHost host;

	private Timer timer;

	public ZGame(ZHost host) {
		emu = EmuMode.DEFAULT;
		this.host = host;
		timer = new Timer();
	}

	public void init() {
		// we want tab etc
		setFocusTraversalKeysEnabled(false);

		setBackground(DEBUG_COLOR.getBack());

		// setup parameters (e.g. rendering)
		int scalingMode = 1;
		worldList = null;
		try {
			// if (host.getParameter("worldlist") != null)
			// worldList = new LocalWorldList(host.getParameter("worldlist"));
			String debugStr = host.getParameter("debug");
			if (debugStr != null)
				debugMode = Boolean.parseBoolean(debugStr);
			String scalingStr = host.getParameter("scaling");
			if (scalingStr != null)
				scalingMode = Integer.parseInt(scalingStr);
		} catch (Exception e) {
			e.printStackTrace();
			// disregard parameter exceptions
		}

		try {
			renderer = new Renderer(scalingMode);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Render init failure: "
					+ e.toString());
			e.printStackTrace();
		}
		debugLine = 0;
		state = GameAppState.INIT;
		// renderer.renderSidebar(state, sidebar);
		// ZTestMain.renderTestPattern(renderer);

		// show a pretty pattern in the sidebar
		ZTestMain.renderSidebarPattern(renderer);
		renderer.renderText(0, debugLine++,
				"Freeze v. 1.0.0alpha", Renderer.SYS_COLOR);
		renderer.renderText(0, debugLine++, "Copyright 2011, 2020 Isaac Brodsky",
				Renderer.SYS_COLOR);
		renderer.renderText(0, debugLine++, "", Renderer.SYS_COLOR);
		debugLine++;
		renderer.renderText(0, debugLine++, "Configuration: ",
				Renderer.SYS_COLOR);
		debugLine++;

		blinking = false;
		blinkingCounter = 3;

		// start listening for keys
		keystates = new HashSet<Integer>();
		keytypes = new ArrayList<KeyEvent>();
		if (host.getHostComponenet() != null) {
			host.getHostComponenet().addKeyListener(new MyZKeyListener());
		} else {
			addKeyListener(new MyZKeyListener());
		}

		mouse = new MyZMouseListener();

		// now start game (needed to handle input to the config
		// process)
		loop = new MasterLoopThread();

		// begin configuration prompts
		overInput = new SelectInput(
				"Input mode: \001K\002eyboard, \001M\002ouse?", Arrays
						.asList(new String[] { "K", "M" }),
				new ConfigurationCallback(), true, true, "input", 0, debugLine);
		loop.start();

		if (host.getParameter("autoconfig") != null) {
			String auto[] = host.getParameter("autoconfig").split(":");
			String input = auto[0];
			String debug = auto[1];
			String gamedir = auto[2];
			String emu = auto[3];
			overInput.keyPress(input.charAt(0));
			overInput.tick();
			overInput.keyPress(debug.charAt(0));
			overInput.tick();
			for (int i = 0; i < gamedir.length(); i++)
				overInput.keyPress(gamedir.charAt(i));
			overInput.keyPress('\n');
			overInput.tick();
			overInput.keyPress(emu.charAt(0));
			overInput.tick();
		}
	}

	/**
	 * Show the world load menu - the given suffix determines what types of
	 * files the menu will show (e.g. only those ending in ZZT or SZT or SAV.)
	 * 
	 * <p>
	 * The SAV parameter has a special function of changing the title of the
	 * menu to restore as opposed to load.
	 */
	private void showLoadMenu(boolean cancelable, String suffix) {
		String loadSelects = worldList.buildLoadMenu(suffix);

		if (!loadSelects.equals(""))
		{
			String title;
			if (suffix.equalsIgnoreCase("sav")) {
				title = "Restore Save Game";
			} else {
				title = "Select World";
			}
			overMenu = new Menu(title, loadSelects.toString(),
					new WorldLoadCallback(), SendMode.LASTLINE, cancelable, suffix);
			redrawBoard = false;
			state = GameAppState.LOAD;
		}
		else
		{
			overMenu = new Menu("No files", "$No files were found.\n\nYou may have no files of the\nspecified type, or your game directory\nmay be incorrect.", null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		start = System.currentTimeMillis();

		paintGame();

		renderer.renderText(62, 1, " -- Freeze  -- ", new ElementColoring(
				"BLACK", "GRAY"));
		if (emu.equals(EmuMode.SUPERZZT)) {
			renderer.renderText(66, 1, 'S', new ElementColoring("PURPLE",
					"GRAY"));// S is for Super!
			renderer.renderText(67, 1, 'F', new ElementColoring("YELLOW",
					"GRAY"));
			renderer.renderText(68, 1, 'r',
					new ElementColoring("WHITE", "GRAY"));
			renderer.renderText(69, 1, 'e',
					new ElementColoring("GREEN", "GRAY"));
			renderer
					.renderText(70, 1, 'e', new ElementColoring("CYAN", "GRAY"));
			renderer.renderText(71, 1, 'z', new ElementColoring("RED",
					"GRAY"));
			renderer.renderText(72, 1, 'e', new ElementColoring("BLACK",
					"GRAY"));
		}

		paintMenus();
		paintDebug();
		paintPaused();

		renderer.render(blinking, debugMode); // heavy
		renderer.renderOut(g); // lightweight

		end = System.currentTimeMillis() - start;
	}

	/**
	 *Draws the game and sidebar, and cycles the flashing state if need be.
	 */
	private void paintGame() {
		if (game != null) {
			if (redrawBoard) {
				game.render(renderer, blinking);

				if (!game.isPaused() && game.getMenu() == null
						&& overMenu == null && overInput == null)
					renderer.flashingTick();
			}
		}

		if (inError == null && state != GameAppState.INIT)
			Sidebar.renderSidebar(renderer, state, game);
	}

	/**
	 * Draws any menus or inputs at the ZGame level.
	 */
	private void paintMenus() {
		if (overInput != null) {
			overInput.render(renderer, 0, true);
		}

		if (overMenu != null) {
			overMenu.render(renderer, 0, true);
		}
	}

	/**
	 * Draws paused message.
	 */
	private void paintPaused() {
		if (game != null) {
			if (game.isPaused()) {
				renderer.renderText(62, 2, " --  PAUSED  -- ",
						new ElementColoring("WHITE", "DARKBLUE", true));
			}
		}
	}

	/**
	 * Draws debugging information.
	 */
	private void paintDebug() {
		if (debugMode) {
			renderer.renderText(61, Sidebar.SIDEBAR_HEIGHT - 4,
					"                 ", DEBUG_COLOR);
			renderer.renderText(61, Sidebar.SIDEBAR_HEIGHT - 3,
					"                 ", DEBUG_COLOR);
			renderer.renderText(61, Sidebar.SIDEBAR_HEIGHT - 2,
					"                 ", DEBUG_COLOR);

			if (end != 0) {
				if (lastFps == -1)
					lastFps = 1000 / end;
				lastFps = (lastFps + (1000 / end)) / 2;
			} else {
				lastFps = -1;
			}

			renderer.renderText(61, Sidebar.SIDEBAR_HEIGHT - 4, "   Time: "
					+ Long.toString(end) + "ms", DEBUG_COLOR);
			renderer.renderText(61, Sidebar.SIDEBAR_HEIGHT - 3, "   ~FPS: ",
					DEBUG_COLOR);
			renderer.renderText(70, Sidebar.SIDEBAR_HEIGHT - 3, Long
					.toString(lastFps), lastFps >= 20 ? DEBUG_COLOR
					: DEBUG_ALERT_COLOR);
			renderer.renderText(61, Sidebar.SIDEBAR_HEIGHT - 2, "Cycles/"
					+ timer.getTimeStep() + "ms: " + psps, DEBUG_COLOR);
		}
	}

	/**
	 * Processes user input (including to menus) and has the GameController run
	 * one cycle of its simulation.
	 * 
	 * @param elapsed
	 *            I don't think anything uses this
	 */
	public void runSimulation(long elapsed) {
		if (overMenu != null || overInput != null)
			return;

		blinkingCounter -= 1;
		if (blinkingCounter == -1) {
			blinking = !blinking;
			blinkingCounter = 3;
		}

		synchronized (keystates) {
			beginMouseInput();
			checkOpenWorldSelect();

			if (game == null)
				return;

			UIInteraction m = game.getMenu();
			if (m != null) {
				processMenu(m);
			} else {
				processKeyCommands();
			}
			endMouseInput();
		}

		// and simulation stuff
		game.runSimulation(elapsed);
	}

	/**
	 * Starting point for processing user input, this method calls out to other
	 * methods to process keys relevant to playing, the title screen, etc.
	 */
	private void processKeyCommands() {
		if (state == GameAppState.TITLE) {
			processTitleKeys();
		}

		if (state == GameAppState.EDITOR) {
			processEditorKeys();
			return;
		}

		boolean pretendPlayer = false;
		if (state == GameAppState.TITLE && game.getPlayer() instanceof Player
				&& !(game instanceof SuperZGameController))
			pretendPlayer = true;

		if (state == GameAppState.PLAYING || pretendPlayer) {
			processPlayingKeys();
		}
	}

	/**
	 * Translates mouse events to <code>keystates</code>
	 */
	private void beginMouseInput() {
		if (mouse.getState(KeyEvent.VK_LEFT))
			keystates.add(KeyEvent.VK_LEFT);
		else if (mouse.getState(KeyEvent.VK_RIGHT))
			keystates.add(KeyEvent.VK_RIGHT);
		else if (mouse.getState(KeyEvent.VK_UP))
			keystates.add(KeyEvent.VK_UP);
		else if (mouse.getState(KeyEvent.VK_DOWN))
			keystates.add(KeyEvent.VK_DOWN);
		if (mouse.getState(KeyEvent.VK_SHIFT)) {
			keystates.addAll(Arrays.asList(new Integer[] { KeyEvent.VK_ENTER,
					KeyEvent.VK_SPACE, KeyEvent.VK_SHIFT }));
		}
	}

	/**
	 * Clears <code>keystates</code> as need be, to prevent infinite messages
	 * from the mouse.
	 */
	private void endMouseInput() {
		if (mouse.clearState())
			keystates.clear();
	}

	/**
	 * Processes and dispatches keys the current GameConroller, which is assumed
	 * to be an Editor.
	 */
	private void processEditorKeys() {
		int xStep = 0, yStep = 0;
		boolean shiftDown = keystates.contains(KeyEvent.VK_SHIFT);

		// if (mouse.getState(KeyEvent.VK_LEFT))
		// xStep = -1;
		// else if (mouse.getState(KeyEvent.VK_RIGHT))
		// xStep = 1;
		// else if (mouse.getState(KeyEvent.VK_UP))
		// yStep = -1;
		// else if (mouse.getState(KeyEvent.VK_DOWN))
		// yStep = 1;

		if (keystates.contains(KeyEvent.VK_LEFT))
			xStep = -1;
		else if (keystates.contains(KeyEvent.VK_RIGHT))
			xStep = 1;
		else if (keystates.contains(KeyEvent.VK_UP))
			yStep = -1;
		else if (keystates.contains(KeyEvent.VK_DOWN))
			yStep = 1;

		// mouse.clearState();

		final EditorController editor = (EditorController) game;
		editor.getCursor().moveXY(xStep, yStep, editor.getBoard().getWidth(),
				editor.getBoard().getHeight());

		if (keystates.remove(KeyEvent.VK_ENTER)) {
			editor.pushBuffer();
		}
		if (keystates.remove(KeyEvent.VK_SPACE)) {
			editor.selectPop();
		}
		if (keystates.remove(KeyEvent.VK_TAB)) {
			if (editor.getMode() == EditorMode.SELECT)
				editor.setMode(EditorMode.DRAW);
			else
				editor.setMode(EditorMode.SELECT);
		}
		if (keystates.remove(KeyEvent.VK_BACK_SLASH)) {
			overInput = new SelectInput(
					"Start game? (unsaved data will be lost)", new String[] {
							"Y", "N" }, new MenuCallback() {

						@Override
						public void menuCommand(String cmd, Object rider) {
							loop.accumulator = 0;
							if (cmd != null && cmd.equalsIgnoreCase("Y")) {
								game = editor.toGame();
								state = GameAppState.PLAYING;
								game.startPlaying();
							}
						}

					}, true, true, null, 1);
		}
		if (keystates.remove(KeyEvent.VK_I)) {
			if (shiftDown)
				overMenu = Cheats.inspectElement(game.getBoard(), editor
						.getCursor().getX(), editor.getCursor().getY());
			else
				editor.showStatInspector();
		}
		if (keystates.remove(KeyEvent.VK_B)) {
			editor.showBoardList();
		}
		if (keystates.remove(KeyEvent.VK_G)) {
			if (shiftDown)
				overMenu = Cheats.inspectGame(editor);
			else
				editor.showGameInspector();
		}
		if (keystates.remove(KeyEvent.VK_F)) {
			if (shiftDown)
				overMenu = Cheats.inspectBoard(editor.getBoard(), editor
						.getBoardIdx());
			else
				editor.showBoardInspector();
		}
		if (keystates.remove(KeyEvent.VK_X)) {
			editor.fill();
		}
		if (keystates.remove(KeyEvent.VK_P)) {
			if (shiftDown)
				editor.decBufferIdx();
			else
				editor.incBufferIdx();
		}
		if (keystates.remove(KeyEvent.VK_C)) {
			if (shiftDown)
				editor.decColorIdx();
			else
				editor.incColorIdx();
		}
		if (keystates.remove(KeyEvent.VK_V)) {
			if (shiftDown)
				editor.decBackIdx();
			else
				editor.incBackIdx();
		}
		if (keystates.remove(KeyEvent.VK_F1)) {
			editor.addElement(true);
		}
		if (keystates.remove(KeyEvent.VK_F2)) {
			editor.addElement(false);
		}
		if (keystates.remove(KeyEvent.VK_F3)) {
			overInput = ZTestMain.generateASCIITable(shiftDown);
		}
		if (keystates.remove(KeyEvent.VK_F4)) {
			editor.engageTyping();
		}
		if (keystates.remove(KeyEvent.VK_N)) {
			overInput = new SelectInput(
					"New world? (unsaved data will be lost)", new String[] {
							"Y", "N" }, new MenuCallback() {

						@Override
						public void menuCommand(String cmd, Object rider) {
							loop.accumulator = 0;
							if (cmd != null && cmd.equalsIgnoreCase("Y")) {
								game = new EditorController(
										new DefaultZWorldCreator()
												.createDefault());
								game.startPlaying();
								game.setPaused(false);
							}
						}

					}, true, true, null, 1);
		}

		checkSaveGame();
		checkQuitWorld();
	}

	/**
	 * Processes and dispatches keys to the game, which is assumed to be
	 * playing, even if in a paused state.
	 */
	private void processPlayingKeys() {
		Player p = game.getPlayer();
		int xStep = 0, yStep = 0;
		if (game.getState().health <= 0) {
			timer.setTimeStep(1);

			p.setShiftDown(false, false);
		} else {
			// if (mouse.getState(KeyEvent.VK_LEFT))
			// xStep = -1;
			// else if (mouse.getState(KeyEvent.VK_RIGHT))
			// xStep = 1;
			// else if (mouse.getState(KeyEvent.VK_UP))
			// yStep = -1;
			// else if (mouse.getState(KeyEvent.VK_DOWN))
			// yStep = 1;

			if (keystates.contains(KeyEvent.VK_LEFT))
				xStep = -1;
			else if (keystates.contains(KeyEvent.VK_RIGHT))
				xStep = 1;
			else if (keystates.contains(KeyEvent.VK_UP))
				yStep = -1;
			else if (keystates.contains(KeyEvent.VK_DOWN))
				yStep = 1;

			if (keystates.remove(KeyEvent.VK_T)) {
				// only light a torch if the current board is dark
				// and the player has a torch
				if (game.getBoard().getState().dark != 0
						&& game.getState().torches > 0) {
					game.getState().torches--;
					game.getState().tcycles = 250;// 200???
				}
			}
			if (keystates.remove(KeyEvent.VK_H)) {
				// only send H message if SuperZZT mode
				if (emu == EmuMode.SUPERZZT) {
					for (Element e : game.getBoard().getElementsByType(
							ZObject.class)) {
						e.message(game, game.getBoard(), Message.HINT);
					}
				}
			}

			boolean shiftDown = keystates.contains(KeyEvent.VK_SHIFT);
			boolean spaceDown = keystates.contains(KeyEvent.VK_SPACE);
			// if (mouse.getState(KeyEvent.VK_SHIFT)) {
			// shiftDown = spaceDown = true;
			// }

			// mouse.clearState();

			p.setShiftDown(shiftDown, spaceDown);
		}

		p.setXYStep(xStep, yStep);
		// end player stuff

		if (keystates.contains(KeyEvent.VK_P) && game.getState().health > 0)
			game.setPaused(true);
		checkSaveGame();
		checkQuitWorld();
	}

	/**
	 * Process keys relevant to the title screen.
	 */
	private void processTitleKeys() {
		if (keystates.contains(KeyEvent.VK_P)) {
			state = GameAppState.PLAYING;
			game.startPlaying();
		}
		if (keystates.contains(KeyEvent.VK_H)) {
			Hiscores hi = new Hiscores(emu, worldList, gameFile);
			overMenu = hi.show(null);
			state = GameAppState.TITLE;
			redrawBoard = true;
		}
		if (keystates.contains(KeyEvent.VK_E)) {
			try {
				Loader loader = null;
				if (emu == EmuMode.ZZT)
					loader = new ZLoader();
				else if (emu == EmuMode.SUPERZZT)
					loader = new SuperZLoader();
				game = loader.load(worldList, gameFile + "."
						+ emu.getWorldFileSuffix());
				game = new EditorController(game);
			} catch (Exception e) {
				GameController def = null;
				if (emu == EmuMode.ZZT)
					def = new DefaultZWorldCreator().createDefault();
				else if (emu == EmuMode.SUPERZZT)
					def = new DefaultSuperZWorldCreator().createDefault();
				game = new EditorController(def);
				game.reportError("Could not load world.\r\n" + e.toString()
						+ "\r\n");
				e.printStackTrace();
			}
			state = GameAppState.EDITOR;
			if (!(game instanceof EditorController)) {
				overMenu = new Menu("Failed to open editor",
						"Editor is not available for this mode.\r\n(Requires ZZT or SUPERZZT, found "
								+ emu + ")", null);
				state = GameAppState.TITLE;
			}
			game.startPlaying();
			game.setPaused(false);
		}
		checkQuitSystem();
	}

	/**
	 *Checks for, and handles if need be, input indicating the player wishes to
	 * save.
	 */
	private void checkSaveGame() {
		if (keystates.remove(KeyEvent.VK_S)) {
			String saveMsg = "Save game name:";
			String defName = "FZSAVED.SAV";

			final boolean isEditor = (state == GameAppState.EDITOR);

			if (isEditor) {
				saveMsg = "Save world name:";
				defName = game.getState().gameName + "."
						+ emu.getWorldFileSuffix();
			}

			overInput = new TextInput(saveMsg, defName, new MenuCallback() {

				@Override
				public void menuCommand(String name, Object rider) {
					loop.accumulator = 0;
					if (name == null)
						return;
					try {
						String outFile = name;
						Saver save = null;

						game.getState().isSave = (isEditor) ? 0 : 1;
						// if (isEditor)
						// game.getState().gameName = name.split("\\.")[0];

						if (emu.equals(EmuMode.ZZT))
							save = new ZSaver();
						else if (emu.equals(EmuMode.SUPERZZT))
							save = new SuperZSaver();

						if (save == null)
							throw new Exception("Illegal mode, couldn't save.");

						GameController savGame = game;
						if (game instanceof EditorController)
							savGame = ((EditorController) game).toGame();

						save.save(savGame, worldList, outFile);
					} catch (Exception e) {
						game.reportError("Could not save game to " + name
								+ ".\r\n" + e.toString());
						e.printStackTrace();
					}
				}

			}, true, null);
			keystates.clear();
			keytypes.clear();
			mouse.clearState();
		}
	}

	/**
	 * Checks for, and handles if need be, input indicating the player wishes to
	 * exit the current GameController, whatever type or state it may be.
	 */
	private void checkQuitWorld() {
		if (keystates.remove(KeyEvent.VK_Q)
				|| keystates.remove(KeyEvent.VK_ESCAPE)) {
			if (game.getState().health <= 0) {
				overInput = new TextInput("(Your score: "
						+ game.getState().score + ") Enter your name:", "",
						new MenuCallback() {

							@Override
							public void menuCommand(String name, Object rider) {
								loop.accumulator = 0;
								int score = (Integer) rider;
								if (name != null) {
									// save hiscores

									try {
										Hiscores hi = new Hiscores(emu,
												worldList, gameFile);
										hi.load();
										hi = hi.insert(hi.new HiscoreEntry(
												name, score));
										hi.save();
									} catch (IOException e) {
										inError = e;
										e.printStackTrace();
									}
								}
							}

						}, true, game.getState().score);
				keystates.clear();
				keytypes.clear();
				mouse.clearState();

				Hiscores hi = new Hiscores(emu, worldList, gameFile);
				try {
					hi.load();
				} catch (Exception e) {
					e.printStackTrace();
					// swallow exception, this will create a blank
					// hiscores list
				}
				hi = hi.insert(hi.new HiscoreEntry("-- You! --", game
						.getState().score));
				new WorldLoadCallback().menuCommand(gameFile, emu
						.getWorldFileSuffix());
				overMenu = hi.show(null);
				state = GameAppState.TITLE;
				redrawBoard = true;
			} else {
				String quitMsg = "Quit this game?";
				if (state == GameAppState.EDITOR)
					quitMsg = "Quit editor? (unsaved work will be lost)";
				overInput = new SelectInput(quitMsg, new String[] { "Y", "N" },
						new MenuCallback() {

							@Override
							public void menuCommand(String cmd, Object rider) {
								loop.accumulator = 0;
								if (cmd != null && cmd.equalsIgnoreCase("Y")) {
									// quit; ie reload the world
									// (not how ZZT does it, all the time
									// anyways)
									new WorldLoadCallback().menuCommand(game
											.getState().gameName, emu
											.getWorldFileSuffix());
								}
							}

						}, true, true, null, 1);
			}
		}
	}

	/**
	 * Checks for, and handles if need be, input indicating the player wishes to
	 * exit the entire game. (exiting being calling <code>host.quit()</code>.)
	 */
	private void checkQuitSystem() {
		if (keystates.remove(KeyEvent.VK_Q)
				|| keystates.remove(KeyEvent.VK_ESCAPE)) {
			overInput = new SelectInput("Really quit?",
					new String[] { "Y", "N" }, new MenuCallback() {

						@Override
						public void menuCommand(String cmd, Object rider) {
							loop.accumulator = 0;
							if (cmd != null && cmd.equalsIgnoreCase("Y")) {
								host.quit();
							}
						}

					}, true, true, null, 1);
		}
	}

	/**
	 * Checks for the player wishing to open the world select or restore save
	 * game screen.
	 */
	private void checkOpenWorldSelect() {
		if ((keystates.contains(KeyEvent.VK_W) || keystates
				.contains(KeyEvent.VK_R))
				&& state == GameAppState.TITLE && overMenu == null) {
			String suffix = keystates.contains(KeyEvent.VK_W) ? emu
					.getWorldFileSuffix() : "sav";
			showLoadMenu(true, suffix);
			keystates.remove(KeyEvent.VK_W);
			keystates.remove(KeyEvent.VK_R);
		}
	}

	/**
	 * Dispatches input to the given UIInteraction.
	 * 
	 * <p>
	 * keystates should be synchronized first
	 * 
	 * <p>
	 * This whole chain is kind of complicated and messy.
	 * 
	 * @param m
	 */
	private void processMenu(UIInteraction m) {
		boolean multi = false, text = false;
		if (m instanceof MultiInput)
			multi = true;

		if (MenuUtils.getFocusedInteraction(m) instanceof TypingInteraction) {
			text = true;
			TypingInteraction t = (TypingInteraction) MenuUtils
					.getFocusedInteraction(m);
			for (KeyEvent k : keytypes) {
				// does not work as intended- I hoped that looking
				// at modifiers for ALT could tell when a user used
				// ALT codes to type Alt+0219 (or whatever) but actually
				// Windows seems to mask the alt codes out.
				t.keyTyped(k.getKeyChar(), k.getModifiersEx());
			}
			// if (!multi)
			// return;
		}
		List<Integer> consumes = new ArrayList<Integer>();
		for (int i : keystates) {
			boolean consume = m.keyPress(i);
			if (!consume && multi && !text)
				consume = ((MultiInput) m).bypassKeyPress(i);
			if (consume)
				consumes.add(i);
		}
		for (int i : consumes) {
			keystates.remove(i);
		}
	}

	private class MasterLoopThread extends Thread {
		protected double accumulator;

		public MasterLoopThread() {
			setDaemon(false);
			setName("FZ Master Thread");
		}

		public void run() {
			long elapsedTime;
			accumulator = 0;

			while (true) {
				// common timing
				startTime = System.currentTimeMillis();
				elapsedTime = timer.timeSince(currTime);
				accumulator += elapsedTime;
				currTime += elapsedTime;
				psps = 0;

				try {
					if (inError != null) {
						renderer.renderText(0, Renderer.DISPLAY_HEIGHT - 1,
								"W", new ElementColoring("BLACK", "WHITE",
										false));
						renderer.renderText(1, Renderer.DISPLAY_HEIGHT - 1,
								" for world list ", new ElementColoring(
										"WHITE", "BLACK", false));
						renderer.renderText(17, Renderer.DISPLAY_HEIGHT - 1,
								"ESC", new ElementColoring("BLACK", "WHITE",
										false));
						renderer.renderText(20, Renderer.DISPLAY_HEIGHT - 1,
								" to reload", new ElementColoring("WHITE",
										"BLACK", false));

						if (keystates.contains(KeyEvent.VK_ESCAPE)) {
							redrawBoard = true;
							inError = null;
							new WorldLoadCallback().menuCommand(gameFile, emu
									.getWorldFileSuffix());
						}
						state = GameAppState.TITLE;// Does this effect anything?
						// checkOpenWorldSelect requires it.
						checkOpenWorldSelect();
						checkQuitSystem();
					} else if (overInput != null || overMenu != null) {
						synchronized (keytypes) {
							synchronized (keystates) {
								beginMouseInput();
								if (overInput != null)
									processMenu(overInput);
								if (overMenu != null)
									processMenu(overMenu);
								endMouseInput();
							}
						}
						if (overInput != null) {
							overInput.tick();
							if (overInput != null)
								if (!overInput.stillAlive())
									overInput = null;
						}
						if (overMenu != null) {
							overMenu.tick();
							if (overMenu != null)
								if (!overMenu.stillAlive())
									overMenu = null;
						}
					} else if (redrawBoard) {
						while (accumulator >= timer.getTimeStep()) {
							if (accumulator > 100 * timer.getTimeStep()) {
								// prevent this from being stuck?
								accumulator = timer.getTimeStep() * 10;
							}

							runSimulation(elapsedTime);
							accumulator -= timer.getTimeStep();
							// prevent floating point errors
							accumulator = TimeAndMathUtils.roundPlaces(
									accumulator, 2);
							psps++;
						}
					} else {
						synchronized (keystates) {
							checkOpenWorldSelect();
							checkQuitSystem();
						}
					}

					synchronized (keytypes) {
						keytypes.clear();
					}
				} catch (Exception e) {
					inError = e;
					redrawBoard = false;
					renderer.renderThrowable(e);
				}

				try {
					EventQueue.invokeAndWait(new Runnable() {
						public void run() {
							repaint((long) (timer.getTimeStep()));
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

				usedTime = timer.timeSince(startTime);

				if (usedTime < timer.getTimeStep()) {
					try {
						Thread.sleep((long) (timer.getTimeStep() - usedTime));
					} catch (Throwable t) {
						// continue
					}
				} else {
					try {
						Thread.sleep((long) timer.getTimeStep() / 10);
					} catch (Throwable t) {

					}
				}
			}
		}
	}

	private class ConfigurationCallback implements MenuCallback {
		@Override
		public void menuCommand(String cmd, Object rider) {
			// select stage
			if (rider.equals("input")) {
				configureInput(cmd);
				debugLine += 3;
				overInput.render(renderer, 0, false);
				overInput = new SelectInput(
						"Debug mode: \001N\002o, \001Y\002es?", Arrays
								.asList(new String[] { "N", "Y" }),
						new ConfigurationCallback(), true, true, "debug", 0,
						debugLine);
				return;
			} else if (rider.equals("debug")) {
				configureDebug(cmd);
				debugLine += 3;
				overInput.render(renderer, 0, false);
				if (worldList == null) {
					overInput = new TextInput(
							"Game directory: (\001ESC\002 for default)", "",
							new ConfigurationCallback(), true, "gamedir",
							debugLine);
				} else {
					overInput = new SelectInput(
							"Emulation mode: \001Z\002ZT, \001S\002uperZZT?",
							Arrays.asList(new String[] { "Z", "S" }),
							new ConfigurationCallback(), true, true, "emumode",
							0, debugLine);
				}
				return;
			} else if (rider.equals("gamedir")) {
				if (cmd != null && !cmd.trim().equals(""))
					worldList = new LocalWorldList(cmd);
				else
					worldList = new LocalWorldList(System.getProperty("user.dir"));

				debugLine += 3;
				overInput.render(renderer, 0, false);
				overInput = new SelectInput(
						"Emulation mode: \001Z\002ZT, \001S\002uperZZT?",
						Arrays.asList(new String[] { "Z", "S" }),
						new ConfigurationCallback(), true, true, "emumode", 0,
						debugLine);
				return;
			} else if (rider.equals("emumode")) {
				configureEmuMode(cmd);
				overInput.render(renderer, 0, false);
				overInput = null; // prevent repaint from calling render on this
				// input
				debugLine += 3;
				renderer.renderText(0, debugLine++,
						"Thank you, your game will start momentarily.",
						Renderer.SYS_COLOR);
			}

			repaint();

			String def = worldList.getDefaultWorld(emu);
			if (def == null) {
				new WorldLoadCallback(true).menuCommand(def, emu
						.getWorldFileSuffix());
			} else {
				new WorldLoadCallback().menuCommand(def, emu
						.getWorldFileSuffix());
			}

			repaint();
		}

		/**
		 * @param cmd
		 * @throws RuntimeException
		 */
		private void configureEmuMode(String cmd) throws RuntimeException {
			if (cmd != null && cmd.equalsIgnoreCase("S")) {
				emu = EmuMode.SUPERZZT;
			} else {
				emu = EmuMode.ZZT;
			}
		}

		/**
		 * @param cmd
		 */
		private void configureDebug(String cmd) {
			if (cmd != null && cmd.equalsIgnoreCase("Y"))
				debugMode = true;
			else
				debugMode = false;
		}

		/**
		 * @param cmd
		 */
		private void configureInput(String cmd) {
			if (cmd != null && cmd.equalsIgnoreCase("M")) {
				if (host.getHostComponenet() != null) {
					host.getHostComponenet().addMouseListener(mouse);
					host.getHostComponenet().addMouseMotionListener(mouse);
				} else {
					addMouseListener(mouse);
					addMouseMotionListener(mouse);
				}
			}
		}
	}

	private class MyZKeyListener implements KeyListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			synchronized (keystates) {
				keystates.add(e.getKeyCode());
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyReleased(KeyEvent e) {
			synchronized (keystates) {
				keystates.remove(e.getKeyCode());
			}
		}

		private int debugMapBase = 0;

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
		 */
		@Override
		public void keyTyped(KeyEvent e) {
			try {
				redrawBoard = true;

				synchronized (keytypes) {
					if (!keystates.contains(KeyEvent.VK_ESCAPE))
						keytypes.add(e);
				}

				synchronized (keystates) {
					boolean isGameOk = true; // is it ok to steal keypresses?
					if (game != null && game.getMenu() != null) {
						if (!(game.getMenu() instanceof Menu)) {
							isGameOk = false;
						}
					}
					if (overInput == null && isGameOk) {
						if (debugMode) {
							switch (e.getKeyChar()) {
							case '0':
							case '1':
							case '2':
							case '3':
							case '4':
							case '5':
							case '6':
							case '7':
							case '8':
							case '9':
								int nboard = debugMapBase + e.getKeyChar()
										- '0';
								if (nboard < game.getBoardList().size())
									game.setBoard(nboard);
								break;
							case '+':
								debugMapBase += 10;
								if (debugMapBase > game.getBoardList().size())
									debugMapBase = 0;
								break;
							case '-':
								debugMapBase -= 10;
								if (debugMapBase < 0)
									debugMapBase = 0;
								break;
							case '`':
							case '~':
								ZTestMain.renderTestPattern(renderer);
								redrawBoard = false;
								keystates.clear();
								break;
							case 'z':
								Cheats.zap(game);
								break;
							}
						}

						if (e.getKeyChar() == '?') {
							// cheats must be enabled for all games
							if (overInput == null) {
								overInput = new TextInput("?", "",
										new CheatCallback(), true, null);
							} else {
								String c = JOptionPane.showInputDialog(host
										.getDialogParent(), "", "?",
										JOptionPane.QUESTION_MESSAGE);
								new CheatCallback().menuCommand(c, null);
							}
							loop.accumulator = 0;
							keytypes.clear();
							keystates.clear();
							mouse.clearState();
						}
					}
				}
			} catch (Exception e3) {
				redrawBoard = false;
				inError = e3;
				renderer.renderThrowable(e3);
			}
			repaint();

		}
	}

	private class CheatCallback implements MenuCallback {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.isaacbrodsky.freeze.menus.MenuCallback#menuCommand(java.lang
		 * .String, java.lang.Object)
		 */
		@Override
		public void menuCommand(String c, Object rider) {
			if (c == null)
				return;
			String orig = c;
			c = c.toLowerCase();

			try {
				handleDebugCheats(c);
				Cheats.handleStandardCheats(c, game);
				if (debugMode) {
					overInput = Cheats.handleAdditionalCheats(c, orig, game);
					handleInternalCheats(c);
				}
			} catch (Exception exc) {
				// ignore errors found on cheats
				// eats integer/string parsing exceptions and everything else
				exc.printStackTrace();
			}

			loop.accumulator = 0;
		}

		/**
		 * Handles debug cheats.
		 * 
		 * <p>
		 * Cheats are case insensitive but must be passed in lower case to this
		 * method.
		 * 
		 * <table>
		 * <tr>
		 * <th>Code</th>
		 * <th>Effect</th>
		 * </tr>
		 * <tr>
		 * <td>debug</td>
		 * <td>Toggles debugmode flag</td>
		 * </tr>
		 * <tr>
		 * <td>version</td>
		 * <td>Displays game and Java version information</td>
		 * </tr>
		 * </table>
		 * 
		 * @param c
		 * @throws HeadlessException
		 */
		private void handleDebugCheats(String c) throws HeadlessException {
			if (c.equals("debug")) {
				debugMode = !debugMode;
			} else if (c.equals("version")) {
				JOptionPane.showMessageDialog(host.getDialogParent(), APP
						+ "\r\nJava: " + System.getProperty("java.vendor")
						+ " v. " + System.getProperty("java.version") + "\r\n"
						+ "OS: " + System.getProperty("os.name") + " "
						+ System.getProperty("os.arch") + " "
						+ System.getProperty("os.version"), APP_SHORT,
						JOptionPane.INFORMATION_MESSAGE);
			}
		}

		/**
		 * Handles additional cheats which require access to ZGame internals.
		 * 
		 * <p>
		 * Cheats are case insensitive but must be passed in lower case to this
		 * method.
		 * 
		 * <table>
		 * <tr>
		 * <th>Code</th>
		 * <th>Effect</th>
		 * </tr>
		 * <tr>
		 * <td>inspect [direction specifier], relinspect [relative x/y
		 * coordinates], absinspect [absolute x/y coordinates]</td>
		 * <td>Displays a menu detailing the element at the given location,
		 * relative directions are relative to the player.</td>
		 * </tr>
		 * <tr>
		 * <td>boardinspect, gameinspect</td>
		 * <td>As with inspect, but for the GameController and board
		 * respectively</td>
		 * </tr>
		 * <tr>
		 * <td>fztimer [optional time control]</td>
		 * <td>Resets the internal time control, or sets it to the given
		 * stepping frequency (delay between cycles in ms)</td>
		 * </tr>
		 * <tr>
		 * <td>emu [mode]</td>
		 * <td>Sets the emulation mode. Valid modes include ZZT, SUPERZZT, and
		 * IOU</td>
		 * </tr>
		 * </table>
		 * 
		 * @param c
		 * @throws NumberFormatException
		 */
		private void handleInternalCheats(String c)
				throws NumberFormatException {
			if (c.startsWith("inspect")) {
				String dir = c.substring(8); // bad solution
				int insDir = OOPHelpers.getDirFromStringArray(game, game
						.getBoard(), game.getPlayer(), new String[] { dir });
				int insX = OOPHelpers.getDirX(insDir);
				int insY = OOPHelpers.getDirY(insDir);

				overMenu = Cheats.inspectElement(game.getBoard(), game
						.getPlayer().getX()
						+ insX, game.getPlayer().getY() + insY);
			} else if (c.startsWith("relinspect") || c.startsWith("absinspect")) {
				String[] dir = c.split(" ");
				int insX = Integer.parseInt(dir[1]);
				int insY = Integer.parseInt(dir[2]);

				if (dir[0].equalsIgnoreCase("relinspect")) {
					insX += game.getPlayer().getX();
					insY += game.getPlayer().getY();
				}

				overMenu = Cheats.inspectElement(game.getBoard(), insX, insY);
			} else if (c.equals("boardinspect")) {
				overMenu = Cheats.inspectBoard(game.getBoard(), game
						.getBoardIdx());
			} else if (c.equals("gameinspect")) {
				overMenu = Cheats.inspectGame(game);
			} else if (c.startsWith("fztimer")) {
				timer = new Timer();
				if (c.split(" ").length > 1) {
					timer.setTimeStep(Double.parseDouble(c.split(" ")[1]));
				}
			} else if (c.startsWith("emu")) {
				if (c.split(" ").length > 1) {
					EmuMode old = emu;
					try {
						emu = EmuMode.valueOf(c.split(" ")[1].trim()
								.toUpperCase());
					} catch (Throwable t) {
						emu = old;
					}
				}
			}
		}
	}

	private class WorldLoadCallback implements MenuCallback {
		private boolean def;

		public WorldLoadCallback() {
			this.def = false;
		}

		public WorldLoadCallback(boolean def) {
			this.def = def;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.isaacbrodsky.freeze.menus.MenuCallback#menuCommand(java.lang
		 * .String )
		 */
		@Override
		public void menuCommand(String cmd, Object rider) {
			timer = new Timer();
			if (def) {
				loadDefault();
				return;
			}

			try {
				loop.accumulator = 0;
				state = GameAppState.TITLE;
				String suffix = rider.toString();
				if (cmd == null) {
					redrawBoard = true;
					if (game == null)
						showLoadMenu(true, suffix);
					return;
				}

				Loader loader = null;
				if (emu.equals(EmuMode.ZZT))
					loader = new ZLoader();
				else if (emu.equals(EmuMode.SUPERZZT))
					loader = new SuperZLoader();

				if (loader == null)
					throw new Exception("Illegal mode, couldn't load.");
				gameFile = cmd;
				game = loader.load(worldList, gameFile + "." + rider);
				game.setBoard(0);
				redrawBoard = true;

				if (suffix.equalsIgnoreCase("sav")) {
					// restoring a save
					state = GameAppState.PLAYING;
					game.startPlaying();
				}
			} catch (Exception e) {
				loadDefault();
				overMenu = MenuUtils.renderThrowable(e);
			}

			host.setTitle((gameFile + " [Freeze]").trim());
		}

		private void loadDefault() {
			gameFile = "DEFAULT";
			if (emu.equals(EmuMode.SUPERZZT))
				game = new DefaultSuperZWorldCreator().createDefault();
			else
				game = new DefaultZWorldCreator().createDefault();
			// game.setBoard(0);
			state = GameAppState.TITLE;
			redrawBoard = true;
		}
	}
}
