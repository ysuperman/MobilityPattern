package RoutesMerge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;
import Config.Config;

/*
 * author:Yuan
 * date:2016/07/24
 * 
*/


public class getUserAllDay {
	private static String ID;
	private static BufferedWriter  bw;
	public static int extract(int curDate) throws Exception{
		//将curDate这一天的OD数据中属于用户ID的提取出来
			String DateS=String.valueOf(curDate);
			System.out.print("now extracting "+DateS);
			Config.setDay(DateS);
			File ODRecordFile = new File(Config.getAttr(Config.ODRecordPath)+File.separator+"OutAll.txt");
			if (!ODRecordFile.exists()) throw new Exception(DateS+" ODRecordFile not Found.");
			
			int n=0;//当天记录数
			//某天OD记录输入流
			BufferedReader br=new BufferedReader(new FileReader(ODRecordFile));
			//该id某天的记录输出流
			BufferedWriter dbw=new BufferedWriter(new FileWriter(Config.getAttr(Config.WorkPath)+File.separator+ID+File.separator+DateS+".txt"));
			String af;
			while ((af=br.readLine())!=null){
				String uid=af.substring(0, Integer.parseInt(Config.getAttr(Config.IdLength)));
				if (uid.equals(ID)){
					n++;
					bw.write(af+"\n");
					dbw.write(af+"\n");
				}
			}
			br.close();
			dbw.flush();
			dbw.close();
			System.out.println(": "+n+"records.");
		//	bw.write("--\n"+DateS);//多天之间分割的标志，后续可以处理根据此标志快速判断相邻两条记录是否在同一天。
			return n;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		/*本程序将输入id在输入起始日期和终止日期之间内所有OD记录输出至%WorkPath%\%id%\AllDay.txt;
		 * 同时将该id每天的记录输出至%WorkPath%\%id%\%DateS%.txt
		*输入要求：id为正确格式，日期为8位数字构成，yearmtdy，且必须在同一年月，
		*输入一个变量后后回车，未做输入检查，程序处理默认第一天为周一，只统计工作日
		*/
		try{
				Config.init();
				Scanner sc=new Scanner(System.in);
				System.out.print("input ID:");
				ID=sc.next();
//				System.out.print("Begin Date:");
//				int beginDate=sc.nextInt();
//				System.out.print("End Date:");
//				int endDate=sc.nextInt();
				int beginDate=20141103;//debug
				int endDate=20141128;//debug
				int curDate=beginDate;

				
				int Daten=0;//天数
				int recordCount=0;//总记录数
				
				File userPath=new File(Config.getAttr(Config.WorkPath)+File.separator+ID);
				if (!userPath.exists()) userPath.mkdirs();
				bw=new BufferedWriter(new FileWriter(Config.getAttr(Config.WorkPath)+File.separator+ID+File.separator+"AllDay.txt"));
				while (curDate<=endDate){
						recordCount+=extract(curDate);
						Daten++;
						if (Daten%5==0) curDate+=3;//如果是星期五，下一天往后计算两天至周一
						else 							 curDate+=1;//否则下一天就是第二天
				}
				bw.flush();
				bw.close();
				System.out.println("User "+ID+" has "+recordCount+" records in the period.");
				System.out.println("finished.");
		}catch (Exception e){
				e.printStackTrace();
		}

	}

}
