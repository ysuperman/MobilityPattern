package Fix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class Feature {
	public long id;
	public int time;
	public int index;
	private static int idLen;
	public  Feature(String term,int idx){
		this.id=Long.valueOf(term.substring(0, idLen));
		this.index=idx;
		this.time=Integer.valueOf(term.substring(idLen+10,idLen+12))*3600;
		this.time+=Integer.valueOf(term.substring(idLen+12,idLen+14))*60;
		this.time+=Integer.valueOf(term.substring(idLen+14,idLen+16));
	}
	public static Feature[] createFeature(ArrayList<String> afList,int idLength){
		int Size=afList.size();
		idLen=idLength;
		Feature re[]=new Feature[Size];
		for (int i=0;i<Size;i++)
			re[i]=new Feature(afList.get(i),i);
		return re;
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
			Feature feature[]=Feature.createFeature(FileList,18);
			for (int i=0;i<feature.length;i++){
				af=feature[i].id+","+String.valueOf(feature[i].time)+"\n";
				bw.write(af);
			}
			bw.close();
				
	}
}
