package solver;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import board.ArraySudoku;
import board.Sudoku;

public class ParallelSolver {

	private ExecutorService threadPool;
	private Sudoku sudoku;
	private LinkedList<Sudoku> tasks;
	private Sudoku solution;

	public ParallelSolver(Sudoku s, int numberOfThreads) {
		sudoku = s;
		threadPool = Executors.newFixedThreadPool(numberOfThreads);
		tasks = new LinkedList<Sudoku>();
		tasks.add(sudoku);
		BacktrackingSolver.removeTrivials(sudoku);
	}

	public void createTasks(int numberOfTasks) {
		int sx = 0;
		int sy = 0;
		while (tasks.size() < numberOfTasks) {
			boolean done = false;
			Sudoku parent = tasks.removeFirst();
			for (int y = sy; y < parent.getSize() && !done; y++) {
				for (int x = sx; x < parent.getSize() && !done; x++) {
					if (!parent.isSettled(x, y)) {
						for (int legalMove : parent.getLegalMoves(x, y)) {
							Sudoku s = new ArraySudoku((ArraySudoku) parent);
							s.set(x, y, legalMove);
							s.settle(x, y);
							tasks.add(s);
						}
						done = true;
						sx = x;
						sy = y;
					}
				}
			}
		}
	}

	// **Greedy, will terminate all other tasks when the first solution is found**//
	public void solve() {
		List<BacktrackingSolver> activeSolvers = new CopyOnWriteArrayList<BacktrackingSolver>();
		for (Sudoku s : tasks) {
			BacktrackingSolver bts = new BacktrackingSolver(s);
			activeSolvers.add(bts);
			threadPool.execute(() -> {
				if (bts.solve(0, 0)) {
					solution = bts.getSudoku();
					for(BacktrackingSolver abts : activeSolvers){
						abts.abort();
					}
				}
			});
		}
		threadPool.shutdown();
		try {
			threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public Sudoku getSolution() {
		return solution;
	}

	@Override
	public String toString() {
		return sudoku.toString();
	}

	public static void main(String[] args) {
		Sudoku s = new ArraySudoku(
				"0,0,0,0,0,0,0,4,6,2,0,0,11,0,0,0,0,1,0,0,0,10,0,0,0,0,15,4,8,0,0,14,4,15,6,0,0,0,0,0,0,10,11,0,7,0,0,0,7,0,10,0,0,0,1,0,0,3,0,0,6,15,2,4,0,0,9,4,13,14,0,15,5,0,0,0,0,0,1,12,0,2,13,0,0,0,0,0,10,0,6,7,16,5,8,0,0,5,0,0,0,7,0,0,14,0,2,8,0,0,0,0,0,12,0,0,0,0,0,0,1,0,4,0,10,14,11,0,0,0,11,8,0,0,5,0,0,0,7,10,0,0,16,0,0,0,0,0,8,3,14,1,0,0,5,0,13,4,15,0,15,0,12,13,9,16,0,0,0,6,0,0,0,0,0,0,0,16,0,0,0,0,15,0,4,0,13,9,12,3,0,10,0,0,2,0,0,9,11,5,12,0,0,0,0,6,10,0,13,0,4,0,3,2,8,0,0,0,0,0,15,0,12,7,0,10,0,0,0,4,0,0,11,0,1,0,0,9,13,0,0,3,0,9,16,6,0,0,0,15,8,2,0,0,0,1",
				16);

		ParallelSolver solver = new ParallelSolver(s, 64);
		solver.createTasks(256);
		long start = System.currentTimeMillis();
		//took 8.5s on my computer
		solver.solve();
		System.out.println(System.currentTimeMillis() - start);
		System.out.println(solver.getSolution());
		s = new ArraySudoku(
				"0,0,0,0,0,0,0,4,6,2,0,0,11,0,0,0,0,1,0,0,0,10,0,0,0,0,15,4,8,0,0,14,4,15,6,0,0,0,0,0,0,10,11,0,7,0,0,0,7,0,10,0,0,0,1,0,0,3,0,0,6,15,2,4,0,0,9,4,13,14,0,15,5,0,0,0,0,0,1,12,0,2,13,0,0,0,0,0,10,0,6,7,16,5,8,0,0,5,0,0,0,7,0,0,14,0,2,8,0,0,0,0,0,12,0,0,0,0,0,0,1,0,4,0,10,14,11,0,0,0,11,8,0,0,5,0,0,0,7,10,0,0,16,0,0,0,0,0,8,3,14,1,0,0,5,0,13,4,15,0,15,0,12,13,9,16,0,0,0,6,0,0,0,0,0,0,0,16,0,0,0,0,15,0,4,0,13,9,12,3,0,10,0,0,2,0,0,9,11,5,12,0,0,0,0,6,10,0,13,0,4,0,3,2,8,0,0,0,0,0,15,0,12,7,0,10,0,0,0,4,0,0,11,0,1,0,0,9,13,0,0,3,0,9,16,6,0,0,0,15,8,2,0,0,0,1",
				16);
		BacktrackingSolver bts = new BacktrackingSolver(s);
		start = System.currentTimeMillis();
		//Took 299s on my computer
		bts.solve(0, 0);
		System.out.println(System.currentTimeMillis() - start);
		System.out.println(bts);
	}
}
