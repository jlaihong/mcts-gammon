package org.mctsgammon.players.mcts.nodes;

public interface ITreeNode<T> {

	public abstract int mcts();

	public abstract double getEV();

	public abstract int getNbSamples();

	public abstract String getStats();

}