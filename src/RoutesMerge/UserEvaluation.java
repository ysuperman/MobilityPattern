package RoutesMerge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import Config.Config;

public class UserEvaluation {
	private static final int DATEN=20;
	private static final int ULEVN=4;
	private static final int ALEVN=4;
	private static HashMap<String,UserEvaluation> UserList;
	private static int alevel[][]=new int[ALEVN][ULEVN] ;//特定记录数区间用户统计分布
	private static BufferedWriter bw;
	
	private String ID;
	private int frequency[]=new int[DATEN];//??
	private int ulevel[]=new int[ULEVN];           //单用户每天记录数分布统计（0~10；10~20；20~30；30+）
	
	public UserEvaluation(String ID){
		this.ID=ID;
		//frequency=new int[DATEN];
		
		for (int i=0;i<DATEN;i++)	frequency[i]=0;
		for (int i=0;i<ULEVN;i++) ulevel[i]=0;
	}
	
	public void acc(int Daten){
		frequency[Daten]++;
	}
	
	public void layer(){
		for (int i=0;i<DATEN;i++)
			if (frequency[i]/10>=ULEVN)
				ulevel[ULEVN-1]++;
			else 
				ulevel[frequency[i]/10]++;
		
	}
	public static void count(){
		for (Map.Entry<String,UserEvaluation> entry: UserList.entrySet() )
			entry.getValue().layer();
		for (int i=0;i<ALEVN;i++) 
		for (int j=0;j<ULEVN;j++) alevel[i][j]=0;
		for (int i=0;i<ULEVN;i++)
			for (Map.Entry<String,UserEvaluation> entry: UserList.entrySet() ){
				int tmp=entry.getValue().ulevel[i]/5;
				if (tmp>=ALEVN)
					alevel[ALEVN-1][i]++;
				else
					alevel[tmp][i]++;
			}
		for (int i=0;i<ALEVN;i++){//天数区间
			for (int j=0;j<ULEVN;j++)//记录数区间
				System.out.print(alevel[i][j]+"\t");
			System.out.println();
		}
	}
	public static  void Handle(String DateS,int Daten) throws Exception{
		System.out.println("now evaluating"+DateS);
		Config.setDay(DateS);
		File ODRecordFile = new File(Config.getAttr(Config.ODRecordPath)+File.separator+"OutAll.txt");
		if (!ODRecordFile.exists()) throw new Exception(DateS+" ODRecordFile not Found.");
		BufferedReader br=new BufferedReader(new FileReader(ODRecordFile));
		String af;
		while ((af=br.readLine())!=null){
			String uid=af.substring(0, Integer.parseInt(Config.getAttr(Config.IdLength)));
			if (UserList.get(uid)==null) 
					UserList.put(uid, new UserEvaluation(uid));
			UserList.get(uid).acc(Daten);
		}
		br.close();
	}
	
	public static void  AllUserEvaluation(){
		try{
			Config.init();
			bw=new BufferedWriter(new FileWriter(Config.getAttr(Config.WorkPath)+File.separator+"AllUserEvaluation.txt"));
//			System.out.print("Begin Date:");
//			int beginDate=sc.nextInt();
//			System.out.print("End Date:");
//			int endDate=sc.nextInt();
			int beginDate=20141103;//debug
			int endDate=20141128;//debug
			int curDate=beginDate;

			UserList=new HashMap<String,UserEvaluation>();
			int Daten=0;//天数
			while (curDate<=endDate){
					Handle(String.valueOf(curDate),Daten);
					Daten++;
					if (Daten%5==0) curDate+=3;//如果是星期五，下一天往后计算两天至周一
					else 							 curDate+=1;//否则下一天就是第二天
			}
			count();
			bw.flush();
			bw.close();
			System.out.println("finished.");
	}catch (Exception e){
			e.printStackTrace();
	}
	}
	public static void main(String[] args) {
		AllUserEvaluation();
	}
}
