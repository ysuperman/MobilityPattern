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
	private static final double MIN_D=0.000001;
	private static void export() throws Exception{
		for (int i=0;i<route.size();i++)
			bw.write(i+","+route.get(i).getString()+"\n");
	}
	
	private static double distance(double Lon1,double Lat1,double Lon2,double Lat2){
			return Math.pow(Math.pow(Lat1-Lat2, 2)+Math.pow(Lon1-Lon2,2),0.5);
	}
	
	private static void simpleMerge(int curDate)throws Exception{
		String DateS=String.valueOf(curDate);
		System.out.println("now merging "+DateS);
		File aDayRouteFile = new File(pathS+File.separator+DateS+".txt");
		if (!aDayRouteFile.exists()) throw new Exception(DateS+" aDayRouteFile not Found.");
		BufferedReader br;
		if (aDayRouteFile.length()==0)	return;
		else 	br=new BufferedReader(new FileReader(aDayRouteFile));
		
		int lenR=route.size();
		String af=null;
		String afList[];
		if (lenR==0){//merge第一天
			double Lon,Lat,dis;
			//把第一天的第一个点加入route中
			af=br.readLine();
			afList=af.split(",");
			Lon=Double.parseDouble(afList[3]);
			Lat=Double.parseDouble(afList[4]);
			route.add(new SimplePoint(af,Lon,Lat));
			//如果第一天后面的点不和前一个点位置相同，则加入route中
			while ((af=br.readLine())!=null){
				afList=af.split(",");
				Lon=Double.parseDouble(afList[3]);
				Lat=Double.parseDouble(afList[4]);
				dis=distance(Lon,Lat,route.getLast().getLon(),route.getLast().getLat());
				if (dis>MIN_D)
					route.add(new SimplePoint(af,Lon,Lat));
			}
		}else{
			int index=0;//当前所在节点索引
			Boolean Next=true;
			double Lon=0,Lat=0;
			//从当前route起点index=0出发，每次循环向前走一步，可能走向当前route的下一个点，也可能走向当天轨迹下一个点，直至走到终点
			while (true){
				if  (Next){
					af=br.readLine();
					if (af==null) break;//当天轨迹已走完（归并完成），退出while循环
					afList=af.split(",");
					if (afList[5].charAt(0)=='2' || afList[5].charAt(0)=='3') continue;
					Lon=Double.parseDouble(afList[3]);
					Lat=Double.parseDouble(afList[4]);//当天轨迹下一个节点位置
				}
				if (index<lenR-1){//当前所在节点不是route轨迹最后一个节点
					double disA=distance(route.get(index+1).getLon(),route.get(index+1).getLat(),route.get(index).getLon(),route.get(index).getLat());
					double disB=distance(Lon,Lat,route.get(index).getLon(),route.get(index).getLat());
					if (disB<MIN_D){//route当前点与当天轨迹当前点重合，则删除当天轨迹当前点
						Next=true;
					}else if(distance(route.get(index+1).getLon(),route.get(index+1).getLat(),Lon,Lat)<MIN_D){
						//route后一点与当天轨迹当前点重合，则走向route后一点
						index++;
						Next=false;
					}else 	if (disA<disB){
						//route后一点与route当前点的距离比当天轨迹当前点与route当前点的距离近，则走向route后一点
						index++;		
						Next=false;
					}else {//route后一点与route当前点的距离比当天轨迹当前点与route当前点的距离远，则走向当天轨迹当前点
						index++;
						route.add(index, new SimplePoint(af,Lon,Lat));
						Next=true;
						lenR++;
					}
				}else{//当前所在节点是route轨迹最后一个节点，则将当天后续点拼接到route最后
					index++;
					lenR++;
					route.add(new SimplePoint(af,Lon,Lat));
					Next=true;
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
