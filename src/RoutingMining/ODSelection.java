package RoutingMining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import Config.Config;
import Model.RawPoint;
import Model.RawRecord;
import Model.StayPoint;
import Model.StayRecord;

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
	
	public static List<StayRecord> stayRecords = new LinkedList<StayRecord>();
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
			StayRecord user = stayRecords.get(len-1);
			StayPoint point = new StayPoint();
			point.setLon(Double.valueOf(afs[5]));
			point.setLat(Double.valueOf(afs[6]));
			point.setEvent(Integer.valueOf(afs[7]));
			user.getStayPoints().add(point);
			lastId = thisId;
		}
		br.close();
	}
	
	//导出OD数据
	public static void exportOD(String ODFileName)throws Exception{
		
	}
	
	public static void main(String[] args)throws Exception{
		Config.init();
		File stayRecordPath = new File(Config.getAttr(Config.StayRecordPath));
		File[] stayRecordFiles = stayRecordPath.listFiles();
		for(File file:stayRecordFiles){
			importStayRecord(file);
			//extractOD();
			exportOD(file.getName());
		}
		System.out.println("finish");
	}
}
