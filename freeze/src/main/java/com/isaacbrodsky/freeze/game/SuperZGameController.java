/**
 * 
 */
package com.isaacbrodsky.freeze.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.isaacbrodsky.freeze.EmuMode;
import com.isaacbrodsky.freeze.ZGame;
import com.isaacbrodsky.freeze.elements.Element;
import com.isaacbrodsky.freeze.elements.ElementDefaults;
import com.isaacbrodsky.freeze.elements.ElementResolver;
import com.isaacbrodsky.freeze.elements.Player;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.graphics.ElementColoring;
import com.isaacbrodsky.freeze.graphics.GraphicsBlock;
import com.isaacbrodsky.freeze.graphics.Renderer;
import com.isaacbrodsky.freeze.menus.Menu;
import com.isaacbrodsky.freeze.menus.MenuCallback;
import com.isaacbrodsky.freeze.menus.MenuUtils;
import com.isaacbrodsky.freeze.menus.UIInteraction;
import com.isaacbrodsky.freeze.menus.Menu.SendMode;

/**
 * @author isaac
 * 
 */
public class SuperZGameController implements GameController {
	/**
	 * Color used for board transition flashes
	 */
	public static final ElementColoring FLASH_COLOR = new ElementColoring(
			"DARKPURPLE", "BLACK");

	private GameState state;
	private ArrayList<Board> boards;
	private int currBoardIndex;
	private Board currBoard;
	private boolean paused, title;

	private int cycleState, boardFlashState, timePassedState;

	private Menu currMenu;

	private ElementResolver resolve;
	private ElementDefaults defaults;

	public SuperZGameController(GameController o) {
		this();
		this.state = o.getState();
		ArrayList<Board> bl = new ArrayList<Board>(o.getBoardList().size());
		for (int i = 0; i < o.getBoardList().size(); i++)
			bl.add(o.getBoardList().get(i));
		setBoardList(bl);
	}

	public SuperZGameController() {
		this.state = new GameState(EmuMode.SUPERZZT);
		cycleState = 1;
		paused = false;
		currMenu = null;
		title = true;
		boardFlashState = 0;

		resolve = new ElementResolver(EmuMode.SUPERZZT);
		defaults = new ElementDefaults(EmuMode.SUPERZZT);
	}

	public SuperZGameController(GameState state) {
		this();
		this.state = state;
	}

	public GameState getState() {
		return state;
	}

	/**
	 * @param boards
	 */
	public void setBoardList(ArrayList<Board> boards) {
		this.boards = boards;
		setBoard(state.startBoard);
	}

	@Override
	public ArrayList<Board> getBoardList() {
		return boards;
	}

	/**
	 * @param renderer
	 */
	public void render(Renderer renderer, boolean blinking) {
		if (currBoard == null) {
			renderer.renderText(0, 0, "null board.", Renderer.SYS_COLOR);
			return;
		}

		Player player = getPlayer();
		Random r = null;
		if (boardFlashState < 2) {
			r = new Random();
			boardFlashState++;
		}

		// scrolling support
		int px = getXViewOffset();
		int py = getYViewOffset();

		for (int x = px; x < px + Renderer.DISPLAY_WIDTH; x++) {
			for (int y = py; y < py + Renderer.DISPLAY_HEIGHT; y++) {
				renderBlock(renderer, blinking, player, x, y, x - px, y - py);
				if (r != null)
					if (r.nextInt(10) > 3)
						renderer.renderText(x - px, y - py, 219, FLASH_COLOR);
			}
		}

		String msg = currBoard.getMessage();
		if (msg != null) {
			String[] split = msg.split("\\r?\\n");
			if (split.length > 1) {
				renderer.renderText(MenuUtils.centerText(split[0],
						Renderer.DISPLAY_WIDTH) + 1,
						Renderer.DISPLAY_HEIGHT - 2, " " + split[0] + " ",
						renderer.getFlashingColor());
				renderer.renderText(MenuUtils.centerText(split[1],
						Renderer.DISPLAY_WIDTH) + 1,
						Renderer.DISPLAY_HEIGHT - 1, " " + split[1] + " ",
						renderer.getFlashingColor());
			} else {
				renderer.renderText(MenuUtils.centerText(msg,
						Renderer.DISPLAY_WIDTH) + 1,
						Renderer.DISPLAY_HEIGHT - 1, " " + msg + " ", renderer
								.getFlashingColor());
			}
		}

		if (currMenu != null) {
			currMenu.render(renderer, 0, true);
		}
	}

	public int getXViewOffset() {
		int px = getPlayer().getX() - 30;
		px = Math.max(px, 0);
		if (px + Renderer.DISPLAY_WIDTH > currBoard.getWidth())
			px = currBoard.getWidth() - Renderer.DISPLAY_WIDTH;
		return px;
	}

	public int getYViewOffset() {
		int py = getPlayer().getY() - 12;
		py = Math.max(py, 0);
		if (py + Renderer.DISPLAY_HEIGHT > currBoard.getHeight())
			py = currBoard.getHeight() - Renderer.DISPLAY_HEIGHT;
		return py;
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
		if (currBoard.getElements()[x][y][0] == null) {
			if (currBoard.getElements()[x][y][1] != null)
				currBoard.getElements()[x][y][0] = currBoard.getElements()[x][y][1];
			else {
				renderer.set(rx, ry, new GraphicsBlock(new ElementColoring(0),
						0x20));
				return;
			}
		}

		if (blinking && isPaused()) {
			if (currBoard.getElements()[x][y][0].equals(player)) {
				if (currBoard.getElements()[x][y][1] != null) {
					renderer.set(rx, ry, new GraphicsBlock(currBoard
							.getElements()[x][y][1].getColoring(), currBoard
							.getElements()[x][y][1].getDisplayCharacter()));
				} else {
					renderer.set(rx, ry, new GraphicsBlock(new ElementColoring(
							0), 0x20));
				}
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

		GraphicsBlock gb = new GraphicsBlock(col,
				currBoard.getElements()[x][y][0].getDisplayCharacter());
		renderer.set(rx, ry, gb);
	}

	@Override
	public void setMessage(String msg) {
		setMessage("Untitled", msg, null, null);
	}

	@Override
	public void setMessage(String title, String msg, MenuCallback callback,
			Object rider) {
		if (msg.split("\\r?\\n").length > 2) {
			setMenu(title, msg, callback, rider);
		} else {
			currBoard.setMessage(msg);
		}
	}

	public void setMenu(String title, String msg, MenuCallback callback,
			Object rider) {
		currMenu = new Menu(title, msg, callback, SendMode.NO, false, rider);
	}

	/**
	 * @param i
	 */
	public void setBoard(int i) {
		currBoardIndex = i;
		currBoard = boards.get(currBoardIndex);

		Player p = currBoard.getPlayer();

		// SuperZZT does not mask the player with a monitor.
		// TODO verify SUPERZ monitor/title screen

		state.timePassed = timePassedState = 0;

		if (p != null) {
			currBoard.getState().enterX = p.getX();
			currBoard.getState().enterY = p.getY();
		}

		boardFlashState = 0;
	}

	/**
	 * Returns the current board object.
	 * 
	 * @return
	 */
	public Board getBoard() {
		return currBoard;
	}

	/**
	 * @return
	 */
	@Override
	public int getBoardIdx() {
		return currBoardIndex;
	}

	public UIInteraction getMenu() {
		return currMenu;
	}

	/**
	 * @param elapsed
	 */
	public void runSimulation(long elapsed) {
		if (currMenu != null) {
			int menuCode = currMenu.hashCode();
			currMenu.tick();
			boolean keepMenu = currMenu.stillAlive();
			if (!keepMenu && menuCode == currMenu.hashCode())
				currMenu = null;
		} else {
			if (isPaused()) {
				getPlayer().tick(this, currBoard);
				cycleState++;

				return;
			}

			Board b = currBoard;

			/*
			 * This is done *before* the ticking so that objects on the board
			 * can set tbeir own messages, which will override this one.
			 */
			if (getState().health <= 0)
				setMessage("Game over  -  press ESCAPE");

			List<Element> eList = b.getElementList();

			for (Element e : eList) {
				if (!e.equals(b.elementAt(e.getX(), e.getY())))
					continue;

				if (e.getStats() == null)
					continue;

				if (e.getCycle() != 0 && cycleState % e.getCycle() == 0)
					e.tick(this, b);
			}

			b.tick();

			cycleState++;
			if (b.getState().timeLimit > 0) {
				timePassedState++;
				if (timePassedState == 10) {
					state.timePassed++;
					timePassedState = 0;
				}
				if (state.timePassed >= b.getState().timeLimit) {
					getPlayer().message(this, b, Message.SHOT);
					if (b.getState().restart == 0) {
						b.getState().timeLimit = 0;
					}
					state.timePassed = 0;
				}
			}
		}
	}

	/**
	 * The board may have more than one Player element, this function returns
	 * ONLY the first. (or Active)
	 * 
	 * @return
	 */
	public Player getPlayer() {
		if (currBoard == null)
			return null;
		return currBoard.getPlayer();
		// List<Element> eList = currBoard.getElementList();
		//
		// for (Element e : eList) {
		// if (e instanceof Player)
		// return (Player) e;
		// }
		//
		// return null;
	}

	public void startPlaying() {
		title = false;
		setBoard(state.startBoard);
		setPaused(true);
	}

	/**
	 * @return
	 */
	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SuperZ\r\n" + state.toString() + "\r\nBoards:\r\n" + boards;
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
		setMenu(ZGame.APP_SHORT, "$Engine Error\n\n" + msg, null, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.game.GameController#getElementDefaults()
	 */
	@Override
	public ElementDefaults getElementDefaults() {
		return defaults;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.game.GameController#getElementResolver()
	 */
	@Override
	public ElementResolver getElementResolver() {
		return resolve;
	}
}
