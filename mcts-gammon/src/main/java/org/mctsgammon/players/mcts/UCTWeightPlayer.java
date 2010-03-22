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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.mctsgammon.players.mcts.nodes.ChanceNode;
import org.mctsgammon.players.mcts.nodes.IChanceOrLeafNode;
import org.mctsgammon.players.mcts.nodes.LeafNode;
import org.mctsgammon.players.mcts.nodes.MaxMinNode;
import org.mctsgammon.states.MoveState;
import org.mctsgammon.states.ThrowState;

/**
 * Standard UCT MCTS player.
 *  - Selection: UCT
 *  - Simulation: uniform
 *  - Backpropagation: sample-weighted
 *  
 * @author Guy Van den Broeck
 */
public class UCTWeightPlayer extends MCTSPlayer {

	private final static Random r = new Random();

	private final int time;
	private final double C;

	public UCTWeightPlayer(int time, double C) {
		this.time = time;
		this.C = C;
	}

	@Override
	public String toString() {
		return "UCTWeightPlayer";
	}

	@Override
	public UCTMaxMinNode doMCTS(MoveState state) {
		long endTime = System.currentTimeMillis()+time;
		UCTMaxMinNode root = new UCTMaxMinNode(state);
		if(root.getChildren().size()>1){
			do{
				root.rootMcts();
			}while(System.currentTimeMillis()<endTime);
		}
		System.out.println(this+" performed "+root.getNbSamples()+" iterations.");
		return root;
	}

	private class UCTChanceNode extends ChanceNode<Double>{

		public UCTChanceNode(ThrowState throwState) {
			super(throwState);
		}

		double evSum = 0;
		
		@Override
		protected void update(int reward) {
			evSum += reward;
		}

		@Override
		public double getEV() {
			return evSum/nbSamples;
		}

		@Override
		protected MaxMinNode<Double> createMaxMinNode(MoveState moveState) {
			return new UCTMaxMinNode(moveState);
		}
	}

	private class UCTLeafState extends LeafNode<Double>{

		public UCTLeafState(ThrowState throwState) {
			super(throwState);
		}

		@Override
		protected boolean botIsBlack() {
			return UCTWeightPlayer.this.isBlack;
		}

	}

	private class UCTMaxMinNode extends MaxMinNode<Double>{

		public UCTMaxMinNode(MoveState moveState) {
			super(moveState);
		}

		/**
		 * UCT
		 */
		protected IChanceOrLeafNode<Double> select(){
			List<IChanceOrLeafNode<Double>> children = getChildren();
			double max = Double.NEGATIVE_INFINITY;
			IChanceOrLeafNode<Double> maxChild = null;
			for(int i=0;i<children.size();i++){
				IChanceOrLeafNode<Double> child = children.get(i);
				if(child.getNbSamples()==0) {
					// pick random unsampled child
					ArrayList<IChanceOrLeafNode<Double>> unsampledChildren = 
						new ArrayList<IChanceOrLeafNode<Double>>(children.size());
					for(;i<children.size();i++){
						if(children.get(i).getNbSamples()==0) unsampledChildren.add(children.get(i));
					}
					return unsampledChildren.get(r.nextInt(unsampledChildren.size()));
				}
				int sign = (moveState.isBlackTurn==UCTWeightPlayer.this.isBlack)? 1:-1;
				double uct = sign*child.getEV() + 
					C*Math.sqrt(Math.log(Math.max(nbSamples,child.getNbSamples()))/child.getNbSamples());
				if(uct> max){
					max = uct;
					maxChild = child;
				}
			}
			return maxChild;
		}

		protected IChanceOrLeafNode<Double> createChanceOrLeafNode(ThrowState throwState) {
			IChanceOrLeafNode<Double> cached;
			if(throwState.board.isGameOver()){
				cached = new UCTLeafState(throwState);
			}else{
				cached = new UCTChanceNode(throwState);
			}
			return cached;
		}

		double evSum = 0;
		
		@Override
		protected void update(int reward) {
			evSum += reward;
		}

		@Override
		public double getEV() {
			return evSum/nbSamples;
		}

	}

}
