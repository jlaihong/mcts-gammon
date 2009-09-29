package org.mctsgammon.players;

import java.util.ArrayList;
import java.util.Random;

import org.mctsgammon.Board;
import org.mctsgammon.DiceThrow;
import org.mctsgammon.MoveState;
import org.mctsgammon.Player;
import org.mctsgammon.ThrowState;

public class MaxUCTPlayer implements Player {

	private final static Random r = new Random();

	private final int time;
	private final double C;

	private boolean isBlack;

	public MaxUCTPlayer(int time, double C) {
		this.time = time;
		this.C = C;
	}

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
			System.out.println(" |__o "+(float)(child.getEV())+" ("+child.nbSamples+")");
			double appreciation = child.getEV();
			if(appreciation>maxAppreciation){
				maxAppreciation = appreciation;
				best = child;
			}
		}
		System.out.println(this+" predicts "+best.getEV());
		ThrowState throwState = new ThrowState(best.board, best.isBlackTurn);
		return throwState;
	}

	@Override
	public String toString() {
		return "UCTPlayer";
	}

	private class UCTThrowState extends ThrowState{

		int[] distribution = new int[7];
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
			++distribution[3+reward];
			++nbSamples;
			return reward;
		}

		public double getEV() {
			double ev = distribution[0]*-3 + distribution[1]*-2 + distribution[2]*-1 +
						distribution[4]*1 + distribution[5]*2 + distribution[6]*3;
			return ev/nbSamples;
		}

	}

	private class UCTLeafState extends UCTThrowState{

		private final int fixedReward;

		public UCTLeafState(Board board, boolean isBlackTurn) {
			super(board, isBlackTurn);
			boolean botWins = MaxUCTPlayer.this.isBlack? board.blackWins():board.redWins();
			fixedReward = botWins? board.getProfit():-board.getProfit();
		}

		public int mcts() {
			++distribution[3+fixedReward];
			++nbSamples;
			return fixedReward;
		}

	}

	private class UCTMoveState extends MoveState{

		int[] distribution = new int[7];
		int nbSamples;

		public UCTMoveState(Board board, DiceThrow diceThrow, boolean isBlackTurn) {
			super(board, diceThrow, isBlackTurn);
		}

		public int mcts() {
			UCTThrowState child  = select();
			int reward = child.mcts();
			++distribution[3+reward];
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
				int sign = (isBlackTurn==MaxUCTPlayer.this.isBlack)? 1:-1;
				double uct = sign*child.getEV() + C*Math.sqrt(Math.log(Math.max(nbSamples,child.nbSamples))/child.nbSamples);
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
