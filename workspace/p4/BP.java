import java.io.Serializable;
import java.text.DecimalFormat;
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
	private long seed = 2026875034;
	private Random random = null;

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
		// search for '-J' and if it exists, update the value of j
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
		double[][] x = new double[ this.I ][ 1 ];
		for(int i = 0; i < this.I; i++) {
			if( i == this.I - 1) {
				x[ i ][ 0 ] = -1.0;	// bias
			}
			else {
				x[ i ][ 0 ] = example.get( i );
			}
		}
		double[][] h = new double[ this.J ][ 1 ];
		for(int j = 0; j < this.J - 1; j++) {
			double input = 0.0;
			double[] V_j = V[ j ];
			for(int i = 0; i < V_j.length; i++) {
				input += V_j[ i ] * x[ i ][ 0 ];
			}
			h[ j ][ 0 ] = this.sigmoid( input );
		}
		h[ this.J - 1 ][ 0 ] = -1.0;	// bias
		double[][] o = new double[ K ][ 1 ];
		for(int k = 0; k < K; k++) {
			double input = 0.0;
			double[] W_k = W[ k ];
			for(int i = 0; i < W_k.length; i++) {
				input += W_k[ i ] * h[ i ][ 0 ];
			}
			o[ k ][ 0 ] = this.sigmoid( input );
		}
		return o[ 0 ];
	}
	/**
	 * this method returns a new random double in the given range
	 * @param min
	 * @param max
	 * @return randomly generated double
	 */
	private double getRandomNum( int min, int max ) {
		if( random == null ) {
			random = new Random( seed );
		}
		else {
			random.setSeed( ++seed );
		}
		double num = random.nextDouble();
		num *= ( max - min ) + min;
		if( num >= max ) {
			num = Math.nextDown( max );
		}
		return num;
	}
	/**
	 * Train using a given data-set
	 * @param dataset
	 */
	public void train( DataSet dataset ) throws Exception {
		// initialization
		this.I = dataset.getAttributes().size();
		this.K = 1;
		int q = 0;
		int m = 0;
		double E = 0.0;
		double p = 0.0;	// what is the use of this variable?

		this.V = new double[ this.J - 1 ][ this.I ];
		this.W = new double[ this.K ][ this.J ];
		for(int i = 0; i < this.V.length; i++) {
			for(int j = 0; j < this.V[ i ].length; j++) {
				this.V[ i ][ j ] = this.getRandomNum( 0, 1 );
				//				this.V[ i ][ j ] = 0.1 * ( i + 1 );
			}
		}
		for(int i = 0; i < this.W.length; i++) {
			for(int j = 0; j < this.W[ i ].length; j++) {
				this.W[ i ][ j ] = this.getRandomNum( 0, 1 );
				//				this.W[ i ][ j ] = 0.1 * ( j + 1 );
			}
		}

		while( q < this.maxIterCnt ) {
			// start the training
			while( m < dataset.getExamples().size() ) {
				// step 2
				Example example = dataset.getExamples().get( m );
				double[][] x = new double[ this.I ][ 1 ];
				for(int i = 0; i < this.I; i++) {
					if( i == this.I - 1) {
						x[ i ][ 0 ] = -1.0;	// bias
					}
					else {
						x[ i ][ 0 ] = example.get( i );
					}
				}
				double[] y = new double[ this.K ];
				int counter = 0;
				for(int i = x.length; i < dataset.getAttributes().size(); i++) {
					y[ counter++ ] = example.get( i );
				}
				double[][] h = new double[ this.J ][ 1 ];
				for(int j = 0; j < this.J - 1; j++) {
					double input = 0.0;
					double[] V_j = this.V[ j ];
					for(int i = 0; i < V_j.length; i++) {
						input += V_j[ i ] * x[ i ][ 0 ];
					}
					h[ j ][ 0 ] = this.sigmoid( input );
				}
				h[ this.J - 1 ][ 0 ] = -1.0;	// bias
				double[][] o = new double[ this.K ][ 1 ];
				for(int k = 0; k < this.K; k++) {
					double input = 0.0;
					double[] W_k = this.W[ k ];
					for(int i = 0; i < W_k.length; i++) {
						input += W_k[ i ] * h[ i ][ 0 ];
					}
					o[ k ][ 0 ] = this.sigmoid( input );
				}
				// step 3
				for(int k = 0; k < this.K; k++) {
					E += 0.5 * ( Math.pow( ( y[ k ] - o[ k ][ 0 ] ), 2.0 ) );
				}
				// step 4
				double[][] signal_o = new double[ this.K ][ 1 ];
				double[][] signal_h = new double[ this.J - 1 ][ 1 ];
				for(int k = 0; k < this.K; k++) {
					signal_o[ k ][ 0 ] = ( y[ k ] - o[ k ][ 0 ] ) * ( 1 - o[ k ][ 0 ] ) * o[ k ][ 0 ];
				}
				for(int j = 0; j < this.J - 1; j++) {
					double sum = 0.0;
					for(int k = 0; k < this.K; k++) {
						sum += signal_o[ k ][ 0 ] * this.W[ k ][ j ];
					}
					signal_h[ j ][ 0 ] = h[ j ][ 0 ] * ( 1 - h[ j ][ 0 ] ) * sum;
				}
				// step 5
				for(int k = 0; k < this.K; k++) {
					for(int j = 0; j < this.J; j++) {
						this.W[ k ][ j ] += this.learningRate * signal_o[ k ][ 0 ] * h[ j ][ 0 ];
					}
				}
				// step 6
				for(int j = 0; j < this.J - 1; j++) {
					for(int i = 0; i < this.I; i++) {
						this.V[ j ][ i ] += this.learningRate * signal_h[ j ][ 0 ] * x[ i ][ 0 ];
					}
				}
				// step 7
				m++;
				q++;
			}
			// step 8
			if( E > this.minErr ) {
				E = 0.0;
				p = 1.0;
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
	 * sigmoid activation method
	 * @param x
	 * @return result
	 */
	private double sigmoid( double x ) {
		double lambda = 1.0;
		return 1 / ( 1 + ( Math.exp( -1.0 * lambda * x ) ) );
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
