/*
 * Classifier.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public abstract class Classifier extends Object implements OptionHandler {
	public Classifier() {

	}
	public Classifier( String[] options ) throws Exception {
		// update later
	}
	abstract public Performance classify( DataSet dataset ) throws Exception;
	abstract public int classify( Example example ) throws Exception;
	public abstract Classifier clone();
	abstract public double[] getDistribution( Example example ) throws Exception;
	public void setOptions( String[] options ) {
		// update later
	}
	public String toString() {
		// update later
		return null;
	}
	abstract public void train( DataSet dataset ) throws Exception;
}