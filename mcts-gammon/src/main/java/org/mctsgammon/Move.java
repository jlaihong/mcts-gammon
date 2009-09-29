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
