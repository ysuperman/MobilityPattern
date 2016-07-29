package RoutesMerge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Scanner;

import Config.Config;
import Model.SimplePoint;

/* 本类将用户ID在某一时期内，每天的轨迹通过简单归并的算法(最近几何距离)进行合并。
 * 输入为用户ID，起始日期，截止日期
 * 输入要求：id为正确格式，日期为8位数字构成，yearmtdy，且必须在同一年月
 * 输入一个变量后后回车，未做输入检查，程序处理默认第一天为周一，只统计工作日
 * 计算需要文件输入%workPath%/%ID%/%Date%.txt
 * 输出为该用户目录下SimpleMerge.txt文件，其中每一项按照合成后轨迹拓扑顺序排列
 * 归并算法描述：
 * 		用第一天的轨迹初始化合成轨迹。
 * 		初始化索引x（当前位置）=0为合成轨迹头结点索引，y=1为当前日期轨迹抛开O点后的第一个点索引
 * 		求合成轨迹x+1点与x点距离disA，当前日期轨迹y点与合成轨迹x点距离，距离用经纬度差绝对值和度量
 * 		如果disA<disB,当前位置=x+1;否则，将y点插入到x+1位置，当前位置=x+1;
 * 		直至y到当前日期最后一个非D结点止
 */
public class SimpleMergeSort {
	private static String ID;
	private static BufferedWriter  bw;
	private static String pathS;
	private static LinkedList<SimplePoint> route=new LinkedList<SimplePoint>();
	
	private static void export() throws Exception{
		for (int i=0;i<route.size();i++)
			bw.write(route.get(i).getString()+"\n");
	}
	private static void simpleMerge(int curDate)throws Exception{
		String DateS=String.valueOf(curDate);
		System.out.print("now merging "+DateS);
		File aDayRouteFile = new File(pathS+File.separator+DateS+".txt");
		if (!aDayRouteFile.exists()) throw new Exception(DateS+" aDayRouteFile not Found.");
		BufferedReader br;
		if (aDayRouteFile.length()==0)	return;
		else 	br=new BufferedReader(new FileReader(aDayRouteFile));
		int lenR=route.size();
		if (lenR==0){
			String af;
			String afList[];
			while ((af=br.readLine())!=null){
				afList=af.split(",");
				route.add(new SimplePoint(af,Double.parseDouble(afList[5]),Double.parseDouble(afList[6])));
			}
		}else{
			int index=0;
			String af;
			String afList[];
			while ((af=br.readLine())!=null){
				afList=af.split(",");
				if (afList[7].charAt(0)=='2' || afList[7].charAt(0)=='3') continue;
				double Lon=Double.parseDouble(afList[5]);
				double Lat=Double.parseDouble(afList[6]);
				if (index<lenR-1){
					double disA=Math.abs(route.get(index+1).getLat()-route.get(index).getLat())+Math.abs(route.get(index+1).getLon()-route.get(index).getLon());
					double disB=Math.abs(Lat-route.get(index).getLat())+Math.abs(Lon-route.get(index).getLon());
					if (disA<disB)
						index++;
					else{
						index++;
						route.add(index, new SimplePoint(af,Lon,Lat));
						lenR++;
					}
				}else{
					index++;
					lenR++;
					route.add(new SimplePoint(af,Lon,Lat));
				}
			}
		}
		br.close();
	}
	
	public static void main(String[] args) {
		try{
			Config.init();
			Scanner sc=new Scanner(System.in);
			System.out.print("input ID:");
			ID=sc.next();
//			System.out.print("Begin Date:");
//			int beginDate=sc.nextInt();
//			System.out.print("End Date:");
//			int endDate=sc.nextInt();
			int beginDate=20141103;//debug
			int endDate=20141128;//debug
			int curDate=beginDate;

			
			int Daten=0;//天数

			pathS=Config.getAttr(Config.WorkPath)+File.separator+ID;
			File userPath=new File(pathS);
			if (!userPath.exists()) throw new Exception();
			bw=new BufferedWriter(new FileWriter(pathS+File.separator+"simpleMerge.txt"));
			while (curDate<=endDate){
				try{
					simpleMerge(curDate);
				}catch(Exception e){
					e.printStackTrace();
				}
					Daten++;
					if (Daten%5==0) curDate+=3;//如果是星期五，下一天往后计算两天至周一
					else 							 curDate+=1;//否则下一天就是第二天
			}
			export();
			bw.flush();
			bw.close();
			System.out.println("the routes of user "+ID+" in the period are merged.");
			System.out.println("finished.");
	}catch (Exception e){
			e.printStackTrace();
	}

	}

}
