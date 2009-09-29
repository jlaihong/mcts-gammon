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
package org.mctsgammon.players;

import java.util.Random;

import org.mctsgammon.MoveState;
import org.mctsgammon.Player;
import org.mctsgammon.ThrowState;

public class RandomPlayer implements Player {

	private static Random r = new Random();
	
	@Override
	public ThrowState chooseMove(MoveState state) {
		ThrowState[] states = state.getChildren();
		return states[r.nextInt(states.length)];
	}
	
	@Override
	public String toString() {
		return "RandomPlayer";
	}
	
	@Override
	public void onMove(Player actor, MoveState from, ThrowState to) {
	}

	@Override
	public void onThrow(Player actor, ThrowState from, MoveState to) {
	}

}
