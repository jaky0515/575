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

	public IBk() {
		this.scaler = new Scaler();	// set scaler
	}
	public IBk( String[] options ) throws Exception {
		this.scaler = new Scaler();	// set scaler
		this.setOptions( options );	// set options
	}
	public Performance classify( DataSet dataset ) throws Exception {
		Performance perform = new Performance( dataset.getAttributes() );
		Examples exs = dataset.getExamples();
		if( exs != null ) {
			for(int i = 0; i < exs.size(); i++) {
//				perform.add( this.classify( exs.get(i) ), this.getDistribution( exs.get(i) ) );
				perform.add( exs.get(i).get( dataset.getAttributes().getClassIndex() ).intValue(), this.getDistribution( exs.get(i) ) );
			}
		}
		return perform;
	}
	public int classify( Example query ) throws Exception {
		if( query == null || query.isEmpty() ) {
			throw new Exception("Error: invalid Example object passed-in!");
		}
		return Utils.maxIndex( this.getDistribution( query ) );
	}
	public double[] getDistribution( Example query ) throws Exception {
		// create distances and indices arrays with size = k
		double[] distances = new double[ this.k ];
		int[] indices = new int[ this.k ];
		// initialize values
		for(int i = 0; i < this.k; i++) {
			distances[ i ] = Double.MAX_VALUE;
		}
		
		DataSet scaledDs = this.scaler.scale( this.dataset );
		Example scaledQ = this.scaler.scale( query );
		for(int i = 0; i < scaledDs.getExamples().size(); i++) {
			Example example = scaledDs.getExamples().get(i);
			// calculate distance between this example and query
			double totalDist = 0;
			for(int j = 0; j < scaledDs.getAttributes().size()-1; j++) {
				if( scaledDs.getAttributes().get(j) instanceof NumericAttribute ) {
					// calculate using distance equation
					totalDist += Math.pow( scaledQ.get(j), example.get(j) );
				}
				else {
					// nominal attributes; 0 or 1
					totalDist += ( scaledQ.get(j).equals( example.get(j) ) ) ? 0 : 1;
				}
			}
			double distance = Math.sqrt(totalDist);
			// get index of max value in distances
			int maxIdx = Utils.maxIndex( distances );
			// if current distance is smaller update the value
			if( distance < distances[ maxIdx ] ) {
				distances[ maxIdx ] = distance;	// update distance value
				indices[ maxIdx ] = i;			// update index
			}
		}
		
		double[] distributions = new double[ scaledDs.getAttributes().getClassAttribute().size() ];
		for(int i = 0; i < indices.length; i++) {
			// get the index of a class that this nearest neighbor belongs to
			int idx = scaledDs.getExamples().get( indices[i] ).get( scaledDs.getAttributes().getClassIndex() ).intValue();
			distributions[ idx ]++;
		}
		// scale distributions
		for(int i = 0; i < distributions.length; i++) {
			distributions[ i ]  = distributions[ i ] / (double) this.k;
		}
		
		return distributions;
	}
	public void setOptions( String args[] ) {
		// search for '-k' and if it exists, update the value of k
		if( Arrays.asList(args).contains("-k") ) {
			this.setK( Integer.parseInt( args[Arrays.asList(args).indexOf("-k") + 1] ) );
		}
	}
	public void train( DataSet dataset ) throws Exception {
		this.dataset = dataset;
		this.scaler.configure( dataset );
	}
	public Classifier clone() {
		return (IBk) Utils.deepClone(this);
	}
	public void setK( int k ) {
		this.k = k;
	}
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