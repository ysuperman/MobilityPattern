package RoutesMerge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class MIFMaker  {
	public static void convert(BufferedReader br,BufferedWriter bw)throws Exception{
		bw.write("Version 300\n");
		bw.write("Charset \"WindowsSimpChinese\"\n");
		bw.write("Delimiter \",\"\n");
		bw.write("CoordSys Earth Projection 1, 0\n");
		bw.write("Columns 1\n");
		bw.write("\t_COL1 Integer\n");
		bw.write("Data\n");
		bw.write("\n");
		String af;
		String[] afList=new String[6];
		ArrayList<String> Lon=new ArrayList<String>();
		ArrayList<String> Lat=new ArrayList<String>();
		
		while ((af=br.readLine())!=null){
			afList=af.split(",");
			bw.write("Point  "+afList[3]+"  "+afList[4]+"\n");
			bw.write("\tSymbol (35,0,12)\n");
			Lon.add(afList[3]);
			Lat.add(afList[4]);
		}
		for (int i=1;i<Lon.size();i++){
			bw.write("Line  "+Lon.get(i-1)+"  "+Lat.get(i-1)+"  "+Lon.get(i)+"  "+Lat.get(i)+"\n");
			bw.write("\tPen (1,2,0)\n");
		}
	}
	public static void Handle(String path,String infilename)throws Exception{
		File in=new File(path+File.separator+infilename);
		if (!in.exists())  throw new Exception("未找到输入文件");
		String outfilename=infilename.replace(".txt", ".MIF");
		if (outfilename.equals(infilename)) throw new Exception("输入文件不为txt格式");
		BufferedReader  br=new BufferedReader(new FileReader(in));
		BufferedWriter  bw=new BufferedWriter(new FileWriter(path+File.separator+outfilename));
		convert(br,bw);
		br.close();
		bw.flush();
		bw.close();
		System.out.println(infilename+" is converted.");
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Scanner sc=new Scanner(System.in);
			System.out.println("input the absolute path:");
			String path=sc.nextLine();
			
			/*单个文件转换
			System.out.println("input the file name:");
			String infilename=sc.next();
			Handle(path,infilename);
			
			*/
			//批量转换
			File Path=new File(path);
			if (!Path.isDirectory()) {sc.close();throw new Exception("目录名无效。");}
			String[] lst=Path.list();
			for (int i=0;i<lst.length;i++)
				if (lst[i].indexOf(".txt")!=-1)   Handle(path,lst[i]);
			sc.close();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
