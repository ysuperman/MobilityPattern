package RoutesMining;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import Config.Config;
import Model.RawPoint;
import Model.RawRecord;
import Model.StayPoint;
import Model.StayRecord;

/*
 * author:youg
 * date:20160614
 * 从StayRecord中提取指定OD范围内的Trips
 * 0raw:原始信令数据，不包含经纬度信息，按时间划分文件
 * 1fixed:添加经纬度信息，按id后两位划分文件，文件内按id和时间排序
 * 2timeSpan:记录每个ID每天最早出现的时间和位置以及最晚出现的时间和位置
 * 3timeLine：以15分钟为单位统计每个ID在每个时间段出现的次数
 * 4goodUser:数据质量好、用于下一步分析的用户ID列表。提取规则：7点前、19点后有记录，7-19点每3个小时有记录的用户数所占比例；用户比例：55%
 * 5goodRecord:4goodUser列表里的用户的完整记录，按id后两位分割到不同文件中
 * 7stayRecord:从5goodRecord中提取出的用户停留点记录
 * 8OD:从7stayRecord中提取出的指定OD范围内的Trips
 */

public class ODSelection {
	//O-天通苑
	public static final double OMaxLon = 116.442177;
	public static final double OMinLon = 116.406162;
	public static final double OMaxLat = 40.079664;
	public static final double OMinLat = 40.061106;
	//D-国贸
	public static final double DMaxLon = 116.477652;
	public static final double DMinLon = 116.450161;
	public static final double DMaxLat = 39.913619;
	public static final double DMinLat = 39.903507;
	//R-Routes范围
	public static final double RMaxLon = 116.548925;
	public static final double RMinLon = 116.391873;
	public static final double RMaxLat = 40.113943;
	public static final double RMinLat = 39.893205;
	
	public static List<StayRecord> stayRecords = new LinkedList<StayRecord>();
	public static List<StayRecord> ODRecords = new LinkedList<StayRecord>();
	
	//载入停留点数据
	public static void importStayRecord(File stayRecordFile)throws Exception{
		System.out.println("Now importing StayRecord: "+stayRecordFile.getAbsolutePath());
		stayRecords.clear();
		BufferedReader br = new BufferedReader(new FileReader(stayRecordFile));
		String lastId=null,thisId=null;
		String af;
		String[] afs;
		int len=0;
		while((af=br.readLine())!=null){
			afs = af.split(",");
			thisId = afs[0];
			if(!thisId.equals(lastId)){
				StayRecord user = new StayRecord(thisId,afs[1]);
				len+=1;
				stayRecords.add(user);
			}
			String[] times = afs[2].split("-");
			StayRecord user = stayRecords.get(len-1);
			StayPoint point = new StayPoint();
			point.setLon(Double.valueOf(afs[3]));
			point.setLat(Double.valueOf(afs[4]));
			point.setState(Integer.valueOf(afs[5]));
			point.setType(0);
			point.setSTime(times[0]);
			point.setETime(times[1]);
			user.getStayPoints().add(point);
			lastId = thisId;
		}
		br.close();
	}
	//提取指定OD范围内的trips
	public static void extractOD(){
		System.out.println("Now extracting OD");
		ODRecords.clear();
		for(StayRecord user:stayRecords){
			StayRecord person = new StayRecord(user.getId(),user.getDate());
			ODRecords.add(person);
			int label=-1;
			for(int i=0;i<user.getStayPoints().size();i++){
				double lon = user.getStayPoints().get(i).getLon();
				double lat = user.getStayPoints().get(i).getLat();
				//如果不是停留点，判断是否在R范围内，如果在，label=-1，不管在不在，continue
				if(user.getStayPoints().get(i).getState()==0){
					if(lon<RMinLon || lon>RMaxLon || lat<RMinLat || lat>RMaxLat)
						label=-1;
					continue;
				}
				//如果起点在O范围内
				if(lon<OMaxLon && lon>OMinLon && lat<OMaxLat && lat>OMinLat){
					label=i;
					continue;
				}
				//如果终点在D范围内//-  && i-label+1>=40 
				if(label!=-1 && lon<DMaxLon && lon>DMinLon && lat<DMaxLat && lat>DMinLat){
					for(int j=label;j<=i;j++){
						StayPoint point = new StayPoint(user.getStayPoints().get(j));
						if(j==label)
							point.setState(2);
						if(j==i)
							point.setState(3);
						person.getStayPoints().add(point);
					}
				}
				label=-1;
			}
		}
	}
	//导出OD数据
	public static void exportOD(String ODRecordFileName)throws Exception{
		File dir=new File( Config.getAttr(Config.ODRecordPath));
		if (!dir.exists()) dir.mkdirs();
		ODRecordFileName = Config.getAttr(Config.ODRecordPath)+File.separator + ODRecordFileName;
		
		System.out.println("Now exporting ODRecord: "+ODRecordFileName);
		BufferedWriter bw = new BufferedWriter(new FileWriter(ODRecordFileName,true));
		DecimalFormat df = new DecimalFormat("#.000000");
		for(StayRecord user:ODRecords){
			for(StayPoint point:user.getStayPoints()){
				bw.write(user.getId()+","+user.getDate()+","+point.getSTime()+"-"+point.getETime()+","+df.format(point.getLon())+","+df.format(point.getLat())+","+point.getState()+"\n");
			}
		}
		bw.close();
	}
	
	public static void main(String[] args)throws Exception{
		Config.init();
		File stayRecordPath = new File(Config.getAttr(Config.StayRecordPath));
		File[] stayRecordFiles = stayRecordPath.listFiles();
		int k=0;
		for(File file:stayRecordFiles){
			k++;
			//if((k&1)==1)
			//	continue;
			importStayRecord(file);
			extractOD();
		//	exportOD("moreThan40Out.txt");
			exportOD("OutAll.txt");
			
			//if(k>=40)
			//	break;
		}
		System.out.println("finish");
	}
}
