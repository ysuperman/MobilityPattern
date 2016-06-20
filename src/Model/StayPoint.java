package Model;

//用户停留点
public class StayPoint{
	private double lon,lat;
	private String sTime,eTime;
	private int event;//信令数据事件位，暂时不用
	private int state;//0：移动点;1：停留点；2:Origin;3:Destination
	private int type;//停留点类型，0:未标定；1:居住地；2:工作地；3:休闲地；当且仅当state＝＝1时该位有意义。
	public StayPoint(){
		
	}
	public StayPoint(StayPoint p){
		this.lon=p.lon;
		this.lat=p.lat;
		this.sTime=p.sTime;
		this.eTime=p.eTime;
		this.event=p.event;
		this.state=p.state;
		this.type=p.type;
	}
	public StayPoint(double lon,double lat,String sTime,String eTime,int event,int state,int type){
		this.lon = lon;
		this.lat = lat;
		this.sTime = sTime;
		this.eTime = eTime;
		this.event = event;
		this.state = state;
		this.type = type;
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
	public String getETime(){
		return this.eTime;
	}
	public void setEvent(int event){
		this.event = event;
	}
	public int getEvent(){
		return this.event;
	}
	public void setState(int state){
		this.state = state;
	}
	public int getState(){
		return this.state;
	}
	public void setType(int type){
		this.type = type;
	}
	public int getType(){
		return this.type;
	}
}