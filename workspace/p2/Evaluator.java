import java.util.Arrays;
import java.util.List;
import java.util.Random;

/*
 * Evaluator.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class Evaluator implements OptionHandler {

	private long seed = 2026875034;
	private Random random;
	private int folds = 10;
	private Classifier classifier;
	private TrainTestSets tts;
	
	/**
	 * Default constructor
	 */
	public Evaluator() {

	}
	/**
	 * Constructor
	 * @param classifier
	 * @param options - string arguments
	 * @throws Exception
	 */
	public Evaluator( Classifier classifier, String[] options ) throws Exception {
		this.random = new Random( seed );
		this.classifier = classifier;
		this.setOptions( options );
	}
	/**
	 * Evaluate a model and return the result
	 * @return Performance - performance of a trained model tested with a given test set
	 * @throws Exception
	 */
	public Performance evaluate() throws Exception {
		Performance perform = null;
		DataSet trainSet = this.tts.getTrainingSet();
		DataSet testSet = this.tts.getTestingSet();
		// check if the test data-set is provided
		if( testSet.getAttributes() == null || testSet.getAttributes().size() == 0 ) {
			// only train data-set is provided
			perform = new Performance( trainSet.getAttributes() );
			for(int i = 0; i < this.folds; i++) {
				Classifier classifier = this.classifier.clone();
				// cross-validate each bin
				TrainTestSets cvSets = trainSet.getCVSets( i );
				// train with current train data-set
				classifier.train( cvSets.getTrainingSet() );
				// add current performance (tested with the current test data-set)
				perform.add( classifier.classify( cvSets.getTestingSet() ) );
			}
		}
		else {
			// both train and test data-sets are provided
			this.classifier.train( trainSet );
			perform = this.classifier.classify( testSet );
		}
		if( perform == null ) {
			throw new Exception("Error: Performance is still not set!");
		}
		return perform;
	}
	/**
	 * Sets the options for this classifier
	 * @param options - the arguments
	 */
	public void setOptions( String args[] ) throws Exception {
		List<String> argsList = Arrays.asList( args );
		this.tts = new TrainTestSets();
		this.tts.setOptions( args );
		if( argsList.contains( "-x" ) ) {
			// if -x exists, update the number of folds
			this.folds = Integer.parseInt( args[argsList.indexOf("-x") + 1] );
			if( this.folds <= 0 ) {
				throw new Exception("Error: invalid fold value detected!");
			}
			this.tts.getTrainingSet().setFolds( this.folds );
			this.tts.getTestingSet().setFolds( this.folds );
		}
		if( argsList.contains( "-s" ) ) {
			// if -s exists, update the seed value
			this.seed = Long.parseLong( args[argsList.indexOf("-s") + 1] );
			this.random.setSeed( this.seed );
		}
		this.tts.getTrainingSet().setRandom( this.random );
		this.tts.getTestingSet().setRandom( this.random );
		this.classifier.setOptions( args );
	}
	/**
	 * Returns seed
	 * @return seed
	 */
	public long getSeed() {
		return this.seed;
	}
	/**
	 * Replace current seed with a new value
	 * @param seed
	 */
	public void setSeed( long seed ) {
		this.seed = seed;
	}
}