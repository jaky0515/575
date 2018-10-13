import java.io.Serializable;

/*
 * IBk.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class IBk extends Classifier implements Serializable, OptionHandler {
	protected DataSet dataset;
	protected Scaler scaler;
	protected int k = 3;

	public IBk() {

	}
	public IBk( String[] options ) throws Exception {
		// update later
	}
	public Performance classify( DataSet dataset ) throws Exception {
		// update later
		return null;
	}
	public int classify( Example query ) throws Exception {
		// update later
		return -1;
	}
	public Classifier clone() {
		// update later
		return null;
	}
	public double[] getDistribution( Example query ) throws Exception {
		// update later
		return null;
	}
	public void setK( int k ) {
		this.k = k;
	}
	public void setOptions( String args[] ) {
		// update later
	}
	public void train( DataSet dataset ) throws Exception {
		// update later
	}
}