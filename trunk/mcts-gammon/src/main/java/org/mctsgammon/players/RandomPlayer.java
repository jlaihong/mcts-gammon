package org.mctsgammon.players;

import java.util.Random;

import org.mctsgammon.MoveState;
import org.mctsgammon.Player;
import org.mctsgammon.ThrowState;

public class RandomPlayer implements Player {

	private static Random r = new Random();
	
	@Override
	public ThrowState chooseMove(MoveState state) {
		ThrowState[] states = state.getChildren();
		return states[r.nextInt(states.length)];
	}
	
	@Override
	public String toString() {
		return "RandomPlayer";
	}
	
	@Override
	public void onMove(Player actor, MoveState from, ThrowState to) {
	}

	@Override
	public void onThrow(Player actor, ThrowState from, MoveState to) {
	}

}
