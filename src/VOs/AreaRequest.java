package VOs;

public class AreaRequest extends Request {

	
	private double lat;
	
	private double lon;
	
	private double dist;
	
	public AreaRequest() {
		super();
	}

	/**
	 * @return the lat
	 */
	public double getLat() {
		return lat;
	}

	/**
	 * @param lat the lat to set
	 */
	public void setLat(double lat) {
		this.lat = lat;
	}

	/**
	 * @return the lon
	 */
	public double getLon() {
		return lon;
	}

	/**
	 * @param lon the lon to set
	 */
	public void setLon(double lon) {
		this.lon = lon;
	}

	/**
	 * @return the dist
	 */
	public double getDist() {
		return dist;
	}

	/**
	 * @param dist the dist to set
	 */
	public void setDist(double dist) {
		this.dist = dist;
	}
}
