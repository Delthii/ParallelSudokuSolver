package solver;

import java.util.List;

import board.ArraySudoku;
import board.Sudoku;

public class BacktrackingSolver {
	private Sudoku sudoku;
	volatile boolean abort = false;
	public BacktrackingSolver(Sudoku s) {
		sudoku = s;
		removeTrivials(sudoku);
	}

	public static void removeTrivials(Sudoku sudoku){
		boolean trivialsLeft = true;
		while(trivialsLeft){
			trivialsLeft = false;
			for (int y = 0; y < sudoku.getSize(); y++) {
				for (int x = 0; x < sudoku.getSize(); x++) {
					if (!sudoku.isSettled(x, y)) {
						List<Integer> legalMoves = sudoku.getLegalMoves(x, y);
						if(legalMoves.size() == 1){
							sudoku.set(x, y, legalMoves.get(0));
							sudoku.settle(x, y);
							trivialsLeft = true;
						}
					}
				}
			}
		}
	}
	
	public boolean solve(int sx, int sy) {		
		for (int y = sy; y < sudoku.getSize(); y++) {
			for (int x = sx; x < sudoku.getSize(); x++) {
				if(abort) return false;
				if (!sudoku.isSettled(x, y)) {
					for (int legalMove : sudoku.getLegalMoves(x, y)) {
						sudoku.set(x, y, legalMove);
						sx = x;
						sy = y;
						if (++sx == sudoku.getSize()) {
							sx = 0;
							++sy;
						}
						if (solve(sx, sy))
							return true;
					}
					sudoku.set(x, y, 0);
					return false;
				}
				sx = 0;
			}
		}
		return true;
	}

	public void abort(){
		abort = true;
	}
	
	public Sudoku getSudoku(){
		return sudoku;
	}
	
	@Override
	public String toString() {
		return sudoku.toString();
	}

	public static void main(String[] args) {
		Sudoku s = new ArraySudoku(
				"8,0,3,0,14,0,0,4,6,2,0,0,11,0,0,0,0,1,0,0,0,10,16,0,0,0,15,4,8,0,0,14,4,15,6,0,0,12,3,13,0,10,11,0,7,0,0,0,7,13,10,0,0,0,1,0,0,3,0,0,6,15,2,4,0,0,9,4,13,14,0,15,5,0,0,0,0,0,1,12,0,2,13,0,0,0,0,0,10,0,6,7,16,5,8,0,0,5,0,0,0,7,0,0,14,0,2,8,0,0,0,0,0,12,0,0,0,0,0,0,1,0,4,0,10,14,11,0,0,0,11,8,0,0,5,0,0,0,7,10,0,0,16,0,0,0,0,0,8,3,14,1,0,0,5,0,13,4,15,0,15,0,12,13,9,16,0,0,0,6,0,0,0,0,0,0,0,16,0,0,0,0,15,0,4,0,13,9,12,3,0,10,0,0,2,0,0,9,11,5,12,0,0,0,0,6,10,0,13,0,4,0,3,2,8,0,0,0,0,0,15,0,12,7,0,10,0,0,0,4,0,0,11,0,1,0,0,9,13,0,0,3,0,9,16,6,0,0,0,15,8,2,0,0,0,1",
				16);
		// s = new ArraySudoku(
		// "0,0,0,0,0,0,0,0,0,8,0,4,6,0,5,2,0,0,5,0,9,2,3,0,0,0,0,2,0,0,9,0,0,6,0,0,0,0,0,0,0,0,8,0,0,4,0,0,0,8,0,1,0,7,0,0,0,0,0,0,0,0,3,0,0,0,0,7,2,0,0,1,0,6,0,8,9,0,0,0,0",
		// 9);
		BacktrackingSolver solver = new BacktrackingSolver(s);
		System.out.println(solver);
		System.out.println(solver.solve(0, 0));
		System.out.println(solver);
	}
}