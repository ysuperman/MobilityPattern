package Model;

//原始点
public class RawPoint{
	private double lon,lat;
	private long lac,cell;
	private String time;
	private int event;//信令数据事件位，暂时不用
	public RawPoint(){
	}
	public void setLon(double lon){
		this.lon = lon;
	}
	public double getLon(){
		return this.lon;
	}
	public void setLat(double lat){
		this.lat = lat;
	}
	public double getLat(){
		return this.lat;
	}
	public void setLac(long lac){
		this.lac = lac;
	}
	public long getLac(){
		return this.lac;
	}
	public void setCell(long cell){
		this.cell = cell;
	}
	public long getCell(){
		return this.cell;
	}
	public void setTime(String time){
		this.time = time;
	}
	public String getTime(){
		return this.time;
	}
	public void setEvent(int event){
		this.event = event;
	}
	public int getEvent(){
		return this.event;
	}
}