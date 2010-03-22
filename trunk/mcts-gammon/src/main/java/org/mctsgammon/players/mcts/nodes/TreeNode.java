package org.mctsgammon.players.mcts.nodes;

public abstract class TreeNode<T> implements ITreeNode<T> {

	protected int nbSamples;

	public abstract int mcts();

	public abstract double getEV();
	
	public final int getNbSamples() {
		return nbSamples;
	}	
	
	public String getStats() {
		return getEV()+" ("+getNbSamples()+")";
	}
	
}
