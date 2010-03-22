package org.mctsgammon.players.mcts.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.mctsgammon.states.MoveState;
import org.mctsgammon.states.ThrowState;


public abstract class ChanceNode<T> extends InteriorNode<T> implements IChanceOrLeafNode<T>{
	
	private final static Random r = new Random();
	private final ThrowState throwState;
	
	public ChanceNode(ThrowState throwState) {
		this.throwState = throwState;
	}
	
	private List<MaxMinNode<T>> children = null;
	protected double[] cumulProb = null;
	protected double totalprob = 0;
	
	public final List<MaxMinNode<T>> getChildren() {
		if(children==null){
			MoveState[] moveStates = throwState.getChildren();
			children = new ArrayList<MaxMinNode<T>>(moveStates.length);
			cumulProb = new double[moveStates.length];
			for(int i=0;i<moveStates.length;i++){
				MaxMinNode<T> newChild = createMaxMinNode(moveStates[i]);
				children.add(newChild);
				totalprob += newChild.moveState.diceThrow.prob;
				cumulProb[i] = totalprob;
			}
		}
		return children;
	}

	public final List<MaxMinNode<T>> getChildrenOrNull(){
		return children;
	}
	
	protected abstract MaxMinNode<T> createMaxMinNode(MoveState moveState);

	/**
	 * Default selection function that samples according to the probability of 
	 * the dice throw
	 */
	protected MaxMinNode<T> select() {
		getChildren();
		int index = Arrays.binarySearch(cumulProb, r.nextDouble());
		if(index<0) index = -index-1; //see binarySearch doc
		List<MaxMinNode<T>> children = getChildren();
		if(index == children.size()) return children.get(index-1);
		else return children.get(index);
	}
	
	@Override
	public ThrowState getThrowState() {
		return throwState;
	}
	
}
