package board;

import java.util.List;

public interface Sudoku {
	public int get(int x, int y);
	public void set(int x, int y, int value);
	public List<Integer> getLegalMoves(int x, int y);
	public boolean isSettled(int x, int y);
	public void settle(int x, int y);
	public int getSize();
}
