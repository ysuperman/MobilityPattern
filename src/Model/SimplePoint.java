package Model;

public class SimplePoint {
	String s;
	double Lon;
	double Lat;
	public SimplePoint(String s,double Lon,double Lat){
		this.s=s;
		this.Lon=Lon;
		this.Lat=Lat;
	}
	public String getString(){
		return s;
	}
	public double getLon(){
		return Lon;
	}
	public double getLat(){
		return Lat;
	}
}
