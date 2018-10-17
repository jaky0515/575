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
	
	/**
	 * Default constructor
	 */
	public NaiveBayes() {
		this.classDistribution = new CategoricalEstimator();	// set classDistribution
		this.classConditionalDistributions = new ArrayList< ArrayList<Estimator> >();	// set classConditionalDistributions
	}
	/**
	 * Constructor
	 * @param options - string arguments
	 * @throws Exception
	 */
	public NaiveBayes( String[] options ) throws Exception {
		if( options == null || options.length < 2 ) {
			throw new Exception("Error: invalid options passed-in!");
		}
		this.classDistribution = new CategoricalEstimator();	// set classDistribution
		this.classConditionalDistributions = new ArrayList< ArrayList<Estimator> >();	// set classConditionalDistributions
		this.setOptions( options );	// set options
	}
	/**
	 * Classifies a given data-set and return its performance
	 * @param dataset
	 * @return performance
	 */
	public Performance classify( DataSet dataSet ) throws Exception {
		Performance perform = new Performance( dataSet.getAttributes() );
		Examples exs = dataSet.getExamples();
		if( exs != null ) {
			for(int i = 0; i < exs.size(); i++) {
				// append this prediction result to the performance
				Example ex = exs.get(i);
				int actualClass = ex.get( dataSet.getAttributes().getClassIndex() ).intValue();
				perform.add( actualClass, this.getDistribution( ex ) );
			}
		}
		return perform;
	}
	/**
	 * Classifies a given example query and return the predicted label
	 * @param query
	 * @return predicted label
	 */
	public int classify( Example example ) throws Exception {
		// parameter validation
		if( example == null || example.isEmpty() ) {
			throw new Exception("Error: invalid example passed-in");
		}
		return Utils.maxIndex( this.getDistribution( example ) );
	}
	/**
	 * Compute the distribution by comparing a given query with other examples
	 * @param query
	 * @return double[] - distribution
	 */
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
	/**
	 * Makes a deep copy of this class
	 * @return Classifier
	 */
	public Classifier clone() {
		return (NaiveBayes) Utils.deepClone(this);
	}
	/**
	 * Train using a given data-set
	 * @param dataset
	 */
	public void train( DataSet dataset ) throws Exception {
		// validation
		if( dataset == null || dataset.getAttributes() == null || dataset.getAttributes().size() == 0 ||
				dataset.getExamples() == null || dataset.getExamples().isEmpty() ) {
			throw new Exception("Error: invalid DataSet object passed-in!");
		}
		// fill in classConditionalDistributions
		this.attributes = dataset.getAttributes();	// set attributes
		for(int i = 0; i < this.attributes.getClassAttribute().size(); i++) {
			ArrayList<Estimator> estimators = new ArrayList<Estimator>();
			for(int j = 0; j < this.attributes.size() - 1; j++) {
				if( this.attributes.get(j) instanceof NumericAttribute ) {
					// for numeric attribute; use Gaussian Estimator
					estimators.add( new GaussianEstimator() );
				} 
				else {
					// for nominal attribute; use Categorical Estimator
					estimators.add( new CategoricalEstimator( ( (NominalAttribute)this.attributes.get(j) ).size() ) );
				}
			}
			this.classConditionalDistributions.add( estimators );
		}
		// start training using the examples in a given data-set
		this.classDistribution = new CategoricalEstimator( this.attributes.getClassAttribute().size() );	// set CategoricalEstimator
		for(int i = 0; i < dataset.getExamples().size(); i++) {
			Example ex = dataset.getExamples().get(i);
			int exClass = ex.get( this.attributes.getClassIndex() ).intValue();	// get this example's class value
			// add 1 to every category
			this.classDistribution.add( exClass );
			for(int j = 0; j < this.attributes.size() - 1; j++) {
				this.classConditionalDistributions.get(exClass).get(j).add( ( ex.get(j) ) );
			}
		}
	}
	/**
	 * Sets the options for this classifier
	 * @param options - the arguments
	 */
	public void setOptions( String[] options ) {
		
	}
	/**
	 * Main method
	 * @param args
	 */
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