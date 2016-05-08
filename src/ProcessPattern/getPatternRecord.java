package ProcessPattern;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Config.Config;
import DBSCAN.Cluster;
import DBSCAN.ClusterAnalysis;
import DBSCAN.DataPoint;
import Model.PatternRecord;
import Model.StayPoint;
/*
 * author:youg
 * date:20160504
 * 从连续多天的StayRecord中提取每个用户的停留模式
 * 0raw:原始信令数据，不包含经纬度信息，按时间划分文件
 * 1fixed:添加经纬度信息，按id后两位划分文件，文件内按id和时间排序
 * 2timeSpan:记录每个ID每天最早出现的时间和位置以及最晚出现的时间和位置
 * 3timeLine：以15分钟为单位统计每个ID在每个时间段出现的次数
 * 4goodUser:数据质量好、用于下一步分析的用户ID列表。提取规则：7点前、19点后有记录，7-19点每3个小时有记录的用户数所占比例；用户比例：55%
 * 5goodRecord:4goodUser列表里的用户的完整记录，按id后两位分割到不同文件中
 * 7stayRecord:从5goodRecord中提取出的用户停留点记录
 * 8patternRecord:从连续多天的7stayRecord中提取出的每个用户的停留模式
 */
public class getPatternRecord {
	public static Map<String,List<DataPoint>> map;//存储（id,多日停留点序列）对
	public static List<PatternRecord> patternRecords;//
	public static BufferedReader br;
	public static BufferedWriter bw;
	//载入停留点数据
	public static void importStayRecord(File goodRecordFile)throws Exception{
		System.out.println("Now importing "+goodRecordFile.getAbsolutePath());
		br = new BufferedReader(new FileReader(goodRecordFile));
		String af;
		String[] afs;
		while((af=br.readLine())!=null){
			if(af.charAt(af.length()-1)=='0')
				continue;
			afs = af.split(",");
			if(!map.containsKey(afs[0]))
				map.put(afs[0], new LinkedList<DataPoint>());
			String[] times = afs[2].split("-");
			StayPoint sp = new StayPoint(Double.valueOf(afs[3]),Double.valueOf(afs[4]),times[0],times[1],0,Integer.valueOf(afs[5]),0);
			DataPoint dp = new DataPoint(String.valueOf(map.get(afs[0]).size()),afs[0],afs[1],sp,false);
			map.get(afs[0]).add(dp);
		}
		br.close();
	}
	/*
	 * 对集合内元素进行分析
	 * 该函数实现聚类主要策略
	 */
	public static PatternRecord generatePattern(String id,List<Cluster> clusterList){
		PatternRecord pr = new PatternRecord(id);
		//对每一个类簇进行处理
		for(Cluster cls:clusterList){
			
		}//endfor
		return null;
	}
	//输出停留模式
	public static void exportPatternRecord(File goodRecordFile)throws Exception{
		
	}
	public static void main(String[] args)throws Exception{
		Config.init();
		File workPath = new File(Config.getAttr(Config.WorkPath));
		File[] workPathPerday = workPath.listFiles();
		File[] stayRecordPathPerday = new File[workPathPerday.length];
		for(int i=0;i<workPathPerday.length;i++){
			stayRecordPathPerday[i] = new File(workPathPerday[i].getAbsolutePath()+File.separator+Config.StayRecordPath);
			//System.out.println(stayRecordPathPerday[i].getAbsolutePath());
		}
		if(stayRecordPathPerday.length<=0){
			System.out.println("no input files,please check the path config.");
			System.out.println("finish");
			return;
		}
		File[] stayRecordFiles = stayRecordPathPerday[0].listFiles();
		//一个处理周期，包括stayRecord文件夹下的一个文件的连续多天
		for(File stayRecordFile:stayRecordFiles){
			File[] stayRecordFilePerday = new File[stayRecordPathPerday.length];
			for(int i=0;i<stayRecordPathPerday.length;i++){
				stayRecordFilePerday[i] = new File(stayRecordPathPerday[i]+File.separator+stayRecordFile.getName());
				//System.out.println(stayRecordFilePerday[i].getAbsolutePath());
			}
			map = new HashMap<String,List<DataPoint>>();
			patternRecords = new LinkedList<PatternRecord>();
			for(File file:stayRecordFilePerday){
				importStayRecord(file);
			}
			for(String id:map.keySet()){
				ClusterAnalysis ca = new ClusterAnalysis();
				List<Cluster> clusterList = ca.doDbscanAnalysis(map.get(id), 1000, 1);
				PatternRecord patternRecord = generatePattern(id,clusterList);
				patternRecords.add(patternRecord);
				//todo
			}//endfor
			exportPatternRecord(stayRecordFile);
			//break;
		}//endfor
	}
}
