package org.mctsgammon;

public abstract class MCTSMoveState extends MoveState {

	public MCTSMoveState(Board board, DiceThrow diceThrow, boolean isBlackTurn) {
		super(board, diceThrow, isBlackTurn);
	}

	public abstract int getNbSamples();
	
	public abstract double getEV();

	
}
