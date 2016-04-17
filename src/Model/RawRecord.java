package Model;

import java.util.LinkedList;
import java.util.List;


//用户原始记录
public class RawRecord{
	private String id;
	private String date;
	private List<RawPoint> rawPoints;
	public RawRecord(){
		
	}
	public RawRecord(String id, String date){
		this.id = id;
		this.date = date;
		rawPoints = new LinkedList<RawPoint>();
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
	public void setRawPoints(){
		rawPoints = new LinkedList<RawPoint>();
	}
	public List<RawPoint> getRawPoints(){
		return rawPoints;
	}
}
