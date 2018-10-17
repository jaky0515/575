import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * NaiveBayes.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class NaiveBayes extends Classifier implements Serializable, OptionHandler {
	protected Attributes attributes;
	protected CategoricalEstimator classDistribution;
	protected ArrayList< ArrayList<Estimator> > classConditionalDistributions;

	public NaiveBayes() {
		this.classDistribution = new CategoricalEstimator();
		this.classConditionalDistributions = new ArrayList< ArrayList<Estimator> >();
	}
	public NaiveBayes( String[] options ) throws Exception {
		this.setOptions( options );	// set options
	}
	public Performance classify( DataSet dataSet ) throws Exception {
		Performance perform = new Performance( dataSet.getAttributes() );
		Examples exs = dataSet.getExamples();
		if( exs != null ) {
			for(int i = 0; i < exs.size(); i++) {
				//				perform.add( this.classify( exs.get(i) ), this.getDistribution( exs.get(i) ) );
				perform.add( exs.get(i).get( dataSet.getAttributes().getClassIndex() ).intValue(), this.getDistribution( exs.get(i) ) );
			}
		}
		return perform;
	}
	public int classify( Example example ) throws Exception {
		// parameter validation
		if( example == null || example.isEmpty() ) {
			throw new Exception("Error: invalid example passed-in");
		}
		return Utils.maxIndex( this.getDistribution( example ) );
	}
	public double[] getDistribution( Example example ) throws Exception {
		double[] dist = new double[ this.attributes.getClassAttribute().size() ];
		double sum = 0;
		for(int i = 0; i < this.attributes.getClassAttribute().size(); i++) {
			double classLabel = 1;
			for(int j = 0; j < this.attributes.size() - 1; j++) {
				classLabel *= this.classConditionalDistributions.get(i).get(j).getProbability( example.get(j) );
			}
			dist[ i ] = (double) this.classDistribution.dist.get(i) / this.classDistribution.n * classLabel;
			sum += dist[ i ];
		}
		for(int i = 0; i < dist.length; i++) {
			dist[ i ] /= sum;
		}
		return dist;
	}
	public Classifier clone() {
		return (NaiveBayes) Utils.deepClone(this);
	}
	public void train( DataSet dataset ) throws Exception {
		this.attributes = dataset.getAttributes();
		this.classDistribution = new CategoricalEstimator( this.attributes.getClassAttribute().size() );

		for (int i = 0; i < this.attributes.getClassAttribute().size(); i++) {
			ArrayList<Estimator> estimators = new ArrayList<Estimator>();
			for (int j = 0; j < dataset.attributes.size() - 1; j++) {
				if (dataset.attributes.get(j) instanceof NominalAttribute) {
					NominalAttribute nominalAttr = (NominalAttribute) dataset.attributes.get(j);
					estimators.add( new CategoricalEstimator( nominalAttr.size() ) );
				} 
				else {
					estimators.add( new GaussianEstimator() );
				}
			}
			this.classConditionalDistributions.add(estimators);
		}

		// train
		for (int i = 0; i < dataset.getExamples().size(); i++) {
			this.classDistribution.add(dataset.getExamples().get(i).get(this.attributes.getClassIndex()));
			for (int j = 0; j < this.attributes.size() - 1; j++) {
				this.classConditionalDistributions
						.get(dataset.getExamples().get(i).get(this.attributes.getClassIndex()).intValue()).get(j)
						.add((dataset.getExamples().get(i).get(j)));
			}
		}
	}
	public void setOptions( String[] options ) {
		
	}
	public static void main( String[] args ) {
		try {
			Evaluator evaluator = new Evaluator( new NaiveBayes(), args );
			Performance performance = evaluator.evaluate();
			System.out.println( performance );
		} // try
		catch ( Exception e ) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
		} // catch
	} // NaiveBayes::main
}