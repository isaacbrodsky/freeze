/**
 *
 */
package com.isaacbrodsky.freeze2;

import com.isaacbrodsky.freeze2.elements.CommonElements;
import com.isaacbrodsky.freeze2.filehandling.*;
import com.isaacbrodsky.freeze2.filehandling.json.JsonGame;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.game.Stat;
import com.isaacbrodsky.freeze2.game.Tile;
import com.isaacbrodsky.freeze2.game.editor.AddElementUtils;
import com.isaacbrodsky.freeze2.game.editor.EditorController;
import com.isaacbrodsky.freeze2.game.editor.EditMode;
import com.isaacbrodsky.freeze2.game.editor.ViewMode;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.NamedColor;
import com.isaacbrodsky.freeze2.graphics.Renderer;
import com.isaacbrodsky.freeze2.graphics.Sidebar;
import com.isaacbrodsky.freeze2.input.MyZKeyListener;
import com.isaacbrodsky.freeze2.input.MyZMouseListener;
import com.isaacbrodsky.freeze2.menus.Menu;
import com.isaacbrodsky.freeze2.menus.Menu.SendMode;
import com.isaacbrodsky.freeze2.menus.*;
import com.isaacbrodsky.freeze2.menus.stateditor.MatrixInput;
import com.isaacbrodsky.freeze2.menus.stateditor.MatrixInputForm;
import com.isaacbrodsky.freeze2.ui.ZHost;
import com.isaacbrodsky.freeze2.utils.TimeAndMathUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	private static final long serialVersionUID = 2L;

	/**
	 *
	 */
	private static final ElementColoring DEBUG_COLOR = ElementColoring.forNames(
			NamedColor.BLACK, NamedColor.GRAY);
	private static final ElementColoring DEBUG_ALERT_COLOR = ElementColoring.forNames(
			NamedColor.DARKRED, NamedColor.GRAY);

	public static final String APP_SHORT = "Freeze v. 2.0.0alpha";
	public static final String APP = APP_SHORT
			+ ", Copyright 2011, 2020 Isaac Brodsky";

	private static final long BLINK_TIME = 500;

	private final Object sync = new Object();

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

	private MyZKeyListener keyboard;

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

	private boolean blinking;
	private long lastBlinkingChange;

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
		emu = EmuMode.ZZT;
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
				"Freeze v. 2.0.0", Renderer.SYS_COLOR);
		renderer.renderText(0, debugLine++, "Copyright 2011, 2020 Isaac Brodsky",
				Renderer.SYS_COLOR);
		renderer.renderText(0, debugLine++, "", Renderer.SYS_COLOR);
		debugLine++;
		renderer.renderText(0, debugLine++, "Configuration: ",
				Renderer.SYS_COLOR);
		debugLine++;

		blinking = false;
		lastBlinkingChange = timer.getCurrTime();

		// start listening for keys
		keyboard = new MyZKeyListener();
		if (host.getHostComponenet() != null) {
			host.getHostComponenet().addKeyListener(keyboard);
		} else {
			addKeyListener(keyboard);
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

	public int getScaling() {
		return renderer.getScaling();
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
		synchronized (sync) {
			start = System.currentTimeMillis();

			paintGame();

			renderer.renderText(62, 1, " -- Freeze  -- ", ElementColoring.forNames(
					NamedColor.BLACK, NamedColor.GRAY));
			if (emu.equals(EmuMode.SUPERZZT)) {
				renderer.renderText(66, 1, 'S', ElementColoring.forNames(NamedColor.PURPLE,
						NamedColor.GRAY));// S is for Super!
				renderer.renderText(67, 1, 'F', ElementColoring.forNames(NamedColor.YELLOW,
						NamedColor.GRAY));
				renderer.renderText(68, 1, 'r',
						ElementColoring.forNames(NamedColor.WHITE, NamedColor.GRAY));
				renderer.renderText(69, 1, 'e',
						ElementColoring.forNames(NamedColor.GREEN, NamedColor.GRAY));
				renderer
						.renderText(70, 1, 'e', ElementColoring.forNames(NamedColor.CYAN, NamedColor.GRAY));
				renderer.renderText(71, 1, 'z', ElementColoring.forNames(NamedColor.RED,
						NamedColor.GRAY));
				renderer.renderText(72, 1, 'e', ElementColoring.forNames(NamedColor.BLACK,
						NamedColor.GRAY));
			}

			paintMenus();
			paintDebug();
			paintPaused();

			renderer.render(blinking, debugMode); // heavy
			renderer.renderOut(g); // lightweight

			end = System.currentTimeMillis() - start;
		}
	}

	/**
	 *Draws the game and sidebar, and cycles the flashing state if need be.
	 */
	private void paintGame() {
		if (game != null) {
			if (redrawBoard) {
				game.render(renderer, blinking);

//				if (!game.isPaused() && game.getMenu() == null
//						&& overMenu == null && overInput == null)
//					renderer.flashingTick();
			}
		} else if (state != GameAppState.INIT) {
			ZTestMain.renderTestPattern(renderer);
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
						ElementColoring.forNames(NamedColor.WHITE, NamedColor.DARKBLUE, true));
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

	public void tickBlinking() {
		if (timer.timeSince(lastBlinkingChange) > BLINK_TIME) {
			blinking = !blinking;
			lastBlinkingChange = timer.getCurrTime();
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

		game.runSimulation(elapsed);
	}

	/**
	 * Starting point for processing user input, this method calls out to other
	 * methods to process keys relevant to playing, the title screen, etc.
	 */
	private void processKeyCommands() {
		switch (state) {
			case TITLE:
				processTitleKeys();
				break;
			case EDITOR:
				processEditorKeys();
				break;
			case PLAYING:
				processPlayingKeys();
				break;
		}
	}

	/**
	 * Translates mouse events to <code>keystates</code>
	 */
	private void beginMouseInput() {
		if (mouse.getState(KeyEvent.VK_LEFT))
			keyboard.keysDown.add(KeyEvent.VK_LEFT);
		else if (mouse.getState(KeyEvent.VK_RIGHT))
			keyboard.keysDown.add(KeyEvent.VK_RIGHT);
		else if (mouse.getState(KeyEvent.VK_UP))
			keyboard.keysDown.add(KeyEvent.VK_UP);
		else if (mouse.getState(KeyEvent.VK_DOWN))
			keyboard.keysDown.add(KeyEvent.VK_DOWN);
		if (mouse.getState(KeyEvent.VK_SHIFT)) {
			keyboard.keysDown.addAll(Arrays.asList(new Integer[] { KeyEvent.VK_ENTER,
					KeyEvent.VK_SPACE, KeyEvent.VK_SHIFT }));
		}
	}

	/**
	 * Clears <code>keystates</code> as need be, to prevent infinite messages
	 * from the mouse.
	 */
	private void endMouseInput() {
		if (mouse.clearState())
			keyboard.keysDown.clear();
	}

	/**
	 * Processes and dispatches keys the current GameConroller, which is assumed
	 * to be an Editor.
	 */
	private void processEditorKeys() {
		int xStep = 0, yStep = 0;
		boolean shiftDown = keyboard.keysDown.contains(KeyEvent.VK_SHIFT);
		boolean controlDown = keyboard.keysDown.contains(KeyEvent.VK_CONTROL);

		if (keyboard.keysDown.contains(KeyEvent.VK_LEFT))
			xStep = -1;
		else if (keyboard.keysDown.contains(KeyEvent.VK_RIGHT))
			xStep = 1;
		// Note permits diagonal movement
		if (keyboard.keysDown.contains(KeyEvent.VK_UP))
			yStep = -1;
		else if (keyboard.keysDown.contains(KeyEvent.VK_DOWN))
			yStep = 1;

		if (controlDown) {
			xStep *= 2;
			yStep *= 2;
		}

		final EditorController editor = (EditorController) game;
		if (editor.getBoard() != null) {
			editor.getCursor().moveXY(xStep, yStep, editor.getBoard().getWidth(),
					editor.getBoard().getHeight());
		}

		if (keyboard.keysDown.remove(KeyEvent.VK_ENTER)) {
			editor.pushBuffer();
		}
		if (keyboard.keysDown.remove(KeyEvent.VK_SPACE)) {
			editor.selectPop();
		}
		if (keyboard.keysDown.remove(KeyEvent.VK_TAB)) {
			if (editor.getEditMode() == EditMode.SELECT)
				editor.setEditMode(EditMode.DRAW);
			else
				editor.setEditMode(EditMode.SELECT);
		}
		if (editor.getEditMode() == EditMode.DRAW) {
			if (keyboard.keysDown.remove(KeyEvent.VK_ESCAPE)) {
				editor.setEditMode(EditMode.SELECT);
			}
		}
		if (keyboard.keysDown.remove(KeyEvent.VK_BACK_SLASH)) {
			overInput = new SelectInput(
					"Start game? (unsaved data will be lost)", new String[]{
					"Y", "N"}, new MenuCallback() {

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
		if (keyboard.keysDown.remove(KeyEvent.VK_I)) {
			if (shiftDown)
				overMenu = Inspector.inspectElement(game, game.getBoard(), editor
						.getCursor().getX(), editor.getCursor().getY());
			else if (controlDown)
				editor.showTileInspector();
			else
				editor.showStatInspector();
		}
		if (keyboard.keysDown.remove(KeyEvent.VK_L)) {
			editor.showStatList(!shiftDown);
		}
		if (keyboard.keysDown.remove(KeyEvent.VK_B)) {
			editor.showBoardList();
		}
		if (keyboard.keysDown.remove(KeyEvent.VK_G)) {
			if (shiftDown)
				overMenu = Inspector.inspectGame(editor);
			else
				editor.showGameInspector();
		}
		if (keyboard.keysDown.remove(KeyEvent.VK_F)) {
			if (shiftDown)
				overMenu = Inspector.inspectBoard(editor.getBoard(), editor
						.getBoardIndex());
			else
				editor.showBoardInspector();
		}
		if (keyboard.keysDown.remove(KeyEvent.VK_X)) {
			editor.fill();
		}
		if (keyboard.keysDown.remove(KeyEvent.VK_P)
				|| keyboard.keysDown.remove(KeyEvent.VK_A)) {
			// A is more convenient than P
			if (controlDown)
				editor.toggleLockBuffer();
			else if (shiftDown)
				editor.decBufferIdx();
			else
				editor.incBufferIdx();
		}
		if (controlDown && keyboard.keysDown.remove(KeyEvent.VK_C)) {
			overMenu = new MatrixInputForm("Editor color:", editor.getColorIdx(), MatrixInput.makeColorList(),
					new MenuCallback() {

						@Override
						public void menuCommand(String cmd, Object rider) {
							if ("SUBMIT".equals(cmd)) {
								editor.setColorIdx(((Number) rider).intValue());
							}
						}
					});
		}
		if (keyboard.keysDown.remove(KeyEvent.VK_C)) {
			if (shiftDown)
				editor.decColorIdx();
			else
				editor.incColorIdx();
		}
		if (keyboard.keysDown.remove(KeyEvent.VK_V)) {
			if (shiftDown)
				editor.decBackIdx();
			else
				editor.incBackIdx();
		}
		for (int i = 0; i <= 9; i++) {
			if (keyboard.keysDown.remove(KeyEvent.VK_0 + i)) {
				int translatedCode = i - 1;
				if (translatedCode < 0)
					translatedCode += 10;
				// TODO: Use shiftdown to acquire to slot, controldown to place from slot
				if (shiftDown) {
				} else {
					editor.setBufferIdx(translatedCode);
				}
			}
		}
		if (keyboard.keysDown.remove(KeyEvent.VK_F1)) {
			// TODO: This is set as overInput in order to draw over the sidebar
			overInput = AddElementUtils.selectCategory(editor, new MenuCallback() {
				@Override
				public void menuCommand(String cmd, Object rider) {
					if (cmd != null) {
						// TODO: Inconsistent use of shift here to mean non-default color
						overInput = AddElementUtils.addElementByCategory(editor, cmd.charAt(0), !shiftDown);
					}
				}
			});
		}
		if (keyboard.keysDown.remove(KeyEvent.VK_F2)) {
			// TODO: This is set as overInput in order to draw over the sidebar
			overInput = AddElementUtils.addElementByCode(editor, shiftDown);
		}
		if (keyboard.keysDown.remove(KeyEvent.VK_F3)) {
			overInput = ZTestMain.generateASCIITable(shiftDown);
		}
		if (keyboard.keysDown.remove(KeyEvent.VK_F4)) {
			editor.engageTyping();
		}
		if (keyboard.keysDown.remove(KeyEvent.VK_N)) {
			overInput = new SelectInput(
					"New world? (unsaved data will be lost)", new String[]{
					"Y", "N"}, new MenuCallback() {

				@Override
				public void menuCommand(String cmd, Object rider) {
					loop.accumulator = 0;
					if (cmd != null && cmd.equalsIgnoreCase("Y")) {
						loadDefault();
						loadEditor();
					}
				}

			}, true, true, null, 1);
		}
		if (controlDown && keyboard.keysDown.remove(KeyEvent.VK_J)) {
			String saveMsg = "Exported game name:";
			String defName = game.getState().gameName + ".JSON";

			overInput = new TextInput(saveMsg, defName, new MenuCallback() {
				@Override
				public void menuCommand(String name, Object rider) {
					loop.accumulator = 0;
					if (name == null)
						return;
					try {
						String outFile = name;

						GameController savGame = game;
						if (game instanceof EditorController)
							savGame = ((EditorController) game).toGame();

						JsonGame.save(savGame, worldList, outFile);
					} catch (Exception e) {
						inError = e;
						e.printStackTrace();
					}
				}

			}, true, null);
			keyboard.clearState();
			mouse.clearState();
		}
		if (keyboard.keysDown.remove(KeyEvent.VK_BACK_QUOTE)) { // tilde key
			overInput = new CharInput("View Mode\n"
					+ ViewMode.makeViewModeList()
					+ "Strike selection", (cmd, rider) -> {
				if (cmd != null) {
					ViewMode nextViewMode = ViewMode.forCode(cmd);
					if (nextViewMode != null) {
						editor.setViewMode(nextViewMode);
					}
				}
			}, true, null, 0);
		}
		if (keyboard.keysDown.remove(KeyEvent.VK_H)) {
			// TODO: Help
		}

		checkSaveGame();
		checkQuitWorld();
	}

	/**
	 * Processes and dispatches keys to the game, which is assumed to be
	 * playing, even if in a paused state.
	 */
	private void processPlayingKeys() {
		// TODO

		checkSaveGame();
		checkQuitWorld();
	}

	/**
	 * Process keys relevant to the title screen.
	 */
	private void processTitleKeys() {
		if (keyboard.keysDown.remove(KeyEvent.VK_P)) {
			state = GameAppState.PLAYING;
			game.startPlaying();
		}
		if (keyboard.keysDown.contains(KeyEvent.VK_CONTROL) && keyboard.keysDown.remove(KeyEvent.VK_J)) {
			String saveMsg = "Load JSON from file:";
			String defName = game.getState().gameName + ".JSON";

			overInput = new TextInput(saveMsg, defName, new MenuCallback() {
				@Override
				public void menuCommand(String name, Object rider) {
					loop.accumulator = 0;
					if (name == null)
						return;
					try {
						game = JsonGame.load(worldList, name);
						loadEditor();
					} catch (Exception e) {
						overMenu = MenuUtils.renderThrowable(e);
						e.printStackTrace();
					}
				}

			}, true, null);
			keyboard.clearState();
			mouse.clearState();
		}
		if (keyboard.keysDown.remove(KeyEvent.VK_E)) {
			try {
				Loader loader = null;
				if (emu == EmuMode.ZZT)
					loader = new ZLoader();
				else if (emu == EmuMode.SUPERZZT)
					loader = new SuperZLoader();
				game = loader.load(worldList, gameFile + "."
						+ emu.getWorldFileSuffix());
				loadEditor();
			} catch (Exception e) {
				loadDefault();
				EditorController editor = loadEditor();
				editor.reportError("Could not load world.\r\n" + e.toString()
						+ "\r\n");
				e.printStackTrace();
			}
		}
		checkQuitSystem();
	}

	private void checkCheatInput() {
		if (overInput == null && overMenu == null && (game == null || game.getMenu() == null)) {
			if (keyboard.keysTyped.stream().anyMatch(e -> e.getKeyChar() == '?')) {
				overInput = new TextInput("?", "",
						new CheatCallback(), true, null);
				keyboard.clearState();
			}
		}
	}

	/**
	 *Checks for, and handles if need be, input indicating the player wishes to
	 * save.
	 */
	private void checkSaveGame() {
		if (keyboard.keysDown.remove(KeyEvent.VK_S)) {
			String saveMsg = "Save game name:";
			String defName = "FZSAVED.SAV";

			final boolean isEditor = (state == GameAppState.EDITOR);

			if (isEditor) {
				saveMsg = "Save world name (doesn't set game name):";
				defName = gameFile + "." + emu.getWorldFileSuffix();
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
//						if (isEditor)
//						 	game.getState().gameName = name.split("\\.")[0];

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
						inError = e;
						e.printStackTrace();
					}
				}

			}, true, null);
			keyboard.clearState();
			mouse.clearState();
		}
	}

	/**
	 * Checks for, and handles if need be, input indicating the player wishes to
	 * exit the current GameController, whatever type or state it may be.
	 */
	private void checkQuitWorld() {
		if (keyboard.keysDown.remove(KeyEvent.VK_Q)
				|| keyboard.keysDown.remove(KeyEvent.VK_ESCAPE)) {
			String quitMsg = "Quit this game?";
			if (state == GameAppState.EDITOR)
				quitMsg = "Quit editor? (unsaved work will be lost)";
			overInput = new SelectInput(quitMsg, new String[]{"Y", "N"},
					new MenuCallback() {

						@Override
						public void menuCommand(String cmd, Object rider) {
							loop.accumulator = 0;
							if (cmd != null && cmd.equalsIgnoreCase("Y")) {
								// quit; ie reload the world
								// (not how ZZT does it, all the time
								// anyways)
								new WorldLoadCallback().menuCommand(gameFile, emu
										.getWorldFileSuffix());
							}
						}

					}, true, true, null, 1);
		}
	}

	/**
	 * Checks for, and handles if need be, input indicating the player wishes to
	 * exit the entire game. (exiting being calling <code>host.quit()</code>.)
	 */
	private void checkQuitSystem() {
		if (state == GameAppState.TITLE && (keyboard.keysDown.remove(KeyEvent.VK_Q)
				|| keyboard.keysDown.remove(KeyEvent.VK_ESCAPE))) {
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
		if (state == GameAppState.TITLE && overMenu == null) {
			boolean wDown = keyboard.keysDown.remove(KeyEvent.VK_W);
			boolean rDown = keyboard.keysDown.remove(KeyEvent.VK_R);
			if (wDown || rDown) {
				String suffix = wDown ? emu.getWorldFileSuffix() : "sav";
				showLoadMenu(true, suffix);
			}
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

		if (MenuUtils.getTypingTarget(m) instanceof TypingInteraction) {
			text = true;
			TypingInteraction t = (TypingInteraction) MenuUtils
					.getTypingTarget(m);
			for (KeyEvent k : keyboard.keysTyped) {
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
		for (int i : keyboard.keysDown) {
			boolean consume = m.keyPress(i);
			if (!consume && multi && !text)
				consume = ((MultiInput) m).bypassKeyPress(i);
			if (consume || text)
				consumes.add(i);
		}
		for (int i : consumes) {
			keyboard.keysDown.remove(i);
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
				startTime = timer.getCurrTime();
				elapsedTime = timer.timeSince(currTime);
				// TODO: Unclear if accumulator, psps, etc serve any useful purpose.
				accumulator += elapsedTime;
				currTime += elapsedTime;
				psps = 0;

				try {
					synchronized (keyboard) {
						synchronized (sync) {
							keyboard.beginFrame();

							checkCheatInput();
							tickBlinking();

							if (inError != null) {
								renderer.renderText(0, Renderer.DISPLAY_HEIGHT - 1,
										"W", ElementColoring.forNames(NamedColor.BLACK, NamedColor.WHITE,
												false));
								renderer.renderText(1, Renderer.DISPLAY_HEIGHT - 1,
										" for world list ", ElementColoring.forNames(
												NamedColor.WHITE, NamedColor.BLACK));
								renderer.renderText(17, Renderer.DISPLAY_HEIGHT - 1,
										"ESC", ElementColoring.forNames(NamedColor.BLACK, NamedColor.WHITE));
								renderer.renderText(20, Renderer.DISPLAY_HEIGHT - 1,
										" to reload", ElementColoring.forNames(NamedColor.WHITE,
												NamedColor.BLACK));

								if (keyboard.keysDown.remove(KeyEvent.VK_ESCAPE)) {
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
								beginMouseInput();
								if (overInput != null)
									processMenu(overInput);
								if (overMenu != null)
									processMenu(overMenu);
								endMouseInput();

								if (overInput != null) {
									overInput.tick();
									if (overInput != null && !overInput.stillAlive())
										overInput = null;
								}
								if (overMenu != null) {
									overMenu.tick();
									if (overMenu != null && !overMenu.stillAlive())
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
								checkOpenWorldSelect();
								checkQuitSystem();
							} else {
								checkOpenWorldSelect();
								checkQuitSystem();
							}

							keyboard.clearState();
						}
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
					} catch (Exception e) {
						// continue
					}
				} else {
					try {
						Thread.sleep((long) timer.getTimeStep() / 10);
					} catch (Exception e) {

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

	private class CheatCallback implements MenuCallback {
		@Override
		public void menuCommand(String c, Object rider) {
			if (c == null)
				return;
			String orig = c;
			c = c.toLowerCase();

			if (c.startsWith("fztimer")) {
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
					} catch (Exception e) {
						emu = old;
					}
				}
			} else if (c.equals("debug")) {
				debugMode = !debugMode;
			} else if (c.equals("version")) {
				JOptionPane.showMessageDialog(host.getDialogParent(), APP
								+ "\r\nJava: " + System.getProperty("java.vendor")
								+ " v. " + System.getProperty("java.version") + "\r\n"
								+ "OS: " + System.getProperty("os.name") + " "
								+ System.getProperty("os.arch") + " "
								+ System.getProperty("os.version"), APP_SHORT,
						JOptionPane.INFORMATION_MESSAGE);
			} else if (c.equals("eraseplayer")) {
				if (game != null) {
					Stat player = game.getBoard().getPlayer();
					if (player != null) {
						game.getBoard().putTileAndStats(player.x, player.y, new Tile(CommonElements.EMPTY, 0));
					}
				}
			}

			loop.accumulator = 0;
		}
	}

	private class WorldLoadCallback implements MenuCallback {
		private final boolean def;

		public WorldLoadCallback() {
			this.def = false;
		}

		public WorldLoadCallback(boolean def) {
			this.def = def;
		}

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

//				if (suffix.equalsIgnoreCase("sav")) {
//					// restoring a save
//					state = GameAppState.PLAYING;
//					game.startPlaying();
//				}
			} catch (Exception e) {
				loadDefault();
				overMenu = MenuUtils.renderThrowable(e);
			}

			host.setTitle((gameFile + " [Freeze]").trim());
		}
	}

	private void loadDefault() {
		gameFile = "DEFAULT";
		if (emu.equals(EmuMode.SUPERZZT))
			game = new DefaultSuperZWorldCreator().createDefault();
		else
			game = new DefaultZWorldCreator().createDefault();
		state = GameAppState.TITLE;
		redrawBoard = true;
	}

	private EditorController loadEditor() {
		EditorController editor = new EditorController(game);
		game = editor;
		game.startPlaying();
		state = GameAppState.EDITOR;
		return editor;
	}
}
