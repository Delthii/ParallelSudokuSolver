package board.exceptions;

public class IllegalSizeException extends RuntimeException {
	public IllegalSizeException() {
		super("Size needs to have an integer root");
	}
}
