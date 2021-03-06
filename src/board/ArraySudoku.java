package board;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import board.exceptions.AlreadySettledException;
import board.exceptions.IllegalSizeException;

public class ArraySudoku implements Sudoku, Iterable<Integer> {

	private int[][] board;
	private HashSet<Pair<Integer, Integer>> settledNodes;
	private int boxSize;
	private int size;

	/** Create an empty sudoku **/
	public ArraySudoku(int size) {
		this.size = size;
		boxSize = (int) Math.sqrt(size);
		if (boxSize != Math.sqrt(size))
			throw new IllegalSizeException();
		board = new int[size][size];
		this.settledNodes = new HashSet<Pair<Integer, Integer>>();
	}

	public ArraySudoku(String str, int size) {
		this(size);
		String[] split = str.split(",");
		for (int i = 0; i < split.length; i++) {
			int value = Integer.parseInt(split[i]);	
			int x = i % size, y = i / size;
			if(value == 0) continue;
			set(x, y, value);
			settle(x, y);
		}
	}

	public ArraySudoku(ArraySudoku s) {
		this.board = copyBoardArray(s);
		this.settledNodes = copySettledNodes(s);
		this.boxSize = s.boxSize;
		this.size = s.size;
	}

	@Override
	public int get(int x, int y) {
		return board[y][x];
	}

	@Override
	public void set(int x, int y, int value) {
		board[y][x] = value;
	}

	@Override
	public List<Integer> getLegalMoves(int x, int y) {
		if(isSettled(x, y)) throw new AlreadySettledException();
		HashSet<Integer> illegalMoves = new HashSet<Integer>();
		for (int i = 0; i < size; i++) {
			illegalMoves.add(board[i][x]);
			illegalMoves.add(board[y][i]);
		}
		for (int i = x - x % boxSize; i < x - x % boxSize + boxSize; i++) {
			for (int j = y - y % boxSize; j < y - y % boxSize + boxSize; j++) {
				illegalMoves.add(board[j][i]);
			}
		}
		return IntStream.rangeClosed(1, size).boxed().filter((m) -> !illegalMoves.contains(m)).collect(Collectors.toList());
	}

	@Override
	public boolean isSettled(int x, int y) {
		return settledNodes.contains(new Pair<Integer, Integer>(x, y));
	}

	@Override
	public void settle(int x, int y) {
		settledNodes.add(new Pair<Integer, Integer>(x, y));
	}

	@Override
	public int getSize() {
		return size;
	}
	
	@Override
	public String toString() {
		String rv = "";
		int count = 0;
		for (int i : this) {
			rv += i + " ";
			count++;
			if (count % size == 0)
				rv += "\n";
		}
		return rv;
	}

	private HashSet<Pair<Integer, Integer>> copySettledNodes(ArraySudoku s) {
		HashSet<Pair<Integer, Integer>> newSettledNodes = new HashSet<Pair<Integer, Integer>>();
		for (Pair<Integer, Integer> p : s.settledNodes) {
			newSettledNodes.add(new Pair<Integer, Integer>(p.x.intValue(), p.y.intValue()));
		}
		return newSettledNodes;
	}

	private int[][] copyBoardArray(ArraySudoku s) {
		int[][] newBoard = new int[s.size][];
		for (int i = 0; i < s.size; i++) {
			int[] row = s.board[i];
			newBoard[i] = new int[s.size];
			System.arraycopy(row, 0, newBoard[i], 0, s.size);
		}
		return newBoard;
	}

	@Override
	public ArraySudokuIterator iterator() {
		return new ArraySudokuIterator(this);
	}

	class ArraySudokuIterator implements Iterator<Integer> {

		private int x = 0, y = 0;
		ArraySudoku sudoku;

		public ArraySudokuIterator(ArraySudoku s) {
			this.sudoku = s;
		}

		@Override
		public boolean hasNext() {
			return y < sudoku.size;
		}

		@Override
		public Integer next() {
			int next = board[y][x];
			if (++x == size) {
				x = 0;
				++y;
			}
			return next;
		}

	}
}
