/*
 * GaussianEstimator.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class GaussianEstimator extends Estimator {
	protected Double sum = 0.0;
	protected Double sumsqr = 0.0;
	protected final static Double oneOverSqrt2PI = 1.0/Math.sqrt(2.0*Math.PI);

	public GaussianEstimator() {
		
	}
	public void add( Number x ) throws Exception {
		// increment values
		this.n++;
		this.sum += x.doubleValue();
		this.sumsqr += Math.pow(x.doubleValue(), 2);
	}
	public Double getMean() {
		return this.sum / ( (double) this.getN() );
	}
	public Double getVariance() {
		return ( this.sumsqr - ( Math.pow( this.sum, 2 ) / ( (double) this.getN() )) ) / ( (double) ( this.getN() - 1 ) );
	}
	public Double getProbability( Number x ) {
		Double var = this.getVariance();
		return oneOverSqrt2PI * ( 1.0 / Math.sqrt( var ) ) * Math.exp( (-1.0 * Math.pow( x.doubleValue() - this.getMean(), 2 ) ) / ( 2.0 * var ));
	}
}