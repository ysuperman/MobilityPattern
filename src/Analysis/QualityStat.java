package Analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 * author:youg
 * date:20160229
 * 分析用户时间跨度timeSpan和时间覆盖情况timeLine，提取高质量用户保存到goodUser中
 * 0raw:原始信令数据，不包含经纬度信息，按时间划分文件
 * 1withPos:添加经纬度信息，按id后两位划分文件，文件内按id和时间排序
 * 2timeSpan:记录每个ID每天最早出现的时间和位置以及最晚出现的时间和位置
 * 3timeLine：以15分钟为单位统计每个ID在每个时间段出现的次数
 * 4goodUser:数据质量好、用于下一步分析的用户ID列表。提取规则：7点前、19点后有记录，7-19点每3个小时有记录的用户数所占比例；用户比例：55%
 */
public class QualityStat {
	public static String workPath = "F:\\BJmobile\\";
	public static String date = "20130228";
	public static String inputPathName = workPath+date+"\\1withPos\\";
	public static String timeSpanPathName = workPath+date+"\\2timeSpan\\";
	public static String timeLinePathName = workPath+date+"\\3timeLine\\";
	public static String goodUserPathName = workPath+date+"\\4goodUser\\";
	
	public static BufferedReader br;
	public static BufferedWriter bw;
	public static int total = 0,useful=0;
	/*
	 * 统计用户数量和用户平均记录数量
	 */
	public static void userNumber(File[] files)throws Exception{
		int userCount=0,recordCount=0;
		for(File file:files){
			System.out.println("Now counting userNumber with "+file.getAbsolutePath());
			br = new BufferedReader(new FileReader(file));
			String af,last=null;
			while((af=br.readLine())!=null){
				recordCount+=1;
				af=af.substring(0,19);
				if(!af.equals(last))
					userCount+=1;
				last=af;
			}
			br.close();
		}
		System.out.println("User Number: "+String.valueOf(userCount));
		System.out.println("平均用户记录数: "+String.valueOf(recordCount*1.0/userCount));
	}
	/*
	 * 统计平均更新周期
	 */
	public static void statTime(File[] files)throws Exception{
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd,HHmmss");
		Date lastTime= new Date(0);
		Date thisTime= new Date(0);
		String thisUser,lastUser=null;
		double timeCount=0;
		int recordCount=0;
		for(File file:files){
			System.out.println("Now stating time with "+file.getAbsolutePath());
			br = new BufferedReader(new FileReader(file));
			String af;
			while((af=br.readLine())!=null){
				thisUser=af.substring(0,19);
				if(!thisUser.equals(lastUser)){
					lastTime = format.parse(af.substring(20,35));
					lastUser = thisUser;
					continue;
				}
				thisTime = format.parse(af.substring(20,35));
				timeCount+= (thisTime.getTime()-lastTime.getTime())/1000.0/60.0;//分钟
				recordCount+=1;
				lastTime = new Date(thisTime.getTime());
				lastUser = thisUser;
			}
			br.close();
		}
		System.out.println("平均更新周期: "+String.valueOf(timeCount/recordCount));
	}
	/*
	 * 统计用户平均时间跨度、时间跨度分布、时间跨度超过12小时的用户占总用户比例
	 */
	public static void statDelay(File[] files)throws Exception{
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd,HHmmss");
		Map<Integer,Integer> delay = new HashMap<Integer,Integer>();
		Date firstTime= new Date(0);
		Date lastTime= new Date(0);
		String thisUser,lastUser=null;
		delay.put(0, 0);
		delay.put(1, 0);
		delay.put(2, 0);
		delay.put(3, 0);
		delay.put(4, 0);
		delay.put(5, 0);
		delay.put(6, 0);
		for(File file:files){
			System.out.println("Now stating delay with "+file.getAbsolutePath());
			br = new BufferedReader(new FileReader(file));
			String af;
			while((af=br.readLine())!=null){
				thisUser = af.substring(0,19);
				if(!thisUser.equals(lastUser)){
					if(lastUser!=null){
						int t = (int)((lastTime.getTime()-firstTime.getTime())/1000.0/60.0/60.0);
						t=t/4;
						if(t>6)
							t=6;
						if(t<0)
							t=0;
						delay.put(t, delay.get(t)+1);
					}
					firstTime = format.parse(af.substring(20,35));
					lastTime = format.parse(af.substring(20,35));
					lastUser = thisUser;
					continue;
				}
				lastTime = format.parse(af.substring(20,35));
				lastUser = thisUser;
			}
			br.close();
		}
		for(Integer i:delay.keySet()){
			System.out.println(String.valueOf(i)+":"+String.valueOf(delay.get(i)));
		}
	}
	/*
	 *统计每个用户的时间跨度，保存到\\2timeSpan路径下 
	 */
	public static void createTimeSpan(File[] files)throws Exception{
		String firstTime=null,lastTime=null;
		String firstLon=null,lastLon=null,firstLat=null,lastLat=null;
		String thisUser=null,lastUser=null;
		for(File file:files){
			System.out.println("Now creating time span with "+file.getAbsolutePath());
			String name = file.getName();
			String outputFileName = timeSpanPathName+name;
			bw = new BufferedWriter(new FileWriter(outputFileName));
			br = new BufferedReader(new FileReader(file));
			String af;
			String[] afList;
			while((af=br.readLine())!=null){
				afList = af.split(",");
				thisUser = af.substring(0,19);
				if(!thisUser.equals(lastUser)){
					if(lastUser!=null){
						//存入文件
						bw.write(lastUser+","+firstTime+","+lastTime+","+firstLon+","+firstLat+","+lastLon+","+lastLat+"\n");
					}
					firstTime = String.format("%.1f", (Double.valueOf(afList[2].substring(0,2))+Double.valueOf(afList[2].substring(2,4))/60.0));
					firstLon = afList[5];
					firstLat = afList[6];
					lastTime = firstTime;
					lastLon = firstLon;
					lastLat = firstLat;
					lastUser = thisUser;
					continue;
				}
				lastTime = String.format("%.1f", (Double.valueOf(afList[2].substring(0,2))+Double.valueOf(afList[2].substring(2,4))/60.0));
				lastLon = afList[5];
				lastLat = afList[6];
				lastUser = thisUser;
			}
			br.close();
			bw.close();
		}
	}
	/*
	 * 统计每个用户的时间轴覆盖，保存到\\3timeLine路径下
	 */
	public static void createTimeLine(File[] files)throws Exception{
		int[] timeLine= new int[96];
		String thisUser,lastUser=null;
		for(File file:files){
			System.out.println("Now creating time line with "+file.getAbsolutePath());
			String name = file.getName();
			String outputFileName = timeLinePathName+name;
			br = new BufferedReader(new FileReader(file));
			bw = new BufferedWriter(new FileWriter(outputFileName));
			String af;
			String[] afList;
			int timeStamp;
			while((af=br.readLine())!=null){
				afList = af.split(",");
				thisUser = afList[0];
				if(!thisUser.equals(lastUser)){
					if(lastUser!=null){
						bw.write(lastUser);
						for(int i=0;i<timeLine.length;i++){
							bw.write(","+String.valueOf(timeLine[i]));
							timeLine[i]=0;
						}
						bw.write("\n");
					}
					timeStamp=Integer.valueOf(afList[2].substring(0,2))*60+Integer.valueOf(afList[2].substring(2,4));
					timeLine[timeStamp/15]+=1;
					lastUser=thisUser;
					continue;
				}
				timeStamp=Integer.valueOf(afList[2].substring(0,2))*60+Integer.valueOf(afList[2].substring(2,4));
				timeLine[timeStamp/15]+=1;
			}
			br.close();
			bw.close();
		}
	}
	/*
	 * 统计7点前、7-19点、19点之后都有记录的用户数所占比例
	 */
	public static void statTimeLine_1(File[] files)throws Exception{
		int totalUser=0;
		int rightUser=0;
		for(File file:files){
			System.out.println("Now stating time line 1 with "+file.getAbsolutePath());
			br = new BufferedReader(new FileReader(file));
			String af;
			String[] afList;
			while((af=br.readLine())!=null){
				totalUser+=1;
				afList = af.split(",");
				boolean ok1=false,ok2=false,ok3=false;
				for(int i=1;i<=28;i++)
					if(!afList[i].equals("0")){
						ok1=true;
						break;
					}
				for(int i=29;i<=76;i++)
					if(!afList[i].equals("0")){
						ok2=true;
						break;
					}
				for(int i=77;i<=96;i++)
					if(!afList[i].equals("0")){
						ok3=true;
						break;
					}
				if(ok1 && ok2 && ok3)
					rightUser+=1;
			}
			br.close();
		}
		System.out.println("good User Ratio: "+String.format("%.6f",rightUser*1.0/totalUser));
	}
	/*
	 * 统计7点前、19点后有记录，7-19点每个小时有记录的用户数所占比例
	 */
	public static void statTimeLine_2(File[] files)throws Exception{
		int totalUser=0;
		int rightUser=0;
		for(File file:files){
			System.out.println("Now stating time line 2 with "+file.getAbsolutePath());
			br = new BufferedReader(new FileReader(file));
			String af;
			String[] afList;
			while((af=br.readLine())!=null){
				totalUser+=1;
				afList = af.split(",");
				boolean ok1=false,ok2=true,ok3=false;
				for(int i=1;i<=28;i++)
					if(!afList[i].equals("0")){
						ok1=true;
						break;
					}
				for(int i=29;i<=76;i=i+4)
					if(afList[i].equals("0") && afList[i+1].equals("0") && afList[i+2].equals("0") && afList[i+3].equals("0")){
						ok2=false;
						break;
					}
				for(int i=77;i<=96;i++)
					if(!afList[i].equals("0")){
						ok3=true;
						break;
					}
				if(ok1 && ok2 && ok3){
					rightUser+=1;
					System.out.println(afList[0]);
				}
			}
			br.close();
		}
		System.out.println("good User Ratio: "+String.format("%.6f",rightUser*1.0/totalUser));
	}
	/*
	 * 统计7点前、19点后有记录，7-19点每3个小时有记录的用户数所占比例
	 */
	public static void statTimeLine_3(File[] files)throws Exception{
		int totalUser=0;
		int rightUser=0;
		String goodUserFileName = goodUserPathName+"goodUser.txt";
		bw = new BufferedWriter(new FileWriter(goodUserFileName));
		for(File file:files){
			System.out.println("Now stating time line 3 with "+file.getAbsolutePath());
			br = new BufferedReader(new FileReader(file));
			String af;
			String[] afList;
			while((af=br.readLine())!=null){
				totalUser+=1;
				afList = af.split(",");
				boolean ok1=false,ok2=true,ok3=false;
				for(int i=1;i<=28;i++)
					if(!afList[i].equals("0")){
						ok1=true;
						break;
					}
				for(int i=29;i<=76;i=i+12)
					if(afList[i].equals("0") && afList[i+1].equals("0") && afList[i+2].equals("0") && afList[i+3].equals("0")
							&& afList[i+4].equals("0") && afList[i+5].equals("0") && afList[i+6].equals("0") && afList[i+7].equals("0")
							&& afList[i+8].equals("0") && afList[i+9].equals("0") && afList[i+10].equals("0") && afList[i+11].equals("0")){
						ok2=false;
						break;
					}
				for(int i=77;i<=96;i++)
					if(!afList[i].equals("0")){
						ok3=true;
						break;
					}
				if(ok1 && ok2 && ok3){
					rightUser+=1;
					//System.out.println(afList[0]);
					bw.write(afList[0]+"\n");
				}
			}
			br.close();
		}
		bw.close();
		System.out.println("good User Ratio: "+String.format("%.6f",rightUser*1.0/totalUser));
	}
	public static void main(String[] args)throws Exception{
		/*
		 * ****************************************************
		 */
		File inputPath = new File(inputPathName);
		File[] inputFiles = inputPath.listFiles();
		//userNumber(inputFiles);
		//statTime(inputFiles);
		//statDelay(inputFiles);
		createTimeSpan(inputFiles);
		/*
		 * ****************************************************
		 */
		createTimeLine(inputFiles);
		File timeLinePath = new File(timeLinePathName);
		File[] timeLineFiles = timeLinePath.listFiles();
		//statTimeLine_1(timeLineFiles);
		//statTimeLine_2(timeLineFiles);
		statTimeLine_3(timeLineFiles);
		/*
		 * ****************************************************
		 */
		System.out.println("finish");
	}
}
