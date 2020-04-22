/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import java.util.ArrayList;
import java.util.List;

import com.isaacbrodsky.freeze.elements.data.InteractionRule;
import com.isaacbrodsky.freeze.elements.data.InteractionRulesSet;
import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.elements.data.Stats;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.elements.oop.OOPHelpers;
import com.isaacbrodsky.freeze.elements.oop.OOPInstruction;
import com.isaacbrodsky.freeze.elements.oop.OOPResolver;
import com.isaacbrodsky.freeze.elements.superz.Web;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.graphics.ElementColoring;
import com.isaacbrodsky.freeze.menus.MenuCallback;

/**
 * A Super/ZZT object. A programmable element.
 * <p>
 * 
 * This class is a bit of a mess.
 * <p>
 * 
 * Named ZObject to differentiate from Java's <code>Object</code> built-in.
 * 
 * @author isaac
 */
public class ZObject extends AbstractElement implements Element, MenuCallback {
	private static final int RECURSION_LIMIT = 16;

	private SaveData dat;
	private ElementColoring color;

	private boolean walkThud;

	private int lastInstr;

	private String name;

	private String menuBuildStr;

	private int recursion;

	public ZObject() {
		color = new ElementColoring(0x02, ElementColoring.ColorMode.DOMINANT);
		walkThud = false;

		dat = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#createInstance(int, int)
	 */
	@Override
	public void createInstance(SaveData dat) {
		this.dat = dat;
		this.color = new ElementColoring(dat.getColor(),
				ElementColoring.ColorMode.CODOMINANT);

		name = getClass() == ZObject.class ? "Interaction" : "Scroll";

		lastInstr = 0;
	}

	public SaveData getSaveData() {
		return dat;
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
	 * @see com.isaacbrodsky.freeze.elements.Element#getDisplayCharacter()
	 */
	@Override
	public int getDisplayCharacter() {
		if (getStats() != null)
			return getStats().p1;
		else
			return 0; // As in ZZT
	}

	public String getName() {
		return name;
	}

	private static final InteractionRulesSet _IRS = new InteractionRulesSet(); // blank

	// set

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#getInteractionsRules()
	 */
	@Override
	public InteractionRulesSet getInteractionsRules() {
		return _IRS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#loadStats(int, int, int,
	 * int, int, int, int, int, int, java.lang.String)
	 */
	@Override
	public void setStats(Stats s) {
		super.setStats(s);

		resetName();
	}

	/**
	 * Resets this object's name from its OOP
	 */
	private void resetName() {
		if (getStats() == null) {
			name = null;
			return;
		}

		OOPInstruction o = getNextInstr(getStats().getOop(), 0);
		if (o == null)
			return;

		if (o.getData().startsWith("@"))
			name = o.getData().substring(1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.menus.MenuCallback#menuCommand(java.lang.String
	 * )
	 */
	@Override
	public void menuCommand(String cmd, Object rider) {
		if (rider instanceof ObjectMenuRider) {
			ObjectMenuRider omr = (ObjectMenuRider) rider;
			int lockedTmp = getStats().p2;
			if (omr.origin.equals(this))
				getStats().p2 = 0;
			message(omr.game, omr.board, new Message(cmd));
			getStats().p2 = lockedTmp;
		} else {
			throw new IllegalArgumentException(
					"Requires an ObjectMenuRider to be passed in");
		}
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
		if (getStats() == null)
			return;

		if (getStats().p2 != 0)
			return;

		int searchIndex = 0;
		while (true) {
			OOPInstruction o = getNextInstr(getStats().getOop(), searchIndex);
			if (o == null)
				break;

			searchIndex = o.getEndLoc();

			if (OOPResolver.isLabel(o)) {
				String lbl = OOPResolver.labelText(o);

				if (lbl.equalsIgnoreCase(msg.getMsg())) {
					getStats().currInstr = o.getLoc();
					return;
				}
			}
		}

		if (msg.getMsg().equalsIgnoreCase("restart"))
			getStats().currInstr = 0;
	}

	/**
	 * @param i
	 * @return
	 */
	private static OOPInstruction getNextInstr(String oop, int i) {
		OOPInstruction o = null;

		if (oop.length() <= i)
			return null;

		char c = oop.charAt(i);
		switch (c) {
		case '@':
		case '#':
		case ':':
		case '\'':
			String s = OOPResolver.readLineAt(oop, i);
			o = new OOPInstruction(i, i + s.length() + 1, s);
			break;
		case '/':
		case '?':
			String s2 = c + OOPResolver.readToControl(oop, i + 1);
			o = new OOPInstruction(i, i + s2.length(), s2);
			break;

		default: // text, read to next element
			if (i == 0 || oop.charAt(i - 1) == '\n'
					|| oop.charAt(i - 1) == '\r' || c == '\r') {
				String s3 = OOPResolver.readText(oop, i);
				s3 = s3.substring(0, s3.length() - 1);
				o = new OOPInstruction(i, i + s3.length() + 1, s3);
			} else {
				// inline command
				String s4 = "#" + OOPResolver.readLineAt(oop, i);
				o = new OOPInstruction(i, i + s4.length(), s4);
			}
			break;
		}

		return o;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.isaacbrodsky.freeze.elements.Element#tick(com.isaacbrodsky.freeze
	 * .game.GameController, com.isaacbrodsky.freeze.game.Board)
	 */
	@Override
	public void tick(GameController game, Board board) {
		handleWalk(game, board);

		boolean goAgain = true;
		menuBuildStr = "";
		recursion = 0;

		while (goAgain && getStats().currInstr >= 0) {
			OOPInstruction o = getNextInstr(getStats().getOop(),
					getStats().currInstr);
			if (o == null)
				break;

			lastInstr = getStats().currInstr;
			if (recursion != RECURSION_LIMIT)
				getStats().currInstr = o.getEndLoc();

			// Debugging
			System.out.println(name + " = " + o);
			// / Debugging

			try {
				goAgain = processCmd(game, board, o.getData());
			} catch (StackOverflowError e) {
				e.printStackTrace();
				game.reportError(getClass().getSimpleName() + "\n$" + name
						+ "\nat " + getX() + "/" + getY() + "@" + lastInstr
						+ "^" + getStats().currInstr
						+ " encountered an error.\n\n" + "Details:\n$"
						+ e.getClass().getCanonicalName() + "\n"
						+ e.getMessage());
				return;
			} catch (Exception e) {
				e.printStackTrace();
				game.reportError(getClass().getSimpleName() + "\n$" + name
						+ "\nat " + getX() + "/" + getY() + "@" + lastInstr
						+ "^" + getStats().currInstr
						+ " encountered an error.\n\n" + "Details:\n$"
						+ e.getClass().getCanonicalName() + "\n"
						+ e.getMessage());
				return;
			}
		}

		if (!menuBuildStr.equals("")) {
			game.setMessage(name, menuBuildStr, this, new ObjectMenuRider(game,
					board, this));
		}
	}

	/**
	 * @param game
	 * @param board
	 */
	private void handleWalk(GameController game, Board board) {
		if (this instanceof Scroll)
			return;

		// TODO check if blocked by any non FLOOR non null
		if (!handleMove(board, getStats().stepX, getStats().stepY, false)) {
			if (!walkThud)
				message(game, board, Message.THUD);
			walkThud = true;
		} else {
			walkThud = false;
		}
	}

	/**
	 * Handles sending a message formatted as "RECIPIENT:LABEL" or "LABEL"
	 * (recipient being implied to be this object)
	 * 
	 * @param game
	 * @param board
	 * @param dat
	 */
	private void sendMessage(GameController game, Board board, String dat) {
		ArrayList<Element> rcptList = new ArrayList<Element>();
		String[] datSplit = dat.split(":");
		String msg;
		if (datSplit.length > 1) {
			if (datSplit[0].equalsIgnoreCase("self")) {
				rcptList.add(this);
			} else if (datSplit[0].equalsIgnoreCase("all")) {
				rcptList.addAll(board.getElementsByType(ZObject.class));
			} else if (datSplit[0].equalsIgnoreCase("others")) {
				rcptList.addAll(board.getElementsByType(ZObject.class));
				rcptList.remove(this);
			} else {
				rcptList.addAll(board.getElementsByName(datSplit[0]));
			}
			msg = datSplit[1];
		} else {
			rcptList.add(this);
			msg = dat;
		}

		int lockedTmp = getStats().p2;
		getStats().p2 = 0;
		Message out = new Message(msg);
		for (Element rcpt : rcptList)
			rcpt.message(game, board, out);
		getStats().p2 = lockedTmp;
	}

	protected boolean processCmd(GameController game, Board board, String cmd) {
		recursion++;
		if (recursion > RECURSION_LIMIT)
			return false;// prevent infinite loops

		if (cmd.length() < 1)
			return true;

		// I don't... I don't know what this is even for
		int numInstrs = 0, endLoc = 0;
		for (; endLoc < cmd.length(); numInstrs++) {
			OOPInstruction checkNext = getNextInstr(cmd, endLoc);
			endLoc = checkNext.getEndLoc();
		}
		if (numInstrs > 1) {
			endLoc = 0;
			while (endLoc < cmd.length()) {
				OOPInstruction next = getNextInstr(cmd, endLoc);
				endLoc = next.getEndLoc();
				if (next.getData().equals("#"))
					continue;
				System.err.println("Hitting unknown code");
				processCmd(game, board, next.getData());
			}
			return true;
		}
		// end unknown block

		if (cmd.startsWith("#!") || cmd.startsWith("#$"))
			cmd = cmd.substring(1);

		switch (cmd.charAt(0)) {
		case '\'':// comment, or a zapped label
		case ':':
			return true;// label, do nothing
		case '@':
			if (getStats().currInstr == 0) // only takes effect if first item
				name = cmd.substring(1);
			return true;
		case '#':
			return processPoundCmd(game, board, cmd);
		case '/':
		case '?':
			String moveDirStr = cmd.substring(1).trim();
			handleMove(game, board, moveDirStr, (cmd.charAt(0) == '/'));
			return false;
		default:
			if (menuBuildStr.equals(""))
				menuBuildStr += cmd;
			else
				menuBuildStr += "\n" + cmd;
			return true;// text
		}
	}

	private boolean processPoundCmd(GameController game, Board board, String cmd) {
		// TODO Strings in switches (once I install JDK7)
		cmd = cmd.substring(1).trim();

		// Single part commands
		if (cmd.equalsIgnoreCase("end")) {
			getStats().currInstr = -1;
			return false;
		} else if (cmd.equalsIgnoreCase("die")) {
			board.removeAt(getX(), getY(), this);
			OOPHelpers.putEmpty(board, getX(), getY());
			return false;
		} else if (cmd.equalsIgnoreCase("lock")) {
			getStats().p2 = 1;
			return true;
		} else if (cmd.equalsIgnoreCase("unlock")) {
			getStats().p2 = 0;
			return true;
		} else if (cmd.equalsIgnoreCase("endgame")) {
			game.getState().health = 0;// kill player
			return true;
		}

		// And now commands that have multiple parts
		String[] cmdParts = OOPResolver.toParts(cmd);
		if (cmdParts[0].equalsIgnoreCase("char")) {
			try {
				getStats().p1 = Integer.parseInt(cmdParts[1]);
			} catch (Exception e) {
				e.printStackTrace();
				game.setMessage("BAD #CHAR");
			}
			return false;
		} else if (cmdParts[0].equalsIgnoreCase("cycle")) {
			try {
				if (cmdParts.length <= 1)
					getStats().cycle = 3;
				else
					getStats().cycle = Integer.parseInt(cmdParts[1]);
			} catch (Exception e) {
				e.printStackTrace();
				game.setMessage("BAD #CYCLE");
			}
			return true;
		} else if (cmdParts[0].equalsIgnoreCase("send")) {
			sendMessage(game, board, cmdParts[1]);
			return false;
		} else if (cmdParts[0].equalsIgnoreCase("restore")
				|| cmdParts[0].equalsIgnoreCase("zap")) {
			cmdZapRestore(board, cmdParts, cmdParts[0].equalsIgnoreCase("zap"));
			return true;
		} else if (cmdParts[0].equalsIgnoreCase("shoot")
				|| cmdParts[0].equalsIgnoreCase("throwstar")) {
			cmdShoot(game, board, cmdParts);
			return true;// ???
		} else if (cmdParts[0].equalsIgnoreCase("bind")) {
			String targetName = cmdParts[1].trim();
			List<Element> targetList = board.getElementsByName(targetName);
			ZObject target = null;
			if (targetList.size() > 0)
				target = (ZObject) targetList.get(0);

			if (target != null) {
				getStats().currInstr = 0;
				getStats().oop = target.getStats().getOop();
				resetName();
				return true;
			}

			game.setMessage("BAD #BIND");
			return false;
		} else if (cmdParts[0].equalsIgnoreCase("if")) {
			try {
				int result = OOPHelpers.getBooleanFromStringArray(game, board,
						this, cmdParts, 1);

				if (result > 0) {
					getStats().currInstr = skipTo(cmd.split(" "), cmdParts,
							result);
					// return processCmd(game, board,
					// OOPHelpers.cmdFromArray(
					// cmdParts, result));
				}
			} catch (Exception e) {
				e.printStackTrace();
				game.setMessage("BAD #IF");
				return false;
			}
			return true;
		} else if (cmdParts[0].equalsIgnoreCase("set")) {
			try {
				String flag = cmdParts[1];

				for (int i = 0; i < game.getState().flags.length; i++) {
					if (game.getState().flags[i] == null) {
						game.getState().flags[i] = flag;
						break;
					}
				}
			} catch (Exception e) {
				game.setMessage("BAD #SET");
				System.err.println("BAD " + cmd);
				return false;
			}
			return true;
		} else if (cmdParts[0].equalsIgnoreCase("clear")) {
			try {
				String flag = cmdParts[1];

				for (int i = 0; i < game.getState().flags.length; i++) {
					if (game.getState().flags[i] != null) {
						if (game.getState().flags[i].equalsIgnoreCase(flag)) {
							game.getState().flags[i] = null;
							// only clear first
							break;
						}
					}
				}
			} catch (Exception e) {
				game.setMessage("BAD #CLEAR");
				System.err.println("BAD " + cmd);
				return false;
			}
			return true;
		} else if (cmdParts[0].equalsIgnoreCase("play")) {
			return true;// TODO
		} else if (cmdParts[0].equalsIgnoreCase("walk")) {
			String walkDirStr = cmd.trim().substring(5);
			walkThud = false;
			int moveDir = OOPHelpers.getDirFromStringArray(game, board, this,
					walkDirStr.split(" "));
			getStats().stepX = OOPHelpers.getDirX(moveDir);
			getStats().stepY = OOPHelpers.getDirY(moveDir);

			return true;
		} else if (cmdParts[0].equalsIgnoreCase("go")) {
			String moveDirStr = cmd.substring(3).trim();
			handleMove(game, board, moveDirStr, true);
			return false;
		} else if (cmdParts[0].equalsIgnoreCase("try")) {
			cmdTry(game, board, cmd, cmdParts);
			return false;
		} else if (cmdParts[0].equalsIgnoreCase("give")
				|| cmdParts[0].equalsIgnoreCase("take")) {
			try {
				String what = cmdParts[1].toLowerCase().trim();
				int howMany = ((cmdParts[0].equalsIgnoreCase("take")) ? -1 : 1)
						* Integer.parseInt(cmdParts[2].trim());

				boolean success = OOPHelpers
						.handleGiveTake(game, what, howMany);
				if (!success && cmdParts.length > 3) {
					return processCmd(game, board, "#" + cmdParts[3]);
				}
			} catch (Exception e) {
				game.setMessage("BAD #GIVE/TAKE");
				System.err.println("BAD " + cmd);
				return false;
			}
			return true;
		} else if (cmdParts[0].equalsIgnoreCase("then")) {
			// Do nothing, process arguments as command
			return processCmd(game, board, OOPHelpers.cmdFromArray(cmdParts, 1));
		} else if (cmdParts[0].equalsIgnoreCase("put")) {
			cmdPut(game, board, cmdParts);
			return true;
		} else if (cmdParts[0].equalsIgnoreCase("change")) {
			cmdChange(game, board, cmdParts);
			return true;
			// return false; // I don't know! I don't know!!! :( :'(
		} else if (cmdParts[0].equalsIgnoreCase("become")) {
			cmdBecome(game, board, cmd, cmdParts);
			return false;
		}

		// If it's not a builtin, it's a message
		int instrTemp = getStats().currInstr;
		sendMessage(game, board, cmd);
		if (getStats().currInstr == instrTemp && cmd.indexOf(':') == -1) {
			// no such label exists
			// board.setMessage("BAD COMMAND #");
			// currInstr = -1; // end
			System.err.println("BAD COMMAND # No command " + cmd);
		}
		return true;
	}

	/**
	 * @param cmdParts
	 * @param cmdParts
	 * @param result
	 * @return
	 */
	private int skipTo(String[] cmdOrig, String[] cmdParts, int result) {
		int newInstr = getStats().currInstr;
		int real = cmdParts.length - 1;
		for (int i = cmdOrig.length - 1; real >= result; i--) {
			if (!cmdOrig[i].trim().equals(""))
				real--;
			newInstr -= (cmdOrig[i].length() + 1);
		}
		return newInstr;
	}

	/**
	 * @param board
	 * @param cmd
	 * @param cmdParts
	 */
	private void cmdTry(GameController game, Board board, String cmd,
			String[] cmdParts) {
		String moveDirStr = cmd.substring(4).trim();
		boolean result = handleMove(game, board, moveDirStr, false);
		if (!result) {
			if (cmdParts.length > 2) {
				int sOffset = 2;
				while (cmdParts[sOffset - 1].equals("cw")
						|| cmdParts[sOffset - 1].equals("ccw")
						|| cmdParts[sOffset - 1].equals("opp")
						|| cmdParts[sOffset - 1].equals("rndp"))
					sOffset++;
				int newInstr = getStats().currInstr;
				if (sOffset == cmdParts.length)
					return;
				for (int i = sOffset; i < cmdParts.length; i++) {
					newInstr -= (cmdParts[i].length() + 1);
				}
				getStats().currInstr = newInstr;
			}
		}
	}

	/**
	 * @param board
	 * @param cmdParts
	 */
	private void cmdChange(GameController game, Board board, String[] cmdParts) {
		int code = -1, toCode = -1;
		int off = 1;
		code = ElementColoring.codeFromName(cmdParts[off]);
		if (code != -1) {
			off++;
		}
		int fromType = game.getElementResolver().codeFromName(cmdParts[off]);
		Class<Element> type = game.getElementResolver().classFromName(
				cmdParts[off]);
		off++;
		toCode = ElementColoring.codeFromName(cmdParts[off]);
		if (toCode != -1) {
			off++;
		}
		int toType = game.getElementResolver().codeFromName(cmdParts[off]);
		if (toType == -1) {
			game.setMessage("BAD #CHANGE");
			return;
		}

		List<Element> from = board.getElementsByType(type);
		for (Element e : from) {
			if (e.equals(board.getPlayer()))
				continue;

			if (e.getInteractionsRules().is(InteractionRule.FLOOR)) {
				if (board.elementAt(e.getX(), e.getY()) != null) {
					continue;
				}
			}

			int compCode = e.getColoring().getForeCode();
			if (e instanceof Door) // fruitloop insane hack
				compCode = e.getColoring().getBackCode();

			if (code == -1 || compCode == code) {
				int toCodeActual = toCode;
				if (toCodeActual == -1)
					toCodeActual = e.getSaveData().getColor();
				toCodeActual = game.getElementDefaults().getColor(toType,
						toCodeActual);
				Element replacement = game.getElementResolver().resolve(toType,
						toCodeActual, e.getX(), e.getY());
				if (replacement == null) {
					game.setMessage("BAD #CHANGE (Couldn't resolve)");
					return;
				}
				if (fromType == toType && e.getStats() != null) {
					replacement.setStats(e.getStats());
				} else {
					replacement.setStats(game.getElementDefaults()
							.getDefaultStats(toType));
				}
				board.removeAt(e.getX(), e.getY(), e);
				board.putElement(e.getX(), e.getY(), replacement);

				if ((type.equals(Line.class) && toType == game
						.getElementResolver().codeFromName("line"))
						|| (type.equals(Web.class) && toType == game
								.getElementResolver().codeFromName("web"))) {
					((Line) replacement).recalculateLineWalls(board);
				}
			}
		}
	}

	/**
	 * @param board
	 * @param cmd
	 * @param cmdParts
	 */
	private void cmdBecome(GameController game, Board board, String cmd,
			String[] cmdParts) {
		String what = "boardedge";
		int code = color.getCode();
		if (cmdParts.length != 1) {
			what = cmdParts[1];
			code = ElementColoring.codeFromName(cmdParts[1]);
			if (code != -1) {
				if (cmdParts.length > 2) {
					what = cmdParts[2];
				} else {
					what = "boardedge";
				}
			} else {
				code = color.getCode();
			}
		}
		code = game.getElementDefaults().getColor(what, code);
		Element replacement = game.getElementResolver().resolve(what, code,
				getX(), getY());
		if (replacement != null) {
			replacement.setStats(new Stats());
			board.removeAt(getX(), getY(), this);
			board.putElement(getX(), getY(), replacement);
		} else {
			game.setMessage("BAD #BECOME");
			System.err.println("BAD " + cmd);
		}
	}

	/**
	 * @param game
	 * @param board
	 * @param cmdParts
	 */
	private void cmdShoot(GameController game, Board board, String[] cmdParts) {
		try {
			int shootDir = OOPHelpers.getDirFromStringArray(game, board, this,
					cmdParts, 1);
			int shootX = OOPHelpers.getDirX(shootDir);
			int shootY = OOPHelpers.getDirY(shootDir);
			OOPHelpers.shoot(game, board, getX(), getY(), shootX, shootY,
					false,
					(cmdParts[0].equalsIgnoreCase("shoot")) ? Bullet.class
							: Star.class);
		} catch (Exception e) {
			e.printStackTrace();
			game.setMessage("BAD #SHOOT/THROWSTAR");
		}
	}

	/**
	 * Parses a restore or zap command and dispatches it, either to this object
	 * or the target object(s).
	 * 
	 * @param board
	 * @param cmdParts
	 * @param zap
	 */
	private void cmdZapRestore(Board board, String[] cmdParts, boolean zap) {
		if (cmdParts.length > 1 && cmdParts[1].indexOf(':') != -1) {
			String[] remoteZap = cmdParts[1].split(":");
			List<Element> zapObjs = board.getElementsByName(remoteZap[0]);
			for (Element e : zapObjs) {
				((ZObject) e).restore(remoteZap[1], zap);
			}
		} else if (cmdParts.length > 1) {
			restore(cmdParts[1], zap);
		} else if (!zap) {
			restore(null, false);
		}
	}

	/**
	 * @param targetLabel
	 *            The label to operate on, or if restoring, <code>null</code> to
	 *            restore all labels
	 * @param zap
	 *            if true, zapping a label, if false, restoring
	 */
	protected void restore(String targetLabel, boolean zap) {
		String target = null;
		if (targetLabel != null && !targetLabel.equals("")) {
			target = targetLabel.toLowerCase(); // all of them!
		}

		int loc = 0;
		while (true) {
			OOPInstruction o = getNextInstr(getStats().getOop(), loc);
			if (o == null)
				return;

			if ((zap && OOPResolver.isLabel(o))
					|| (!zap && OOPResolver.isComment(o))) {
				if (target == null
						|| target.equalsIgnoreCase(OOPResolver.labelText(o))) {
					// modify (zap or restore) this label (modifying even a
					// single char requires rebuilding the String)
					StringBuilder sb = new StringBuilder();
					sb.append(getStats().getOop().substring(0, o.getLoc()));
					sb.append(zap ? '\'' : ':');
					sb.append(getStats().getOop().substring(o.getLoc() + 1,
							getStats().getOop().length()));
					getStats().oop = sb.toString();

					if (target != null)
						return;
				}
			}

			loc = o.getEndLoc();
		}
	}

	/**
	 * @param game
	 * @param board
	 * @param cmdParts
	 */
	private void cmdPut(GameController game, Board board, String[] cmdParts) {
		// Get dir param
		int dir = OOPHelpers.getDirFromStringArray(game, board, this, cmdParts,
				1);

		// Bounds checking
		if (board.boundsCheck(getX() + OOPHelpers.getDirX(dir), getY()
				+ OOPHelpers.getDirY(dir)) != -1)
			return;// out of bounds

		// Skip the dir param to get to the stuff to put
		int sOffset = 2;
		while (cmdParts[sOffset - 1].equals("cw")
				|| cmdParts[sOffset - 1].equals("ccw")
				|| cmdParts[sOffset - 1].equals("opp")
				|| cmdParts[sOffset - 1].equals("rndp"))
			sOffset++;

		// Check if the word is a color; if it is, the next word
		// is the element type
		int code = -1;
		if (cmdParts.length > sOffset) {
			code = ElementColoring.codeFromName(cmdParts[sOffset]);
			if (code != -1) {
				sOffset++;
			}
		}

		// So check if we're being blocked
		Element blocking = board.elementAt(OOPHelpers.getDirX(dir) + getX(),
				OOPHelpers.getDirY(dir) + getY());
		if (blocking == null)
			blocking = board.floorAt(OOPHelpers.getDirX(dir) + getX(),
					OOPHelpers.getDirY(dir) + getY());

		// Get type param
		String toType;
		if (cmdParts.length > sOffset) {
			toType = cmdParts[sOffset];
		} else {
			toType = "boardedge";
		}
		// start building element to put
		if (game.getElementDefaults().getDefaultColor(toType) != -1) {
			code = game.getElementDefaults().getColor(toType, code);
		} else {
			if (code == -1 && blocking != null) {
				if (blocking.getInteractionsRules().is(InteractionRule.FLOOR))
					code = 0x0F;
				else
					code = blocking.getSaveData().getColor();
			}
		}
		// some elements requiring changing colors around a bit
		if (toType.equalsIgnoreCase("door")
				|| toType.equalsIgnoreCase("passage")) {
			code = ((code << 4) | 0x0F);
			if (blocking == null
					|| blocking.getInteractionsRules()
							.is(InteractionRule.FLOOR))
				code &= ~0x80;
		}

		Element replacement = game.getElementResolver().resolve(toType, code,
				OOPHelpers.getDirX(dir) + getX(),
				OOPHelpers.getDirY(dir) + getY());

		if (replacement == null) {
			game.setMessage("BAD #PUT");
			return;
			// throw new IllegalArgumentException("Couldn't resolve: " +
			// toType);
		}

		if (blocking != null && blocking.getStats() != null
				&& replacement.getClass().equals(blocking.getClass())) {
			replacement.setStats(blocking.getStats());
		} else {
			// default
			replacement.setStats(game.getElementDefaults().getDefaultStats(
					toType));
		}

		// Check if this can be moved out of the way- but remember
		// to move back afterwards
		if (blocking != null
				&& (blocking.getClass().equals(replacement.getClass()) || blocking instanceof Player)) {
			OOPHelpers.tryMove(getX(), getY(), OOPHelpers.getDirX(dir),
					OOPHelpers.getDirY(dir), board, this, true, false);
		}
		if (blocking instanceof Player)
			blocking = board.elementAt(OOPHelpers.getDirX(dir) + getX(),
					OOPHelpers.getDirY(dir) + getY());
		if (blocking instanceof Player)
			return;// do not allow killing the player

		// destroy anything already there
		board.removeAt(replacement.getX(), replacement.getY(), blocking);
		// and put the new element into place
		board.putElement(replacement.getX(), replacement.getY(), replacement);

		if (replacement instanceof Line) {
			((Line) replacement).recalculateLineWalls(board);
		}
	}

	/**
	 * Duplicative (see below)
	 * <p>
	 * This method does handle moving idle and resetting on fail properly
	 * 
	 * @param board
	 * @param moveDirStr
	 * @param resetOnFail
	 */
	private boolean handleMove(GameController game, Board board,
			String moveDirStr, boolean resetOnFail) {
		// try move
		try {
			int moveDir = OOPHelpers.getDirFromStringArray(game, board, this,
					moveDirStr.split(" "));
			int movX = OOPHelpers.getDirX(moveDir);
			int movY = OOPHelpers.getDirY(moveDir);

			if (movX == 0 && movY == 0)
				return false;

			int couldMove = OOPHelpers.tryMove(getX(), getY(), movX, movY,
					board, this);
			// if not..
			if (couldMove != -1 && resetOnFail && moveDir != -1) {
				getStats().currInstr = lastInstr;
			}
			if (couldMove == -1)
				return true;
		} catch (Exception e) {
			game.setMessage("BAD " + (resetOnFail ? '/' : '?') + " MOVE");
			System.err.println("BAD " + moveDirStr + " MOVE No direction "
					+ moveDirStr);
		}
		return false;
	}

	/**
	 * Duplicative (see above)
	 * 
	 * <p>
	 * This method does not handle moving idle and resetting on fail properly .
	 * 
	 * <p>
	 * One would be prompted to start screaming there are two, subtly different
	 * movement methods on this class. One should do so now. I'm afraid to
	 * refactor this because I'd have to verify there's no dependancy on the
	 * exact method of operation or something insane like that.
	 * 
	 * @param board
	 * @param movX
	 * @param movY
	 * @param resetOnFail
	 * @return
	 */
	private boolean handleMove(Board board, int movX, int movY,
			boolean resetOnFail) {
		if (movX == 0 && movY == 0)
			return false;

		int couldMove = OOPHelpers.tryMove(getX(), getY(), movX, movY, board,
				this);
		// if not..
		if (couldMove != -1 && resetOnFail) {
			getStats().currInstr = lastInstr;
		}
		if (couldMove == -1)
			return true;
		return false;
	}

	/**
	 * Using public fields- even if they're private ZObject can just reach in
	 * and take them so who cares
	 */
	public static class ObjectMenuRider {
		public GameController game;
		public Board board;
		public Element origin;

		public ObjectMenuRider(GameController game, Board board, Element origin) {
			this.game = game;
			this.board = board;
			this.origin = origin;
		}
	}
}
