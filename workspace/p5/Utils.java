import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/*
 * Utils.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class Utils {
	public static double z = 0.6925;	// z value for u25
	
	/**
	 * Returns an index of a maximum value
	 * @param p
	 * @return index of a maximum value
	 */
	public static int maxIndex( double[] p ) {
		int maxIdx = 0;
		double maxVal = p[0];
		for(int i = 1; i < p.length; i++) {
			// if maxVal is smaller than the current value; replace
			if( maxVal < p[i] ) {
				maxVal = p[i];
				maxIdx = i;
			}
		}
		return maxIdx;
	}
	/**
	 * This method makes a "deep clone" of any object it is given.
	 * Copied from: https://alvinalexander.com/java/java-deep-clone-example-source-code
	 */
	public static Object deepClone(Object object) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			return ois.readObject();
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * This method calculate U25% and return its result
	 * @param n
	 * @param x
	 * @return calculated U25% value
	 */
	public static double u25( int n, int x ) {
		// implements the calculation of a node error for pruning
		double a = Math.pow(Utils.z, 2) * ( ( x + 0.5 ) * ( 1 - ( ( x + 0.5 ) / n ) ) + ( Math.pow(Utils.z, 2) / 4 ) );
		return ( x + 0.5 + ( Math.pow(Utils.z, 2) / 2 ) + Math.sqrt( a ) ) / ( n + Math.pow(Utils.z, 2) );
	}
	/**
	 * This method compute and return the dot product between two matrices
	 * @param x
	 * @param y
	 * @return dot product
	 */
	public static Double dotProduct( double[] x, double[] y ) {
		if( x.length != y.length ) {
			System.err.println("Error: length of x and y are different!");
			return null;
		}
		double product = 0.0;
		for(int i = 0; i < x.length; i++) {
			product += x[ i ] * y[ i ];
		}
		return product;
	}
	/**
	 * sigmoid activation method
	 * @param x
	 * @return result
	 */
	public static double sigmoid( double x ) {
		double lambda = 1.0;
		return ( 1.0 / ( 1.0 + ( Math.exp( -1.0 * lambda * x ) ) ) );
	}
	/**
	 * return encoded example
	 * @param example
	 * @param exampleLen
	 * @param attributes
	 * @return array containing encoded example values
	 */
	public static double[] getEncodedExample( Example example, int exampleLen, Attributes attributes ) {
		ArrayList< ArrayList< Double[] > > encodedAttrs = attributes.getEncodedAttrs();
		double[] x = new double[ exampleLen ];
		int counter = 0;
		for(int j = 0; j < encodedAttrs.size() - 1; j++) {
			if( attributes.get( j ) instanceof NumericAttribute ) {
				x[ counter++ ] = example.get( j );
			}
			else {
				int attrIdx = example.get( j ).intValue();
				for(int k = 0; k < encodedAttrs.get( j ).get( attrIdx ).length; k++) {
					Double attrVal = encodedAttrs.get( j ).get( attrIdx )[ k ];
					x[ counter++ ] = attrVal;
				}
			}
		}
		return x;
	}
}