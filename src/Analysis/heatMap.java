package Analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

/*
 * author:youg
 * date:15.12.16
 * 生成热力图所需json格式文件
 * 以0.01精度为单位生成json格式data.json
 * data.json格式：
 * var data = "[{"lng":116.425000,"lat":39.925000,"count":400},...]";
 */
class Point{
	public double lng;
	public double lat;
	public int count;
	public Point(){
	};
	public Point(double lng,double lat,int count){
		this.lng = lng;
		this.lat = lat;
		this.count = count;
	}
}

public class heatMap {
	public static String inputPath;
	public static String outputFile;
	public static BufferedReader inputFile;
	public static FileWriter jsonFile;
	public static Map<String,Integer> pointCount = new HashMap<String,Integer>();
	public static int maxValue;
	/*
	 * 计算两个时间点的时间差b-a，输入格式HHmmSS,输出单位：分钟
	 */
	public static int timeSpan(String a, String b){
		int span = Integer.valueOf(b.substring(0,2))*60+Integer.valueOf(b.substring(2,4));
		span = span - (Integer.valueOf(a.substring(0,2))*60+Integer.valueOf(a.substring(2,4)));
		return span;
	}
	public static void main(String[] args) throws IOException{
		//设置输入文件夹路径
		inputPath = "F:\\BJmobile\\20130226\\7stayRecord\\";
		//设置输出文件路径
		outputFile = "E:\\DataVisual\\amap\\heatMap\\heatmap_data_20130226_work.js";

		String af;
		File filePath = new File(inputPath);
		System.out.println("start");
		//判断是否是文件夹
		if(filePath.isDirectory()){
			File[] fileList = filePath.listFiles();
			for(File file:fileList){
				System.out.println("Now dealing with:"+file.getAbsolutePath());
				inputFile = new BufferedReader(new FileReader(file));
				//af = inputFile.readLine();
				while((af = inputFile.readLine())!=null){
					String[] afList = af.trim().split(",");
					if(afList.length<6)// || afList[3].length()<1 || afList[4].length()<1)
						continue;
					if(afList[5].equals("0"))
						continue;
					String[] times = afList[2].split("-");
					//居住地判断条件
					//if(!(timeSpan(times[1],"080000")>0 || timeSpan(times[0],"200000")<0))
					//	continue;
					//工作地判断条件
					if(timeSpan(times[0],"080000")>0 || timeSpan(times[1],"190000")<0 || timeSpan(times[0],times[1])<240)
						continue;
					double longitude = Double.valueOf(afList[3]).doubleValue();
					double latitude = Double.valueOf(afList[4]).doubleValue();
					longitude = ((int)(longitude*1000000)/10000+0.5)/100.0;
					latitude = ((int)(latitude*1000000)/10000+0.5)/100.0;
					String key = String.valueOf(longitude)+","+String.valueOf(latitude);
					//if(key.equals("117.005,36.655")||key.equals("117.015,36.655")||key.equals("117.025,36.655"))
					//	System.out.println(afList[2]);
					if(pointCount.containsKey(key)){
						pointCount.put(key, pointCount.get(key)+1);
					}else{
						pointCount.put(key,1);
					}
				}
				inputFile.close();
			}
		}
		//point
		//json
		maxValue = 0;
		String jsonString="var data = '[";
		for(String key:pointCount.keySet()){
			String[] keyList = key.trim().split(",");
			Point point = new Point();
			point.lng = Double.valueOf(keyList[0]).doubleValue();
			point.lat = Double.valueOf(keyList[1]).doubleValue();
			point.count = pointCount.get(key);
			if(point.count>maxValue){
				maxValue = point.count;
			}
			jsonString += JSON.toJSONString(point)+",\n";
		}
		jsonString=jsonString.substring(0, jsonString.length()-1)+"]'";
		jsonFile = new FileWriter(outputFile);
		jsonFile.write(jsonString);
		jsonFile.close();
		System.out.println("maxValue="+maxValue);
		System.out.println("Finished!");
	}
}
