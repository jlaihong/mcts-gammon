/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package org.mctsgammon.players.mcts;

import java.util.List;

import org.mctsgammon.Player;
import org.mctsgammon.players.mcts.nodes.IChanceOrLeafNode;
import org.mctsgammon.players.mcts.nodes.MaxMinNode;
import org.mctsgammon.states.MoveState;
import org.mctsgammon.states.ThrowState;

abstract class MCTSPlayer implements Player{

	protected boolean isBlack;
	
	public ThrowState chooseMove(MoveState state) {
		
		this.isBlack = state.isBlackTurn;
		
		List<IChanceOrLeafNode> states = doMCTS(state).getChildren();
		
		if(states.size()==1) return states.get(0).getThrowState();
		
		double maxAppreciation = Double.NEGATIVE_INFINITY;
		IChanceOrLeafNode best = null;
		for(IChanceOrLeafNode child:states){
			System.out.println(" |__o "+child.getStats());
			double appreciation = child.getEV();
			if(appreciation>maxAppreciation){
				maxAppreciation = appreciation;
				best = child;
			}
		}
		System.out.println(this+" predicts "+best.getStats());
		return best.getThrowState();
	}
	
	public abstract MaxMinNode<?> doMCTS(MoveState state);
	
	@Override
	public void onMove(Player actor, MoveState from, ThrowState to) {
	}

	@Override
	public void onThrow(Player actor, ThrowState from, MoveState to) {
	}
}
