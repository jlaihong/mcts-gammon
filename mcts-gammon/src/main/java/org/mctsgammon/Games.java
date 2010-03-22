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

import org.mctsgammon.states.ThrowState;

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
	
	
}
