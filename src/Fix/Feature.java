package Fix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class Feature {
	//public String id;
	public long id;
	public int time;
	public int index;
	private static int idLen;
	public static Feature[] feature=new Feature[6500000]; 
	private  Feature(String term,int idx){
		//this.id=term.substring(0, idLen);
		this.id=Long.valueOf(term.substring(0, idLen));
		this.index=idx;
		this.time=Integer.valueOf(term.substring(idLen+10,idLen+12))*3600;
		this.time+=Integer.valueOf(term.substring(idLen+12,idLen+14))*60;
		this.time+=Integer.valueOf(term.substring(idLen+14,idLen+16));
	}
	public static void createFeature(ArrayList<String> afList,int idLength){
		int Size=afList.size();
		idLen=idLength;
		for (int i=0;i<Size;i++)
			feature[i]=new Feature(afList.get(i),i);
		
	}

	public static void main(String argv[]) throws Exception {
			Scanner sc=new Scanner(System.in);
			String Sin=sc.nextLine();
			String Sout=sc.nextLine();
			File fixedFile=new File(Sin);
			if (!fixedFile.exists()){ System.out.println("not exist"); return;}
			File outFile=new File(Sout);
			if (!outFile.exists()) outFile.createNewFile();
			
			BufferedReader br = new BufferedReader(new FileReader(fixedFile));
			
			String af;
			ArrayList<String> FileList=new ArrayList<String>();

			while((af=br.readLine())!=null)
				FileList.add(af);
			br.close();
			if (FileList.size()==0) return;

			BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
			Feature.createFeature(FileList,18);
			for (int i=0;i<feature.length;i++){
				af=Feature.feature[i].id+","+Feature.feature[i].time+"\n";
				bw.write(af);
			}
			bw.close();
	}
}
