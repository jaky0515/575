import java.io.Serializable;
import java.util.ArrayList;

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
				Example ex = exs.get(i);
				perform.add( ex.get( dataSet.getAttributes().getClassIndex() ).intValue(), this.getDistribution( ex ) );
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
		// validation
		if( example == null || example.isEmpty() ) {
			throw new Exception("Error: invalid Example object passed-in!");
		}
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
		// validation
		if( dataset == null || dataset.getAttributes() == null || dataset.getAttributes().size() == 0 ||
				dataset.getExamples() == null || dataset.getExamples().isEmpty() ) {
			throw new Exception("Error: invalid DataSet object passed-in!");
		}
		
		// initialize classConditionalDistributions
		this.attributes = dataset.getAttributes();	// set attributes
		for(int i = 0; i < this.attributes.getClassAttribute().size(); i++) {
			ArrayList<Estimator> estimators = new ArrayList<Estimator>();
			for(int j = 0; j < dataset.attributes.size() - 1; j++) {
				if( dataset.attributes.get(j) instanceof NominalAttribute ) {
					// for nominal attribute; use Categorical Estimator
					estimators.add( new CategoricalEstimator( ( (NominalAttribute) dataset.attributes.get(j) ).size() ) );
				} 
				else {
					// for numeric attribute; use Gaussian Estimator
					estimators.add( new GaussianEstimator() );
				}
			}
			this.classConditionalDistributions.add(estimators);
		}

		this.classDistribution = new CategoricalEstimator( this.attributes.getClassAttribute().size() );	// set classDistribution
		for(int i = 0; i < dataset.getExamples().size(); i++) {
			Example ex = dataset.getExamples().get(i);
			Double exClass = ex.get( this.attributes.getClassIndex() );
			this.classDistribution.add( exClass );
			for(int j = 0; j < this.attributes.size() - 1; j++) {
				this.classConditionalDistributions.get( exClass.intValue() ).get(j).add( ( ex.get(j) ) );
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