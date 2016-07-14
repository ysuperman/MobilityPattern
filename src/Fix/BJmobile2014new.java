package Fix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

//import Fix.Feature;
import Config.Config;

/*
 * author:yuan
 * date:20160710
 * 北京移动数据预处理
 * 把原始信令数据按id的Hash值后两位分割到不同文件中，并按id和时间字段排序
 * 0raw:原始信令数据，按时间划分文件
 * 1fixed:按id后两位划分文件，文件内按id和时间排序
 */

class FeatureComparator implements Comparator <Feature>{  
    public final int compare(Feature a, Feature b) {  
    	
    	if(a.id>b.id){
			return 1;
		}else if(a.id<b.id){
			return -1;
		}else {
				if(a.time>b.time){
					return 1;
				}else{
					return -1;
				}
		}
    }  
}  


public class BJmobile2014new {
	public static double cityMaxLon;
	public static double cityMaxLat;
	public static double cityMinLon;
	public static double cityMinLat;
	public static String DateS;
	public static int Cnt_records=0;
	public static int Cnt_usl_records=0;
	public static int Cnt_rep_records=0;
	public static int Cnt_usf_records=0;
	public static int Cnt_users=0;
	public static int Cnt_interval_n=0;
	public static long Cnt_interval_t=0;
	public static double Avg_records;
	public static double Avg_interval;
	public static int idLen;
	public static BufferedWriter[] bws;
	public static BufferedReader br;
	public static File[] fixedFiles;
	public static ArrayList<String> FileList;
	public static Feature feature[];
	public static String[] fileNames_36 = {
		"0","1","2","3","4","5","6","7","8","9",
		"a","b","c","d","e","f","g","h","i","j",
		"k","l","m","n","o","p","q","r","s","t",
		"u","v","w","x","y","z"
		};
	public static String[] fileNames_100 = {
		"00","01","02","03","04","05","06","07","08","09",
		"10","11","12","13","14","15","16","17","18","19",
		"20","21","22","23","24","25","26","27","28","29",
		"30","31","32","33","34","35","36","37","38","39",
		"40","41","42","43","44","45","46","47","48","49",
		"50","51","52","53","54","55","56","57","58","59",
		"60","61","62","63","64","65","66","67","68","69",
		"70","71","72","73","74","75","76","77","78","79",
		"80","81","82","83","84","85","86","87","88","89",
		"90","91","92","93","94","95","96","97","98","99",
		};

	/*
	 * 在1fixed文件夹中创建两位尾数命名的txt文件
	 */
	public static void mkDir(String[] fileNames, String fixedPathName)throws Exception{
		File fixedPath = new File(fixedPathName);
		fixedFiles= new File[fileNames.length];
		//如果输出路径不存在则新建输出路径
		if(!fixedPath.exists())
			fixedPath.mkdirs();

		for(int i=0;i<fileNames.length;i++){
			String fixedFileName = fixedPathName+File.separator+fileNames[i]+".txt";
			File fixedFile = new File(fixedFileName);
			if(!fixedFile.exists())
				fixedFile.createNewFile();
			else{
				BufferedWriter bw = new BufferedWriter(new FileWriter(fixedFile));
				bw.close();
			}
			fixedFiles[i]=fixedFile;
		}
		Arrays.sort(fixedFiles);
	}
	/*
	 * 把按时间分割的数据转存到按ID尾数分割
	 */
	public static void splitFile(File rawFile)throws Exception{
		System.out.println("Now spliting "+rawFile.getAbsolutePath());
		bws = new BufferedWriter[fixedFiles.length];
		for(int i=0;i<fixedFiles.length;i++)
			bws[i] = new BufferedWriter(new FileWriter(fixedFiles[i],true));
		br = new BufferedReader(new FileReader(rawFile));
		String af;
		String[] afList;
		while((af=br.readLine())!=null){
			Cnt_records++;
			afList = af.split(",");
			if(afList.length!=14 ||afList[0].length()!=idLen ||afList[2].length()!=14)
			{
				Cnt_usl_records++;
				continue;
			}
			int num = Math.abs(afList[0].hashCode())%100;
			String date = afList[2].substring(0,8);
			if(!date.equals(DateS))
			{
				Cnt_usl_records++;
				continue;
			}
			String time = afList[2].substring(8);
			afList[2] = date+","+time;
			if(afList[4].length()<3)
			{
				Cnt_usl_records++;
				continue;
			}
			try
			{
				Double.valueOf(afList[4]);
			}	catch(Exception e){
				Cnt_usl_records++;
				continue;
			}
			if(afList[5].length()<3)
			{
				Cnt_usl_records++;
				continue;
			}
			try{
			Double.valueOf(afList[5]);
			}catch (Exception e){
				Cnt_usl_records++;
				continue;
			}
			bws[num].write(afList[0]+","+afList[2]+",0,0,"+afList[4]+","+afList[5]+",0\n");
		}
		br.close();
		for(int i=0;i<bws.length;i++)
			bws[i].close();
		

	}
	/*
	 * 对文件中的记录按（ID，time）排序
	 */
	public static void sortByIdTime(File fixedFile)throws IOException{
		System.out.println("Now sorting "+fixedFile.getAbsolutePath());
		br = new BufferedReader(new FileReader(fixedFile));

		String af;
		FileList=new ArrayList<String>();
		br = new BufferedReader(new FileReader(fixedFile));
		while((af=br.readLine())!=null)
			FileList.add(af);
		br.close();
		if (FileList.size()==0) return;
		feature=Feature.createFeature(FileList,idLen);
		Arrays.sort(feature, new FeatureComparator());

	}
	
	
	/*
	 * 删除文件中重复出现的record
	 */
	public static void deleteRepeat(File fixedFile)throws Exception{
		
		System.out.println("Now deleting repeat "+fixedFile.getAbsolutePath());
		
		if (FileList.size()==0) return;
		bws = new BufferedWriter[1];
		bws[0] = new BufferedWriter(new FileWriter(fixedFile));

		bws[0].write(FileList.get(feature[0].index)+"\n");
		Cnt_usf_records++;
		Cnt_users++;
		
		for(int i=1;i<FileList.size();i++){
			if(!FileList.get(feature[i].index).equals(FileList.get(feature[i-1].index))){
				Cnt_usf_records++;
				bws[0].write(FileList.get(feature[i].index)+"\n");
				if (feature[i].id==feature[i-1].id){
					Cnt_interval_n++;
					Cnt_interval_t+=feature[i].time-feature[i-1].time;
				}
				else Cnt_users++;
			}
			else Cnt_rep_records++;

		}
		bws[0].close();
//		for (int i=0;i<FileList.size();i++) feature[i]=null;
//		feature=null;
//		FileList.clear();
//		FileList=null;
	}
	
	public static void main(String[] args)throws Exception{
		Config.init();
		cityMaxLon = Double.valueOf(Config.getAttr(Config.CityMaxLon));
		cityMinLon = Double.valueOf(Config.getAttr(Config.CityMinLon));
		cityMaxLat = Double.valueOf(Config.getAttr(Config.CityMaxLat));
		cityMinLat = Double.valueOf(Config.getAttr(Config.CityMinLat));
		DateS = Config.getAttr(Config.Date);
		idLen = Integer.valueOf(Config.getAttr(Config.IdLength));
		File rawPath = new File(Config.getAttr(Config.RawPath));
		File[] rawFiles = rawPath.listFiles();
		//生成输出目录
		mkDir(fileNames_100,Config.getAttr(Config.FixedPath));

		//分割raw文件
		for(File file:rawFiles){
			splitFile(file);
		}
		//按id和timestamp排序       并删除重复记录数
		//fixedFiles=new File(Config.getAttr(Config.FixedPath)).listFiles();
		for(int i=0;i< fixedFiles.length;i++){
			sortByIdTime(fixedFiles[i]);
			deleteRepeat(fixedFiles[i]);
		}


		System.out.println("finish");
		System.out.println("总记录数："+String.valueOf(Cnt_records));
		System.out.println("无效记录数："+String.valueOf(Cnt_usl_records));
		System.out.println("重复记录数："+String.valueOf(Cnt_rep_records));
		System.out.println("有效记录数："+String.valueOf(Cnt_usf_records));
		System.out.println("有效用户数："+String.valueOf(Cnt_users));
		Avg_records=(double)Cnt_usf_records/(double)Cnt_users;
		System.out.println("平均用户有效记录数："+String.valueOf(Avg_records));
		Avg_interval=(double)Cnt_interval_t/(double)Cnt_interval_n;
		System.out.println("平均相邻时间间隔"+String.valueOf(Cnt_interval_t)+","+String.valueOf(Cnt_interval_n)+","+String.valueOf(Avg_interval));
	}
}
/*修改自BJmobile.java。
 *整合了BJmobile2014.java中关于ID取Hash后两位以及原始数据项格式判断的代码。
 *新增对原始数据项日期长度，经纬度是否为实型之间的判断。
 *新增数据质量评估统计功能（总记录数、无效记录数、重复记录数、有效记录数、有效用户数、平均用户有效记录数、平均相邻时间间隔） 。
 * 新增成员Cnt_records,Cnt_usl_records,Cnt_rep_records,Cnt_usf_records,Cnt_users,
   Cnt_interval_n,Cnt_interval_t,Avg_records,Avg_interval统计数据信息,
   DateS,FixedFiles,FileList,feature提升运行效率。
 * 删除成员total,useful,fileNums,basePos及相关代码。
 * 优化执行效率（避免fixedFiles重复计算,判断date串是否匹配当前日期时的重复计算,
        将SortByTime和deleteRepeat函数中的afList数据类型改为ArrayList，去除Sort后写delete前读的过程，避免重复读取,
          调用Arrays自带的sort方法替代自主编写的qsort
 * 构造Feature类，处理数据项特征值（ID，时间）相关
        将从1fixed中文件读取的afList数组提取特征值构建特征数组feature，通过对feature排序提升排序效率 

 */ 