/*
 * Attribute.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class Attribute extends Object {

	protected String name;
	
	/**
	 * Default constructor.
	 */
	public Attribute() {

	}
	/**
	 * Explicit constructor that sets the name of this attribute.
	 * @param name - the name of this attribute
	 */
	public Attribute( String name ) {
		this.name = name;
	}
	/**
	 * Gets the name of this attribute.
	 * @return - a string storing the name
	 */
	public String getName() {
		return this.name;
	}
	/**
	 * Gets the size of this attribute's domain.
	 * @return - an int storing the size of the domain
	 */
	public int size() {
		if(this instanceof NumericAttribute) {
			return Integer.MAX_VALUE;
		}
		else {
			return ((NominalAttribute) this).size();
		}
	}
	/**
	 * Sets the name of this attribute to the specified name.
	 * @param name - the name of this attribute
	 */
	public void setName( String name ) {
		this.name = name;
	}
	/**
	 * Returns a string representation of this attribute.
	 * @return a string representation of this attribute
	 */
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("@attribute ").append(this.name);
		return strBuilder.toString();
	}

}