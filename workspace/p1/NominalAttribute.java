import java.util.ArrayList;

/*
 * NominalAttribute.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class NominalAttribute extends Attribute {
	private ArrayList<String> domain = new ArrayList<String>();	// a list of strings for the domain of nominal attributes

	/**
	 * Default constructor.
	 */
	public NominalAttribute() {
		super();
	}
	/**
	 * Explicit constructor. Creates a nominal attribute with the specified name.
	 * @param name - the name of this data set
	 */
	public NominalAttribute( String name ) {
		super(name);
	}
	/**
	 * Adds a new nominal value to the domain of this nominal attribute.
	 * @param value - the attribute's new domain value
	 */
	public void addValue( String value ) {
		this.domain.add(value);
	}
	/**
	 * Gets the size of this nominal attribute's domain.
	 * @return an int storing the size of the domain
	 */
	public int size() {
		return this.domain.size();
	}
	/**
	 * Returns the value of this nominal attribute at the specified index.
	 * @param index - the attribute value's index
	 * @return the attribute value at the specified index
	 */
	public String getValue( int index ) {
		return this.domain.get(index);
	}
	/**
	 * Returns the index of the specified value index for this nominal attribute.
	 * @param value - the attribute's value
	 * @return the index of the specified value
	 * @throws Exception - if the value is not in the domain
	 */
	public int getIndex( String value ) throws Exception {
		if(this.validValue(value)) {
			return this.domain.indexOf(value);
		}
		throw new Exception("Invalid value passed-in!");
	}
	/**
	 * Returns a string representation of this NominalAttribute.
	 */
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("@attribute ").append(this.name);
		for(int i=0; i < this.size(); i++) {
			strBuilder.append(" ").append(this.domain.get(i));
		}
		return strBuilder.toString();
	}
	/**
	 * Returns whether the value is valid for a nominal attribute
	 * @param value - the value for testing
	 * @return true if the value is valid; false otherwise
	 */
	public boolean validValue( String value ) {
		if(value == null || value.isEmpty() || !(value instanceof String) || !(this.domain.contains(value))) {
			return false;
		}
		return true;
	}

}