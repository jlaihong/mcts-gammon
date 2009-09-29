import java.util.Arrays;
import java.util.Map.Entry;

import org.mctsgammon.Board;
import org.mctsgammon.Move;

import junit.framework.TestCase;


public class BoardTest extends TestCase {

	public void testBlackMove() {
		Board board = Board.startBoard; 

		System.out.println("= From  =========================================");
		System.out.println(board.toString());
		System.out.println("= For Black in 3 ================================");
		for(Entry<Board, Move[]> b: board.afterBlackMoves(3).entrySet() ){
			System.out.println(b.getKey().toString());
			System.out.println("after "+Arrays.toString(b.getValue()));
			System.out.println();
		}

		board = Board.cleanBoard.afterMove(new Move(Board.blackbar, 13, true, false));
		System.out.println("= From  =========================================");
		System.out.println(board.toString());
		System.out.println("= For Black in 3 ================================");
		for(Entry<Board, Move[]> b: board.afterBlackMoves(3).entrySet()){
			System.out.println(b.getKey().toString());
			System.out.println("after "+Arrays.toString(b.getValue()));
			System.out.println();
		}

		board = Board.cleanBoard.afterMove(new Move(10, Board.blackbar, 4, true, false),new Move(5, Board.blackbar, 6, true, false));
		System.out.println("= From  =========================================");
		System.out.println(board.toString());
		System.out.println("= For Black in 4 ================================");
		for(Entry<Board, Move[]> b: board.afterBlackMoves(4).entrySet()){
			System.out.println(b.getKey().toString());
			System.out.println("after "+Arrays.toString(b.getValue()));
			System.out.println();
		}
		
		board = Board.cleanBoard.afterMove(new Move(10, Board.blackbar, 4, true, false),new Move(5, Board.blackbar, 6, true, false));
		System.out.println("= From  =========================================");
		System.out.println(board.toString());
		System.out.println("= For Black in 5 ================================");
		for(Entry<Board, Move[]> b: board.afterBlackMoves(5).entrySet()){
			System.out.println(b.getKey().toString());
			System.out.println("after "+Arrays.toString(b.getValue()));
			System.out.println();
		}
		
		board = Board.cleanBoard.afterMove(new Move(15, Board.blackbar, 4, true, false));
		System.out.println("= From  =========================================");
		System.out.println(board.toString());
		System.out.println("= For Black in 5 ================================");
		for(Entry<Board, Move[]> b: board.afterBlackMoves(5).entrySet()){
			System.out.println(b.getKey().toString());
			System.out.println("after "+Arrays.toString(b.getValue()));
			System.out.println();
		}

		board = Board.cleanBoard.afterMove(
				new Move(10, Board.blackbar, 9, true, false),
				new Move(5, Board.blackbar, 13, true, false),
				new Move(1, Board.redbar, 11, false, false));
		System.out.println("= From  =========================================");
		System.out.println(board.toString());
		System.out.println("= For Black in 2 ================================");
		for(Entry<Board, Move[]> b: board.afterBlackMoves(2).entrySet()){
			System.out.println(b.getKey().toString());
			System.out.println("after "+Arrays.toString(b.getValue()));
			System.out.println();
		}
		
		board = Board.cleanBoard.afterMove(
				new Move(14, Board.blackbar, 1, true, false),
				new Move(1, Board.blackbar, Board.blackBearOff, true, false));
		System.out.println("= From  =========================================");
		System.out.println(board.toString());
		System.out.println("= For Black in 2 ================================");
		for(Entry<Board, Move[]> b: board.afterBlackMoves(2).entrySet()){
			System.out.println(b.getKey().toString());
			System.out.println("after "+Arrays.toString(b.getValue()));
			System.out.println();
		}
	}
	
	public void testRedMove() {
		Board board = Board.startBoard; 

		System.out.println("= From  =========================================");
		System.out.println(board.toString());
		System.out.println("= For Red in 3 ================================");
		for(Entry<Board, Move[]> b: board.afterRedMoves(3).entrySet()){
			System.out.println(b.getKey().toString());
			System.out.println("after "+Arrays.toString(b.getValue()));
			System.out.println();
		}


		board = Board.cleanBoard.afterMove(new Move(Board.redbar, 12, false, false));
		System.out.println("= From  =========================================");
		System.out.println(board.toString());
		System.out.println("= For Red in 3 ================================");
		for(Entry<Board, Move[]> b: board.afterRedMoves(3).entrySet()){
			System.out.println(b.getKey().toString());
			System.out.println("after "+Arrays.toString(b.getValue()));
			System.out.println();
		}

		board = Board.cleanBoard.afterMove(new Move(10, Board.redbar, 21, false, false),new Move(5, Board.redbar, 19, false, false));
		System.out.println("= From  =========================================");
		System.out.println(board.toString());
		System.out.println("= For Red in 4 ================================");
		for(Entry<Board, Move[]> b: board.afterRedMoves(4).entrySet()){
			System.out.println(b.getKey().toString());
			System.out.println("after "+Arrays.toString(b.getValue()));
			System.out.println();
		}
		
		board = Board.cleanBoard.afterMove(new Move(10, Board.redbar, 21, false, false),new Move(5, Board.redbar, 19, false, false));
		System.out.println("= From  =========================================");
		System.out.println(board.toString());
		System.out.println("= For Red in 5 ================================");
		for(Entry<Board, Move[]> b: board.afterRedMoves(5).entrySet()){
			System.out.println(b.getKey().toString());
			System.out.println("after "+Arrays.toString(b.getValue()));
			System.out.println();
		}
		
		board = Board.cleanBoard.afterMove(new Move(15, Board.redbar, 21, false, false));
		System.out.println("= From  =========================================");
		System.out.println(board.toString());
		System.out.println("= For Red in 5 ================================");
		for(Entry<Board, Move[]> b: board.afterRedMoves(5).entrySet()){
			System.out.println(b.getKey().toString());
			System.out.println("after "+Arrays.toString(b.getValue()));
			System.out.println();
		}
	}
	
	
	
}
