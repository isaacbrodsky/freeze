/**
 * 
 */
package com.isaacbrodsky.freeze;

import com.isaacbrodsky.freeze.elements.Element;
import com.isaacbrodsky.freeze.elements.Invisible;
import com.isaacbrodsky.freeze.elements.Player;
import com.isaacbrodsky.freeze.elements.ZObject;
import com.isaacbrodsky.freeze.elements.data.InteractionRule;
import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.elements.data.Stats;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.elements.oop.OOPHelpers;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.BoardState;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.game.GameState;
import com.isaacbrodsky.freeze.menus.EditableMenu;
import com.isaacbrodsky.freeze.menus.Menu;
import com.isaacbrodsky.freeze.menus.MenuCallback;
import com.isaacbrodsky.freeze.menus.UIInteraction;
import com.isaacbrodsky.freeze.menus.Menu.SendMode;

/**
 * Static utility class with methods implementing both Freeze-specific cheats
 * and standard Super/ZZT cheats.
 * 
 * @author isaac
 */
public final class Cheats {

	private Cheats() {

	}

	/**
	 * Accepts a String, as one entered into the ZZT ? prompt, and parses it for
	 * known cheats. If one is found, the effect is applied to the given
	 * GameController.
	 * 
	 * <p>
	 * ZZT cheats are case insensitive, but this method requires lower case.
	 * 
	 * <p>
	 * List of cheats:
	 * <table>
	 * <tr>
	 * <th>Code</th>
	 * <th>Effect</th>
	 * <th>Engine</th>
	 * </tr>
	 * <tr>
	 * <td>health</td>
	 * <td>+50 Health, even if "dead" (0 health)</td>
	 * <td>All</td>
	 * </tr>
	 * <tr>
	 * <td>ammo</td>
	 * <td>+5 Ammo</td>
	 * <td></td>All
	 * </tr>
	 * <tr>
	 * <td>gems</td>
	 * <td>+5 Gems</td>
	 * <td>All</td>
	 * </tr>
	 * <tr>
	 * <td>torches</td>
	 * <td>+5 Torches</td>
	 * <td>ZZT</td>
	 * </tr>
	 * <tr>
	 * <td>zap</td>
	 * <td>See {@link #zap(GameController)}</td>
	 * <td>All</td>
	 * </tr>
	 * <tr>
	 * <td>keys</td>
	 * <td>Sets all keys (not the "black key")</td>
	 * <td>All</td>
	 * </tr>
	 * <tr>
	 * <td>z</td>
	 * <td>+1 Stone of power</td>
	 * <td>SuperZZT</td>
	 * </tr>
	 * <tr>
	 * <td>noz</td>
	 * <td>-1 Stone of power</td>
	 * <td>SuperZZT</td>
	 * </tr>
	 * <tr>
	 * <td>+[User input]</td>
	 * <td>Sets the flag name given, minus the +.</td>
	 * <td>All</td>
	 * </tr>
	 * <tr>
	 * <td>-[User input]</td>
	 * <td>Unsets the flag name given, minus the -.</td>
	 * <td>All</td>
	 * </tr>
	 * <tr>
	 * <td>-DARK</td>
	 * <td>As above for -, but also unsets the dark flag on the current board</td>
	 * <td>ZZT</td>
	 * </tr>
	 * <tr>
	 * <td>+DARK</td>
	 * <td>As above for +, but also sets the dark flag on the current board</td>
	 * <td>ZZT</td>
	 * </tr>
	 * </table>
	 * 
	 * @param c
	 * @param game
	 */
	public static void handleStandardCheats(String c, GameController game) {
		if (c.equals("health")) {
			game.getState().health += 50;
		} else if (c.equals("ammo")) {
			game.getState().ammo += 5;
		} else if (c.equals("gems")) {
			game.getState().gems += 5;
		} else if (c.equals("torches")) {
			game.getState().torches += 5;
		} else if (c.equals("zap")) {
			Cheats.zap(game);
		} else if (c.equals("keys")) {
			for (int i = 0; i < game.getState().keys.length; i++) {
				game.getState().keys[i] = 1;
			}
		} else if (c.equals("time")) {
			game.getState().timePassed -= 30;
		} else if (c.equals("z")) {
			game.getState().stones++;
		} else if (c.equals("noz")) {
			game.getState().stones--;
		} else if (c.startsWith("+")) {
			for (int i = 0; i < game.getState().flags.length; i++) {
				if (game.getState().flags[i] == null) {
					game.getState().flags[i] = c.substring(1);
					break;
				}
			}
			if (c.substring(1).equals("dark")) {
				game.getBoard().getState().dark = 1;
			}
		} else if (c.startsWith("-")) {
			for (int i = 0; i < game.getState().flags.length; i++) {
				if (game.getState().flags[i] != null) {
					if (game.getState().flags[i].equalsIgnoreCase(c
							.substring(1))) {
						game.getState().flags[i] = null;
						break;
					}
				}
			}
			if (c.substring(1).equalsIgnoreCase("dark")) {
				game.getBoard().getState().dark = 0;
			}
		}
	}

	/**
	 * Implements the ZZT ?ZAP cheat, which deletes the four cells surrounding
	 * the player. This version of the cheat is faithful to the original, but as
	 * an engine constraint cannot be used to exit the playing area.
	 * 
	 * <p>
	 * The ?ZAP effect is applied to the current board in the given
	 * GameController.
	 * 
	 * @param game
	 */
	public static void zap(GameController game) {
		Player p = game.getPlayer();
		Board b = game.getBoard();
		int x = p.getX();
		int y = p.getY();
		try {
			b.removeAt(x - 1, y);
			b.removeAt(x - 1, y);
			OOPHelpers.putEmpty(b, x - 1, y);
		} catch (Exception e) {

		}
		try {
			b.removeAt(x + 1, y);
			b.removeAt(x + 1, y);
			OOPHelpers.putEmpty(b, x + 1, y);
		} catch (Exception e) {

		}
		try {
			b.removeAt(x, y - 1);
			b.removeAt(x, y - 1);
			OOPHelpers.putEmpty(b, x, y - 1);
		} catch (Exception e) {

		}
		try {
			b.removeAt(x, y + 1);
			b.removeAt(x, y + 1);
			OOPHelpers.putEmpty(b, x, y + 1);
		} catch (Exception e) {

		}
	}

	/**
	 * Handles additional Freeze cheats. Cheats are case insensitive
	 * but the first argument to this function must be lower case.
	 * 
	 * <p>
	 * All cheats are applicable to all games.
	 * 
	 * <p>
	 * Additional cheats are implementation details and subject to change.
	 * 
	 * <table>
	 * <tr>
	 * <th>Code</th>
	 * <th>Effect</th>
	 * </tr>
	 * <tr>
	 * <td>nowisee</td>
	 * <td>Reveals all invisible walls on the current board.</td>
	 * </tr>
	 * <tr>
	 * <td>atm</td>
	 * <td>Gives the player a large bonus to most counters.</td>
	 * </tr>
	 * <tr>
	 * <td>time</td>
	 * <td>(This might have been a ZZT cheat, I forget) Gives the player more
	 * time on the current board</td>
	 * </tr>
	 * <tr>
	 * <td>timelimit</td>
	 * <td>Deletes the time limit on the current board, or sets it to the limit
	 * given after a space.</td>
	 * </tr>
	 * <tr>
	 * <td>reenter</td>
	 * <td>Toggles the reenter flag</td>
	 * </tr>
	 * <tr>
	 * <td>shots</td>
	 * <td>Toggles if the player can shoot all or no bullets; can be used to
	 * disable a shot limit</td>
	 * </tr>
	 * <tr>
	 * <td>unpause</td>
	 * <td>Unpauses the game without player movement</td>
	 * </tr>
	 * <tr>
	 * <td>*</td>
	 * <td>Executes the text after the * as OOP at the same location as the
	 * player.</td>
	 * </tr>
	 * <tr>
	 * <td>cinnamon</td>
	 * <td>Returns a menu to enter a program to be executed as with the * cheat,
	 * but easier input for multiline programs.</td>
	 * </tr>
	 * </table>
	 * 
	 * @param c
	 * @param orig
	 * @param game
	 * @throws NumberFormatException
	 */
	public static UIInteraction handleAdditionalCheats(String c, String orig,
			final GameController game) throws NumberFormatException {
		if (c.equals("nowisee")) {
			for (Element inv : game.getBoard().getElementList()) {
				if (inv instanceof Invisible) {
					inv.message(game, game.getBoard(), Message.TOUCH);
					game.getBoard().setMessage("nanolathing");
				}
			}
		} else if (c.equals("atm")) {
			game.getState().health += 500;
			game.getState().ammo += 500;
			game.getState().torches += 500;
			game.getState().gems += 256;
			game.getState().stones += 500;
		} else if (c.startsWith("timelimit")) {
			if (c.split(" ").length > 1) {
				game.getBoard().getState().timeLimit = Integer.valueOf(c
						.split(" ")[1]);
			} else {
				game.getBoard().getState().timeLimit = 0;
			}
		} else if (c.equals("reenter")) {
			game.getBoard().getState().restart = (game.getBoard().getState().restart == 0) ? 1
					: 0;
		} else if (c.equals("shots")) {
			game.getBoard().getState().shots = (game.getBoard().getState().shots == 0) ? 255
					: 0;
		} else if (c.equals("unpause")) {
			game.setPaused(false);
		} else if (c.startsWith("*")) {
			spawnDebugObject(game, orig.substring(1));
		} else if (c.equals("cinnamon")) {
			return new EditableMenu("Your program", "",
					new MenuCallback() {

						@Override
						public void menuCommand(String cmd, Object rider) {
							if (cmd != null)
								spawnDebugObject(game, cmd);
						}

					}, null);
		}
		return null;
	}

	/**
	 * Creates a ZObject at the same location as the player and has it begin
	 * executing.
	 * 
	 * <p>
	 * The ZObject is not actually on the board, so it will fall out of scope
	 * after this function unless it moves.
	 * 
	 * @param game
	 * @param oop
	 */
	private static void spawnDebugObject(GameController game, String oop) {
		// why use the player's color and char?
		// - why not!
		ZObject temp = new ZObject();
		temp.createInstance(new SaveData(0x24, game.getPlayer().getColoring()
				.getCode()));
		temp.setStats(new Stats(game.getPlayer().getX(), game.getPlayer()
				.getY(), game.getPlayer().getDisplayCharacter(), 0, 0, 0, 0, 1,
				0, 0, 0, oop.length(), oop));
		temp.setXY(game.getPlayer().getX(), game.getPlayer().getY());
		temp.tick(game, game.getBoard());
	}

	/**
	 * Constructs a menu detailing the object at the given location on the given
	 * board.
	 * 
	 * @param board
	 * @param x
	 * @param y
	 */
	public static Menu inspectElement(Board board, int x, int y) {
		if (board.boundsCheck(x, y) == -1) {
			Element insE = board.elementAt(x, y, 0);
			if (insE == null)
				insE = board.floorAt(x, y);
			if (insE != null) {
				StringBuilder msg = new StringBuilder("$").append(insE
						.getClass().getName());
				msg.append("\r\nType ID: ")
						.append(insE.getSaveData().getType());
				msg.append(" (Color: ").append(insE.getSaveData().getColor())
						.append(")");
				msg.append("\r\nCycle: ").append(insE.getCycle());
				msg.append("\r\nChar: ").append(insE.getDisplayCharacter());
				msg.append("\r\nColor: ").append(insE.getColoring().toString())
						.append(" (").append(insE.getColoring().getCode())
						.append(")");
				msg.append("\r\nInteraction rules: ");
				for (InteractionRule inter : insE.getInteractionsRules()) {
					msg.append("\r\n    ").append(inter);
				}
				msg.append("\r\nX/Y: ").append(insE.getX()).append("/").append(
						insE.getY());
				msg.append("\r\n\r\n");
				Element underE = board.elementAt(x, y, 1);
				if (underE == null)
					underE = board.floorAt(x, y);
				if (underE != insE && underE != null) {
					msg.append("$Under\r\n");
					msg.append(underE.getClass().getName());
					msg.append("\r\nUnder Type ID: ").append(
							underE.getSaveData().getType());
					msg.append("\r\nUnder Color: ")
							.append(underE.getColoring()).append(" (").append(
									underE.getColoring().getCode()).append(")");
					msg.append("\r\n\r\n");
				}

				if (insE.getStats() != null) {
					msg.append("$Stats\r\n");
					msg.append(insE.getStats().toString());
				} else {
					msg.append("$No stats");
				}
				// if (insE instanceof ZObject) {
				// ZObject insObj = (ZObject) insE;
				// msg.append("\r\nName: ").append(insObj.getName());
				// msg.append("\r\nOOP:\r\n");
				// msg.append(insObj.getOOP());
				// }
				return new Menu("Element inspector: "
						+ insE.getClass().getSimpleName(), msg.toString(),
						null, SendMode.NO, true);
			} else {
				return new Menu("Element inspector: null", "$null\r\nNothing!",
						null, SendMode.NO, true);
			}
		} else {
			return new Menu("Can't.", "Coordinates out of bounds", null,
					SendMode.NO, true);
		}
		// return null;
	}

	/**
	 * Creates a menu detailing the given board.
	 * 
	 * @param board
	 * @param id
	 * @return
	 */
	public static UIInteraction inspectBoard(Board board, int id) {
		BoardState s = board.getState();
		StringBuilder msg = new StringBuilder("$").append(board.getClass()
				.getName());
		msg.append("\r\nID: ").append(id);
		msg.append("\r\nDimensions: ").append(board.getWidth()).append("x")
				.append(board.getHeight()).append("x").append(board.getDepth());
		// msg.append("\r\nBoard N/S/E/W: ").append(s.boardNorth).append(" ")
		// .append(s.boardSouth).append(" ").append(s.boardEast).append(
		// " ").append(s.boardWest);
		// msg.append("\r\nEnter X/Y: ").append(s.enterX).append(" ").append(
		// s.enterY);
		// msg.append("\r\nDark/Restart/Shots: ").append(s.dark).append(" ")
		// .append(s.restart).append(" ").append(s.shots);
		// msg.append("\r\nTime Limit: ").append(s.timeLimit);
		// msg.append("\r\nMessage:").append("\r\n").append(s.message);
		msg.append("\r\n").append(s.toString().replaceAll("\t", "    "));

		return new Menu("Board inspector: " + board.getState().boardName, msg
				.toString(), null, SendMode.NO, true);
	}

	/**
	 * Creates a menu detailing the given game.
	 * 
	 * @param game
	 * @return
	 */
	public static UIInteraction inspectGame(GameController game) {
		GameState s = game.getState();
		StringBuilder msg = new StringBuilder("$").append(game.getClass()
				.getName());
		msg.append("\r\n").append(s.toString().replaceAll("\t", "    "));

		return new Menu("Game inspector: " + s.gameName, msg.toString(), null,
				SendMode.NO, true);
	}
}
