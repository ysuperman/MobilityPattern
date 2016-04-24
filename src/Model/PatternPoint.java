package Model;
//模式点
public class PatternPoint {
	private double lon,lat;
	private String sTime,eTime;
	private int type;//模式类型：0：待识别；1:居家；2:工作；3:娱乐休闲；4:无法识别
	public PatternPoint(){
		
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
	public void setSTime(String sTime){
		this.sTime = sTime;
	}
	public String getSTime(){
		return this.sTime;
	}
	public void setETime(String eTime){
		this.eTime = eTime;
	}
	public String getETime(String eTime){
		return this.eTime;
	}
	public void setType(int type){
		this.type = type;
	}
	public int getType(int type){
		return this.type;
	}
}
