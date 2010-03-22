package org.mctsgammon.players.mcts.nodes;

public abstract class InteriorNode<T> extends TreeNode<T>{
	
	public final int mcts() {
		ITreeNode<T> child = select();
		int reward = child.mcts();
		++nbSamples;
		update(reward);
		return reward;
	}
	
	public final void rootMcts() {
		ITreeNode<T> child = select();
		child.mcts();
		++nbSamples;
	}
	
	protected abstract ITreeNode<T> select();
	
	protected void update(int reward){
		// no op
	}
	
}
