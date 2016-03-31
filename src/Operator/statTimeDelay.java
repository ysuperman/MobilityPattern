package Operator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class statTimeDelay {
	/*
	 * author:youg
	 * date:20160329
	 * 统计相邻信令记录间距离分布
	 * 0raw:原始信令数据，不包含经纬度信息，按时间划分文件
	 * 1withPos:添加经纬度信息，按id后两位划分文件，文件内按id和时间排序
	 * 2timeSpan:记录每个ID每天最早出现的时间和位置以及最晚出现的时间和位置
	 * 3timeLine：以15分钟为单位统计每个ID在每个时间段出现的次数
	 * 4goodUser:数据质量好、用于下一步分析的用户ID列表。提取规则：7点前、19点后有记录，7-19点每3个小时有记录的用户数所占比例；用户比例：55%
	 * 5goodRecord:4goodUser列表里的用户的完整记录，按id后两位分割到不同文件中
	 * 7stayRecord:从5goodRecord中提取出的用户停留点记录
	 */
	public static String workPath = "F:\\BJmobile\\";
	public static String date = "20130226";
	public static String withPosPathName = workPath+date+"\\1withPos\\";
	public static String timeSpanPathName = workPath+date+"\\2timeSpan\\";
	public static String timeLinePathName = workPath+date+"\\3timeLine\\";
	public static String goodUserPathName = workPath+date+"\\4goodUser\\";
	public static String goodRecordPathName = workPath+date+"\\5goodRecord\\";
	public static String stayRecordPathName = workPath+date+"\\7stayRecord\\";
	public static int[] stat = new int[21];
	
	public static double distanceInGlobal(double lon1, double lat1, double lon2, double lat2){
		double x1 = lon1;
		double y1 = lat1;
		double x2 = lon2;
		double y2 = lat2;

		double L = (3.1415926*6370/180)*Math.sqrt((Math.abs((x1)-(x2)))*(Math.abs((x1)-(x2)))*(Math.sin((90-(y1))*(3.1415926/180)))*(Math.sin((90-(y1))*(3.1415926/180)))+(Math.abs((y1)-(y2)))*(Math.abs((y1)-(y2))));
		return L * 1000;
	}
	public static void calDis(File goodRecordFile)throws Exception{
		System.out.println("Now calulating Dis:"+goodRecordFile.getAbsolutePath());
		BufferedReader br = new BufferedReader(new FileReader(goodRecordFile));
		String af;
		String[] afs;
		String lastId=null,thisId;
		double lastLon=0.0,lastLat=0.0;
		double thisLon=0.0,thisLat=0.0;
		while((af=br.readLine())!=null){
			afs = af.split(",");
			thisId = afs[0];
			if(thisId.equals(lastId)){
				thisLon=Double.valueOf(afs[5]);
				thisLat=Double.valueOf(afs[6]);
				int dis = (int)distanceInGlobal(thisLon,thisLat,lastLon,lastLat);
				if(dis==0)
					continue;
				if(dis<2000)
					stat[dis/100]+=1;
				else
					stat[20]+=1;
			}
			lastId=thisId;
			lastLon=thisLon;
			lastLat=thisLat;
		}
		br.close();
	}
	public static void main(String[] args)throws Exception{
		File goodRecordPath = new File(goodRecordPathName);
		File[] goodRecordFiles = goodRecordPath.listFiles();
		int j=0;
		for(File file:goodRecordFiles){
			calDis(file);
			if(++j>5)
				break;
		}
		System.out.println("finish");
		for(int i=0;i<stat.length;i++)
			System.out.println(i+":"+stat[i]);
	}
}
