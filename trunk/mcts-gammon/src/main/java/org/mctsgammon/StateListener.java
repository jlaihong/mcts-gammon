package org.mctsgammon;

public interface StateListener {

	public void onMove(Player actor, MoveState from,ThrowState to);
	public void onThrow(Player actor, ThrowState from, MoveState to);
	
}
