import java.util.ArrayList;
import java.util.Scanner;

/*
 * Attributes.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class Attributes {

	private ArrayList<Attribute> attributes = new ArrayList<Attribute>();	// a list of attributes
	private boolean hasNumericAttributes = false;	// a flag indicating that the data set has one or more numeric attributes
	private int classIndex;	// stores the position of the class label
	private ArrayList< ArrayList< Double[] > > encodedAttrs = new ArrayList< ArrayList< Double[] > >();
	/*
	 * Default constructor.
	 */
	public Attributes() {}
	/**
	 * Adds a new attribute to this set of attributes.
	 * @param attribute - the attribute's name
	 */
	public void add( Attribute attribute ) {
		if(attribute instanceof NumericAttribute) {
			// set the numeric flag
			this.hasNumericAttributes = true;
		}
		this.attributes.add(attribute);
	}
	/**
	 * Returns the index of the class label.
	 * @return - the index of the class label
	 */
	public int getClassIndex() {
		return this.classIndex;
	}
	/**
	 * Returns true if this set of attributes has one or more numeric attributes; returns false otherwise.
	 * @return - true if this set of attributes has numeric attributes
	 */
	public boolean getHasNumericAttributes() {
		return this.hasNumericAttributes;
	}
	/**
	 * Returns the ith attribute in this set of attributes.
	 * @param i - the index of the specified attribute
	 * @return - the ith Attribute
	 */
	public Attribute get( int i ) {
		return attributes.get(i);
	}
	/**
	 * Returns the class attribute.
	 * @return - the class attribute
	 */
	public Attribute getClassAttribute() {
		return this.attributes.get(this.classIndex);
	}
	/**
	 * Returns the attribute's index.
	 * @param name - the attribute's name
	 * @return - the attribute's position in the names array
	 * @throws Exception - if the attribute does not exist
	 */
	public int getIndex( String name ) throws Exception {
		for(int i = 0; i < this.attributes.size(); i++) {
			if(this.attributes.get(i).name.equals(name)) {
				return i;
			}
		}
		throw new Exception("Attribute not found!");
	}
	/**
	 * Returns the number of attributes.
	 * @return - the number of attributes
	 */
	public int size() {
		return this.attributes.size();
	}
	/**
	 * Parses the attribute declarations in the specified scanner. By convention, the last attribute is the class label after parsing.
	 * @param scanner - a Scanner containing the data set's tokens
	 * @throws Exception - if a parse exception occurs
	 */
	public void parse( Scanner scanner ) throws Exception {
		if(scanner == null) {
			throw new Exception("Invalid Scanner object passed-in!");
		}
		String line;
		while(scanner.hasNextLine()) {
			if(scanner.hasNext("@examples")) {
				break;
			}
			line = scanner.nextLine().trim();
			if(!line.contains("@attribute")) {
				continue;
			}
			Attribute attr = AttributeFactory.make(new Scanner(line));
			this.add(attr);
		}
	}
	/**
	 * Sets the class index for this set of attributes.
	 * @param classIndex - the new class index
	 * @throws Exception - if the class index is out of bounds
	 */
	public void setClassIndex( int classIndex ) throws Exception {
		if( this.attributes.isEmpty() || 
				classIndex < 0 || 
				classIndex > (this.attributes.size() - 1) ) {
			throw new Exception("Passed-in class index is out of bounds!");
		}
		this.classIndex = classIndex;
	}
	/**
	 * Returns a string representation of this Attributes object.
	 */
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		for(int i=0; i < this.attributes.size(); i++) {
			strBuilder.append(this.attributes.get(i));
			strBuilder.append("\n");
		}
		return strBuilder.toString();
	}
	/**
	 * Encodes the attribute values; use bipolor or binary encoding depending on the value of isBipolar
	 * @param isBipolar
	 */
	public void encode( boolean isBipolar ) {
		this.encodedAttrs = new ArrayList< ArrayList< Double[] > >();
		// perform encoding for the values
		double[] vals = new double[ 2 ];
		vals[ 0 ] = ( isBipolar ) ? -1.0 : 0.0;
		vals[ 1 ] = ( isBipolar ) ? 1.0 : 1.0;
		for( Attribute attr : this.attributes ) {
			ArrayList< Double[] > attrVals;
			if( attr instanceof NumericAttribute ) {
				attrVals = new ArrayList< Double[] >();
				Double[] encodedVal = new Double[ 1 ];
				encodedVal[ 0 ] = null;
				attrVals.add( encodedVal );
			}
			else {
				attrVals = new ArrayList< Double[] >();
				int numBits = (int) Math.ceil( Math.log( attr.size() ) / Math.log( 2 ) );
				for(int i = 0; i < attr.size(); i++) {
					Double[] encodedVal = new Double[ numBits ];
					String binaryStr = Integer.toBinaryString( i );
					if( binaryStr.length() < numBits ) {
						for(int j = 0; j < numBits - binaryStr.length(); j++) {
							encodedVal[ j ] = vals[ 0 ];
						}
					}
					for(int j = 0; j < binaryStr.length(); j++) {
						encodedVal[ numBits - binaryStr.length() + j ] = vals[ Integer.parseInt( "" + binaryStr.charAt( j )) ];
					}
					attrVals.add( encodedVal );
				}
			}
			
			this.encodedAttrs.add( attrVals );
		}
	}
	/**
	 * Getter method for encodedAttrs
	 * @return
	 */
	public ArrayList< ArrayList< Double[] > > getEncodedAttrs() {
		return this.encodedAttrs;
	}
}