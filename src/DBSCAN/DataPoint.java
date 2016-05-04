package DBSCAN;

public class DataPoint {
    private String dataPointName; // 样本点名
    private double dimensioin[]; // 样本点的维度
    private boolean isKey; //是否是核心对象

    public DataPoint(){

    }

    public DataPoint(double[] dimensioin,String dataPointName,boolean isKey){
         this.dataPointName=dataPointName;
         this.dimensioin=dimensioin;
         this.isKey=isKey;
    }
    public String getDataPointName() {
        return dataPointName;
    }
    public void setDataPointName(String name) {
        this.dataPointName = name;
    }
    public double[] getDimensioin() {
        return dimensioin;
    }
    public void setDimensioin(double[] dimensioin) {
        this.dimensioin = dimensioin;
    }
    public boolean isKey(){
    	return isKey;
    }
    public void setKey(boolean isKey){
    	this.isKey = isKey;
    }
}