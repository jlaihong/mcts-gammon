package org.mctsgammon.players.mcts.nodes;

import java.util.List;

import org.mctsgammon.states.ThrowState;

public interface IChanceOrLeafNode<T> extends ITreeNode<T>{

	ThrowState getThrowState();

	List<MaxMinNode<T>> getChildrenOrNull();

}
