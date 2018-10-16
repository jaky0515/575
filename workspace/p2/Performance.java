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
	private int m = 0;            // number of additions (number of performances added to the current Performance object)

	public Performance( Attributes attributes ) throws Exception {
		if( attributes == null || attributes.size() == 0 ) {
			throw new Exception("Error: invalid attributes provided!");
		}
		this.attributes = attributes;
		if (this.attributes.get(this.attributes.getClassIndex()) instanceof NominalAttribute) {
			this.c = ((NominalAttribute) this.attributes.get(this.attributes.getClassIndex())).size();
		}
		// initialize confusionMatrix
		this.confusionMatrix = new int[c][c];
		for (int i = 0; i < this.c; i++) {
			for (int j = 0; j < this.c; j++) {
				this.confusionMatrix[i][j] = 0;
			}
		}
	}
	public void add( int actual, double[] pr ) {
		// update later
		// pr = output from getDistribution()
		// increment values
		this.n++;
		for(int i = 0; i < pr.length; i++) {
			this.confusionMatrix[ actual ][ i ]++;
			if(actual == this.confusionMatrix[ actual ][ i ]) {
				this.corrects++;
			}
			this.confusionMatrix[ actual ][ i ]++;
		}
	}
	public void add( Performance p ) throws Exception {
		// parameter validation
		if( p == null ) {
			throw new Exception("Error: invalid Performance object passed-in!");
		}
		// increment values
		this.m++;
		this.n += p.n;
		this.corrects += p.corrects;
		double accuracy = p.getAccuracy();
		this.sum += accuracy;
		this.sumSqr += Math.pow( accuracy, 2 );
		for(int i = 0; i < this.confusionMatrix.length; i++) {
			for(int j = 0; j < this.confusionMatrix[i].length; j++) {
				this.confusionMatrix[i][j] += p.confusionMatrix[i][j];
			}
		}
	}
	public double getAccuracy() {
		if( this.n == 0 ) {
			return 0;
		}
		else {
			return (double)this.corrects / (double)this.n;
		}
	}
	public double getSDAcc() {
		if( this.m == 0 ) {
			return 0;
		}
		else {
			return  (this.sumSqr - ( Math.pow(this.sum, 2) / (double)this.m ) ) / (double)( this.m - 1 );
		}
	}
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("Performance:\n\t** Accuracy = ").append(this.getAccuracy());
		strBuilder.append("\n\t** SDAcc = ").append(this.getSDAcc());
		return strBuilder.toString();
	}
}