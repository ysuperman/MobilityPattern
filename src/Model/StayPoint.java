package Model;

//停留点
public class StayPoint{
	public double lon,lat;
	public String sTime,eTime;
	public int event;//信令数据事件位，暂时不用
	public int state;//1：停留点；0：移动点
	public StayPoint(){
		
	}
}