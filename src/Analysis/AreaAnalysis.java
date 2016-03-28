package Analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

/*
 * author:youg
 * date:20160229
 * 分析指定时间段出现在指定区域内的用户
 * 0raw:原始信令数据，不包含经纬度信息，按时间划分文件
 * 1withPos:添加经纬度信息，按id后两位划分文件，文件内按id和时间排序
 * 2timeSpan:记录每个ID每天最早出现的时间和位置以及最晚出现的时间和位置
 * 3timeLine：以15分钟为单位统计每个ID在每个时间段出现的次数
 * 4goodUser:数据质量好、用于下一步分析的用户ID列表。提取规则：7点前、19点后有记录，7-19点每3个小时有记录的用户数所占比例；用户比例：55%
 * 5goodRecord:4goodUser列表里的用户的完整记录，按id后两位分割到不同文件中
 * 6mergeSameLoc:合并连续出现在某个位置的点，时间字段表示起止时间
 */
public class AreaAnalysis {
	public static double latMin = 40.061106;
	public static double latMax = 40.079664;
	public static double lonMin = 116.406162;
	public static double lonMax = 116.442177;
	public static String basePath = "F:\\basemap\\BJbase_cellidcn.txt";
	public static String workPath = "F:\\BJmobile\\";
	public static String date = "20130226";
	public static String inputPathName = workPath+date+"\\1withPos\\";
	public static String timeSpanPathName = workPath+date+"\\2timeSpan\\";
	public static String timeLinePathName = workPath+date+"\\3timeLine\\";
	public static String goodUserPathName = workPath+date+"\\4goodUser\\";
	public static String goodRecordPathName = workPath+date+"\\5goodRecord\\";
	public static String mergeSameLocPathName = workPath+date+"\\6mergeSameLoc\\";
	
	public static BufferedReader br;
	public static BufferedWriter bw;
	public static int total = 0,useful=0;
	public static Set<String> baseSet = new HashSet<String>();
	
	/*
	 * 加载经纬度范围内的基站号
	 */
	public static void loadBase(String basePath)throws Exception{
		br = new BufferedReader(new FileReader(basePath));
		String af;
		String[] afList;
		double lon,lat;
		while((af=br.readLine())!=null){
			afList = af.split(",");
			lon = Double.valueOf(afList[3]);
			if(lon<lonMin || lon>lonMax)
				continue;
			lat = Double.valueOf(afList[4]);
			if(lat<latMin || lat>latMax)
				continue;
			baseSet.add(afList[0]);
		}
		br.close();
	}
	/*
	 * 筛选凌晨出现在该区域，且出现停留时间段超过4小时的ID
	 */
	public static void selectAreaUser(File mergeSameLocFile)throws Exception{
		br = new BufferedReader(new FileReader(mergeSameLocFile));
		String af;
		String[] afList;
		double startTime,endTime;
		int lac,cell;
		String base;
		while((af=br.readLine())!=null){
			afList = af.split(",");
			lac = Integer.valueOf(afList[3]);
			cell = Integer.valueOf(afList[4]);
			lac = lac*100000+cell;
			base = String.valueOf(lac);
			if(!baseSet.contains(base))
				continue;
			startTime = Integer.valueOf(afList[2].substring(0,2))*1.0+Integer.valueOf(afList[2].substring(2,4))/60.0;
			endTime = Integer.valueOf(afList[2].substring(7,9))*1.0+Integer.valueOf(afList[2].substring(9,11))/60.0;
			if(endTime-startTime<4.0)
				continue;
			if(!(endTime<8.0 || startTime>18.0))
				continue;
			useful+=1;
			System.out.println(afList[0]);
		}
		br.close();
	}
	
	public static void main(String[] args)throws Exception{
		//加载区域内基站
		loadBase(basePath);
		File mergeSameLocPath = new File(mergeSameLocPathName);
		File[] mergeSameLocFiles = mergeSameLocPath.listFiles();
		total=0;
		useful=0;
		int i=0;
		for(File file:mergeSameLocFiles){
			selectAreaUser(file);
			if(i++==1)
				break;
		}
		System.out.println("finish");
		System.out.println("total:"+String.valueOf(total));
		System.out.println("useful:"+String.valueOf(useful));
	}
}
