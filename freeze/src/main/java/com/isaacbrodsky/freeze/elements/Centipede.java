/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.elements.oop.OOPHelpers;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;

/**
 * Centipedes cannot be directly instantiated; they must be composed of a head
 * and segments.
 * 
 * @author isaac
 */
public abstract class Centipede extends Lion {
	/**
	 * 
	 */
	private Centipede() {

	}

	abstract public void followTick(GameController game, Board board, int x,
			int y, List<Element> prevention);

	public void message(GameController game, Board board, Message msg) {
		super.message(game, board, msg);
		if (msg.isShot() || msg.isTouch() || msg.isBombed()) {
			if (getStats().getFollowerElement() != null)
				getStats().getFollowerElement().getStats().elLeader = null;
			if (getStats().getLeaderElement() != null)
				getStats().getLeaderElement().getStats().elFollower = null;
		}
	}

	private static boolean existsOnBoard(Board b, Element e) {
		return b.elementAt(e.getX(), e.getY()) == e;
	}

	/**
	 * @param board
	 * @param x
	 * @param y
	 * @param xs
	 * @param ys
	 * @return
	 */
	private static Element connectFollower(Element base, Board board, int x,
			int y) {
		int xs = 1;
		int ys = 1;
		Element fol = base.getStats().getFollowerElement();
		if (fol == null) {
			// try to rebuild centipede!
			Element nFol = null;
			Element e;
			if (board.boundsCheck(x - xs, y) == -1
					&& (e = board.elementAt(x - xs, y)) instanceof Segment
					&& e.getStats() != null
					&& (e.getStats().getLeaderElement() == null || e.getStats()
							.getLeaderElement() == base)) {
				nFol = e;
			} else if (board.boundsCheck(x + xs, y) == -1
					&& (e = board.elementAt(x + xs, y)) instanceof Segment
					&& e.getStats() != null
					&& (e.getStats().getLeaderElement() == null || e.getStats()
							.getLeaderElement() == base)) {
				nFol = e;
			} else if (board.boundsCheck(x, y - ys) == -1
					&& (e = board.elementAt(x, y - ys)) instanceof Segment
					&& e.getStats() != null
					&& (e.getStats().getLeaderElement() == null || e.getStats()
							.getLeaderElement() == base)) {
				nFol = e;
			} else if (board.boundsCheck(x, y + ys) == -1
					&& (e = board.elementAt(x, y + ys)) instanceof Segment
					&& e.getStats() != null
					&& (e.getStats().getLeaderElement() == null || e.getStats()
							.getLeaderElement() == base)) {
				nFol = e;
			} else {
				nFol = null;
			}
			fol = nFol;
			base.getStats().elFollower = nFol;
			if (nFol != null)
				nFol.getStats().elLeader = base;
		} else {
			// fol in stats not null - check to make
			// sure it's correct and exists on board
			if (!existsOnBoard(board, fol)) {
				base.getStats().elFollower = null;
				//will not result in an infinite loop
				//because the elFollower field is null
				//and this branch can only be accessed
				//with a non-null elFollower
				return connectFollower(base, board, x, y);
			}
		}
		return fol;
	}

	/**
	 * @param board
	 */
	private static Element replaceWithHead(Board board, Element before, int x,
			int y) {
		Head nh = new Head();
		nh.createInstance(new SaveData(44, before.getSaveData().getColor()));
		nh.setStats(before.getStats());
		nh.setXY(x, y);
		board.removeAt(x, y, before);
		board.putElement(x, y, nh);
		return nh;
	}

	/**
	 * @param board
	 */
	private static Element replaceWithSegment(Board board, Element before,
			int x, int y) {
		Segment ns = new Segment();
		ns.createInstance(new SaveData(45, before.getSaveData().getColor()));
		ns.setStats(before.getStats());
		ns.setXY(x, y);
		board.removeAt(x, y, before);
		board.putElement(x, y, ns);
		return ns;
	}

	public static class Segment extends Centipede {
		private int leadt = 0;

		@Override
		public int getDisplayCharacter() {
			return 'O';
		}

		@Override
		public void followTick(GameController game, Board board, int x, int y,
				List<Element> prevention) {
			int ox = getX();
			int oy = getY();
			int xs = x - ox;
			int ys = y - oy;
			if (board.boundsCheck(x, y) != -1)
				return;

			Element at = board.elementAt(x, y);
			if (at instanceof Player) {
				message(game, board, Message.TOUCH);
				board.removeAt(getX(), getY(), this);
				return;
			}

			Element fol = connectFollower(this, board, ox, oy);
			// xs, ys);

			OOPHelpers.tryMove(getX(), getY(), xs, ys, board, this, false);

			if (!prevention.contains(fol)) {
				// Prevent infinite recursing - TODO fix this better
				if (fol != null) {
					prevention.add(fol);
					((Centipede) fol).followTick(game, board, ox, oy,
							prevention);
				}
			}
		}

		@Override
		public void tick(GameController game, Board board) {
			Element lead = getStats().getLeaderElement();

			// verify lead exists on board - if it doesn't,
			// we're just carrying around a reference to
			// a dead element
			//doesn't work properly
//			if (lead != null && !existsOnBoard(board, lead)) {
//				lead = null;
//				getStats().elLeader = null;
//			}

			if (lead == null && leadt >= 0)
				leadt = 0;
			if (lead != null)
				leadt = 1;
			if (leadt <= 0)
				leadt--;
			if (leadt == -2)
				replaceWithHead(board, this, getX(), getY());
		}
	}

	public static class Head extends Centipede {
		@Override
		public int getDisplayCharacter() {
			return 233;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.isaacbrodsky.freeze.elements.Centipede#followTick(com.isaacbrodsky
		 * .freeze.game.GameController, com.isaacbrodsky.freeze.game.Board,
		 * int, int)
		 */
		@Override
		public void followTick(GameController game, Board board, int x, int y,
				List<Element> prevention) {
			// TODO Auto-generated method stub
		}

		@Override
		public void tick(GameController game, Board board) {
			int x = getX(), y = getY();
			// int xs = getStats().getStepX(), ys = getStats().getStepY();
			Element fol = connectFollower(this, board, x, y);

			Random r = new Random();
			if (r.nextInt(10) < getStats().getP2()) {
				if (r.nextInt(10) > getStats().getP1()) {
					handleMove(game, board, "rnd");
				} else {
					handleMove(game, board, "seek");
				}
			} else {
				handleMove(game, board, "flow");
			}

			if (fol != null) {
				// if (fol.getCycle() == getCycle()) {
				if (fol instanceof Centipede) {
					Centipede f = (Centipede) fol;
					f.followTick(game, board, x, y, new ArrayList<Element>());
				}
				// } else {
				// crash!
				// }
			}

			// if blocked in on all sides switch sides
			if (blockedIn(board)) {
				Element traverse = fol;
				Element last = traverse;
				List<Element> seen = new ArrayList<Element>();
				while (traverse != null) {
					seen.add(traverse);
					last = traverse;
					traverse = traverse.getStats().getFollowerElement();
					if (seen.contains(traverse)) {
						System.err.println("infinite cetipede loop 1");
						break; // prevent infinite loops
					}
				}
				if (last != null) {
					replaceWithHead(board, last, last.getX(), last.getY());
					Element replacement = replaceWithSegment(board, this,
							getX(), getY());

					replacement.getStats().elFollower = null;
					replacement.getStats().elLeader = fol;

					traverse = fol;
					last = replacement;
					Element next;
					seen.clear();
					while (traverse != null) {
						seen.add(traverse);
						next = traverse.getStats().elFollower;
						traverse.getStats().elFollower = last;
						traverse.getStats().elLeader = next;
						last = traverse;
						traverse = next;
						if (seen.contains(traverse)) {
							System.err.println("infinite cetipede loop 2");
							break; // prevent infinite loops
						}
					}
				}
			}
		}

		private boolean blockedIn(Board board) {
			int x = getX(), y = getY();
			int xs, ys;
			xs = ys = 1;
			if (((board.boundsCheck(x + xs, y) != -1 || board.elementAt(x + xs,
					y) != null))
					&& ((board.boundsCheck(x - xs, y) != -1 || board.elementAt(
							x - xs, y) != null))
					&& ((board.boundsCheck(x, y + ys) != -1 || board.elementAt(
							x, y + ys) != null))
					&& ((board.boundsCheck(x, y - ys) != -1 || board.elementAt(
							x, y - ys) != null))) {
				return true;
			}

			return false;
		}

		protected void handleMove(GameController game, Board board,
				String moveDirStr) {
			int firstMoveDir = OOPHelpers.getDirFromStringArray(game, board,
					this, new String[] { moveDirStr });
			List<Integer> dirs = new ArrayList<Integer>(4);
			if (firstMoveDir != -1)
				dirs.add(firstMoveDir);
			for (int i = firstMoveDir + 1; i < 4; i++) {
				dirs.add(i);
			}
			for (int i = 0; i < firstMoveDir; i++) {
				dirs.add(i);
			}

			for (Integer dir : dirs) {
				int movX = OOPHelpers.getDirX(dir);
				int movY = OOPHelpers.getDirY(dir);

				if (board.boundsCheck(getX() + movX, getY() + movY) != -1)
					continue;

				Element at = board.elementAt(getX() + movX, getY() + movY);
				if (at instanceof Player) {
					message(game, board, Message.TOUCH);
					board.removeAt(getX(), getY(), this);
					return;
				}

				getStats().stepX = movX;
				getStats().stepY = movY;

				int ret = OOPHelpers.tryMove(getX(), getY(), movX, movY, board,
						this, false);
				if (ret == -1)
					return;
			}
		}
	}
}
