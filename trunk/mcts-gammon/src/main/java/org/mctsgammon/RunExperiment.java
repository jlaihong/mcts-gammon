package org.mctsgammon;
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
import org.mctsgammon.players.RolloutPlayer;
import org.mctsgammon.players.UCTPlayer;


public class RunExperiment {

	
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
