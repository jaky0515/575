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

	// update later
	Node() {
		
	}
	Node( int[] classCounts ) {
		this.classCounts = classCounts;
	}
	public boolean isLeaf() {
		// check if label is -1 and return true if it is
		return ( this.children == null || this.children.size() == 0 ) ? true : false;
	}
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
	public double getError() {
		// use the class counts to call u25 and return the product
		double err = 0;
		int totalCounts = 0;
		for(int i = 0; i < this.classCounts.length; i++) {
			totalCounts += this.classCounts[ i ];
		}
		for(int i = 0; i < this.classCounts.length; i++) {
			err += totalCounts * Utils.u25( totalCounts, this.classCounts[ i ] );
		}
		return err;
	}

}