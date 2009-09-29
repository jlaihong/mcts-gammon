package org.mctsgammon;

public interface Player extends StateListener{

	public ThrowState chooseMove(MoveState state);
	
}
