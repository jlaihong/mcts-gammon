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
package org.mctsgammon;

import java.util.LinkedList;
import java.util.List;

import org.mctsgammon.players.RolloutPlayer;
import org.mctsgammon.players.UCTPlayer;

public class Games implements WinnerListener {

	public final Player black;
	public final Player red;
	public final int nbGames;
	
	private int blackPoints = 0;
	private int redPoints = 0;
	
	public final ThrowState start;

	public Games(ThrowState start, int nbGames, Player black, Player red) {
		this.start = start;
		this.black = black;
		this.red = red;
		this.nbGames = nbGames;
		addWinListener(this);
	}
	
	public Games(int nbGames, Player black, Player red) {
		this(null, nbGames, black, red);
	}
	
	public void play() {
		for(int i=0;i<nbGames;i++){
			Game game = start==null ? new Game(black,red) : new Game(null,start, black, red);
			for(WinnerListener l:winnerListeners){
				game.addWinListener(l);
			}
			for(StateListener l:stateListeners){
				game.addStateListener(l);
			}
			game.play();
		}
	}
	
	@Override
	public void onWinner(Player winner, int amount) {
		if(winner==black) blackPoints+=amount;
		else redPoints += amount;
//		System.out.println((winner==black? "Black":"Red")+" wins "+amount);
	}
	
	public int getBlackPoints() {
		return blackPoints;
	}
	
	public int getRedPoints() {
		return redPoints;
	}	
	
	private List<WinnerListener> winnerListeners = new LinkedList<WinnerListener>();
	private List<StateListener> stateListeners = new LinkedList<StateListener>();

	public void addWinListener(WinnerListener listener){
		winnerListeners.add(listener);
	}
	
	public void addStateListener(StateListener listener){
		stateListeners.add(listener);
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		final int time = Integer.parseInt(args[0]);
		final Player black = new UCTPlayer(time, 3);
		final Player red = new RolloutPlayer(time);
		final Games games = new Games(1000, black, red);
		games.addStateListener(new StateListener() {
			
			@Override
			public void onThrow(Player actor, ThrowState from, MoveState to) {
				System.out.println(actor.toString()+"("+(actor==black?"x":"o")+") throws "+to.diceThrow);
			}
			
			@Override
			public void onMove(Player actor, MoveState from, ThrowState to) {
				System.out.println(to.board);
//				System.out.println("Hit: "+MoveState.hit+" Miss: "+MoveState.miss+" Cache size: "+MoveState.cache.size());
//				MoveState.hit = 0;
//				MoveState.miss = 0;
//				System.out.println("QUERY CACHE: Hit: "+Board.hits+" Miss: "+Board.miss+" Cache size: "+Board.cache.size());
//				Board.hits = 0;
//				Board.miss = 0;
//				if(actor instanceof UCTPlayer){
//					UCTPlayer uctPlayer = (UCTPlayer) actor;
//					int hit = uctPlayer.hit;
//					int miss = uctPlayer.miss;
//					int size = uctPlayer.cache.size();
//					System.out.println("THROWSTATE CACHE: Hit: "+hit+" ("+(100*hit/(float)(hit+miss))+"%) Miss: "+miss+" Cache size: "+size);
//					uctPlayer.hit = 0;
//					uctPlayer.miss = 0;
//				}
			}
			
		});
		games.addWinListener(new WinnerListener() {
			
			@Override
			public void onWinner(Player winner, int amount) {
				System.out.println(winner+" won this game with "+amount);
				System.out.println(black+" has won "+games.getBlackPoints()+", "+red+" has won "+games.getRedPoints());
			}
		});
		games.play();
	}
	
}
