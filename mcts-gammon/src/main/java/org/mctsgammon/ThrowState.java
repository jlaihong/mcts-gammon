package org.mctsgammon;
import java.util.Random;


public class ThrowState extends GameState{
	
	private final static Random r = new Random();
	
	public final static DiceThrow[] diceThrows = DiceThrow.values();

	private MoveState[] children = null;

	public ThrowState(Board board, boolean isBlackTurn) {
		super(board, isBlackTurn);
	}

	public MoveState[] getChildren() {
		if(children==null){
			children = new MoveState[diceThrows.length]; 
			for(int i=0;i<diceThrows.length;i++){
				children[i] = createMoveState(board, diceThrows[i], isBlackTurn);
			}
		}
		return children;
	}
	
	protected MoveState createMoveState(Board board, DiceThrow diceThrow, boolean isBlackTurn) {
		return new MoveState(board, diceThrow, isBlackTurn);
	}

	public MoveState getRandomChild(){
		getChildren();
		return children[r.nextInt(children.length)];
	}

}
