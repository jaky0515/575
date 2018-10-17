import java.io.File;
import java.util.Random;
import java.util.Scanner;

/*
 * DataSet.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class DataSet {
	protected String name; // the name of this data set
	protected Attributes attributes = null; // the attributes of this data set
	protected Examples examples = null; // the examples of this data set
	protected Random random; // a random-number generator
	protected int folds = 10;
	protected int[] partitions = null;
	/**
	 * Default constructor
	 */
	public DataSet() {

	}
	/**
	 * Explicit constructor
	 * 
	 * @param attributes
	 *            - the attributes for this data set
	 */
	public DataSet(Attributes attributes) {
		this.attributes = attributes;
	}
	/**
	 * Adds the specified example to this data set
	 * 
	 * @param example
	 *            - the example to be added
	 */
	public void add(Example example) {
		// check if examples is set
		if (this.examples == null) {
			this.examples = new Examples(this.attributes);
		}
		this.examples.add(example);
	}
	/**
	 * Gets the attributes of this DataSet object.
	 * 
	 * @return the attributes of this data set
	 */
	public Attributes getAttributes() {
		return this.attributes;
	}
	/**
	 * Gets the examples of this data set.
	 * 
	 * @return the examples of this data set
	 */
	public Examples getExamples() {
		return this.examples;
	}
	/**
	 * Returns true if this data set has numeric attributes; returns false
	 * otherwise.
	 * 
	 * @return true if this data set has numeric attributes
	 */
	public boolean getHasNumericAttributes() {
		return this.attributes.getHasNumericAttributes();
	}
	/**
	 * Loads a data set from the specified file.
	 * 
	 * @param filename
	 *            - the file from which to read
	 * @throws Exception
	 *             - if the file is not found or if a parsing exception occurs
	 */
	public void load(String filename) throws Exception {
		this.attributes = new Attributes();
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(filename));
			this.parse(scanner);
			this.attributes.setClassIndex(this.attributes.size()-1);
		}
		catch(Exception e) {
			System.err.println("Error while loading dataset");
			e.printStackTrace();
		}
		finally {
			if(scanner != null) {
				scanner.close();
			}
		}
	}

	/**
	 * Parses a data set from the specified scanner, which consists of parsing the
	 * data set's header, attributes, and examples.
	 * 
	 * @param scanner
	 *            - a scanner containing the data set's tokens
	 * @throws Exception
	 *             - if a parsing exception occurs
	 */
	private void parse( Scanner scanner ) throws Exception {
		if(scanner == null) {
			throw new Exception("Invalid Scanner object passed-in!");
		}

		try {
			while(scanner.hasNextLine()) {
				if (scanner.hasNext("@dataset")) {
					/* Dataset Section */
					this.name = scanner.nextLine().trim().split(" ")[1].trim();
				} 
				else if (scanner.hasNext("@attribute")) {
					/* Attribute Section */
					this.attributes.parse(scanner);
				} 
				else if (scanner.hasNext("@examples")) {
					/* Example Section */
					if(this.examples == null) {
						this.examples = new Examples(this.attributes);
					}
					this.examples.parse(scanner);
				} 
			}
		}
		catch(Exception e) {
			System.err.println("Parsing error occurred!");
			e.printStackTrace();
		}
	}
	/**
	 * Sets the random-number generator for this data set.
	 * 
	 * @param random
	 *            - the random-number generator
	 */
	public void setRandom(Random random) {
		this.random = random;
	}
	/**
	 * Returns a string representation of the data set in a format identical to that
	 * of the file format.
	 */
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		// append @dataset section
		if(this.name != null) {
			strBuilder.append("@dataset ").append(this.name).append("\n\n");
		}
		// append @attribute section
		strBuilder.append(this.attributes);
		// append @examples section
		strBuilder.append(this.examples);
		return strBuilder.toString();
	}
	/**
	 * Returns train and test sets after performing cross validation
	 * @param p - current test bin's index
	 * @return TrainTestSets
	 * @throws Exception
	 */
	public TrainTestSets getCVSets( int p ) throws Exception {
		// validation
		if( this.examples == null || this.examples.isEmpty() ) {
			throw new Exception("Error: invalid Examples object detected!");
		}
		
		// check if partition has been created
		if( this.partitions == null || this.partitions.length == 0 ) {
			this.partitions = new int[ this.getExamples().size() ];
			for(int i = 0; i < this.partitions.length; i++) {
				// randomly partition each example by assigning a value [0, this.folds)
				this.partitions[i] = this.random.nextInt( this.folds );
			}
		}
		
		// create data-sets and update values
		DataSet trainSet = new DataSet( this.attributes );
		trainSet.setFolds( this.folds );
		DataSet testSet = new DataSet( this.attributes );
		testSet.setFolds( this.folds );
		// fill test and train data-sets
		for(int i = 0; i < this.examples.size(); i++) {
			if( this.partitions[i] == p ) {
				// add this example to a test data-set
				testSet.add( this.examples.get(i) );
			} 
			else {
				// add this example to a train data-set
				trainSet.add( this.examples.get(i) );
			}
		}
		// create cvSets and update values
		TrainTestSets cvSets = new TrainTestSets();
		cvSets.setTestingSet( testSet );
		cvSets.setTrainingSet( trainSet );
		return cvSets;
	}
	/**
	 * Returns folds
	 * @return folds
	 */
	public int getFolds() {
		return this.folds;
	}
	/**
	 * Replace folds with new value
	 * @param folds
	 * @throws Exception
	 */
	public void setFolds( int folds ) throws Exception {
		if( folds <= 0 ) {
			throw new Exception("Error: invalid folds passed-in!");
		}
		this.folds = folds;
	}
}