package Fix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/*
 * author:youg
 * date:20160418
 * ɽ����ͨ���Ԥ����
 * ��ԭʼ������ݰ�id����λ�ָ��ͬ�ļ��У�����id��ʱ���ֶ�����
 * 0raw:ԭʼ������ݣ���ʱ�仮���ļ�
 * 1fixed:��id����λ�����ļ����ļ��ڰ�id��ʱ������
 */
public class SDUnicom {
	//public static String basePath = "F:\\basemap\\BJbase_cellidcn.txt";
	public static String workPath = "F:\\SDUnicom\\";
	public static String date = "20151117";
	public static String rawPathName = workPath+date+"\\0raw\\";
	public static String fixedPathName = workPath+date+"\\1fixed\\";
	public static double cityMaxLon = 117.255435;
	public static double cityMaxLat = 36.851682;
	public static double cityMinLon = 116.827556;
	public static double cityMinLat = 36.544621;
	
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
	 * ��ȡ��վλ����Ϣ��������map��
	 */
	/*public static void getBasePos(String basePath)throws Exception{
		BufferedReader br = new BufferedReader(new FileReader(basePath));
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
	 * ��1withPos�ļ����д�����λβ�������txt�ļ�
	 */
	public static void mkDir(String[] fileNames, String fixedPathName)throws Exception{
		File outputPath = new File(fixedPathName);
		//������·�����������½����·��
		if(!outputPath.exists())
			outputPath.mkdirs();
		for(int i=0;i<fileNames.length;i++)
			fileNums.put(fileNames[i], i);
		for(int i=0;i<fileNames.length;i++){
			String fixedFileName = fixedPathName+fileNames[i]+".txt";
			File fixedFile = new File(fixedFileName);
			if(!fixedFile.exists())
				fixedFile.createNewFile();
			else{
				BufferedWriter bw = new BufferedWriter(new FileWriter(fixedFile));
				bw.close();
			}
		}
	}
	/*
	 * �Ѱ�ʱ��ָ�����ת�浽��IDβ��ָ�
	 */
	public static void splitFile(File inputFile)throws Exception{
		System.out.println("Now spliting "+inputFile.getAbsolutePath());
		File outputPath = new File(fixedPathName);
		File[] outputFiles = outputPath.listFiles();
		Arrays.sort(outputFiles);
		bws = new BufferedWriter[outputFiles.length];
		for(int i=0;i<outputFiles.length;i++)
			bws[i] = new BufferedWriter(new FileWriter(outputFiles[i],true));
		br = new BufferedReader(new FileReader(inputFile));
		String af;
		String[] afList;
		while((af=br.readLine())!=null){
			total+=1;
			afList = af.split(",");
			if(afList.length<7)
				continue;
			if(afList[5].equals("\\N"))
				continue;
			if(afList[6].equals("\\N"))
				continue;
			double lon = Double.valueOf(afList[5]);
			if(lon<cityMinLon || lon>cityMaxLon)
				continue;
			double lat = Double.valueOf(afList[6]);
			if(lat<cityMinLat || lat>cityMaxLat)
				continue;
			int num = fileNums.get(afList[0].substring(17));
			useful+=1;
			String date = afList[2].substring(0,8);
			String time = afList[2].substring(8);
			afList[2] = date+","+time;
			bws[num].write(afList[0]+","+afList[2]+","+"0"+","+"0"+","+afList[5]+","+afList[6]+","+afList[1]+"\n");
		}
		br.close();
		for(int i=0;i<bws.length;i++)
			bws[i].close();
	}
	/*
	 * ���ļ��еļ�¼����ID��time������
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
	 * ɾ���ļ����ظ����ֵ�record
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
		//������Ŀ¼
		//mkDir(fileNames_36,fixedPathName);
		//��ȡ��վλ�����
		//getBasePos(basePath);
		//�ָ�raw�ļ�
		//for(File file:inputFiles){
		//	splitFile(file);
		//}
		
		File fixedPath = new File(fixedPathName);
		File[] fixedFiles = fixedPath.listFiles();
		//��id��timestamp����
		for(File file:fixedFiles){
			sortByIdTime(file);
		}
		//ɾ���ظ���¼��
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
