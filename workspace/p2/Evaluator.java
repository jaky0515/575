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
		this.classifier = classifier;
		// update later
	}
	public Performance evaluate() throws Exception {
		return null;
		// update later
	}
	public long getSeed() {
		return this.seed;
	}
	public void setOptions( String args[] ) throws Exception {
		// update later
	}
	public void setSeed( long seed ) {
		this.seed = seed;
	}
}