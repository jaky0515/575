import java.util.ArrayList;

/*
 * Scaler.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class Scaler extends Object {

	private Attributes attributes;
	private ArrayList<Double> mins;
	private ArrayList<Double> maxs;

	public Scaler() {
		this.mins = new ArrayList<Double>();	// set mins
		this.maxs = new ArrayList<Double>();	// set maxs
	}
	public void configure( DataSet ds ) throws Exception {
		// used only for training
		this.attributes = ds.getAttributes();
		// initialize mins and maxs
		this.mins = new ArrayList<Double>();
		this.maxs = new ArrayList<Double>();
		for(int i = 0; i < ds.getExamples().get(0).size(); i++) {
			this.mins.add(null);
			this.maxs.add(null);
		}
		// set mins and maxs
		for(int i = 0; i < ds.getExamples().size(); i++) {
			for(int j  = 0; j < ds.getExamples().get(i).size(); j++) {
				if( this.attributes.get(j) instanceof NominalAttribute ) {
					this.mins.set(j, null);
				}
				else {
					// compare mins
					if( this.mins.get(j) == null || this.mins.get(j) > ds.getExamples().get(i).get(j) ) {
						// set this value as a minimum
						this.mins.set(j, ds.getExamples().get(i).get(j));
					}
					// compare maxs
					if( this.maxs.get(j) == null || this.maxs.get(j) < ds.getExamples().get(i).get(j) ) {
						// set this value as a maximum
						this.maxs.set(j, ds.getExamples().get(i).get(j));
					}
				}
			}
		}
	}
	public DataSet scale( DataSet ds ) throws Exception {
		// configure to set up mins and maxs
		this.configure( ds );
		// scale numeric values in the examples
		for(int i = 0; i < ds.getExamples().size(); i++) {
			Example scaledEx = this.scale(ds.getExamples().get(i));
			ds.getExamples().set(i, scaledEx);
		}
		return ds;
	}
	public Example scale( Example example ) throws Exception {
		for(int i = 0; i < this.attributes.size(); i++) {
			if( this.attributes.get(i) instanceof NumericAttribute ) {
				// scale and replace the value for numeric attribute values
				Double min = this.mins.get(i);
				Double max = this.maxs.get(i);
				example.set( i, ( example.get(i) - min ) / ( max - min ) );
			}
		}
		return example;
	}
}