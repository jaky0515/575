import java.util.Arrays;

/*
 * TrainTestSets.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class TrainTestSets implements OptionHandler{

	protected DataSet train;	// the training examples
	protected DataSet test;		// the testing examples

	/**
	 * Default constructor
	 */
	public TrainTestSets() {}
	/**
	 * Explicit constructor that processes the specified arguments.
	 * @param options - the arguments for this train/test set
	 * @throws Exception - if the file is not found or if a parsing exception occurs
	 */
	public TrainTestSets( String [] options ) throws Exception {
		setOptions(options);
	}
	/**
	 * Explicit constructor that sets the training and testing sets to the specified data sets
	 * @param train - the training set
	 * @param test - the testing set
	 */
	public TrainTestSets( DataSet train, DataSet test ) {
		this.train = train;
		this.test = test;
	}
	/**
	 * Returns the training set of this train/test set.
	 * @return a training set
	 */
	public DataSet getTrainingSet() {
		return this.train;
	}
	/**
	 * Returns the testing set of this train/test set.
	 * @return a testing set
	 */
	public DataSet getTestingSet() {
		return this.test;
	}
	/**
	 * Sets the training set of this train/test set to the specified data set.
	 * @param train - the specified training set
	 */
	public void setTrainingSet( DataSet train ) {
		this.train = train;
	}
	/**
	 * Sets the testing set of this train/test set to the specified data set.
	 * @param test - the specified testing set
	 */
	public void setTestingSet( DataSet test ) {
		this.test = test;
	}
	/**
	 * Sets the options for this train/test set.
	 * @param options - the arguments
	 * @throws Exception - if the file is not found or if a parsing exception occurs
	 */
	public void setOptions( String[] options ) throws Exception {
		// argument validation
		if( options.length < 2 || 
				!Arrays.asList(options).contains("-t") || 
				(Arrays.asList(options).contains("-T") && options.length != 4) ) {
			throw new Exception("Invalid arguments passed!");
		}
		try {
			// load data
			this.train = new DataSet();
			// load training data
			this.train.load(options[1].trim());
			if(options.length == 4) {
				this.test = new DataSet();
				// load testing data
				this.test.load(options[3].trim());
			}
		}
		catch(Exception e) {
			System.err.println("Error occurred while loading a dataset!");
			e.printStackTrace();
		}
	}
	/**
	 * Returns a string representation of this train/test set in a format similar to that of the file format. Includes the testing examples if present.
	 */
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append((this.train == null) ? "" : this.train);
		strBuilder.append((this.test == null) ? "" : this.test);
		return strBuilder.toString();
	}
}