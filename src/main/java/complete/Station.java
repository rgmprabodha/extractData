package complete;

public class Station {

	private String ID;
	private String name;
	private double lat;
	private double lon;
	private int capacity;

	public Station(String ID, String name, double lat, double lon, int cp) {
		this.ID = ID;
		this.name = name;
		this.lat = lat;
		this.lon = lon;
		this.capacity = cp;
	}
	
	public void setID(String ID) {
		this.ID = ID;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	public void setLon(double lon) {
		this.lon = lon;
	}
	
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	public String getID() {
		return this.ID;
	}
	
	public String getName() {
		return this.name;
	}
	
	public double getLat() {
		return this.lat;
	}
	
	public double getLon() {
		return this.lon;
	}
	
	public int getCapacity() {
		return this.capacity;
	}
}
