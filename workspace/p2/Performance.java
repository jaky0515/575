/*
 * Performance.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class Performance extends Object {

	private Attributes attributes;
	private int[][] confusionMatrix;
	private int corrects = 0;
	private double sum = 0.0;
	private double sumSqr = 0.0;
	private int c;                // number of classes
	private int n = 0;            // number of predictions
	private int m = 0;            // number of additions

	public Performance( Attributes attributes ) throws Exception {
		this.attributes = attributes;
	}
	public void add( int actual, double[] pr ) {
		// update later
	}
	public void add( Performance p ) throws Exception {
		// update later
	}
	public double getAccuracy() {
		// update later
		return -1;
	}
	public double getSDAcc() {
		// update later
		return -1;
	}
	public String toString() {
		// update later
		return null;
	}
}