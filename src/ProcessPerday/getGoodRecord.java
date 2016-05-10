package ProcessPerday;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

import Config.Config;

/*
 * author:youg
 * date:20160229
 * 提取质量好的用户用于进一步分析
 * 0raw:原始信令数据，不包含经纬度信息，按时间划分文件
 * 1withPos:添加经纬度信息，按id后两位划分文件，文件内按id和时间排序
 * 2timeSpan:记录每个ID每天最早出现的时间和位置以及最晚出现的时间和位置
 * 3timeLine：以15分钟为单位统计每个ID在每个时间段出现的次数
 * 4goodUser:数据质量好、用于下一步分析的用户ID列表。提取规则：7点前、19点后有记录，7-19点每3个小时有记录的用户数所占比例；用户比例：55%
 * 5goodRecord:4goodUser列表里的用户的完整记录，按id后两位分割到不同文件中
 */
public class getGoodRecord {
	//public static String workPath = "F:\\SDunicom\\";
	//public static String date = "20151117";
	//public static String withPosPathName = workPath+date+"\\1fixed\\";
	//public static String timeSpanPathName = workPath+date+"\\2timeSpan\\";
	//public static String timeLinePathName = workPath+date+"\\3timeLine\\";
	//public static String goodUserPathName = workPath+date+"\\4goodUser\\";
	//public static String goodRecordPathName = workPath+date+"\\5goodRecord\\";
	
	public static BufferedReader br;
	public static BufferedWriter bw;
	public static Set<String> goodUser = new HashSet<String>();//存放好样本ID，由readGoodUserList读入
	/*
	 * 读入好用户ID，存入goodUser
	 */
	public static void readGoodUserList(String goodUserFileName)throws Exception{
		System.out.println("Now readGoodUserList");
		br = new BufferedReader(new FileReader(goodUserFileName));
		String af;
		while((af=br.readLine())!=null)
			goodUser.add(af);
		br.close();
	}
	/*
	 * 从1withPos中提取goodUser的Record存入5goodRecord中
	 */
	public static void selectGoodRecord(File[] files)throws Exception{
		String af;
		String subAf;
		for(File file:files){
			System.out.println("Now selecting good record from:"+file.getAbsolutePath());
			br = new BufferedReader(new FileReader(file));
			bw = new BufferedWriter(new FileWriter(Config.getAttr(Config.GoodRecordPath)+File.separator+file.getName()));
			while((af=br.readLine())!=null){
				subAf = af.substring(0,18);
				if(goodUser.contains(subAf))
					bw.write(af+"\n");
			}
			br.close();
			bw.close();
		}
	}
	public static void main(String[] args)throws Exception{
		Config.init();
		String goodUserFileName = Config.getAttr(Config.GoodUserPath)+"goodUser.txt";
		readGoodUserList(goodUserFileName);
		File withPosPath = new File(Config.getAttr(Config.FixedPath));
		File[] withPosFiles = withPosPath.listFiles();
		selectGoodRecord(withPosFiles);
		System.out.println("finish");
	}
}
