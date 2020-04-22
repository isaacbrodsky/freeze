/**
 * 
 */
package com.isaacbrodsky.freeze.elements;

import java.util.Random;

import com.isaacbrodsky.freeze.elements.data.InteractionRulesSet;
import com.isaacbrodsky.freeze.elements.data.SaveData;
import com.isaacbrodsky.freeze.elements.oop.Message;
import com.isaacbrodsky.freeze.elements.oop.OOPHelpers;
import com.isaacbrodsky.freeze.game.Board;
import com.isaacbrodsky.freeze.game.GameController;
import com.isaacbrodsky.freeze.graphics.ElementColoring;

/**
 * @author isaac
 * 
 */
public class SpinningGun extends AbstractElement {
	private static char[] GUN_CHARS = { 24, 26, 25, 27 };

	private int charIndex;
	private ElementColoring color;

	public SpinningGun() {
		color = new ElementColoring(2, ElementColoring.ColorMode.RECESSIVE);

		charIndex = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.isaacbrodsky.freeze.elements.Element#createInstance(int, int)
	 */
	@Override
	public void createInstance(SaveData dat) {
		this.color = new ElementColoring(dat.getColor(),
				ElementColoring.ColorMode.RECESSIVE);
	}

	@Override
	public SaveData getSaveData() {
		return new SaveData(0x27, color);
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
		return GUN_CHARS[charIndex];
	}

	private static final InteractionRulesSet _IRS = new InteractionRulesSet();

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
	 * @see
	 * com.isaacbrodsky.freeze.elements.Element#message(com.isaacbrodsky.freeze
	 * .game.GameController, com.isaacbrodsky.freeze.game.Board,
	 * com.isaacbrodsky.freeze.elements.oop.Message)
	 */
	@Override
	public void message(GameController game, Board board, Message msg) {
		// don't care
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
		charIndex++;
		if (charIndex >= GUN_CHARS.length)
			charIndex = 0;

		Player p = board.getPlayer();

		int fireRate = getStats().getP2();
		boolean typeShootsStars = false;
		if ((fireRate & 128) == 128) {
			fireRate = fireRate ^ 128;
			typeShootsStars = true;
		}

		Random r = new Random();

		boolean playerNearby = (p != null && (Math.abs(getX() - p.getX()) <= 2 || Math
				.abs(getY() - p.getY()) <= 2));

		if (shouldShoot3(playerNearby, fireRate, r)) {
			String dirStr = "seek";
			if (r.nextInt(10) > getStats().getP1())
				dirStr = "rnd";
			int actDir = OOPHelpers.getDirFromStringArray(game, board, this,
					new String[] { dirStr });
			int actX = OOPHelpers.getDirX(actDir);
			int actY = OOPHelpers.getDirY(actDir);

			if (typeShootsStars) {
				OOPHelpers.shoot(game, board, getX(), getY(), actX, actY,
						false, Star.class);
			} else {
				OOPHelpers.shoot(game, board, getX(), getY(), actX, actY,
						false, Bullet.class);
			}
		}
	}

	/**
	 * @param p
	 * @param fireRate
	 * @param r
	 * @param shouldShoot
	 * @return
	 * 
	 * @deprecated Do not use
	 */
	private boolean shouldShoot(boolean playerNearby, int fireRate, Random r) {
		// odds information from MWEnc
		if (playerNearby) {
			// if (r.nextInt(9) >= 9 - fireRate)
			if (r.nextDouble() > ((double) 9 / (double) (9 - fireRate)) - 1)
				return true;
		} else {
			// this is not really what MWEnc says
			if (r.nextInt(fireRate + 15) > 15)
				// if (r.nextDouble() > ((double) (fireRate - 1) / (double) (10
				// - fireRate)) - 1 )
				return true;
		}
		return false;
	}

	/**
	 * @param p
	 * @param fireRate
	 * @param r
	 * @return
	 * 
	 * @deprecated Do not use
	 */
	private boolean shouldShoot2(boolean playerNearby, int fireRate, Random r) {
		// these stats are off and do not match MWEnc
		// probably a little overactive on the shotting
		int odds = 10;
		int check = 9;
		if (playerNearby) {
			odds /= 2;
		}

		odds -= fireRate;

		if (r.nextInt(check) >= odds)
			return true;
		else
			return false;
	}

	/**
	 * Compromise solution
	 * 
	 * @param p
	 * @param fireRate
	 * @param r
	 * @return
	 */
	private boolean shouldShoot3(boolean playerNearby, int fireRate, Random r) {
		// these stats are off and do not match MWEnc
		// probably a little overactive on the shooting
		int odds = 10;
		int check = 9;
		if (playerNearby) {
			odds /= 2;
		} else {
			if (r.nextInt(fireRate + 15) > 15)
				return true;
			else
				return false;
		}

		odds -= fireRate;

		if (r.nextInt(check + 3) >= odds + 3)
			return true;
		else
			return false;
	}
}
