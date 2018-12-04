
public class FailedToConvergeException extends RuntimeException {
	
	public FailedToConvergeException( int maxIterCnt ) {
		super("Error: Has not yet converged until it reaches the maximum iteration number, " + maxIterCnt + ", is reached!\n");
	}
	
	public FailedToConvergeException( String msg ) {
		super( msg );
	}
	
}
