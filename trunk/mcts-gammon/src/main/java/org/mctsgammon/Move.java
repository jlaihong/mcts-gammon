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
import java.util.ArrayList;


public class Move {

	public final int to;
	public final int from;
	public final boolean isBlack;
	public final int times;
	public final boolean isSteal;

	public Move(int from, int to, boolean isBlack, boolean isSteal) {
		this(1,from,to,isBlack,isSteal);
	}

	public Move(int times, int from, int to, boolean isBlack,boolean isSteal) {
		this.times = times;
		this.from = from;
		this.to = to;
		this.isBlack = isBlack;
		this.isSteal = isSteal;
	}

	@Override
	public String toString() {
		String froms,tos;
		if(from==Board.blackbar || from==Board.redbar) froms = "bar";
		else froms = ""+from;
		if(to==Board.blackBearOff || to==Board.redBearOff) tos = "off";
		else tos = ""+to;
		return froms+"/"+tos+(isSteal?"*":"")+(times>1?"("+times+")":"");
	}

	public static Move[] reduce(Move... moves){
		for(Move m1: moves){
			for(Move m2: moves){
				if(m1!=m2){
					if(m1.to == m2.from && m1.isBlack == m2.isBlack && m1.times == m2.times && !m1.isSteal && !m2.isSteal){
						ArrayList<Move> newMoves = new ArrayList<Move>(moves.length);
						for(Move m3: moves){
							if(m3!=m1 && m3!=m2) newMoves.add(m3);
						}
						newMoves.add(new Move(m1.times, m1.from,m2.to,m1.isBlack, false));
						return reduce(newMoves.toArray(new Move[0]));
					}else if(m1.to == m2.to && m1.from == m2.from && m1.isBlack == m2.isBlack){
						ArrayList<Move> newMoves = new ArrayList<Move>(moves.length);
						for(Move m3: moves){
							if(m3!=m1 && m3!=m2) newMoves.add(m3);
						}
						newMoves.add(new Move(m1.times+m2.times, m1.from,m1.to,m1.isBlack, m1.isSteal || m2.isSteal));
						return reduce(newMoves.toArray(new Move[0]));
					}
				}
			}
		}
		return moves;
	}

}
