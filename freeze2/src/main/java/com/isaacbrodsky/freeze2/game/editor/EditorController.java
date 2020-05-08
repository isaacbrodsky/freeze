/**
 *
 */
package com.isaacbrodsky.freeze2.game.editor;

import com.isaacbrodsky.freeze2.EmuMode;
import com.isaacbrodsky.freeze2.ZGame;
import com.isaacbrodsky.freeze2.elements.*;
import com.isaacbrodsky.freeze2.game.*;
import com.isaacbrodsky.freeze2.graphics.ElementColoring;
import com.isaacbrodsky.freeze2.graphics.GraphicsBlock;
import com.isaacbrodsky.freeze2.graphics.NamedColor;
import com.isaacbrodsky.freeze2.graphics.Renderer;
import com.isaacbrodsky.freeze2.menus.*;
import com.isaacbrodsky.freeze2.menus.stateditor.*;
import com.isaacbrodsky.freeze2.utils.Point;
import com.isaacbrodsky.freeze2.utils.TimeAndMathUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author isaac
 * 
 */
public class EditorController implements GameController {
	private final static ElementColoring STAT_COUNT_COLOR = ElementColoring.forNames(NamedColor.WHITE, NamedColor.DARKBLUE);

	public static final int USER_BUFFER_SLOTS = 11;

	private GameController game;
	private UIInteraction currMenu;
	private TypingInput typingInput;

	private final Cursor cur;

	private int bufferIdx;
	private ArrayList<ElementBuffer> buffer;
	private boolean bufferLocked;

	private EditMode editMode;
	private ViewMode viewMode;

	private int curColor;

	/**
	 * @param game
	 */
	public EditorController(GameController game) {
		this.game = game;

		cur = new Cursor();

		curColor = 0x0F;

		buffer = new ArrayList<>();
		resetBuffer();
		bufferIdx = USER_BUFFER_SLOTS;
		bufferLocked = false;

		editMode = EditMode.SELECT;
		viewMode = ViewMode.EDITOR;
		typingInput = null;
		currMenu = null;
	}

	@Override
	public int currentTick() {
		return game.currentTick();
	}

	@Override
	public void render(Renderer renderer, boolean blinking) {
		if (getBoard() == null) {
			renderer.renderText(0, 0, "null board.", Renderer.SYS_COLOR);

			// Allow rendering menus over corrupt boards
			if (currMenu != null)
				currMenu.render(renderer, 0, true);
			return;
		}

		// scrolling support
		int px = getXViewOffset();
		int py = getYViewOffset();

		for (int x = px + 1; x <= px + Renderer.DISPLAY_WIDTH; x++) {
			for (int y = py + 1; y <= py + Renderer.DISPLAY_HEIGHT; y++) {
				Tile t = getBoard().tileAt(x, y);
				Element e = resolveElement(t.getType());
				GraphicsBlock block = e.impl().draw(this, getBoard(), x, y, t, e);
				ElementBuffer buf = getElementBuffer(x, y, false);
				renderer.set(x - 1 - px, y - 1 - py, overrideGraphicsBlock(x, y, block, buf, false));
			}
		}

		if (typingInput != null && blinking)
			typingInput.render(renderer, px, py);

		if (currMenu != null)
			currMenu.render(renderer, 0, true);
		else
			cur.render(renderer, blinking, px, py, editMode.cursorCharacter());
	}

	public GraphicsBlock overrideGraphicsBlock(int x, int y, GraphicsBlock block, ElementBuffer buf, boolean isSidebar) {
		if (viewMode != ViewMode.PREVIEW) {
			// Invisible override mode
			if (buf.tile.getType() == CommonElements.INVISIBLE) {
				return new GraphicsBlock(block.getColoring(), 176);
			}
		}
		switch (viewMode) {
			case PREVIEW:
				if (isSidebar)
					break;
				if (getBoard().getState().dark != 0) {
					if (resolveElement(buf.tile.getType()).def().isVisibleInDark())
						break;
					if (TimeAndMathUtils.ipow(cur.getX() - x, 2) + (TimeAndMathUtils.ipow(cur.getY() - y, 2) * 2) >=
							ZGameController.TORCH_DIST_SQR) {
						return new GraphicsBlock(0x07, 176);
					}
				}
				break;
			case EMPTIES:
				// Color empty mode:
				if (buf.tile.getType() == CommonElements.EMPTY) {
					return new GraphicsBlock(buf.tile.getColor(), 176);
				}
				break;
			case STAT_COUNT:
				// Show stats
				if (buf.stats.size() > 0) {
					return renderDebugNumberBlock(buf.stats.size());
				}
				break;
			case STAT_ORDER:
				// Show stat order (only takes first stat)
				// TODO: Make it work for sidebar
				if (isSidebar)
					break;
				int statId = getBoard().statIdAt(x, y);
				if (statId >= 0) {
					return renderDebugNumberBlock(statId);
				}
				break;
			case SUPER_WINDOW:
				if (isSidebar)
					break;
				int boardWidth = getBoard().getWidth();
				int boardHeight = getBoard().getHeight();
				int windowWidth = 24;
				int windowHeight = 20;

				// MIN DIST: 6 Y DOWN, 8 Y UP, 9 X LEFT, 10 X RIGHT
				// MAX DIST: 11 Y DOWN, 13 Y UP, 13 X LEFT, 14 X RIGHT
				// Numbers are incremented by one so that the area visible
				// is the area visible in SuperZZT.

				int maxVisibleLeft = Math.min(cur.getX() - 14, boardWidth - windowWidth);
				int maxVisibleDown = Math.min(cur.getY() - 14, boardHeight - windowHeight);
				int maxVisibleRight = Math.max(cur.getX() + 15, windowWidth + 1);
				int maxVisibleUp = Math.max(cur.getY() + 12, windowHeight + 1);

				int minVisibleLeft = Math.min(cur.getX() - 10, boardWidth - windowWidth);
				int minVisibleDown = Math.min(cur.getY() - 9, boardHeight - windowHeight);
				int minVisibleRight = Math.max(cur.getX() + 11, windowWidth + 1);
				int minVisibleUp = Math.max(cur.getY() + 7, windowHeight + 1);

				// Farthest visible from this location
				// (Note the horizontal bars are just offscreen when not at an edge)
				if (x == maxVisibleLeft || x == maxVisibleRight
						|| y == maxVisibleDown || y == maxVisibleUp) {
					return new GraphicsBlock(0x0F, 219);
				}
				// Definitely visible from this location
				if (x == minVisibleLeft || x == minVisibleRight
						|| y == minVisibleDown || y == minVisibleUp) {
					return new GraphicsBlock(0x07, 176);
				}
				break;
			case MONOCHROME:
				if (buf.tile.getType() == CommonElements.EMPTY)
					break;
				ElementColoring color = block.getColoring().toMonochrome();
				return new GraphicsBlock(color, block.getCharIndex());
			case OOP_PRESENT:
				for (Stat s : buf.stats) {
					if (s.oopLength < 0) {
						return new GraphicsBlock(0x2F, 'B');
					}
					if (s.oop != null && s.oop.toLowerCase().startsWith("#bind")) {
						return new GraphicsBlock(0x1F, 'B');
					}
					if (s.oop != null && s.oop.length() > 0) {
						return new GraphicsBlock(0x1F, 'P');
					}
				}
				break;
			case WALKABLE:
				// Note that this isn't the ZZT definition of "walkable" -
				// this is trying to determine what areas are reachable
				// by the player. So, things that can be picked up or
				// destroyed by the player like ammo and gems are marked
				// as walkable.
				ElementDef def = resolveElement(buf.tile.getType()).def();
				if (!def.walkable && !def.pushable && !def.destructible) {
					return new GraphicsBlock(0x70, 0);
				} else {
					return new GraphicsBlock(0, 0);
				}
		}
		return block;
	}

	private static GraphicsBlock renderDebugNumberBlock(long i) {
		if (i > 0xF * 6)
			return new GraphicsBlock(STAT_COUNT_COLOR, '*');
		else
			return new GraphicsBlock(ElementColoring.forCode(0xF | ((((int) i / 0xF) + 1) << 4)), Long.toHexString(i % 0xF).charAt(0));
	}

	@Override
	public int getXViewOffset() {
		int px = getCursor().getX() - 30;
		px = Math.max(px, 0);
		if (px + Renderer.DISPLAY_WIDTH > getBoard().getWidth())
			px = getBoard().getWidth() - Renderer.DISPLAY_WIDTH;
		return px;
	}

	@Override
	public int getYViewOffset() {
		int py = getCursor().getY() - 14;
		py = Math.max(py, 0);
		if (py + Renderer.DISPLAY_HEIGHT > getBoard().getHeight())
			py = getBoard().getHeight() - Renderer.DISPLAY_HEIGHT;
		return py;
	}

	public ArrayList<ElementBuffer> getBuffer() {
		return buffer;
	}

	public int getBufferIdx() {
		return bufferIdx;
	}

	public void toggleLockBuffer() {
		bufferLocked = !bufferLocked;
	}

	public boolean isBufferLocked() {
		return bufferLocked;
	}

	public void setBufferIdx(int i) {
		bufferIdx = i;
		if (bufferIdx >= buffer.size())
			bufferIdx = 0;
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

	public void setColorIdx(int color) {
		this.curColor = color;

		resetBuffer();
	}

	@Override
	public void startPlaying() {
		game.startPlaying();
	}

	@Override
	public void runSimulation(long elapsed) {
		typingInput = (TypingInput) checkMenu(typingInput);
		currMenu = checkMenu(currMenu);

		if (typingInput == null && editMode == EditMode.TYPE)
			editMode = EditMode.SELECT;

		if (editMode == EditMode.DRAW)
			popBuffer();
	}

	private static UIInteraction checkMenu(UIInteraction menu) {
		if (menu != null) {
			menu.tick();
			if (!menu.stillAlive()) {
				return null;
			}
		}
		return menu;
	}

	public Cursor getCursor() {
		return cur;
	}

	public ElementBuffer getElementBuffer(int x, int y) {
		return getElementBuffer(x, y, true);
	}

	public ElementBuffer getElementBuffer(int x, int y, boolean copy) {
		Tile t = getBoard().tileAt(x, y);
		List<Stat> allStats = getBoard().getStats().stream()
				.filter(s -> s.x == x && s.y == y)
				.map(s -> copy ? s.clone() : s)
				.collect(Collectors.toList());
		Element e = resolveElement(t.getType());
		GraphicsBlock block = e.impl().draw(this, getBoard(), x, y, t, e);
		return new ElementBuffer(t, allStats, block);
	}

	public void selectPop() {
		if (getBoard().tileAt(cur.getX(), cur.getY()).getType() == CommonElements.EMPTY) {
			popBuffer();
		} else {
			dropElement(new ElementBuffer(new Tile(), Collections.emptyList()));
		}
	}

	public void pushBuffer() {
		if (bufferLocked)
			return;
		ElementBuffer e = getElementBuffer(cur.getX(), cur.getY());

		resetBuffer();
		int idx = getBufferIdx();
		if (idx >= USER_BUFFER_SLOTS)
			idx = 0;
		buffer.set(idx, e);
	}

	private void resetBuffer() {
		List<Element> defaultBuffer = getElements().defaultEditorPalette();
		while (buffer.size() < defaultBuffer.size() + USER_BUFFER_SLOTS) {
			ElementBuffer e = new ElementBuffer(new Tile(CommonElements.EMPTY, 0), Collections.emptyList());
			buffer.add(e);
		}

		for (int i = 0; i < defaultBuffer.size(); i++) {
			Element e = defaultBuffer.get(i);
			buffer.set(i + USER_BUFFER_SLOTS, new ElementBuffer(new Tile(e, curColor), Collections.emptyList()));
		}
	}

	public void popBuffer() {
		ElementBuffer e = buffer.get(bufferIdx);

		dropElement(e);
	}

	public void dropElement(ElementBuffer e) {
		Board board = getBoard();

		Stat player = board.getPlayer();
		if (player != null && player.x == cur.getX() && player.y == cur.getY()) {
			// Do not permit overwriting the current player element.
			// TODO: It is possible to rewrite the player element to something else
			// - but usually this would be an error.
			return;
		}

		board.putTileAndStats(cur.getX(), cur.getY(), e.tile, e.stats.toArray(new Stat[0]));
	}

	public EditMode getEditMode() {
		return editMode;
	}

	public void setEditMode(EditMode n) {
		editMode = n;
	}

	public ViewMode getViewMode() {
		return viewMode;
	}

	public void setViewMode(ViewMode viewMode) {
		this.viewMode = viewMode;
	}

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
		List<Board> boards = getBoardList();
		List<StaticInput<Board>> ui = IntStream.range(0, boards.size())
				.mapToObj(i -> new StaticInput<>(
						BoardMultiInput.formatBoard(i, boards.get(i)),
						boards.get(i)
				))
				.collect(Collectors.toList());

		currMenu = new BoardMultiInput(this,
				"Board Select\n"
						+ "(\001Q\002 and \001A\002 to reorder,"
						+ " \001UP\002 and \001DOWN\002 to navigate,\n"
						+ " \001I\002 for insert, \001U\002 to dupe, \001DEL\002 to delete)", ui, getBoardIndex(),
				(cmd, rider) -> {
					if ("SUBMIT".equals(cmd)) {
						List<Board> processedBoardLists = rider.boards.stream()
								.filter(Objects::nonNull)
								.collect(Collectors.toList());
						int indexOffset = (int) rider.boards.stream()
								.limit(rider.selected)
								.filter(Objects::isNull)
								.count();
						int selectedIndex = rider.selected - indexOffset;

						setBoardList(processedBoardLists);
						if (selectedIndex >= 0 && selectedIndex < getBoardList().size())
							setBoard(selectedIndex);
					}
				});
	}

	public void engageTyping() {
		int off = (curColor & 0x0F) - 9;
		if (off < 0) {
			return;
		}
		int type = getElements().getTextMin() + off;
		typingInput = new TypingInput(getBoard(), type, cur);
		editMode = EditMode.TYPE;
	}

	@Override
	public Elements getElements() {
		return game.getElements();
	}

	public void fill() {
		Tile orig = getBoard().tileAt(cur.getX(), cur.getY());
		Set<Point> toFill = new HashSet<>();
		Stack<Point> frontier = new Stack<>();
		frontier.add(new Point(cur.getX(), cur.getY()));

		while (!frontier.isEmpty()) {
			Point p = frontier.pop();

			for (int dx = -1; dx <= 1; dx++) {
				for (int dy = -1; dy <= 1; dy++) {
					if (dx != 0 && dy != 0) {
						continue;
					}
					Point next = new Point(p.getX() + dx, p.getY() + dy);
					if (getBoard().inBounds(next.getX(), next.getY())
							&& !toFill.contains(next)) {
						Tile nextTile = getBoard().tileAt(next.getX(), next.getY());
						if (nextTile.getType() == orig.getType()
								&& (orig.getType() == CommonElements.EMPTY || nextTile.getColor() == orig.getColor())) {
							frontier.add(next);
							toFill.add(next);
						}
					}
				}
			}
		}

		ElementBuffer e = buffer.get(bufferIdx);
		for (Point p : toFill) {
			getBoard().putTileAndStats(p.getX(), p.getY(), e.tile, e.stats.toArray(new Stat[0]));
		}
	}

	public void reportError(String msg) {
		currMenu = new Menu(ZGame.APP_SHORT,
				MenuUtils.wrapText("$Engine Error\n\n" + msg),
				null);
	}

	public void showSuperGameInspector() {
		ArrayList<UIInteraction> ui = new ArrayList<>();

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
			ui.add(new SelectInput(ElementColoring.forCode(code).getForeName().toString()
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

		ui.add(new BoardInput("Board north:", state.boardNorth, this, false));
		ui.add(new BoardInput("Board south:", state.boardSouth, this, false));
		ui.add(new BoardInput("Board west:", state.boardWest, this, false));
		ui.add(new BoardInput("Board east:", state.boardEast, this, false));

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
									// Doesn't set message timer
									state.message = results.get(4 + offset);
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
			ui.add(new SelectInput(ElementColoring.forCode(code).getForeName().toString()
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

	public void showStatList(boolean filterToPoint) {
		String title = "Stats List";
		List<Stat> stats = getBoard().getStats();
		List<Integer> statIds = IntStream.range(0, stats.size())
				.boxed()
				.collect(Collectors.toList());

		if (filterToPoint) {
			ElementBuffer e = getElementBuffer(cur.getX(), cur.getY());
			title += " " + resolveElement(e.tile.getType()) + " @ " + cur;
			statIds = statIds.stream()
				.filter(i -> stats.get(i).x == cur.getX() && stats.get(i).y == cur.getY())
				.collect(Collectors.toList());
			title += " (" + statIds.size() + "/" + stats.size() + " stats)";
		} else {
			title += " (" + stats.size() + " stats)";
		}

		final List<Integer> initialStatIds = statIds;
		List<StatInput> ui = initialStatIds.stream()
				.map(i -> {
					Stat s = stats.get(i);
					return new StatInput(this, getBoard(), i, new Stat(s));
				})
				.collect(Collectors.toList());
		int nextStatIndex = getBoard().getStats().size() - 1;
		if (ui.size() == 0) {
			nextStatIndex++;
			ui = Collections.singletonList(new StatInput(this, getBoard(), getBoard().getStats().size(), null));
		}

		currMenu = new StatMultiInput(this,
				getBoard(),
				title + "\n"
				+ "(\001Q\002 and \001A\002 to reorder,"
				+ " \001UP\002 and \001DOWN\002 to navigate,\n"
				+ " \001I\002 to insert, \001U\002 to dupe, \001DEL\002 to delete)",
				nextStatIndex,
				ui,
				new MenuCallback() {
					@Override
					public void menuCommand(String cmd, Object rider) {
						if (cmd != null && cmd.equals("SUBMIT")) {
							// TODO: rider should be list of stats to put in those slots
							try {
								List<Stat> results = (List<Stat>) rider;
								List<Stat> boardStats = getBoard().getStats();

								for (int i = 0; i < results.size(); i++) {
									Stat editedStat = results.get(i);
									if (i >= initialStatIds.size()){
										boardStats.add(editedStat);
									} else {
										int origStatId = initialStatIds.get(i);
										boardStats.set(origStatId, editedStat);
									}
								}
								boardStats.removeIf(s -> s == null);
							} catch (Exception e) {
								e.printStackTrace();
								reportError("Problem setting stat info\r\n"
										+ e.toString());
							}
						}
					}

				});
	}

	public void showTileInspector() {
		ElementBuffer e = getElementBuffer(cur.getX(), cur.getY(), false);

		currMenu = TileEditor.makeTileInspector(
				this, e, cur, new MenuCallback<ElementBuffer>() {
					@Override
					public void menuCommand(String cmd, ElementBuffer rider) {
						// TODO: Make a constant
						if (cmd.equals("SUBMIT")) {
							dropElement(rider);
						}
					}
				}
		);
	}

	public void showStatInspector() {
		ElementBuffer e = getElementBuffer(cur.getX(), cur.getY(), false);

		// TODO: Only supports editing a single existing stat!
		if (e.getStats().size() == 0)
			return;

		Stat s = e.getStats().get(0);
		Stat c = s;
		if (s == null) {
			ElementDef def = resolveElement(e.tile.getType()).def();
			c = AddElementUtils.createDefaultStat(def, cur.getX(), cur.getY());
		}

		currMenu = StatEditor.makeStatInspector(
				this, e.tile, new Stat(c), new MenuCallback() {
					@Override
					public void menuCommand(String cmd, Object rider) {
						// TODO: Make a constant
						if (cmd.equals("SUBMIT")) {
							s.assignFrom((Stat) rider);
						}
					}
				}
		);
	}

	@Override
	public Board getBoard() {
		return game.getBoard();
	}

	@Override
	public int getBoardIndex() {
		return game.getBoardIndex();
	}

	@Override
	public List<Board> getBoardList() {
		return game.getBoardList();
	}

	@Override
	public GameState getState() {
		return game.getState();
	}

	@Override
	public boolean isPaused() {
		return game.isPaused();
	}

	@Override
	public boolean isMonochrome() {
		return viewMode == ViewMode.MONOCHROME;
	}

	@Override
	public void setBoard(int i) {
		game.setBoard(i);
	}

	@Override
	public void setBoardList(List<Board> boards) {
		game.setBoardList(boards);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());

		sb.insert(0, "Editor\n");
		sb.append("\n\nEditor mode: ").append(editMode);
		sb.append("\nCursor: ").append(cur);
		sb.append("\nBuffer/pallete index: ").append(bufferIdx).append(" / ")
				.append(curColor);
		sb.append("\nBuffer:\n").append(buffer);

		return sb.toString();
	}

	@Override
	public EmuMode getEmuMode() { return game.getEmuMode(); }

	@Override
	public int statsLimit() {
		return game.statsLimit();
	}

	public boolean isAbnormal() {
		if (getBoard().getStats().size() == 0) {
			return true;
		}
		Stat player = getBoard().getPlayer();
		if (!getBoard().inBounds(player.x, player.y)) {
			return true;
		}
		Tile playerTile = getBoard().tileAt(player.x, player.y);
		if (playerTile.getType() != CommonElements.PLAYER) {
			return true;
		}

		return false;
	}

	public boolean isOverLimit() {
		int numStats = getBoard().getStats().size();
		if (numStats > statsLimit() + 1)
			return true;

		return false;
	}
}
