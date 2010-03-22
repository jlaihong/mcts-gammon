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
import java.util.Scanner;

import org.mctsgammon.states.MoveState;
import org.mctsgammon.states.ThrowState;


public class ConsoleGame implements Player, WinnerListener{

	private Scanner s = new Scanner(System.in);
	private Game game;

	public ConsoleGame() {
		this.game = new Game(this,this);
		game.addWinListener(this);
	}
	
	private void start() {
		game.play();
	}

	public static void main(String[] args) {
		(new ConsoleGame()).start();
	}

	@Override
	public ThrowState chooseMove(MoveState state) {
		System.out.println(state.board.toString());
		System.out.println("Dice: "+state.diceThrow);
		System.out.println((state.isBlackTurn?"Black(x)":"Red(o)")+" can choose:");
		//		TreeSet<Entry<Board, Move[]>> treeset = new TreeSet<Entry<Board,Move[]>>(new Comparator<T>() {
		//			@Override
		//			public int compare(T o1, T o2) {
		//				return 0;
		//			}
		//		})
		ThrowState[] children = state.getChildren();
//		Move[][] moves = state.getMoves();
//		Board newBoard;
//		for(int i=0; i<moves.length;i++){
//			if((i+1)<10) System.out.print("    ["+(i+1)+"]");
//			else System.out.print("   ["+(i+1)+"]");
//
//			for(Move m:moves[i]){
//				System.out.print(" "+m);
//			}
//			System.out.println();
//		}
		System.out.print("choice? ");
		int choice;
		//			do{
		//				choice = s.nextInt();
		//			}while(choice<1 || choice>list.size());
		choice=1;
		System.out.println(choice);
		return children[choice-1];
	}

	
	@Override
	public void onWinner(Player winner, int amount) {
		System.out.println();
		System.out.println("We have a winner!");
		System.out.println(game.getBoard().toString());
		System.out.println((game.getBoard().blackWins()? "Black":"Red")+" wins "+amount);
	}

	@Override
	public void onMove(Player actor, MoveState from, ThrowState to) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onThrow(Player actor, ThrowState from, MoveState to) {
		// TODO Auto-generated method stub
		
	}
}
