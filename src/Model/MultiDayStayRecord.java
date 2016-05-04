package Model;

import java.util.LinkedList;
import java.util.List;

public class MultiDayStayRecord {
	private String id;
	private List<StayRecord> stayRecords;
	public MultiDayStayRecord(){
		
	}
	public MultiDayStayRecord(String id){
		this.id = id;
		stayRecords = new LinkedList<StayRecord>();
	}
	public String getId(){
		return id;
	}
	public void setId(String id){
		this.id = id;
	}
	public List<StayRecord> getStayRecords(){
		return stayRecords;
	}
}
