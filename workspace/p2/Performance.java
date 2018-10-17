/*
 * Performance.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class Performance extends Object {

	private Attributes attributes;
	private int[][] confusionMatrix;
	private int corrects = 0;
	private double sum = 0.0;		// sum of the accuracy
	private double sumSqr = 0.0;	// sum of the squares of the accuracies
	private int c = 0;				// number of classes
	private int n = 0;				// number of predictions
	private int m = 0;				// number of additions (number of performances added to the current Performance object)
	/**
	 * Constructor
	 * @param attributes
	 * @throws Exception
	 */
	public Performance( Attributes attributes ) throws Exception {
		if( attributes == null || attributes.size() == 0 ) {
			throw new Exception("Error: invalid attributes provided!");
		}
		this.attributes = attributes;	// set attributes
		if( this.attributes.get( this.attributes.getClassIndex() ) instanceof NominalAttribute ) {
			// if the class attribute is a NominalAttribute, update number of classes (c)
			this.c = ( (NominalAttribute) this.attributes.get( this.attributes.getClassIndex() ) ).size();
		}
		this.confusionMatrix = new int[ this.c ][ this.c ];	// set confusion matrix
	}
	/**
	 * Increment values
	 * @param actual
	 * @param pr
	 */
	public void add( int actual, double[] pr ) {
		// increment values
		this.n++;
		// get the best prediction out of pr[]
		int predicted = Utils.maxIndex( pr );
		this.confusionMatrix[ actual ][ predicted ]++;
		// increment correct if actual and predicted match
		this.corrects += ( actual == predicted ) ? 1 : 0;
	}
	/**
	 * Increment values using a given Performance object
	 * @param p
	 * @throws Exception
	 */
	public void add( Performance p ) throws Exception {
		// parameter validation
		if( p == null ) {
			throw new Exception("Error: invalid Performance object passed-in!");
		}
		// add values
		double accuracy = p.getAccuracy();
		this.sum += accuracy;
		this.sumSqr += Math.pow( accuracy, 2 );
		this.m++;
		this.n += p.n;
		this.corrects += p.corrects;
		for(int i = 0; i < this.confusionMatrix.length; i++) {
			for(int j = 0; j < this.confusionMatrix[i].length; j++) {
				this.confusionMatrix[i][j] += p.confusionMatrix[i][j];
			}
		}
	}
	/**
	 * Compute and return accuracy
	 * @return accuracy
	 */
	public double getAccuracy() {
		return ( this.n == 0) ? 0 : ( ( (double)this.corrects ) / ( (double)this.n ) );
	}
	/**
	 * Compute and return standard deviation
	 * @return
	 */
	public double getSDAcc() {
		return ( this.m == 0 ) ? 0 : ( (this.sumSqr - ( Math.pow(this.sum, 2) / (double)this.m ) ) / ( (double)( this.m - 1 ) ) );
	}
	/**
	 * Returns a string representation of this class
	 */
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("Performance:\n\t** Accuracy = ").append( Math.round( this.getAccuracy() * 100.00 ) ).append( "%" );
		strBuilder.append("\n\t** SDAcc = ").append( this.getSDAcc() );
		return strBuilder.toString();
	}
}