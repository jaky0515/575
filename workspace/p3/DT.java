import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/*
 * DT.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class DT extends Classifier implements Serializable, OptionHandler {
	// update later
	protected Attributes attributes;
	protected Node root;
	protected boolean doPrune = true;	// default it to true

	public DT() {

	}
	public DT( String[] options ) throws Exception {
		if( options == null || options.length < 2 ) {
			throw new Exception("Error: invalid options passed-in!");
		}
		this.setOptions( options );
	}
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
	public int classify( Example example ) throws Exception {
		// parameter validation
		if( example == null || example.isEmpty() ) {
			throw new Exception("Error: invalid example passed-in");
		}
		return Utils.maxIndex( this.getDistribution( example ) );
	}
	public double[] getDistribution( Example example ) throws Exception {
		// validation
		if( this.root == null || example == null || example.isEmpty() ) {
			throw new Exception("Error: invalid root or example passed-in!");
		}
		return this.getDistribution( this.root, example );
	}
	public void prune() throws Exception {
		// post-pruning
		this.prune( this.root );
	}
	public void setOptions( String[] options ) throws Exception {
		// search for '-u' and if it exists, update the value of isPrune
		this.doPrune = Arrays.asList( options ).contains( "-u" ) ? false : true;
	}
	public void train( DataSet ds ) throws Exception {
		// set the root as the node returned by train_aux
		this.root = this.train_aux( ds );
		if( this.doPrune ) {
//			this.prune();
		}
	}

	// private recursive methods
	private double[] getDistribution( Node node, Example example ) throws Exception {
		/*
			traverse to a leaf
			if node is a leaf:
				return the normalized class counts
		 */
		if( node.isLeaf() ) {
			double[] dist = new double[ node.classCounts.length ];
			int totalCnt = 0;
			for(int i = 0; i < node.classCounts.length; i++) {
				totalCnt += node.classCounts[ i ];
			}
			for(int i = 0; i < node.classCounts.length; i++) {
				dist[ i ] = (double) node.classCounts[ i ] / (double) totalCnt;
			}
			return dist;
		}
		else {
			return this.getDistribution( node.children.get( example.get( node.attribute ).intValue() ), example );
		}
	}
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
				for( Node child : node.children ) {
					childError += this.prune( child );
				}
				if( childError > node.getError() ) {
					// prune
					node.children = new ArrayList<Node>();
				}
			}
		}
		return error;
	}
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
			int bestAttrIdx = ds.getBestSplittingAttribute();
			tree.attribute = bestAttrIdx;
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