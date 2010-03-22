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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public final class Board {
	
//	private static class Query{
//		
//		private final byte move;
//		private final boolean isBlack;
//		private final Board board;
//
//		public Query(Board board, byte move, boolean isBlack) {
//			this.board = board;
//			this.move = move;
//			this.isBlack = isBlack;
//		}
//
//		@Override
//		public int hashCode() {
//			final int prime = 31;
//			int result = 1;
//			result = prime * result + ((board == null) ? 0 : board.hashCode());
//			result = prime * result + (isBlack ? 1231 : 1237);
//			result = prime * result + move;
//			return result;
//		}
//
//		@Override
//		public boolean equals(Object obj) {
//			if (this == obj)
//				return true;
//			if (obj == null)
//				return false;
//			if (!(obj instanceof Query))
//				return false;
//			Query other = (Query) obj;
//			if (board == null) {
//				if (other.board != null)
//					return false;
//			} else if (!board.equals(other.board))
//				return false;
//			if (isBlack != other.isBlack)
//				return false;
//			if (move != other.move)
//				return false;
//			return true;
//		}		
//		
//	}
//	
//	final static Set<Query> cache = new HashSet<Query>();
//
//	static int hits;
//	static int miss;

	public final static Board startBoard = new Board(
			new byte[]{	0,-2,0,0,0,0,+5,0,+3,0,0,0,-5,
						+5,0,0,0,-3,0,-5,0,0,0,0,+2,0,
						0,0});

	public final static Board cleanBoard = new Board(
			new byte[]{  0,0,0,0,0,0,0,0,0,0,0,0,0,
						 0,0,0,0,0,0,0,0,0,0,0,0,0,
						 +15,-15});

	public static final byte blackbar = 26;
	public static final byte redbar = 27;

	public static final byte blackBearOff = 0;
	public static final byte redBearOff = 25;

	private final byte[] checkers;

	private final int hashCode;

	public Board(byte[] checkers) {
		this.checkers = checkers;
		this.hashCode = Arrays.hashCode(checkers);
	}

	@Override
	public String toString() {
		int maxHeight = 3;
		String string = "";

		string += "  | 13 14 15 16 17 18 |   | 19 20 21 22 23 24 |  \n";
		string += "  |--ᵥ--ᵥ--ᵥ--ᵥ--ᵥ--ᵥ-|   |--ᵥ--ᵥ--ᵥ--ᵥ--ᵥ--ᵥ-|  \n";
		for(int i=1;i<=maxHeight;i++){
			string += "  | "+symbol(13,i,maxHeight)+" "+symbol(14,i,maxHeight)+" "+symbol(15,i,maxHeight)+" "+symbol(16,i,maxHeight)+" "+symbol(17,i,maxHeight)+" "+symbol(18,i,maxHeight)
			+" |"+blackBarSymbol(i,maxHeight)+"| "+symbol(19,i,maxHeight)+" "+symbol(20,i,maxHeight)+" "+symbol(21,i,maxHeight)+" "+symbol(22,i,maxHeight)+" "+symbol(23,i,maxHeight)+" "+symbol(24,i,maxHeight)+" |"+symbol(25,i,maxHeight)+"\n";
		}
		string += "  |                   |   |                   |  \n";
		for(int i=maxHeight;i>0;i--){
			string += "  | "+symbol(12,i,maxHeight)+" "+symbol(11,i,maxHeight)+" "+symbol(10,i,maxHeight)+" "+symbol(9,i,maxHeight)+" "+symbol(8,i,maxHeight)+" "+symbol(7,i,maxHeight)
			+" |"+redBarSymbol(i,maxHeight)+"| "+symbol(6,i,maxHeight)+" "+symbol(5,i,maxHeight)+" "+symbol(4,i,maxHeight)+" "+symbol(3,i,maxHeight)+" "+symbol(2,i,maxHeight)+" "+symbol(1,i,maxHeight)+" |"+symbol(0,i,maxHeight)+"\n";
		}

		string += "  |--ᶺ--ᶺ--ᶺ--ᶺ--ᶺ--ᶺ-|   |--ᶺ--ᶺ--ᶺ--ᶺ--ᶺ--ᶺ-|  \n";
		string += "  | 12 11 10  9  8  7 |   |  6  5  4  3  2  1 |  \n";
		return string;
	}

	private String symbol(int row, int height, int maxHeight){
		if(Math.abs(checkers[row])<height) return "  ";
		else{
			if(height>=maxHeight) return (Math.abs(checkers[row])>9? "":" ")+Math.abs(checkers[row]);
			else return checkers[row]>0? " x": " o";
		}
	}

	private String blackBarSymbol(int height, int maxHeight){
		if(checkers[blackbar]==0 || height<maxHeight) return "   ";
		else return checkers[blackbar]>9? checkers[blackbar]+"x": checkers[blackbar]+" x";
	}

	private String redBarSymbol(int height, int maxHeight){
		if(checkers[redbar]==0 || height<maxHeight) return "   ";
		else return checkers[redbar]<-9? -checkers[redbar]+"o": -checkers[redbar]+" o";
	}
	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Board))
			return false;
		Board other = (Board) obj;
		if (!Arrays.equals(checkers, other.checkers))
			return false;
		return true;
	}

	public final boolean isGameOver(){
		return blackWins() || redWins();
	}

	public final boolean blackWins(){
		return checkers[blackBearOff]==15;
	}

	public final boolean redWins(){
		return checkers[redBearOff]==-15;
	}

	public int getProfit(){
		if(blackWins()){
			if(checkers[redBearOff]!=0) return 1;
			if(checkers[redbar]!=0) return 3;
			for(int i=6; i>0;i--){
				int position = blackBearOff+i;
				if(checkers[position]!=0) {
					return 3;
				}
			}
			return 2;
		}else if(redWins()){
			if(checkers[blackBearOff]!=0) return 1;
			if(checkers[blackbar]!=0) return 3;
			for(int i=6; i>0;i--){
				int position = redBearOff-i;
				if(checkers[position]!=0) {
					return 3;
				}
			}
			return 2;

		}else throw new IllegalStateException("No winner");
	}

	private final Board afterBlackMove(byte from, byte to) {
		byte[] checkers2 = this.checkers.clone();
		-- checkers2[from];
		++ checkers2[to];
		return new Board(checkers2);
	}
	
	private final Board afterRedMove(byte from, byte to) {
		byte[] checkers2 = this.checkers.clone();
		++ checkers2[from];
		-- checkers2[to];
		return new Board(checkers2);
	}

	private final Board afterBlackSteal(byte from, byte to) {
		byte[] checkers2 = this.checkers.clone();
		-- checkers2[redbar];
		-- checkers2[from];
		checkers2[to] = 1;
		return new Board(checkers2);
	}
	
	private final Board afterRedSteal(byte from, byte to) {
		byte[] checkers2 = this.checkers.clone();
		++ checkers2[blackbar];
		++ checkers2[from];
		checkers2[to] = -1;
		return new Board(checkers2);
	}

	public final Collection<Board> afterMove(byte move, boolean isBlack) {
//		Query query = new Query(this, move, isBlack);
//		if(cache.contains(query)) {
//			hits++;
//		}else miss++;
		Collection<Board> result;
		if(isBlack) {
			result = afterBlackMoves(move);
		} else {
			result = afterRedMoves(move);
		}
//		cache.add(query);
		return result;
	}

	public Collection<Board> afterBlackMoves(final byte move) {
		if(checkers[blackbar]!=0){
			return getBoardsAfterBlackBringingIn(move);
		}else{
			Collection<Board> states = new ArrayList<Board>();
			addBlackBearOffBoards(move, states);
			addBlackRegularMoveBoards(move, states);
			return states;
		}
	}

	public final Collection<Board> afterRedMoves(final byte move) {
		if(checkers[redbar]!=0){
			return getBoardsAfterRedBringingIn(move);
		}else{
			Collection<Board> states = new ArrayList<Board>();
			addRedBearOffBoards(move, states);
			addRedRegularMoveBoards(move, states);
			return states;
		}
	}
	private final Collection<Board> getBoardsAfterBlackBringingIn(final byte move) {
		//we must bring in checkers from the bar
		byte target = (byte) (redBearOff-move);
		if(checkers[target]>=0){
			//free to move
			return Collections.singleton(afterBlackMove(blackbar, target));
		}else if(checkers[target]==-1){
			//free to steal
			return Collections.singleton(afterBlackSteal(blackbar, target));
		}else{
			//can't move
			return Collections.emptySet();
		}
	}

	private final Collection<Board> getBoardsAfterRedBringingIn(final byte move) {
		//we must bring in checkers from the bar
		byte target = (byte) (blackBearOff+move);
		if(checkers[target]<=0){
			//free to move
			return Collections.singleton(afterRedMove(redbar, target));
		}else if(checkers[target]==1){
			//free to steal
			return Collections.singleton(afterRedSteal(redbar, target));
		}else{
			//can't move
			return Collections.emptySet();
		}
	}

	private final void addBlackBearOffBoards(final byte move, Collection<Board> states) {
		//check for endzone
		byte furthestFromBearOff = 0;
		byte homeBoardSum = 0;
		for(byte i=6; i>0;i--){
			int position = blackBearOff+i;
			if(checkers[position]>0) {
				homeBoardSum += checkers[position];
				if(furthestFromBearOff==0){
					furthestFromBearOff=i;
				}
			}
		}
		if(homeBoardSum+checkers[blackBearOff]==15){
			//we can bear off!
			if(checkers[blackBearOff+move]>0){
				// bear off from exact position
				states.add(afterBlackMove((byte) (blackBearOff+move), blackBearOff));
			}else if(furthestFromBearOff<move && furthestFromBearOff>0){
				states.add(afterBlackMove((byte) (blackBearOff+furthestFromBearOff), blackBearOff));
			}
		}
	}

	private final void addRedBearOffBoards(final byte move, Collection<Board> states) {
		//check for endzone
		byte furthestFromBearOff = 0;
		byte homeBoardSum = 0;
		for(byte i=6; i>0;i--){
			int position = redBearOff-i;
			if(checkers[position]<0) {
				homeBoardSum += checkers[position];
				if(furthestFromBearOff==0){
					furthestFromBearOff=i;
				}
			}
		}
		if(homeBoardSum+checkers[redBearOff]==-15){
			//we can bear off!
			if(checkers[redBearOff-move]<0){
				// bear off from exact position
				states.add(afterRedMove((byte) (redBearOff-move), redBearOff));
			}else if(furthestFromBearOff<move && furthestFromBearOff>0){
				states.add(afterRedMove((byte) (redBearOff-furthestFromBearOff), redBearOff));
			}
		}
	}

	private final void addBlackRegularMoveBoards(final byte move, Collection<Board> states) {
		for(byte from=(byte) (blackBearOff+1+move);from<redBearOff;from++){
			if(checkers[from]>0){
				//there is a black checkers we can move
				byte to = (byte) (from-move);
				if(checkers[to]>=0){
					//the target is free
					states.add(afterBlackMove(from, to));
				}else if(checkers[to]==-1){
					//we can steal the target
					states.add(afterBlackSteal(from, to));
				}
			}
		}
	}

	private final void addRedRegularMoveBoards(final byte move, Collection<Board> states) {
		for(byte from=(byte) (redBearOff-1-move);from>blackBearOff;from--){
			if(checkers[from]<0){
				//there is a red checkers we can move
				byte to = (byte) (from+move);
				if(checkers[to]<=0){
					//the target is free
					states.add(afterRedMove(from, to));
				}else if(checkers[to]==1){
					//we can steal the target
					states.add(afterRedSteal(from, to));
				}
			}
		}
	}
	
}
