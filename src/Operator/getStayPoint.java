package Operator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/*
 * author:youg
 * date:20160328
 * 从5goodRecord中提取每个用户的停留点
 * 0raw:原始信令数据，不包含经纬度信息，按时间划分文件
 * 1withPos:添加经纬度信息，按id后两位划分文件，文件内按id和时间排序
 * 2timeSpan:记录每个ID每天最早出现的时间和位置以及最晚出现的时间和位置
 * 3timeLine：以15分钟为单位统计每个ID在每个时间段出现的次数
 * 4goodUser:数据质量好、用于下一步分析的用户ID列表。提取规则：7点前、19点后有记录，7-19点每3个小时有记录的用户数所占比例；用户比例：55%
 * 5goodRecord:4goodUser列表里的用户的完整记录，按id后两位分割到不同文件中
 * 7stayRecord:从5goodRecord中提取出的用户停留点记录
 */
//原始点
class RawPoint{
	public double lon,lat;
	public long lac,cell;
	public String time;
	public int event;//淇′护鏁版嵁浜嬩欢浣嶏紝鏆傛椂涓嶇敤
}
//用户原始记录
class RawRecord{
	public String id;
	public String date;
	public List<RawPoint> rawPoints;
	
	public RawRecord(String id, String date){
		this.id = id;
		this.date = date;
		rawPoints = new LinkedList<RawPoint>();
	}
}
//停留点
class StayPoint{
	public double lon,lat;
	public String sTime,eTime;
	public int event;//信令数据事件位，暂时不用
	public int state;//1：停留点；0：移动点
}
//用户停留点记录
class StayRecord{
	public String id;
	public String date;
	public List<StayPoint> stayPoints;
	public StayRecord(String id, String date){
		this.id = id;
		this.date = date;
		stayPoints = new LinkedList<StayPoint>();
	}
}

public class getStayPoint {
	public static String workPath = "F:\\BJmobile\\";
	public static String date = "20130226";
	public static String withPosPathName = workPath+date+"\\1withPos\\";
	public static String timeSpanPathName = workPath+date+"\\2timeSpan\\";
	public static String timeLinePathName = workPath+date+"\\3timeLine\\";
	public static String goodUserPathName = workPath+date+"\\4goodUser\\";
	public static String goodRecordPathName = workPath+date+"\\5goodRecord\\";
	public static String stayRecordPathName = workPath+date+"\\7stayRecord\\";
	public static final int TRD = 500;//(Threshold of Roaming Distance)空间阈值500米
	public static final int TRT = 30;//(Threshold of Roaming Time)空间阈值10分钟
	public static List<RawRecord> rawRecords = new LinkedList<RawRecord>();;
	public static List<StayRecord> stayRecords = new LinkedList<StayRecord>();;
	//读入RawRecord用户原始记录信息
	public static void importRawRecord(File goodRecordFileName)throws Exception{
		System.out.println("Now importing RawRecord: "+goodRecordFileName.getAbsolutePath());
		rawRecords.clear();
		BufferedReader br = new BufferedReader(new FileReader(goodRecordFileName));
		String lastId=null,thisId=null;
		String af;
		String[] afs;
		int len=0;
		while((af=br.readLine())!=null){
			afs = af.split(",");
			thisId = afs[0];
			if(!thisId.equals(lastId)){
				RawRecord user = new RawRecord(thisId,afs[1]);
				len+=1;
				rawRecords.add(user);
			}
			RawRecord user = rawRecords.get(len-1);
			RawPoint point = new RawPoint();
			point.lon=Double.valueOf(afs[5]);
			point.lat=Double.valueOf(afs[6]);
			point.lac=Long.valueOf(afs[3]);
			point.cell=Long.valueOf(afs[4]);
			point.time=afs[2];
			point.event=Integer.valueOf(afs[7]);
			user.rawPoints.add(point);
			lastId = thisId;
		}
		br.close();
	}
	//计算StayRecord用户停留点信息
	public static void calStayRecord(){
		System.out.println("Now calculating...");
		stayRecords.clear();
		for(RawRecord user:rawRecords){
			StayRecord stay = new StayRecord(user.id,user.date);
			List<RawPoint> tempStays = new LinkedList<RawPoint>();
			int start=0;
			while(start<user.rawPoints.size()){
				tempStays.clear();
				int newStart = start;
				tempStays.add(user.rawPoints.get(newStart));
				int newAdd = 1;
				int newEnd = newStart + newAdd;
				while(newAdd>0 && newStart<user.rawPoints.size()){
					newAdd = 0;
					for(int i=newStart;i<newEnd;i++){
						int j=i+1;
						while(j<user.rawPoints.size() && !tempStays.contains(user.rawPoints.get(j)) && distanceInGlobal(user.rawPoints.get(i),user.rawPoints.get(j))<TRD){
							tempStays.add(user.rawPoints.get(j));
							j+=1;
							newAdd+=1;
						}//end while k
					}//end for j
					newStart = newEnd;
					newEnd = newStart + newAdd;
				}//end while newAdd
				//do something with tempStays
				if(tempStays.size()==1 || timeSpan(tempStays.get(0).time,tempStays.get(tempStays.size()-1).time)<TRT){
					start+=1;
					StayPoint sp = new StayPoint();
					sp.lon = tempStays.get(0).lon;
					sp.lat = tempStays.get(0).lat;
					sp.sTime = tempStays.get(0).time;
					sp.eTime = tempStays.get(0).time;
					sp.event = tempStays.get(0).event;
					sp.state = 0;
					stay.stayPoints.add(sp);
				}else{
					start+=tempStays.size();
					double mLon = 0;
					double mLat = 0;
					for(int i=0;i<tempStays.size();i++){
						mLon+=tempStays.get(i).lon;
						mLat+=tempStays.get(i).lat;
					}
					mLon/=tempStays.size();
					mLat/=tempStays.size();
					StayPoint sp = new StayPoint();
					sp.lon = mLon;
					sp.lat = mLat;
					sp.sTime = tempStays.get(0).time;
					sp.eTime = tempStays.get(tempStays.size()-1).time;
					sp.event = 0;
					sp.state = 1;
					stay.stayPoints.add(sp);
				}
			}//end while newStart
			stayRecords.add(stay);
		}//end for user
	}
	//输出StayRecord用户停留点信息
	public static void exportStayRecord(String stayRecordFileName)throws Exception{
		stayRecordFileName = stayRecordPathName + stayRecordFileName;
		System.out.println("Now exporting StayRecord: "+stayRecordFileName);
		BufferedWriter bw = new BufferedWriter(new FileWriter(stayRecordFileName));
		DecimalFormat df = new DecimalFormat("#.000000");
		for(StayRecord user:stayRecords){
			for(StayPoint point:user.stayPoints){
				bw.write(user.id+","+user.date+","+point.sTime+","+point.eTime+","+df.format(point.lon)+","+df.format(point.lat)+","+point.state+"\n");
			}
		}
		bw.close();
	}
	/*
	 * 计算两位置之间的距离，根据球面坐标长度公式计算(单位：米)
	 * 注意，这个计算很耗时间,另外,这个计算把经纬度的100万倍还原了!
	 */
	public static double distanceInGlobal(double lon1, double lat1, double lon2, double lat2){
		double x1 = lon1;
		double y1 = lat1;
		double x2 = lon2;
		double y2 = lat2;

		double L = (3.1415926*6370/180)*Math.sqrt((Math.abs((x1)-(x2)))*(Math.abs((x1)-(x2)))*(Math.sin((90-(y1))*(3.1415926/180)))*(Math.sin((90-(y1))*(3.1415926/180)))+(Math.abs((y1)-(y2)))*(Math.abs((y1)-(y2))));
		return L * 1000;
	}
	public static double distanceInGlobal(RawPoint a, RawPoint b){
		return distanceInGlobal(a.lon, a.lat, b.lon, b.lat);
	}
	/*
	 * 计算两个时间点之间的时间差，时间格式“HHmmSS”，返回结果单位分钟
	 */
	public static int timeSpan(String a, String b){
		int span = Integer.valueOf(b.substring(0,2))*60+Integer.valueOf(b.substring(2,4));
		span = span - (Integer.valueOf(a.substring(0,2))*60+Integer.valueOf(a.substring(2,4)));
		return span;
	}
	public static void main(String[] args)throws Exception{
		File goodRecordPath = new File(goodRecordPathName);
		File[] goodRecordFiles = goodRecordPath.listFiles();
		//System.out.println(timeSpan("191708","191730"));
		
		for(File file:goodRecordFiles){
			importRawRecord(file);
			calStayRecord();
			exportStayRecord(file.getName());
			//break;
		}
		
		System.out.println("finish");
	}
}
