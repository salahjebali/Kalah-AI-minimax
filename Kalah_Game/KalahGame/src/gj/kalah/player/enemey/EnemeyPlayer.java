package gj.kalah.player.enemey;

import java.util.ArrayList;
import java.util.Random;

import gj.kalah.player.*;

public class EnemeyPlayer implements Player {
	private Board board;

	public void start(boolean isFirst) {
		board = new Board();
	}

	public int move() {
		ArrayList<Integer> pm = board.possibleMoves(0);
		int move = new Random().nextInt(pm.size());
		board.moveDell(0, pm.get(move));
		return pm.get(move);
	}

	public void tellMove(int m) {
		board.moveDell(1, m);
	}

}
