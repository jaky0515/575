import java.io.Serializable;
import java.util.Arrays;

/*
 * IBk.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class IBk extends Classifier implements Serializable, OptionHandler {
	protected DataSet dataset;
	protected Scaler scaler;
	protected int k = 3;

	/**
	 * Default constructor
	 */
	public IBk() {
		this.scaler = new Scaler();	// set scaler
	}
	/**
	 * Constructor
	 * @param options - string arguments
	 * @throws Exception
	 */
	public IBk( String[] options ) throws Exception {
		if( options == null || options.length < 2 ) {
			throw new Exception("Error: invalid options passed-in!");
		}
		this.scaler = new Scaler();	// set scaler
		this.setOptions( options );	// set options
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
	public double[] getDistribution( Example query ) throws Exception {
		if( query == null || query.isEmpty() ) {
			throw new Exception("Error: invalid Example object passed-in!");
		}
		
		// create distances and indices arrays with size = k
		double[] distances = new double[ this.k ];
		int[] indices = new int[ this.k ];
		// initialize values
		for(int i = 0; i < this.k; i++) {
			distances[ i ] = Double.MAX_VALUE;
		}
		Example scaledQ = this.scaler.scale( query );
		for(int i = 0; i < this.dataset.getExamples().size(); i++) {
			Example example = this.dataset.getExamples().get(i);
			// calculate distance between this example and query
			double totalDist = 0;
			for(int j = 0; j < this.dataset.getAttributes().size()-1; j++) {
				if( this.dataset.getAttributes().get(j) instanceof NumericAttribute ) {
					// calculate using distance equation
					totalDist += Math.pow( ( scaledQ.get(j) - example.get(j) ), 2 );
				}
				else {
					// nominal attributes; 0 or 1
					totalDist += ( scaledQ.get(j).equals( example.get(j) ) ) ? 0 : 1;
				}
			}
			double distance = Math.sqrt( totalDist );
			// get index of max value in distances
			int maxIdx = Utils.maxIndex( distances );
			// if current distance is smaller update the value
			if( distance <= distances[ maxIdx ] ) {
				distances[ maxIdx ] = distance;	// update distance value
				indices[ maxIdx ] = i;			// update index
			}
		}
		double[] distributions = new double[ this.dataset.getAttributes().getClassAttribute().size() ];
		for(int i = 0; i < indices.length; i++) {
			// get the index of a class that this nearest neighbor belongs to
			int idx = this.dataset.getExamples().get( indices[i] ).get( this.dataset.getAttributes().getClassIndex() ).intValue();
			distributions[ idx ]++;
		}
		// scale distributions
		for(int i = 0; i < distributions.length; i++) {
			distributions[ i ]  = distributions[ i ] / (double) this.k;
		}
		return distributions;
	}
	/**
	 * Sets the options for this classifier
	 * @param options - the arguments
	 */
	public void setOptions( String args[] ) {
		// search for '-k' and if it exists, update the value of k
		if( Arrays.asList(args).contains("-k") ) {
			this.setK( Integer.parseInt( args[Arrays.asList(args).indexOf("-k") + 1] ) );
		}
	}
	/**
	 * Train using a given data-set
	 * @param dataset
	 */
	public void train( DataSet dataset ) throws Exception {
		this.scaler.configure( dataset );
		if( dataset.getHasNumericAttributes() ) {
			// scale this data-set only when numeric attributes exist
			this.dataset = this.scaler.scale( dataset );
		}
		else {
			this.dataset = dataset;
		}
	}
	/**
	 * Makes a deep copy of this class
	 * @return Classifier
	 */
	public Classifier clone() {
		return (IBk) Utils.deepClone(this);
	}
	/**
	 * Replace current k with new value
	 * @param k
	 */
	public void setK( int k ) {
		this.k = k;
	}
	/**
	 * Main method
	 * @param args
	 */
	public static void main( String[] args ) {
		try {
			Evaluator evaluator = new Evaluator( new IBk(), args );
			Performance performance = evaluator.evaluate();
			System.out.println( performance );
		} // try
		catch ( Exception e ) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
		} // catch
	} // IBk::main
}