import java.util.ArrayList;
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
	private Double holdouts = null;
	private Classifier classifier;
	private TrainTestSets tts;
	private Scaler scaler;

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
		this.scaler = new Scaler();
		this.setOptions( options );
	}
	private DataSet cleanDs( DataSet ds ) {
		if( !ds.getHasNumericAttributes() ) {
			return ds;
		}
		Attributes attrs = new Attributes();
		ArrayList< Integer > indices = new ArrayList< Integer >();
		for(int i = 0; i < ds.attributes.size(); i++) {
			if( ds.attributes.get( i ) instanceof NumericAttribute ) {
				indices.add( i );
				continue;
			}
			attrs.add(  ds.attributes.get( i ) );
		}
		DataSet newDs = new DataSet( attrs );
		for(int i = 0; i < ds.examples.size(); i++) {
			Example ex = ds.examples.get( i );
			for(int j = 0; j < indices.size(); j++) {
				ex.remove( indices.get( j ) - j );
			}
			newDs.add( ex );
		}
		newDs.name = ds.name;
		newDs.random = ds.random;
		newDs.folds = ds.folds;
		newDs.partitions = ds.partitions;
		return newDs;
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
			if( trainSet.getHasNumericAttributes() ) {
				this.scaler.configure( trainSet );
				trainSet = this.scaler.scale( trainSet );
			}
//			trainSet = this.cleanDs( trainSet );
			// only train data-set is provided
			perform = new Performance( trainSet.getAttributes() );
			// check if hold-out value is passed-in
			if( this.holdouts == null ) {
				// use k-fold method
				double totalAUC = 0;
				for(int i = 0; i < this.folds; i++) {
					Classifier classifier = this.classifier.clone();
					// cross-validate each bin
					TrainTestSets cvSets = trainSet.getCVSets( i );
					// train with current train data-set
					classifier.train( cvSets.getTrainingSet() );
					// add current performance (tested with the current test data-set)
					perform.add( classifier.classify( cvSets.getTestingSet() ) );
					totalAUC += perform.getAccuracy();
				}
				// take the average and set this value as AUC
				perform.setAvgAUC( totalAUC / this.folds );
			}
			else {
				// use hold-out method
				DataSet newTrainSet = new DataSet( trainSet.getAttributes() );
				DataSet newTestSet = new DataSet( trainSet.getAttributes() );
				newTrainSet.setRandom( trainSet.random );
				newTestSet.setRandom( trainSet.random );
				newTrainSet.examples = new Examples( trainSet.getAttributes() );
				newTestSet.examples = new Examples( trainSet.getAttributes() );
				// randomly pick percentage of examples from trainSet for newTrainSet
				int numTrainExs = (int) Math.floor( trainSet.getExamples().size() * this.holdouts );
				for(int i = 0; i < trainSet.getExamples().size(); i++) {
					// randomly get 0 or 1
					int randomVal = trainSet.random.nextInt( 1 );
					if( randomVal == 0 && newTrainSet.getExamples().size() < numTrainExs ) {
						// if newTrainSet is not full, add it to the newTrainSet
						newTrainSet.add( trainSet.getExamples().get( i ) );
					}
					else {
						// otherwise, insert to newTestSet
						newTestSet.add( trainSet.getExamples().get( i ) );
					}
				}
				// train using newTrainSet
				this.classifier.train( newTrainSet );
				// compute performance using newTestSet
				perform = this.classifier.classify( newTestSet );
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
		if( argsList.contains( "-p" ) ) {
			// if -p exists, use the hold-out method
			this.holdouts = Double.parseDouble( args[argsList.indexOf("-p") + 1] );
			// check the value of holdouts
			if( this.holdouts > 1.0 || this.holdouts < 0.0 ) {
				throw new Exception("Error: invalid hold-out value passed-in!");
			}
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