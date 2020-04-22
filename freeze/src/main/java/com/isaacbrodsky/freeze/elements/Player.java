/**
 *
 */
package com.isaacbrodsky.freeze.elements;

import java.util.List;

import com.isaacbrodsky.freeze.elements.data.InteractionRule;
import com.isaacbrodsky.freeze.elements.data.InteractionRulesSet;
import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.elements.data.Stats;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.elements.oop.OOPHelpers;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.graphics.ElementColoring;
import com.isaacbrodsky.freeze.graphics.ElementColoring.ColorMode;

/**
 * A Player element represents the player in the game world. Player elements can
 * also represent player clones and "dead players".
 * 
 * @author isaac
 */
public class Player implements Element {
	private ElementColoring color;
	protected ColorMode cm;

	protected int stepX, stepY;
	private int x, y, forceNextX, forceNextY;
	private int lastX, lastY;
	private boolean shiftDown, spaceDown;
	private boolean moving, active;
	private boolean flashing;
	private int thischar;

	private Stats stats;
	private SaveData dat;

	public Player() {
		active = false;
		moving = false;
		shiftDown = spaceDown = false;
		flashing = false;

		lastX = lastY = 0;
	}

	@Override
	public void createInstance(SaveData dat) {
		cm = ColorMode.DOMINANT;
		this.color = new ElementColoring(dat.getColor(), cm);
		thischar = 2;
		this.dat = dat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getSaveData()
	 */
	@Override
	public SaveData getSaveData() {
		return dat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getStats()
	 */
	@Override
	public Stats getStats() {
		if (stats != null) {
			stats.x = getX();
			stats.y = getY();
			return stats;
		} else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.elements.Element#setStats(com.isaacbrodsky.
	 * freeze.elements.data.Stats)
	 */
	@Override
	public void setStats(Stats stats) {
		this.stats = stats;
		active = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getColoring()
	 */
	@Override
	public ElementColoring getColoring() {
		return color;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getCycle()
	 */
	@Override
	public int getCycle() {
		return 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		return thischar;
	}

	private static final InteractionRulesSet _IRS = new InteractionRulesSet(
			InteractionRule.POINT_BLANK_SHOOTABLE, InteractionRule.MOVEABLE_EW,
			InteractionRule.MOVEABLE_NS, InteractionRule.NOT_PLAYER_MOVEABLE);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getInteractionsRules()
	 */
	@Override
	public InteractionRulesSet getInteractionsRules() {
		return _IRS;
	}

	public boolean isActive() {
		return active;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.elements.Element#message(com.isaacbrodsky.freeze
	 * .game.GameController, com.isaacbrodsky.freeze.game.Board,
	 * com.isaacbrodsky.freeze.elements.oop.Message)
	 */
	@Override
	public void message(GameController game, Board board, Message msg) {
		if ((!active || isClone(board)) && (msg.isShot() || msg.isBombed())) {
			board.removeAt(x, y, this);
			return;
		}

		if (msg.isBombed()) {
			if (game.getState().ecycles > 0)
				return;

			game.getState().health -= 10;
			if (game.getState().health < 0)
				game.getState().health = 0;
			else
				game.setMessage("Ouch");
		} else if (msg.isShot()) {
			if (game.getState().ecycles > 0)
				return;

			game.getState().health -= 10;
			if (game.getState().health < 0)
				game.getState().health = 0;
			else
				game.setMessage("Ouch");

			if (board.getState().restart != 0 && game.getState().health != 0) {
				// True, this has the same problem as passages (possibly
				// destroying underlying data... WAIT!
				// ZZT *doesn't* have this problem because the Stats element has
				// the underT and underC properties!
				//
				// I've verified it myself by glitching a player onto a scroll
				// which I think was on top of something... I might want to see
				// what
				// happens when a Player is on top of a Scroll on top of a
				// object or whatever either via ZAP and line wall glitch
				// or just using ZTestMain and saving it.
				//
				// The RLE will have PLAYER, the ACE stats will have PASSAGE as
				// the underlying, PASSAGE stats will have EMPTY as it's
				// underlying element.
				OOPHelpers.moveElementAbs(x, y,
						game.getBoard().getState().enterX, game.getBoard()
								.getState().enterY, board, this);
				game.setPaused(true);
				game.getState().timePassed = 0;
			}
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#setXY(int, int)
	 */
	@Override
	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#tick()
	 */
	@Override
	public void tick(GameController game, Board board) {
		if (!active || isClone(board))
			return;

		if (game.getState().tcycles > 0 && !game.isPaused())
			game.getState().tcycles--;
		if (game.getState().ecycles < 0) {
			game.getState().ecycles = 0;
		}
		if (game.getState().ecycles == 0 && flashing == false) {
			color = new ElementColoring(/* color */0x1F,// masked
					cm);
			thischar = 2;
		} else if (game.getState().ecycles > 0 && !game.isPaused()) {
			if (thischar == 2)
				thischar = 1;
			else
				thischar = 2;

			int i = color.getBackCode();
			i++;
			if (i >= 0x07)
				i = 0;
			color = new ElementColoring(i, color.getForeCode(),
					ElementColoring.ColorMode.DOMINANT);

			game.getState().ecycles--;
		} else if (flashing) {
			thischar = 1;
			color = new ElementColoring(7, color.getForeCode(),
					ElementColoring.ColorMode.DOMINANT);

			flashing = false;
		}

		if (!(forceNextX == 0 && forceNextY == 0)) {
			stepX = forceNextX;
			stepY = forceNextY;
			forceNextX = forceNextY = 0;
		}

		int dirOff = board.boundsCheck(x + stepX, y + stepY);
		if (dirOff >= 0) {
			handleEdgeOfBoard(game, board, dirOff);

			return;
		}

		Element e = board.elementAt(x + stepX, y + stepY);
		if (((moving && shiftDown) || spaceDown) && !game.isPaused()) {
			if (spaceDown) {
				stepX = lastX;
				stepY = lastY;
			}
			handleShooting(game, board);
		} else if (moving) {
			handleMoving(game, board, e);
		}
	}

	/**
	 * @param game
	 * @param board
	 */
	private void handleShooting(GameController game, Board board) {
		if (game.getState().ammo > 0) {
			int shotsInPlay = 0;
			for (Element tmp : board.getElementsByType(Bullet.class)) {
				if (((Bullet) tmp).isPlayerShot())
					shotsInPlay++;
			}
			if (shotsInPlay < board.getState().shots)
				if (OOPHelpers.shoot(game, board, x, y, stepX, stepY, true,
						Bullet.class))
					game.getState().ammo--;

			// Player clone shooting.. this might allow
			// the player to generate more bullets than the limit,
			// but who uses player clones and shot limits??
			for (Element tmp : board.getElementsByType(Player.class)) {
				if (tmp.equals(this))
					continue;

				Player p = (Player) tmp;
				if (p.isActive())
					if (OOPHelpers.shoot(game, board, tmp.getX(), tmp.getY(),
							stepX, stepY, true, Bullet.class))
						game.getState().ammo--;
			}
		} else {
			game.setMessage("No ammo!");
		}
	}

	/**
	 * @param game
	 * @param board
	 * @param e
	 */
	protected void handleMoving(GameController game, Board board, Element e) {
		boolean doMove = true;

		if (e != null) {
			int oldX = x, oldY = y;
			e.message(game, board, Message.TOUCH);

			if (e instanceof Scroll) {
				return;
			}
			if (e instanceof Passage) {
				newBoard(game);
				if (board.elementAt(getX(), getY(), 1) instanceof Passage) {
					board.removeAt(getX(), getY(), board.elementAt(getX(),
							getY(), 1));
					OOPHelpers.putEmpty(board, getX(), getY());
				}
			}

			if (oldX == x && oldY == y) {
				if (e.getInteractionsRules().is(InteractionRule.BLOCKS_MOVE)
						&& !e.getInteractionsRules().is(InteractionRule.ITEM)) {
					doMove = false;
				}
				if (e.getInteractionsRules().is(InteractionRule.ITEM)) {
					takeItem(board, e);
				}
			} else if (oldX != x || oldY != y) {
				doMove = false;// transporters take up current move for the time
				// being. This code will be useless after the movement code is
				// modified to take account for transporters itself.
			}
		}

		if (e != null)
			if (e.getInteractionsRules()
					.is(InteractionRule.NOT_PLAYER_MOVEABLE)
					&& !e.getInteractionsRules().is(InteractionRule.ITEM))
				doMove = false;

		if (doMove) {
			int moveRet = 0;
			if (e instanceof BoardEdge) {
				handleEdgeOfBoard(game, board, OOPHelpers.getDir(stepX, stepY));
			} else {
				// if (e == null
				// || !e.getInteractionsRules()
				// .is(InteractionRule.BLOCKS_MOVE))
				moveRet = OOPHelpers.tryMove(x, y, stepX, stepY, board, this);
			}

			if (moveRet == -1) { // moved
				if (game.isPaused())
					game.setPaused(false);
				lastX = stepX;
				lastY = stepY;
			}
		}

		List<Element> clones = board.getElementsByType(Player.class);
		clones.remove(this);
		for (Element ptmp : clones) {
			Player p = (Player) ptmp;
			if (!p.isActive())
				continue;
			if (board.boundsCheck(p.getX() + stepX, p.getY() + stepY) != -1)
				continue;

			Element at = board.elementAt(p.getX() + stepX, p.getY() + stepY);

			if (at != null)
				at.message(game, board, Message.TOUCH);
			if (at == null
					|| at.getInteractionsRules().is(InteractionRule.FLOOR)
					|| at.getInteractionsRules().is(InteractionRule.ITEM)) {
				if (at != null) {
					if (at.getInteractionsRules().is(InteractionRule.ITEM)) {
						board.removeAt(p.getX() + stepX, p.getY() + stepY, at);
						// OOPHelpers.putEmpty(board, p.getX() + stepX, p.getY()
						// + stepY);
					}
				}
				// move there
				OOPHelpers.moveElementAbs(x, y, p.getX() + stepX, p.getY()
						+ stepY, board, this);
			}
		}

		// OOPHelpers.moveElement(x, y, stepX, stepY, board, this);
	}

	/**
	 * @param board
	 * @param e
	 */
	protected void takeItem(Board board, Element e) {
		board.removeAt(x + stepX, y + stepY, e);
		if (board.floorAt(x + stepX, y + stepY) == null)
			OOPHelpers.putEmpty(board, x + stepX, y + stepY);
	}

	/**
	 * Handles interaction when the player is at the edge of the board (ie
	 * warping to adjacent boards)
	 * 
	 * @param game
	 * @param board
	 * @param dirOff
	 */
	protected void handleEdgeOfBoard(GameController game, Board board,
			int dirOff) {
		int destBoard = 0;
		switch (dirOff) {
		case 0:
			destBoard = board.getState().boardNorth;
			break;
		case 1:
			destBoard = board.getState().boardEast;
			break;
		case 2:
			destBoard = board.getState().boardSouth;
			break;
		case 3:
			destBoard = board.getState().boardWest;
			break;
		}

		if (destBoard != 0) {
			// treat 0 as no board, this is a limitation
			// of the ZZT engine which prevents any
			// board from connecting to the title board
			Board dest = game.getBoardList().get(destBoard);
			int checkX = x, checkY = y;
			switch (dirOff) {
			case 0:
				checkY = dest.getHeight() - 1;
				break;
			case 1:
				checkX = 0;
				break;
			case 2:
				checkY = 0;
				break;
			case 3:
				checkX = dest.getWidth() - 1;
				break;
			}

			if (dest == null) {
				// Missing board; ERROR CASE
				// This represents an error in the loading process

				game
						.reportError("Board ID "
								+ destBoard
								+ " missing.\n\n"
								+ "The board could not be loaded\nor is missing from the world file.");
				return;
			}

			Element blocking = dest.elementAt(checkX, checkY);
			if (blocking != null)
				blocking.message(game, board, Message.TOUCH);
			if (blocking == null
					|| blocking.getInteractionsRules()
							.is(InteractionRule.FLOOR)
					|| blocking.getInteractionsRules().is(InteractionRule.ITEM)
					|| blocking instanceof Player) {
				if (blocking != null
						&& blocking.getInteractionsRules().is(
								InteractionRule.ITEM)) {
					dest.removeAt(checkX, checkY, blocking);
				}
				Player destPlayer = dest.getPlayer();
				OOPHelpers.moveElementAbs(destPlayer.x, destPlayer.y, checkX,
						checkY, dest, destPlayer);
				game.setPaused(false);

				if (game.getMenu() == null) {
					game.setBoard(destBoard);
					newBoard(game);
				} else {
					forceNextX = stepX;
					forceNextY = stepY;
				}
			}
		}
	}

	protected void newBoard(GameController game) {

	}

	/**
	 * @param xStep
	 * @param yStep
	 */
	public void setXYStep(int xStep, int yStep) {
		this.stepX = xStep;
		this.stepY = yStep;
		if (xStep != 0 || yStep != 0) {
			moving = true;
		} else {
			moving = false;
		}
	}

	public int getLastMoveX() {
		return lastX;
	}

	public int getLastMoveY() {
		return lastY;
	}

	/**
	 * @param shiftDown
	 * @param spaceDown
	 */
	public void setShiftDown(boolean shiftDown, boolean spaceDown) {
		this.shiftDown = shiftDown;
		this.spaceDown = spaceDown;
	}

	public void takeDamage(GameController game, Board board, int amount) {
		if (game.getState().ecycles == 0) {
			game.setMessage("Ouch!");
			game.getState().health -= amount;
			if (game.getState().health < 0)
				game.getState().health = 0;
			flashing = true;
		}
	}

	public boolean isClone(Board b) {
		return !equals(b.getPlayer());
	}

}
