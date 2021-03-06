/*
 * NumericAttribute.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class NumericAttribute extends Attribute {
	
	/**
	 * Default constructor.
	 */
	public NumericAttribute() {
		super();
	}
	/**
	 * Explicit constructor. Creates a numeric attribute set of the specified name.
	 * @param name - the name of this data set
	 */
	public NumericAttribute( String name ) {
		super(name);
	}
	/**
	 * Returns a string representation of this NumericAttribute.
	 */
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("@attribute ").append(this.name).append(" numeric");
		return strBuilder.toString();
	}
	/**
	 * Returns whether the specified value is valid for a numeric attribute.
	 * @param value
	 * @return true if the value is valid; false otherwise
	 */
	public boolean validValue( Double value ) {
		// test if value is null or in Double type
		return (value != null && value instanceof Double);
	}

}