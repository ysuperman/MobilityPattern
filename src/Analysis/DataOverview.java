package Analysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Config.Config;

public class DataOverview {
	public static BufferedReader br;
	public static BufferedWriter bw;
	public static DecimalFormat df = new DecimalFormat("#0.000000");
	public static double maxLon,maxLat,minLon,minLat;
	public static int idLength;
	
	public static Map<String,Double> map;
	public static int[] rcdNum;
	public static int[] updNum;
	public static int[] perhNum;
	public static int[] userNum;
	//原始信令数据在地图上的分布情况
	public static void heatMap(File fixedPath)throws Exception{
		map = new HashMap<String,Double>();
		File[] fixedFiles = fixedPath.listFiles();
		double maxValue=0.0;
		for(File file:fixedFiles){
			System.out.println("Now heatMaping "+file.getAbsolutePath());
			br = new BufferedReader(new FileReader(file));
			String af;
			String[] afs;
			while((af=br.readLine())!=null){
				afs = af.split(",");
				double lon = Double.valueOf(afs[5]);
				double lat = Double.valueOf(afs[6]);
				if(lon<minLon || lon>maxLon || lat<minLat || lat>maxLat)
					continue;
				lon = ((int)(lon*1000000)/10000+0.5)/100.0;
				lat = ((int)(lat*1000000)/10000+0.5)/100.0;
				String pos = df.format(lon)+","+df.format(lat);
				if(map.containsKey(pos)){
					double val = map.get(pos)+1.0;
					map.put(pos, val);
					if(val>maxValue)
						maxValue=val;
				}
				else
					map.put(pos, 1.0);
			}
			br.close();
			break;
		}
		maxValue/=10;
		for(String key:map.keySet()){
			double val = map.get(key);
			if(val>maxValue-0.0000001)
				val=1.0;
			else
				val/=maxValue;
			map.put(key, val);
		}
	}
	//用户记录数分布[0,5),[5,10),[10,15),[15,20),[20,25),[25,30),[30,35),[35,40),[40,45),[45,50),[50,)
	public static void recordNum(File fixedPath)throws Exception{
		rcdNum = new int[11];
		File[] fixedFiles = fixedPath.listFiles();
		for(File file:fixedFiles){
			System.out.println("Now recordNuming "+file.getAbsolutePath());
			br = new BufferedReader(new FileReader(file));
			String af,lastAf=null;
			int value=0;
			while((af=br.readLine())!=null){
				af = af.substring(0,idLength);
				if(lastAf!=null && !af.equals(lastAf)){
					value/=5;
					if(value<11)
						rcdNum[value]+=1;
					//else
					//	rcdNum[20]+=1;
					value=0;
				}
				value+=1;
				lastAf=af;
			}
			br.close();
			//break;
		}
		for(int i=0;i<rcdNum.length;i++){
			System.out.println(i+":"+rcdNum[i]);
		}
	}
	//更新周期分布(0,10),(10,30),(30,60),(60,120),(120,)
	public static void updatePeriod(File fixedPath)throws Exception{
		updNum = new int[5];
		File[] fixedFiles = fixedPath.listFiles();
		for(File file:fixedFiles){
			System.out.println("Now updatePerioding "+file.getAbsolutePath());
			br = new BufferedReader(new FileReader(file));
			String af,lastAf=null;
			String[] afs;
			String time,lastTime=null;
			int value=0;
			while((af=br.readLine())!=null){
				afs = af.split(",");
				af = afs[0];
				time = afs[2];
				if(af.equals(lastAf)){
					int t = Integer.valueOf(time.substring(0,2))*60+Integer.valueOf(time.substring(2,4));
					int lt = Integer.valueOf(lastTime.substring(0,2))*60+Integer.valueOf(lastTime.substring(2,4));
					value = lt-t;
					if(value<0)
						continue;
					else if(value<=10)
						updNum[0]+=1;
					else if(value<=30)
						updNum[1]+=1;
					else if(value<=60)
						updNum[2]+=1;
					else if(value<=120)
						updNum[3]+=1;
					else
						updNum[4]+=1;
				}
				lastTime = time;
				lastAf=af;
			}
			br.close();
			break;
		}
	}
	//每小时信令数量分布
	public static void numPerhour(File fixedPath)throws Exception{
		perhNum = new int[24];
		File[] fixedFiles = fixedPath.listFiles();
		for(File file:fixedFiles){
			System.out.println("Now numPerhouring "+file.getAbsolutePath());
			br = new BufferedReader(new FileReader(file));
			String af;
			String[] afs;
			while((af=br.readLine())!=null){
				afs = af.split(",");
				int h = Integer.valueOf(afs[2].substring(0,2));
				if(h>23 ||  h<0){
					System.out.println(h);
					continue;
				}
				perhNum[h]+=1;
			}
			br.close();
			break;
		}
	}
	//每小时用户数量分布
	public static void userPerhour(File fixedPath)throws Exception{
		userNum = new int[24];
		Set<String> idSet;
		for(int i=0;i<24;i++){
			System.out.println("Now userPerhouring "+i);
			idSet = new HashSet<String>();
			File[] fixedFiles = fixedPath.listFiles();
			for(File file:fixedFiles){
				br = new BufferedReader(new FileReader(file));
				String af;
				String[] afs;
				while((af=br.readLine())!=null){
					afs = af.split(",");
					if(idSet.contains(afs[0]))
						continue;
					if(Integer.valueOf(afs[2].substring(0,2))!=i)
						continue;
					idSet.add(afs[0]);
				}
				br.close();
				break;
			}
			userNum[i]=idSet.size();
		}
	}
	//生成json文件
	public static void generateJson(String fileName)throws Exception{
		System.out.println("now generateJsoning "+fileName);
		bw = new BufferedWriter(new FileWriter(fileName));
		bw.write("var geodata = [\n");
		for(String pos:map.keySet())
			bw.write("["+pos+"],\n");
		bw.write("];\n");
		
		bw.write("var heatdate = [\n");
		for(String pos:map.keySet())
			bw.write("["+pos+","+df.format(map.get(pos))+"],\n");
		bw.write("];\n");
		
		bw.write("var count_legend_data = ['0~5','5~10','10~15','15~20','20~25','25~30','30~35','35~40','40~45','45~50','50~'];\n");
		bw.write("var count_data = [\n");
		bw.write("{value:"+rcdNum[0]+",name:'0~5'},\n");
		bw.write("{value:"+rcdNum[1]+",name:'5~10'},\n");
		bw.write("{value:"+rcdNum[2]+",name:'10~15'},\n");
		bw.write("{value:"+rcdNum[3]+",name:'15~20'},\n");
		bw.write("{value:"+rcdNum[4]+",name:'20~25'},\n");
		bw.write("{value:"+rcdNum[5]+",name:'25~30'},\n");
		bw.write("{value:"+rcdNum[6]+",name:'30~35'},\n");
		bw.write("{value:"+rcdNum[7]+",name:'35~40'},\n");
		bw.write("{value:"+rcdNum[8]+",name:'40~45'},\n");
		bw.write("{value:"+rcdNum[9]+",name:'45~50'},\n");
		bw.write("{value:"+rcdNum[10]+",name:'50~'},\n");
		bw.write("];\n");
		
		bw.write("var update_legend_data = ['0~10','10~30','30~60','60~120','120~'];\n");
		bw.write("var update_data = [\n");
		bw.write("{value:"+updNum[0]+",name:'0~10'},\n");
		bw.write("{value:"+updNum[0]+",name:'10~30'},\n");
		bw.write("{value:"+updNum[0]+",name:'30~60'},\n");
		bw.write("{value:"+updNum[0]+",name:'60~120'},\n");
		bw.write("{value:"+updNum[0]+",name:'120~'},\n");
		bw.write("];\n");
		
		bw.write("var line_data = [");
		for(int i=0;i<24;i++)
			bw.write(perhNum[i]+",");
		bw.write("];\n");
		
		bw.write("var line_data2 = [");
		for(int i=0;i<24;i++)
			bw.write(userNum[i]+",");
		bw.write("];\n");
		
		bw.close();
	}
	public static void main(String[] args)throws Exception{
		Config.init();
		maxLon = Double.valueOf(Config.getAttr(Config.CityMaxLon));
		minLon = Double.valueOf(Config.getAttr(Config.CityMinLon));
		maxLat = Double.valueOf(Config.getAttr(Config.CityMaxLat));
		minLat = Double.valueOf(Config.getAttr(Config.CityMinLat));
		idLength = Integer.valueOf(Config.getAttr(Config.IdLength));
		//heatMap(new File(Config.getAttr(Config.FixedPath)));
		recordNum(new File(Config.getAttr(Config.FixedPath)));
		//updatePeriod(new File(Config.getAttr(Config.FixedPath)));
		//numPerhour(new File(Config.getAttr(Config.FixedPath)));
		//userPerhour(new File(Config.getAttr(Config.FixedPath)));
		//generateJson("F:\\sample\\phone_heat_data_"+Config.getAttr(Config.Date)+".json");
		System.out.println("finish");
	}
}
