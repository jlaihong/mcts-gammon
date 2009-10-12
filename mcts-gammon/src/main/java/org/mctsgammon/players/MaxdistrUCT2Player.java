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
package org.mctsgammon.players;

import java.util.ArrayList;
import java.util.Random;

import org.mctsgammon.Board;
import org.mctsgammon.DiceThrow;
import org.mctsgammon.MoveState;
import org.mctsgammon.Player;
import org.mctsgammon.ThrowState;
import org.mctsgammon.util.Gaussian;

public class MaxdistrUCT2Player implements Player {

	private final static Random r = new Random();

	private final int time;
	private boolean isBlack;

	private final double avgNbSamplesBeforeMax;

	private final double e;

	public MaxdistrUCT2Player(int time, double avgNbSamplesBeforeMax, double e) {
		this.time = time;
		this.avgNbSamplesBeforeMax = avgNbSamplesBeforeMax;
		this.e = e;
	}

	@Override
	public ThrowState chooseMove(MoveState state) {
		this.isBlack = state.isBlackTurn;
		long endTime = System.currentTimeMillis()+time;
		UCTMoveState root = new UCTMoveState(state.board, state.diceThrow, state.isBlackTurn);
		ThrowState[] states = root.getChildren();

		if(states.length==1) return states[0];
		do{
			root.rootMcts();
		}while(System.currentTimeMillis()<endTime);
		System.out.println(this+" performed "+root.nbSamples+" iterations.");

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
	public String toString() {
		return "MaxGaussianUCT2Player";
	}

	private class UCTThrowState extends ThrowState{

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
			MoveState[] children = getChildren();
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
			double var = sigmaMuTot/nbSamples-mean*mean;
			ev = new Gaussian(mean, var/nbSamples);
		}

	}

	private class UCTLeafState extends UCTThrowState{

		private final int fixedReward;

		public UCTLeafState(Board board, boolean isBlackTurn) {
			super(board, isBlackTurn);
			boolean botWins = MaxdistrUCT2Player.this.isBlack? board.blackWins():board.redWins();
			fixedReward = botWins? board.getProfit():-board.getProfit();
			ev = new Gaussian(fixedReward,0);
		}

		public int mcts() {
			++nbSamples;
			return fixedReward;
		}

	}

	private class UCTMoveState extends MoveState{

		Gaussian ev = null;
		int nbSamples;

		public UCTMoveState(Board board, DiceThrow diceThrow, boolean isBlackTurn) {
			super(board, diceThrow, isBlackTurn);
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
			}else if(isBlackTurn==MaxdistrUCT2Player.this.isBlack){
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
			if(isBlackTurn==MaxdistrUCT2Player.this.isBlack){
				return selectMax();
			}else{
				return selectMin();
			}

		}

		private UCTThrowState selectMax(){
			ThrowState[] children = getChildren();
			Gaussian maxEv = null;
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
				if(maxEv == null || maxEv.mean < child.ev.mean){
					maxEv = child.ev;
				}
			}
			double[] weights = new double[children.length];
			double weightSum = 0;
			for(int i=0;i<children.length;i++){
				UCTThrowState child = (UCTThrowState)children[i];
				double prob = (maxEv.mean==child.ev.mean) ? 1
						: Math.exp(-2.4*(maxEv.mean-child.ev.mean)/Math.sqrt(2*maxEv.variance+child.ev.variance));
				weights[i] = prob+e;
				weightSum += weights[i];
			}
			double random = r.nextDouble()*weightSum;
			double cumulWeight = 0;
			for(int i=0;i<children.length;i++){
				cumulWeight += weights[i];
				if(random<cumulWeight) return (UCTThrowState)children[i];
			}
			throw new IllegalStateException("Bad roundoff?");
		}
		
		private UCTThrowState selectMin(){
			ThrowState[] children = getChildren();
			Gaussian minEv = null;
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
				if(minEv == null || minEv.mean > child.ev.mean){
					minEv = child.ev;
				}
			}
			double[] weights = new double[children.length];
			double weightSum = 0;
			for(int i=0;i<children.length;i++){
				UCTThrowState child = (UCTThrowState)children[i];
				double prob = (minEv.mean==child.ev.mean) ? 1
						: Math.exp(-2.4*(child.ev.mean-minEv.mean)/Math.sqrt(2*minEv.variance+child.ev.variance));
				weights[i] = prob+e;
				weightSum += weights[i];
			}
			double random = r.nextDouble()*weightSum;
			double cumulWeight = 0;
			for(int i=0;i<children.length;i++){
				cumulWeight += weights[i];
				if(random<cumulWeight) return (UCTThrowState)children[i];
			}
			throw new IllegalStateException("Bad roundoff?");
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

	}
	
	static int nbMix, nbMax, nbMin;

	@Override
	public void onMove(Player actor, MoveState from, ThrowState to) {
	}

	@Override
	public void onThrow(Player actor, ThrowState from, MoveState to) {
	}
}
