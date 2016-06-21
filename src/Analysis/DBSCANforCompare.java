package Analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import DBSCAN.Cluster;
import DBSCAN.ClusterAnalysis;
import DBSCAN.DataPoint;
/*
 * author:youg
 * date:20160621
 * 使用DBSCAN计算user的停留点，与STDC计算的结果做对比分析用
 * 0raw:原始信令数据，不包含经纬度信息，按时间划分文件
 * 1fixed:添加经纬度信息，按id后两位划分文件，文件内按id和时间排序
 * 2timeSpan:记录每个ID每天最早出现的时间和位置以及最晚出现的时间和位置
 * 3timeLine：以15分钟为单位统计每个ID在每个时间段出现的次数
 * 4goodUser:数据质量好、用于下一步分析的用户ID列表。提取规则：7点前、19点后有记录，7-19点每3个小时有记录的用户数所占比例；用户比例：55%
 * 5goodRecord:4goodUser列表里的用户的完整记录，按id后两位分割到不同文件中
 * 7stayRecord:从5goodRecord中提取出的用户停留点记录
 */
public class DBSCANforCompare {
	public static void main(String[] args)throws Exception{
		List<DataPoint> dpoints;
		int targetCluster=5;
		int totalCluster=0;
		int userSetSize=100;
		File userNameFile = new File("F:\\Experiment\\sp5.txt");
		File goodRecordFile = new File("F:\\BJmobile\\20130226\\5goodRecord\\00.txt");
		int wa=0;
		//读入user list
		BufferedReader br = new BufferedReader(new FileReader(userNameFile));
		String[] userList = new String[userSetSize];
		String af=null;
		int i=0;
		while((af=br.readLine())!=null){
			userList[i++]=af;
		}
		br.close();
		//对每个user进行分析
		for(String user:userList){
			//去goodRecord挑该user的record，并存入DataPointList中
			System.out.println("Now dealing with user:"+user);
			dpoints = new LinkedList<DataPoint>();
			br = new BufferedReader(new FileReader(goodRecordFile));
			af=null;
			String[] afs;
			int name=0;
			while((af=br.readLine())!=null){
				afs = af.split(",");
				if(!user.equals(afs[0]))
					continue;
				DataPoint dp = new DataPoint();
				dp.setDataPointName(String.valueOf(name++));
				dp.setId(user);
				dp.setDate(afs[1]);
				dp.setLon(Double.valueOf(afs[5]));
				dp.setLat(Double.valueOf(afs[6]));
				dp.setSTime(afs[2]);
				dp.setETime(afs[2]);
				dp.setKey(false);
				dpoints.add(dp);
			}
			br.close();
			ClusterAnalysis ca=new ClusterAnalysis();
		    List<Cluster> clusterList=ca.doDbscanAnalysis(dpoints, 1200, 5);
		    int nc = ca.displayCluster(clusterList);
		    totalCluster+=nc;
		    if(nc!=targetCluster)
		    	wa++;
		    System.out.println("#############################################");
		}
		System.out.println("wrong="+wa);
		System.out.println("avg="+totalCluster*1.0/userSetSize);
	}
}
