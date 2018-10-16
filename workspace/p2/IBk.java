import java.io.Serializable;
import java.util.Arrays;

/*
 * IBk.java
 * Copyright (c) 2018 Georgetown University.  All Rights Reserved.
 */

public class IBk extends Classifier implements Serializable, OptionHandler {
	protected DataSet dataset;
	protected Scaler scaler;
	protected int k = 3;

	public IBk() {
		this.scaler = new Scaler();	// set scaler
	}
	public IBk( String[] options ) throws Exception {
		this.scaler = new Scaler();	// set scaler
		this.setOptions( options );	// set options
	}
	public Performance classify( DataSet dataset ) throws Exception {
		Performance perform = new Performance( this.dataset.getAttributes() );
		Examples exs = this.dataset.getExamples();
		for(int i = 0; i < exs.size(); i++) {
			//			perform.add( exs.get(i).get( this.dataset.getAttributes().getClassIndex() ), this.classify( exs.get(i) ) );
			perform.add( exs.get(i).get( this.dataset.getAttributes().getClassIndex() ).intValue(), this.getDistribution( exs.get(i) ) );
		}
		return perform;
	}
	public int classify( Example query ) throws Exception {
		if( query == null || query.isEmpty() ) {
			throw new Exception("Error: invalid Example object passed-in!");
		}
		return Utils.maxIndex( this.getDistribution( query ) );
	}
	public double[] getDistribution( Example query ) throws Exception {
		// gives class rating
		// parameter validation
		if( query == null || query.isEmpty() ) {
			throw new Exception("Error: invalid Example object passed-in!");
		}
		NominalAttribute classAttribute = (NominalAttribute) this.dataset.attributes.getClassAttribute();
		// initialize distributions
		double[] distribution = new double[ classAttribute.size() ];
		for (int i = 0; i < classAttribute.size(); i++) {
			distribution[i] = 0.0;
		}

		DataSet scaledDs = scaler.scale(dataset);

		double[] dists = new double[this.k];
		int[] indices = new int[this.k];
		double[] knn = new double[this.k];

		for (int i = 0; i < this.k; i++) {
			dists[i] = Double.MAX_VALUE;
			indices[i] = 0;
			knn[i] = 0.0;
		}

		for (int i = 0; i < scaledDs.getExamples().size(); i++) {
			double distance = this.calculateDistance(this.scaler.scale(query), scaledDs.getExamples().get(i));

			int maxIdx = Utils.maxIndex(dists);
			if (distance <= dists[maxIdx]) {
				dists[maxIdx] = distance;
				indices[maxIdx] = i;
			}
		}

		for (int i = 0; i < this.k; i++) {
			knn[i] = (double) this.dataset.getExamples().get(indices[i])
					.get(this.dataset.getAttributes().getClassIndex());
		}

		for (int i = 0; i < k; i++) {
			distribution[(int) knn[i]] += 1;
		}

		for (int i = 0; i < distribution.length; i++) {
			distribution[i] = distribution[i] / (double) k;
		}

		return distribution;
	}
	public double calculateDistance(Example train, Example test) {
		double distance = 0.0;

		for (int i = 0; i < this.dataset.getAttributes().size() - 1; i++) {

			if (this.dataset.getAttributes().get(i) instanceof NumericAttribute) {
				distance += Math.pow(train.get(i) - test.get(i), 2);
			} else {
				if (train.get(i).equals(test.get(i))) {
					distance += 0;
				} else {
					distance += 1;
				}
			}

			// distance += Math.pow(train.get(i) - test.get(i), 2);
		}

		distance = Math.sqrt(distance);

		return distance;
	}
	public void setOptions( String args[] ) {
		// search for '-k' and if it exists, update the value of k
		if( Arrays.asList(args).contains("-k") ) {
			this.setK( Integer.parseInt( args[Arrays.asList(args).indexOf("-k") + 1] ) );
		}
	}
	public void train( DataSet dataset ) throws Exception {
		this.dataset = dataset;
		this.scaler.configure( dataset );
	}
	public Classifier clone() {
		return (IBk) Utils.deepClone(this);
	}
	public void setK( int k ) {
		this.k = k;
	}
	public static void main( String[] args ) {
		try {
			Evaluator evaluator = new Evaluator( new IBk(), args );
			Performance performance = evaluator.evaluate();
			System.out.println( performance );
		} // try
		catch ( Exception e ) {
			System.out.println( e.getMessage() );
			e.printStackTrace();
		} // catch
	} // IBk::main
}