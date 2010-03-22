package org.mctsgammon.players.mcts.nodes;

import java.util.ArrayList;
import java.util.List;

import org.mctsgammon.states.MoveState;
import org.mctsgammon.states.ThrowState;

public abstract class MaxMinNode<T> extends InteriorNode<T>{

	public final MoveState moveState;

	public MaxMinNode(MoveState moveState) {
		this.moveState = moveState;
	}
	
	private List<IChanceOrLeafNode<T>> children = null;
	
	public final List<IChanceOrLeafNode<T>> getChildren() {
		if(children==null){
			ThrowState[] throwStates = moveState.getChildren();
			children = new ArrayList<IChanceOrLeafNode<T>>(throwStates.length);
			for(ThrowState throwState:throwStates){
				children.add(createChanceOrLeafNode(throwState));
			}
		}
		return children;
	}
	
	public final List<IChanceOrLeafNode<T>> getChildrenOrNull() {
		return children;
	}

	protected abstract IChanceOrLeafNode<T> createChanceOrLeafNode(ThrowState throwState);

}
