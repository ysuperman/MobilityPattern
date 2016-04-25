package Model;

import java.util.LinkedList;
import java.util.List;

//用户模式点记录序列
public class PatternRecord {
	private String id;
	private List<PatternPoint> patternPoints;
	public PatternRecord(){
		
	}
	public PatternRecord(String id){
		this.id = id;
		patternPoints = new LinkedList<PatternPoint>();
	}
	public void setId(String id){
		this.id = id;
	}
	public String getId(){
		return this.id;
	}
	public List<PatternPoint> getPatternPoints(){
		return this.patternPoints;
	}
}
