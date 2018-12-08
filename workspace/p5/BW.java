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
	private ArrayList< Integer > c = new ArrayList< Integer >();
	private ArrayList< double[] > u = new ArrayList< double[] >();	// positive model
	private ArrayList< double[] > v = new ArrayList< double[] >();	// negative model
	private ArrayList< double[] > encodedExs = new ArrayList< double[] >();
	private ArrayList< Double > encodedLabels = new ArrayList< Double >();
	private Attributes attributes;

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
		double y_hat =  scoreFunction( this.u.get( this.u.size() - 1 ), this.v.get( this.v.size() - 1 ), x_t );
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
	private ArrayList< double[] > preProcessExamples( DataSet dataset ) {
		ArrayList< double[] > preProcessedExs = new ArrayList< double[] >();
		Examples examples = dataset.getExamples();
		/*
		Examples examples = new Examples( this.attributes );
		// clean invalid examples
		if( dataset.name.trim().equals("house-votes-84") ) {
			// remove any examples with attribute value 'u'
			for( Example example : dataset.getExamples() ) {
				if( example.contains( 2.0 ) ) {
					continue;
				}
				examples.add( example );
				double label = example.get( this.attributes.getClassIndex() );
				this.encodedLabels.add( ( label == 0.0) ? -1.0 : 1.0 );
			}
		}
		*/
		for( Example example : dataset.getExamples() ) {
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
	private double[] getNewU( int length ) {
		double[] newU = new double[ length ];
		for(int i = 0; i < newU.length; i++) {
			newU[ i ] = this.initU;
		}
		return newU;
	}
	private double[] getNewV( int length ) {
		double[] newV = new double[ length ];
		for(int i = 0; i < newV.length; i++) {
			newV[ i ] = this.initV;
		}
		return newV;
	}
	private double scoreFunction( double[] u, double[] v, double[] x ) {
		double product = Utils.dotProduct( x, u ) - Utils.dotProduct( x, v ) - this.threshold;
		if( product == 0.0 ) System.out.println( product );
		return ( product > 0.0 ) ? 1.0 : -1.0;
	}
	private double[] getNextU( double[] u_i, double y_t ) {
		double[] nextU = new double[ u_i.length ];
		for(int j = 0; j < u_i.length; j++) {
			if( y_t > 0 ) {
				nextU[ j ] = u_i[ j ] * this.alpha;
			}
			else {
				nextU[ j ] = u_i[ j ] * this.beta;
			}
		}
		return nextU;
	}
	private double[] getNextV( double[] v_i, double y_t ) {
		double[] nextV = new double[ v_i.length ];
		for(int j = 0; j < v_i.length; j++) {
			if( y_t > 0 ) {
				nextV[ j ] = v_i[ j ] * this.beta;
			}
			else {
				nextV[ j ] = v_i[ j ] * this.alpha;
			}
		}
		return nextV;
	}
	private double voting( ArrayList< double[] > w ){
		double Z = 0.0;
		for( double c_i : this.c ) {
			Z += c_i;
		}
		double productSum = 0.0;
		for(int i = 0; i < w.size(); i++) {
			for(int j = 0; j < w.get( i ).length; j++) {
				productSum += w.get( i )[ j ] * this.c.get( i );
			}
		}
		return productSum / Z;
	}
	public void train(DataSet dataset) throws Exception {
		// binary encode attributes
		dataset.getAttributes().encode( false );
		this.attributes = dataset.getAttributes();
		// pre-processing examples
		ArrayList< double[] > preProcessedExs = this.preProcessExamples( dataset );
		this.c = new ArrayList< Integer >();
		this.u = new ArrayList< double[] >();	// positive model
		this.v = new ArrayList< double[] >();	// negative model
		// add weight vector to vector u and v
		this.u.add( this.getNewU( preProcessedExs.get( 0 ).length ) );
		this.v.add( this.getNewV( preProcessedExs.get( 0 ).length ) );
		this.c.add( 0 );
		int i = 0;
		for(int t = 0; t < preProcessedExs.size(); t++) {
			double[] x_t = preProcessedExs.get( t );
			double y_hat =  scoreFunction( this.u.get( i ), this.v.get( i ), x_t );
			double y_t = this.encodedLabels.get( t );
			if( y_hat != y_t ) {
				// when prediction is a mistake; Update model w_i → w_i + 1
				this.u.add( i + 1, this.getNextU( this.u.get( i ), y_t) );
				this.v.add( i + 1, this.getNextV( this.v.get( i ), y_t) );
				this.c.add( i + 1, this.c.get( i ) );
				i++;
			}
			else {
				this.c.set( i, this.c.get( i ) + 1 );
			}
		}
		System.out.println("Training done");
	}
	public void setOptions( String[] options ) throws Exception {
		// validation
		if( options == null ) {
			throw new Exception("Error: invalid options passed-in!");
		}
		// search for -v and if it exists, update the value of doVoting
		this.doVoting = Arrays.asList( options ).contains( "-v" ) ? false : true;
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
