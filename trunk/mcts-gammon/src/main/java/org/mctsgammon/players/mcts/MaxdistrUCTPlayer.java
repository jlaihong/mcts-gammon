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
import java.util.Random;

import org.mctsgammon.Board;
import org.mctsgammon.DiceThrow;
import org.mctsgammon.Player;
import org.mctsgammon.players.mcts.nodes.ChanceNode;
import org.mctsgammon.players.mcts.nodes.MaxMinNode;
import org.mctsgammon.states.MoveState;
import org.mctsgammon.states.ThrowState;
import org.mctsgammon.util.Gaussian;

public class MaxdistrUCTPlayer implements MCTSPlayer {

	private final static Random r = new Random();

	private final int time;
	private final double C;

	private boolean isBlack;

	private final double avgNbSamplesBeforeMax;

	public MaxdistrUCTPlayer(int time, double C, double avgNbSamplesBeforeMax) {
		this.time = time;
		this.C = C;
		this.avgNbSamplesBeforeMax = avgNbSamplesBeforeMax;
	}

	@Override
	public ThrowState chooseMove(MoveState state) {
		ThrowState[] states = doMCTS(state);

		double maxAppreciation = Double.NEGATIVE_INFINITY;
		UCTThrowState best = null;
		for(ThrowState tchild:states){
			UCTThrowState child = (UCTThrowState)tchild;
			System.out.println(" |__o "+child.ev.toString()+" ("+child.nbSamples+")");
			double appreciation = child.ev.mean;
			if(appreciation>maxAppreciation){
				maxAppreciation = appreciation;
				best = child;
			}
		}
		System.out.println(this+" predicts "+best.ev);
		System.out.println("NbMix: "+nbMix+" NbMax: "+nbMax+" NbMin: "+nbMin);
		return best;
	}

	@Override
	public ThrowState[] doMCTS(MoveState state) {
		this.isBlack = state.isBlackTurn;
		long endTime = System.currentTimeMillis()+time;
		UCTMoveState root = new UCTMoveState(state.board, state.diceThrow, state.isBlackTurn);
		ThrowState[] states = root.getChildren();

		if(states.length>1){
			do{
				root.rootMcts();
			}while(System.currentTimeMillis()<endTime);
		}
		System.out.println(this+" performed "+root.nbSamples+" iterations.");
		return states;
	}

	@Override
	public String toString() {
		return "MaxGaussianPlayer";
	}

	private class UCTThrowState extends ChanceNode{

		Gaussian ev = null;
		int nbSamples;

		public UCTThrowState(Board board, boolean isBlackTurn) {
			super(board, isBlackTurn);
		}

		@Override
		protected MoveState createMoveState(Board board, DiceThrow diceThrow,
				boolean isBlackTurn) {
			return new UCTMoveState(board, diceThrow, isBlackTurn);
		}

		public int mcts() {
			UCTMoveState child  = (UCTMoveState)getRandomChild();
			int reward = child.mcts();
			++nbSamples;
			update();
			return reward;
		}

		private void update() {
			MoveState[] children = getOrMakeChildren();
			double meanTot = 0;
			double sigmaMuTot = 0;
			for(MoveState child: children){
				UCTMoveState uctChild = (UCTMoveState)child;
				if(uctChild.nbSamples>0){
					meanTot += uctChild.nbSamples*uctChild.ev.mean;
					double rewardVar = uctChild.ev.variance*uctChild.nbSamples;
					sigmaMuTot += uctChild.nbSamples*(rewardVar+uctChild.ev.mean*uctChild.ev.mean);
				}
			}
			double mean = meanTot/nbSamples;
			double var = Math.max(0, sigmaMuTot/nbSamples-mean*mean);
			ev = new Gaussian(mean, var/nbSamples);
		}

		@Override
		public double getEV() {
			return ev==null? Double.NaN: ev.mean;
		}

		@Override
		public int getNbSamples() {
			return nbSamples;
		}

	}

	private class UCTLeafState extends UCTThrowState{

		private final int fixedReward;

		public UCTLeafState(Board board, boolean isBlackTurn) {
			super(board, isBlackTurn);
			boolean botWins = MaxdistrUCTPlayer.this.isBlack? board.blackWins():board.redWins();
			fixedReward = botWins? board.getProfit():-board.getProfit();
			ev = new Gaussian(fixedReward,0);
		}

		public int mcts() {
			++nbSamples;
			return fixedReward;
		}

	}

	private class UCTMoveState extends MaxMinNode{

		Gaussian ev = null;
		int nbSamples;

		public UCTMoveState(MoveState moveState) {
			super(moveState);
		}

		void rootMcts() {
			UCTThrowState child  = select();
			child.mcts();
			++nbSamples;
		}
		
		public int mcts() {
			UCTThrowState child  = select();
			int reward = child.mcts();
			++nbSamples;
			update();
			return reward;
		}

		private void update() {
			ThrowState[] children = getChildren();
			if(nbSamples<avgNbSamplesBeforeMax*children.length){
				//resort to mixture mode propagation
				nbMix++;
				double meanTot = 0;
				double sigmaMuTot = 0;
				for(ThrowState child: children){
					UCTThrowState uctChild = (UCTThrowState)child;
					if(uctChild.nbSamples>0){
						meanTot += uctChild.nbSamples*uctChild.ev.mean;
						double rewardVar = uctChild.ev.variance*uctChild.nbSamples;
						sigmaMuTot += uctChild.nbSamples*(rewardVar+uctChild.ev.mean*uctChild.ev.mean);
					}
				}
				double mean = meanTot/nbSamples;
				double var = sigmaMuTot/nbSamples-mean*mean;
				ev = new Gaussian(mean, var/nbSamples);
			}else if(isBlackTurn==MaxdistrUCTPlayer.this.isBlack){
				//take maximum distribution in program decision nodes!
				nbMax++;
				Gaussian[] gaussians = new Gaussian[children.length];
				for(int i=0;i<gaussians.length;i++){
					gaussians[i] = ((UCTThrowState)children[i]).ev;
				}
				ev = Gaussian.maxOf(gaussians);
			}else{
				//take minimum distribution in opponent nodes!
				nbMin++;
				Gaussian[] gaussians = new Gaussian[children.length];
				for(int i=0;i<gaussians.length;i++){
					gaussians[i] = ((UCTThrowState)children[i]).ev.min();
				}
				ev = Gaussian.maxOf(gaussians).min();
			}
		}

		private UCTThrowState select(){
			ThrowState[] children = getChildren();
			double max = Double.NEGATIVE_INFINITY;
			UCTThrowState maxChild = null;
			for(int i=0;i<children.length;i++){
				UCTThrowState child = (UCTThrowState)children[i];
				if(child.nbSamples==0) {
					// pick random unsampled child
					ArrayList<UCTThrowState> unsampledChildren = new ArrayList<UCTThrowState>(children.length);
					for(;i<children.length;i++){
						child = (UCTThrowState)children[i];
						if(child.nbSamples==0) unsampledChildren.add(child);
					}
					return unsampledChildren.get(r.nextInt(unsampledChildren.size()));
				}
				int sign = (isBlackTurn==MaxdistrUCTPlayer.this.isBlack)? 1:-1;
				double uct = sign*child.ev.mean + C*Math.sqrt(Math.log(Math.max(nbSamples,child.nbSamples))/child.nbSamples);
				if(uct> max){
					max = uct;
					maxChild = child;
				}
			}
			return maxChild;
		}

		@Override
		protected UCTThrowState createThrowState(Board board, boolean isBlackTurn) {
			return createNewThrowState(board, isBlackTurn);
		}

		private UCTThrowState createNewThrowState(Board board,
				boolean isBlackTurn) {
			UCTThrowState cached;
			if(board.isGameOver()){
				cached = new UCTLeafState(board, isBlackTurn);
			}else{
				cached = new UCTThrowState(board, isBlackTurn);
			}
			return cached;
		}

		@Override
		public double getEV() {
			return ev==null? Double.NaN: ev.mean;
		}

		@Override
		public int getNbSamples() {
			return nbSamples;
		}

	}
	
	static int nbMix, nbMax, nbMin;

	@Override
	public void onMove(Player actor, MoveState from, ThrowState to) {
	}

	@Override
	public void onThrow(Player actor, ThrowState from, MoveState to) {
	}
}
