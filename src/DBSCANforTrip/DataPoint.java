package DBSCANforTrip;

import Model.StayPoint;

public class DataPoint {
    private String dataPointName; // 样本点名
    private String id;
    private String date;
    private String sTime,eTime;
    private double lon,lat;
    private boolean isKey; //是否是核心对象

    public DataPoint(){

    }
    public DataPoint(String dataPointName,String id,String date,StayPoint sp,boolean isKey){
    	this.dataPointName = dataPointName;
    	this.id = id;
    	this.date = date;
    	this.lon = sp.getLon();
    	this.lat = sp.getLat();
    	this.sTime = sp.getSTime();
    	this.eTime = sp.getETime();
    	this.isKey = isKey;
    }
    public DataPoint(DataPoint dp){
    	this.dataPointName = dp.dataPointName;
    	this.id = dp.id;
    	this.date = dp.date;
    	this.sTime = dp.sTime;
    	this.eTime = dp.eTime;
    	this.lon = dp.lon;
    	this.lat = dp.lat;
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
    public double getLon(){
    	return lon;
    }
    public void setLon(double lon){
    	this.lon = lon;
    }
    public double getLat(){
    	return lat;
    }
    public void setLat(double lat){
    	this.lat = lat;
    }
    public String getDate(){
    	return date;
    }
    public void setDate(String date){
    	this.date = date;
    }
    public String getSTime(){
    	return sTime;
    }
    public void setSTime(String sTime){
    	this.sTime = sTime;
    }
    public String getETime(){
    	return eTime;
    }
    public void setETime(String eTime){
    	this.eTime = eTime;
    }
    public boolean isKey(){
    	return isKey;
    }
    public void setKey(boolean isKey){
    	this.isKey = isKey;
    }
}