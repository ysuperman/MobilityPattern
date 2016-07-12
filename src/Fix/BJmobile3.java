package Fix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

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
public class BJmobile3 {
	public static int Cnt_records=0;
	public static int Cnt_usl_records=0;
	public static int Cnt_rep_records=0;
	public static int Cnt_usf_records=0;
	public static int Cnt_users=0;
	public static int Cnt_interval_n=0;
	public static int Cnt_interval_t=0;
	public static double Avg_records;
	public static double Avg_interval;
	public static int idLen;
	public static BufferedWriter[] bws;
	public static BufferedReader br;
	public static File[] fixFiles;
	public static String afList[];
	//public static Feature feature[];

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
	 * 对文件中的记录按（ID，time）排序
	 */
	public static void sortByIdTime(File fixedFile)throws IOException{
		System.out.println("Now sorting "+fixedFile.getAbsolutePath());
		br = new BufferedReader(new FileReader(fixedFile));

		/*
		String af;
		FileList=new ArrayList<String>();
		br = new BufferedReader(new FileReader(fixedFile));
		while((af=br.readLine())!=null)
			FileList.add(af);
		br.close();
		if (FileList.size()==0) return;
		feature=Feature.createFeature(FileList,idLen);
		qsort(feature,0,FileList.size()-1);
		*/
		
		String af;
		afList=new String[7500000];
		br = new BufferedReader(new FileReader(fixedFile));
		int i=0;
		while((af=br.readLine())!=null)
			afList[i++]=af;
		br.close();
		if (i==0) return;
		qsort(afList,0,i-1);
		

	}
	
	/*
	public static void qsort(Feature[] a,int s,int t){
		int i=s;
		int j=t;
		int k=i+(j-i)/2;
		Feature x = a[k];
		a[k]=a[i];
		a[i]=x;
		while(i<j){
			while((i<j) && a[j].isBiggerThan(x))
				j--;
			a[i]=a[j];
			while((i<j) && x.isBiggerThan(a[i]))
				i++;
			a[j]=a[i];
		}
		a[i]=x;
		if(i-1>s)
			qsort(a,s,i-1);
		if(j+1<t)
			qsort(a,j+1,t);
	}
	*/
	public static void qsort(String[] a,int s,int t){
		int i=s;
		int j=t;
		int k=i+(j-i)/2;
		String x = a[k];
		a[k]=a[i];
		a[i]=x;
		while(i<j){
			while((i<j) && (biger(a[j],x)))
				j--;
			a[i]=a[j];
			while((i<j) && (biger(x,a[i])))
				i++;
			a[j]=a[i];
		}
		a[i]=x;
		if(i-1>s)
			qsort(a,s,i-1);
		if(j+1<t)
			qsort(a,j+1,t);
	}
	
	public static boolean biger(String a,String b){
		String[] aList = a.split(",");
		String[] bList = b.split(",");
		if(aList.length<3)
			return true;
		if(bList.length<3)
			return false;
		if(aList[0].compareTo(bList[0])>0)
			return true;
		else if(aList[0].compareTo(bList[0])==0 && aList[2].compareTo(bList[2])>=0)
			return true;
		else
			return false;
	}
	
	/*
	 * 删除文件中重复出现的record
	 */
/*	public static void deleteRepeat(File fixedFile)throws Exception{
		
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
				if (feature[i].id.equals(feature[i-1].id)){
					Cnt_interval_n++;
					Cnt_interval_t+=feature[i].time-feature[i-1].time;
				}
				else Cnt_users++;
			}
			else Cnt_rep_records++;

		}
		bws[0].close();
		for (int i=0;i<FileList.size();i++) feature[i]=null;
		feature=null;
		FileList.clear();
		FileList=null;
	}
	*/
public static void deleteRepeat(File fixedFile)throws Exception{
		
		System.out.println("Now deleting repeat "+fixedFile.getAbsolutePath());
		
		if (afList.length==0) return;
		bws = new BufferedWriter[1];
		bws[0] = new BufferedWriter(new FileWriter(fixedFile));

		bws[0].write(afList[0]+"\n");
		Cnt_usf_records++;
		Cnt_users++;
		
		for(int i=1;i<afList.length;i++){
			if(!afList[i].equals(afList[i-1])){
				Cnt_usf_records++;
				bws[0].write(afList[i]+"\n");
			}
			else Cnt_rep_records++;

		}
		bws[0].close();
	}
	
	
	public static void main(String[] args)throws Exception{
		Config.init();
		idLen = Integer.valueOf(Config.getAttr(Config.IdLength));
		File fixPath = new File(Config.getAttr(Config.FixedPath));
		fixFiles = fixPath.listFiles();
		Scanner sc=new Scanner(System.in);
		String fixedPathS=sc.nextLine();
		File fixedPath = new File(fixedPathS);
		if (!fixedPath.exists()) fixedPath.mkdirs();
		for(File file:fixFiles){
			sortByIdTime(file);
			deleteRepeat(file);
		}


		System.out.println("finish");
		System.out.println("重复记录数："+String.valueOf(Cnt_rep_records));
		System.out.println("有效记录数："+String.valueOf(Cnt_usf_records));
	//	System.out.println("有效用户数："+String.valueOf(Cnt_users));
	//	Avg_records=(double)Cnt_usf_records/(double)Cnt_users;
	//	System.out.println("平均用户有效记录数："+String.valueOf(Avg_records));
	//	Avg_interval=(double)Cnt_interval_t/(double)Cnt_interval_n;
	//	System.out.println("平均相邻时间间隔"+String.valueOf(Avg_interval));
	}
}
/*修改自BJmobile.java。
 *整合了BJmobile2014.java中关于ID取Hash后两位以及原始数据项格式判断的代码。
 *新增对原始数据项日期长度，经纬度是否处在CityMax~CityMin之间的判断。
 *新增数据质量评估统计功能（总记录数、无效记录数、重复记录数、有效记录数、有效用户数、平均用户有效记录数、平均相邻时间间隔） 。
 * 新增成员Cnt_records,Cnt_usl_records,Cnt_rep_records,Cnt_usf_records,Cnt_users,
   Cnt_interval_n,Cnt_interval_t,Avg_records,Avg_interval统计数据信息,
   DateS,FixedFiles,FileList,feature提升运行效率。
 * 删除成员total,useful,fileNums,basePos及相关代码。
 * 优化执行效率（避免fixedFiles重复计算,判断date串是否匹配当前日期时的重复计算,
        将SortByTime和deleteRepeat函数中的afList数据类型改为ArrayList避免重复读取
 * 构造Feature类，处理数据项特征值（ID，时间）相关
        将从1fixed中文件读取的afList数组提取特征值构建特征数组feature，通过对feature排序提升排序效率 

 */ 