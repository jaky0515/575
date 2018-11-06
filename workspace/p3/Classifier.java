/*
 * Classifier.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public abstract class Classifier extends Object implements OptionHandler {
	/**
	 * Default constructor
	 */
	public Classifier() {

	}
	/**
	 * Constructor
	 * @param options - the arguments
	 * @throws Exception
	 */
	public Classifier( String[] options ) throws Exception {
		this.setOptions(options);
	}
	
	abstract public Performance classify( DataSet dataset ) throws Exception;
	abstract public int classify( Example example ) throws Exception;
	abstract public double[] getDistribution( Example example ) throws Exception;
	abstract public void train( DataSet dataset ) throws Exception;
	public abstract Classifier clone();
	
	/**
	 * Sets the options for this classifier
	 * @param options - the arguments
	 * @throws Exception 
	 */
	public void setOptions( String[] options ) throws Exception {
		if( this instanceof IBk) {
			( (IBk) this ).setOptions( options );
		}
		else if( this instanceof NaiveBayes ) {
			( (NaiveBayes) this ).setOptions( options );
		}
		else if( this instanceof DT ) {
			( (DT) this ).setOptions( options );
		}
	}
	/**
	 * Returns a string representation of this classifier
	 */
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		if( this instanceof IBk ) {
			strBuilder.append("k = ").append( ( (IBk) this ).k );
			strBuilder.append("\ndataset: \n").append( ( (IBk) this ).dataset );
		}
		else if( this instanceof NaiveBayes ) {
			strBuilder.append("attributes:\n").append( ( (NaiveBayes) this ).attributes );
		}
		else if( this instanceof DT ) {
			strBuilder.append("attributes:\n").append( ( (DT) this ).attributes );
			strBuilder.append("\nroot: \n").append( ( (DT) this ).root );
		}
		return strBuilder.toString();
	}
}