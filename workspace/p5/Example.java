import java.util.ArrayList;

/*
 * Example.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class Example extends ArrayList<Double> {

	/**
	 * Default constructor
	 */
	public Example() {
		super();
	}
	/**
	 * Explicit constructor. Constructs an Example with n values, where n is greater or equal to two.
	 * @param n - the number of values of this example
	 */
	public Example( int n ) {
		super(n);
	}

}