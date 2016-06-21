package Analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
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
		   File userNameFile = new File("F:\\Experiment\\sp1.txt");
		   File goodRecordFile = new File("F:\\BJmobile\\20130226\\5goodRecord\\00.txt");
		   //读入user list
		   BufferedReader br = new BufferedReader(new FileReader(userNameFile));
		   String[] userList = new String[20];
		   String af=null;
		   int i=0;
		   while((af=br.readLine())!=null){
			   userList[++i]=af;
		   }
		   br.close();
		   //对每个user进行分析
		   for(String user:userList){
			   //去goodRecord挑user的record
			   br = new BufferedReader(new FileReader(goodRecordFile));
			   
			   br.close();
		   }

		   
		   
	       ArrayList<DataPoint> dpoints = new ArrayList<DataPoint>();
	      /*
	       double[] a={2,3};
	       double[] b={2,4};
	       double[] c={1,4};
	       double[] d={1,3};
	       double[] e={2,2};
	       double[] f={3,2};

	       double[] g={8,7};
	       double[] h={8,6};
	       double[] i={7,7};
	       double[] j={7,6};
	       double[] k={8,5};

	       double[] l={100,2};//孤立点


	       double[] m={8,20};
	       double[] n={8,19};
	       double[] o={7,18};
	       double[] p={7,17};
	       double[] q={8,21};

	       dpoints.add(new DataPoint(a,"a",false));
	       dpoints.add(new DataPoint(b,"b",false));
	       dpoints.add(new DataPoint(c,"c",false));
	       dpoints.add(new DataPoint(d,"d",false));
	       dpoints.add(new DataPoint(e,"e",false));
	       dpoints.add(new DataPoint(f,"f",false));

	       dpoints.add(new DataPoint(g,"g",false));
	       dpoints.add(new DataPoint(h,"h",false));
	       dpoints.add(new DataPoint(i,"i",false));
	       dpoints.add(new DataPoint(j,"j",false));
	       dpoints.add(new DataPoint(k,"k",false));

	       dpoints.add(new DataPoint(l,"l",false));

	       dpoints.add(new DataPoint(m,"m",false));
	       dpoints.add(new DataPoint(n,"n",false));
	       dpoints.add(new DataPoint(o,"o",false));
	       dpoints.add(new DataPoint(p,"p",false));
	       dpoints.add(new DataPoint(q,"q",false));
			*/
	       ClusterAnalysis ca=new ClusterAnalysis();
	       List<Cluster> clusterList=ca.doDbscanAnalysis(dpoints, 2, 4);
	       ca.displayCluster(clusterList);

	   }
}
