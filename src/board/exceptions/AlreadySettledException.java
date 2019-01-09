package board.exceptions;

public class AlreadySettledException extends RuntimeException {
	public AlreadySettledException() {
		super("You can't request legal moves from an already settled node");
	}
}
