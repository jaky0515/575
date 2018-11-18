
public class FailedToConvergeException extends RuntimeException {
	
	public FailedToConvergeException() {
		super("Error: Has not yet converged until it reaches the maximum iteration number, 50,000, is reached!");
	}
	
	public FailedToConvergeException( String msg ) {
		super( msg );
	}
	
}
