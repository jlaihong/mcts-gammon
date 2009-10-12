package org.mctsgammon;

public abstract class MCTSThrowState extends ThrowState {

	public MCTSThrowState(Board board, boolean isBlackTurn) {
		super(board, isBlackTurn);
	}
	
	public abstract int getNbSamples();
	
	public abstract double getEV();
	
}
