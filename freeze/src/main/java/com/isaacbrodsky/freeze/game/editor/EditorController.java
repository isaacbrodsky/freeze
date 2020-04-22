/**
 *
 */
package com.isaacbrodsky.freeze.game.editor;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.isaacbrodsky.freeze.EmuMode;
import com.isaacbrodsky.freeze.ZGame;
import com.isaacbrodsky.freeze.elements.Breakable;
import com.isaacbrodsky.freeze.elements.Element;
import com.isaacbrodsky.freeze.elements.ElementDefaults;
import com.isaacbrodsky.freeze.elements.ElementEditorDefaults;
import com.isaacbrodsky.freeze.elements.ElementResolver;
import com.isaacbrodsky.freeze.elements.Empty;
import com.isaacbrodsky.freeze.elements.Invisible;
import com.isaacbrodsky.freeze.elements.Line;
import com.isaacbrodsky.freeze.elements.Normal;
import com.isaacbrodsky.freeze.elements.Player;
import com.isaacbrodsky.freeze.elements.Solid;
import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.elements.data.Stats;
import com.isaacbrodsky.freeze.elements.oop.OOPHelpers;
import com.isaacbrodsky.freeze.filehandling.DefaultZWorldCreator;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.BoardState;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.game.GameState;
import com.isaacbrodsky.freeze.graphics.ElementColoring;
import com.isaacbrodsky.freeze.graphics.GraphicsBlock;
import com.isaacbrodsky.freeze.graphics.Renderer;
import com.isaacbrodsky.freeze.menus.BoardInput;
import com.isaacbrodsky.freeze.menus.CharInput;
import com.isaacbrodsky.freeze.menus.Menu;
import com.isaacbrodsky.freeze.menus.MenuCallback;
import com.isaacbrodsky.freeze.menus.MenuUtils;
import com.isaacbrodsky.freeze.menus.MultiInput;
import com.isaacbrodsky.freeze.menus.SelectInput;
import com.isaacbrodsky.freeze.menus.TextInput;
import com.isaacbrodsky.freeze.menus.UIInteraction;
import com.isaacbrodsky.freeze.menus.Menu.SendMode;
import com.isaacbrodsky.freeze.utils.TimeAndMathUtils;

/**
 * @author isaac
 * 
 */
public class EditorController implements GameController {

	private GameController game;
	private UIInteraction currMenu;
	private TypingInput typingInput;

	private final Cursor cur;

	private int bufferIdx;
	private ArrayList<Element> buffer;

	private EditorMode mode;

	private int curColor;

	/**
	 * @param game
	 */
	public EditorController(GameController game) {
		this.game = game;

		cur = new Cursor();

		curColor = 0x0F;

		buffer = new ArrayList<Element>();
		resetBuffer();
		Element e = new Empty();
		e.createInstance(new SaveData(0x00, 0x07));
		buffer.add(e);
		buffer.add(e);
		buffer.add(e);
		bufferIdx = 0;

		mode = EditorMode.SELECT;
		typingInput = null;
		currMenu = null;
	}

	public EmuMode getEmuMode() {
		return EmuMode.fromGame(game);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.game.ZGameController#render(com.isaacbrodsky
	 * .freeze.graphics.Renderer, boolean)
	 */
	@Override
	public void render(Renderer renderer, boolean blinking) {
		Board currBoard = getBoard();
		if (currBoard == null) {
			renderer.renderText(0, 0, "null board.", Renderer.SYS_COLOR);
			return;
		}

		Player player = getPlayer();
		// scrolling support
		int px = getXViewOffset();
		int py = getYViewOffset();

		// render the board
		for (int x = px; x < px + Renderer.DISPLAY_WIDTH; x++) {
			for (int y = py; y < py + Renderer.DISPLAY_HEIGHT; y++) {
				renderBlock(renderer, blinking, player, x, y, x - px, y - py);
			}
		}

		String msg = currBoard.getMessage();
		if (msg != null) {
			if (msg.split("\\r?\\n").length > 1) {
				renderer.renderText(MenuUtils.centerText(
						msg.split("\\r?\\n")[0], Renderer.DISPLAY_WIDTH) + 1,
						Renderer.DISPLAY_HEIGHT - 2, " "
								+ msg.split("\\r?\\n")[0] + " ", renderer
								.getFlashingColor());
				renderer.renderText(MenuUtils.centerText(
						msg.split("\\r?\\n")[1], Renderer.DISPLAY_WIDTH) + 1,
						Renderer.DISPLAY_HEIGHT - 1, " "
								+ msg.split("\\r?\\n")[1] + " ", renderer
								.getFlashingColor());
			} else {
				renderer.renderText(MenuUtils.centerText(msg,
						Renderer.DISPLAY_WIDTH) + 1,
						Renderer.DISPLAY_HEIGHT - 1, " " + msg + " ", renderer
								.getFlashingColor());
			}
		}

		if (typingInput != null && blinking)
			typingInput.render(renderer, px, py);
		else if (currMenu != null)
			currMenu.render(renderer, 0, true);
		else
			cur.render(renderer, blinking, px, py);
	}

	/**
	 * @param renderer
	 * @param blinking
	 * @param player
	 * @param x
	 * @param y
	 */
	private void renderBlock(Renderer renderer, boolean blinking,
			Player player, int x, int y, int rx, int ry) {
		Board currBoard = getBoard();
		if (currBoard.getElements()[x][y][0] == null) {
			if (currBoard.getElements()[x][y][1] != null)
				currBoard.getElements()[x][y][0] = currBoard.getElements()[x][y][1];
			else {
				renderer.set(rx, ry, new GraphicsBlock(new ElementColoring(0),
						0x20));
				return;
			}
		}

		ElementColoring col = currBoard.getElements()[x][y][0].getColoring();
		if (currBoard.getElements()[x][y][1] != null) {
			if (col.getMode() == ElementColoring.ColorMode.RECESSIVE) {
				col = new ElementColoring(col.getFore(), currBoard
						.getElements()[x][y][1].getColoring().getBack(), null);
			}
		}

		int ch = currBoard.getElements()[x][y][0].getDisplayCharacter();
		if (currBoard.getElements()[x][y][0] instanceof Invisible)
			ch = 176;

		GraphicsBlock gb = new GraphicsBlock(col, ch);
		renderer.set(rx, ry, gb);
	}

	public int getXViewOffset() {
		int px = getCursor().getX() - 30;
		px = Math.max(px, 0);
		if (px + Renderer.DISPLAY_WIDTH > getBoard().getWidth())
			px = getBoard().getWidth() - Renderer.DISPLAY_WIDTH;
		return px;
	}

	public int getYViewOffset() {
		int py = getCursor().getY() - 12;
		py = Math.max(py, 0);
		if (py + Renderer.DISPLAY_HEIGHT > getBoard().getHeight())
			py = getBoard().getHeight() - Renderer.DISPLAY_HEIGHT;
		return py;
	}

	public ArrayList<Element> getBuffer() {
		return buffer;
	}

	public int getBufferIdx() {
		return bufferIdx;
	}

	public void incBufferIdx() {
		bufferIdx++;
		if (bufferIdx >= buffer.size())
			bufferIdx = 0;
	}

	public void decBufferIdx() {
		bufferIdx--;
		if (bufferIdx < 0)
			bufferIdx = buffer.size() - 1;
	}

	public void incColorIdx() {
		if ((curColor & 0x0F) == 0x0F) {
			curColor &= 0xF0;
		} else {
			curColor += 1;
		}

		resetBuffer();
	}

	public void incBackIdx() {
		if ((curColor & 0xF0) == 0xF0) {
			curColor &= 0x0F;
		} else {
			curColor += 1 << 4;
		}

		resetBuffer();
	}

	public void decColorIdx() {
		if ((curColor & 0x0F) == 0x00) {
			curColor |= 0x0F;
		} else {
			curColor -= 1;
		}

		resetBuffer();
	}

	public void decBackIdx() {
		if ((curColor & 0xF0) == 0x00) {
			curColor |= 0xF0;
		} else {
			curColor -= 1 << 4;
		}

		resetBuffer();
	}

	public int getColorIdx() {
		return curColor;
	}

	/**
	 * @param elapsed
	 */
	public void runSimulation(long elapsed) {
		typingInput = (TypingInput) checkMenu(typingInput);
		currMenu = checkMenu(currMenu);

		if (typingInput == null && mode == EditorMode.TYPE)
			mode = EditorMode.SELECT;

		if (mode == EditorMode.DRAW)
			popBuffer();
	}

	/**
	 * 
	 */
	private static UIInteraction checkMenu(UIInteraction menu) {
		if (menu != null) {
			int menuCode = menu.hashCode();
			menu.tick();
			boolean keepMenu = menu.stillAlive();
			if (!keepMenu && menuCode == menu.hashCode()) {
				return null;
			}
		}
		return menu;
	}

	public Cursor getCursor() {
		return cur;
	}

	public Element getRealElement(int x, int y) {
		Element e = getBoard().elementAt(x, y);
		if (e == null)
			e = getBoard().floorAt(x, y);
		return e;
	}

	/**
	 *
	 */
	public void selectPop() {
		Element e = getRealElement(cur.getX(), cur.getY());
		if (e == null || e instanceof Empty) {
			popBuffer();
		} else {
			Board board = getBoard();
			board.removeAt(cur.getX(), cur.getY());
			board.removeAt(cur.getX(), cur.getY());
			OOPHelpers.putEmpty(board, cur.getX(), cur.getY());
			recalcLineWalls(board);
		}
	}

	/**
	 *
	 */
	public void pushBuffer() {
		Element e = getRealElement(cur.getX(), cur.getY());

		resetBuffer();
		int idx = getBufferIdx();
		if (idx < 5)
			idx = 5;
		buffer.remove(idx);
		buffer.add(idx, e);
	}

	/**
	 *
	 */
	private void resetBuffer() {
		ArrayList<Element> s = new ArrayList<Element>();
		for (int i = 5; i < buffer.size(); i++)
			s.add(buffer.get(i));

		buffer.clear();
		Element e;
		e = new Solid();
		e.createInstance(new SaveData(0x15, curColor));
		buffer.add(0, e);
		e = new Normal();
		e.createInstance(new SaveData(0x16, curColor));
		buffer.add(1, e);
		e = new Breakable();
		e.createInstance(new SaveData(0x17, curColor));
		buffer.add(2, e);
		e = new Empty();
		e.createInstance(new SaveData(0x00, curColor));
		buffer.add(3, e);
		e = new Line();
		e.createInstance(new SaveData(0x1F, curColor));
		((Line) e).recalculateLineWalls(null);
		buffer.add(4, e);

		for (int i = 0; i < s.size(); i++)
			buffer.add(s.get(i));
	}

	/**
	 *
	 */
	public void popBuffer() {
		Element e = buffer.get(bufferIdx);

		dropElement(e);
	}

	private void dropElement(Element e) {
		Board board = getBoard();

		Element e2 = getElementResolver().resolve(e.getSaveData(), cur.getX(),
				cur.getY());
		if (e.getStats() != null)
			e2.setStats(new Stats(e.getStats()));

		board.removeAt(cur.getX(), cur.getY());
		board.removeAt(cur.getX(), cur.getY());
		e2.setXY(cur.getX(), cur.getY());
		board.putElement(cur.getX(), cur.getY(), e2);

		recalcLineWalls(board);
	}

	/**
	 * @param board
	 */
	private void recalcLineWalls(Board board) {
		for (Element iterE : board.getElementList()) {
			if (iterE instanceof Line)
				((Line) iterE).recalculateLineWalls(board);
		}
	}

	/**
	 * @return
	 */
	public EditorMode getMode() {
		return mode;
	}

	/**
	 * @param n
	 */
	public void setMode(EditorMode n) {
		mode = n;
	}

	/**
	 * @return
	 */
	public GameController toGame() {
		return game;
	}

	@Override
	public UIInteraction getMenu() {
		if (typingInput != null)
			return typingInput;
		return currMenu;
	}

	/**
	 *
	 */
	public void showBoardList() {
		StringBuilder boardList = new StringBuilder();

		for (int i = 0; i < getBoardList().size(); i++) {
			boardList.append((i != 0) ? "\r\n" : "").append(
					getBoardList().get(i).getState().boardName);
		}
		boardList.append("\r\nADD NEW BOARD...");

		currMenu = new Menu("Board Select", boardList.toString(),
				new MenuCallback() {
					@Override
					public void menuCommand(String cmd, Object rider) {
						if (cmd != null) {
							int b = Integer.parseInt(cmd);
							if (b < getBoardList().size())
								setBoard(b);
							else if (b == getBoardList().size()) {
								ArrayList<Board> bl = getBoardList();
								bl.add(new DefaultZWorldCreator()
										.createDefaultBoard());
								setBoardList(bl);
								setBoard(b);
							}
						}
					}
				}, SendMode.LASTNO, true);
		for (int i = 0; i < getBoardIdx(); i++) {
			currMenu.keyPress(KeyEvent.VK_DOWN);
		}
	}

	public void engageTyping() {
		int off = (curColor & 0x0F) - 9;
		if (off < 0) {
			return;
		}
		int type = getElementResolver().getTextBase() + off;
		typingInput = new TypingInput(getBoard(), type, cur);
		mode = EditorMode.TYPE;
	}

	/**
	 *
	 */
	public void addElement(final boolean useCodes) {
		MenuCallback callback = new MenuCallback() {

			@Override
			public void menuCommand(String cmd, Object rider) {
				if (cmd == null)
					return;

				try {
					int id;
					if (useCodes)
						id = getElementResolver().resolveEditorCode(cmd);
					else
						id = TimeAndMathUtils.parseInt(cmd);

					int col = (Integer) rider;
					Element e = getElementResolver().resolve(id, col,
							cur.getX(), cur.getY());
					if (e == null)
						return;
					e.setStats(getElementDefaults().getDefaultStats(id));
					dropElement(e);
				} catch (Exception e) {
					e.printStackTrace();
					reportError("Error adding element:\n" + e.toString());
				}
			}

		};
		if (useCodes)
			currMenu = new CharInput(getEmuMode() + " Element Reference\n"
					+ getElementResolver().makeElementList(useCodes)
					+ "Strike selection", callback, true, Integer
					.valueOf(curColor), 0);
		else
			currMenu = new TextInput(getEmuMode() + " Element Reference\n"
					+ getElementResolver().makeElementList(useCodes)
					+ "Element ID?", "", callback, true, Integer
					.valueOf(curColor), 0);
	}

	public void fill() {
		ArrayList<Element> oldlist = new ArrayList<Element>();
		oldlist.add(getRealElement(cur.getX(), cur.getY()));
		fillBuildReplaceList(oldlist, cur.getX(), cur.getY());

		Element e = buffer.get(bufferIdx);
		Board board = getBoard();
		for (Element old : oldlist) {
			int x = old.getX();
			int y = old.getY();

			Element e2 = getElementResolver().resolve(e.getSaveData(), x, y);
			if (e.getStats() != null)
				e2.setStats(new Stats(e.getStats()));

			board.removeAt(x, y);
			board.removeAt(x, y);
			e2.setXY(x, y);
			board.putElement(x, y, e2);
		}
		recalcLineWalls(board);
	}

	private void fillBuildReplaceList(ArrayList<Element> in, int x, int y) {
		Element comp = in.get(0);
		for (int i = 0; i < 4; i++) {
			int offx = OOPHelpers.getDirX(i);
			int offy = OOPHelpers.getDirY(i);

			if (getBoard().boundsCheck(x + offx, y + offy) == -1) {
				Element etest = getRealElement(x + offx, y + offy);

				if (etest != null) {
					if (!in.contains(etest)) {
						if (etest.getSaveData().getType() == comp.getSaveData()
								.getType()) {
							in.add(etest);
							fillBuildReplaceList(in, x + offx, y + offy);
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.game.GameController#reportError(java.lang.String
	 * )
	 */
	@Override
	public void reportError(String msg) {
		currMenu = new Menu(ZGame.APP_SHORT, "$Engine Error\n\n" + msg, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.game.Editor#showGameInspector()
	 */
	public void showSuperGameInspector() {
		ArrayList<UIInteraction> ui = new ArrayList<UIInteraction>();

		final GameState state = getState();

		ui.add(new TextInput("Game name:", state.gameName, false));
		ui.add(new SelectInput("Save: \001N\002o, \001Y\002es", new String[] {
				"N", "Y" }, true, (state.isSave != 0) ? 1 : 0, false));
		ui.add(new TextInput("Ammo:", Integer.toString(state.ammo), false));
		ui.add(new TextInput("Health:", Integer.toString(state.health), false));
		ui.add(new TextInput("Stones:", Integer.toString(state.stones), false));
		ui.add(new TextInput("Gems:", Integer.toString(state.gems), false));
		ui.add(new TextInput("Score:", Integer.toString(state.score), false));
		ui
				.add(new TextInput("ECycles:", Integer.toString(state.ecycles),
						false));
		ui.add(new TextInput("Time passed:",
				Integer.toString(state.timePassed), false));

		int code = 9;
		for (int i = 0; i < state.keys.length; i++) {
			ui.add(new SelectInput(ElementColoring.nameFromCode(code)
					+ " key: \001N\002o, \001Y\002es",
					new String[] { "N", "Y" }, true, (state.keys[i] != 0) ? 1
							: 0, false));
			code++;
		}

		for (int i = 0; i < state.flags.length; i++) {
			String s = state.flags[i];
			if (s == null)
				s = "";
			ui.add(new TextInput("Flag " + (i + 1) + ":", s, false));
		}

		currMenu = new MultiInput("Game Inspector\n"
				+ "(\001ENTER\002 to save, \001ESC\002 to cancel,"
				+ " \001UP\002 and \001DOWN\002 to navigate)", ui,
				new MenuCallback() {

					@Override
					public void menuCommand(String cmd, Object rider) {
						if (cmd != null && cmd.equals("SUBMIT")) {
							try {
								ArrayList<String> results = (ArrayList<String>) rider;

								int ammo = TimeAndMathUtils.parseInt(results
										.get(2));
								int health = TimeAndMathUtils.parseInt(results
										.get(3));
								int stones = TimeAndMathUtils.parseInt(results
										.get(4));
								int gems = TimeAndMathUtils.parseInt(results
										.get(5));
								int score = TimeAndMathUtils.parseInt(results
										.get(6));
								int ecycles = TimeAndMathUtils.parseInt(results
										.get(7));
								int timePassed = TimeAndMathUtils
										.parseInt(results.get(8));

								state.gameName = results.get(0);
								state.isSave = results.get(1).startsWith("Y") ? 1
										: 0;
								state.ammo = ammo;
								state.health = health;
								state.stones = stones;
								state.gems = gems;
								state.score = score;
								state.ecycles = ecycles;
								state.timePassed = timePassed;

								for (int i = 9; i < 9 + state.keys.length; i++) {
									state.keys[i - 9] = results.get(i)
											.startsWith("Y") ? 1 : 0;
								}

								for (int i = 9 + state.keys.length; i < 9
										+ state.keys.length
										+ state.flags.length; i++) {
									String s = results.get(i);
									if (s.equals(""))
										s = null;
									state.flags[i - 9 - state.keys.length] = s;
								}
							} catch (Exception e) {
								e.printStackTrace();
								reportError("Problem setting board info\r\n"
										+ e.toString());
							}
						}
					}

				});
	}

	public void showBoardInspector() {
		ArrayList<UIInteraction> ui = new ArrayList<UIInteraction>();

		final BoardState state = getBoard().getState();

		ui.add(new TextInput("Board name:", state.boardName, false));
		ui.add(new TextInput("Shots:", Integer.toString(state.shots), false));
		if (getEmuMode() == EmuMode.ZZT)
			ui.add(new SelectInput("Dark: \001N\002o, \001Y\002es",
					new String[] { "N", "Y" }, true, (state.dark != 0) ? 1 : 0,
					false));
		ui.add(new SelectInput("Restart on hit: \001N\002o, \001Y\002es",
				new String[] { "N", "Y" }, true, (state.restart != 0) ? 1 : 0,
				false));
		ui.add(new TextInput("Message:", state.message != null ? state.message
				: "", false));
		ui.add(new TextInput("Time limit:", Integer.toString(state.timeLimit),
				false));

		ui.add(new BoardInput("Board north:", state.boardNorth, this));
		ui.add(new BoardInput("Board south:", state.boardSouth, this));
		ui.add(new BoardInput("Board west:", state.boardWest, this));
		ui.add(new BoardInput("Board east:", state.boardEast, this));

		currMenu = new MultiInput("Board Inspector\n"
				+ "(\001ENTER\002 to save, \001ESC\002 to cancel,"
				+ " \001UP\002 and \001DOWN\002 to navigate)", ui,
				new MenuCallback() {

					@Override
					public void menuCommand(String cmd, Object rider) {
						if (cmd != null && cmd.equals("SUBMIT")) {
							try {
								ArrayList<String> results = (ArrayList<String>) rider;

								int offset = 0;
								if (getEmuMode() != EmuMode.ZZT)
									offset = -1;

								int shots = TimeAndMathUtils.parseInt(results
										.get(1));
								int timeLimit = TimeAndMathUtils
										.parseInt(results.get(5 + offset));

								state.boardName = results.get(0);
								state.shots = shots;
								if (getEmuMode() == EmuMode.ZZT)
									state.dark = results.get(2).startsWith("Y") ? 1
											: 0;
								state.restart = results.get(3 + offset)
										.startsWith("Y") ? 1 : 0;
								if (results.get(4 + offset).equals(""))
									state.message = null;
								else
									getBoard().setMessage(
											results.get(4 + offset));
								state.timeLimit = timeLimit;

								state.boardNorth = TimeAndMathUtils
										.parseInt(results.get(6 + offset));
								state.boardSouth = TimeAndMathUtils
										.parseInt(results.get(7 + offset));
								state.boardWest = TimeAndMathUtils
										.parseInt(results.get(8 + offset));
								state.boardEast = TimeAndMathUtils
										.parseInt(results.get(9 + offset));
							} catch (Exception e) {
								e.printStackTrace();
								reportError("Problem setting board info\r\n"
										+ e.toString());
							}
						}
					}

				});
	}

	public void showGameInspector() {
		if (getEmuMode() == EmuMode.SUPERZZT) {
			showSuperGameInspector();
			return;
		}

		ArrayList<UIInteraction> ui = new ArrayList<UIInteraction>();

		final GameState state = getState();

		ui.add(new TextInput("Game name:", state.gameName, false));
		ui.add(new SelectInput("Save: \001N\002o, \001Y\002es", new String[] {
				"N", "Y" }, true, (state.isSave != 0) ? 1 : 0, false));
		ui.add(new TextInput("Ammo:", Integer.toString(state.ammo), false));
		ui.add(new TextInput("Health:", Integer.toString(state.health), false));
		ui
				.add(new TextInput("Torches:", Integer.toString(state.torches),
						false));
		ui.add(new TextInput("Gems:", Integer.toString(state.gems), false));
		ui.add(new TextInput("Score:", Integer.toString(state.score), false));
		ui
				.add(new TextInput("TCycles:", Integer.toString(state.tcycles),
						false));
		ui
				.add(new TextInput("ECycles:", Integer.toString(state.ecycles),
						false));
		ui.add(new TextInput("Time passed:",
				Integer.toString(state.timePassed), false));

		int code = 9;
		for (int i = 0; i < state.keys.length; i++) {
			ui.add(new SelectInput(ElementColoring.nameFromCode(code)
					+ " key: \001N\002o, \001Y\002es",
					new String[] { "N", "Y" }, true, (state.keys[i] != 0) ? 1
							: 0, false));
			code++;
		}

		for (int i = 0; i < state.flags.length; i++) {
			String s = state.flags[i];
			if (s == null)
				s = "";
			ui.add(new TextInput("Flag " + (i + 1) + ":", s, false));
		}

		currMenu = new MultiInput("Game Inspector\n"
				+ "(\001ENTER\002 to save, \001ESC\002 to cancel,"
				+ " \001UP\002 and \001DOWN\002 to navigate)", ui,
				new MenuCallback() {

					@Override
					public void menuCommand(String cmd, Object rider) {
						if (cmd != null && cmd.equals("SUBMIT")) {
							try {
								ArrayList<String> results = (ArrayList<String>) rider;

								int ammo = TimeAndMathUtils.parseInt(results
										.get(2));
								int health = TimeAndMathUtils.parseInt(results
										.get(3));
								int torches = TimeAndMathUtils.parseInt(results
										.get(4));
								int gems = TimeAndMathUtils.parseInt(results
										.get(5));
								int score = TimeAndMathUtils.parseInt(results
										.get(6));
								int tcycles = TimeAndMathUtils.parseInt(results
										.get(7));
								int ecycles = TimeAndMathUtils.parseInt(results
										.get(8));
								int timePassed = TimeAndMathUtils
										.parseInt(results.get(9));

								state.gameName = results.get(0);
								state.isSave = results.get(1).startsWith("Y") ? 1
										: 0;
								state.ammo = ammo;
								state.health = health;
								state.torches = torches;
								state.gems = gems;
								state.score = score;
								state.tcycles = tcycles;
								state.ecycles = ecycles;
								state.timePassed = timePassed;

								for (int i = 10; i < 10 + state.keys.length; i++) {
									state.keys[i - 10] = results.get(i)
											.startsWith("Y") ? 1 : 0;
								}

								for (int i = 10 + state.keys.length; i < 10
										+ state.keys.length
										+ state.flags.length; i++) {
									String s = results.get(i);
									if (s.equals(""))
										s = null;
									state.flags[i - 10 - state.keys.length] = s;
								}
							} catch (Exception e) {
								e.printStackTrace();
								reportError("Problem setting board info\r\n"
										+ e.toString());
							}
						}
					}

				});
	}

	public void showStatInspector() {
		final Element e = getRealElement(cur.getX(), cur.getY());

		ArrayList<UIInteraction> ui = new ArrayList<UIInteraction>();

		Stats s = e.getStats();
		Stats c = s;
		if (s == null)
			c = new Stats();

		ui.add(new SelectInput("Has stats: \001N\002o, \001Y\002es",
				new String[] { "N", "Y" }, true, (s == null) ? 0 : 1, false));
		ElementEditorDefaults.generateStatUIFields(getEmuMode(), e, ui, c);

		currMenu = new MultiInput("Stats Inspector "
				+ e.getClass().getSimpleName() + " @ " + cur + "\n"
				+ "(\001ENTER\002 to save, \001ESC\002 to cancel,"
				+ " \001UP\002 and \001DOWN\002 to navigate)", ui,
				new MenuCallback() {

					@Override
					public void menuCommand(String cmd, Object rider) {
						if (cmd != null && cmd.equals("SUBMIT")) {
							try {
								ArrayList<String> results = (ArrayList<String>) rider;

								if (results.get(0).startsWith("N"))
									e.setStats(null);
								else {
									int p1 = TimeAndMathUtils.parseInt(results
											.get(1));
									int p2 = TimeAndMathUtils.parseInt(results
											.get(2));
									int p3 = TimeAndMathUtils.parseInt(results
											.get(3));
									int stepX = TimeAndMathUtils
											.parseInt(results.get(4));
									int stepY = TimeAndMathUtils
											.parseInt(results.get(5));
									int cycle = TimeAndMathUtils
											.parseInt(results.get(6));
									int currInstr = TimeAndMathUtils
											.parseInt(results.get(7));

									Stats ns = new Stats(e.getX(), e.getY(),
											p1, p2, p3, stepX, stepY, cycle,
											currInstr, results.get(8));
									e.setStats(ns);
								}
							} catch (Exception e) {
								e.printStackTrace();
								reportError("Problem setting stat info\r\n"
										+ e.toString());
							}
						}
					}

				});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.game.GameController#getBoard()
	 */
	@Override
	public Board getBoard() {
		return game.getBoard();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.game.GameController#getBoardIdx()
	 */
	@Override
	public int getBoardIdx() {
		return game.getBoardIdx();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.game.GameController#getBoardList()
	 */
	@Override
	public ArrayList<Board> getBoardList() {
		return game.getBoardList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.game.GameController#getElementDefaults()
	 */
	@Override
	public ElementDefaults getElementDefaults() {
		return game.getElementDefaults();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.game.GameController#getElementResolver()
	 */
	@Override
	public ElementResolver getElementResolver() {
		return game.getElementResolver();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.game.GameController#getPlayer()
	 */
	@Override
	public Player getPlayer() {
		return game.getPlayer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.game.GameController#getState()
	 */
	@Override
	public GameState getState() {
		return game.getState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.game.GameController#isPaused()
	 */
	@Override
	public boolean isPaused() {
		return game.isPaused();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.game.GameController#setBoard(int)
	 */
	@Override
	public void setBoard(int i) {
		game.setBoard(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.game.GameController#setBoardList(java.util.
	 * ArrayList)
	 */
	@Override
	public void setBoardList(ArrayList<Board> boards) {
		game.setBoardList(boards);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.game.GameController#setMenu(java.lang.String,
	 * java.lang.String, com.isaacbrodsky.freeze.menus.MenuCallback,
	 * java.lang.Object)
	 */
	@Override
	public void setMenu(String title, String msg, MenuCallback callback,
			Object rider) {
		game.setMenu(title, msg, callback, rider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.game.GameController#setMessage(java.lang.String
	 * )
	 */
	@Override
	public void setMessage(String msg) {
		game.setMessage(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.game.GameController#setMessage(java.lang.String
	 * , java.lang.String, com.isaacbrodsky.freeze.menus.MenuCallback,
	 * java.lang.Object)
	 */
	@Override
	public void setMessage(String title, String msg, MenuCallback callback,
			Object rider) {
		game.setMessage(title, msg, callback, rider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.game.GameController#setPaused(boolean)
	 */
	@Override
	public void setPaused(boolean paused) {
		game.setPaused(paused);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.game.GameController#startPlaying()
	 */
	@Override
	public void startPlaying() {
		game.startPlaying();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());

		sb.insert(0, "Editor\n");
		sb.append("\n\nEditor mode: ").append(mode);
		sb.append("\nCursor: ").append(cur);
		sb.append("\nBuffer/pallete index: ").append(bufferIdx).append(" / ")
				.append(curColor);
		sb.append("\nBuffer:\n").append(buffer);

		return sb.toString();
	}
}
