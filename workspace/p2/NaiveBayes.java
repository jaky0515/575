import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * NaiveBayes.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class NaiveBayes extends Classifier implements Serializable, OptionHandler {
	protected Attributes attributes;
	protected CategoricalEstimator classDistribution;
	protected ArrayList< ArrayList<Estimator> > classConditionalDistributions;

	public NaiveBayes() {

	}
	public NaiveBayes( String[] options ) throws Exception {
		// update later
	}
	public Performance classify( DataSet dataSet ) throws Exception {
		// update later
		return null;
	}
	public int classify( Example example ) throws Exception {
		// update later
		return -1;
	}
	public double[] getDistribution( Example example ) throws Exception {
		// update later
		return null;
	}
	public Classifier clone() {
		return (NaiveBayes) Utils.deepClone(this);
	}
	public void train( DataSet dataset ) throws Exception {
		// update later
	}
	public void setOptions( String[] options ) {
		// update later
	}
	public static void main( String[] args ) {
		try {
			Evaluator evaluator = new Evaluator( new NaiveBayes(), args );
			Performance performance = evaluator.evaluate();
			System.out.println( performance );
		} // try
		catch ( Exception e ) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
		} // catch
	} // NaiveBayes::main
}