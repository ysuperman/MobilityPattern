package Model;

import java.util.LinkedList;
import java.util.List;


//用户停留点记录
public class StayRecord{
	private String id;
	private String date;
	private List<StayPoint> stayPoints;
	public StayRecord(){
		
	}
	public StayRecord(String id, String date){
		this.id = id;
		this.date = date;
		stayPoints = new LinkedList<StayPoint>();
	}
	public void setId(String id){
		this.id = id;
	}
	public String getId(){
		return this.id;
	}
	public void setDate(String date){
		this.date = date;
	}
	public String getDate(){
		return this.date;
	}
	public List<StayPoint> getStayPoints(){
		return this.stayPoints;
	}
}