package Analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import Config.Config;



/*
 * author:youg
 * date:20160411
 * 分析停留点出现在指定区域内的用户
 * 0raw:原始信令数据，不包含经纬度信息，按时间划分文件
 * 1withPos:添加经纬度信息，按id后两位划分文件，文件内按id和时间排序
 * 2timeSpan:记录每个ID每天最早出现的时间和位置以及最晚出现的时间和位置
 * 3timeLine：以15分钟为单位统计每个ID在每个时间段出现的次数
 * 4goodUser:数据质量好、用于下一步分析的用户ID列表。提取规则：7点前、19点后有记录，7-19点每3个小时有记录的用户数所占比例；用户比例：55%
 * 5goodRecord:4goodUser列表里的用户的完整记录，按id后两位分割到不同文件中
 * 6mergeSameLoc:合并连续出现在某个位置的点，时间字段表示起止时间
 */

public class AreaStayPointAnalysis {
	public static double latMin = 36.605354;//天通苑40.061106;
	public static double latMax = 36.616883;//天通苑40.079664;
	public static double lonMin = 117.021422;//天通苑116.406162;
	public static double lonMax = 117.033403;//天通苑116.442177;
	//public static String basePath = "F:\\basemap\\BJbase_cellidcn.txt";
	//public static String workPath = "F:\\SDunicom\\";
	//public static String date = "20151117";
	//public static String inputPathName = workPath+date+"\\1fixed\\";
	//public static String timeSpanPathName = workPath+date+"\\2timeSpan\\";
	//public static String timeLinePathName = workPath+date+"\\3timeLine\\";
	//public static String goodUserPathName = workPath+date+"\\4goodUser\\";
	//public static String goodRecordPathName = workPath+date+"\\5goodRecord\\";
	//public static String mergeSameLocPathName = workPath+date+"\\6mergeSameLoc\\";
	//public static String stayRecordPathName = workPath+date+"\\7stayRecord\\";
	public static String areaStayPointAnalysis = "F:\\sample\\AreaStayPointJuzhudiSD1\\";
	
	public static BufferedReader br;
	public static BufferedWriter bw;
	public static int total = 0,useful=0;
	public static Set<String> userSet = new HashSet<String>();
	public static HashMap<String,Integer> numMap = new HashMap<String,Integer>();
	public static long[] stat = new long[11];//统计用户停留点分布
		
	/*
	 * 提取指定区域内停留的用户
	 */
	public static void selectUser(File stayRecordFile)throws Exception{
		System.out.println("Now selecting "+stayRecordFile.getAbsolutePath());
		BufferedReader br = new BufferedReader(new FileReader(stayRecordFile));
		String af;
		String[] afs;
		while((af=br.readLine())!=null){
			afs = af.split(",");
			double lon = Double.valueOf(afs[3]);
			double lat = Double.valueOf(afs[4]);
			if(userSet.contains(afs[0]))
				continue;
			if(afs[5].equals("1") && lon<lonMax && lon>lonMin && lat<latMax && lat>latMin){
				String[] times = afs[2].split("-");
				if(timeSpan(times[0],"020000")>0 && timeSpan(times[1],"050000")<0){
					userSet.add(afs[0]);
					numMap.put(afs[0], 0);
				}
			}
		}
		br.close();
	}
	/*
	 * 计算两个时间点之间的时间差，时间格式“HHmmSS”，返回结果单位分钟
	 */
	public static int timeSpan(String a, String b){
		int span = Integer.valueOf(b.substring(0,2))*60+Integer.valueOf(b.substring(2,4));
		span = span - (Integer.valueOf(a.substring(0,2))*60+Integer.valueOf(a.substring(2,4)));
		return span;
	}
	
	public static void statNumber(File stayRecordFile)throws Exception{
		System.out.println("Now stating Number...");
		BufferedReader br = new BufferedReader(new FileReader(stayRecordFile));
		String af;
		String[] afs;
		while((af=br.readLine())!=null){
			afs = af.split(",");
			if(afs[5].equals("1") && userSet.contains(afs[0])){
				numMap.put(afs[0], numMap.get(afs[0])+1);
			}
		}
		br.close();
	}
	/*
	 * 输出选定用户记录到区域停留点分析路径
	 */
	public static void exportRecord(File stayRecordFile)throws Exception{
		System.out.println("Now exporting...");
		BufferedReader br = new BufferedReader(new FileReader(stayRecordFile));
		BufferedWriter bw = new BufferedWriter(new FileWriter(areaStayPointAnalysis+stayRecordFile.getName()));
		String af;
		String[] afs;
		while((af=br.readLine())!=null){
			afs = af.split(",");
			if(afs[5].equals("1") && userSet.contains(afs[0])){
				String[] times = afs[2].split("-");
				if(timeSpan(times[1],"073000")>0)
					continue;
				bw.write(af+"\n");
			}
		}
		br.close();
		bw.close();
	}
	//统计用户停留点数量分布
	public static void stat(){
		for(String user:numMap.keySet()){
			int num = numMap.get(user);
			if(num<10)
				stat[num]+=1;
			else
				stat[10]+=1;
		}
	}
	
	public static void main(String[] args)throws Exception{
		Config.init();
		File stayRecordPath = new File(Config.getAttr(Config.StayRecordPath));
		File[] stayRecordFiles = stayRecordPath.listFiles();
		int i=0;
		for(File file:stayRecordFiles){
			selectUser(file);
			statNumber(file);
			exportRecord(file);
			//if(++i>=10)
			//	break;
		}
		stat();
		System.out.println("finish");
		System.out.println(userSet.size());
		for(i=0;i<=10;i++)
			System.out.println(i+":"+stat[i]);
	}
}
