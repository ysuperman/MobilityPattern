package RoutesMining;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import DBSCANforTrip.Cluster;
import DBSCANforTrip.ClusterAnalysis;
import DBSCANforTrip.DataPoint;
import Model.StayPoint;
import Model.StayRecord;

/*
 * author:youg
 * date:20160621
 * 使用DBSCANtorTrip算法对trips进行聚类，生成routes
 * 0raw:原始信令数据，不包含经纬度信息，按时间划分文件
 * 1fixed:添加经纬度信息，按id后两位划分文件，文件内按id和时间排序
 * 2timeSpan:记录每个ID每天最早出现的时间和位置以及最晚出现的时间和位置
 * 3timeLine：以15分钟为单位统计每个ID在每个时间段出现的次数
 * 4goodUser:数据质量好、用于下一步分析的用户ID列表。提取规则：7点前、19点后有记录，7-19点每3个小时有记录的用户数所占比例；用户比例：55%
 * 5goodRecord:4goodUser列表里的用户的完整记录，按id后两位分割到不同文件中
 * 7stayRecord:从5goodRecord中提取出的用户停留点记录
 */
public class RoutesClustering {
	public static BufferedReader br;
	public static BufferedWriter bw;
	public static Map<String,StayRecord> map = new HashMap<String,StayRecord>();
	//载入停留点数据
	public static void importStayRecord(File goodRecordFile)throws Exception{
		System.out.println("Now importing "+goodRecordFile.getAbsolutePath());
		br = new BufferedReader(new FileReader(goodRecordFile));
		String af;
		String[] afs;
		while((af=br.readLine())!=null){
			//if(af.charAt(af.length()-1)=='0')
			//	continue;
			afs = af.split(",");
			if(!map.containsKey(afs[0]))
				map.put(afs[0], new StayRecord(afs[0],afs[1]));
			String[] times = afs[2].split("-");
			StayPoint sp = new StayPoint(Double.valueOf(afs[3]),Double.valueOf(afs[4]),times[0],times[1],0,Integer.valueOf(afs[5]),0);
			map.get(afs[0]).getStayPoints().add(sp);
		}
		br.close();
	}
	//导出ODRoutes
	public static void exportODRoutes(List<Cluster> clusterList,File ODRoutesPath)throws Exception{
        if(clusterList!=null){
            for(Cluster tempCluster:clusterList){
                if(tempCluster.getDataPoints()!=null&&tempCluster.getDataPoints().size()>0){
                	bw = new BufferedWriter(new FileWriter(ODRoutesPath.getAbsolutePath()+"\\"+tempCluster.getClusterName()+".txt"));
                    
                    for(DataPoint dp:tempCluster.getDataPoints()){
                        for(StayPoint sp:dp.getStayPoints()){
                        	bw.write(dp.getId()+","+dp.getDate()+","+sp.getSTime()+"-"+sp.getETime()+","+sp.getLon()+","+sp.getLat()+","+sp.getState()+"\n");
                        }
                    }
                    bw.close();
                }
            }
        }
	}
	public static void main(String[] args)throws Exception{
		List<DataPoint> dpoints = new LinkedList<DataPoint>();
		File ODRecordFile = new File("F:\\BJmobile\\20130226\\8ODRecord\\100out.txt");
		File ODRoutesPath = new File("F:\\BJmobile\\20130226\\9ODRoutes\\");
		
		importStayRecord(ODRecordFile);
		int k=0;
		for(String user:map.keySet()){
			StayRecord sr = map.get(user);
			DataPoint dp = new DataPoint(String.valueOf(k++),sr.getId(),sr.getDate(),sr.getStayPoints(),false);
			dpoints.add(dp);
		}
		ClusterAnalysis ca = new ClusterAnalysis();
		List<Cluster> clusterList = ca.doDbscanAnalysis(dpoints, 800, 5);
		int nc = ca.displayCluster(clusterList);
		System.out.println("Number of Cluster:"+nc);
		//exportODRoutes(clusterList,ODRoutesPath);
	}
}
