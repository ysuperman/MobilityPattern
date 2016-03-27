package preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/*
 * author:youg
 * date:20160311
 * 把1withPos的日期时间字段划分为“日期，时间”字段，用4splitDateColumn暂存并覆盖1withPos文件
 * 0raw:原始信令数据，不包含经纬度信息，按时间划分文件
 * 1withPos:添加经纬度信息，按id后两位划分文件，文件内按id和时间排序
 * 2timeSpan:记录每个ID每天最早出现的时间和位置以及最晚出现的时间和位置
 * 3timeLine：以15分钟为单位统计每个ID在每个时间段出现的次数
 * 1splitDateColumn：暂存修改字段的1withPos文件
 */
public class SplitDateColumn {
	public static String basePath = "F:\\basemap\\BJbase_cellidcn.txt";
	public static String workPath = "F:\\BJmobile\\";
	public static String date = "20130226";
	public static String rawPathName = workPath+date+"\\0raw\\";
	public static String withPosPathName = workPath+date+"\\1withPos\\";
	public static String splitDateColumnPathName = workPath+date+"\\1splitDateColumn\\";
	public static BufferedWriter bw;
	public static BufferedReader br;
	public static int total = 0,useful=0;
	/*
	 * 对1withPos文件夹下文件的日期和时间字段分割。
	 */
	public static void splitDateColumn(File withPosFile)throws Exception{
		System.out.println("Now dealing with "+withPosFile.getAbsolutePath());
		br = new BufferedReader(new FileReader(withPosFile));
		String splitDateColumnFileName = splitDateColumnPathName+withPosFile.getName();
		bw = new BufferedWriter(new FileWriter(splitDateColumnFileName));
		String af,date,time;
		String[] afList;
		while((af=br.readLine())!=null){
			afList = af.split(",");
			date = afList[1].substring(0,8);
			time = afList[1].substring(8);
			afList[1] = date+","+time;
			for(int i=0;i<afList.length-1;i++){
				bw.write(afList[i]+",");
			}
			bw.write(afList[afList.length-1]+"\n");
		}
		br.close();
		bw.close();
	}
	public static void main(String[] args)throws Exception{
		File withPosPath = new File(withPosPathName);
		File[] files = withPosPath.listFiles();
		for(File file:files){
			splitDateColumn(file);
		}
		System.out.println("finish");
		
	}
}
