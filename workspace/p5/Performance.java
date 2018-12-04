import java.util.ArrayList;
import java.util.List;

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
	private List<Double> prNeg = new ArrayList<Double>();
	private List<Double> prPos = new ArrayList<Double>();
	private Double avgAUC = null;
	private double aucSum = 0.0;
	private double aucSumSqr = 0.0;
	
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
		if( this.prNeg == null || this.prPos == null ) {
			this.prNeg = new ArrayList<Double>();
			this.prPos = new ArrayList<Double>();
		}
		// increment values
		this.n++;
		// get the best prediction out of pr[]
		int predicted = Utils.maxIndex( pr );
		if( this.c == 2 ) {
			// perform this only when two-class data-set is provided
			if( predicted == 0 ) {
				this.prPos.add( pr[ 0 ] );
			}
			else {
				this.prNeg.add( pr[ 1 ] );
			}
		}
		this.confusionMatrix[ actual ][ predicted ]++;
		// increment correct if actual and predicted match
		this.corrects += ( (double) actual == predicted ) ? 1 : 0;
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
		if( this.c == 2 ) {
			// perform this only when two-class data-set is provided
			if( this.prNeg == null || this.prPos == null ) {
				this.prNeg = new ArrayList<Double>();
				this.prPos = new ArrayList<Double>();
			}
			this.prNeg.addAll( p.prNeg );
			this.prPos.addAll( p.prPos );
			double auc = p.getAUC();
			this.aucSum += auc;
			this.aucSumSqr += Math.pow( auc, 2 );
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
		if( this.c > 2 ) {
			strBuilder.append("\n\t** This data-set is not a two-class data-set, therefore, AUC and SDAUC value do not exist");
		}
		else {
			strBuilder.append("\n\t** AUC = ").append( Math.round( ( ( this.avgAUC == null ) ? this.getAUC() : this.avgAUC ) * 100.00 ) ).append( "%" );
			strBuilder.append("\n\t** SDAUC = ").append( this.getSDAUC() );
		}
		return strBuilder.toString();
	}
	/**
	 * Helper method for computing AUC
	 * @param n
	 * @param p
	 * @return
	 */
	private double i( double n, double p ) {
		return ( n < p ) ? 1.0 : ( ( n == p ) ? 0.5 : 0.0 );
	}
	/**
	 * Compute and return area under the ROC curve
	 * @return AUC
	 */
	public double getAUC() {
		if( this.c > 2 ) {
			return 0;
		}
		double auc = 0;
		for( Double neg : this.prNeg ) {
			for( Double pos : this.prPos ) {
				auc += this.i( neg, pos );
			}
		}
		// averaged AUC results if we use k-folds cross validation
		return ( this.prNeg.isEmpty() || this.prPos.isEmpty() ) ? 0.0 : auc / ( this.prNeg.size() * this.prPos.size() );
	}
	/**
	 * Compute and return the SD of AUC
	 * @return
	 */
	public double getSDAUC() {
		// sd of averaged auc over folds
		return ( this.m == 0 ) ? 0 : ( (this.aucSumSqr - ( Math.pow(this.aucSum, 2) / (double)this.m ) ) / ( (double)( this.m - 1 ) ) );
	}
	/**
	 * Setter method for avgAUC
	 * @param avgAUC
	 */
	public void setAvgAUC( double avgAUC ) {
		this.avgAUC = avgAUC;
	}
}