package Model;

//停留点
public class StayPoint{
	public double lon,lat;
	public String sTime,eTime;
	public int event;//信令数据事件位，暂时不用
	public int state;//1：停留点；0：移动点
	public int type;//停留点类型，0:未标定；1:居住地；2:工作地；3:休闲地；当且仅当state＝＝1时该位有意义。
	public StayPoint(){
		
	}
}