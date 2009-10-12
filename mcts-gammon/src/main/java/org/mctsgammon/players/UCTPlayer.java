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
import org.mctsgammon.MCTSMoveState;
import org.mctsgammon.MCTSPlayer;
import org.mctsgammon.MCTSThrowState;
import org.mctsgammon.MoveState;
import org.mctsgammon.Player;
import org.mctsgammon.ThrowState;

public class UCTPlayer implements MCTSPlayer {

	private final static Random r = new Random();

	private final int time;
	private final double C;

	private boolean isBlack;

	public UCTPlayer(int time, double C) {
		this.time = time;
		this.C = C;
	}

	//	int maxBF = -1;
	//	MoveState maxBFState;
	//	double avgBFSum;
	//	int nbBFs;

	@Override
	public void onMove(Player actor, MoveState from, ThrowState to) {
	}

	@Override
	public void onThrow(Player actor, ThrowState from, MoveState to) {
	}

	public ThrowState chooseMove(MoveState state) {
		ThrowState[] states = doMCTS(state);

		double maxAppreciation = Double.NEGATIVE_INFINITY;
		UCTThrowState best = null;
		for(ThrowState tchild:states){
			UCTThrowState child = (UCTThrowState)tchild;
			System.out.println(" |__o "+(float)(child.evSum/child.nbSamples)+" ("+child.nbSamples+")");
			double appreciation = child.evSum/child.nbSamples;
			if(appreciation>maxAppreciation){
				maxAppreciation = appreciation;
				best = child;
			}
		}
		System.out.println(this+" predicts "+best.evSum/best.nbSamples);
		return best;
	}

	public ThrowState[] doMCTS(MoveState state) {
		long endTime = System.currentTimeMillis()+time;
		this.isBlack = state.isBlackTurn;
		UCTMoveState root = new UCTMoveState(state.board, state.diceThrow, state.isBlackTurn);
		ThrowState[] states = root.getChildren();
		//		if(states.length>maxBF){
		//			maxBF = states.length;
		//			maxBFState = state;
		//		}
		//		avgBFSum += states.length;
		//		nbBFs ++;
		//		System.out.println("Max BF="+maxBF);
		//		System.out.println("For "+maxBFState.board+"\n with dice "+maxBFState.diceThrow);
		//		System.out.println("Avg BF="+(avgBFSum/nbBFs));

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
		return "UCTPlayer";
	}

	private class UCTThrowState extends MCTSThrowState{

		double evSum = 0;
		int nbSamples;

		public UCTThrowState(Board board, boolean isBlackTurn) {
			super(board, isBlackTurn);
		}

		@Override
		protected MoveState createMoveState(Board board, DiceThrow diceThrow,
				boolean isBlackTurn) {
			return new UCTMoveState(board, diceThrow, isBlackTurn);
		}

		public double mcts() {
			UCTMoveState child  = (UCTMoveState)getRandomChild();
			double reward = child.mcts();
			evSum += reward;
			++nbSamples;
			return reward;
		}

		@Override
		public double getEV() {
			return evSum/nbSamples;
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
			boolean botWins = UCTPlayer.this.isBlack? board.blackWins():board.redWins();
			fixedReward = botWins? board.getProfit():-board.getProfit();
		}

		public double mcts() {
			evSum += fixedReward;
			++nbSamples;
			return fixedReward;
		}

	}

	private class UCTMoveState extends MCTSMoveState{

		double evSum = 0;
		int nbSamples;

		public UCTMoveState(Board board, DiceThrow diceThrow, boolean isBlackTurn) {
			super(board, diceThrow, isBlackTurn);
		}	

		@Override
		public double getEV() {
			return evSum/nbSamples;
		}

		@Override
		public int getNbSamples() {
			return nbSamples;
		}

		void rootMcts() {
			UCTThrowState child  = select();
			child.mcts();
			++nbSamples;
		}

		public double mcts() {
			UCTThrowState child  = select();
			double reward = child.mcts();
			evSum += reward;
			++nbSamples;
			return reward;
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
				int sign = (isBlackTurn==UCTPlayer.this.isBlack)? 1:-1;
				double uct = sign*child.evSum/child.nbSamples + C*Math.sqrt(Math.log(Math.max(nbSamples,child.nbSamples))/child.nbSamples);
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

	}

}
