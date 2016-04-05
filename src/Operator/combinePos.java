package Operator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
 * author:youg
 * date:20160225
 * 把经纬度位置数据添加到原始信令数据中，并按id后两位分割到不同文件中
 * 0raw:原始信令数据，不包含经纬度信息，按时间划分文件
 * 1withPos:添加经纬度信息，按id后两位划分文件，文件内按id和时间排序
 */
public class combinePos {
	public static String basePath = "F:\\basemap\\BJbase_cellidcn.txt";
	public static String workPath = "F:\\BJmobile\\";
	public static String date = "20130228";
	public static String rawPathName = workPath+date+"\\0raw\\";
	public static String withPosPathName = workPath+date+"\\1withPos\\";
	
	public static Map<String,String> basePos = new HashMap<String,String>();
	public static BufferedWriter[] bws;
	public static BufferedReader br;
	public static int total = 0,useful=0;
	/*
	 * 读取基站位置信息，并存入map中
	 */
	public static void getBasePos(String basePath)throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(basePath));
		String af;
		String[] afList;
		while((af=br.readLine())!=null){
			afList = af.split(",");
			basePos.put(afList[0], afList[3]+","+afList[4]);
		}
		br.close();
	}
	/*
	 * 在1withPos文件夹中创建两位尾数命名的txt文件
	 */
	public static void mkDir(String withPosPathName)throws Exception{
		File outputPath = new File(withPosPathName);
		//如果输出路径不存则则新建输出路径
		if(!outputPath.exists())
			outputPath.mkdirs();
		for(int i=0;i<100;i++){
			String num = String.format("%02d", i);
			String withPosFileName = withPosPathName+num+".txt";
			File withPosFile = new File(withPosFileName);
			if(!withPosFile.exists())
				withPosFile.createNewFile();
			else{
				BufferedWriter bw = new BufferedWriter(new FileWriter(withPosFile));
				bw.close();
			}
		}
	}
	/*
	 * 把按时间分割的数据转存到按ID尾数分割
	 */
	public static void splitFile(File inputFile)throws Exception{
		System.out.println("Now spliting "+inputFile.getAbsolutePath());
		File outputPath = new File(withPosPathName);
		File[] outputFiles = outputPath.listFiles();
		bws = new BufferedWriter[outputFiles.length];
		for(int i=0;i<outputFiles.length;i++)
			bws[i] = new BufferedWriter(new FileWriter(outputFiles[i],true));
		br = new BufferedReader(new FileReader(inputFile));
		String af;
		String[] afList;
		while((af=br.readLine())!=null){
			total+=1;
			afList = af.split(",");
			if(afList.length<11)
				continue;
			int num = Integer.valueOf(afList[2].substring(17)).intValue();
			int cellid = Integer.valueOf(afList[6]).intValue();
			String lc = afList[5]+String.format("%05d", cellid);
			//System.out.println(lc);
			if(!basePos.containsKey(lc))
				continue;
			useful+=1;
			String loc = basePos.get(lc);
			String date = afList[4].substring(0,8);
			String time = afList[4].substring(8);
			afList[4] = date+","+time;
			bws[num].write(afList[2]+","+afList[4]+","+afList[5]+","+afList[6]+","+loc+","+afList[7]+"\n");
		}
		br.close();
		for(int i=0;i<bws.length;i++)
			bws[i].close();
	}
	/*
	 * 对文件中的记录按（ID，time）排序
	 */
	public static void sortByIdTime(File inputFile)throws IOException{
		System.out.println("Now sorting "+inputFile.getAbsolutePath());
		br = new BufferedReader(new FileReader(inputFile));
		int len=0;
		String af;
		String[] afList;
		while((af=br.readLine())!=null)
			len+=1;
		br.close();
		if(len==0)
			return;
		afList = new String[len];
		br = new BufferedReader(new FileReader(inputFile));
		int i=0;
		while((af=br.readLine())!=null)
			afList[i++]=af;
		br.close();
		//System.out.println("start sort");
		qsort(afList,0,len-1);
		//System.out.println("finish sort");
		bws = new BufferedWriter[1];
		bws[0] = new BufferedWriter(new FileWriter(inputFile));
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
	public static void deleteRepeat(File inputFile)throws Exception{
		System.out.println("Now deleting repeat "+inputFile.getAbsolutePath());
		br = new BufferedReader(new FileReader(inputFile));
		int len=0;
		String af;
		String[] afList;
		while((af=br.readLine())!=null)
			len+=1;
		br.close();
		if(len==0)
			return;
		afList = new String[len];
		br = new BufferedReader(new FileReader(inputFile));
		int i=0;
		while((af=br.readLine())!=null)
			afList[i++]=af;
		br.close();
		bws = new BufferedWriter[1];
		bws[0] = new BufferedWriter(new FileWriter(inputFile));
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
		File inputPath = new File(rawPathName);
		File[] inputFiles = inputPath.listFiles();
		//生成输出目录
		//mkDir(withPosPathName);
		//读取基站位置数据
		//getBasePos(basePath);
		//分割raw文件
		//for(File file:inputFiles){
		//	splitFile(file);
		//}
		
		File withPosPath = new File(withPosPathName);
		File[] withPosFiles = withPosPath.listFiles();
		//按id和timestamp排序
		for(File file:withPosFiles){
			sortByIdTime(file);
		}
		//删除重复记录数
		total=0;
		useful=0;
		for(File file:withPosFiles){
			deleteRepeat(file);
		}
		System.out.println("finish");
		System.out.println("total:"+String.valueOf(total));
		System.out.println("useful:"+String.valueOf(useful));
	}
}
