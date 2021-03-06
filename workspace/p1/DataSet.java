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

}
