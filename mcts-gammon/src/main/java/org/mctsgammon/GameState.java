package org.mctsgammon;

public class GameState {

	public final Board board;
	public final boolean isBlackTurn;
	
	public GameState(Board board, boolean isBlackTurn) {
		this.board = board;
		this.isBlackTurn = isBlackTurn;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((board == null) ? 0 : board.hashCode());
		result = prime * result + (isBlackTurn ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof GameState))
			return false;
		GameState other = (GameState) obj;
		if (board == null) {
			if (other.board != null)
				return false;
		} else if (!board.equals(other.board))
			return false;
		if (isBlackTurn != other.isBlackTurn)
			return false;
		return true;
	}
	
	
	
}
