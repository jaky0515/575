import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * DT.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class DT extends Classifier implements Serializable, OptionHandler {
	protected Attributes attributes;
	protected Node root;
	protected boolean doPrune = true;	// default it to true
	
	/**
	 * Default constructor
	 */
	public DT() {

	}
	/**
	 * Constructor
	 * @param options - string arguments
	 * @throws Exception
	 */
	public DT( String[] options ) throws Exception {
		if( options == null || options.length < 2 ) {
			throw new Exception("Error: invalid options passed-in!");
		}
		this.setOptions( options );
	}
	/**
	 * Classifies a given data-set and return its performance
	 * @param dataset
	 * @return performance
	 */
	public Performance classify( DataSet ds ) throws Exception {
		Performance perform = new Performance( ds.getAttributes() );
		Examples exs = ds.getExamples();
		if( exs != null ) {
			for(int i = 0; i < exs.size(); i++) {
				// append this prediction result to the performance
				Example ex = exs.get(i);
				int actualClass = ex.get( ds.getAttributes().getClassIndex() ).intValue();
				perform.add( actualClass, this.getDistribution( ex ) );
			}
		}
		return perform;
	}
	/**
	 * Classifies a given example query and return the predicted label
	 * @param query
	 * @return predicted label
	 */
	public int classify( Example example ) throws Exception {
		// parameter validation
		if( example == null || example.isEmpty() ) {
			throw new Exception("Error: invalid example passed-in");
		}
		return Utils.maxIndex( this.getDistribution( example ) );
	}
	/**
	 * Calls a recursive method to compute the distribution
	 * @param example
	 * @return double[] - distribution
	 */
	public double[] getDistribution( Example example ) throws Exception {
		// validation
		if( this.root == null || example == null || example.isEmpty() ) {
			throw new Exception("Error: invalid root or example passed-in!");
		}
		return this.getDistribution( this.root, example );
	}
	/**
	 * Post pruning method that calls a recursive prune method
	 * @throws Exception
	 */
	public void prune() throws Exception {
		// post-pruning
		this.prune( this.root );
	}
	/**
	 * Sets the options for this classifier
	 * @param options - the arguments
	 */
	public void setOptions( String[] options ) throws Exception {
		// validation
		if( options == null ) {
			throw new Exception("Error: invalid options passed-in!");
		}
		// search for -u and if it exists, update the value of isPrune
		this.doPrune = Arrays.asList( options ).contains( "-u" ) ? false : true;
	}
	/**
	 * Train using a given data-set
	 * @param dataset
	 */
	public void train( DataSet ds ) throws Exception {
		// set the root as the node returned by train_aux
		this.root = this.train_aux( ds );
		if( this.doPrune ) {
			// perform post-pruning
			this.prune();
		}
	}

	// private recursive methods
	/**
	 * Recursive method that traverse to a leaf and calculate the distribution
	 * @param node
	 * @param example
	 * @return
	 * @throws Exception
	 */
	private double[] getDistribution( Node node, Example example ) throws Exception {
		/*
			traverse to a leaf
			if node is a leaf:
				return the normalized class counts
		 */
		if( node.isLeaf() ) {
			double[] dist = new double[ node.classCounts.length ];
			int totalCnt = 0;
			// calculate total count
			for( int classCnt : node.classCounts ) {
				totalCnt += classCnt;
			}
			for(int i = 0; i < node.classCounts.length; i++) {
				// set the normalized class counts
				dist[ i ] = (double) node.classCounts[ i ] / (double) totalCnt;
			}
			return dist;
		}
		else {
			// traverse to a leaf
			return this.getDistribution( node.children.get( example.get( node.attribute ).intValue() ), example );
		}
	}
	/**
	 * Recursive post-prune method
	 * @param node
	 * @return
	 * @throws Exception
	 */
	private double prune( Node node ) throws Exception {
		double error = 0;
		if( node.isLeaf() ) {
			return node.getError();
		}
		else {
			if( node.isEmpty() ) {
				return node.getError();
			}
			else {
				double childError = 0;
				// get total child errors
				for( Node child : node.children ) {
					childError += this.prune( child );
				}
				// check if sum of errors on children nodes is greater than the error on the current node
				if( childError > node.getError() ) {
					// prune
					node.children = new ArrayList<Node>();
				}
			}
		}
		return error;
	}
	/**
	 * Recursive Flach's grow tree method
	 * @param ds - given data-set
	 * @return Node - Decision Tree
	 * @throws Exception
	 */
	private Node train_aux( DataSet ds ) throws Exception {
		/*
		if ds.homogeneous() or ds.getExamples().size() <= 3:
			return a leaf label with ds.majorityClassLabel()
		else if:
			A <-- ds.getBestSplittingAttribute()
		data-sets <-- split ds into subsets ds_i according to the values of A ( use ds.splitOnAttribute(A) )
		for each i do:
			if ds_i is empty:
				T_i is a leaf with ds.majorityClassLabel()
			else:
				T_i <-- GrowTree(ds_i)
		endfor
		return a tree labeled with A and whose children are T_i
		 */
		// validation
		if( ds == null || ds.getAttributes() == null || ds.getAttributes().size() == 0 || ds.getExamples() == null || ds.getExamples().isEmpty() ) {
			throw new Exception("Error: invalid DataSet passed-in!");
		}
		// recursive method that grows a decision tree
		Node tree = new Node( ds.getClassCounts() );
		// base case
		if( ds.homogeneous() || ds.getExamples().size() <= 3 ) {
			tree.label = ds.getMajorityClassLabel();
			return tree;
		}
		else {
			// get the index of the best splitting attribute
			int bestAttrIdx = ds.getBestSplittingAttribute();
			tree.attribute = bestAttrIdx;
			// split using the best splitting attribute
			ArrayList<DataSet> dataSetList = ds.splitOnAttribute( bestAttrIdx );
			for( DataSet dataSet : dataSetList ) {
				Node subTree = new Node( dataSet.getClassCounts() );
				if( dataSet.isEmpty() ) {
					subTree.label = ds.getMajorityClassLabel();
				}
				else {
					subTree = this.train_aux( dataSet );
				}
				tree.children.add( subTree );
			}
			return tree;
		}
	}
	@Override
	public Classifier clone() {
		// TODO Auto-generated method stub
		return (DT) Utils.deepClone(this);
	}
	/**
	 * Main method
	 * @param args
	 */
	public static void main( String[] args ) {
		try {
			Evaluator evaluator = new Evaluator( new DT(), args );
			Performance performance = evaluator.evaluate();
			System.out.println( performance );
		} // try
		catch ( Exception e ) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
		} // catch
	} // DT::main
}