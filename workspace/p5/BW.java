import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class BW extends Classifier implements Serializable, OptionHandler {

	private boolean doVoting = false;
	private double alpha = 1.5;
	private double beta = 0.5;
	private double threshold = 1.0;
	private double initU = 2.0;
	private double initV = 1.0;
	private double c = 0.0;
	private ArrayList< Integer > cs = new ArrayList< Integer >();
	private ArrayList< double[] > encodedExs = new ArrayList< double[] >();
	private ArrayList< Double > encodedLabels = new ArrayList< Double >();
	private Attributes attributes;
	private double[] U;
	private double[] V;
	private double[] totalU;
	private double[] totalV;

	public BW() {

	}
	public BW( String[] options ) throws Exception {
		if( options == null || options.length < 2 ) {
			throw new Exception("Error: invalid options passed-in!");
		}
		this.setOptions( options );
	}
	public Performance classify(DataSet dataset) throws Exception {
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
	public int classify(Example example) throws Exception {
		if( example == null || example.isEmpty() ) {
			throw new Exception("Error: invalid Example object passed-in!");
		}
		return Utils.maxIndex( this.getDistribution( example ) );
	}
	public double[] getDistribution(Example example) throws Exception {
		// example pre-processing
		// encode example
		ArrayList< ArrayList< Double[] > > encodedAttrs = this.attributes.getEncodedAttrs();
		int exampleLen = 0;
		for(int i = 0; i < encodedAttrs.size() - 1; i++) {
			exampleLen += encodedAttrs.get( i ).get( 0 ).length;
		}
		double[] x_t = Utils.getEncodedExample( example, exampleLen, this.attributes );
		// augmentation on example
		x_t = this.augmentation( x_t );
		// normalization on example
		x_t = this.normalization( x_t );
		double y_hat;
		if( this.doVoting ) {
			double[] newU = new double[ this.totalU.length ];
			double[] newV = new double[ this.totalV.length ];
			for(int i = 0; i < newU.length; i++) {
				newU[ i ] = this.totalU[ i ] / this.c;
				newV[ i ] = this.totalV[ i ] / this.c;
			}
			y_hat = this.scoreFunction( newU, newV, x_t );
			y_hat = ( y_hat > 0.0 ) ? 1.0 : ( ( y_hat == 0.0 ) ? 0.0 : -1.0);
		}
		else {
			y_hat =  scoreFunction( this.U, this.V, x_t );
		}
		double[] dist = new double[ 2 ];
		dist[ 0 ] = ( y_hat > 0.0 ) ? 0.0 : 1.0;
		dist[ 1 ] = ( y_hat > 0.0 ) ? 1.0 : 0.0;
		return dist;
	}
	private void encodeExamples( Examples examples ) {
		this.encodedExs = new ArrayList< double[] >();
		int exampleLen = 0;
		for(int i = 0; i < this.attributes.getEncodedAttrs().size() - 1; i++) {
			exampleLen += this.attributes.getEncodedAttrs().get( i ).get( 0 ).length;
		}
		for(int i = 0; i < examples.size(); i++) {
			Example ex = examples.get( i );
			double[] encodedEx = Utils.getEncodedExample( ex, exampleLen, this.attributes );
			this.encodedExs.add( encodedEx );
		}
	}
	private double[] augmentation( double[] example ) {
		double[] newEx = new double[ example.length + 1 ];
		newEx = Arrays.copyOf( example, newEx.length );
		newEx[ newEx.length - 1 ] = 1.0;
		return newEx;
	}
	private double[] normalization( double[] example ) {
		double total = 0.0;
		for(int j = 0; j < example.length; j++) {
			total += example[ j ];
		}
		for(int j = 0; j < example.length; j++) {
			example[ j ] /= total;
		}
		return example;
	}
	private ArrayList< double[] > preProcessExamples( Examples examples ) {
		ArrayList< double[] > preProcessedExs = new ArrayList< double[] >();
		for( Example example : examples ) {
			double label = example.get( this.attributes.getClassIndex() );
			this.encodedLabels.add( ( label == 0.0 ) ? -1.0 : 1.0 );
		}
		// encoding
		this.encodeExamples( examples );
		// augmentation
		for( double[] example : this.encodedExs ) {
			preProcessedExs.add( this.augmentation( example ) );
		}
		// normalization
		for(int i = 0; i < preProcessedExs.size(); i++) {
			preProcessedExs.set( i, this.normalization( preProcessedExs.get( i ) ) ); 
		}
		return preProcessedExs;
	}
	private double scoreFunction( double[] u, double[] v, double[] x ) {
		double product = Utils.dotProduct( x, u ) - Utils.dotProduct( x, v ) - this.threshold;
		return ( product > 0.0 ) ? 1.0 : ( ( product == 0.0 ) ? 0.0 : -1.0);
	}
	private void initWeightVectors( int length ) {
		this.U = new double[ length ];
		this.V = new double[ length ];
		for(int i = 0; i < this.U.length; i++) {
			this.U[ i ] = this.initU;
			this.V[ i ] = this.initV;
		}
		
		this.totalU = new double[ length ];
		this.totalV = new double[ length ];
		for(int i = 0; i < this.U.length; i++) {
			this.totalU[ i ] = 0.0;
			this.totalV[ i ] = 0.0;
		}
	}
	private void updateWeightVectors( double[] x_t, double y_t ) {
		for(int j = 0; j < this.U.length; j++) {
			if( x_t[ j ] > 0 ) {
				if( y_t > 0 ) {
					this.U[ j ] *= this.alpha;
					this.V[ j ] *= this.beta;
				}
				else {
					this.U[ j ] *= this.beta;
					this.V[ j ] *= this.alpha;
				}
			}
		}
	}
	public void train( DataSet dataset ) throws Exception {
		// binary encode attributes
		dataset.getAttributes().encode( false );
		this.attributes = dataset.getAttributes();
		// pre-processing examples
		ArrayList< double[] > preProcessedExs = this.preProcessExamples( dataset.getExamples() );
		this.cs = new ArrayList< Integer >();
		// initialize vectors
		this.initWeightVectors( preProcessedExs.get( 0 ).length );
		// start updating the vectors
		int i = 0;
		int c_i = 0;
		for(int t = 0; t < preProcessedExs.size(); t++) {
			double[] x_t = preProcessedExs.get( t );
			double y_hat =  this.scoreFunction( this.U, this.V, x_t );
			double y_t = this.encodedLabels.get( t );
			if( y_hat != y_t ) {
				double[] U_i = this.U;
				double[] V_i = this.V;
				for(int j = 0; j < U_i.length; j++) {
					this.totalU[ j ] += U_i[ j ] * c_i;
					this.totalV[ j ] += V_i[ j ] * c_i;
				}
				// when prediction is a mistake; Update model w_i → w_i + 1
				this.updateWeightVectors( x_t, y_t );
				this.cs.add( i, c_i );
				c_i = 0;
				i++;
			}
			else {
				c_i++;
				this.c++;
			}
		}
	}
	public void setOptions( String[] options ) throws Exception {
		// validation
		if( options == null ) {
			throw new Exception("Error: invalid options passed-in!");
		}
		// search for -v and if it exists, update the value of doVoting
		this.doVoting = Arrays.asList( options ).contains( "-v" ) ? true : false;
	}
	public Classifier clone() {
		return ( BW ) Utils.deepClone( this );
	}
	/**
	 * Main method
	 * @param args
	 */
	public static void main( String[] args ) {
		try {
			Evaluator evaluator = new Evaluator( new BW(), args );
			Performance performance = evaluator.evaluate();
			System.out.println( performance );
		}
		catch ( Exception e ) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
		}
	}
}
