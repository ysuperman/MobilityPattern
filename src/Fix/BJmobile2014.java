package Fix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import Config.Config;

class BJmobileComparator implements Comparator {  
    public final int compare(Object a, Object b) {  
    	long la = Long.valueOf(((String)a).substring(0, 18));
		long lb = Long.valueOf(((String)b).substring(0, 18));
		if(la>lb){
			return 1;
		}else if(la<lb){
			return -1;
		}
		int ia = Integer.valueOf(((String)a).substring(28, 34));
		int ib = Integer.valueOf(((String)b).substring(28, 34));
		if(ia>ib){
			return 1;
		}else{
			return -1;
		}
    }  
}  

/*
 * author:youg
 * date:20160705
 * 2014年北京移动数据预处理
 * 把原始信令数据按id后两位分割到不同文件中，并按id和时间字段排序
 * 0raw:原始信令数据，按时间划分文件
 * 1fixed:按id后两位划分文件，文件内按id和时间排序
 */
public class BJmobile2014 {
	public static double cityMaxLon;
	public static double cityMaxLat;
	public static double cityMinLon;
	public static double cityMinLat;
	public static int idLen;
	
	//public static Map<String,String> basePos = new HashMap<String,String>();
	public static BufferedWriter[] bws;
	public static BufferedReader br;
	public static int total = 0,useful=0;
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
	public static Map<String,Integer> fileNums = new HashMap<String,Integer>();
	/*
	 * 读取基站位置信息，并存入map中
	 */
	/*
	public static void getBasePos(String baseFile)throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(baseFile));
		String af;
		String[] afList;
		while((af=br.readLine())!=null){
			afList = af.split(",");
			basePos.put(afList[0], afList[3]+","+afList[4]);
		}
		br.close();
	}
	*/
	/*
	 * 在1fixed文件夹中创建两位尾数命名的txt文件
	 */
	public static void mkDir(String[] fileNames, String fixedPathName)throws Exception{
		File fixedPath = new File(fixedPathName);
		//如果输出路径不存在则新建输出路径
		if(!fixedPath.exists())
			fixedPath.mkdirs();
		for(int i=0;i<fileNames.length;i++)
			fileNums.put(fileNames[i], i);
		for(int i=0;i<fileNames.length;i++){
			String fixedFileName = fixedPathName+File.separator+fileNames[i]+".txt";
			File fixedFile = new File(fixedFileName);
			if(!fixedFile.exists())
				fixedFile.createNewFile();
//			else{
//				BufferedWriter bw = new BufferedWriter(new FileWriter(fixedFile));
//				bw.close();
//			}
		}
	}
	/*
	 * 把按时间分割的数据转存到按ID尾数分割
	 */
	public static void splitFile(File rawFile)throws Exception{
		System.out.println("Now spliting "+rawFile.getAbsolutePath());
		File fixedPath = new File(Config.getAttr(Config.FixedPath));
		File[] fixedFiles = fixedPath.listFiles();
		Arrays.sort(fixedFiles);
		bws = new BufferedWriter[fixedFiles.length];
		for(int i=0;i<fixedFiles.length;i++)
			bws[i] = new BufferedWriter(new FileWriter(fixedFiles[i],true));
		br = new BufferedReader(new FileReader(rawFile));
		String af;
		String[] afList;
		while((af=br.readLine())!=null){
			total+=1;
			afList = af.split(",");
			if(afList.length<11)
				continue;
			if(afList[0].length()!=idLen)
				continue;
			int num = Math.abs(afList[0].hashCode())%100;
			//System.out.println(afList[0]+"------："+num);
			//int cellid = Integer.valueOf(afList[6]).intValue();
			//String lc = afList[5]+String.format("%05d", cellid);
			//System.out.println(lc);
			//if(!basePos.containsKey(lc))
			//	continue;
			//String loc = basePos.get(lc);
			String date = afList[2].substring(0,8);
			if(!date.equals(Config.getAttr(Config.Date)))
				continue;
			String time = afList[2].substring(8);
			afList[2] = date+","+time;
			if(afList[4].length()<3)
				continue;
			if(afList[5].length()<3)
				continue;
			//System.out.println(af);
			useful+=1;
			//id,date,time,cell,lac,lon,lat,state
			bws[num].write(afList[0]+","+afList[2]+","+"0"+","+"0"+","+afList[4]+","+afList[5]+","+"0"+"\n");
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
		int len=0;
		String af;
		String[] afList;
		while((af=br.readLine())!=null)
			len+=1;
		br.close();
		if(len==0)
			return;
		afList = new String[len];
		br = new BufferedReader(new FileReader(fixedFile));
		int i=0;
		while((af=br.readLine())!=null)
			afList[i++]=af;
		br.close();
		System.out.println("start sort");
		//qsort(afList,0,len-1);
		Arrays.sort(afList, new BJmobileComparator());
		System.out.println("finish sort");
		bws = new BufferedWriter[1];
		bws[0] = new BufferedWriter(new FileWriter(fixedFile));
		for(String afs:afList)
			bws[0].write(afs+"\n");
		bws[0].close();
	}
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
//		String[] aList = a.split(",");
//		String[] bList = b.split(",");
//		if(aList.length<3)
//			return true;
//		if(bList.length<3)
//			return false;
		long la = Long.valueOf(a.substring(0, 18));
		long lb = Long.valueOf(b.substring(0, 18));
		if(la>lb){
			return true;
		}else if(la<lb){
			return false;
		}
		int ia = Integer.valueOf(a.substring(28, 34));
		int ib = Integer.valueOf(b.substring(28, 34));
		if(ia>ib){
			return true;
		}else{
			return false;
		}
		/*
		if(aList[0].compareTo(bList[0])>0)
			return true;
		else if(aList[0].compareTo(bList[0])==0 && aList[2].compareTo(bList[2])>=0)
			return true;
		else
			return false;
		*/
	}
	/*
	 * 删除文件中重复出现的record
	 */
	public static void deleteRepeat(File fixedFile)throws Exception{
		System.out.println("Now deleting repeat "+fixedFile.getAbsolutePath());
		br = new BufferedReader(new FileReader(fixedFile));
		int len=0;
		String af;
		String[] afList;
		while((af=br.readLine())!=null)
			len+=1;
		br.close();
		if(len==0)
			return;
		afList = new String[len];
		br = new BufferedReader(new FileReader(fixedFile));
		int i=0;
		while((af=br.readLine())!=null)
			afList[i++]=af;
		br.close();
		bws = new BufferedWriter[1];
		bws[0] = new BufferedWriter(new FileWriter(fixedFile));
		total+=1;
		useful+=1;
		bws[0].write(afList[0]+"\n");
		for(i=1;i<len;i++){
			total+=1;
			if(!afList[i].equals(afList[i-1])){
				useful+=1;
				bws[0].write(afList[i]+"\n");
			}
		}
		bws[0].close();
	}
	
	public static void main(String[] args)throws Exception{
		Config.init();
		cityMaxLon = Double.valueOf(Config.getAttr(Config.CityMaxLon));
		cityMinLon = Double.valueOf(Config.getAttr(Config.CityMinLon));
		cityMaxLat = Double.valueOf(Config.getAttr(Config.CityMaxLat));
		cityMinLat = Double.valueOf(Config.getAttr(Config.CityMinLat));
		idLen = Integer.valueOf(Config.getAttr(Config.IdLength));
		
		File rawPath = new File(Config.getAttr(Config.RawPath));
		File[] rawFiles = rawPath.listFiles();
		//生成输出目录
		mkDir(fileNames_100,Config.getAttr(Config.FixedPath));
		//读取基站位置数据
		//getBasePos(Config.getAttr(Config.BaseFile));
		//分割raw文件
//		for(File file:rawFiles){
//			splitFile(file);
//		}
		
		File fixedPath = new File(Config.getAttr(Config.FixedPath));
		File[] fixedFiles = fixedPath.listFiles();
		//按id和timestamp排序

		for(int i=40;i<fixedFiles.length;i++){
			sortByIdTime(fixedFiles[i]);
		}
		//删除重复记录数
		total=0;
		useful=0;
		for(File file:fixedFiles){
			deleteRepeat(file);
		}
		System.out.println("finish");
		System.out.println("total:"+String.valueOf(total));
		System.out.println("useful:"+String.valueOf(useful));
	}
}