package Analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;

import Config.Config;

public class DistanceBetweenAdjacentRecords {
	public static BufferedReader br;
	public static BufferedWriter bw;
	public static DecimalFormat df = new DecimalFormat("#0.000000");
	public static double maxLon,maxLat,minLon,minLat;
	public static int idLength;
	public static void statDistance(File goodRecordPath)throws Exception{
		File[] files = goodRecordPath.listFiles();
		int[] disDis = new int[32];
		for(File file:files){
			System.out.println("Now stating distance "+file.getAbsolutePath());
			br = new BufferedReader(new FileReader(file));
			String af,lastAf=null;
			double lon1=0.0,lon2=0.0,lat1=0.0,lat2=0.0;
			while((af=br.readLine())!=null){
				String[] afs = af.split(",");
				lon1 = Double.valueOf(afs[5]);
				lat1 = Double.valueOf(afs[6]);
				if(afs[0].equals(lastAf)){
					//计算距离
					int dis = (int)distanceInGlobal(lon1,lat1,lon2,lat2);
					if(dis==0)
						continue;
					dis=dis/100;
					if(dis<31)
						disDis[dis]+=1;
					else
						disDis[31]+=1;
				}
				lon2 = lon1;
				lat2 = lat1;
				lastAf = afs[0];
			}
			br.close();
		}
		for(int i=0;i<disDis.length;i++){
			System.out.println(i+":"+disDis[i]);
		}
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
	public static void main(String[] args)throws Exception{
		Config.init();
		maxLon = Double.valueOf(Config.getAttr(Config.CityMaxLon));
		minLon = Double.valueOf(Config.getAttr(Config.CityMinLon));
		maxLat = Double.valueOf(Config.getAttr(Config.CityMaxLat));
		minLat = Double.valueOf(Config.getAttr(Config.CityMinLat));
		idLength = Integer.valueOf(Config.getAttr(Config.IdLength));
		statDistance(new File(Config.getAttr(Config.GoodRecordPath)));
	}
}
