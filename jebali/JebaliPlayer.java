package gj.kalah.player.jebali;

import gj.kalah.player.*;
import java.util.ArrayList;

public class JebaliPlayer implements Player {
	private static Board game;
	private int maxDepth = 5;
	private int myplayer;

	// METODI PRINCIPALI DELLA CLASSE PLAYER //

	/*
	 * Per scegliere che mossa fare mi avvalgo del metodo minmax, spiegato
	 * dettagliatamente piu in basso. Il metodo move, che chiama minmax, inizializza
	 * un vettore di due posizioni il cui nome è bestMove. In prima posizione
	 * contiene un valore che determina se la mossa è vantaggiosa o meno. In seconda
	 * posizione contiene la posizione della mossa. Scelgo di inizializzare la prima
	 * posizione con il minimo degli Interi e la seconda posizione con il valore -
	 * 1. In questo modo, anche la mossa piu "scarsa" tra quelle proposte da minmax
	 * potra essere eseguita senza errori. Creo un ArrayList che contiene gli indici
	 * delle mosse giocabili, e per ogni indice chiamo il metodo minmax e simulo una
	 * partita a profondita 6. Dunque, il metodo chiamato, restituira un valore
	 * vantaggioso o svantaggioso. Itero questo procedimento per tutte le mosse a
	 * dispozione, ogni turno, e decido di giocare quella con punteggio maggiore.
	 * Dunque, aggiorno la board di gioco, per i turni successivi, e comunico la
	 * posizione, contenuta in bestMove[1].
	 */

	public int move() {
		int[] alfabeta = new int[2];// [0] alfa [1] beta
		initAlfaBeta(alfabeta);
		int[] bestMove = new int[2];// [0] valore [1] posizione
		initBest(bestMove);
		ArrayList<Integer> possibleMoves = game.possibleMoves(myplayer);
		for (int positionMove : possibleMoves) {
			int value = 0;
			Board fakeBoard = new Board(game);
			value = minmax(fakeBoard, myplayer, 0, true, alfabeta);
			bestMove = (value >= bestMove[0]) ? new int[] { value, positionMove } : bestMove;
		}
		game.moveDell(myplayer, bestMove[1]);
		return bestMove[1];
	}

	public void start(boolean isFirst) {
		myplayer = (isFirst) ? 0 : 1;
		game = new Board();
	}

	public void tellMove(int move) { // move è la mossa avversaria
		game.moveDell((myplayer + 1) % 2, move); // la faccio eseguire sulla board
	}

	// METODI AUSILIARI DELLA CLASSE JEBALIPLAYER //

	private void initBest(int[] best) {
		best[0] = Integer.MIN_VALUE;
		best[1] = -1;
	}

	private void initAlfaBeta(int[] alfabeta) {
		alfabeta[0] = Integer.MIN_VALUE; // in prima posizione alfa
		alfabeta[1] = Integer.MAX_VALUE; // in seconda poszione beta

	}

	// MINMAX PLAYER alfa-beta pruning //

	/*
	 * L'algoritmo per scegliere la posizione dalla quale prelevare le pietre è il
	 * minmax. Il minmax è un algoritmo euristico decisionale che ha come scopo
	 * quello di scegliere la mossa che minimizza la perdita del mio giocatore,
	 * ovvero ottenere il massimo tra i valori proposti, i quali sono i minimi.
	 * Infatti, si suppone che anche l'avversario adotti la stessa strategia, ma nel
	 * suo caso lui scegliera le mosse che garantiranno a me il minimo punteggio.
	 * Io, tra queste, scegliero le mosse che garantiranno a me il massimo
	 * punteggio. La profondita scelta e 6 ed e stata scelta arbitrariamente. Ad
	 * ogni chiamata ricorsiva, se non devo giocare nuovamente (ultima pietra mio
	 * mancala) inverto il giocatore e aumento la profondita, che parte da zero. Se
	 * gioca max gli assegno come valore il minimo, cosicche possa crescere. Se
	 * gioca min gli assegno come valore il massimo, cosicche possa diminuire. Creo
	 * un ArrayList, per ciascun giocatore, di possibili mosse. Questo insieme di
	 * posizioni dipende dalla configurazione attuale della board. Scelto un
	 * ArrayList per rappresentare questi dati perche il numero di posizioni
	 * giocabili è variabile. Quindi, per ogni posizione buona, eseguo la mossa, e
	 * ,a seconda di chi gioca, le attribuisco un punteggio. Dunque, a questo punto,
	 * nel metodo chiamante valutero il valore ritornato e aggiornero la variabile
	 * bestMove[0] con il valore e bestMove[1] con la posizione che ha generato il
	 * valore. L'algoritmo viene ottimizzato mediante la potatura alfabeta.Questa
	 * ottimizzazione ci permettera di evitare di visitare tutti i nodi dell'albero,
	 * risparmiando risorse. Infatti non appena il valore di beta sara minore o
	 * uguale a quello di alfa, ovvero il minimo dei valori di MIN è minore o uguale
	 * al massimo dei valori di MAX non ha senso proseguire con la ricerca.
	 */

	private int minmax(Board board, int player, int depth, boolean playAgain, int[] alfabeta) {
		player = (!playAgain) ? (player + 1) % 2 : player; // se devo giocare, sta di nuovo a me non cambio giocatore
		if (board.checkEndGame() || depth == maxDepth) { // mi fermo quando la partita è finita o profondita = max
			return valuateBoard(board, depth);
		} else {
			int value = (player % 2 == myplayer) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
			ArrayList<Integer> possibleMoves = board.possibleMoves(player);
			for (int move : possibleMoves) {
				Board testBoard = new Board(board);
				playAgain = testBoard.moveDell(player, move);
				int score = minmax(testBoard, player, depth + 1, playAgain, alfabeta);
				value = (player % 2 == myplayer) ? Math.max(value, score) : Math.min(value, score);
				alfabeta = (player % 2 == myplayer) ? new int[] { Math.max(alfabeta[0], value), alfabeta[1] }
						: new int[] { alfabeta[0], Math.min(alfabeta[1], value) };
				if (alfabeta[1] <= alfabeta[0]) { // se beta minore uguale alfa interrompo la ricerca
					break;
				}
			}
			return value;
		}
	}

	/*
	 * Il criterio per assegnare il punteggio è il seguente: 1) Se ho vinto, il
	 * punteggio sarà il massimo meno la profondita. Infatti se la mossa porta alla
	 * mia vittoria voglio che ciò accada nel minor numero di turni . Se invece ho
	 * perso, il punteggio sara minimo aumentato della profondita. Se perdo voglio
	 * che ciò accada il più tardi possibile. 2) se invece non è finito il gioco,
	 * valuto positivamente le mosse che hanno fatto si che il numero di pietre nel
	 * mio mancala aumentasse rispetto a quelle contenute nel mancala avversario. E
	 * non solo, se il numero di pietre nel mio mancala si è avvicinato a 24, ovvero
	 * il numero di pietre per vincere, o addirittura superato, allora lo valuto
	 * positivamente. Negative le altre. Basterà attribuire la differenza al valore
	 * ed esso si regolera da se.
	 * 
	 */
	private int valuateBoard(Board board, int depth) {
		int score = 0;
		if (board.checkEndGame()) { // se la partita è finita
			if (board.checkWinner(myplayer)) { // se ho vinto io
				score = Integer.MAX_VALUE - depth; // assegno il punteggio massimo
			} else if (board.checkWinner((myplayer + 1) % 2)) { // altrimenti controllo se è stato l'avversario a
																// vincere
				score = Integer.MIN_VALUE + depth; // in tal caso assegno il punteggio minimo
			}
		} else {
			score = board.getMancala(myplayer) - board.getMancala((myplayer + 1) % 2);
		}
		return score;
	}

}
