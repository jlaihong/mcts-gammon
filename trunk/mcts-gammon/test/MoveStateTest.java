import java.util.Arrays;
import java.util.Map.Entry;

import org.mctsgammon.Board;
import org.mctsgammon.DiceThrow;
import org.mctsgammon.Move;
import org.mctsgammon.MoveState;

import junit.framework.TestCase;


public class MoveStateTest extends TestCase {

	public void testDoubleBlackMove() {
		Board board = Board.startBoard; 

		System.out.println("= From  =========================================");
		System.out.println(board.toString());
		System.out.println("= For 2,2 ================================");
		MoveState state = new MoveState(board, DiceThrow._22, true);
		for(Entry<Board, Move[]> b: MoveState.reduce(state.getPossibleStates()).entrySet() ){
			System.out.println(b.getKey().toString());
			System.out.println("after "+Arrays.toString(b.getValue()));
			System.out.println();
		}
	}
	
	public void testSingleBlackMove() {
		Board board = Board.startBoard; 

		System.out.println("= From  =========================================");
		System.out.println(board.toString());
		System.out.println("= For 2,5 ================================");
		MoveState state = new MoveState(board, DiceThrow._25, true);
		for(Entry<Board, Move[]> b: MoveState.reduce(state.getPossibleStates()).entrySet() ){
			System.out.println(b.getKey().toString());
			System.out.println("after "+Arrays.toString(b.getValue()));
			System.out.println();
		}

	}
	
	
	
}
