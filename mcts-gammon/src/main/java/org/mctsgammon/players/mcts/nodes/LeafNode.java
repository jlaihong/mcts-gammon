package org.mctsgammon.players.mcts.nodes;

import java.util.Collections;
import java.util.List;

import org.mctsgammon.states.ThrowState;


public abstract class LeafNode<T> extends TreeNode<T> implements IChanceOrLeafNode<T>{
	
	public final int fixedReward;
	public final ThrowState throwState;

	public LeafNode(ThrowState throwState) {
		this.throwState = throwState;
		boolean botWins = botIsBlack() ? throwState.board.blackWins():throwState.board.redWins();
		fixedReward = botWins? throwState.board.getProfit(): -throwState.board.getProfit();
	}

	protected abstract boolean botIsBlack();

	@Override
	public double getEV() {
		return fixedReward;
	}

	public int mcts() {
		++nbSamples;
		return fixedReward;
	}

	public List<MaxMinNode<T>> getChildrenOrNull(){
		return Collections.emptyList();
	}

	@Override
	public ThrowState getThrowState() {
		return throwState;
	}
}
