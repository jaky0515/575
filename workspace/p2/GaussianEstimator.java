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
		// update later
	}
	public Double getMean() {
		// update later
		return null;
	}
	public Double getVariance() {
		// update later
		return null;
	}
	public Double getProbability( Number x ) {
		// update later
		return null;
	}
}