package ProcessPerday;

import java.util.Scanner;
import Config.Config;

public class getPerdayAll {

	public static void main(String[] args) {

		try{
		long startTime = 0,endTime=0;
		Scanner sc=new Scanner(System.in);
		String s1=sc.nextLine();
		String s3=s1;
		String s2=sc.nextLine();
		Config.init(s1);
		while (s2.compareTo(s3)>=0)	{
			try{
				startTime=System.currentTimeMillis();
//				getGoodUser.Handle();
				getGoodRecord.Handle();
				getStayRecord.Handle();
				System.out.println("Finished.");
			}catch (Exception e){
						e.printStackTrace();
						System.out.println("An Error occured.");
			}
			finally{				
					System.out.println("日期："+Config.getAttr(Config.Date));
					endTime=System.currentTimeMillis();
					System.out.println("执行时间："+(endTime-startTime)/1000.0f+"s");
					s3=Config.setDay(String.valueOf(Integer.parseInt(s3)+1));
			}
		}//end while
		}catch (Exception e){
			e.printStackTrace();
			System.out.println("Init error.");
		}
	}//end main

}
