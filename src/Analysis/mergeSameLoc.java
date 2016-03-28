package Analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/*
 * author:youg
 * date:20160312
 * 把sample\\selectWithPos（格式同1withPose）文件夹中的文件中连续出现在某个位置的点合并，标记起止时间，保存到6mergeSameLoc文件夹下
 * 0raw:原始信令数据，不包含经纬度信息，按时间划分文件
 * 1withPos:添加经纬度信息，按id后两位划分文件，文件内按id和时间排序
 * 2timeSpan:记录每个ID每天最早出现的时间和位置以及最晚出现的时间和位置
 * 3timeLine：以15分钟为单位统计每个ID在每个时间段出现的次数
 * 1splitDateColumn：暂存修改字段的1withPos文件
 * 6mergeSample：合并连续出现在某个位置的点，时间字段表示起止时间
 */
public class mergeSameLoc {
	public static String basePath = "F:\\basemap\\BJbase_cellidcn.txt";
	public static String workPath = "F:\\BJmobile\\";
	public static String date = "20130228";
	public static String rawPathName = workPath+date+"\\0raw\\";
	public static String withPosPathName = workPath+date+"\\1withPos\\";
	public static String goodRecordPathName = workPath+date+"\\5goodRecord\\";
	public static String mergeSameLocPathName = workPath+date+"\\6mergeSameLoc\\";
	
	public static BufferedReader br;
	public static BufferedWriter bw;
	
	public static void merge(File inputfile)throws Exception{
		System.out.println("Now merging with "+inputfile.getAbsolutePath());
		String outputfileName = mergeSameLocPathName+inputfile.getName();
		br = new BufferedReader(new FileReader(inputfile));
		bw = new BufferedWriter(new FileWriter(outputfileName));
		String thisLabel,lastLabel=null;
		String startTime=null,endTime=null;
		String thisAf,lastAf=null;
		String[] afList;
		while((thisAf=br.readLine())!=null){
			afList = thisAf.split(",");
			thisLabel = afList[0]+","+afList[3]+","+afList[4];
			if(!thisLabel.equals(lastLabel)){
				if(lastLabel!=null)
					bw.write(lastAf.substring(0,29)+startTime+"-"+endTime+lastAf.substring(35)+"\n");
				startTime = afList[2];
			}
			endTime = afList[2];
			lastLabel = thisLabel;
			lastAf = thisAf;
		}
		bw.write(lastAf.substring(0,29)+startTime+"-"+endTime+lastAf.substring(35)+"\n");
		br.close();
		bw.close();
	}
	public static void main(String[] args)throws Exception{
		File goodRecordPath = new File(goodRecordPathName);
		File[] files = goodRecordPath.listFiles();
		for(File file:files){
			merge(file);
		}
		System.out.println("finish");
	}
}
