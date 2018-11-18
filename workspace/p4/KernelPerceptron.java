import java.io.Serializable;

public class KernelPerceptron extends Classifier implements Serializable, OptionHandler {
	
	/**
	 * Default constructor
	 */
	public KernelPerceptron() {
		
	}
	/**
	 * Constructor
	 * @param options - string arguments
	 * @throws Exception
	 */
	public KernelPerceptron( String[] options ) throws Exception {
		if( options == null || options.length < 2 ) {
			throw new Exception("Error: invalid options passed-in!");
		}
		this.setOptions( options );
	}
	/**
	 * Sets the options for this classifier
	 * @param options - the arguments
	 */
	public void setOptions( String options[] ) {
		
	}
	/**
	 * Classifies a given data-set and return its performance
	 * @param dataset
	 * @return performance
	 */
	public Performance classify( DataSet dataset ) throws Exception {
		Performance perform = new Performance( dataset.getAttributes() );
		Examples exs = dataset.getExamples();
		if( exs != null ) {
			for(int i = 0; i < exs.size(); i++) {
				// append this prediction result to the performance
				Example ex = exs.get(i);
				int actualClass = ex.get( dataset.getAttributes().getClassIndex() ).intValue();
				perform.add( actualClass, this.getDistribution( ex ) );
			}
		}
		return perform;
	}
	/**
	 * Classifies a given example query and return the predicted label
	 * @param query
	 * @return predicted label
	 */
	public int classify( Example query ) throws Exception {
		if( query == null || query.isEmpty() ) {
			throw new Exception("Error: invalid Example object passed-in!");
		}
		return Utils.maxIndex( this.getDistribution( query ) );
	}
	/**
	 * Compute the distribution by comparing a given query with other examples
	 * @param query
	 * @return double[] - distribution
	 */
	public double[] getDistribution( Example example ) throws Exception {
		return null;
	}
	/**
	 * Train using a given data-set
	 * @param dataset
	 */
	public void train( DataSet dataset ) throws Exception {
		
	}
	/**
	 * Makes a deep copy of this class
	 * @return Classifier
	 */
	public Classifier clone() {
		return ( KernelPerceptron ) Utils.deepClone( this );
	}
	/**
	 * Main method
	 * @param args
	 */
	public static void main( String[] args ) {
		try {
			Evaluator evaluator = new Evaluator( new KernelPerceptron(), args );
			Performance performance = evaluator.evaluate();
			System.out.println( performance );
		}
		catch ( Exception e ) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
		}
	}
}
