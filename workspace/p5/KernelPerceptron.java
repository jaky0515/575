import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class KernelPerceptron extends Classifier implements Serializable, OptionHandler {
	protected double[] coeffs;
	protected int maxIterCnt = 50000;
	protected boolean fc = false;
	protected DataSet dataset = null;
	private ArrayList< double[] > encodedExs = new ArrayList< double[] >();

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
		// search for -fc and if it exists, update the value of fc
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
		ArrayList< ArrayList< Double[] > > encodedAttrs = this.dataset.attributes.getEncodedAttrs();
		int classIdx = this.dataset.getAttributes().getClassIndex();
		double product = 0.0;
		for(int j = 0; j < this.dataset.getExamples().size(); j++) {
			double y_j = encodedAttrs.get( classIdx ).get( this.dataset.getExamples().get( j ).get( classIdx ).intValue() )[ 0 ];
			double kernelResult = Utils.dotProduct( Utils.getEncodedExample( example, this.encodedExs.get(0).length, this.dataset.attributes ), this.encodedExs.get( j ) );
			kernelResult = Math.pow( kernelResult, 2.0 );
			product += this.coeffs[ j ] * y_j * kernelResult;
		}
		double[] dist = new double[ 2 ];
		if( this.fc ) {
			double sigmoid = Utils.sigmoid( product );
			dist[ 0 ] = 1.0 - sigmoid;
			dist[ 1 ] = sigmoid;
		}
		else {
			dist[ 0 ] = ( product >= 0 ) ? 0.0 : 1.0;
			dist[ 1 ] = ( product >= 0 ) ? 1.0 : 0.0;
		}
		return dist;
	}
	private void encodeExamples( DataSet dataset ) {
		ArrayList< ArrayList< Double[] > > encodedAttrs = dataset.getAttributes().getEncodedAttrs();
		int exampleLen = 0;
		for(int i = 0; i < encodedAttrs.size() - 1; i++) {
			exampleLen += encodedAttrs.get( i ).get( 0 ).length;
		}
		this.encodedExs = new ArrayList< double[] >();
		for(int i = 0; i < dataset.getExamples().size(); i++) {
			Example example = dataset.getExamples().get( i );
			double[] x_i = Utils.getEncodedExample( example, exampleLen, this.dataset.attributes );
			this.encodedExs.add( x_i );
		}
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
		// perform bipolar encoding for the attribute values
		dataset.getAttributes().encode( true );
		this.dataset = dataset;
		Attributes attributes = dataset.getAttributes();
		this.encodeExamples( dataset );
		// initialize the coefficients
		ArrayList< ArrayList< Double[] > > encodedAttrs = attributes.getEncodedAttrs();
		this.coeffs = new double[ dataset.getExamples().size() ];
		for(int i = 0; i < this.coeffs.length; i++) {
			this.coeffs[ i ] = 0.0;
		}
		boolean isConverged = false;
		int iterCount = 0;
		int classIdx = attributes.getClassIndex();
		while( !isConverged && iterCount < this.maxIterCnt ) {
			iterCount++;
			isConverged = true;
			for(int i = 0; i < dataset.getExamples().size(); i++) {
				Example example = dataset.getExamples().get( i );
				double y_i = encodedAttrs.get( classIdx ).get( example.get( classIdx ).intValue() )[ 0 ];
				double product = 0.0;
				for(int j = 0; j < dataset.getExamples().size(); j++) {
					double y_j = encodedAttrs.get( classIdx ).get( dataset.getExamples().get( j ).get( classIdx ).intValue() )[ 0 ];
					product += this.coeffs[ j ] * y_j * this.polynomial( i, j );
				}
				if( y_i * product <= 0 ) {
					this.coeffs[ i ] += 1.0;
					isConverged = false;
				}
			}
		}
	}
	/**
	 * Polynomial kernel function
	 * @param x
	 * @param z
	 * @return result
	 */
	private double polynomial( int i, int j ) {
		double product = Utils.dotProduct( this.encodedExs.get( i ), this.encodedExs.get( j ) );
		return Math.pow( product, 2.0 );
	}
	/**
	 * Makes a deep copy of this class
	 * @return Classifier
	 */
	public Classifier clone() {
		return ( KernelPerceptron ) Utils.deepClone( this );
	}
	/**
	 * Setter method for fc (use of calibration)
	 * @param fc
	 */
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
