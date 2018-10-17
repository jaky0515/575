/*
 * GaussianEstimator.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class GaussianEstimator extends Estimator {
	protected Double sum = 0.0;
	protected Double sumsqr = 0.0;
	protected final static Double oneOverSqrt2PI = 1.0/Math.sqrt(2.0*Math.PI);

	/**
	 * Default constructor
	 */
	public GaussianEstimator() {
		
	}
	/**
	 * Increment values
	 * @param x
	 */
	public void add( Number x ) throws Exception {
		// increment values
		this.n++;
		this.sum += x.doubleValue();
		this.sumsqr += Math.pow(x.doubleValue(), 2);
	}
	/**
	 * Compute and return mean
	 * @return mean
	 */
	public Double getMean() {
		double mean = 0;
		if( this.n > 0 ) {
			// calculate only when number of samples is greater than 0
			mean = this.sum / this.n;
		}
		return mean;
	}
	/**
	 * Compute and return variance
	 * @return variance
	 */
	public Double getVariance() {
        double variance = 0;
        if( this.n > 0 ) {
        	// calculate only when number of samples is greater than 0
        	variance = ( this.sumsqr - ( Math.pow(this.sum, 2) / this.n ) ) / ( this.n - 1 );
        }
        return variance;
	}
	/**
	 * Compute and return probability
	 * @param x
	 * @return probability
	 */
	public Double getProbability( Number x ) {
		double prob = 0;
		if( this.n > 0 ) {
			// calculate only when number of samples is greater than 0
			Double var = this.getVariance();
			prob = oneOverSqrt2PI * ( 1 / Math.sqrt( var ) ) * Math.exp( (-1 * Math.pow( x.doubleValue() - this.getMean(), 2 ) ) / ( 2 * var ));
		}
		return prob;
	}
}