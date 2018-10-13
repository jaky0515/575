import java.io.Serializable;
import java.util.ArrayList;

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
	public Classifier clone() {
		// update later
		return null;
	}
	public double[] getDistribution( Example example ) throws Exception {
		// update later
		return null;
	}
	public void setOptions( String[] options ) {
		// update later
	}
	public void train( DataSet dataset ) throws Exception {
		// update later
	}
}