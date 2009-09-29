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

public class UCTPlayer implements Player {

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

	@Override
	public ThrowState chooseMove(MoveState state) {
		this.isBlack = state.isBlackTurn;
		long endTime = System.currentTimeMillis()+time;

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

		if(states.length==1) return states[0];

		int nbIterations = 0;
		do{
			root.mcts();
			++nbIterations;
		}while(System.currentTimeMillis()<endTime);
		System.out.println(this+" performed "+nbIterations+" iterations.");



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
		ThrowState throwState = new ThrowState(best.board, best.isBlackTurn);
		return throwState;
	}

	@Override
	public String toString() {
		return "UCTPlayer";
	}

	private class UCTThrowState extends ThrowState{

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

	private class UCTMoveState extends MoveState{

		double evSum = 0;
		int nbSamples;

		public UCTMoveState(Board board, DiceThrow diceThrow, boolean isBlackTurn) {
			super(board, diceThrow, isBlackTurn);
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
			//			Q q = new Q(board,isBlackTurn);
			//			UCTThrowState cached = cache.get(q);
			//			if(cached==null){
			//				++miss;
			//				cached = createNewThrowState(board, isBlackTurn);
			//				cache.put(q, cached);
			//			}else{
			//				++hit;
			//				System.out.println("Reusing "+cached.nbSamples+" samples");
			//				
			//			}
			//			return cached;
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


	//	private static class Q {
	//		
	//		private final Board b;
	//		private final boolean t;
	//
	//		public Q(Board b, boolean t) {
	//			this.b = b;
	//			this.t = t;
	//		}
	//
	//		@Override
	//		public int hashCode() {
	//			final int prime = 31;
	//			int result = 1;
	//			result = prime * result + ((b == null) ? 0 : b.hashCode());
	//			result = prime * result + (t ? 1231 : 1237);
	//			return result;
	//		}
	//
	//		@Override
	//		public boolean equals(Object obj) {
	//			if (this == obj)
	//				return true;
	//			if (obj == null)
	//				return false;
	//			if (!(obj instanceof Q))
	//				return false;
	//			Q other = (Q) obj;
	//			if (b == null) {
	//				if (other.b != null)
	//					return false;
	//			} else if (!b.equals(other.b))
	//				return false;
	//			if (t != other.t)
	//				return false;
	//			return true;
	//		}
	//		
	//	}
	//
	//	public final Map<Q, UCTThrowState> cache = new MapMaker()
	//		.concurrencyLevel(1)
	//	    .softValues()
	//	    .makeMap();
	//	public int hit, miss;

}
