
import java.util.List;

/**
 * Uses the lat and lon location as a reference point and searches all the bins in the server
 * filtering the bins specified by the filter and will return the bin info closest K Bins
 * @author hartesamu
 *
 */
public class KNearestNeighbour {

	private int k = 10;		// default

	private List<Bin> allBins;	// holds all the bins
	private Bin find;			// the users location or the center of the search

	// Max and min for each field used for range for vectors
	private int params = 2;	// lat and long
	private double[] max;	// the max for each lat and lng, used to find range
	private double[] min;	// the min for each lat and lng, used to find range

	private String filter;	// the filter for Bin types

	/**
	 * Constructor for the K nearest neighbour method for searching for the closest
	 * bins the given Lat, lng location
	 * @param lat, your lat position
	 * @param lng, your lng position
	 * @param k, the number of nearby bins to find
	 * @param bins, a list of all the bins to search(in the server)
	 * @param filter, a filter for the type of bins returned
	 */
	public KNearestNeighbour(double lat, double lng, int k, List<Bin> bins, String filter){
		if(k > 0) this.k = k;
		this.filter = filter;
		max = new double[]{-1000, -1000};
		min = new double[]{Double.MAX_VALUE, Double.MAX_VALUE};
		find = new Bin(lat, lng, "search");	// a temp bin representing the user location
		allBins = bins;	// all the bins

		train();	// call the next part of the class
	}

	/**finds the limits and fills the min/max values for the range, by looking at all the bins
	 * we can get theses values for comparing distances
	 */
	protected void train() {
		double[] forLimit;
		for(Bin b: allBins){
			forLimit = b.getValues();
			if(params != forLimit.length)
				System.out.println("incorrect number of bin parameters");
			for(int i = 0; i < forLimit.length; i++){
				limit(i, forLimit[i]);
			}
		}
	}

	/**<p>Finds the closest number of points given the initial point from the constructor</p>
	 * <p>Creates an array of length K and stores the first K Bins as the closest Bins. When
	 * adding Bins the distance vector calculated from calDistance will also be recorded. From
	 * these distances we can replace any elements of the array if we find a closer match. When
	 * we have tested all of the bins we will be left with an array with the closest bins to the
	 * user</p>
	 *
	 * @return String[], an array of strings that hold the lat, lng and type
	 * 		for a given number of bins
	 */
	public String[] findBins() {
		Bin[] close;
		double[] closeDis;
		int longD;	// stores the longest distance to the origin point
		int i;	// counter to k
		double d;	// distance between training case and test case

		double[] range = getRange();

		close = new Bin[k];
		closeDis = new double[k];
		longD = 0;
		i = 0;	// counter to k
		for(Bin b: allBins) {
			// apply the filter and skip any types that don't match
			if(!filter.equalsIgnoreCase("all") || !b.type.equalsIgnoreCase(filter)) continue;
			d = calDistance(find, b, range);	// get the distance
			//System.out.println("distance: "+d);
			if(i < k) {
				close[i] = b;		// close bins
				closeDis[i] = d;	// bin distances
				if(d > closeDis[longD]) longD = i;	// store the furtherest distance to test
				i++;				// don't need to increment after i > k
			} else if (d < closeDis[longD]){	// if new closer value
				close[longD] = b;				// store new training value
				closeDis[longD] = d;			// and distance
				// then find which training value distance is longest
				for (int j = 0; j < closeDis.length; j++)
					if (closeDis[j] > closeDis[longD]) longD = j;
			}
		}

		String[] returnResult = new String[k];
		// if statement, for if K is greater than the number of bins to be returned
		if(close[k-1] == null){	// last bin not present
			for(int j = 0; j < k; j++){	// check index of last valid item
				if(close[j] == null){	// found last valid item
					k = j;	// make return array this long
					returnResult = new String[k];
					break;	// got last bin position break from loop
				}
			}
		}
		for(int j = 0; j < k; j++)	// load return array
			returnResult[j] = close[j].location();

		// return bin information
		return returnResult;
	}

	/**
	 * Uses the range for the parameters held by the bins to create a distance vector
	 * for the distance between the find bin and the bin being examined
	 * @param find, the origin point to find the closest bins to
	 * @param bin, the bin being tested for a distance
	 * @param range, the range for each parameter
	 * @return double, the distance vector found between find and bin
	 */
	private double calDistance(Bin find, Bin bin, double[] range) {
		double[] vecFind = find.getValues();
		double[] vecBin = bin.getValues();
		double ans = 0;
		double ele = 0;
		for(int i = 0; i < range.length; i++){
			ele = vecFind[i];
			ele -= vecBin[i];
			ele *= ele;
			ele /= range[i];
			ans += ele;
		}
		ans = Math.sqrt(ans);
		return ans;
	}

	/**
	 * Calculates the max/min of the current max/min and a given value.
	 * Also sorts by i to distinguish between bin parameters. Stores
	 * the max/min value in an array of max values
	 * @param i, the parameter index to measure
	 * @param val, the value to test for the max
	 */
	private void limit(int i, double val) {
		max[i] = Math.max(max[i], val);
		min[i] = Math.min(min[i], val);
	}

	/**
	 * Uses the max and min arrays from the training method to find
	 * the range of the given set of bins
	 * @return double[], an array of the range between the max and min values
	 * 					for each bin parameter
	 */
	private double[] getRange() {
		double[] r = new double[params];
		for(int i = 0; i < params; i++)
			r[i] = max[i] - min[i];

		return r;

	}
}
