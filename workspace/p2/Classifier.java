/*
 * Classifier.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public abstract class Classifier extends Object implements OptionHandler {
	public Classifier() {

	}
	public Classifier( String[] options ) throws Exception {
		this.setOptions(options);
	}
	
	abstract public Performance classify( DataSet dataset ) throws Exception;
	abstract public int classify( Example example ) throws Exception;
	abstract public double[] getDistribution( Example example ) throws Exception;
	abstract public void train( DataSet dataset ) throws Exception;
	public abstract Classifier clone();
	
	public void setOptions( String[] options ) {
		if( this instanceof IBk) {
			( (IBk) this ).setOptions( options );
		}
		else if( this instanceof NaiveBayes ) {
			( (NaiveBayes) this ).setOptions( options );
		}
	}
	public String toString() {
		// update later
		return null;
	}
}