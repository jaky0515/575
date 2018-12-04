import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class BP extends Classifier implements Serializable, OptionHandler {
	protected double learningRate = 0.9;
	protected double minErr = 0.1;
	protected int J;
	protected int I;
	protected int K;
	protected double[][] V;
	protected double[][] W;
	protected int maxIterCnt = 50000;
	protected Attributes attributes = null;
	private Random random = null;
	private ArrayList< double[] > encodedExs = new ArrayList< double[] >();

	/**
	 * Default constructor
	 */
	public BP() {

	}
	/**
	 * Constructor
	 * @param options - string arguments
	 * @throws Exception
	 */
	public BP( String[] options ) throws Exception {
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
		// search for -J and if it exists, update the value of j
		if( Arrays.asList( options ).contains( "-J" ) ) {
			this.setJ( Integer.parseInt( options[Arrays.asList( options ).indexOf("-J") + 1] ) );
		}
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
		// validation
		if( example == null || example.isEmpty() ) {
			throw new Exception("Error: invalid Example object passed-in!");
		}
		double[] x = Utils.getEncodedExample( example, this.I, this.attributes );
		x[ this.I - 1 ] = -1.0;	// bias
		double[] h = new double[ this.J ];
		for(int j = 0; j < this.J - 1; j++) {
			double product = Utils.dotProduct( this.V[ j ], x );
			h[ j ] = Utils.sigmoid( product );
		}
		h[ this.J - 1 ] = -1.0;	// bias
		double[] o = new double[ this.K ];
		for(int k = 0; k < this.K; k++) {
			double product = Utils.dotProduct( this.W[ k ] , h );
			o[ k ] = Utils.sigmoid( product );
		}
		return o;
	}
	/**
	 * this method returns a new random double
	 * @return randomly generated double
	 */
	private double getRandomNum() {
		return this.random.nextDouble() / 10.0;
	}
	private void encodeExamples( DataSet dataset ) {
		this.encodedExs = new ArrayList< double[] >();
		for(int i = 0; i < dataset.getExamples().size(); i++) {
			Example example = dataset.getExamples().get( i );
			double[] x_i = Utils.getEncodedExample( example, this.I, this.attributes );
			this.encodedExs.add( x_i );
		}
	}
	/**
	 * Train using a given data-set
	 * @param dataset
	 */
	public void train( DataSet dataset ) throws Exception {
		// perform bipolar encoding for the attribute values
		dataset.getAttributes().encode( false );
		this.attributes = dataset.getAttributes();

		// initialization
		ArrayList< ArrayList< Double[] > > encodedAttrs = this.attributes.getEncodedAttrs();
		this.I = 1;
		for(int i = 0; i < encodedAttrs.size() - 1; i++) {
			this.I += encodedAttrs.get( i ).get( 0 ).length;
		}
		this.encodeExamples( dataset );
		int classIdx = dataset.getAttributes().getClassIndex();
		this.K = dataset.getAttributes().get( classIdx ).size();
		int q = 0;
		int m = 0;
		double E = 0.0;
		this.random = new Random( System.currentTimeMillis() );
		this.V = new double[ this.J - 1 ][ this.I ];
		this.W = new double[ this.K ][ this.J ];
		for(int i = 0; i < this.V.length; i++) {
			for(int j = 0; j < this.V[ i ].length; j++) {
				this.V[ i ][ j ] = this.getRandomNum();
			}
		}
		for(int i = 0; i < this.W.length; i++) {
			for(int j = 0; j < this.W[ i ].length; j++) {
				this.W[ i ][ j ] = this.getRandomNum();
			}
		}
		while( q < this.maxIterCnt ) {
			// start the training
			while( m < dataset.getExamples().size() ) {
				// step 2
				Example example = dataset.getExamples().get( m );
				double[] x = this.encodedExs.get( m );
				x[ this.I - 1 ] = -1.0;	// bias

				double[] y = new double[ this.K ];
				y[ example.get( classIdx ).intValue() ] = 1.0;

				double[] h = new double[ this.J ];
				for(int j = 0; j < this.J - 1; j++) {
					double product = Utils.dotProduct( this.V[ j ], x );
					h[ j ] = Utils.sigmoid( product );
				}
				h[ this.J - 1 ] = -1.0;	// bias

				double[] o = new double[ this.K ];
				for(int k = 0; k < this.K; k++) {
					double product = Utils.dotProduct( this.W[ k ], h );
					o[ k ] = Utils.sigmoid( product );
				}
				// step 3
				for(int k = 0; k < this.K; k++) {
					E = 0.5 * ( Math.pow( ( y[ k ] - o[ k ] ), 2.0 ) ) + E;
				}
				// step 4
				double[] signal_o = new double[ this.K ];
				double[] signal_h = new double[ this.J ];
				for(int k = 0; k < this.K; k++) {
					signal_o[ k ] = ( y[ k ] - o[ k ] ) * ( 1 - o[ k ] ) * o[ k ];
				}
				for(int j = 0; j < this.J; j++) {
					double sum = 0.0;
					for(int k = 0; k < this.K; k++) {
						sum += signal_o[ k ] * this.W[ k ][ j ];
					}
					signal_h[ j ] = h[ j ] * ( 1 - h[ j ] ) * sum;
				}
				// step 5
				for(int k = 0; k < this.K; k++) {
					for(int j = 0; j < this.J; j++) {
						this.W[ k ][ j ] += this.learningRate * signal_o[ k ] * h[ j ];
					}
				}
				// step 6
				for(int j = 0; j < this.J - 1; j++) {
					for(int i = 0; i < this.I; i++) {
						this.V[ j ][ i ] += this.learningRate * signal_h[ j ] * x[ i ];
					}
				}
				// step 7
				m++;
			}
			q++;
			// step 8
			if( E >= this.minErr ) {
				E = 0.0;
				m = 0;
			}
			else {
				break;
			}
		}

		if( q >= this.maxIterCnt ) {
			throw new FailedToConvergeException( this.maxIterCnt );
		}
	}
	/**
	 * Makes a deep copy of this class
	 * @return Classifier
	 */
	public Classifier clone() {
		return ( BP ) Utils.deepClone( this );
	}
	/**
	 * Setter function for j (number of units in the hidden layer)
	 * @param j
	 */
	public void setJ( int J ) {
		this.J = J;
	}
	/**
	 * Main method
	 * @param args
	 */
	public static void main( String[] args ) {
		try {
			Evaluator evaluator = new Evaluator( new BP(), args );
			Performance performance = evaluator.evaluate();
			System.out.println( performance );
		}
		catch ( Exception e ) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
		}
	}

}
