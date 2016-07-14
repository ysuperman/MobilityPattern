package Fix;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import Config.Config;

public class getana {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Config.init();
		File rawPath = new File(Config.getAttr(Config.RawPath));
		File[] rawFiles = rawPath.listFiles();
		BufferedReader br;
		int Cnt_records=0;
		for (File file:rawFiles){
			System.out.println("Now raw "+file.getAbsolutePath());
			br = new BufferedReader(new FileReader(file));
			while (br.readLine()!=null)
				Cnt_records++;
		}
			
		File fixedPath = new File(Config.getAttr(Config.FixedPath));
		File[] fixedFiles = fixedPath.listFiles();
		int Cnt_usl_records=0;
		for (File file:fixedFiles){
			System.out.println("Now fixed "+file.getAbsolutePath());
			br = new BufferedReader(new FileReader(file));
			while (br.readLine()!=null)
				Cnt_usl_records++;
		}

		System.out.println("total records:"+String.valueOf(Cnt_records));
		
	
		System.out.println("useless records:"+String.valueOf(Cnt_records-Cnt_usl_records));
		
	}

}
