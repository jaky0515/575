import java.io.Serializable;
import java.util.ArrayList;

/*
 * CategoricalEstimator.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class CategoricalEstimator extends Estimator implements Serializable {
	protected ArrayList<Integer> dist;

	public CategoricalEstimator() {
		this.dist = new ArrayList<Integer>();	// set dist
	}
	public CategoricalEstimator( Integer k ) {
		this.dist = new ArrayList<Integer>();	// set dist
		// initialize dist
		for(int i = 0; i < k; i++) {
			this.dist.add(0);
		}
	}
	public void add( Number x ) throws Exception {
		// increment values
		this.n++;
		// add 1 to this class value
		int newVal = this.dist.get( x.intValue() ) + 1;
		this.dist.set( x.intValue(), newVal );
	}
	public Double getProbability( Number x ) {
		double smoothed = this.dist.get( x.intValue() ) + 1; // add one smoothing
		double total = this.n + this.dist.size();
		return smoothed / total ;
	}
}