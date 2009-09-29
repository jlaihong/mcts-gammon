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

public enum DiceThrow {

	_11(1,1),
	_12(1,2),
	_13(1,3),
	_14(1,4),
	_15(1,5),
	_16(1,6),

	_22(2,2),
	_23(2,3),
	_24(2,4),
	_25(2,5),
	_26(2,6),

	_33(3,3),
	_34(3,4),
	_35(3,5),
	_36(3,6),

	_44(4,4),
	_45(4,5),
	_46(4,6),

	_55(5,5),
	_56(5,6),

	_66(6,6);

	public final static double singles = 2.0/36.0;
	public final static double doubles = 1.0/36.0;

	public final byte low;
	public final byte high;
	public final boolean isDouble;
	public final double prob;

	private DiceThrow(int low, int high) {
		this((byte)low,(byte)high);
	}
	
	private DiceThrow(byte low, byte high) {
		this.low = low;
		this.high = high;
		this.isDouble = (low==high);
		this.prob = isDouble? doubles:singles;
	}
	
	@Override
	public String toString() {
		return low+" "+high;
	}

}
