/**
 * 
 */
package com.isaacbrodsky.freeze.elements.oop;

import java.util.Random;

import com.isaacbrodsky.freeze.elements.BoardEdge;
import com.isaacbrodsky.freeze.elements.Element;
import com.isaacbrodsky.freeze.elements.Empty;
import com.isaacbrodsky.freeze.elements.Player;
import com.isaacbrodsky.freeze.elements.Star;
import com.isaacbrodsky.freeze.elements.Transporter;
import com.isaacbrodsky.freeze.elements.data.InteractionRule;
import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.elements.data.Stats;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.game.GameState;
import com.isaacbrodsky.freeze.graphics.ElementColoring;

/**
 * @author isaac
 * 
 */
public final class OOPHelpers {
	private OOPHelpers() {
		/*
		 * disable instantiating this class
		 */
	}

	public static String cmdFromArray(String[] arr, int start) {
		StringBuilder sb = new StringBuilder("#");
		if (OOPResolver.isControl(arr[start].charAt(0))) {
			sb.deleteCharAt(0);
		}
		for (int i = start; i < arr.length; i++) {
			sb.append(arr[i]).append(' ');
		}
		return sb.toString().trim();
	}

	/**
	 * Moves an object to a set of relative coordinates, not checking if an
	 * element is blocking it.
	 */
	public static void moveElement(int x, int y, int xS, int yS, Board board,
			Element e) {
		board.removeAt(x, y, e);
		e.setXY(x + xS, y + yS);
		board.putElement(x + xS, y + yS, e);
	}

	/**
	 * Moves an object to a set of absolute coordinates, not checking if an
	 * element is blocking it.
	 */
	public static void moveElementAbs(int oldX, int oldY, int x, int y,
			Board board, Element e) {
		board.removeAt(oldX, oldY, e);
		e.setXY(x, y);
		board.putElement(x, y, e);
	}

	/**
	 * @param game
	 * @param b
	 * @param e
	 * @param s
	 * @return
	 */
	public static int getBooleanFromStringArray(GameController game, Board b,
			Element e, String[] s) {
		return getBooleanFromStringArray(game, b, e, s, 0);
	}

	/**
	 * Returns positive if true, negative if false. The absolute value refers to
	 * the location that the method stopped parsing the input array.
	 * 
	 * <p>
	 * Will never return 0.
	 * 
	 * @param game
	 * @param b
	 * @param e
	 * @param inS
	 * @param start
	 * @return
	 */
	public static int getBooleanFromStringArray(GameController game, Board b,
			Element e, String[] inS, int start) {
		String s = inS[start].toLowerCase().trim();

		// flags TODO i don't know if its checked first or last
		// or if ZZT just falls through on false
		for (int i = 0; i < game.getState().flags.length; i++) {
			if (game.getState().flags[i] != null) {
				if (game.getState().flags[i].equalsIgnoreCase(s))
					return start + 1;
			}
		}

		if (s.equals("alligned")) {// two 'l's
			Player p = b.getPlayer();
			if (p != null && (p.getX() == e.getX() || p.getY() == e.getY()))
				return start + 1;
			else
				return -(start + 1);
		} else if (s.equals("contact")) {
			Player p = b.getPlayer();
			if (p.getX() == e.getX()) {
				if (Math.abs(e.getY() - p.getY()) == 1)
					return start + 1;
				else
					return -(start + 1);
			} else if (p.getY() == e.getY()) {
				if (Math.abs(e.getX() - p.getX()) == 1)
					return start + 1;
				else
					return -(start + 1);
			}
			return -(start + 1);
		} else if (s.equals("energized")) {
			if (game.getState().ecycles > 0)
				return (start + 1);
			else
				return -(start + 1);
		}

		if (s.equals("not")) {
			int ret = getBooleanFromStringArray(game, b, e, inS, start + 1);
			// if (ret > 0)
			// ret++;
			// else
			// ret--;
			return -ret;
		} else if (s.equals("any")) {
			int color = ElementColoring.codeFromName(inS[start + 1]);
			if (color == -1) {
				start--;
			}
			start++;
			String type = inS[start + 1];
			start++;

			if (color == -1) {
				// TODO dosn't work: clockwise/ccw, sliderns/sliderew
				if (b.getElementsByType(
						game.getElementResolver().classFromName(type)).size() > 0)
					return start + 1;
				else
					return -(start + 1);
			} else {
				for (Element iterE : b.getElementsByType(game
						.getElementResolver().classFromName(type))) {
					if (iterE.getColoring().getForeCode() == color)
						return (start + 1);
				}
				return -(start + 1);
			}
			// TODO
		} else if (s.equals("blocked")) {
			int dir = getDirFromStringArray(game, b, e, inS, start + 1);
			Element blocker = new BoardEdge();
			if (b.boundsCheck(e.getX() + getDirX(dir), e.getY() + getDirY(dir)) == -1) {
				blocker = b.elementAt(e.getX() + getDirX(dir), e.getY()
						+ getDirY(dir));
			}
			int sOffset = 2; // blocked + direction
			while (inS[sOffset].equals("cw") || inS[sOffset].equals("ccw")
					|| inS[sOffset].equals("opp")
					|| inS[sOffset].equals("rndp"))
				sOffset++;
			if (blocker == null
					|| blocker.getInteractionsRules().is(InteractionRule.FLOOR))
				return -(start + sOffset);
			else
				return start + sOffset;
		}

		return -(start + 1);
	}

	/**
	 * @param board
	 * @param e
	 * @param s
	 * @return
	 */
	public static int getDirFromStringArray(GameController game, Board board,
			Element e, String[] s) {
		return getDirFromStringArray(game, board, e, s, 0);
	}

	/**
	 * @param board
	 * @param e
	 * @param inS
	 * @param start
	 * @return
	 */
	public static int getDirFromStringArray(GameController game, Board board,
			Element e, String[] inS, int start) {
		String s = inS[start].toLowerCase().trim();
		if (s.equals("n") || s.equals("north"))
			return getDir(0, -1);
		else if (s.equals("s") || s.equals("south"))
			return getDir(0, 1);
		else if (s.equals("e") || s.equals("east"))
			return getDir(1, 0);
		else if (s.equals("w") || s.equals("west"))
			return getDir(-1, 0);
		else if (s.equals("i") || s.equals("idle"))
			return -1;
		else if (s.equals("seek")) {
			if (board.getPlayer() == null)
				// throw new IllegalArgumentException(
				// "Not a legal direction; no player");
				return getDirFromStringArray(game, board, e,
						new String[] { "rnd" }, 0);

			int pX = board.getPlayer().getX(); // player loc
			int pY = board.getPlayer().getY();
			int eX = e.getX();// element loc
			int eY = e.getY();
			if (game.getState().ecycles > 0) {
				pX = -pX;
				pY = -pY;
			}

			Random r = new Random();

			if (pX == eX)
				return getDir(0, (pY > eY) ? 1 : -1);
			if (pY == eY)
				return getDir((pX > eX) ? 1 : -1, 0);

			if (r.nextBoolean())
				return getDir((pX > eX) ? 1 : -1, 0);
			else
				return getDir(0, (pY > eY) ? 1 : -1);
		} else if (s.equals("flow")) {
			// if (e instanceof ZObject) {
			// ZObject zo = (ZObject) e;
			// return getDir(zo.getWalkX(), zo.getWalkY());
			// } else {
			if (e.getStats().getStepX() == 0 && e.getStats().getStepY() == 0)
				return getDir(-1, 0);
			return getDir(e.getStats().getStepX(), e.getStats().getStepY());
			// }
		} else if (s.equals("rndns")) {
			Random r = new Random();
			if (r.nextBoolean())
				return getDir(0, 1);
			else
				return getDir(0, -1);
		} else if (s.equals("rndne")) {
			Random r = new Random();
			if (r.nextBoolean())
				return getDir(0, 1);
			else
				return getDir(1, 0);
		} else if (s.equals("rnd")) {
			// This weighting from Mystic Winds encyc.
			Random r = new Random();
			if (r.nextInt(3) <= 1) { // X change is more likely
				if (r.nextBoolean())
					return getDir(1, 0);
				else
					return getDir(-1, 0);
			} else {
				if (r.nextBoolean())
					return getDir(0, 1);
				else
					return getDir(0, -1);
			}
		}
		// MULTIPLE PARTS
		int dir = getDirFromStringArray(game, board, e, inS, start + 1);
		int x = getDirX(dir);
		int y = getDirY(dir);

		if (s.equals("cw")) {
			return getClockwise(dir);
		} else if (s.equals("ccw")) {
			return getCounterclockwise(dir);
		} else if (s.equals("opp")) {
			return getDir(-x, -y);
		} else if (s.equals("rndp")) {
			Random r = new Random();
			if (r.nextBoolean()) {
				return getClockwise(dir);
			} else {
				return getCounterclockwise(dir);
			}
		}

		throw new IllegalArgumentException("Not a legal direction: " + s);
	}

	/**
	 * Returns the given direction adjusted counterclockwise.
	 * 
	 * <p>
	 * Passing an invalid direction is undefined.
	 * 
	 * @param dir
	 * @return
	 */
	public static int getCounterclockwise(int dir) {
		int x = getDirX(dir);
		int y = getDirY(dir);
		return getDir(y, -x);
	}

	/**
	 * Returns the given direction adjusted clockwise.
	 * 
	 * <p>
	 * Passing an invalid direction is undefined.
	 * 
	 * @param dir
	 * @return
	 */
	public static int getClockwise(int dir) {
		int x = getDirX(dir);
		int y = getDirY(dir);
		return getDir(-y, x);
	}

	/**
	 * @param s
	 * @return
	 **/
	/*
	 * public static int getDirFromString(Board board, ZObject e, String s) { s
	 * = s.toLowerCase(); if (s.equals("n") || s.equals("north")) return
	 * getDir(0, -1); else if (s.equals("s") || s.equals("south")) return
	 * getDir(0, 1); else if (s.equals("e") || s.equals("east")) return
	 * getDir(1, 0); else if (s.equals("w") || s.equals("west")) return
	 * getDir(-1, 0); else if (s.equals("i") || s.equals("idle")) return -1;
	 * else if (s.equals("seek")) { if (board.getPlayer() == null) throw new
	 * IllegalArgumentException( "Not a legal direction; no player");
	 * 
	 * int pX = board.getPlayer().getX(); // player loc int pY =
	 * board.getPlayer().getY(); int eX = e.getX();// element loc int eY =
	 * e.getY();
	 * 
	 * Random r = new Random();
	 * 
	 * if (pX == eX) return getDir(0, (pY > eY) ? 1 : -1); if (pY == eY) return
	 * getDir((pX > eX) ? 1 : -1, 0);
	 * 
	 * if (r.nextBoolean()) return getDir((pX > eX) ? 1 : -1, 0); else return
	 * getDir(0, (pY > eY) ? 1 : -1); } else if (s.equals("flow")) {
	 * 
	 * } else if (s.equals("rndns")) { Random r = new Random(); if
	 * (r.nextBoolean()) return getDir(0, 1); else return getDir(0, -1); } else
	 * if (s.equals("rndne")) { Random r = new Random(); if (r.nextBoolean())
	 * return getDir(0, 1); else return getDir(1, 0); } else if
	 * (s.equals("rnd")) { // This weighting from Mystic Winds encyc. Random r =
	 * new Random(); if (r.nextInt(3) <= 1) { // X change is more likely if
	 * (r.nextBoolean()) return getDir(1, 0); else return getDir(-1, 0); } else
	 * { if (r.nextBoolean()) return getDir(0, 1); else return getDir(0, -1); }
	 * } // MULTIPLE PARTS else if (s.equals("cw")) {
	 * 
	 * } else if (s.equals("ccw")) {
	 * 
	 * } else if (s.equals("opp")) {
	 * 
	 * } else if (s.equals("rndp")) {
	 * 
	 * }
	 * 
	 * throw new IllegalArgumentException("Not a legal direction: " + s); }
	 */

	/**
	 * Encodes a change of 1 or -1 in either (not both) parameters into a single
	 * int. This int can be decoded with getDirX and getDirY into its
	 * components. Returns -1 on error.
	 * 
	 * <p>
	 * Coding (implementation detail- do not rely on this)<br>
	 * 0 = NORTH <br>
	 * 1 = EAST <br>
	 * 2 = SOUTH <br>
	 * 3 = WEST
	 * 
	 * @param xS
	 * @param yS
	 * @return
	 */
	public static int getDir(int xS, int yS) {
		if (xS > 1 || xS < -1 || yS > 1 || yS < -1)
			return -1;
		if (xS != 0 && yS != 0)
			return -1;

		if (yS == -1)
			return 0;
		if (xS == 1)
			return 1;
		if (yS == 1)
			return 2;
		if (xS == -1)
			return 3;

		return -1;
	}

	/**
	 * Returns the reverse of the direction returned by a dir obtained from
	 * getDir(int)
	 * 
	 * @param dir
	 * @return
	 */
	public static int reverseDir(int dir) {
		if (dir == 0)
			return 2;
		else if (dir == 1)
			return 3;
		else if (dir == 2)
			return 0;
		else if (dir == 3)
			return 1;

		return -1;
	}

	/**
	 * @param dir
	 * @return
	 */
	public static int getDirX(int dir) {
		if (dir == 1)
			return 1;
		else if (dir == 3)
			return -1;
		return 0;
	}

	/**
	 * @param dir
	 * @return
	 */
	public static int getDirY(int dir) {
		if (dir == 0)
			return -1;
		else if (dir == 2)
			return 1;
		return 0;
	}

	/**
	 * Modifies the given {@link GameController}'s {@link GameState} object
	 * based off the given string.
	 * 
	 * <p>
	 * This method expects lower-case Strings, "AMMO" and "ammo" are not the
	 * same, only the latter is valid. If an invalid type string is given, an
	 * exception is thrown.
	 * 
	 * <p>
	 * Accepts negative values for howMany. If the value is negative and the
	 * player does not have enough of whatever the type is, the counter is not
	 * modified and false is returned, otherwise returns true.
	 * 
	 * @param game
	 * @param what
	 * @param howMany
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static boolean handleGiveTake(GameController game, String what,
			int howMany) throws IllegalArgumentException {
		if (what.equals("ammo")) {
			if (game.getState().ammo + howMany < 0)
				return false;
			game.getState().ammo += howMany;
		} else if (what.equals("gems")) {
			if (game.getState().gems + howMany < 0)
				return false;
			game.getState().gems += howMany;
		} else if (what.equals("score")) {
			if (game.getState().score + howMany < 0)
				return false;
			game.getState().score += howMany;
		} else if (what.equals("health")) {
			if (game.getState().health + howMany < 0)
				return false;
			game.getState().health += howMany;
		} else if (what.equals("torches")) {
			if (game.getState().torches + howMany < 0)
				return false;
			game.getState().torches += howMany;
		} else if (what.equals("time")) {
			if (game.getState().timePassed + howMany < 0)
				return false;
			game.getState().timePassed += howMany;
		} else if (what.equals("z")) {
			if (game.getState().stones < 0)
				game.getState().stones = 0;
			if (game.getState().stones + howMany < 0)
				return false;
			game.getState().stones += howMany;
		} else {
			// Invalid type handler
			throw new IllegalArgumentException("Bad type.");
		}

		// Fall through, not reached if an invalid type
		return true;
	}

	/**
	 * @param e
	 * @param xS
	 * @param yS
	 * @return
	 */
	public static boolean isMoveable(Element e, int xS, int yS) {
		if (xS != 0 && e.getInteractionsRules().is(InteractionRule.MOVEABLE_EW))
			return true;
		if (yS != 0 && e.getInteractionsRules().is(InteractionRule.MOVEABLE_NS))
			return true;

		return false;
	}

	/**
	 * @param game
	 * @param board
	 * @param x
	 * @param y
	 * @param xS
	 * @param yS
	 * @param playerFired
	 * @param what
	 * @return true if a bullet was fired, false otherwise. True does not
	 *         necessarily mean that a Bullet element not exists on the board.
	 * @throws IllegalArgumentException
	 *             on any issue relating to instantiating the type passed in as
	 *             "what"
	 */
	public static boolean shoot(GameController game, Board board, int x, int y,
			int xS, int yS, boolean playerFired, Class<? extends Element> what)
			throws IllegalArgumentException {
		if (xS == 0 && yS == 0)
			return false; // ???? don't shoot yourself
		if (board.boundsCheck(x + xS, y + yS) != -1)
			return false;// can't shoot off board

		Element e = board.elementAt(x + xS, y + yS);
		if (e != null) {
			if (e.getInteractionsRules().is(InteractionRule.SHOOTOVER)) {
				// continue
			} else if (e.getInteractionsRules().is(
					InteractionRule.POINT_BLANK_SHOOTABLE)) {
				if (!e.getInteractionsRules().is(
						InteractionRule.ONLY_PLAYER_SHOOTABLE)
						|| playerFired)
					e.message(game, board, Message.SHOT);
				return true;
			} else if (!e.getInteractionsRules().is(InteractionRule.FLOOR)) {
				return false;
			}
		}

		Element bullet = null;
		try {
			bullet = (Element) what.newInstance();
		} catch (Exception e1) {
			throw new IllegalArgumentException(
					"Could not instantiate your type", e1);
		}
		bullet.createInstance(new SaveData(what.equals(Star.class) ? 0x0F : 18,
				0x0F));
		bullet.setStats(new Stats(x + xS, y + yS, playerFired ? 0 : 1, what.equals(Star.class) ? 100 : 0, 0,
				xS, yS, 1, -1, -1, -1, -1, ""));
		bullet.setXY(x + xS, y + yS);
		board.putElement(x + xS, y + yS, bullet);
		bullet.tick(game, board);
		return true;
	}

	/**
	 * @param x
	 * @param y
	 * @param xS
	 * @param yS
	 * @param board
	 * @param movingElement
	 * @return >= 0 hit a boundary <br>
	 *         -1 OK <br>
	 *         -2 blocked
	 */
	public static int tryMove(int x, int y, int xS, int yS, Board board,
			Element movingElement) {
		return tryMove(x, y, xS, yS, board, movingElement, true);
	}

	/**
	 * @param x
	 * @param y
	 * @param xS
	 * @param yS
	 * @param board
	 * @param movingElement
	 * @param pushStuff
	 * @return >= 0 hit a boundary <br>
	 *         -1 OK <br>
	 *         -2 blocked
	 */
	public static int tryMove(int x, int y, int xS, int yS, Board board,
			Element movingElement, boolean pushStuff) {
		return tryMove(x, y, xS, yS, board, movingElement, pushStuff, true);
	}

	/**
	 * @param x
	 * @param y
	 * @param xS
	 * @param yS
	 * @param board
	 * @param movingElement
	 * @param pushStuff
	 * @param reallyMove
	 * @return >= 0 hit a boundary <br>
	 *         -1 OK <br>
	 *         -2 blocked
	 */
	public static int tryMove(int x, int y, int xS, int yS, Board board,
			Element movingElement, boolean pushStuff, boolean reallyMove) {
		if (board.boundsCheck(x + xS, y + yS) != -1) {
			return board.boundsCheck(x + xS, y + yS);
		}

		boolean doMove = true;

		Element blocking = board.elementAt(x + xS, y + yS);
		if (blocking != null) {
			if (!blocking.getInteractionsRules().is(InteractionRule.FLOOR)) {
				if (isMoveable(blocking, xS, yS) && pushStuff) {
					int ret = tryMove(x + xS, y + yS, xS, yS, board, blocking);
					if (ret != -1) // on failure
						doMove = false;

					if (blocking.getInteractionsRules().is(
							InteractionRule.ONLY_PLAYER_SHOOTABLE)) {
						// kill!.. I mean crush.

						if (board.boundsCheck(x + xS + xS, y + yS + yS) == -1) {
							Element crushLookAhead = board.elementAt(x + xS
									+ xS, y + yS + yS);

							if (crushLookAhead != null
									&& crushLookAhead
											.getInteractionsRules()
											.is(
													InteractionRule.ONLY_PLAYER_SHOOTABLE)) {
								board.removeAt(x + xS, y + yS, blocking);
								return tryMove(x, y, xS, yS, board,
										movingElement, pushStuff);
							}
						}

						doMove = false;
					}
				} else if (blocking instanceof Transporter && pushStuff) {
					if (reallyMove
							&& !movingElement.getInteractionsRules().is(
									InteractionRule.NO_TRANSPORTER)) {
						((Transporter) blocking).teleportElement(board,
								movingElement);
						if (movingElement.getX() != x
								|| movingElement.getY() != y) {
							return -1;
						} else {
							doMove = false;
						}
					} else {
						doMove = false;
					}
				} else {
					doMove = false;
				}
			}
		}

		if (doMove) {
			if (reallyMove) {
				OOPHelpers.moveElement(x, y, xS, yS, board, movingElement);
			}
			return -1;
		} else {
			return -2;
		}
	}

	public static void putEmpty(Board board, int x, int y) {
		Element e = new Empty();
		e.createInstance(new SaveData(0x00, /* 0x07 */15));
		e.setXY(x, y);
		board.putElement(x, y, e);
	}
}
