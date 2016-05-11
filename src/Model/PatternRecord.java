package Model;

import java.util.LinkedList;
import java.util.List;

//用户模式点记录序列
public class PatternRecord {
	private String id;
	private List<PatternPoint> normalPoints;
	private List<PatternPoint> dynamicPoints;
	public PatternRecord(){
		
	}
	public PatternRecord(String id){
		this.id = id;
		normalPoints = new LinkedList<PatternPoint>();
		dynamicPoints = new LinkedList<PatternPoint>();
	}
	public void setId(String id){
		this.id = id;
	}
	public String getId(){
		return this.id;
	}
	public List<PatternPoint> getNormalPoints(){
		return this.normalPoints;
	}
	public List<PatternPoint> getDynamicPoints(){
		return this.dynamicPoints;
	}
	@Override
	public String toString(){
		String ans=id+"\n";
		ans+="normalPoints:\n";
		for(PatternPoint np:normalPoints){
			ans+=np.toString()+"\n";
		}
		ans+="dynamicPoints:\n";
		for(PatternPoint dp:dynamicPoints){
			ans+=dp.toString()+"\n";
		}
		return ans;
	}
}
