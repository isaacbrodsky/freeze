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
import com.isaacbrodsky.freeze.elements.Monitor;
import com.isaacbrodsky.freeze.elements.Passage;
import com.isaacbrodsky.freeze.elements.Player;
import com.isaacbrodsky.freeze.elements.Torch;
import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.elements.data.Stats;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.graphics.ElementColoring;
import com.isaacbrodsky.freeze.graphics.GraphicsBlock;
import com.isaacbrodsky.freeze.graphics.Renderer;
import com.isaacbrodsky.freeze.menus.Menu;
import com.isaacbrodsky.freeze.menus.MenuCallback;
import com.isaacbrodsky.freeze.menus.UIInteraction;
import com.isaacbrodsky.freeze.menus.Menu.SendMode;

/**
 * @author isaac
 * 
 */
public class ZGameController implements GameController {
	public static final ElementColoring FLASH_COLOR = new ElementColoring(
			"DARKPURPLE", "BLACK");
	public static final ElementColoring DARK_COLOR = new ElementColoring(
			"DARKGRAY", "BLACK");

	private GameState state;
	private ArrayList<Board> boards;
	private int currBoardIndex;
	private Board currBoard;
	private boolean paused, title;

	private int cycleState, boardFlashState, timePassedState;

	private Menu currMenu;

	private ElementResolver resolve;
	private ElementDefaults defaults;

	public ZGameController() {
		this.state = new GameState(EmuMode.ZZT);
		cycleState = 1;
		paused = false;
		currMenu = null;
		title = true;
		boardFlashState = 0;

		resolve = new ElementResolver(EmuMode.ZZT);
		defaults = new ElementDefaults(EmuMode.ZZT);
	}

	public ZGameController(GameState state) {
		this();
		this.state = state;
	}

	public ZGameController(GameController o) {
		this();
		this.state = o.getState();
		ArrayList<Board> bl = new ArrayList<Board>(o.getBoardList().size());
		for (int i = 0; i < o.getBoardList().size(); i++)
			bl.add(o.getBoardList().get(i));
		setBoardList(bl);
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

	public ArrayList<Board> getBoardList() {
		return boards;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.game.GameController#render(com.isaacbrodsky
	 * .freeze.graphics.Renderer, boolean)
	 */
	@Override
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
		// render the board
		for (int x = 0; x < currBoard.getWidth(); x++) {
			for (int y = 0; y < currBoard.getHeight(); y++) {
				renderBlock(renderer, blinking, player, x, y);

				// board entry flash
				if (r != null)
					if (r.nextInt(10) > 3)
						renderer.renderText(x, y, 219, FLASH_COLOR);
			}
		}

		// board is dark
		if (currBoard.getState().dark != 0) {
			for (int x = 0; x < currBoard.getWidth(); x++) {
				for (int y = 0; y < currBoard.getHeight(); y++) {
					// these elements are never hidden
					if (!(currBoard.getElements()[x][y][0] instanceof Player
							|| currBoard.getElements()[x][y][0] instanceof Torch || currBoard
							.getElements()[x][y][0] instanceof Passage)) {
						// if a torch is on
						if (state.tcycles > 0) {
							// exclude torch lit cells
							int torchwidth = -1;
							if (Math.abs(y - player.getY()) == 4)
								torchwidth = 5;
							else if (Math.abs(y - player.getY()) == 3)
								torchwidth = 6;
							else if (Math.abs(y - player.getY()) == 2
									|| Math.abs(y - player.getY()) == 1)
								torchwidth = 7;
							else if (Math.abs(y - player.getY()) == 0)
								torchwidth = 8;

							if (Math.abs(x - player.getX()) < torchwidth) {
								continue;
							}
						}
						renderer.renderText(x, y, 176, DARK_COLOR);
					}

					// board entry flash
					if (r != null)
						if (r.nextInt(10) > 3)
							renderer.renderText(x, y, 219, FLASH_COLOR);
				}
			}
		}

		if (currBoard.getMessage() != null) {
			renderer.renderText(Menu.centerText(" " + currBoard.getMessage()
					+ " ", currBoard.getWidth()) + 1,
					currBoard.getHeight() - 1, " " + currBoard.getMessage()
							+ " ", renderer.getFlashingColor());
		}

		if (currMenu != null) {
			currMenu.render(renderer, 0, true);
		}
	}

	/**
	 * @param renderer
	 * @param blinking
	 * @param player
	 * @param x
	 * @param y
	 */
	private void renderBlock(Renderer renderer, boolean blinking,
			Player player, int x, int y) {
		if (currBoard.getElements()[x][y][0] == null) {
			if (currBoard.getElements()[x][y][1] != null)
				currBoard.getElements()[x][y][0] = currBoard.getElements()[x][y][1];
			else {
				renderer.set(x, y, new GraphicsBlock(new ElementColoring(0),
						0x20));
				return;
			}
		}

		if (blinking && isPaused()) {
			if (currBoard.getElements()[x][y][0].equals(player)) {
				if (currBoard.getElements()[x][y][1] != null) {
					renderer.set(x, y, new GraphicsBlock(currBoard
							.getElements()[x][y][1].getColoring(), currBoard
							.getElements()[x][y][1].getDisplayCharacter()));
				} else {
					renderer.set(x, y, new GraphicsBlock(
							new ElementColoring(0), 0x20));
				}
				return;
			}
		}

		if (title && currBoard.getElements()[x][y][0].equals(player)) {
			renderer.set(x, y, new GraphicsBlock(new ElementColoring(0), 0));
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
		renderer.set(x, y, gb);
	}

	@Override
	public void setMessage(String msg) {
		setMessage("Untitled", msg, null, null);
	}

	@Override
	public void setMessage(String title, String msg, MenuCallback callback,
			Object rider) {
		if (msg.split("[\\r|\\n]").length > 1) {
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

		// ZZT masks the current player
		// with a black "Monitor" element
		// (which itself becomes the ACE)
		if (title && currBoardIndex == 0) {
			if (p != null) {
				currBoard.removeAt(p.getX(), p.getY(), p);
				Monitor monTemp = new Monitor();
				monTemp.createInstance(new SaveData(3, 0));
				monTemp.setXY(p.getX(), p.getY());
				currBoard.putElement(p.getX(), p.getY(), monTemp);
			} else {
				if (!(currBoard.getElementsByType(Monitor.class).size() > 0))
					reportError("No player on title screen.");
			}
		}

		state.timePassed = timePassedState = 0;

		if (p != null) {
			currBoard.getState().enterX = currBoard.getPlayer().getX();
			currBoard.getState().enterY = currBoard.getPlayer().getY();
		}

		if (title && currBoardIndex == 0) {
			currBoard.setACE(null);
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

			// This is done *before* the ticking so that objects on the board
			// can set their own messages, which will override this one.
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
		// replace Monitor which has been masked on the title
		// with the player
		List<Element> e = boards.get(0).getElementsByType(Monitor.class);
		if (e.size() > 0) {
			int x = e.get(0).getX();
			int y = e.get(0).getY();
			boards.get(0).removeAt(x, y, e.get(0));
			Player player = new Player();
			player.createInstance(new SaveData(0x04, 0x1F));
			player.setStats(new Stats(x, y, 0, 0, 0, 0, 0, 1, -1, null, null,
					null));
			player.setXY(x, y);
			boards.get(0).putElement(x, y, player);
		}

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
		return state.toString() + "\r\nBoards:\r\n" + boards;
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
