package org.mctsgammon;

import java.util.LinkedList;
import java.util.List;

public class Game {

	private MoveState moveState;
	private ThrowState throwState;
	
	private final Player black;
	private final Player red;
	
	public Game(Player black, Player red) {
		this(null, null, black, red);
	}
	
	public Game(MoveState startMove, ThrowState startThrow ,Player black,Player red) {
		if(startMove!=null && startThrow!=null) throw new IllegalArgumentException("Can't have 2 start states");
		this.moveState = startMove;
		this.throwState = startThrow;
		this.black = black;
		this.red = red;
		addStateListener(black);
		addStateListener(red);
	}
	
	public void play() {
		while(true){
			if(moveState!=null){
				// must make move
			    Player p = moveState.isBlackTurn? black: red;
			    throwState = p.chooseMove(moveState);
			    MoveState oldMoveState = moveState;
			    moveState = null;
			    for(StateListener listener: stateListeners){
			    	listener.onMove(p, oldMoveState, throwState);
			    }
			    if(throwState.board.isGameOver()) {
					int amount = throwState.board.getProfit();
					for(WinnerListener listener: winnerListeners) {
						listener.onWinner(p, amount);
					}
			    	return;
			    }
			}else if(throwState!=null){
				// must roll dice
			    moveState = throwState.getRandomChild();
			    ThrowState oldThrowState = throwState;
			    throwState = null;
				Player p = oldThrowState.isBlackTurn? black: red;
			    for(StateListener listener: stateListeners) {
					listener.onThrow(p, oldThrowState, moveState);
				} 
			}else{
				moveState = MoveState.getRandomStartState();
			}
		}
	}
	
	private List<WinnerListener> winnerListeners = new LinkedList<WinnerListener>();
	private List<StateListener> stateListeners = new LinkedList<StateListener>();

	public void addWinListener(WinnerListener listener){
		winnerListeners.add(listener);
	}
	
	public void addStateListener(StateListener listener){
		stateListeners.add(listener);
	}
	
	public Board getBoard() {
		if(moveState!=null) return moveState.board;
		else return throwState.board;
	}
	
}
