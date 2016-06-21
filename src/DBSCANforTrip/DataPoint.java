package DBSCANforTrip;

import java.util.LinkedList;
import java.util.List;

import Model.StayPoint;

public class DataPoint {
    private String dataPointName; // 样本点名
    private String id;
    private String date;
    private List<StayPoint> stayPoints;
    private boolean isKey; //是否是核心对象

    public DataPoint(){
    	stayPoints = new LinkedList<StayPoint>();
    }
    public DataPoint(String dataPointName,String id,String date,List<StayPoint> sps,boolean isKey){
    	this.dataPointName = dataPointName;
    	this.id = id;
    	this.date = date;
    	this.stayPoints = sps;
    	this.isKey = isKey;
    }
    public DataPoint(DataPoint dp){
    	this.dataPointName = dp.dataPointName;
    	this.id = dp.id;
    	this.date = dp.date;
    	this.stayPoints = dp.stayPoints;
    	this.isKey = dp.isKey;
    }
    public String getDataPointName() {
        return dataPointName;
    }
    public void setDataPointName(String name) {
        this.dataPointName = name;
    }
    public String getId(){
    	return id;
    }
    public void setId(String id){
    	this.id = id;
    }
    public String getDate(){
    	return date;
    }
    public void setDate(String date){
    	this.date = date;
    }
    public List<StayPoint> getStayPoints(){
    	return stayPoints;
    }
    public boolean isKey(){
    	return isKey;
    }
    public void setKey(boolean isKey){
    	this.isKey = isKey;
    }
}