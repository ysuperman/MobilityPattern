package Model;
//模式点
public class PatternPoint {
	private double lon,lat;
	private String sTime,eTime;
	private int type;//模式类型：0：待识别；1:居家；2:工作；3:娱乐休闲；4:无法识别
	private int num;//原始停留点数量
	private double prob;//类型为type的概率：0~1
	public PatternPoint(){
		
	}
	public PatternPoint(double lon,double lat,String sTime,String eTime,int type,int num,double prob){
		this.lon = lon;
		this.lat = lat;
		this.sTime = sTime;
		this.eTime = eTime;
		this.type = type;
		this.num = num;
		this.prob = prob;
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
	public void setType(int type){
		this.type = type;
	}
	public int getType(){
		return this.type;
	}
	public void setNum(int num){
		this.num = num;
	}
	public int getNum(){
		return this.num;
	}
	public void setProb(double prob){
		this.prob = prob;
	}
	public double getProb(){
		return this.prob;
	}
}
