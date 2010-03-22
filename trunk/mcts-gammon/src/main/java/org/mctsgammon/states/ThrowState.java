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
package org.mctsgammon.states;
import org.mctsgammon.Board;
import org.mctsgammon.DiceThrow;
import org.mctsgammon.GameState;

public class ThrowState extends GameState{

	public ThrowState(Board board, boolean isBlackTurn) {
		super(board, isBlackTurn);
	}

	public MoveState[] getChildren() {
		DiceThrow[] diceThrows = DiceThrow.values();
		MoveState[] children = new MoveState[diceThrows.length];
		for(int i=0;i<diceThrows.length;i++){
			children[i] = new MoveState(board, diceThrows[i], isBlackTurn);
		}
		return children;
	}

	public MoveState getRandomChild(){
		return new MoveState(board, DiceThrow.getRandomThrow(), isBlackTurn);
	}

}
