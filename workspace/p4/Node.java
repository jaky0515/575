import java.util.ArrayList;

/*
 * Node.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class Node {

	public int attribute = -1;
	public int label = -1;
	public int[] classCounts = null;
	public ArrayList<Node> children = new ArrayList<Node>();
	
	/**
	 * Default constructor
	 */
	Node() {
		
	}
	/**
	 * Constructor
	 * @param classCounts
	 */
	Node( int[] classCounts ) {
		this.classCounts = classCounts;
	}
	/**
	 * Checks if this node is a leaf node
	 * @return boolean - true if a leaf node, false otherwise
	 */
	public boolean isLeaf() {
		// check if label is -1 and return true if it is
		return ( this.children == null || this.children.size() == 0 ) ? true : false;
	}
	/**
	 * Checks if this node doesn't contain any class counts
	 * @return boolean - true if empty, false otherwise
	 */
	public boolean isEmpty() {
		// return true if classCounts is zero or null
		if( this.classCounts == null || this.classCounts.length == 0 ) {
			return true;
		}
		// check if all the class counts are zero
		for(int i = 0; i < this.classCounts.length; i++) {
			if( this.classCounts[ i ] > 0 ) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Compute and returns the error value of this node using U25%
	 * @return
	 */
	public double getError() {
		// use the class counts to call u25 and return the product
		double err = 0;
		int totalCounts = 0;
		for( int classCount : this.classCounts ) {
			totalCounts += classCount;
		}
		for( int classCount : this.classCounts ) {
			err += totalCounts * Utils.u25( totalCounts, classCount );
		}
		return err;
	}

}