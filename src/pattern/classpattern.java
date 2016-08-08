package pattern;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Scanner;

public class classpattern {

	public static void Handle(String path,String infilename)throws Exception{
		File in=new File(path+File.separator+infilename);
		if (!in.exists())  throw new Exception("未找到输入文件");
		String outfilename=infilename.replace(".txt", ".MIF");
		if (outfilename.equals(infilename)) throw new Exception("输入文件不为txt格式");
		BufferedReader  br=new BufferedReader(new FileReader(in));
		BufferedWriter  bw=new BufferedWriter(new FileWriter(path+File.separator+outfilename));
		
		br.close();
		bw.flush();
		bw.close();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Scanner sc=new Scanner(System.in);
			System.out.println("input the absolute path:");
			String path=sc.nextLine();
			System.out.println("input the file name:");
			String infilename=sc.next();
			sc.close();
			Handle(path,infilename);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

}
