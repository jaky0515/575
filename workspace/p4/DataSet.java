import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
			// update later
			// this.attributes.setClassIndex(this.attributes.size()-1);
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
					this.attributes.setClassIndex( this.attributes.size() - 1 );
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
	/**
	 * Check if this data-set is empty by checking the number of examples in this data-set
	 * @return boolean - true if empty, otherwise false
	 */
	public boolean isEmpty() {
		// check if examples exist in this data-set
		return ( this.examples == null || this.examples.isEmpty() ) ? true : false;
	}
	/**
	 * Compute and return the gain ratio
	 * @param attribute
	 * @return gain ratio of a given attribute
	 * @throws Exception
	 */
	public double gainRatio( int attribute ) throws Exception {
		// validation
		if( this.isEmpty() || this.attributes == null || this.attributes.size() == 0 ) {
			throw new Exception("Error: examples or attributes doesn't exist!");
		}
		// calculate Entropy of a data-set
		double entropy = 0;
		int classIdx = this.attributes.getClassIndex();
		Attribute classAttr = this.attributes.get( classIdx );
		double numExs = this.examples.size();
		for(int i = 0; i < classAttr.size(); i++) {
			double classCnt = this.examples.getClassCounts()[ i ];
			double p_i = classCnt / numExs;
			entropy += ( p_i == 0 ) ? 0 : ( -1.0 * p_i ) * ( Math.log( p_i ) / Math.log( 2 ) );
		}
		// calculate Gain() and SplitInformation()
		double gainTotal = 0;
		double splitInfo = 0;
		for(int i = 0; i < this.attributes.get( attribute ).size(); i++) {
			// count this attribute in examples
			double attrCnt = 0;
			int[] attrClassCnts = new int[ classAttr.size() ];
			for(int j = 0; j < attrClassCnts.length; j++) {
				attrClassCnts[ j ] = 0;
			}
			for(int j = 0; j < this.examples.size(); j++) {
				if( this.examples.get( j ).get( attribute ) == i ) {
					attrCnt++;
					attrClassCnts[ this.examples.get( j ).get( classIdx ).intValue() ]++;
				}
			}
			if( attrCnt == 0 ) {
				continue;
			}
			double x = attrCnt / numExs;	// |S_v| / |S|
			// calculate entropy
			double attrEntropy = 0;
			for(int j = 0; j < classAttr.size(); j++) {
				double attrClassCnt = attrClassCnts[ j ];
				double p_i = attrClassCnt / attrCnt;
				attrEntropy += ( p_i == 0 ) ? 0 : ( -1.0 * p_i ) * ( Math.log( p_i ) / Math.log( 2 ) );
			}
			gainTotal += x * attrEntropy;
			splitInfo += ( -1.0 * x ) * ( Math.log( x ) / Math.log( 2 ) );
		}
		double gain = entropy - gainTotal;
		// calculate GainRatio
		return ( splitInfo == 0.0 ) ? 0.0 : gain / splitInfo;	// GainRatio() = Gain() / SplitInfo()
	}
	/**
	 * Compute and return the best splitting attribute
	 * @return index of the best splitting attribute
	 * @throws Exception
	 */
	public int getBestSplittingAttribute() throws Exception {
		// validation
		if( this.attributes == null || this.attributes.size() == 0 || this.isEmpty() ) {
			throw new Exception("Error: invalid data-set without attributes or examples!");
		}
		// attribute with the max gain ratio is selected as the splitting attribute
		int bestAttrIdx = -1;
		double bestRatio = 0;
		boolean isBestAttrSet = false;
		for(int i = 0; i < this.attributes.size() - 1; i++) {
			// skip numeric attributes
			if( this.attributes.get( i ) instanceof NumericAttribute ) {
				continue;
			}
			double ratio = this.gainRatio( i );
			if( ratio > bestRatio || !isBestAttrSet ) {
				// replace bestRatio with the current ratio
				bestAttrIdx = i;
				bestRatio = ratio;
				isBestAttrSet = true;
			}
		}
		if( bestAttrIdx == -1) {
			throw new Exception("Error: best splitting attribute is not found!");
		}
		return bestAttrIdx;
	}
	/**
	 * Split the examples in the data-set by the given attribute and return the list of the data-sets with examples
	 * @param attribute
	 * @return list of DataSet
	 * @throws Exception
	 */
	public ArrayList<DataSet> splitOnAttribute( int attribute ) throws Exception {
		// validation
		if( this.isEmpty() || this.attributes == null || this.attributes.size() == 0 || attribute < 0 || attribute >= this.attributes.size() || this.attributes.get( attribute ) instanceof NumericAttribute ) {
			throw new Exception("Error: invalid attribute or data-set!");
		}
		ArrayList<DataSet> dsList = new ArrayList<DataSet>();
		for(int i = 0; i < this.attributes.get( attribute ).size(); i++) {
			// create a new DataSet for this attribute value
			DataSet ds = new DataSet( this.attributes );
			ds.name = this.name;
			ds.random = this.random;
			ds.folds = this.folds;
			ds.partitions = this.partitions;
			ds.examples = new Examples( this.attributes );
			// update examples
			for( Example ex : this.examples ) {
				if( ex.get( attribute ) == i ) {
					// if attribute values match; add this to ds.examples
					ds.examples.add( ex );
				}
			}
			dsList.add( ds );
		}
		return dsList;
	}
	/**
	 * Check if the examples of this data-set have the same class label
	 * @return boolean - true if all examples have the same class label, false otherwise
	 * @throws Exception
	 */
	public boolean homogeneous() throws Exception {
		// validation
		if( this.examples == null || this.examples.isEmpty() ) {
			throw new Exception("Error: examples is empty!");
		}
		// check if all examples have the same class label
		int classIdx = this.attributes.getClassIndex();
		double classLabel = this.examples.get( 0 ).get( classIdx );
		for( int i = 1; i < this.examples.size(); i++ ) {
			if( classLabel != this.examples.get( i ).get( classIdx ) ) {
				// if the class of the first example does not match with this example class; return false
				return false;
			}
		}
		// all examples have the same class label
		return true;
	}
	/**
	 * Returns the classCounts of this data-set's examples
	 * @return int[] - classCounts of the examples
	 * @throws Exception
	 */
	public int[] getClassCounts() throws Exception {
		// validation
		if( this.examples.getClassCounts() == null || this.examples.getClassCounts().length == 0 ) {
			throw new Exception("Error: classCounts is not set!");
		}
		return this.examples.getClassCounts();
	}
	/**
	 * Returns the class label that most examples have
	 * @return index of the class label
	 * @throws Exception
	 */
	public int getMajorityClassLabel() throws Exception {
		// validation
		if( this.isEmpty() || this.examples.getClassCounts() == null || this.examples.getClassCounts().length == 0 ) {
			throw new Exception("Error: examples is empty or classCounts is not set!");
		}
		int majorLabelIdx = 0;
		int majorLabelCnt = this.examples.getClassCounts()[ majorLabelIdx ];
		for(int i = 1; i < this.examples.getClassCounts().length; i++) {
			if( this.examples.getClassCounts()[ i ] > majorLabelCnt ) {
				majorLabelIdx = i;
				majorLabelCnt = this.examples.getClassCounts()[ i ];
			}
		}
		return majorLabelIdx;
	}
}