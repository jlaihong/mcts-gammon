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
import java.util.Random;


public class ThrowState extends GameState{
	
	private final static Random r = new Random();
	
	public final static DiceThrow[] diceThrows = DiceThrow.values();

	private MoveState[] children = null;

	public ThrowState(Board board, boolean isBlackTurn) {
		super(board, isBlackTurn);
	}

	public MoveState[] getChildren() {
		if(children==null){
			children = new MoveState[diceThrows.length]; 
			for(int i=0;i<diceThrows.length;i++){
				children[i] = createMoveState(board, diceThrows[i], isBlackTurn);
			}
		}
		return children;
	}
	
	protected MoveState createMoveState(Board board, DiceThrow diceThrow, boolean isBlackTurn) {
		return new MoveState(board, diceThrow, isBlackTurn);
	}

	public MoveState getRandomChild(){
		getChildren();
		return children[r.nextInt(children.length)];
	}

}
