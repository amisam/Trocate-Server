import java.io.Serializable;

/**
 * a bin object to hold coordinates of a bin and a bin type.
 * Implements serializable to be written as an object to a file
 * @author hartesamu
 *
 */
public class Bin implements Serializable{

	/**
	 * A unique ID of the objects class
	 */
	private static final long serialVersionUID = -5142565806931287996L;
	public final double lat;
	public final double lng;
	public final String type;

	/**
	 * A Bin constructor that takes a location and bin type
	 * @param lat, latitude of a Bin
	 * @param lng, longitude of a Bin
	 * @param type, type of a Bin
	 */
	public Bin (double lat, double lng, String type){
		this.lat = lat;
		this.lng = lng;
		this.type = type;
	}

	/**
	 * Gets this Bins parameters returning lat then lng
	 * @return double[], the parameters of this bin object
	 */
	public double[] getValues(){
		return new double[]{lat, lng};
	}

	/**
	 * A type of toString method that gives the location of this bin followed by its type
	 * @return String, a discription of this bin
	 */
	public String location(){
		return lat+"\t"+lng+"\t"+type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lng);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/**
	 * Tests if a given object is the same as this Bin.
	 * Given both the object and this are Bins will compare
	 * the lat, lng and type of bin to check weather they match
	 */
	@Override public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bin other = (Bin) obj;
		if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
			return false;
		if (Double.doubleToLongBits(lng) != Double.doubleToLongBits(other.lng))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equalsIgnoreCase(other.type))
			return false;
		return true;
	}



}
