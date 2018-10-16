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
	
	public Evaluator() {

	}
	public Evaluator( Classifier classifier, String[] options ) throws Exception {
		this.random = new Random(seed);
		this.classifier = classifier;
		this.setOptions( options );
	}
	public Performance evaluate() throws Exception {
		Performance perform = null;
		DataSet trainSet = this.tts.getTrainingSet();
		DataSet testSet = this.tts.getTestingSet();
		// check if the test data-set is provided
		if( testSet.getAttributes() == null || testSet.getAttributes().size() == 0 ) {
			// only train data-set is provided
			
			// set partitions
			trainSet.partitions = new int[ trainSet.getExamples().size() ];
			trainSet.setRandom( this.random );	// use the same random with same seed as this Evaluator
			for(int i = 0; i < trainSet.partitions.length; i++) {
				// randomly partition each example
				trainSet.partitions[i] = trainSet.random.nextInt( this.folds );
			}
			
			perform = new Performance( trainSet.getAttributes() );
			for(int i = 0; i < this.folds; i++) {
				// cross-validate each bin
				TrainTestSets cvSets = trainSet.getCVSets( i );
				this.classifier.train( cvSets.getTrainingSet() );
				perform.add( this.classifier.classify( cvSets.getTestingSet() ) );
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
	public void setOptions( String args[] ) throws Exception {
		List<String> argsList = Arrays.asList( args );
		this.tts = new TrainTestSets();
		this.tts.setOptions( args );
		if( argsList.contains( "-x" ) ) {
			this.folds = Integer.parseInt( args[argsList.indexOf("-x") + 1] );
			this.tts.getTrainingSet().setFolds( this.folds );
			this.tts.getTestingSet().setFolds( this.folds );
			// update later; update the folds value of DataSet?
		}
		if( argsList.contains( "-s" ) ) {
			this.seed = Long.parseLong( args[argsList.indexOf("-s") + 1] );
			this.random.setSeed( this.seed );
		}
		this.classifier.setOptions( args );
	}
	public long getSeed() {
		return this.seed;
	}
	public void setSeed( long seed ) {
		this.seed = seed;
	}
}