import java.io.Serializable;
import java.util.Arrays;

public class MBW extends Classifier implements Serializable, OptionHandler {

	private boolean doVoting = false;
	private double alpha = 1.5;
	private double beta = 0.5;
	private double threshold = 1.0;
	private double initPosW = 2.0;
	private double initNegW = 1.0;
	private double M = 1.0;
	
	public MBW() {

	}
	public MBW( String[] options ) throws Exception {
		if( options == null || options.length < 2 ) {
			throw new Exception("Error: invalid options passed-in!");
		}
		this.setOptions( options );
	}
	public Performance classify(DataSet dataset) throws Exception {
		return null;
	}
	public int classify(Example example) throws Exception {
		return 0;
	}
	public double[] getDistribution(Example example) throws Exception {
		return null;
	}
	private double scoreFunction( double[] w, Example x ) {
		double result = 0.0;
		return result;
	}
	public void train(DataSet dataset) throws Exception {
		Examples examples = dataset.getExamples();
		Attributes attrs = dataset.getAttributes();
		int classIdx = attrs.getClassIndex();
		int i = 0;
		int c_i = 0;
		double[] w_i = new double[ attrs.size() ];
		for(int t = 0; t < examples.size(); t++) {
			Example x_t = examples.get( t );
			double y_hat =  scoreFunction( w_i, x_t );
			double y_t = x_t.get( classIdx );
			if( y_hat != y_t ) {
				// prediction == mistake
				// Update model w_i → w_i + 1
				i++;
			}
			else {
				c_i++;
			}
		}
	}
	public void setOptions( String[] options ) throws Exception {
		// validation
		if( options == null ) {
			throw new Exception("Error: invalid options passed-in!");
		}
		// search for -v and if it exists, update the value of doVoting
		this.doVoting = Arrays.asList( options ).contains( "-v" ) ? false : true;
	}
	public Classifier clone() {
		return ( BW ) Utils.deepClone( this );
	}

}
