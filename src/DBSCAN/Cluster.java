package DBSCAN;

import java.util.LinkedList;
import java.util.List;

public class Cluster {
	private List<DataPoint> dataPoints = new LinkedList<DataPoint>(); // 类簇中的样本点
    private String clusterName; //簇名

    public List<DataPoint> getDataPoints() {
        return dataPoints;
    }

    public void setDataPoints(List<DataPoint> dataPoints) {
        this.dataPoints = dataPoints;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

}