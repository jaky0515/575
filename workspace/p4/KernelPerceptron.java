import java.io.Serializable;
import java.util.Arrays;

public class KernelPerceptron extends Classifier implements Serializable, OptionHandler {
	protected double[] coeffs;
	protected int maxIterCnt = 50000;
	protected boolean fc = false;

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
		// search for '-fc' and if it exists, update the value of fc
		this.setFc( Arrays.asList( options ).contains( "-fc" ) ? true : false);
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
		// validation on data-set
		if( dataset.getAttributes().get( dataset.getAttributes().getClassIndex() ).size() != 2 ) {
			throw new Exception("Error: this dataset is not a two-class dataset!");
		}
		// initialize the coefficients
		this.coeffs = new double[ dataset.getExamples().size() ];
		for(int i = 0; i < this.coeffs.length; i++) {
			this.coeffs[ i ] = 0.0;
		}
		boolean isConverged = false;
		int iterCount = 0;
		Attributes attrs = dataset.getAttributes();
		int classIdx = attrs.getClassIndex();
		while( !isConverged && iterCount < this.maxIterCnt ) {
			iterCount++;
			isConverged = true;
			for(int i = 0; i < dataset.getExamples().size(); i++) {
				Example example = dataset.getExamples().get( i );
				double y_i = example.get( classIdx );
				// do the encoding for the class label
				if( attrs.get( classIdx ).size() == 2 ) {
					y_i = ( y_i == 0.0 ) ? -1.0 : 1.0;
				}

				double product = 0.0;
				for(int j = 0; j < dataset.getExamples().size(); j++) {
					double y_j = dataset.getExamples().get( j ).get( classIdx );
					// do the encoding for the class label
					if( attrs.get( classIdx ).size() == 2 ) {
						y_j = ( y_j == 0.0 ) ? -1.0 : 1.0;
					}
					product += this.coeffs[ j ] * y_j * this.polynomial( example, dataset.getExamples().get( j ) );
				}
				if( y_i * product <= 0 ) {
					this.coeffs[ i ] += 1.0;
					isConverged = false;
				}
			}
		}

		if( !isConverged ) {
			throw new FailedToConvergeException( this.maxIterCnt );
		}
	}
	private double polynomial(Example x, Example z) {
		double result = 0.0;
		for(int i = 0; i < x.size(); i++) {
			result += x.get( i ) * z.get( i );
		}
		return Math.pow( result, 2.0 );
	}
	/**
	 * Makes a deep copy of this class
	 * @return Classifier
	 */
	public Classifier clone() {
		return ( KernelPerceptron ) Utils.deepClone( this );
	}
	public void setFc( boolean fc ) {
		this.fc = fc;
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
