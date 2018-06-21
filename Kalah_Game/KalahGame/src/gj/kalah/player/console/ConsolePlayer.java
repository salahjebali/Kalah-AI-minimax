package gj.kalah.player.console;

import java.util.Scanner;

import gj.kalah.player.Player;

public class ConsolePlayer implements Player {
	
	
	private Scanner in = new Scanner(System.in);

	public void start(boolean isFirst) {
	}

	public int move() {
		int n = in.nextInt();
		return n;
	}

	public void tellMove(int m) {
	}
}