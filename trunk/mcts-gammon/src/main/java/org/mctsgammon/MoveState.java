package org.mctsgammon;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

public class MoveState extends GameState{

	private final static Random r = new Random();

	//	final static Set<Board> cache = new HashSet<Board>();
	//	static int hit, miss;

	public final DiceThrow diceThrow;

	private ThrowState[] children;


	public MoveState(Board board, DiceThrow diceThrow, boolean isBlackTurn) {	
		super(board, isBlackTurn);
		this.diceThrow = diceThrow;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((board == null) ? 0 : board.hashCode());
		result = prime * result
		+ ((diceThrow == null) ? 0 : diceThrow.hashCode());
		result = prime * result + (isBlackTurn ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MoveState))
			return false;
		MoveState other = (MoveState) obj;
		if (board == null) {
			if (other.board != null)
				return false;
		} else if (!board.equals(other.board))
			return false;
		if (diceThrow == null) {
			if (other.diceThrow != null)
				return false;
		} else if (!diceThrow.equals(other.diceThrow))
			return false;
		if (isBlackTurn != other.isBlackTurn)
			return false;
		return true;
	}

	public ThrowState[] getChildren() {
		if(children==null){
			//			if(cache.contains(board)) {
			//				hit++;
			//			}else {
			//				miss++;
			//			}
			Collection<Board> boards = getPossibleStates();
			//			cache.add(board);
			saveChildren(boards);
		}
		return children;
	}

	private void saveChildren(Collection<Board> boards) {
		if(boards.isEmpty()) {
			children = new ThrowState[]{createThrowState(board, !isBlackTurn)};
		}
		else{
			int i = 0;
			children = new ThrowState[boards.size()];
			for(Board b:boards){
				children[i] = createThrowState(b, !isBlackTurn);
				++i;
			}
		}
	}

	protected ThrowState createThrowState(Board board, boolean isBlackTurn) {
		return new ThrowState(board, isBlackTurn);
	}

	public ThrowState getRandomChild(){
		getChildren();
		return children[r.nextInt(children.length)];
	}

	public Collection<Board> getPossibleStates() {
		Collection<Board> boards;
		if(diceThrow.isDouble){
			boards = getPossibleStatesAfterDouble(diceThrow.low);
		}else{
			boards = getPossibleStatesAfterSingle(diceThrow.low,diceThrow.high);
		}
		return boards;
	}

	public Collection<Board> getPossibleStatesAfterDouble(byte move) {
		Collection<Board> after1 = board.afterMove((byte) move,isBlackTurn);

		Set<Board> after2 = generateSuccessors(move, after1);
		if(after2.isEmpty()) return after1;

		Set<Board> after3 = generateSuccessors(move, after2);
		if(after3.isEmpty()) return after2;

		Set<Board> after4 = generateSuccessors(move, after3);
		if(after4.isEmpty()) return after3;

		return after4;
	}

	public static Map<Board, Move[]> reduce(Map<Board, Move[]> states) {
		Map<Board, Move[]> states2 = new HashMap<Board, Move[]>();
		for(Entry<Board, Move[]> entry: states.entrySet()){
			states2.put(entry.getKey(), Move.reduce(entry.getValue()));
		}
		return states2;
	}

	public Collection<Board> getPossibleStatesAfterSingle(byte low, byte high) {
		Collection<Board> after1a = board.afterMove((byte) low,isBlackTurn);
		Collection<Board> after1b = board.afterMove((byte) high,isBlackTurn);

		Set<Board> after2a = generateSuccessors(high, after1a);

		Set<Board> after2b = generateSuccessors(low, after1b);

		if(after2a.isEmpty() && after2b.isEmpty()) {
			if(after1b.isEmpty()) return after1a;
			return after1b;
		}
		else if(after2a.isEmpty()) return after2b;
		else if(after2b.isEmpty()) return after2a;
		else {
			after2a.addAll(after2b);
			return after2a;
		}
	}

	private final Set<Board> generateSuccessors(byte move, Collection<Board> before) {
		Set<Board> after = new HashSet<Board>();
		for(Board b : before) {
			Collection<Board> partialMoves = b.afterMove( move,isBlackTurn);
			after.addAll(partialMoves);
		}
		//		System.out.println("From "+before.size()+" to "+after.size()+" ( times "+after.size()/(0.0001+before.size())+")");
		return after;
	}

	public static MoveState getRandomStartState(){
		DiceThrow[] dice = DiceThrow.values();
		DiceThrow firstThrow;
		do{
			firstThrow = dice[r.nextInt(dice.length)];
		}while(firstThrow.isDouble);
		boolean isBlackTurn = r.nextBoolean();

		return new MoveState(Board.startBoard, firstThrow, isBlackTurn);
	}

}
