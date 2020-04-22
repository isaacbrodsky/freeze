/**
 * 
 */
package com.isaacbrodsky.freeze.game;

/**
 * Probably inappropriate use of public fields.
 * 
 * @author isaac
 */
public class BoardState {
	/*
	 * Yech public elements
	 */
	public String boardName, message;
	public int shots, dark, boardNorth, boardSouth, boardWest, boardEast,
			restart, enterX, enterY, timeLimit;
	
	public BoardState() {
		boardName = "Untitled board";
		shots = dark = boardNorth = boardSouth = boardWest = boardEast = restart = 0;
		message = "";
		enterX = enterY = timeLimit = 0;
	}
	
	public BoardState(String boardName2, int shots2, int dark2, int boardNorth2,
			int boardSouth2, int boardWest2, int boardEast2, int restart2,
			String message2, int enterX2, int enterY2, int timeLimit2) {
		this.boardName = boardName2;
		this.shots = shots2;
		this.dark = dark2;
		this.boardNorth = boardNorth2;
		this.boardSouth = boardSouth2;
		this.boardWest = boardWest2;
		this.boardEast = boardEast2;
		this.restart = restart2;
		this.message = message2;
		this.enterX = enterX2;
		this.enterY = enterY2;
		this.timeLimit = timeLimit2;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\tName: " + boardName + "\r\n");
		sb.append("\tShorts: " + shots + "\r\n");
		sb.append("\tDark: " + dark + "\r\n");
		sb.append("\tConnections: N: " + boardNorth + " S: " + boardSouth
				+ " W: " + boardWest + " E: " + boardEast + "\r\n");
		sb.append("\tRestart: " + restart + "\r\n");
		sb.append("\tMessage:\r\n" + message + "\r\n");
		sb.append("\tEnter: " + enterX + ", " + enterY + "\r\n");
		sb.append("\tTime Limit: " + timeLimit + "\r\n");
		return sb.toString();
	}
}
