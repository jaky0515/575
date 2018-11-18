import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Perceptron extends Classifier implements Serializable, OptionHandler {
	protected double learningRate = 0.9;
	protected double minErr = 0.1;
	protected int j; 
	protected double[] weightVector;

	/**
	 * Default constructor
	 */
	public Perceptron() {

	}
	/**
	 * Constructor
	 * @param options - string arguments
	 * @throws Exception
	 */
	public Perceptron( String[] options ) throws Exception {
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
		// search for '-k' and if it exists, update the value of k
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
		return null;
	}
	/**
	 * Train using a given data-set
	 * @param dataset
	 */
	/*
	 * The perceptron iterates over the training set, 
	 updating the weight vector every time it encounters an incorrectly classified example.
	 * It iterates through the training examples 
	 until all examples are correctly classified
	 * Every time an example xi is misclassified, we add y_i * x_i to the weight vector.
	 * x_i = current example
	 * y_i = label of the current example (x_i)
	 */
	public void train( DataSet dataset ) throws Exception {
		// initialize the weight vector
		this.weightVector = new double[  dataset.getExamples().get(0).size() - 1 ];
		for(int i = 0; i < this.weightVector.length; i++) {
			this.weightVector[ i ] = 0.0;
		}
		boolean isConverged = false;
		int iterCount = 0;
		Attributes attrs = dataset.getAttributes();
		int classIdx = attrs.getClassIndex();
		while( !isConverged && iterCount < 500000 ) {
			iterCount++;
			isConverged = true;
			for( Example example : dataset.getExamples() ) {
				double y_i = example.get( classIdx );
				// do the encoding for the class label
				if( attrs.get( classIdx ).size() == 2 ) {
					y_i = ( y_i == 0.0 ) ? -1.0 : 1.0;
				}
				double product = 0.0;
				for(int i = 0; i < this.weightVector.length; i++) {
					double attrVal = example.get( i );
					// check if this attribute is a nominal attribute and binary
					if( attrs.get( i ) instanceof NominalAttribute && attrs.get( i ).size() == 2 ) {
						// do the encoding for this example attribute value
						attrVal = ( attrVal == 0.0 ) ? -1.0 : 1.0;
					}
					product += this.weightVector[ i ] * attrVal;
				}
				
				if( y_i * product <= 0 ) {
					// get sum of all the attribute values in this example
					double xSum = 0.0;
					for(int i = 0; i < example.size() - 1; i++) {
						double attrVal = example.get( i );
						// check if this attribute is a nominal attribute and binary
						if( attrs.get( i ) instanceof NominalAttribute && attrs.get( i ).size() == 2 ) {
							// do the encoding for this example attribute value
							attrVal = ( attrVal == 0.0 ) ? -1.0 : 1.0;
						}
						xSum += attrVal;
					}
					// update the weight vector
					for(int i = 0; i < this.weightVector.length; i++) {
						this.weightVector[ i ] += this.learningRate * y_i * xSum;
					}
					isConverged = false;
				}
			}
		}

		if( !isConverged ) {
			throw new FailedToConvergeException();
		}
	}
	/**
	 * Makes a deep copy of this class
	 * @return Classifier
	 */
	public Classifier clone() {
		return ( Perceptron ) Utils.deepClone( this );
	}
	/**
	 * Setter function for j (number of units in the hidden layer)
	 * @param j
	 */
	public void setJ( int j ) {
		this.j = j;
	}
	/**
	 * Main method
	 * @param args
	 */
	public static void main( String[] args ) {
		try {
			Evaluator evaluator = new Evaluator( new Perceptron(), args );
			Performance performance = evaluator.evaluate();
			System.out.println( performance );
		}
		catch ( Exception e ) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
		}
	}
}
