package gj.kalah.player.enemey;

import java.util.ArrayList;



public class Board {

	private static int size = 14; // dimensione totale board
	private int[] board = new int[size]; // board unidimensionale

	// COSTRUTTORI
	public Board() {
		for (int i = 0; i < size; i++) {
			board[i] = 4;
		}
		board[size - 1] = 0; // mancala li metto zero
		board[size / 2 - 1] = 0;

	}

	// Costruttore che crea una board riempiendola con i valori della board in
	// ingresso. Questa board fittizia sarà necessaria per testare le mosse
	// dell'algoritmo di scelta delle mosse implementato nella classe player
	public Board(Board previousBoard) {
		for (int i = 0; i < size; i++) {
			board[i] = previousBoard.board[i]; // copio tutti gli elementi al suo interno ho tolto this
		}
	}

	// METODI ACCESSORI

	/*
	 * Metodo che restituisce quante pietre un giocatore ha in totale tra mancala e
	 * conche. Mi serve per stabilire il valore di una mossa
	 */
	public int getTotalStones(int player) {
		int stones = 0;
		for (int i = (size / 2) * player; i <= getMancalaIndex(player); i++) {
			stones = stones + board[i];
		}
		return stones;
	}
	/*
	 * Questa funzione restituisce il vincitore della partita. Una volta conclusa la
	 * partita se un giocatore ha ancora pietre nelle sue conche esse vengono
	 * prelevate e messe nel suo mancala. Per questo chiamo la funziona
	 * getTotalStones. Inoltre, un giocatore ha vinto anche se ha piu di 24 pietre
	 * nel suo mancala.
	 */

	public boolean checkWinner(int player) {
		boolean winner = false;
		if (getTotalStones(player) > getTotalStones((player + 1) % 2) || getMancala(player) > 24) {
			winner = true;
		}
		return winner;
	}

	// metodo che restituisce l'indice del mancala
	private int getMancalaIndex(int player) { // restituisce l'indice del mancala del player
		return (size / 2) * player + (size / 2) - 1;
	}

	// restituisce il contenuto di una conca

	public int getDell(int player, int index) {
		return board[getActualIndex(player, index)];

	}

	// restituisce il contenuto di un mancala
	public int getMancala(int player) {
		return board[getMancalaIndex(player)];
	}

	// restituisce l'effettivo indice della conca
	private int getActualIndex(int player, int index) {
		return (size / 2) * player + index;
	}

	// restituisce la dimensione della board
	public int getSize() {
		return size;
	}

	// METODI MUTATORI

	/*
	 * Metodo che seleziona una conca di un dato giocatore, ne preleva le pietre e
	 * le distrubuisce nelle altre conche e mancala (escluso quello avversario).
	 * Restituisce un boolean perche se l'ultima pietra finisse nel mancala di chi
	 * sta giocando, il giocatore potrebbe ripetere la mossa e percio restituisco
	 * true. Il controllo è delegato ad un altro metodo.
	 */

	public boolean moveDell(int player, int index) {
		int actualIndex = getActualIndex(player, index); // indice effettivo della board del player
		int stones = board[actualIndex]; // mi salvo quante pietre devo prelevare
		board[actualIndex] = 0; // azzero la conca da cui prelevo le pietre
		for (int i = 1; i <= stones; i++) {
			int withdrawIndex = getWithDrawIndex(player, index, i); // la spiegazione dopo
			board[withdrawIndex] = board[withdrawIndex] + 1; // distribuisco una pietra
		}
		int lastIndex = getWithDrawIndex(player, index, stones);
		checkFrontOpponent(player, lastIndex); // controllo se posso rubare le pietre avversarie
		return checkMancala(player, lastIndex);
	}

	/*
	 * Quando il seguente metodo viene invocato alla riga 101 restituisce via via le
	 * posizioni della board dove inserire le pietre. Quando, invece, viene invocato
	 * alla riga 103, passandogli le pietre, esso restituisce l'ultima posizione in
	 * cui siamo giunti. Infatti, eseguendo nella prima parentesi tonda l'operazione
	 * modulo 13 ( che corrisponde alla posizione del mancala avversario ), escludo
	 * a priori la possibilita di inserirci pietre quando e il MIO turno. In ogni
	 * caso, quando sara il turno dell'avversario, il prodotto 7*player sara 7 e
	 * sommato a 13 restituira la posizione del mancala avversario in cui poi
	 * inserira la pietra. Poiche il numero di pietre corrisponde anche all'ultimo
	 * valore che assumera l'indice i, utilizzando lo stesso metodo passandogli il
	 * numero di pietre, ottero l'ultima conca visitata.
	 * 
	 * 
	 */

	private int getWithDrawIndex(int player, int index, int i) {
		int r = ((((index + i) % (size - 1)) + (size / 2) * player) % size);
		return r;
	}

	/*
	 * Il seguente metodo controlla se la mia ultima pietra e finita in una mia
	 * conca vuota. Se cosi fosse dovrei prelevare le pietre nella conca avversaria
	 * di fronte alla mia, e insieme alla mia ultima pietra aggiungerla al mio
	 * mancala.
	 */
	private void checkFrontOpponent(int player, int lastIndex) {
		int actualIndex = getActualIndex(player, lastIndex - (size / 2) * player); // posizione
		// effettiva giocatore
		int stones = board[actualIndex] - 1;
		if ((checkBorders(actualIndex, player) && stones == 0)) { // controllo che non sia finita in un mancala
			// se sono finito in una conca che prima
			// era vuota
			board[actualIndex] = 0; // azzero la mia conca
			int stolenStones = board[(size - 2 - actualIndex)]; // pietre nella conca dell'avversario
			board[(size - 2 - actualIndex)] = 0; // azzero la conca avversaria
			board[getMancalaIndex(player)] = board[getMancalaIndex(player)] + 1 + stolenStones;// aggiorno il mio
																								// mancala
		}

	}

	/*
	 * Valuto che la pietra sia all'interno del confine di chi sta giocando. Deve
	 * essere compresa tra la prima posizione ed il mancala di chi gioca.
	 */
	private boolean checkBorders(int index, int player) {
		int left = (size / 2) * player; // la prima posizione del giocatore
		int right = getMancalaIndex(player); // posizione mancala giocatore
		boolean r = (index >= left && index < right) ? true : false;
		return r;
	}

	/*
	 * Controllo se l'ultima pietra è finita nel mio mancala. Se cosi fosse
	 * restituisco un valore true cosi da poter ripetere la mossa.
	 */
	private boolean checkMancala(int player, int lastIndex) {
		boolean r = lastIndex == getMancalaIndex(player);
		return r;
	}

	@Override
	public String toString() {
		String s = new String();
		s += "[" + board[size - 1] + "]"; // in estrema sinistra metto il mancala avversario
		s += "  "; // due space
		for (int i = size - 2; i >= size / 2; i--) { // in alto metto le conche avversarie
			s += "(" + board[i] + ")";
		}
		s += "\n     ";
		for (int i = 0; i < (size / 2 - 1); i++) { // in basso le mie conche
			s += "(" + board[i] + ")";
		}
		s += "  "; // due space
		s += "[" + board[(size / 2) - 1] + "]"; // in estrema sinistra metto il mio mancala
		return s;
	}

	/*
	 * Il seguente metodo controlla se la partita è finita. Per farlo scorre le
	 * conche di uno dei due giocatori e controlla che siano tutte vuote.
	 */
	public boolean checkEndGame(int player) {
		boolean result = checkEmptyDells(player); // se le conche sono vuote ritorna true
		if (!result) {// se è false controllo che le conche avversarie
			player = (player + 1) % 2;// cambio giocatore
			result = checkEmptyDells(player);
		}
		return result;// true = gioco finito
	}

	private boolean checkEmptyDells(int player) {
		boolean result = true;
		int i = (size / 2) * player;
		int mancala = getMancalaIndex(player);
		while (result && i < mancala) {
			result = board[i] == 0;
			i = i + 1;
		}
		return result;
	}

	/*
	 * Questo metodo restituisce le possibili mosse. Per possibili mosse si
	 * intendono le posizioni delle conche non vuote di ciascun giocatore. La
	 * struttura dati più appropriata è un arraylist, poiche non possiamo sapere a
	 * priori quante mosse a disposione ha un giocatore. Sappiamo che al massimo ne
	 * ha 6.
	 * 
	 */
	public ArrayList<Integer> possibleMoves(int player) {
		ArrayList<Integer> pm = new ArrayList<>();
		int mancala = getMancalaIndex(player);
		for (int i = (size / 2) * player; i < mancala; i++) {
			if (board[i] != 0)
				pm.add(i - (size / 2) * player);// devo far cosi perche in movedell aggiorna la posizione
		}
		return pm;
	}
	
	// METODI PER DEBUGGARE

	public void setDell(int player, int index, int stone) {
		int actualIndex = getActualIndex(player, index);
		board[actualIndex] = stone;
	}

	public void setMancala(int player, int stone) {
		int mancalaIndex = getMancalaIndex(player);
		board[mancalaIndex] = stone;
	}
}
